package ml.northwestwind.forgeautofish;

import ml.northwestwind.forgeautofish.config.Config;
import ml.northwestwind.forgeautofish.keybind.KeyBinds;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AutoFish.MODID)
public class AutoFish
{
    public static final String MODID = "forgeautofish";
    public static final Logger LOGGER = LogManager.getLogger();

    public AutoFish(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT);

        RegisterKeyMappingsEvent.BUS.addListener(KeyBinds::register);

        Config.loadConfig(FMLPaths.CONFIGDIR.get().resolve("forgeautofish-client.toml").toString());
        context.registerExtensionPoint(IExtensionPoint.DisplayTest.class, ()->new IExtensionPoint.DisplayTest(()->"ANY", (remote, isServer)-> true));
    }

    public static MutableComponent getTranslatableComponent(String key, Object... args) {
        return MutableComponent.create(new TranslatableContents(key, null, args));
    }

    public static MutableComponent getLiteralComponent(String str) {
        return MutableComponent.create(new PlainTextContents.LiteralContents(str));
    }
}
