package com.kirdow.ultimodum.core.lua.lib.data.globals.calls;

import com.kirdow.ultimodum.core.lua.LuaBase;
import com.kirdow.ultimodum.core.lua.lib.data.globals.IGlobalCall;
import net.minecraft.client.Minecraft;

public class GReload implements IGlobalCall {

    @Override
    public String getName() {
        return "ReloadUI";
    }

    @Override
    public Object[] call(Object[] args) {

        Minecraft.getInstance().tell(() -> {
            LuaBase.get().reload();
        });

        return new Object[0];
    }
}
