package com.kirdow.ultimodum.core.lua.lib.data.event;

import com.kirdow.ultimodum.core.lua.lib.data.ILuaObject;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.util.Optional;

public class LuaChatReceiveEvent implements ILuaObject {

    private ClientChatReceivedEvent event;

    public LuaChatReceiveEvent(ClientChatReceivedEvent event) {
        this.event = event;
    }

    @Override
    public String[] getMethodNames() {
        return new String[] {
                "getMessage",
                "setMessage",
                "cancel"
        };
    }

    @Override
    public String[] getVariableNames() {
        return new String[] {
                "cancelled"
        };
    }

    @Override
    public Object[] call(int method, Object[] args) {
        switch (method) {
            case 0:
            {
                // getMessage

                return new Object[]{ event.getMessage().getString() };
            }
            case 1:
            {
                // setMessage

                StringBuilder sb = new StringBuilder();
                for (Object obj : args) {
                    if (obj != null)
                        sb.append(obj);
                }

                event.setMessage(new StringTextComponent(sb.toString()));
                return new Object[0];
            }
            case 2:
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
        switch(variable) {
            case 0:
            {
                return event.isCanceled();
            }
        }

        return null;
    }
}
