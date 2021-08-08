package com.kirdow.ultimodum.core.lua.lib.data.globals;

public interface IGlobalCall {

    String getName();

    Object[] call(Object[] args);

}
