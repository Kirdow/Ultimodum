package com.kirdow.ultimodum.core.lua;

import net.minecraft.client.Minecraft;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class LuaObject {

    private static Map<UUID, Object> REF_MAP = new HashMap<>();
    private static String put(Object obj) {
        UUID id = UUID.randomUUID();
        REF_MAP.put(id, obj);
        return id.toString();
    }

    public static Object get(String id) {
        return REF_MAP.getOrDefault(UUID.fromString(id), null);
    }

    public static LuaValue of(Object obj) {
        LuaValue result = LuaBase.toValue(obj);
        if (result != LuaValue.NIL || obj == null) {
            return result;
        }

        result = new LuaTable();
        result.set("__refid", LuaBase.toValue(put(obj)));
        List<Class<?>> classList = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        do {
            classList.add(clazz);
        } while ((clazz = clazz.getSuperclass()) != Object.class);

        for (int i = classList.size() - 1; i >= 0; --i) {
            clazz = classList.get(i);

            for (Field field : clazz.getDeclaredFields()) {
                result.set(field.getName(), new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        try {
                            field.setAccessible(true);
                            Object data = field.get(obj);
                            return LuaObject.of(data);
                        } catch (Throwable ignored) {
                        }

                        return LuaValue.NIL;
                    }
                });
            }

            Map<String, List<Method>> methodMap = new HashMap<>();
            for (Method _method : clazz.getDeclaredMethods()) {
                String methodName = _method.getName();
                final List<Method> list = methodMap.computeIfAbsent(methodName, key -> new ArrayList<>());
                list.add(_method);
                if (!result.get(methodName).isfunction()) {
                    result.set(methodName, new VarArgFunction() {
                        @Override
                        public Varargs invoke(Varargs args) {
                            try {
                                Method method = null;
                                Object[] objects = null;
                                for (Method m : list) {
                                    Object[] objs = LuaBase.toObjects(args, 1);
                                    Class[] parTypes = m.getParameterTypes();
                                    if (objs.length == 0 && parTypes.length == 1 && parTypes[0].isArray()) {
                                        objects = new Object[]{
                                                Array.newInstance(parTypes[0].getComponentType(), 0)
                                        };
                                        method = m;
                                        break;
                                    }

                                    if (parTypes.length != objs.length)
                                        continue;

                                    boolean failed = false;

                                    for (int i = 0; i < objs.length; i++) {
                                        if (objs[i] instanceof Map) {
                                            LuaValue value = args.arg(i + 1);
                                            if (!value.istable() || !value.get("__refid").isstring()) {
                                                failed = true;
                                                break;
                                            }

                                            Object ostr = LuaBase.toObject(value.get("__refid"));
                                            String str = (String)ostr;

                                            Object ref = LuaObject.get(str);
                                            if (ref == null) {
                                                failed = true;
                                                break;
                                            }

                                            objs[i] = ref;
                                        }
                                    }

                                    if (failed)
                                        continue;

                                    for (int i = 0; i < objs.length; i++) {
                                        if (objs[i] instanceof Number) {
                                            Number num = (Number) objs[i];
                                            if (parTypes[i] == int.class)
                                                objs[i] = num.intValue();
                                            else if (parTypes[i] == float.class)
                                                objs[i] = num.floatValue();
                                            else if (parTypes[i] == double.class)
                                                objs[i] = num.doubleValue();
                                            else if (parTypes[i] == long.class)
                                                objs[i] = num.longValue();
                                            else if (parTypes[i] == short.class)
                                                objs[i] = num.shortValue();
                                            else if (parTypes[i] == byte.class)
                                                objs[i] = num.byteValue();
                                            else if (parTypes[i] == boolean.class)
                                                objs[i] = num.intValue() == 1;
                                            else if (parTypes[i] == char.class)
                                                objs[i] = (char) num.shortValue();
                                            else {
                                                failed = true;
                                                break;
                                            }
                                        } else if (!parTypes[i].isAssignableFrom(objs[i].getClass())) {
                                            failed = true;
                                            break;
                                        }
                                    }

                                    if (failed)
                                        continue;

                                    method = m;
                                    objects = objs;
                                }

                                method.setAccessible(true);
                                Object result = method.invoke(obj, objects);
                                if (result != null)
                                    return LuaValue.varargsOf(new LuaValue[]{LuaObject.of(result)});
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }

                            return LuaValue.varargsOf(new LuaValue[]{LuaValue.NIL});
                        }
                    });
                }
            }
        }

        return result;
    }

}
