package com.kirdow.ultimodum.core.lua.lib.data.globals.apis;

import com.kirdow.ultimodum.core.lua.lib.LuaInclude;
import com.kirdow.ultimodum.core.lua.lib.data.ILuaObject;
import com.kirdow.ultimodum.core.lua.lib.data.structs.LuaInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;

import java.util.Optional;

public class GPlayer implements ILuaObject {

    @Override
    public String[] getMethodNames() {
        return new String[]{
                "isActive"
        };
    }

    @Override
    public String[] getVariableNames() {
        return new String[]{
                "inventory",
                "name"
        };
    }

    @Override
    public Object[] call(int method, Object[] args) {

        switch (method) {
            case 0:
            {
                return new Object[]{isValid()};
            }
        }

        return new Object[0];
    }

    @Override
    public Object access(int variable, Optional<Object> value) {

        if (!isValid())
            return null;

        switch (variable) {
            case 0:
            {
                return new LuaInventory(player().inventory);
            }
            case 1:
            {
                return player().getName().getString();
            }
        }

        return null;
    }

    private ClientPlayerEntity player() {
        return Minecraft.getInstance().player;
    }

    private boolean isValid() {
        return Minecraft.getInstance().player != null;
    }
}
