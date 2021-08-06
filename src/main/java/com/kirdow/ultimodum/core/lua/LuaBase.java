package com.kirdow.ultimodum.core.lua;

import com.kirdow.ultimodum.Ultimodum;
import com.kirdow.ultimodum.core.lua.lib.LuaEvent;
import com.kirdow.ultimodum.core.lua.lib.LuaInclude;
import com.kirdow.ultimodum.util.FileUtil;
import net.minecraft.client.Minecraft;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class LuaBase {

    private Globals globals;
    private List<LuaAddon> addonList;

    private LuaBase() {
        globals = JsePlatform.standardGlobals();
        globals.set("getMinecraft", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaObject.of(Minecraft.getInstance());
            }
        });

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

    public boolean postEvent(String name, Object... args) {
        boolean result = false;

        for (LuaAddon addon : addonList) {
            if (addon.postEvent(name, args))
                result = true;
        }

        return result;
    }

    public static final LuaBase get() {
        return _inst != null ? _inst : (_inst = new LuaBase());
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
        } /*else if (object instanceof ILuaObject) {
            return wrapLuaObject((ILuaObject)object);
        } */else {
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
            values[i] = toValue(object);
        }

        return values;
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
