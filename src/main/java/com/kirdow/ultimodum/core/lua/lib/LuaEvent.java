package com.kirdow.ultimodum.core.lua.lib;

import com.kirdow.ultimodum.Ultimodum;
import com.kirdow.ultimodum.core.lua.LuaAddon;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaEvent extends ZeroArgFunction {

    private LuaAddon addon = null;

    @Override
    public LuaValue call() {
        if (!isValid()) return LuaValue.NIL;

        Ultimodum.debug("getEventTable() called from addon '%s'", addon.getName());
        return addon.getEventTable();
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
}
