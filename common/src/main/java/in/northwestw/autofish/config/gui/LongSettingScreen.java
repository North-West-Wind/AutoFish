package in.northwestw.autofish.config.gui;

import in.northwestw.autofish.AutoFish;
import net.minecraft.client.Minecraft;
//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
 //?} else
//import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;

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
            public boolean mouseClicked(MouseButtonEvent ev, boolean flag) {
                if (ev.button() == GLFW.GLFW_MOUSE_BUTTON_2) this.setValue("");
                return super.mouseClicked(ev, flag);
            }
        };
        editBox.setValue(Long.toString(this.supplier.get()));
        addRenderableWidget(editBox);
        Button save = new Button.Builder(AutoFish.getTranslatableComponent("gui." + this.middleTranslationKey + ".save"), button -> {
            if (!isNumeric(editBox.getValue())) editBox.setValue(Long.toString(this.supplier.get()));
            else {
                long delay = Long.parseLong(editBox.getValue());
                if (delay < this.min || delay > this.max) editBox.setValue(Long.toString(this.supplier.get()));
                else {
                    this.consumer.accept(delay);
                    Minecraft.getInstance().setScreenAndShow(parent);
                }
            }
        }).pos(this.width / 2 - 75, this.height / 2).size(150, 20).build();
        addRenderableWidget(save);
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
    }//?} else {
    /*public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, -1);
        this.editBox.render(graphics, mouseX, mouseY, partialTicks);
    }*///?}

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent ev) {
        if (ev.key() == GLFW.GLFW_KEY_ESCAPE) Minecraft.getInstance().setScreenAndShow(parent);
        return super.keyPressed(ev);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
