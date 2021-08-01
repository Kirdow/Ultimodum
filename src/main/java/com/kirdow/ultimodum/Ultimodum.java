package com.kirdow.ultimodum;

import com.kirdow.ultimodum.proxy.ClientProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(Ultimodum.MOD_ID)
public class Ultimodum {

    public static final Logger LOGGER = LogManager.getLogger();
    public static Ultimodum instance;

    public static File modFolder;

    private ClientProxy clientProxy;

    public Ultimodum() {
        if (Ultimodum.instance == null)
            Ultimodum.instance = this;

        log("Preparing mod config directory");
        modFolder = new File(FMLPaths.CONFIGDIR.get().toFile(), MOD_NAME);
        if (!modFolder.exists()) {
            log("Creating config directory");
            if (!modFolder.mkdir())
                log("Failed creating config directory");
        }

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void setupClient(final FMLClientSetupEvent event) {
        clientProxy = new ClientProxy();
        clientProxy.registerEvents();

        //KeyBindings.init();
        //KeyBindings.register();
    }

    public ClientProxy getClientProxy() {
        return clientProxy;
    }

    public static final Ultimodum getInstance() {
        return instance;
    }

    public static final void log(String format, Object...args) {
        LOGGER.info(String.format(format, args));
    }

    public static final void debug(String format, Object...args) {
        LOGGER.debug(String.format(format, args));
    }

    public static final String MOD_ID = "ktnultimod";
    public static final String MOD_NAME = "Ultimodum";
    public static final String MOD_VERSION = "1.0.0";

}
