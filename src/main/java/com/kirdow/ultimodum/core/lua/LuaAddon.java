package com.kirdow.ultimodum.core.lua;

import com.kirdow.ultimodum.Ultimodum;
import com.kirdow.ultimodum.core.lua.lib.LuaRegisterEvent;
import com.kirdow.ultimodum.core.lua.lib.data.LuaEventCallback;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LuaAddon {

    public static final Pattern ADDON_NAME_PATTERN = Pattern.compile("^[a-z]([a-z]*_?)+$");

    private String name;
    private File root;
    private File mainFile;
    private boolean valid;

    private List<File> addonFiles = new ArrayList<>();
    private LuaValue eventTable;
    private LuaValue addonTable;

    private Map<String, LuaEventCallback> eventCallbackMap = new HashMap<>();

    public LuaAddon(File addonRoot) {
        name = addonRoot.getName();

        if (!ADDON_NAME_PATTERN.matcher(name).matches()) {
            Ultimodum.log("Addon '%s' has an invalid name", name);
            valid = false;
            return;
        }

        root = addonRoot;
        mainFile = new File(root, name + ".lua");
        valid = root.exists() && mainFile.exists();

        if (!valid) {
            Ultimodum.log("Addon '%s' has missing data", name);
        } else {
            Ultimodum.log("Addon '%s' has been detected", name);
        }
    }

    public boolean isValid() {
        return valid;
    }

    public String getName() {
        return name;
    }

    public File getRoot() {
        return root;
    }

    public File getMainFile() {
        return mainFile;
    }

    public void addFile(File file) {
        addonFiles.add(file);
    }

    public List<File> getFiles() {
        return addonFiles;
    }

    public String getFileName(File file) {
        return file.getPath().substring(root.getPath().length() + 1);
    }

    public LuaValue getEventTable() {
        if (eventTable != null) {
            return eventTable;
        }

        eventTable = new LuaTable();
        eventTable.set("addon_name", this.name);
        eventTable.set("onPostLoad", LuaValue.NIL);
        eventTable.set("onLoadComplete", LuaValue.NIL);
        eventTable.set("registerEvent", new LuaRegisterEvent(this));

        return eventTable;
    }

    public LuaValue getAddonTable() {
        if (addonTable != null) return addonTable;

        LuaValue tbl = new LuaTable();
        addonTable = tbl;
        return tbl;
    }

    public Varargs getAddonArgs() {
        return LuaValue.varargsOf(new LuaValue[]{getAddonTable(), LuaBase.toValue(name)});
    }

    public boolean addEventCallback(String name, LuaValue callback) {
        LuaEventCallback eventCallback = eventCallbackMap.computeIfAbsent(name, key -> new LuaEventCallback(this, key));
        return eventCallback.addCallback(callback);
    }

    public boolean postEvent(String name, Object...args) {
        LuaValue[] luaArgs = LuaBase.toValues(args, 0);

        LuaEventCallback eventCallback = eventCallbackMap.get(name);
        if (eventCallback == null)
            return false;

        return eventCallback.calls(luaArgs);
    }

}
