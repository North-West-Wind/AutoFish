package in.northwestw.autofish.config.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import in.northwestw.autofish.AutoFish;
import in.northwestw.autofish.config.Config;
//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
 //?} elif >=1.20.1 {
//import net.minecraft.client.gui.GuiGraphics;
//? } else
//import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

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
        List<Pair<Component, Button.OnPress>> pairs = ImmutableList.of(
                Pair.of(AutoFish.getTranslatableComponent("gui.autofish.recastdelay"), button ->
                        ScreenHelper.showScreen(new LongSettingScreen(this, "setrecastdelay", () -> Config.recastDelay, (newDelay) -> Config.recastDelay = newDelay, Config.RECAST_DELAY_RANGE[0], Config.RECAST_DELAY_RANGE[1]))),
                Pair.of(AutoFish.getTranslatableComponent("gui.autofish.reelindelay"), button ->
                        ScreenHelper.showScreen(new LongSettingScreen(this, "setreelindelay", () -> Config.reelInDelay, (newDelay) -> Config.reelInDelay = newDelay, Config.REEL_IN_DELAY_RANGE[0], Config.REEL_IN_DELAY_RANGE[1]))),
                Pair.of(AutoFish.getTranslatableComponent("gui.autofish.throwdelay"), button ->
                        ScreenHelper.showScreen(new LongSettingScreen(this, "setthrowdelay", () -> Config.throwDelay, (newDelay) -> Config.throwDelay = newDelay, Config.THROW_DELAY_RANGE[0], Config.THROW_DELAY_RANGE[1]))),
                Pair.of(AutoFish.getTranslatableComponent("gui.autofish.checkinterval"), button ->
                        ScreenHelper.showScreen(new LongSettingScreen(this, "setcheckinterval", () -> Config.checkInterval, (newInterval) -> Config.checkInterval = newInterval, Config.CHECK_INTERVAL_RANGE[0], Config.CHECK_INTERVAL_RANGE[1]))),
                Pair.of(AutoFish.getTranslatableComponent("gui.autofish.filter"), button ->
                        ScreenHelper.showScreen(new SuperFilterScreen(this)))
        );

        for (int ii = 0; ii < pairs.size(); ii++) {
            Pair<Component, Button.OnPress> pair = pairs.get(ii);
            Button button = ScreenHelper.makeButton(this.width / 2 - WIDTH / 2, this.height / 2 + (ii - pairs.size() / 2) * (HEIGHT + MARGIN), WIDTH, HEIGHT, pair.getLeft(), pair.getRight());
            //? if <=1.16.5 {
            /*addButton(button);
            *///? } else
            addRenderableWidget(button);
        }

        Button done = ScreenHelper.makeButton(this.width / 2 - 75, this.height - 25, 150, 20, AutoFish.getTranslatableComponent("gui.autofish.done"), button -> onClose());
        //? if <=1.16.5 {
        /*addButton(done);
        *///? } else
        addRenderableWidget(done);
    }

    @Override
    //? if >=26.1 {
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, -1);
    }//?} elif >=1.20.1 {
    /*public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, -1);
    }*///?} else {
    /*public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, -1);
    }*///?}
}
