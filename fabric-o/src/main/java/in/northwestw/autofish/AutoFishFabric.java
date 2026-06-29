package in.northwestw.autofish;

import in.northwestw.autofish.handler.AutoFishHandler;
import in.northwestw.autofish.keybind.KeyBinds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

public class AutoFishFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        KeyMappingHelper.registerKeyMapping(KeyBinds.autofish);
        KeyMappingHelper.registerKeyMapping(KeyBinds.rodprotect);
        KeyMappingHelper.registerKeyMapping(KeyBinds.autoreplace);
        KeyMappingHelper.registerKeyMapping(KeyBinds.settings);
        KeyMappingHelper.registerKeyMapping(KeyBinds.itemfilter);

        ClientTickEvents.END_CLIENT_TICK.register(_ -> AutoFishHandler.onKeyInput());
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            AutoFishHandler.onPlayerTick(client.player);
        });
    }
}
