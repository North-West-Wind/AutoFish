package in.northwestw.autofish.keybind;

import in.northwestw.autofish.AutoFish;
import net.minecraft.client.KeyMapping;
//? if >=1.21.11 {
import net.minecraft.resources.Identifier;
//? }
//? if >=1.19.2 {
import org.lwjgl.glfw.GLFW;
//? }

public class KeyBinds {
    //? if >=1.19.2 {
    private static final int KEY_MINUS = GLFW.GLFW_KEY_MINUS;
    private static final int KEY_BACKSLASH = GLFW.GLFW_KEY_BACKSLASH;
    private static final int KEY_RIGHT_BRACKET = GLFW.GLFW_KEY_RIGHT_BRACKET;
    private static final int KEY_K = GLFW.GLFW_KEY_K;
    private static final int KEY_APOSTROPHE = GLFW.GLFW_KEY_APOSTROPHE;
    //? } else {
    /*private static final int KEY_MINUS = 45;
    private static final int KEY_BACKSLASH = 92;
    private static final int KEY_RIGHT_BRACKET = 93;
    private static final int KEY_K = 75;
    private static final int KEY_APOSTROPHE = 39;
    *///? }

    public static KeyMapping autofish, rodprotect, autoreplace, settings, itemfilter;

    static {
        //? if >= 1.21.11 {
        KeyMapping.Category cat = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(AutoFish.MOD_ID, "autofish"));
        //? } else
        //String cat = "key.categories.autofish";
        autofish = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.autofish").getString(), KEY_MINUS, cat);
        rodprotect = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.rodprotect").getString(), KEY_BACKSLASH, cat);
        autoreplace = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.autoreplace").getString(), KEY_RIGHT_BRACKET, cat);
        settings = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.settings").getString(), KEY_K, cat);
        itemfilter = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.itemfilter").getString(), KEY_APOSTROPHE, cat);
    }
}
