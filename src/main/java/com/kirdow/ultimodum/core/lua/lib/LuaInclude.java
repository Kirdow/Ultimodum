package com.kirdow.ultimodum.core.lua.lib;

import com.kirdow.ultimodum.Ultimodum;
import com.kirdow.ultimodum.core.lua.LuaAddon;
import com.kirdow.ultimodum.core.lua.LuaBase;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.io.File;

public class LuaInclude extends OneArgFunction {

    private LuaAddon addon = null;

    @Override
    public LuaValue call(LuaValue luaFilename) {
        if (!isValid())
            return LuaValue.FALSE;

        Object obj = LuaBase.toObject(luaFilename);
        if (obj == null)
            return LuaValue.FALSE;

        String filename = obj.toString();
        if (!filename.endsWith(".lua"))
            return LuaValue.FALSE;

        File file = new File(addon.getRoot(), filename);
        if (!file.getPath().startsWith(addon.getRoot().getPath()))
            return LuaValue.FALSE;

        if (!file.exists())
            return LuaValue.FALSE;

        Ultimodum.debug("include('%s') called from addon '%s'", filename, addon.getName());
        addFile(file);
        return LuaValue.TRUE;
    }

    public void reset() {
        addon = null;
    }

    public void setAddon(LuaAddon addon) {
        this.addon = addon;
    }

    public boolean isValid() {
        return addon != null;
    }

    private void addFile(File file) {
        if (isValid()) addon.addFile(file);
    }
}
