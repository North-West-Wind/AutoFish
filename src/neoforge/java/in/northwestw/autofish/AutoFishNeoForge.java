package in.northwestw.autofish;

import in.northwestw.autofish.handler.AutoFishHandler;
import in.northwestw.autofish.keybind.KeyBinds;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@Mod(AutoFish.MOD_ID)
public class AutoFishNeoForge {

    public AutoFishNeoForge(IEventBus eventBus) {
    }

    @EventBusSubscriber
    public static class ModEvents {
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(KeyBinds.autofish);
            event.register(KeyBinds.rodprotect);
            event.register(KeyBinds.autoreplace);
            event.register(KeyBinds.settings);
            event.register(KeyBinds.itemfilter);
        }

        @SubscribeEvent
        public static void inputKey(InputEvent.Key event) {
            AutoFishHandler.onKeyInput();
        }

        @SubscribeEvent
        public static void playerTickPre(PlayerTickEvent.Pre event) {
            if (!(event.getEntity() instanceof LocalPlayer)) return;
            AutoFishHandler.onPlayerTick(event.getEntity());
        }
    }
}