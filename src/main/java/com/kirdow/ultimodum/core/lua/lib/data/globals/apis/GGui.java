package com.kirdow.ultimodum.core.lua.lib.data.globals.apis;

import com.kirdow.ultimodum.core.lua.lib.data.ILuaObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;

import java.util.Optional;

public class GGui implements ILuaObject {

    private MatrixStack stack() { return new MatrixStack(); }

    @Override
    public String[] getMethodNames() {
        return new String[]{
                "drawString",
                "drawCenteredString",
                "drawRect",
                "drawTexturedRect"
        };
    }

    @Override
    public String[] getVariableNames() {
        return new String[0];
    }

    @Override
    public Object[] call(int method, Object[] args) {

        switch (method) {
            case 0:
            {
                if (args.length < 4)
                    break;

                if (!(args[0] instanceof String) || !(args[1] instanceof Number) || !(args[2] instanceof Number) || !(args[3] instanceof Number)) {
                    break;
                }

                boolean shadow = true;
                if (args.length >= 5 && args[4] instanceof Boolean) {
                    shadow = ((Boolean)args[4]).booleanValue();
                }

                String text = (String)args[0];
                float x = ((Number)args[1]).floatValue();
                float y = ((Number)args[2]).floatValue();
                int color = ((Number)args[3]).intValue();
                if (shadow)
                    Minecraft.getInstance().font.drawShadow(stack(), text, x, y, color);
                else
                    Minecraft.getInstance().font.draw(stack(), text, x, y, color);

                break;
            }
            case 1:
            {
                if (args.length < 4)
                    break;

                if (!(args[0] instanceof String) || !(args[1] instanceof Number) || !(args[2] instanceof Number) || !(args[3] instanceof Number)) {
                    break;
                }

                boolean shadow = true;
                if (args.length >= 5 && args[4] instanceof Boolean) {
                    shadow = ((Boolean)args[4]).booleanValue();
                }

                String text = (String)args[0];
                float x = ((Number)args[1]).floatValue();
                float y = ((Number)args[2]).floatValue();
                int color = ((Number)args[3]).intValue();
                FontRenderer font = Minecraft.getInstance().font;
                if (shadow)
                    font.drawShadow(stack(), text, x - (float)font.width(text) / 2.0f, y, color);
                else
                    font.draw(stack(), text, x - (float)font.width(text) / 2.0f, y, color);

                break;
            }
            case 2:
            {
                if (args.length < 5)
                    break;

                if (!(args[0] instanceof Number) || !(args[1] instanceof Number) || !(args[2] instanceof Number) || !(args[3] instanceof Number) || !(args[4] instanceof Number))
                    break;

                int x = ((Number)args[0]).intValue();
                int y = ((Number)args[1]).intValue();
                int w = ((Number)args[2]).intValue();
                int h = ((Number)args[3]).intValue();
                int color = ((Number)args[4]).intValue();

                Screen.fill(stack(), x, y, x + w, y + h, color);

                break;
            }
        }

        return new Object[0];
    }

    @Override
    public Object access(int variable, Optional<Object> value) {
        return null;
    }
}
