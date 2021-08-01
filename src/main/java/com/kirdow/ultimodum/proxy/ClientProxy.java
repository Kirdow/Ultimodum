package com.kirdow.ultimodum.proxy;

import net.minecraftforge.common.MinecraftForge;

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

    public ClientProxy() {
    }

    public void registerEvents() {
        registerComponentWithEvents(ClientProxy.class, this, true);
    }

}
