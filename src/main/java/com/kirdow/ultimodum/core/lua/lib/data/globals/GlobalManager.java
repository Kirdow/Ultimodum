package com.kirdow.ultimodum.core.lua.lib.data.globals;

import com.kirdow.ultimodum.core.lua.LuaBase;
import com.kirdow.ultimodum.core.lua.lib.data.ILuaObject;
import com.kirdow.ultimodum.core.lua.lib.data.globals.apis.GGui;
import com.kirdow.ultimodum.core.lua.lib.data.globals.apis.GPlayer;
import com.kirdow.ultimodum.core.lua.lib.data.globals.calls.GReload;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;

public class GlobalManager {

    private static final GlobalManager inst = new GlobalManager();
    public static final GlobalManager get() { return inst; }

    private GlobalManager() {
    }

    private void globals(Consumer<Globals> globals) {
        LuaBase.get().getGlobals(globals);
    }

    private <T extends ILuaObject> void registerGlobalObject(final String name, Class<T> clazz) {
        globals(_G -> {
            if (!_G.get(name).istable()) {
                try {
                    Constructor<T> construct = clazz.getConstructor();
                    construct.setAccessible(true);
                    ILuaObject luaObject = construct.newInstance();
                    LuaValue luaValue = LuaBase.toValue(luaObject);
                    if (luaValue != null && luaValue.istable()) {
                        _G.set(name, luaValue);
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    private <T extends IGlobalCall> void registerGlobalCall(Class<T> clazz) {
        try {
            Constructor<T> construct = clazz.getDeclaredConstructor();
            construct.setAccessible(true);
            final IGlobalCall globalCall = construct.newInstance();
            if (globalCall != null) {
                final String name = globalCall.getName();
                globals(_G -> {
                    if (!_G.get(name).isfunction()) {
                        _G.set(name, new VarArgFunction() {
                            @Override
                            public Varargs invoke(Varargs args) {
                                Object[] objs = LuaBase.toObjects(args, 1);
                                Object[] results = new Object[0];
                                try {
                                    results = globalCall.call(objs);
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }

                                return LuaValue.varargsOf(LuaBase.toValues(results, 0));
                            }
                        });
                    }
                });
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void registerGlobals() {
        inst.registerGlobalCall(GReload.class);
        inst.registerGlobalObject("gui", GGui.class);
        inst.registerGlobalObject("player", GPlayer.class);
    }

}
