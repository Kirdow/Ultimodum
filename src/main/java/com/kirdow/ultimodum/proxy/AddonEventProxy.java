package com.kirdow.ultimodum.proxy;

import com.kirdow.ultimodum.core.lua.LuaBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
    public void onGuiOpen(GuiOpenEvent event) {
        Screen screen = event.getGui();
        if (screen == null) return;

        String guiName = screen.getClass().getName();
        if (guiName.contains("."))
            guiName = guiName.substring(guiName.lastIndexOf('.')+1);

        if (luaBase.postEvent("GuiOpen", guiName))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        RenderGameOverlayEvent.ElementType type = event.getType();
        if (event.isCancelable() || (type != RenderGameOverlayEvent.ElementType.EXPERIENCE && type != RenderGameOverlayEvent.ElementType.JUMPBAR))
            return;

        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        luaBase.postEvent("Overlay", width, height);
    }

}
