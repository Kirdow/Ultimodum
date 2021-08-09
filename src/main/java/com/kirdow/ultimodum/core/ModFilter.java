package com.kirdow.ultimodum.core;

import com.kirdow.ultimodum.Ultimodum;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.network.FMLHandshakeMessages;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModFilter extends ArrayList<ModInfo> {

    private List<ModInfo> parent;
    private List<ModInfo> filter;

    public ModFilter(List<ModInfo> parent) {
        super(parent);

        this.parent = parent;
        this.filter = parent.stream().filter(p -> !p.getModId().equals(Ultimodum.MOD_ID)).collect(Collectors.toList());
    }

    @Override
    public Stream<ModInfo> stream() {

        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        final Class<?> clazz = FMLHandshakeMessages.C2SModListReply.class;
        if (Arrays.stream(trace).anyMatch(p -> p.getClassName().equals(clazz.getName()))) {
            Ultimodum.debug("Safeguard used");
            return this.filter.stream();
        }

        return super.stream();
    }

    public static void validate() {
        ModList list = ModList.get();
        List<ModInfo> parent = list.getMods();

        if (parent instanceof ModFilter)
            return;

        Class<ModList> clazz = ModList.class;
        Field[] fields = clazz.getDeclaredFields();
        Field field = null;
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                Object o = f.get(list);
                if (o == parent) {
                    field = f;
                    break;
                }
            } catch (IllegalAccessException e) {
            }
        }

        if (field == null)
            return;

        ModFilter filter = new ModFilter(parent);
        field.setAccessible(true);
        try {
            field.set(list, filter);
            Ultimodum.debug("Safeguard active");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
