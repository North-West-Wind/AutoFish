package in.northwestw.autofish;

import in.northwestw.autofish.config.Config;
import net.minecraft.network.chat.MutableComponent;
//? if >=1.21.1 {
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
//? } elif >=1.19.2 {
/*import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
*///? } else {
/*import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
*///? }
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AutoFish
{
    public static final String MOD_ID = "autofish";
    public static final Logger LOGGER = LogManager.getLogger();

    static {
        Config.load();
    }

    public static MutableComponent getTranslatableComponent(String key, Object... args) {
        //? if >=1.19.2 {
        return MutableComponent.create(new TranslatableContents(key, null, args));
        //? } else
        //return new TranslatableComponent(key, args);
    }

    public static MutableComponent getLiteralComponent(String str) {
        //? if >=1.21.1 {
        return MutableComponent.create(new PlainTextContents.LiteralContents(str));
        //? } elif >=1.19.2 {
        //return MutableComponent.create(new LiteralContents(str));
        //? } else
        //return new TextComponent(str);
    }
}
