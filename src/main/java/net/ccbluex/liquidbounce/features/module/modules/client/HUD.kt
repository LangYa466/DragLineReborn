/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.client

import cn.langya.font.FontManager
import cn.langya.util.render.ColorUtil
import cn.langya.util.render.GaussianBlur
import cn.langya.util.render.GradientUtil
import cn.langya.util.render.RoundedUtil
import cn.langya.util.render.ShadowUtil
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.text.TextFormatting
import java.awt.Color

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.CLIENT, array = false)
class HUD : Module() {
    val inventoryParticle = BoolValue("InventoryParticle", false)
    private val blurValue = BoolValue("Blur", false)
    val fontChatValue = BoolValue("FontChat", false)
    private val tenacityLogo = BoolValue("TenacityLogo", false)
    private val logo = ListValue("Logo", arrayOf("LangYa","Sun","Sun2","Tenacity","None"),"Sun")
    private val thud = ListValue("TargetHud", arrayOf("Sun","LangYa","Style","None"), "LangYa")
    private val str = (TextFormatting.DARK_GRAY.toString() + " | " + TextFormatting.WHITE + mc2.player.name + TextFormatting.DARK_GRAY) + " | " + TextFormatting.WHITE + Minecraft.getDebugFPS() + "fps" + TextFormatting.DARK_GRAY + " | " + TextFormatting.WHITE + if (mc2.isSingleplayer) "SinglePlayer" else mc2.currentServerData!!.serverIP
    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if (classProvider.isGuiHudDesigner(mc2.currentScreen))
            return

        LiquidBounce.hud.render(false)

        
        if(tenacityLogo.get()) {
            val xVal = 6f
            val yVal = 6f
            val width = FontManager.T50.getStringWidth(LiquidBounce.CLIENT_NAME).toFloat()


            RenderUtils.resetColor()
            GradientUtil.applyGradientHorizontal(
                xVal,
                yVal,
                width,
                20f,
                1f,
                Color(236, 133, 209),
                Color(28, 167, 222)
            ) {
                RenderUtils.setAlphaLimit(0f)
                FontManager.T50.drawString(LiquidBounce.CLIENT_NAME, xVal, yVal, 0)
            }
        }



        // logo
        if (logo.get() == "Sun") {
            val str =
                (TextFormatting.DARK_GRAY.toString() + " | " + TextFormatting.WHITE + mc2.player.name + TextFormatting.DARK_GRAY) + " | " + TextFormatting.WHITE + Minecraft.getDebugFPS() + "fps" + TextFormatting.DARK_GRAY + " | " + TextFormatting.WHITE + if (mc2.isSingleplayer) "SinglePlayer" else mc2.currentServerData!!.serverIP
            RenderUtils.drawRect(
                6.0f, 6.0f,
                (FontManager.S20.getStringWidth(str) + 18).toFloat(), 19.0f, Color(19, 19, 19, 230).rgb
            )
            RenderUtils.drawRect(
                6.0f,
                6.0f,
                (FontManager.S20.getStringWidth(str) + 18).toFloat(),
                1.0f,
                ColorUtil.color(8).rgb
            )
            RenderUtils.resetColor()
            FontManager.S20.drawString(
                str,
                11 + FontManager.S20.getStringWidth(LiquidBounce.CLIENT_NAME.toUpperCase()),
                7.5f.toInt(),
                Color.WHITE.rgb
            )
            FontManager.S20.drawString(LiquidBounce.CLIENT_NAME.toUpperCase(), 10.0f.toInt(), 7.5f.toInt(), Color.WHITE.rgb)
        }

        if (logo.get() == "Sun2") {
            RenderUtils.drawBorderedRect(
                6.0f,
                6.0f,
                (FontManager.S20.getStringWidth(str) + 25).toFloat(),
                19.0f,
                3f,
                Color(0, 0, 0, 120).rgb,
                Color(0, 0, 0, 120).rgb
            )
            RenderUtils.resetColor()
            FontManager.S20.drawString(
                str,
                11 + FontManager.S20.getStringWidth(LiquidBounce.CLIENT_NAME.toUpperCase()),
                7.5f.toInt(),
                Color.WHITE.rgb
            )
            FontManager.S20.drawString(LiquidBounce.CLIENT_NAME.toUpperCase(), 10.0f.toInt(), 7.5f.toInt(), Color.WHITE.rgb)
        }

        if (logo.get() == "LangYa") {
            GaussianBlur.startBlur()
            RoundedUtil.drawRound(
                6.0f, 6.0f,
                (FontManager.S20.getStringWidth(str) + 13).toFloat(), 13.0f, 0f, Color(0, 0, 0, 130)
            )
            GaussianBlur.endBlur(13f, 2f)
            RoundedUtil.drawRound(
                6.0f, 6.0f,
                (FontManager.S20.getStringWidth(str) + 13).toFloat(), 13.0f, 0f, Color(0, 0, 0, 130)
            )
            RenderUtils.resetColor()
            ShadowUtil.drawShadow(6.0f, 6.0f, (FontManager.S20.getStringWidth(str) + 13).toFloat(), 13.0f)
            FontManager.S20.drawString(
                str,
                11 + FontManager.S20.getStringWidth(LiquidBounce.CLIENT_NAME.toUpperCase()),
                7.5f.toInt(),
                Color.WHITE.rgb
            )
            FontManager.S20.drawString(LiquidBounce.CLIENT_NAME.toUpperCase(), 10.0f.toInt(), 7.5f.toInt(), Color.WHITE.rgb)
        }

        if (mc2.player != null && mc2.world != null) {
            if (thud.get() == "Style") {
                GlStateManager.pushMatrix()
                GlStateManager.translate(10f, 15f, 0.0f)

                // draw rect
                RoundedUtil.drawRound(
                    10f,
                    10f,
                    90f + mc2.fontRenderer.getStringWidth(mc2.player.name),
                    60f,
                    3f,
                    Color(0, 0, 0, 80)
                )
                RoundedUtil.drawRound(
                    10f,
                    10f,
                    90f + mc2.fontRenderer.getStringWidth(mc2.player.name),
                    10f,
                    3f,
                    Color(0, 0, 0, 130)
                )

                // draw string
                FontManager.S20.drawCenteredString("Session", 45.0, 10.0, -1)
                FontManager.S15.drawCenteredString("Played for 52m 52s", 82.0, 25.0, Color.GRAY.rgb)
                FontManager.S25.drawCenteredString(mc2.player.name, 90.0, 38.0, Color.GRAY.rgb)
                FontManager.S20.drawCenteredString("0 Kills, 0 Game won.", 93.0, 55.0, Color.GRAY.rgb)

                // draw head
                RenderUtils.drawBigHead(13.5f, 38f, 28.0f, 28.0f, mc2.player)
                GlStateManager.resetColor()
                GlStateManager.enableAlpha()
                GlStateManager.disableBlend()
                GlStateManager.popMatrix()
            }

            if (thud.get() == "Sun") {
                GlStateManager.pushMatrix()
                GlStateManager.translate(10f, 15f, 0.0f)

                // draw rect
                RoundedUtil.drawRound(
                    10f,
                    10f,
                    60f + mc2.fontRenderer.getStringWidth(mc2.player.name),
                    28f,
                    5f,
                    Color(0, 0, 0, 80)
                )
                RenderUtils.resetColor()
                RoundedUtil.drawRound(40f, 30f, mc2.player.health * 3f, 3f, 1f, Color(61, 131, 173))
                RenderUtils.resetColor()

                // draw head
                RenderUtils.drawBigHead(13.5f, 12.5f, 23.0f, 23.0f, mc2.player)

                // draw string
                FontManager.T18.drawString(mc2.player.name, 47f, 16.0f, -1)
                GlStateManager.resetColor()
                GlStateManager.enableAlpha()
                GlStateManager.disableBlend()
                GlStateManager.popMatrix()
            }
        }


    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        LiquidBounce.hud.update()
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        LiquidBounce.hud.handleKey('a', event.key)
    }

    @EventTarget(ignoreCondition = true)
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (state && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.guiScreen != null &&
                !(classProvider.isGuiChat(event.guiScreen) || classProvider.isGuiHudDesigner(event.guiScreen))) mc.entityRenderer.loadShader(classProvider.createResourceLocation("liquidbounce" + "/blur.json")) else if (mc.entityRenderer.shaderGroup != null &&
                mc.entityRenderer.shaderGroup!!.shaderGroupName.contains("liquidbounce/blur.json")) mc.entityRenderer.stopUseShader()
    }



    init {
        state = true
    }
}