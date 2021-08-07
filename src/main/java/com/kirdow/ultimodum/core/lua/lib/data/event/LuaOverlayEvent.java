package com.kirdow.ultimodum.core.lua.lib.data.event;

import com.kirdow.ultimodum.core.lua.lib.data.ILuaObject;
import net.minecraft.client.Minecraft;

import java.util.Optional;

public class LuaOverlayEvent implements ILuaObject {
    @Override
    public String[] getMethodNames() {
        return new String[0];
    }

    @Override
    public String[] getVariableNames() {
        return new String[] {
                "width",
                "height"
        };
    }

    @Override
    public Object[] call(int method, Object[] args) {
        return new Object[0];
    }

    @Override
    public Object access(int variable, Optional<Object> value) {
        switch (variable) {
            case 0:
            {
                // width

                return Minecraft.getInstance().getWindow().getGuiScaledWidth();
            }
            case 1:
            {
                // height

                return Minecraft.getInstance().getWindow().getGuiScaledHeight();
            }
        }

        return null;
    }
}
