package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.LiquidBounce.CLIENT_NAME
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.network.play.server.SPacketChat
import java.util.regex.Pattern

@ModuleInfo(name = "BanChecker", description = "Hyt", category = ModuleCategory.MISC)
class BanChecker : Module() {

    private val waterMarkValue = BoolValue("WaterMark", false)
    private val atall = BoolValue("HYTAtAll", true)
    private val showtotalban = BoolValue("showtotalban", true)
    private val message = TextValue("BanMsg", "%name% 死号了")
    private val banChatMsg = TextValue("BanChat", "已超越%ban%人")
    private var text = ""
    var ban = 0

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        if (packet is SPacketChat) {

            val chat = packet.chatComponent.unformattedText
            if ((!chat.contains(":") || !chat.contains("]") || !chat.contains(">")) && chat.contains("在本局游戏中行为异常")) {
                val matcher = Pattern.compile("玩家(.*?)在本局游戏中行为异常").matcher(chat)
                if (matcher.find()) {
                    ban++
                    if (this.state) {
                        val banname = matcher.group(1).trim()
                        if (banname != mc.thePlayer!!.displayNameString) {
                            LiquidBounce.hud.addNotification(
                                Notification(
                                this.name,
                                "$banname was banned(TotalBan:${ban})",
                                NotifyType.WARNING
                            )
                            )
                            chatBan(banname)
                        }
                    }
                }
            }
        }
    }

    private fun chatBan(name: String) {
        text = message.get()
        text = text.replace("%name%", name).replace("%ban%", ban.toString())

        if (waterMarkValue.get()) text = ("[$CLIENT_NAME] $text")
        if (showtotalban.get()) text = ("$text | " + banChatMsg.get())
        if (atall.get()) text = ("@a$text")
        mc.thePlayer!!.sendChatMessage(text)
    }

    override fun handleEvents() = true
}
