package ml.northwestwind.forgeautofish.config.gui;

import ml.northwestwind.forgeautofish.AutoFish;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public class SettingsScreen extends Screen {
    private static final int WIDTH = 150, HEIGHT = 20, MARGIN = 5;

    public SettingsScreen() {
        super(AutoFish.getTranslatableComponent("gui.forgeautofish"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        Button.Builder[] builders = {
                new Button.Builder(AutoFish.getTranslatableComponent("gui.forgeautofish.recastdelay"), button -> Minecraft.getInstance().setScreen(new RecastDelayScreen(this))),
                new Button.Builder(AutoFish.getTranslatableComponent("gui.forgeautofish.reelindelay"), button -> Minecraft.getInstance().setScreen(new ReelInDelayScreen(this))),
                new Button.Builder(AutoFish.getTranslatableComponent("gui.forgeautofish.throwdelay"), button -> Minecraft.getInstance().setScreen(new ThrowDelayScreen(this))),
                new Button.Builder(AutoFish.getTranslatableComponent("gui.forgeautofish.checkinterval"), button -> Minecraft.getInstance().setScreen(new CheckIntervalScreen(this))),
                new Button.Builder(AutoFish.getTranslatableComponent("gui.forgeautofish.filter"), button -> Minecraft.getInstance().setScreen(new SuperFilterScreen(this)))
        };

        for (int ii = 0; ii < builders.length; ii++) {
            Button button = builders[ii].pos(this.width / 2 - WIDTH / 2, this.height / 2 + (ii - builders.length / 2) * (HEIGHT + MARGIN)).size(WIDTH, HEIGHT).build();
            addRenderableWidget(button);
        }

        Button done = new Button.Builder(AutoFish.getTranslatableComponent("gui.forgeautofish.done"), button -> onClose()).pos(this.width / 2 - 75, this.height - 25).size(150, 20).build();
        addRenderableWidget(done);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, -1);
    }
}
