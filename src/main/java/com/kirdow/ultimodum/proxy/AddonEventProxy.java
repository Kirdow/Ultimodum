package com.kirdow.ultimodum.proxy;

import com.kirdow.ultimodum.core.lua.LuaBase;
import com.kirdow.ultimodum.core.lua.lib.data.event.LuaChatReceiveEvent;
import com.kirdow.ultimodum.core.lua.lib.data.event.LuaGuiOpenEvent;
import com.kirdow.ultimodum.core.lua.lib.data.event.LuaOverlayEvent;
import com.kirdow.ultimodum.util.ThreadUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
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

import javax.swing.text.AttributeSet;

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

    @SubscribeEvent
    public void onPreChat(ClientChatEvent event) {
        if (filterReloadCommand(event)) return;
    }

    private Thread reloadThread = null;
    private final Object reloadThreadMutex = new Object();
    private boolean filterReloadCommand(ClientChatEvent event) {
        if (event.getMessage().equalsIgnoreCase("/rl")) {
            event.setCanceled(true);
            synchronized (reloadThreadMutex) {
                if (reloadThread != null) {
                    return true;
                }

                reloadThread = new Thread(() -> {
                    try {
                        ThreadUtil.runOnMainThread(() -> {
                            Minecraft.getInstance().gui.getChat().addMessage(new StringTextComponent(String.format("%sReloading addons...", TextFormatting.GREEN)));
                        });
                        LuaBase.get().reload();
                        ThreadUtil.runOnMainThread(() -> {
                            Minecraft.getInstance().gui.getChat().addMessage(new StringTextComponent(String.format("%sAddons reloaded!", TextFormatting.GREEN)));
                        });
                    } finally {
                        reloadThread = null;
                    }
                });
                reloadThread.start();
            }
            return true;
        }

        return false;
    }

}
