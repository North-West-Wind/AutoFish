package in.northwestw.autofish.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//? if >=1.19.2 {
import org.lwjgl.glfw.GLFW;
//? }

public class ScreenHelper {
    //? if >=1.19.2 {
    public static final int MOUSE_BUTTON_LEFT = GLFW.GLFW_MOUSE_BUTTON_2;
    public static final int KEY_ESCAPE = GLFW.GLFW_KEY_ESCAPE;
    //? } else {
    /*public static final int MOUSE_BUTTON_LEFT = 1;
    public static final int KEY_ESCAPE = 256;
    *///? }

    public static void showScreen(Screen screen) {
        //? if >=1.21.11 {
        Minecraft.getInstance().setScreenAndShow(screen);
        //? } else
        //Minecraft.getInstance().setScreen(screen);
    }

    public static Button makeButton(int x, int y, int width, int height, Component label, Button.OnPress onPress) {
        //? if >=1.19.4 {
        return new Button.Builder(label, onPress).pos(x, y).size(width, height).build();
        //? } else
        //return new Button(x, y, width, height, label, onPress);
    }
}
