package com.kirdow.ultimodum;

import com.kirdow.ultimodum.core.lua.LuaBase;
import com.kirdow.ultimodum.proxy.ClientProxy;
import com.kirdow.ultimodum.util.Net;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(Ultimodum.MOD_ID)
public class Ultimodum {

    public static final Logger LOGGER = LogManager.getLogger();
    public static Ultimodum instance;

    public static File modFolder;
    public static File addonFolder;

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

        log("Preparing lua addon directory");
        addonFolder = new File(new File(FMLPaths.MODSDIR.get().toFile(), MOD_NAME), "AddOns");
        if (!addonFolder.exists()) {
            log("Creating addon directory");
            if (!addonFolder.mkdirs()) {
                log("Failed creating addon directory");
            }
        }

        File readmeFile = new File(addonFolder, "readme.txt");
        if (!readmeFile.exists()) {
            Net.useWriterForFile(readmeFile, writer -> {
                writer.write("To create an addon, create a folder in here and give it the name of your addon,\nand within have a lua file of the same name, let's refer to this file as \"addon.lua\".\nYou should now have a structure similar to this:\n");
                writer.write("-----\naddon\\\n| addon.lua\n-----\n");
                writer.write("Inside addon.lua will be your addon setup code. Load other files using include(\"file.lua\").\n");
                writer.write("Loaded files are run in the order they were loaded, although after addon.lua finishes execution.\n");
                writer.write("If you want to wait for some files to load for code to execute in addon.lua, create a function onPostLoad(files).\n");
                writer.write("The files parameter will contain a list of the files that were loaded. For reference, any variable not defined as local will be in the global scope,\naccessible by other addons.\n");
                writer.write("Additionally, if you have dependencies like other addons, all addons are initially loaded when onPostLoad executes,\nif you require further execution before yours you can also use a function onLoadComplete() which runs after all onPostLoad calls.\n");
                writer.write("You could also create a file called \"dep.txt\" containing an addon name on each line, and your addon will load after all those have been loaded, which would leave your structure like this:\n");
                writer.write("-----\naddon\\\n| addon.lua\n| dep.txt\n-----\n");
            });
        }

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void setupClient(final FMLClientSetupEvent event) {
        log("Setting up client");
        LuaBase base = LuaBase.get();
        base.setup();

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
