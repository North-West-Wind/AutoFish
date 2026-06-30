package in.northwestw.autofish.config.gui;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import in.northwestw.autofish.AutoFish;
import in.northwestw.autofish.config.Config;
import net.minecraft.client.Minecraft;
//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
 //?} else
//import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.11 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//? }
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SuperFilterScreen extends Screen {
    private final Screen parent;
    private EditBox search;
    private Collection<Item> original;
    private Collection<Item> searching;
    private int page = 0, maxPage, max = 30;
    private Button previous, next;
    int reducedHeight;
    int reducedWidth;

    protected SuperFilterScreen(Screen parent) {
        super(AutoFish.getTranslatableComponent("gui.superfilterscreen"));
        this.parent = parent;
    }

    @Override
    public void tick() {
        //search.tick();
        previous.visible = page >= 1;
        next.visible = page < maxPage - 1;
    }

    @Override
    protected void init() {
        reducedHeight = this.height - 90;
        reducedWidth = this.width - 30;
        max = /* (int) Math.round(30 * (reducedWidth / 550.0 + reducedHeight / 330.0) / 2.0) */ 30;
        original = Config.filter.stream().map(string -> BuiltInRegistries.ITEM.getOptional(Identifier.parse(string))).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        maxPage = (int) Math.ceil(original.size() / (double) max);
        searching = original;
        search = new EditBox(this.font, this.width / 2 - 75, 35, 150, 20, AutoFish.getTranslatableComponent("gui.superfilterscreen.search")) {
            @Override
            //? if >=1.21.11 {
            public boolean mouseClicked(MouseButtonEvent ev, boolean p_430750_) {
                if (ev.button() == GLFW.GLFW_MOUSE_BUTTON_2) this.setValue("");
                return super.mouseClicked(ev, p_430750_);
            }
            //? } else {
            /*public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_2) this.setValue("");
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
            //? } else
            //List<HolderSet.Named<Item>> itemTags = BuiltInRegistries.ITEM.getTags().map(Pair::getSecond).filter(tag -> tags.stream().anyMatch(t -> tag.key().location().getPath().contains(t))).toList();
            searching = original.stream().filter(item -> {
                Optional<ResourceKey<Item>> opt = BuiltInRegistries.ITEM.getResourceKey(item);
                if (opt.isEmpty()) return false;
                //? if >=1.21.11 {
                Identifier rl = opt.get().identifier();
                //? } else
                //Identifier rl = opt.get().location();
                boolean matchmod = mods.isEmpty(), matchtag = tags.isEmpty(), matcharg = false;
                for (String mod : mods)
                    matchmod = matchmod || rl.getNamespace().toLowerCase().contains(mod);
                for (HolderSet.Named<Item> itemTag : itemTags)
                    matchtag = matchtag || itemTag.stream().anyMatch(tagItem -> tagItem.value() == item);
                for (String arg : paths)
                    matcharg = matcharg || rl.getPath().contains(arg);
                return matchmod && matchtag && matcharg;
            }).collect(Collectors.toList());
            maxPage = (int) Math.ceil(original.size() / (double) max);
            if (page > maxPage - 1) page = Math.max(0, maxPage - 1);
        });
        addRenderableWidget(search);
        Button add = new Button.Builder(AutoFish.getTranslatableComponent("gui.superfilterscreen.openfilter"), button -> ScreenHelper.showScreen(new FilterSelectionScreen(this))).pos(this.width / 2 - 75, 60).size(72, 20).build();
        addRenderableWidget(add);
        Button done = new Button.Builder(AutoFish.getTranslatableComponent("gui.superfilterscreen.done"), button -> ScreenHelper.showScreen(parent)).pos(this.width / 2 + 3, 60).size(72, 20).build();
        addRenderableWidget(done);
        previous = new Button.Builder(AutoFish.getLiteralComponent("<"), button -> { if (page > 0) page--; }).pos(this.width / 2 - 100, 60).size(20, 20).build();
        previous.visible = false;
        addRenderableWidget(previous);
        next = new Button.Builder(AutoFish.getLiteralComponent(">"), button -> { if (page < maxPage - 1) page++; }).pos(this.width / 2 + 80, 60).size(20, 20).build();
        next.visible = false;
        addRenderableWidget(next);
    }

    @Override
    //? if >=26.1 {
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, -1);
    //? } else {
    /*public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, -1);
    *///? }
        Item[] items = searching.toArray(new Item[0]);
        for (int i = page * max; i < Math.min((page + 1) * max, searching.size()); i++) {
            Item item = items[i];
            int h = (i % max) / (max / 3);
            int k = (i % max) % (max / 3);
            ItemStack stack = ItemStack.EMPTY;
            if (item != null) stack = new ItemStack(item);
            //? if >=26.1 {
            if (!stack.isEmpty()) graphics.item(stack, (reducedWidth * h / 3) + 15, (reducedHeight * k / (max / 3)) + 90);
            graphics.text(this.font, stack.getDisplayName().getString(), ((reducedWidth * h / 3) + 45), ((reducedHeight * k / (max / 3)) + 95), 0xFFFFFFFF);
            //? } else {
            /*if (!stack.isEmpty()) graphics.renderItem(stack, (reducedWidth * h / 3) + 15, (reducedHeight * k / (max / 3)) + 90);
            graphics.drawString(this.font, stack.getDisplayName().getString(), ((reducedWidth * h / 3) + 45), ((reducedHeight * k / (max / 3)) + 95), 0xFFFFFFFF);
            *///? }
            //this.font.draw(graphics, stack.getDisplayName().getString(), (float) ((reducedWidth * h / 3) + 45), (float) ((reducedHeight * k / (max / 3)) + 95), Color.WHITE.getRGB());
        }
        //? if >=26.1 {
        search.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        //? } else
        //search.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    //? if >=1.21.11 {
    public boolean keyPressed(KeyEvent ev) {
        if (ev.key() == GLFW.GLFW_KEY_ESCAPE) ScreenHelper.showScreen(parent);
        return super.keyPressed(ev);
    }
    //? } else {
    /*public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) ScreenHelper.showScreen(parent);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    *///? }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
