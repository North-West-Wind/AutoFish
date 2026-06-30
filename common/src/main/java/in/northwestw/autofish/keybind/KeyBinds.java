package in.northwestw.autofish.keybind;

import in.northwestw.autofish.AutoFish;
import net.minecraft.client.KeyMapping;
//? if >=1.21.11 {
import net.minecraft.resources.Identifier;
//? }
import org.lwjgl.glfw.GLFW;

public class KeyBinds {

    public static KeyMapping autofish, rodprotect, autoreplace, settings, itemfilter;

    static {
        //? if >= 1.21.11 {
        KeyMapping.Category cat = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(AutoFish.MOD_ID, "autofish"));
        //? } else
        //String cat = "key.categories.autofish";
        autofish = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.autofish").getString(), GLFW.GLFW_KEY_MINUS, cat);
        rodprotect = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.rodprotect").getString(), GLFW.GLFW_KEY_BACKSLASH, cat);
        autoreplace = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.autoreplace").getString(), GLFW.GLFW_KEY_RIGHT_BRACKET, cat);
        settings = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.settings").getString(), GLFW.GLFW_KEY_K, cat);
        itemfilter = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.itemfilter").getString(), GLFW.GLFW_KEY_APOSTROPHE, cat);
    }
}
