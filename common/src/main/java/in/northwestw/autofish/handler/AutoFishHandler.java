package in.northwestw.autofish.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import in.northwestw.autofish.AutoFish;
import in.northwestw.autofish.config.Config;
import in.northwestw.autofish.config.gui.SettingsScreen;
import in.northwestw.autofish.keybind.KeyBinds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AutoFishHandler {
    private static final List<Item> shouldDrop = Lists.newArrayList();
    private static boolean processingDrop, pendingReelIn, pendingRecast, lastTickFishing, afterDrop;
    private static int dropCd, rodSlot;
    private static long tick, checkTick;
    private static final Map<Identifier, Integer> itemsBeforeFished = Maps.newHashMap();

    public static void onKeyInput() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (KeyBinds.autofish.consumeClick()) {
            Config.setAutoFish(!Config.autoFish);
            if (player != null) player.sendOverlayMessage(getText("forgeautofish", Config.autoFish));
        } else if (KeyBinds.rodprotect.consumeClick()) {
            Config.setRodProtect(!Config.rodProtect);
            if (player != null) player.sendOverlayMessage(getText("rodprotect", Config.rodProtect));
        } else if (KeyBinds.autoreplace.consumeClick()) {
            Config.setAutoReplace(!Config.autoReplace);
            if (player != null) player.sendOverlayMessage(getText("autoreplace", Config.autoReplace));
        } else if (KeyBinds.itemfilter.consumeClick()) {
            Config.enableFilter(!Config.allFilters);
            if (player != null) player.sendOverlayMessage(getText("itemfilter", Config.allFilters));
        } else if (KeyBinds.settings.consumeClick())
            minecraft.setScreenAndShow(new SettingsScreen());
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
                player.getInventory().setSelectedSlot(rodSlot);
                AutoFish.LOGGER.info("Swapped to hotbar slot {} for rod", rodSlot);
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
        if (y < -0.075 && !player.level().getFluidState(player.fishing.blockPosition()).isEmpty() && x == 0 && z == 0)
            pendingReelIn = true;
    }

    private static void reelIn(Player player) {
        if (!Config.autoFish) return;
        InteractionHand hand = findHandOfRod(player);
        if (hand == null) return;
        player.getInventory().getNonEquipmentItems().forEach(stack -> {
            Identifier rl = BuiltInRegistries.ITEM.getKey(stack.getItem());
            itemsBeforeFished.put(rl, itemsBeforeFished.getOrDefault(rl, 0) + stack.count());
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
                player.sendOverlayMessage(getText("forgeautofish", Config.autoFish));
                return;
            }
        if (needReplace) {
            AutoFish.LOGGER.info("Fishing rod broke. Finding replacement...");
            boolean found = false;
            for (int i = 0; i < 9; i++) {
                if (i == player.getInventory().getSelectedSlot()) continue;
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() instanceof FishingRodItem) {
                    if (Config.rodProtect && stack.getMaxDamage() - stack.getDamageValue() < 2) continue;
                    AutoFish.LOGGER.info("Found fishing rod for replacement");
                    player.getInventory().setSelectedSlot(i);
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
            List<ItemStack> items = player.getInventory().getNonEquipmentItems();
            for (String name : Config.filter) {
                Identifier rl = Identifier.parse(name);
                Optional<Item> opt = BuiltInRegistries.ITEM.getOptional(rl);
                if (opt.isEmpty()) continue;
                Item item = opt.get();
                int newCount = items.stream().filter(stack -> stack.getItem().toString().equals(rl.toString())).mapToInt(ItemStack::getCount).reduce(Integer::sum).orElse(0);
                int oldCount = itemsBeforeFished.getOrDefault(rl, 0);
                int diff = newCount - oldCount;
                for (int ii = 0; ii < diff; ii++) shouldDrop.add(item);
            }
            itemsBeforeFished.clear();
            if (!shouldDrop.isEmpty()) {
                processingDrop = true;
                rodSlot = player.getInventory().getSelectedSlot();
            }
        }
    }

    private static void dropItem(Player player) {
        if (dropCd != 10 && dropCd != 0) return;
        Item item = shouldDrop.getFirst();
        if (dropCd == 10) {
            ((LocalPlayer) player).drop(false);
            shouldDrop.remove(item);
            return;
        }
        for (int ii = 0; ii < 9; ii++) {
            if (!player.getInventory().getItem(ii).getItem().equals(item)) continue;
            player.getInventory().setSelectedSlot(ii);
            AutoFish.LOGGER.info("Swapped to hotbar slot {} for item", ii);
            dropCd = 20;
            return;
        }
        // if item cannot be found in hotbar, just ignore it
        shouldDrop.remove(item);
    }

    private static void click(Player player, InteractionHand hand, MultiPlayerGameMode controller) {
        if (controller == null) return;
        controller.useItem(player, hand);
    }

    private static InteractionHand findHandOfRod(Player player) {
        if (player.getMainHandItem().getItem() instanceof FishingRodItem) return InteractionHand.MAIN_HAND;
        else if (player.getOffhandItem().getItem() instanceof FishingRodItem) return InteractionHand.OFF_HAND;
        else return null;
    }

    private static Component getText(String key, boolean bool) {
        return AutoFish.getTranslatableComponent("toggle." + key, AutoFish.getTranslatableComponent("toggle.enable." + bool).withStyle(bool ? ChatFormatting.GREEN : ChatFormatting.RED));
    }
}