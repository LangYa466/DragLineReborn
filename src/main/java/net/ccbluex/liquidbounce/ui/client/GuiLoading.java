package net.ccbluex.liquidbounce.ui.client;

import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author LangYa
 * @date 2024/2/8 下午 11:32
 */

public class GuiLoading extends GuiScreen {
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        RenderUtils.drawLoadingCircle((float) scaledResolution.getScaledWidth() / 2, (float) scaledResolution.getScaledHeight() / 4 + 70);
        mc.fontRenderer.drawStringWithShadow("Loading...", (float) scaledResolution.getScaledWidth() / 2, (float) scaledResolution.getScaledHeight() / 4 + 110, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
