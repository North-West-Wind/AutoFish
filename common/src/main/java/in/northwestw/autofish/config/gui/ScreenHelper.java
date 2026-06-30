package in.northwestw.autofish.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ScreenHelper {
    public static void showScreen(Screen screen) {
        //? if >=1.21.11 {
        Minecraft.getInstance().setScreenAndShow(screen);
        //? } else
        //Minecraft.getInstance().setScreen(screen);
    }
}
