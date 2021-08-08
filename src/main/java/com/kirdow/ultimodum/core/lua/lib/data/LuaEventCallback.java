package com.kirdow.ultimodum.core.lua.lib.data;

import com.kirdow.ultimodum.Ultimodum;
import com.kirdow.ultimodum.core.lua.LuaAddon;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;
import java.util.List;

public class LuaEventCallback {

    private LuaAddon addon;
    private String name;
    private List<LuaValue> callbacks;

    public LuaEventCallback(LuaAddon addon, String name) {
        this.addon = addon;
        this.name = name;

        callbacks = new ArrayList<>();
    }

    public void dispose() {
        String callbackName = name;
        Ultimodum.log("Disposing '%s' callbacks for addon '%s'", callbackName, addon.getName());

        // Clear ref data;
        addon = null;
        name = null;

        // Clear callbacks
        callbacks.clear();

        Ultimodum.log("Dispose complete for '%s' callbacks!", callbackName);
    }

    public boolean addCallback(LuaValue callback) {
        if (!callback.isfunction())
            return false;

        callbacks.add(callback);
        return true;
    }

    public List<LuaValue> getCallbacks() {
        return callbacks;
    }

    public boolean calls(LuaValue...args) {
        boolean result = false;

        Varargs varargs = args.length == 0 ? null : LuaValue.varargsOf(args);

        for (LuaValue callback : callbacks) {
            LuaValue callResult = LuaValue.NIL;

            if (varargs != null) {
                Varargs varargsResult = callback.invoke(varargs);
                if (varargsResult.narg() == 1)
                    callResult = varargsResult.arg(0);
            } else {
                callResult = callback.call();
            }

            if (callResult == LuaValue.TRUE)
                result = true;
        }

        return result;
    }

}
