package in.northwestw.autofish;

import in.northwestw.autofish.handler.AutoFishHandler;
import in.northwestw.autofish.keybind.KeyBinds;
import net.minecraftforge.client.event.InputEvent;
//? if >=1.19.2 {
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
//? } else {
/*import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
*///? }
import net.minecraftforge.event.TickEvent;
//? if >=1.21.11 {
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
//? } else
//import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod(AutoFish.MOD_ID)
public class AutoFishForge {

    public AutoFishForge() {
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        //? if >=1.19.2 {
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(KeyBinds.autofish);
            event.register(KeyBinds.rodprotect);
            event.register(KeyBinds.autoreplace);
            event.register(KeyBinds.settings);
            event.register(KeyBinds.itemfilter);
        }
        //? } else {
        /*public static void setupClient(FMLClientSetupEvent event) {
            ClientRegistry.registerKeyBinding(KeyBinds.autofish);
            ClientRegistry.registerKeyBinding(KeyBinds.rodprotect);
            ClientRegistry.registerKeyBinding(KeyBinds.autoreplace);
            ClientRegistry.registerKeyBinding(KeyBinds.settings);
            ClientRegistry.registerKeyBinding(KeyBinds.itemfilter);
        }
        *///? }

        @SubscribeEvent
        //? if >=1.19.2 {
        public static void inputKey(InputEvent.Key event) {
        //? } else
        //public static void inputKey(InputEvent.KeyInputEvent event) {
            AutoFishHandler.onKeyInput();
        }

        @SubscribeEvent
        //? if >=1.21.1 {
        public static void playerTickPre(TickEvent.PlayerTickEvent.Pre event) {
        //? } else {
        /*public static void playerTickPre(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.START) return;
            *///? }
            //? if >=1.21.11 {
            if (event.side() != LogicalSide.CLIENT) return;
            AutoFishHandler.onPlayerTick(event.player());
            //? } else {
            /*if (event.side != LogicalSide.CLIENT) return;
            AutoFishHandler.onPlayerTick(event.player);
            *///? }
        }
    }
}