package in.northwestw.autofish.config.gui;

import in.northwestw.autofish.AutoFish;
import in.northwestw.autofish.config.Config;
import in.northwestw.autofish.handler.AutoFishHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;

import java.util.regex.Pattern;

public class ThrowDelayScreen extends Screen {
    private final Screen parent;
    private EditBox throwDelay;

    protected ThrowDelayScreen(Screen parent) {
        super(AutoFish.getTranslatableComponent("gui.setthrowdelay"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        throwDelay = new EditBox(this.font, this.width / 2 - 75, this.height / 2 - 25, 150, 20, AutoFish.getTranslatableComponent("gui.setthrowdelay.throwdelay")) {
            @Override
            public boolean mouseClicked(MouseButtonEvent ev, boolean flag) {
                if (ev.button() == GLFW.GLFW_MOUSE_BUTTON_2) this.setValue("");
                return super.mouseClicked(ev, flag);
            }
        };
        throwDelay.setValue(Long.toString(Config.throwDelay));
        addRenderableWidget(throwDelay);
        Button save = new Button.Builder(AutoFish.getTranslatableComponent("gui.setthrowdelay.save"), button -> {
            if (!isNumeric(throwDelay.getValue())) throwDelay.setValue(Long.toString(Config.throwDelay));
            else {
                long delay = Long.parseLong(throwDelay.getValue());
                if (delay < Config.THROW_DELAY_RANGE[1] || delay > Config.THROW_DELAY_RANGE[2]) throwDelay.setValue(Long.toString(Config.throwDelay));
                else {
                    Config.setThrowDelay(delay);
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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, -1);
        this.throwDelay.extractRenderState(graphics, mouseX, mouseY, partialTicks);
    }

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
