package in.northwestw.autofish;

import in.northwestw.autofish.handler.AutoFishHandler;
import in.northwestw.autofish.keybind.KeyBinds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=26.1 {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//? } else
//import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class AutoFishFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        //? if >=26.1 {
        KeyMappingHelper.registerKeyMapping(KeyBinds.autofish);
        KeyMappingHelper.registerKeyMapping(KeyBinds.rodprotect);
        KeyMappingHelper.registerKeyMapping(KeyBinds.autoreplace);
        KeyMappingHelper.registerKeyMapping(KeyBinds.settings);
        KeyMappingHelper.registerKeyMapping(KeyBinds.itemfilter);
        //? } else {
        /*KeyBindingHelper.registerKeyBinding(KeyBinds.autofish);
        KeyBindingHelper.registerKeyBinding(KeyBinds.rodprotect);
        KeyBindingHelper.registerKeyBinding(KeyBinds.autoreplace);
        KeyBindingHelper.registerKeyBinding(KeyBinds.settings);
        KeyBindingHelper.registerKeyBinding(KeyBinds.itemfilter);
        *///? }

        ClientTickEvents.END_CLIENT_TICK.register(client -> AutoFishHandler.onKeyInput());
        //? if >=26.1 {
        ClientTickEvents.START_CLIENT_TICK.register(client -> AutoFishHandler.onPlayerTick(client.player));
        //? } else
        //ClientTickEvents.START_CLIENT_TICK.register(client -> AutoFishHandler.onPlayerTick(client.player));
    }
}
