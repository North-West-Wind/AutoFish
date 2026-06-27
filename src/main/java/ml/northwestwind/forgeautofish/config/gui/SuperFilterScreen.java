package ml.northwestwind.forgeautofish.config.gui;

import ml.northwestwind.forgeautofish.AutoFish;
import ml.northwestwind.forgeautofish.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.IReverseTag;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
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
        original = Config.FILTER.get().stream().map(string -> ForgeRegistries.ITEMS.getValue(Identifier.parse(string))).collect(Collectors.toList());
        maxPage = (int) Math.ceil(original.size() / (double) max);
        searching = original;
        search = new EditBox(this.font, this.width / 2 - 75, 35, 150, 20, AutoFish.getTranslatableComponent("gui.superfilterscreen.search")) {
            @Override
            public boolean mouseClicked(MouseButtonEvent ev, boolean flag) {
                if (ev.button() == GLFW.GLFW_MOUSE_BUTTON_2) this.setValue("");
                return super.mouseClicked(ev, flag);
            }
        };
        search.setResponder(s -> {
            String[] args = s.split("/ +/");
            String[] mods = Arrays.stream(args).filter(s1 -> s1.startsWith("@")).toArray(String[]::new);
            String[] tags = Arrays.stream(args).filter(s1 -> s1.startsWith("#")).toArray(String[]::new);
            String[] finalArgs = Arrays.stream(args).filter(s1 -> !s1.startsWith("@") && !s1.startsWith("#")).toArray(String[]::new);;
            searching = original.stream().filter(item -> {
                Identifier rl = ForgeRegistries.ITEMS.getKey(item);
                boolean matchmod = mods.length < 1, matchtag = tags.length < 1, matcharg = finalArgs.length < 1;
                for (String mod : mods) {
                    mod = mod.toLowerCase().substring(1);
                    if (rl != null) matchmod = rl.getNamespace().toLowerCase().contains(mod);
                }
                Optional<IReverseTag<Item>> reverseTagsOptional = ForgeRegistries.ITEMS.tags().getReverseTag(item);
                if (reverseTagsOptional.isPresent())
                    for (String tag : tags) {
                        String finalTag = tag.toLowerCase().substring(1);
                        matchtag = reverseTagsOptional.get().getTagKeys().anyMatch(tagKey -> tagKey.location().getPath().contains(finalTag));
                    }
                for (String arg : finalArgs) {
                    arg = arg.toLowerCase();
                    if (rl != null) matcharg = rl.getPath().contains(arg);
                }
                return matchmod && matchtag && matcharg;
            }).collect(Collectors.toList());
            maxPage = (int) Math.ceil(original.size() / (double) max);
            if (page > maxPage - 1) page = maxPage - 1;
        });
        addRenderableWidget(search);
        Button add = new Button.Builder(AutoFish.getTranslatableComponent("gui.superfilterscreen.openfilter"), button -> Minecraft.getInstance().setScreen(new FilterSelectionScreen(this))).pos(this.width / 2 - 75, 60).size(72, 20).build();
        addRenderableWidget(add);
        Button done = new Button.Builder(AutoFish.getTranslatableComponent("gui.superfilterscreen.done"), button -> Minecraft.getInstance().setScreen(parent)).pos(this.width / 2 + 3, 60).size(72, 20).build();
        addRenderableWidget(done);
        previous = new Button.Builder(AutoFish.getLiteralComponent("<"), button -> { if (page > 0) page--; }).pos(this.width / 2 - 100, 60).size(20, 20).build();
        previous.visible = false;
        addRenderableWidget(previous);
        next = new Button.Builder(AutoFish.getLiteralComponent(">"), button -> { if (page < maxPage - 1) page++; }).pos(this.width / 2 + 80, 60).size(20, 20).build();
        next.visible = false;
        addRenderableWidget(next);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, -1);
        Item[] items = searching.toArray(new Item[0]);
        for (int i = page * max; i < Math.min((page + 1) * max, searching.size()); i++) {
            Item item = items[i];
            int h = (i % max) / (max / 3);
            int k = (i % max) % (max / 3);
            ItemStack stack = ItemStack.EMPTY;
            if (item != null) stack = new ItemStack(item);
            if (!stack.isEmpty()) graphics.item(stack, (reducedWidth * h / 3) + 15, (reducedHeight * k / (max / 3)) + 90);
            graphics.text(this.font, stack.getDisplayName().getString(), ((reducedWidth * h / 3) + 45), ((reducedHeight * k / (max / 3)) + 95), Color.WHITE.getRGB());
            //this.font.draw(graphics, stack.getDisplayName().getString(), (float) ((reducedWidth * h / 3) + 45), (float) ((reducedHeight * k / (max / 3)) + 95), Color.WHITE.getRGB());
        }
        search.extractRenderState(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent ev) {
        if (ev.key() == GLFW.GLFW_KEY_ESCAPE) Minecraft.getInstance().setScreen(parent);
        return super.keyPressed(ev);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
