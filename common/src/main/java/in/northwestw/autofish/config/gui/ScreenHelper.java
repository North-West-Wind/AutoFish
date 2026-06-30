package in.northwestw.autofish.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ScreenHelper {
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
