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

public class CheckIntervalScreen extends Screen {
    private final Screen parent;
    private EditBox checkInterval;

    protected CheckIntervalScreen(Screen parent) {
        super(AutoFish.getTranslatableComponent("gui.setcheckinterval"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        checkInterval = new EditBox(this.font, this.width / 2 - 75, this.height / 2 - 25, 150, 20, AutoFish.getTranslatableComponent("gui.setcheckinterval.checkinterval")) {
            @Override
            public boolean mouseClicked(MouseButtonEvent ev, boolean p_430750_) {
                if (ev.button() == GLFW.GLFW_MOUSE_BUTTON_2) this.setValue("");
                return super.mouseClicked(ev, p_430750_);
            }
        };
        checkInterval.setValue(Long.toString(Config.checkInterval));
        addRenderableWidget(checkInterval);
        Button save = new Button.Builder(AutoFish.getTranslatableComponent("gui.setcheckinterval.save"), button -> {
            if (!isNumeric(checkInterval.getValue())) checkInterval.setValue(Long.toString(Config.checkInterval));
            else {
                long delay = Long.parseLong(checkInterval.getValue());
                if (delay < Config.CHECK_INTERVAL_RANGE[1] || delay > Config.CHECK_INTERVAL_RANGE[2]) checkInterval.setValue(Long.toString(Config.checkInterval));
                else {
                    Config.setCheckInterval(delay);
                    Minecraft.getInstance().setScreenAndShow(parent);
                }
            }
        }).pos(this.width / 2 - 75, this.height / 2).size(150, 20).build();
        addRenderableWidget(save);
    }

    @Override
    public void tick() {
        //checkInterval.tick();
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
        this.checkInterval.extractRenderState(graphics, mouseX, mouseY, partialTicks);
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
