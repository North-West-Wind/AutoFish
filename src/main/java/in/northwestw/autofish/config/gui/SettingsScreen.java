package in.northwestw.autofish.config.gui;

import in.northwestw.autofish.AutoFish;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public class SettingsScreen extends Screen {
    private static final int WIDTH = 150, HEIGHT = 20, MARGIN = 5;

    public SettingsScreen() {
        super(AutoFish.getTranslatableComponent("gui.autofish"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        Button.Builder[] builders = {
                new Button.Builder(AutoFish.getTranslatableComponent("gui.autofish.recastdelay"), button -> Minecraft.getInstance().setScreenAndShow(new RecastDelayScreen(this))),
                new Button.Builder(AutoFish.getTranslatableComponent("gui.autofish.reelindelay"), button -> Minecraft.getInstance().setScreenAndShow(new ReelInDelayScreen(this))),
                new Button.Builder(AutoFish.getTranslatableComponent("gui.autofish.throwdelay"), button -> Minecraft.getInstance().setScreenAndShow(new ThrowDelayScreen(this))),
                new Button.Builder(AutoFish.getTranslatableComponent("gui.autofish.checkinterval"), button -> Minecraft.getInstance().setScreenAndShow(new CheckIntervalScreen(this))),
                new Button.Builder(AutoFish.getTranslatableComponent("gui.autofish.filter"), button -> Minecraft.getInstance().setScreenAndShow(new SuperFilterScreen(this)))
        };

        for (int ii = 0; ii < builders.length; ii++) {
            Button button = builders[ii].pos(this.width / 2 - WIDTH / 2, this.height / 2 + (ii - builders.length / 2) * (HEIGHT + MARGIN)).size(WIDTH, HEIGHT).build();
            addRenderableWidget(button);
        }

        Button done = new Button.Builder(AutoFish.getTranslatableComponent("gui.autofish.done"), button -> onClose()).pos(this.width / 2 - 75, this.height - 25).size(150, 20).build();
        addRenderableWidget(done);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, -1);
    }
}
