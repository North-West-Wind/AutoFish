package in.northwestw.autofish.config.gui;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import in.northwestw.autofish.AutoFish;
import in.northwestw.autofish.config.Config;
//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} elif >=1.20.1 {
//import net.minecraft.client.gui.GuiGraphics;
//? } else
//import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.11 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//? }
import net.minecraft.core.HolderSet;
//? if >=1.19.4 {
import net.minecraft.core.registries.BuiltInRegistries;
//? } else
//import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterSelectionScreen extends Screen {
    private final Screen parent;
    private EditBox search;
    //? if >=1.19.4 {
    private final Collection<Item> original = BuiltInRegistries.ITEM.stream().toList();
    //? } else
    //private final Collection<Item> original = Registry.ITEM.stream().toList();
    private Collection<Item> searching;
    private final Set<Item> selected = new HashSet<>(Config.filter.stream().map(string ->
            //? if >=1.21.1 {
            BuiltInRegistries.ITEM.getOptional(Identifier.parse(string))
            //? } elif >=1.19.4 {
            //BuiltInRegistries.ITEM.getOptional(new Identifier(string))
            //? } else
            //Optional.of(Registry.ITEM.get(new Identifier(string)))
    ).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    private int page, maxPage = (int) Math.ceil(original.size() / 300.0), max = 300;
    private boolean clickProcessed = true;
    private double clickX, clickY;
    private Button previous, next;
    int reducedHeight;
    int reducedWidth;

    public FilterSelectionScreen(Screen parent) {
        super(AutoFish.getTranslatableComponent("gui.filterselection"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        reducedHeight = this.height - 90;
        reducedWidth = this.width - 30;
        max = /* (int) Math.round(300 * (reducedWidth / 550.0 + reducedHeight / 330.0) / 2.0) */ 300;
        maxPage = (int) Math.ceil(original.size() / (double) max);
        searching = original;
        search = new EditBox(this.font, this.width / 2 - 75, 35, 150, 20, AutoFish.getTranslatableComponent("gui.superfilterscreen.search")) {
            @Override
            //? if >=1.21.11 {
            public boolean mouseClicked(MouseButtonEvent ev, boolean p_430750_) {
                if (ev.button() == ScreenHelper.MOUSE_BUTTON_LEFT) this.setValue("");
                return super.mouseClicked(ev, p_430750_);
            }
            //? } else {
            /*public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == ScreenHelper.MOUSE_BUTTON_LEFT) this.setValue("");
                return super.mouseClicked(mouseX, mouseY, button);
            }
            *///? }
        };
        search.setResponder(s -> {
            String[] args = s.split("/ +/");
            List<String> mods = Lists.newArrayList(), tags = Lists.newArrayList(), paths = Lists.newArrayList();
            for (String arg : args) {
                if (arg.startsWith("@")) mods.add(arg.toLowerCase().substring(1));
                else if (arg.startsWith("#")) tags.add(arg.toLowerCase().substring(1));
                else paths.add(arg.toLowerCase());
            }
            //? if >=1.21.11 {
            List<HolderSet.Named<Item>> itemTags = BuiltInRegistries.ITEM.getTags().filter(tag -> tags.stream().anyMatch(t -> tag.key().location().getPath().contains(t))).toList();
             //? } elif >=1.19.4 {
            //List<HolderSet.Named<Item>> itemTags = BuiltInRegistries.ITEM.getTags().map(Pair::getSecond).filter(tag -> tags.stream().anyMatch(t -> tag.key().location().getPath().contains(t))).toList();
            //? } else
            //List<HolderSet.Named<Item>> itemTags = Registry.ITEM.getTags().map(Pair::getSecond).filter(tag -> tags.stream().anyMatch(t -> tag.key().location().getPath().contains(t))).toList();
            searching = original.stream().filter(item -> {
                //? if >=1.21.11 {
                Optional<ResourceKey<Item>> opt = BuiltInRegistries.ITEM.getResourceKey(item);
                if (opt.isEmpty()) return false;
                Identifier rl = opt.get().identifier();
                //? } elif >=1.19.4 {
                /*Optional<ResourceKey<Item>> opt = BuiltInRegistries.ITEM.getResourceKey(item);
                if (opt.isEmpty()) return false;
                Identifier rl = opt.get().location();
                *///? } else {
                /*Optional<ResourceKey<Item>> opt = Registry.ITEM.getResourceKey(item);
                if (opt.isEmpty()) return false;
                Identifier rl = opt.get().location();
                *///? }
                boolean matchmod = mods.isEmpty(), matchtag = tags.isEmpty(), matcharg = false;
                for (String mod : mods)
                    matchmod = matchmod || rl.getNamespace().toLowerCase().contains(mod);
                for (HolderSet.Named<Item> itemTag : itemTags)
                    matchtag = matchtag || itemTag.stream().anyMatch(tagItem -> tagItem.value() == item);
                for (String arg : paths)
                    matcharg = matcharg || rl.getPath().contains(arg);
                return matchmod && matchtag && matcharg;
            }).collect(Collectors.toList());
            maxPage = (int) Math.ceil(searching.size() / (double) max);
            if (page > maxPage - 1) page = Math.max(0, maxPage - 1);
        });
        addRenderableWidget(search);
        Button add = ScreenHelper.makeButton(this.width / 2 - 75, 60, 72, 20, AutoFish.getTranslatableComponent("gui.filterselection.save"), button -> {
            //? if >=1.19.4 {
            List<String> items = selected.stream().map(item -> BuiltInRegistries.ITEM.getKey(item).toString()).collect(Collectors.toList());
            //? } else
            //List<String> items = selected.stream().map(item -> Registry.ITEM.getKey(item).toString()).collect(Collectors.toList());
            Config.setFilter(items);
            ScreenHelper.showScreen(parent);
        });
        addRenderableWidget(add);
        Button done = ScreenHelper.makeButton(this.width / 2 + 3, 60, 72, 20, AutoFish.getTranslatableComponent("gui.filterselection.cancel"), button -> ScreenHelper.showScreen(parent));
        addRenderableWidget(done);
        previous = ScreenHelper.makeButton(this.width / 2 - 100, 60, 20, 20, AutoFish.getLiteralComponent("<"), button -> { if (page > 0) page--; });
        previous.visible = false;
        addRenderableWidget(previous);
        next = ScreenHelper.makeButton(this.width / 2 + 80, 60, 20, 20, AutoFish.getLiteralComponent(">"), button -> { if (page < maxPage - 1) page++; });
        next.visible = false;
        addRenderableWidget(next);
    }

    @Override
    //? if >=26.1 {
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, -1);
    //?} elif >=1.20.1 {
    /*public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, -1);
    *///?} else {
    /*public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, -1);
    *///? }
        Collection<Item> searchingCopy = Lists.newArrayList();
        Collection<Item> prioritized = searching.stream().filter(item -> {
            //? if >=1.21.11 {
            Optional<ResourceKey<Item>> opt = BuiltInRegistries.ITEM.getResourceKey(item);
            if (opt.isEmpty()) return false;
            Identifier rl = opt.get().identifier();
            //? } elif >=1.19.4 {
            /*Optional<ResourceKey<Item>> opt = BuiltInRegistries.ITEM.getResourceKey(item);
            if (opt.isEmpty()) return false;
            Identifier rl = opt.get().location();
            *///? } else {
            /*Optional<ResourceKey<Item>> opt = Registry.ITEM.getResourceKey(item);
            if (opt.isEmpty()) return false;
            Identifier rl = opt.get().location();
            *///? }
            boolean pri = Config.prioritize.contains(rl.toString());
            if (!pri) searchingCopy.add(item);
            return pri;
        }).toList();
        Item[] items = Stream.concat(prioritized.stream(), searchingCopy.stream()).toArray(Item[]::new);
        if (items.length > 0 && page >= 0) {
            for (int i = page * max; i < Math.min((page + 1) * max, searching.size()); i++) {
                Item item = items[i];
                int h = (i % max) / (max / 30);
                int k = (i % max) % (max / 30);
                int x = getXPos(h, reducedWidth);
                int y = getYPos(k, reducedHeight);
                ItemStack stack = new ItemStack(item);
                if (!stack.isEmpty()) {
                    //? if >=26.1 {
                    graphics.item(stack, x, y);
                    //? } elif >=1.20.1 {
                    //graphics.renderItem(stack, x, y);
                    //? } elif >=1.19.4 {
                    //itemRenderer.renderGuiItem(poseStack, stack, x, y);
                    //? } else
                    //itemRenderer.renderGuiItem(stack, x, y);
                    if (!clickProcessed && isMouseInRange(clickX, clickY, x, y, x+16, y+16)) {
                        if (selected.contains(item)) selected.remove(item);
                        else selected.add(item);
                        clickProcessed = true;
                    }
                    //? if >=1.20.1 {
                    if (selected.contains(item)) graphics.fillGradient(x - 2, y - 2, x + 18, y + 18, 0xFF00FF00, 0xFF00FF00);
                    else if (isMouseInRange(mouseX, mouseY, x, y,x + 16, y + 16)) graphics.fillGradient(x - 2, y - 2, x + 18, y + 18, 0xFFC0C0C0, 0xFFC0C0C0);
                    //? } else {
                    /*if (selected.contains(item)) fillGradient(poseStack, x - 2, y - 2, x + 18, y + 18, 0xFF00FF00, 0xFF00FF00);
                    else if (isMouseInRange(mouseX, mouseY, x, y,x + 16, y + 16)) fillGradient(poseStack, x - 2, y - 2, x + 18, y + 18, 0xFFC0C0C0, 0xFFC0C0C0);
                    *///? }
                    //if (isMouseInRange(mouseX, mouseY, x, y,x + 16, y + 16)) graphics.item(this.font, stack, mouseX, mouseY);
                    //? if >=26.1 {
                    graphics.item(stack, x, y);
                    //? } elif >=1.20.1 {
                    //graphics.renderItem(stack, x, y);
                    //? } elif >=1.19.4 {
                    //itemRenderer.renderGuiItem(poseStack, stack, x, y);
                    //? } else
                    //itemRenderer.renderGuiItem(stack, x, y);
                }
            }
        }
        //? if >=26.1 {
        search.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        //?} elif >=1.20.1 {
        //search.render(graphics, mouseX, mouseY, partialTicks);
        //? } else
        //search.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private boolean isMouseInRange(double mouseX, double mouseY, int x1, int y1, int x2, int y2) {
        return mouseX > x1 && mouseX < x2 && mouseY > y1 && mouseY < y2;
    }

    private int getXPos(int h, int width) {
        return (width * h / 30) + 15;
    }

    private int getYPos(int k, int height) {
        return ((height * k / (max / 30)) + 90);
    }

    @Override
    //? if >=1.21.11 {
    public boolean keyPressed(KeyEvent ev) {
        if (ev.key() == ScreenHelper.KEY_ESCAPE) {
            if (!search.isFocused()) ScreenHelper.showScreen(parent);
            else search.setFocused(false);
        }
        return super.keyPressed(ev);
    }
    //? } else {
    /*public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == ScreenHelper.KEY_ESCAPE) {
            if (!search.isFocused()) ScreenHelper.showScreen(parent);
            else
                //? if >=1.19.4 {
                search.setFocused(false);
                //? } else
                //search.setFocus(false);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    *///? }

    @Override
    //? if >=1.21.11 {
    public boolean mouseClicked(MouseButtonEvent ev, boolean flag) {
        clickX = ev.x();
        clickY = ev.y();
        clickProcessed = false;
        return super.mouseClicked(ev, flag);
    }
    //? } else {
    /*public boolean mouseClicked(double mouseX, double mouseY, int button) {
        clickX = mouseX;
        clickY = mouseY;
        clickProcessed = false;
        return super.mouseClicked(mouseX, mouseY, button);
    }
    *///? }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void tick() {
        //search.tick();
        super.tick();
        previous.visible = page >= 1;
        next.visible = page < maxPage - 1;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
