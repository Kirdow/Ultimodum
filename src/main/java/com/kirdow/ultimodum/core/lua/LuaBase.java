package com.kirdow.ultimodum.core.lua;

import com.kirdow.ultimodum.Ultimodum;
import com.kirdow.ultimodum.core.lua.lib.LuaEvent;
import com.kirdow.ultimodum.core.lua.lib.LuaInclude;
import com.kirdow.ultimodum.core.lua.lib.data.ILuaObject;
import com.kirdow.ultimodum.core.lua.lib.data.globals.GlobalManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.util.text.StringTextComponent;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LuaBase {

    private Globals globals;
    private List<LuaAddon> addonList;
    private LuaValue globalPrint;
    private LuaValue addonPrint;
    private boolean debugMode;

    private final Object mutex = new Object();

    private LuaBase() {
    }

    private void init() {
        globals = JsePlatform.standardGlobals();
        setGlobals();
    }

    public void setGlobals() {
        if (!globals.get("getMinecraft").isfunction()) {
            globals.set("getMinecraft", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaObject.of(Minecraft.getInstance());
                }
            });
        }

        if (!globals.get("getClass").isfunction()) {
            globals.set("getClass", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    if (!arg.isstring())
                        return LuaValue.NIL;

                    String className = toObject(arg).toString();
                    try {
                        Class<?> clazz = Class.forName(className);
                        return LuaObject.of(clazz);
                    } catch (Throwable ignored) {
                        return LuaValue.NIL;
                    }
                }
            });
        }

        globals.set("collectgarbage", LuaValue.NIL);
        globals.set("dofile", LuaValue.NIL);
        globals.set("loadfile", LuaValue.NIL);
        globals.set("module", LuaValue.NIL);
        globals.set("require", LuaValue.NIL);
        globals.set("package", LuaValue.NIL);
        globals.set("io", LuaValue.NIL);
        globals.set("os", LuaValue.NIL);
        globals.set("luajava", LuaValue.NIL);
        globals.set("debug", LuaValue.NIL);
        globals.set("newproxy", LuaValue.NIL);
        globals.set("__inext", LuaValue.NIL);

        globals.set("_VERSION", "Lua 5.2");

        if (globalPrint == null) {
            globalPrint = globals.get("print");
            globals.set("print", LuaValue.NIL);
        }

        if (addonPrint == null) {
            addonPrint = new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    if (debugMode) {
                        debugChat(LuaBase.toString(args));
                    }

                    return globalPrint.invoke(args);
                }
            };
        }

        if (globals.get("print") != addonPrint) {
            globals.set("print", addonPrint);
        }

        if (!globals.get("dprint").isfunction()) {
            globals.set("dprint", new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    debugChat(String.format("\u00A78DEBUG: \u00A7r%s", LuaBase.toString(args)));

                    return globalPrint.invoke(args);
                }
            });
        }

        if (!globals.get("setDebugMode").isfunction()) {
            globals.set("setDebugMode", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    debugMode = (arg == LuaValue.TRUE);
                    return debugMode ? LuaValue.TRUE : LuaValue.FALSE;
                }
            });
        }

        GlobalManager.registerGlobals();
    }

    private void debugChat(String msg) {
        IngameGui gui = Minecraft.getInstance().gui;
        if (gui != null && gui.getChat() != null) {
            gui.getChat().addMessage(new StringTextComponent(String.format("\u00A76[\u00A73UM\u00A76] \u00A7r%s", msg)));
        }
    }

    public static String toString(Varargs args) {
        Object[] objs = LuaBase.toObjects(args, 1);
        StringBuilder sb = new StringBuilder();
        for (Object obj : objs) {
            try {
                sb.append(obj == null ? "" : obj.toString());
            } catch (Throwable ignored) {
            }
        }

        return sb.toString();
    }

    public void detectAddons() {
        File[] addonFolders = Ultimodum.addonFolder.listFiles((FileFilter)(file) -> {
            if (!file.isDirectory()) return false;
            String name = file.getName();
            File luaFile = new File(file, String.format("%s.lua", name));
            if (!luaFile.exists())
                return false;

            return true;
        });

        List<LuaAddon> addons = new ArrayList<>();
        for (File addonFolder : addonFolders) {
            LuaAddon addon = new LuaAddon(addonFolder);
            if (addon.isValid())
                addons.add(addon);
        }

        addonList = addons;
    }

    public void loadAddons() {
        LuaInclude includeFunc = new LuaInclude();
        LuaEvent eventFunc = new LuaEvent();

        globals.set("include", includeFunc);
        globals.set("getEventTable", eventFunc);

        for (LuaAddon addon : this.addonList) {
            includeFunc.setAddon(addon);
            eventFunc.setAddon(addon);
            LuaValue chunk = globals.loadfile(addon.getMainFile().getPath());
            Ultimodum.debug("Calling '%s' for addon '%s'", addon.getMainFile().getName(), addon.getName());
            chunk.invoke(addon.getAddonArgs());
        }

        includeFunc.reset();

        for (LuaAddon addon : this.addonList) {
            for (File addonFile : addon.getFiles()) {

                LuaValue chunk = globals.loadfile(addonFile.getPath());
                Ultimodum.debug("Calling included file '%s' for addon '%s'", addon.getFileName(addonFile), addon.getName());
                chunk.invoke(addon.getAddonArgs());
            }
        }

        globals.set("include", LuaValue.NIL);
        globals.set("getEventTable", LuaValue.NIL);
    }

    private void postLoadAddons() {
        for (LuaAddon addon : this.addonList) {
            LuaValue table = addon.getEventTable();
            if (table.get("onPostLoad").isfunction()) {
                LuaValue[] strings = addon.getFiles().stream().map(file -> {
                    String fileName = addon.getFileName(file);
                    return toValue(fileName);
                }).collect(Collectors.toList()).toArray(new LuaValue[0]);
                LuaValue stringArray = LuaValue.listOf(strings);

                Ultimodum.debug("Calling onPostLoad(files) for addon '%s'", addon.getName());
                table.get("onPostLoad").call(stringArray);
            } else {
                Ultimodum.debug("No event onPostLoad(files) for addon '%s'", addon.getName());
            }
        }
    }

    private void addonLoadComplete() {
        for (LuaAddon addon : this.addonList) {
            LuaValue table = addon.getEventTable();
            if (table.get("onLoadComplete").isfunction()) {
                LuaValue name = toValue(addon.getName());
                Ultimodum.debug("Calling onLoadComplete(name) for addon '%s'", addon.getName());
                table.get("onLoadComplete").call(name);
            } else {
                Ultimodum.debug("No event onLoadComplete(name) for addon '%s'", addon.getName());
            }
        }
    }

    public void setup() {
        Ultimodum.log("Detecting addons");
        detectAddons();

        Ultimodum.log("Loading addons");
        loadAddons();

        Ultimodum.log("Addon Post Load");
        postLoadAddons();

        Ultimodum.log("Addon Load Complete");
        addonLoadComplete();
    }

    public void reload() {
        synchronized (mutex) {
            Ultimodum.log("Reloading addons");

            Ultimodum.log("Disposing loaded addons");
            // dispose addons
            for (LuaAddon addon : addonList) {
                addon.dispose();
            }

            Ultimodum.log("Unloading addons");
            // Clear list
            addonList.clear();
            addonList = null;

            Ultimodum.log("Resetting lua global");
            // Reset data and globals
            globalPrint = null;
            addonPrint = null;
            debugMode = false;
            globals = null;

            Ultimodum.log("Creating new lua global");
            // Recreate globals
            globals = JsePlatform.standardGlobals();

            Ultimodum.log("Setting lua globals");
            // Set globals
            setGlobals();

            Ultimodum.log("Loading addons");
            // Load/Init addons
            setup();

            Ultimodum.log("Addon Reload complete!");
        }
    }

    public void getGlobals(Consumer<Globals> consumer) {
        if (consumer == null)
            return;

        consumer.accept(globals);
    }

    public boolean postEvent(String name, Object... args) {
        synchronized (mutex) {
            boolean result = false;

            setGlobals();
            for (LuaAddon addon : addonList) {
                try {
                    if (addon.postEvent(name, args))
                        result = true;
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }

            return result;
        }
    }

    public static final LuaBase get() {
        if (_inst != null) return _inst;

        _inst = new LuaBase();
        _inst.init();
        return _inst;
    }

    private static LuaBase _inst;

    private static Map<Object, LuaValue> valuesInProgress;
    private static Map<LuaValue, Object> objectsInProgress;

    public static LuaValue toValue(Object object) {
        if (object == null) {
            return LuaValue.NIL;
        } else if (object instanceof Number) {
            double d = ((Number)object).doubleValue();
            return LuaValue.valueOf(d);
        } else if (object instanceof Boolean) {
            boolean b = (Boolean)object;
            return LuaValue.valueOf(b);
        } else if (object instanceof String) {
            String s = object.toString();
            return LuaValue.valueOf(s);
        } else if (object instanceof byte[]) {
            byte[] b = (byte[])object;
            return LuaValue.valueOf(Arrays.copyOf(b, b.length));
        } else if (object instanceof Map) {
            boolean clearWhenDone = false;
            try {
                if (valuesInProgress == null) {
                    valuesInProgress = new IdentityHashMap<>();
                    clearWhenDone = true;
                } else if (valuesInProgress.containsKey(object)) {
                    return valuesInProgress.get(object);
                }

                LuaValue table = new LuaTable();
                valuesInProgress.put(object, table);

                for (Map.Entry<?,?> pair : ((Map<?,?>)object).entrySet()) {
                    LuaValue key = toValue(pair.getKey());
                    LuaValue value = toValue(pair.getValue());
                    if (!key.isnil() && !value.isnil()) {
                        table.set(key, value);
                    }
                }

                return table;
            } finally {
                if (clearWhenDone) {
                    valuesInProgress = null;
                }
            }
        } else if (object instanceof ILuaObject) {
            return wrapLuaObject((ILuaObject)object);
        } else {
            return LuaValue.NIL;
        }
    }

    public static LuaValue[] toValues(Object[] objects, int leaveEmpty) {
        if (objects == null || objects.length == 0) {
            return new LuaValue[leaveEmpty];
        }

        LuaValue[] values = new LuaValue[objects.length + leaveEmpty];
        for (int i = 0; i < values.length; i++) {
            if (i < leaveEmpty) {
                values[i] = null;
                continue;
            }

            Object object = objects[i - leaveEmpty];
            if (object instanceof LuaValue)
                values[i] = (LuaValue)object;
            else
                values[i] = toValue(object);
        }

        return values;
    }

    private static LuaTable wrapLuaObject(ILuaObject object) {
        LuaTable table = new LuaTable();
        String[] methods = object.getMethodNames();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i] != null) {
                final String name = methods[i];
                final int methodId = i;
                table.set(name, new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        Object[] objs = toObjects(args, 1);
                        Object[] results = new Object[0];
                        try {
                            results = object.call(methodId, objs);
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }

                        return LuaValue.varargsOf(toValues(results, 0));
                    }
                });
            }
        }

        String[] variables = object.getVariableNames();
        for (int i = 0; i < variables.length; i++) {
            if (variables[i] != null) {
                final String name = variables[i];
                final int variableId = i;
                table.set(name, new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        Object[] objs = toObjects(args, 1);
                        Object result = null;
                        try {
                            result = object.access(variableId, Optional.ofNullable(objs.length > 0 ? objs[0] : null));
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }

                        return LuaValue.varargsOf(new LuaValue[]{toValue(result)});
                    }
                });
            }
        }

        return table;
    }

    public static Object toObject(LuaValue value) {
        switch (value.type()) {
            case LuaValue.TINT:
            case LuaValue.TNUMBER:
                return value.todouble();
            case LuaValue.TBOOLEAN:
                return value.toboolean();
            case LuaValue.TSTRING:
            {
                LuaString str = value.checkstring();
                return str.tojstring();
            }
            case LuaValue.TTABLE:
            {
                boolean clearWhenDone = false;
                try {
                    if (objectsInProgress == null) {
                        objectsInProgress = new IdentityHashMap<>();
                        clearWhenDone = true;
                    } else if (objectsInProgress.containsKey(value)) {
                        return objectsInProgress.get(value);
                    }

                    Map<Object, Object> table = new HashMap<>();
                    objectsInProgress.put(value, table);

                    LuaValue k = LuaValue.NIL;
                    while (true) {
                        Varargs keyValue = value.next(k);
                        k = keyValue.arg1();
                        if (k.isnil()) {
                            break;
                        }

                        LuaValue v = keyValue.arg(2);
                        Object keyObject = toObject(k);
                        Object valueObject = toObject(v);
                        if (keyObject != null && valueObject != null) {
                            table.put(keyObject, valueObject);
                        }
                    }

                    return table;
                } finally {
                    if (clearWhenDone) {
                        objectsInProgress = null;
                    }
                }
            }
            case LuaValue.TNIL:
            case LuaValue.TNONE:
            default:
                return null;
        }
    }

    public static Object[] toObjects(Varargs values, int startIdx) {
        int count = values.narg();
        Object[] objects = new Object[count - startIdx + 1];
        for (int n = startIdx; n <= count; n++) {
            int i = n - startIdx;
            LuaValue value = values.arg(n);
            objects[i] = toObject(value);
        }
        return objects;
    }
}
