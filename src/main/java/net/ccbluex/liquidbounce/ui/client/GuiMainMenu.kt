package net.ccbluex.liquidbounce.ui.client

import cn.langya.font.FontManager
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiButton
import net.ccbluex.liquidbounce.api.util.WrappedGuiScreen
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.resources.I18n

class GuiMainMenu : WrappedGuiScreen() {
    override fun initGui() {
        val defaultHeight = representedScreen.height / 4 + 30
        val defaultWidth = representedScreen.width / 2 - 60
        val buttonWidth = 120
        val buttonHeight = 20

        representedScreen.buttonList.add(classProvider.createGuiButton(0, defaultWidth, defaultHeight, buttonWidth, buttonHeight, I18n.format("Singleplayer")))
        representedScreen.buttonList.add(classProvider.createGuiButton(1, defaultWidth, defaultHeight + 25, buttonWidth, buttonHeight, I18n.format("Multiplayer")))
        representedScreen.buttonList.add(classProvider.createGuiButton(2, defaultWidth, defaultHeight + 50, buttonWidth, buttonHeight, "AltManager"))
        representedScreen.buttonList.add(classProvider.createGuiButton(3, defaultWidth, defaultHeight + 75, buttonWidth, buttonHeight, I18n.format("Options")))
        representedScreen.buttonList.add(classProvider.createGuiButton(4, defaultWidth, defaultHeight + 100, buttonWidth, buttonHeight, I18n.format("Quit Game")))

        super.initGui()
    }
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        representedScreen.drawBackground(0)

        Fonts.fontBold80.drawString(LiquidBounce.CLIENT_NAME, representedScreen.width / 2f - 50f, representedScreen.height / 4.5f, -1)
        FontManager.T20.drawString("Copyright Mojang AB. Do not distribute!", 5f, representedScreen.height - 15f, -1)

        representedScreen.superDrawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: IGuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(classProvider.createGuiSelectWorld(this.representedScreen))
            1 -> mc.displayGuiScreen(classProvider.createGuiMultiplayer(this.representedScreen))
            2 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiAltManager(this.representedScreen)))
            3 -> mc.displayGuiScreen(classProvider.createGuiOptions(this.representedScreen, mc.gameSettings))
            4 -> mc.shutdown()
        }
    }
}