package com.kirdow.ultimodum.core.lua.lib.data.structs;

import com.kirdow.ultimodum.core.lua.lib.data.ILuaObject;
import net.minecraft.inventory.IInventory;

import java.util.Optional;

public class LuaInventory implements ILuaObject {

    private IInventory inventory;

    public LuaInventory(IInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public String[] getMethodNames() {
        return new String[]{
                "size",
                "getItem"
        };
    }

    @Override
    public String[] getVariableNames() {
        return new String[0];
    }

    @Override
    public Object[] call(int method, Object[] args) {

        switch(method) {
            case 0:
            {
                return new Object[]{inventory.getContainerSize()};
            }
            case 1:
            {
                if (args.length < 1 || !(args[0] instanceof Number))
                    break;

                int index = ((Number)args[0]).intValue() - 1;

                return new Object[]{new LuaItemStack(inventory.getItem(index))};
            }
        }

        return new Object[0];
    }

    @Override
    public Object access(int variable, Optional<Object> value) {
        return null;
    }
}
