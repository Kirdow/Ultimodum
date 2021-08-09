package com.kirdow.ultimodum.core.lua.lib.data.structs;

import com.kirdow.ultimodum.core.lua.lib.data.ILuaObject;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class LuaItemStack implements ILuaObject {

    private ItemStack itemStack;

    public LuaItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public String[] getMethodNames() {
        return new String[]{
                "getName",
                "getCount",
                "getDamage",
                "isAir"
        };
    }

    @Override
    public String[] getVariableNames() {
        return new String[0];
    }

    @Override
    public Object[] call(int method, Object[] args) {
        switch (method) {
            case 0:
            {
                return new Object[]{itemStack.getItem().toString()};
            }
            case 1:
            {
                return new Object[]{itemStack.getCount()};
            }
            case 2:
            {
                return new Object[]{itemStack.getDamageValue()};
            }
            case 3:
            {
                return new Object[]{itemStack == null || itemStack.isEmpty()};
            }
        }

        return new Object[0];
    }

    @Override
    public Object access(int variable, Optional<Object> value) {
        return null;
    }
}
