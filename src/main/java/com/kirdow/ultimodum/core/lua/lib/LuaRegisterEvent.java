package com.kirdow.ultimodum.core.lua.lib;

import com.kirdow.ultimodum.Ultimodum;
import com.kirdow.ultimodum.core.lua.LuaAddon;
import com.kirdow.ultimodum.core.lua.LuaBase;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class LuaRegisterEvent extends TwoArgFunction {

    public LuaAddon addon;

    public LuaRegisterEvent(LuaAddon addon) {
        this.addon = addon;
    }

    @Override
    public LuaValue call(LuaValue name, LuaValue callback) {
        if (addon == null) return LuaValue.FALSE;

        if (!name.isstring() || !callback.isfunction())
            return LuaValue.FALSE;

        String eventName = LuaBase.toObject(name).toString();
        if (!addon.addEventCallback(eventName, callback))
            return LuaValue.FALSE;

        Ultimodum.debug("registerEvent('%s', callback) called from addon '%s'", eventName, addon.getName());
        
        return LuaValue.TRUE;
    }
}
