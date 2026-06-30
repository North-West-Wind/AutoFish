package in.northwestw.autofish.config.gui;

import in.northwestw.autofish.AutoFish;
import in.northwestw.autofish.config.Config;
import net.minecraft.client.Minecraft;
//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
 //?} else
//import net.minecraft.client.gui.GuiGraphics;
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
                new Button.Builder(AutoFish.getTranslatableComponent("gui.autofish.recastdelay"), button ->
                        ScreenHelper.showScreen(new LongSettingScreen(this, "setrecastdelay", () -> Config.recastDelay, (newDelay) -> Config.recastDelay = newDelay, Config.RECAST_DELAY_RANGE[0], Config.RECAST_DELAY_RANGE[1]))),
                new Button.Builder(AutoFish.getTranslatableComponent("gui.autofish.reelindelay"), button ->
                        ScreenHelper.showScreen(new LongSettingScreen(this, "setreelindelay", () -> Config.reelInDelay, (newDelay) -> Config.reelInDelay = newDelay, Config.REEL_IN_DELAY_RANGE[0], Config.REEL_IN_DELAY_RANGE[1]))),
                new Button.Builder(AutoFish.getTranslatableComponent("gui.autofish.throwdelay"), button ->
                        ScreenHelper.showScreen(new LongSettingScreen(this, "setthrowdelay", () -> Config.throwDelay, (newDelay) -> Config.throwDelay = newDelay, Config.THROW_DELAY_RANGE[0], Config.THROW_DELAY_RANGE[1]))),
                new Button.Builder(AutoFish.getTranslatableComponent("gui.autofish.checkinterval"), button ->
                        ScreenHelper.showScreen(new LongSettingScreen(this, "setcheckinterval", () -> Config.checkInterval, (newInterval) -> Config.checkInterval = newInterval, Config.CHECK_INTERVAL_RANGE[0], Config.CHECK_INTERVAL_RANGE[1]))),
                new Button.Builder(AutoFish.getTranslatableComponent("gui.autofish.filter"), button ->
                        ScreenHelper.showScreen(new SuperFilterScreen(this)))
        };

        for (int ii = 0; ii < builders.length; ii++) {
            Button button = builders[ii].pos(this.width / 2 - WIDTH / 2, this.height / 2 + (ii - builders.length / 2) * (HEIGHT + MARGIN)).size(WIDTH, HEIGHT).build();
            addRenderableWidget(button);
        }

        Button done = new Button.Builder(AutoFish.getTranslatableComponent("gui.autofish.done"), button -> onClose()).pos(this.width / 2 - 75, this.height - 25).size(150, 20).build();
        addRenderableWidget(done);
    }

    @Override
            //? if >=26.1 {
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, -1);
    }//?} else {
    /*public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, -1);
    }*///?}
}
