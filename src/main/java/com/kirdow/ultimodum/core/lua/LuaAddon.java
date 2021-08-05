package com.kirdow.ultimodum.core.lua;

import com.kirdow.ultimodum.Ultimodum;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

        return eventTable;
    }

}
