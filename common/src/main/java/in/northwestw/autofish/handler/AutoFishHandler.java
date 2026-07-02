package in.northwestw.autofish.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import in.northwestw.autofish.AutoFish;
import in.northwestw.autofish.config.Config;
import in.northwestw.autofish.config.gui.ScreenHelper;
import in.northwestw.autofish.config.gui.SettingsScreen;
import in.northwestw.autofish.keybind.KeyBinds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
//? if >=1.19.4 {
import net.minecraft.core.registries.BuiltInRegistries;
//? } else
//import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AutoFishHandler {
    private static final List<Item> shouldDrop = Lists.newArrayList();
    private static boolean processingDrop, pendingReelIn, pendingRecast, lastTickFishing, afterDrop;
    private static int dropCd, rodSlot;
    private static long tick, checkTick;
    private static final Map<String, Integer> itemsBeforeFished = Maps.newHashMap();

    public static void onKeyInput() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (KeyBinds.autofish.consumeClick()) {
            Config.setAutoFish(!Config.autoFish);
            if (player != null) sendOverlayMessage(player, "autofish", Config.autoFish);
        } else if (KeyBinds.rodprotect.consumeClick()) {
            Config.setRodProtect(!Config.rodProtect);
            if (player != null) sendOverlayMessage(player, "rodprotect", Config.rodProtect);
        } else if (KeyBinds.autoreplace.consumeClick()) {
            Config.setAutoReplace(!Config.autoReplace);
            if (player != null) sendOverlayMessage(player, "autoreplace", Config.autoReplace);
        } else if (KeyBinds.itemfilter.consumeClick()) {
            Config.enableFilter(!Config.allFilters);
            if (player != null) sendOverlayMessage(player, "itemfilter", Config.allFilters);
        } else if (KeyBinds.settings.consumeClick())
            ScreenHelper.showScreen(new SettingsScreen());
    }

    public static void onPlayerTick(final Player player) {
        if (Minecraft.getInstance().player == null) return;
        if (!player.getUUID().equals(Minecraft.getInstance().player.getUUID())) return;
        if (checkTick > 0) checkTick--;
        else {
            checkTick = Config.checkInterval;
            if (!pendingRecast) {
                if (player.fishing == null) recast(player);
                else if (player.fishing.getDeltaMovement().lengthSqr() == 0) pendingReelIn = true;
            }
        }
        if (afterDrop) {
            if (tick == 0 && rodSlot != -1) {
                //? if >=1.21.11 {
                player.getInventory().setSelectedSlot(rodSlot);
                //? } elif >=1.17.1 {
                /*player.getInventory().selected = rodSlot;
                *///? } else
                //player.inventory.selected = rodSlot;
                rodSlot = -1;
            }
            tick++;
            if (tick > 2) {
                afterDrop = false;
                tick = 0;
            }
            return;
        }
        if (pendingReelIn) {
            tick++;
            if (tick >= Config.reelInDelay) {
                reelIn(player);
                tick = 0;
                pendingReelIn = false;
            }
            return;
        }
        if (processingDrop) {
            if (dropCd > 0) dropCd--;
            dropItem(player);
            if (shouldDrop.isEmpty()) {
                processingDrop = false;
                afterDrop = true;
            }
            return;
        }
        if (pendingRecast) {
            tick++;
            if (tick >= Config.recastDelay) {
                checkItem(player);
                if (processingDrop) {
                    tick = 0;
                    return;
                }
                recast(player);
                tick = 0;
                pendingRecast = false;
            }
            return;
        }
        if (!Config.autoFish || player.fishing == null) return;
        Vec3 vector = player.fishing.getDeltaMovement();
        double x = vector.x();
        double y = vector.y();
        double z = vector.z();
        //? if >=1.20.1 {
        Level level = player.level();
        //? } else
        //Level level = player.level;
        if (y < -0.075 && !level.getFluidState(player.fishing.blockPosition()).isEmpty() && x == 0 && z == 0)
            pendingReelIn = true;
    }

    private static void reelIn(Player player) {
        if (!Config.autoFish) return;
        InteractionHand hand = findHandOfRod(player);
        if (hand == null) return;
        //? if >=1.21.11 {
        List<ItemStack> items = player.getInventory().getNonEquipmentItems();
        //? } elif >=1.17.1 {
        /*List<ItemStack> items = player.getInventory().items;
        *///? } else
        //List<ItemStack> items = player.inventory.items;
        items.forEach(stack -> {
            //? if >=1.19.4 {
            Identifier rl = BuiltInRegistries.ITEM.getKey(stack.getItem());
            //? } else
            //Identifier rl = Registry.ITEM.getKey(stack.getItem());
            //? if >=26.1 {
            itemsBeforeFished.put(rl.toString(), itemsBeforeFished.getOrDefault(rl, 0) + stack.count());
            //? } else
            //itemsBeforeFished.put(rl.toString(), itemsBeforeFished.getOrDefault(rl.toString(), 0) + stack.getCount());
        });
        click(player, hand, Minecraft.getInstance().gameMode);
        ItemStack fishingRod = player.getItemInHand(hand);
        boolean needReplace = false;
        if (fishingRod.getMaxDamage() - fishingRod.getDamageValue() < 2)
            if (Config.autoReplace) needReplace = true;
            else return;
        else if (fishingRod.getMaxDamage() - fishingRod.getDamageValue() < 3 && !player.isCreative() && Config.rodProtect)
            if (Config.autoReplace) needReplace = true;
            else {
                Config.autoFish = false;
                sendOverlayMessage(player, "autofish", Config.autoFish);
                return;
            }
        if (needReplace) {
            AutoFish.LOGGER.info("Fishing rod broke. Finding replacement...");
            boolean found = false;
            for (int i = 0; i < 9; i++) {
                //? if >=1.21.11 {
                if (i == player.getInventory().getSelectedSlot()) continue;
                ItemStack stack = player.getInventory().getItem(i);
                //? } elif >=1.17.1 {
                /*if (i == player.getInventory().selected) continue;
                ItemStack stack = player.getInventory().getItem(i);
                *///? } else {
                /*if (i == player.inventory.selected) continue;
                ItemStack stack = player.inventory.getItem(i);
                *///? }
                if (stack.getItem() instanceof FishingRodItem) {
                    if (Config.rodProtect && stack.getMaxDamage() - stack.getDamageValue() < 2) continue;
                    AutoFish.LOGGER.info("Found fishing rod for replacement");
                    //? if >=1.21.11 {
                    player.getInventory().setSelectedSlot(i);
                    //? } elif >=1.17.1 {
                    /*player.getInventory().selected = i;
                    *///? } else
                    //player.inventory.selected = i;
                    found = true;
                    break;
                }
            }
            if (!found) return;
        }
        pendingRecast = true;
    }

    private static void recast(Player player) {
        if (!Config.autoFish) return;
        InteractionHand hand = findHandOfRod(player);
        if (hand == null) return;
        ItemStack fishingRod = player.getItemInHand(hand);
        if (fishingRod.isEmpty()) return;
        click(player, hand, Minecraft.getInstance().gameMode);
    }

    private static void checkItem(Player player) {
        if (!itemsBeforeFished.isEmpty()) {
            //? if >=1.21.11 {
            List<ItemStack> items = player.getInventory().getNonEquipmentItems();
            //? } elif >=1.17.1 {
            /*List<ItemStack> items = player.getInventory().items;
            *///? } else
            //List<ItemStack> items = player.inventory.items;
            for (String name : Config.filter) {
                //? if >=1.21.1 {
                Identifier rl = Identifier.parse(name);
                //? } else
                //Identifier rl = new Identifier(name);
                //? if >=1.19.4 {
                Optional<Item> opt = BuiltInRegistries.ITEM.getOptional(rl);
                //? } else
                //Optional<Item> opt = Registry.ITEM.getOptional(rl);
                if (!opt.isPresent()) continue;
                Item item = opt.get();
                int newCount = items.stream().filter(stack -> stack.getItem().equals(item)).mapToInt(ItemStack::getCount).reduce(Integer::sum).orElse(0);
                int oldCount = itemsBeforeFished.getOrDefault(rl.toString(), 0);
                int diff = newCount - oldCount;
                for (int ii = 0; ii < diff; ii++) shouldDrop.add(item);
            }
            itemsBeforeFished.clear();
            if (!shouldDrop.isEmpty()) {
                processingDrop = true;
                //? if >=1.21.11 {
                rodSlot = player.getInventory().getSelectedSlot();
                //? } elif >=1.17.1 {
                /*rodSlot = player.getInventory().selected;
                *///? } else
                //rodSlot = player.inventory.selected;
            }
        }
    }

    private static void dropItem(Player player) {
        if (dropCd != 10 && dropCd != 0) return;
        Item item = shouldDrop.get(0);
        if (dropCd == 10) {
            ((LocalPlayer) player).drop(false);
            shouldDrop.remove(item);
            return;
        }
        for (int ii = 0; ii < 9; ii++) {
            //? if >=1.21.11 {
            if (!player.getInventory().getItem(ii).getItem().equals(item)) continue;
            player.getInventory().setSelectedSlot(ii);
            //? } elif >=1.17.1 {
            /*if (!player.getInventory().getItem(ii).getItem().equals(item)) continue;
            player.getInventory().selected = ii;
            *///? } else {
            /*if (!player.inventory.getItem(ii).getItem().equals(item)) continue;
            player.inventory.selected = ii;
            *///? }
            dropCd = 20;
            return;
        }
        // if item cannot be found in hotbar, just ignore it
        shouldDrop.remove(item);
    }

    private static void click(Player player, InteractionHand hand, MultiPlayerGameMode controller) {
        if (controller == null) return;
        //? if >=1.19.2 {
        controller.useItem(player, hand);
        //? } else
        //controller.useItem(player, player.level, hand);
    }

    private static InteractionHand findHandOfRod(Player player) {
        if (player.getMainHandItem().getItem() instanceof FishingRodItem) return InteractionHand.MAIN_HAND;
        else if (player.getOffhandItem().getItem() instanceof FishingRodItem) return InteractionHand.OFF_HAND;
        else return null;
    }

    private static void sendOverlayMessage(Player player, String key, boolean state) {
        Component component = AutoFish.getTranslatableComponent("toggle." + key, AutoFish.getTranslatableComponent("toggle.enable." + state).withStyle(state ? ChatFormatting.GREEN : ChatFormatting.RED));
        //? if >=26.1 {
        player.sendOverlayMessage(component);
        //? } else
        //player.displayClientMessage(component, true);
    }
}