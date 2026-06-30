package in.northwestw.autofish.config.gui;

import in.northwestw.autofish.AutoFish;
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

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class LongSettingScreen extends Screen {
    private final Screen parent;
    private final String middleTranslationKey;
    private final Supplier<Long> supplier;
    private final Consumer<Long> consumer;
    private final long min, max;
    private EditBox editBox;

    protected LongSettingScreen(Screen parent, String middleTranslationKey, Supplier<Long> supplier, Consumer<Long> consumer, long min, long max) {
        super(AutoFish.getTranslatableComponent("gui." + middleTranslationKey));
        this.parent = parent;
        this.middleTranslationKey = middleTranslationKey;
        this.supplier = supplier;
        this.consumer = consumer;
        this.min = min;
        this.max = max;
    }

    @Override
    protected void init() {
        editBox = new EditBox(this.font, this.width / 2 - 75, this.height / 2 - 25, 150, 20, AutoFish.getTranslatableComponent("gui." + this.middleTranslationKey + ".throwdelay")) {
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
        editBox.setValue(Long.toString(this.supplier.get()));
        Button save = ScreenHelper.makeButton(this.width / 2 - 75, this.height / 2, 150, 20, AutoFish.getTranslatableComponent("gui." + this.middleTranslationKey + ".save"), button -> {
            if (!isNumeric(editBox.getValue())) editBox.setValue(Long.toString(this.supplier.get()));
            else {
                long delay = Long.parseLong(editBox.getValue());
                if (delay < this.min || delay > this.max) editBox.setValue(Long.toString(this.supplier.get()));
                else {
                    this.consumer.accept(delay);
                    ScreenHelper.showScreen(parent);
                }
            }
        });
        //? if <=1.16.5 {
        /*this.children.add(editBox);
        addButton(save);
        *///? } else {
        addRenderableWidget(editBox);
        addRenderableWidget(save);
        //? }
    }

    @Override
    public void tick() {
        //throwDelay.tick();
        super.tick();
    }

    private static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    @Override
    //? if >=26.1 {
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, -1);
        this.editBox.extractRenderState(graphics, mouseX, mouseY, partialTicks);
    }
    //? } elif >=1.20.1 {
    /*public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, -1);
        this.editBox.render(graphics, mouseX, mouseY, partialTicks);
    }
    *///? } else {
    /*public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, -1);
        this.editBox.render(poseStack, mouseX, mouseY, partialTicks);
    }
    *///? }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    //? if >=1.21.11 {
    public boolean keyPressed(KeyEvent ev) {
        if (ev.key() == ScreenHelper.KEY_ESCAPE) ScreenHelper.showScreen(parent);
        return super.keyPressed(ev);
    }
    //? } else {
    /*public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == ScreenHelper.KEY_ESCAPE) ScreenHelper.showScreen(parent);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    *///? }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
