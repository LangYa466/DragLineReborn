/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "AsianHat", description = "Yep. China Hat.", category = ModuleCategory.RENDER)
class AsianHat : Module() {
    private val colorModeValue =
        ListValue("Color", arrayOf("Custom"), "Custom")
    private val colorRedValue = IntegerValue("Red", 255, 0, 255)
    private val colorGreenValue = IntegerValue("Green", 255, 0, 255)
    private val colorBlueValue = IntegerValue("Blue", 255, 0, 255)
    private val colorAlphaValue = IntegerValue("Alpha", 255, 0, 255)
    private val colorEndAlphaValue = IntegerValue("EndAlpha", 255, 0, 255)
    private val saturationValue = FloatValue("Saturation", 1f, 0f, 1f)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val mixerSecondsValue = IntegerValue("Seconds", 2, 1, 10)
    private val spaceValue = IntegerValue("Color-Space", 0, 0, 100)
    private val noFirstPerson = BoolValue("NoFirstPerson", true)
    private val hatBorder = BoolValue("HatBorder", true)

    //private final BoolValue hatRotation = new BoolValue("HatRotation", true);
    private val borderAlphaValue = IntegerValue("BorderAlpha", 255, 0, 255)
    private val borderWidthValue = FloatValue("BorderWidth", 1f, 0.1f, 4f)
    private val positions: MutableList<DoubleArray> = ArrayList()
    private var lastRadius = 0.0
    private fun checkPosition(radius: Double) {
        if (radius != lastRadius) {
            // generate new positions
            positions.clear()
            var i = 0
            while (i <= 360) {
                positions.add(
                    doubleArrayOf(
                        -Math.sin(i * Math.PI / 180) * radius,
                        Math.cos(i * Math.PI / 180) * radius
                    )
                )
                i += 1
            }
        }
        lastRadius = radius
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val entity: EntityLivingBase? = mc2.player
        if (entity == null || noFirstPerson.get() && mc2.gameSettings.thirdPersonView == 0) return
        val bb = entity.entityBoundingBox
        val radius = bb.maxX - bb.minX
        val height = bb.maxY - bb.minY
        val posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc2.timer.renderPartialTicks
        val posY =
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc2.timer.renderPartialTicks
        val posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc2.timer.renderPartialTicks
        val colour = getColor(0)
        val r = colour.red / 255.0f
        val g = colour.green / 255.0f
        val b = colour.blue / 255.0f
        val al = colorAlphaValue.get() / 255.0f
        val Eal = colorEndAlphaValue.get() / 255.0f
        val viewX = -mc.renderManager.viewerPosX
        val viewY = -mc.renderManager.viewerPosY
        val viewZ = -mc.renderManager.viewerPosZ
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.buffer
        checkPosition(radius)
        pre3D()
        worldrenderer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_COLOR)

        // main section
        worldrenderer.pos(viewX + posX, viewY + posY + height + 0.3, viewZ + posZ).color(r, g, b, al).endVertex()
        var i = 0
        for (smolPos in positions) {
            val posX2 = posX + smolPos[0]
            val posZ2 = posZ + smolPos[1]
            if (spaceValue.get() > 0 && !colorModeValue.get().equals("Custom", ignoreCase = true)) {
                val colour2 = getColor(i * spaceValue.get())
                val r2 = colour2.red / 255.0f
                val g2 = colour2.green / 255.0f
                val b2 = colour2.blue / 255.0f
                worldrenderer.pos(viewX + posX2, viewY + posY + height, viewZ + posZ2).color(r2, g2, b2, Eal)
                    .endVertex()
            } else {
                worldrenderer.pos(viewX + posX2, viewY + posY + height, viewZ + posZ2).color(r, g, b, Eal).endVertex()
            }
            i++
        }
        worldrenderer.pos(viewX + posX, viewY + posY + height + 0.3, viewZ + posZ).color(r, g, b, al).endVertex()
        tessellator.draw()

        // border section
        if (hatBorder.get()) {
            val lineAlp = borderAlphaValue.get() / 255.0f
            GL11.glLineWidth(borderWidthValue.get())
            worldrenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR)
            i = 0
            for (smolPos in positions) {
                val posX2 = posX + smolPos[0]
                val posZ2 = posZ + smolPos[1]
                if (spaceValue.get() > 0 && !colorModeValue.get().equals("Custom", ignoreCase = true)) {
                    val colour2 = getColor(i * spaceValue.get())
                    val r2 = colour2.red / 255.0f
                    val g2 = colour2.green / 255.0f
                    val b2 = colour2.blue / 255.0f
                    worldrenderer.pos(viewX + posX2, viewY + posY + height, viewZ + posZ2).color(r2, g2, b2, lineAlp)
                        .endVertex()
                } else {
                    worldrenderer.pos(viewX + posX2, viewY + posY + height, viewZ + posZ2).color(r, g, b, lineAlp)
                        .endVertex()
                }
                i++
            }
            tessellator.draw()
        }
        post3D()
    }

    fun getColor(index: Int): Color {
        return Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get())
    }

    companion object {
        fun pre3D() {
            GL11.glPushMatrix()
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glShadeModel(GL11.GL_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LIGHTING)
            GL11.glDepthMask(false)
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
            GL11.glDisable(2884)
        }

        fun post3D() {
            GL11.glDepthMask(true)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glPopMatrix()
            GL11.glColor4f(1f, 1f, 1f, 1f)
        }
    }
}