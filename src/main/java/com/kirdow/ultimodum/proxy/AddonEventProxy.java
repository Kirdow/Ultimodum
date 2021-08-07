package com.kirdow.ultimodum.proxy;

import com.kirdow.ultimodum.core.lua.LuaBase;
import com.kirdow.ultimodum.core.lua.lib.data.event.LuaChatReceiveEvent;
import com.kirdow.ultimodum.core.lua.lib.data.event.LuaGuiOpenEvent;
import com.kirdow.ultimodum.core.lua.lib.data.event.LuaOverlayEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class AddonEventProxy {

    private ClientProxy clientProxy;
    private LuaBase luaBase;

    public AddonEventProxy(ClientProxy proxy) {
        clientProxy = proxy;
        luaBase = LuaBase.get();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (luaBase.postEvent("ClientTick"))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onChatReceive(ClientChatReceivedEvent event) {
        LuaChatReceiveEvent luaEvent = new LuaChatReceiveEvent(event);

        LuaValue tbl = new LuaTable();
        tbl.set("setMessage", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (!arg.isstring())
                    return LuaValue.FALSE;

                String text = arg.tojstring();
                event.setMessage(new StringTextComponent(text));
                return LuaValue.TRUE;
            }
        });
        tbl.set("getMessage", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaBase.toValue(event.getMessage().getString());
            }
        });
        tbl.set("cancel", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                event.setCanceled(true);

                return LuaValue.NONE;
            }
        });

        luaBase.postEvent("ChatReceive", luaEvent);
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        Screen screen = event.getGui();
        if (screen == null) return;

        LuaGuiOpenEvent luaEvent = new LuaGuiOpenEvent(event);

        luaBase.postEvent("GuiOpen", luaEvent);
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        RenderGameOverlayEvent.ElementType type = event.getType();
        if (event.isCancelable() || (type != RenderGameOverlayEvent.ElementType.EXPERIENCE && type != RenderGameOverlayEvent.ElementType.JUMPBAR))
            return;

        luaBase.postEvent("Overlay", new LuaOverlayEvent());
    }

}
