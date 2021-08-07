package com.kirdow.ultimodum.core.lua.lib.data;

import java.util.Optional;

public interface ILuaObject {

    String[] getMethodNames();
    String[] getVariableNames();

    Object[] call(int method, Object[] args);
    Object access(int variable, Optional<Object> value);

}
