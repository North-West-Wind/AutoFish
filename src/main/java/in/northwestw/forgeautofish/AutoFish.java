package in.northwestw.forgeautofish;

import in.northwestw.forgeautofish.config.Config;
import in.northwestw.forgeautofish.keybind.KeyBinds;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AutoFish.MODID)
public class AutoFish
{
    public static final String MODID = "forgeautofish";
    public static final Logger LOGGER = LogManager.getLogger();

    public AutoFish(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT);
        modEventBus.addListener(KeyBinds::register);
        modEventBus.addListener(Config::onLoad);
    }

    public static MutableComponent getTranslatableComponent(String key, Object... args) {
        return MutableComponent.create(new TranslatableContents(key, null, args));
    }

    public static MutableComponent getLiteralComponent(String str) {
        return MutableComponent.create(new PlainTextContents.LiteralContents(str));
    }
}
