package com.kirdow.ultimodum.core.lua.lib.data.event;

import com.kirdow.ultimodum.core.lua.lib.data.ILuaObject;
import net.minecraftforge.client.event.GuiOpenEvent;

import java.util.Optional;

public class LuaGuiOpenEvent implements ILuaObject {

    private GuiOpenEvent event;

    public LuaGuiOpenEvent(GuiOpenEvent event) {
        this.event = event;
    }

    @Override
    public String[] getMethodNames() {
        return new String[] {
               "cancel"
        };
    }

    @Override
    public String[] getVariableNames() {
        return new String[]{
                "type",
                "gui"
        };
    }

    @Override
    public Object[] call(int method, Object[] args) {
        switch (method) {
            case 0:
            {
                // cancel

                event.setCanceled(true);
                return new Object[0];
            }
        }

        return new Object[0];
    }

    @Override
    public Object access(int variable, Optional<Object> value) {
        switch (variable) {
            case 0:
            {
                // type

                String guiName = event.getGui().getClass().getName();
                if (guiName.contains("."))
                    guiName = guiName.substring(guiName.lastIndexOf('.')+1);

                return guiName;
            }
            case 1:
            {
                // gui

                return event.getGui();
            }
        }

        return null;
    }
}
