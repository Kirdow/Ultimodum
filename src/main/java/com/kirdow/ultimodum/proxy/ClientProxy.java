package com.kirdow.ultimodum.proxy;

import com.kirdow.ultimodum.Ultimodum;
import com.kirdow.ultimodum.core.ModFilter;
import com.kirdow.ultimodum.util.Util;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.HashMap;
import java.util.Map;

public class ClientProxy {

    public static Map<Class, Object> components = new HashMap<>();

    public static <T> T getComponent(Class<T> clazz) {
        return (T)components.getOrDefault(clazz, null);
    }

    public static <T> void registerComponent(Class<T> clazz, T component) {
        components.put(clazz, component);
    }

    private static <T> void registerComponentWithEvents(Class<T> clazz, T component) {
        registerComponentWithEvents(clazz, component, false);
    }

    private static <T> void registerComponentWithEvents(Class<T> clazz, T component, boolean withStatic) {
        if (withStatic)
            MinecraftForge.EVENT_BUS.register(clazz);
        if (component != null) {
            MinecraftForge.EVENT_BUS.register(component);
            registerComponent(clazz, component);
        }
    }

    private AddonEventProxy eventProxy;

    public ClientProxy() {
        eventProxy = new AddonEventProxy(this);
    }

    public void registerEvents() {
        registerComponentWithEvents(ClientProxy.class, this, true);
        registerComponentWithEvents(AddonEventProxy.class, eventProxy, false);

        Util.registerEvents();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof MainMenuScreen) {
            ModFilter.validate();
        }
    }

}
