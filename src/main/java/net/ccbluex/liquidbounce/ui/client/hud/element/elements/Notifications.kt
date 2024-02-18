/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/liquidbounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IFontRenderer
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.misc.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max


/**
 * CustomHUD Notification element
 */

@ElementInfo(name = "Notifications")
class Notifications(
    x: Double = 0.0,
    y: Double = 0.0,
    scale: Float = 1F,
    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)
) : Element(x, y, scale, side) {


    private val titleShadow = BoolValue("TitleShadow", false)
    private val contentShadow = BoolValue("ContentShadow", true)
    private val whiteText = BoolValue("WhiteTextColor", true)

    companion object {
        val radius = IntegerValue("Radius", 2, 0, 10)
    }

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Notification", "This is an example notification.", NotifyType.INFO)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        // bypass java.util.ConcurrentModificationException
        LiquidBounce.hud.notifications.map { it }.forEachIndexed { index, notify ->
            GL11.glPushMatrix()

            if (notify.drawNotification(
                    index,
                    Fonts.font35,
                    contentShadow.get(),
                    titleShadow.get(),
                    whiteText.get(),
                    radius.get()
                )
            ) {
                LiquidBounce.hud.notifications.remove(notify)
            }

            GL11.glPopMatrix()
        }

        if (LiquidBounce.wrapper.classProvider.isGuiHudDesigner(mc.currentScreen)) {
            if (!LiquidBounce.hud.notifications.contains(exampleNotification)) {
                LiquidBounce.hud.addNotification(exampleNotification)
            }

            exampleNotification.fadeState = FadeState.STAY
            exampleNotification.displayTime = System.currentTimeMillis()

            return Border(-exampleNotification.width.toFloat(), -exampleNotification.height.toFloat(), 0F, 0F)
        }

        return null
    }
}


class Notification(
    val title: String,
    private val content: String,
    val type: NotifyType,
    val time: Int = 1500,
    private val animeTime: Int = 500
) {
    var width = 100
    val height = 30

    var x = 0F

    var fadeState = FadeState.IN
    private var nowY = -height
    var displayTime = System.currentTimeMillis()
    private var animeXTime = System.currentTimeMillis()
    private var animeYTime = System.currentTimeMillis()

    /**
     * Draw notification
     */
    fun drawNotification(
        index: Int, font: IFontRenderer,
        contentShadow: Boolean,
        titleShadow: Boolean,
        whiteText: Boolean,
        radius: Int
    ): Boolean {
        this.width = 100.coerceAtLeast(
            font.getStringWidth(content)
                .coerceAtLeast(font.getStringWidth(title)) + 15
        )
        val realY = -(index + 1) * height
        val nowTime = System.currentTimeMillis()
        var transY = nowY.toDouble()
        font.getStringWidth("$title: $content")
        val s: String = when (type) {
            NotifyType.SUCCESS -> "SUCCESS"
            NotifyType.ERROR -> "ERROR"
            NotifyType.WARNING -> "WARNING"
            NotifyType.INFO -> "INFO"
        }

        val textColor: Int = if (whiteText) {
            Color(255, 255, 255).rgb
        } else {
            Color(10, 10, 10).rgb
        }

        // Y-Axis Animation
        if (nowY != realY) {
            var pct = (nowTime - animeYTime) / animeTime.toDouble()
            if (pct > 1) {
                nowY = realY
                pct = 1.0
            } else {
                pct = EaseUtils.easeOutExpo(pct)
            }
            transY += (realY - nowY) * pct
        } else {
            animeYTime = nowTime
        }

        // X-Axis Animation
        var pct = (nowTime - animeXTime) / animeTime.toDouble()
        when (fadeState) {
            FadeState.IN -> {
                if (pct > 1) {
                    fadeState = FadeState.STAY
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = EaseUtils.easeOutExpo(pct)
            }

            FadeState.STAY -> {
                pct = 1.0
                if ((nowTime - animeXTime) > time) {
                    fadeState = FadeState.OUT
                    animeXTime = nowTime
                }
            }

            FadeState.OUT -> {
                if (pct > 1) {
                    fadeState = FadeState.END
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = 1 - EaseUtils.easeInExpo(pct)
            }

            FadeState.END -> {
                return true
            }
        }
        val transX = width - (width * pct) - width
        GL11.glTranslated(transX, transY, 0.0)

        var colorRed = 0
        var colorGreen = 0
        var colorBlue = 0

        if (s == "SUCCESS") {
            //success
            colorRed = 36
            colorGreen = 211
            colorBlue = 99
        }

        if (s == "ERROR") {
            //error
            colorRed = 248
            colorGreen = 72
            colorBlue = 72
        }

        if (s == "WARNING") {

            //warning
            colorRed = 251
            colorGreen = 189
            colorBlue = 23
        }

        //info
        if (s == "INFO") {
            colorRed = 242
            colorGreen = 242
            colorBlue = 242
        }


        RenderUtils.drawRoundedCornerRect(3f, 0F, width.toFloat() + 5f, 22f, radius.toFloat(), Color(0, 0, 0, 120).rgb)
        RenderUtils.drawRoundedCornerRect(
            3f,
            0F,
            max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)) + 5f, 0F),
            22f,
            radius.toFloat(),
            Color(colorRed, colorGreen, colorBlue, 150).rgb
        )
        Fonts.font35.drawString(title, 6F, 3F, textColor, titleShadow)
        font.drawString(content, 6F, 12F, textColor, contentShadow)

        return false
    }
}



