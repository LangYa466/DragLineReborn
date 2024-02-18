/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.TextEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.misc.StringUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils.translateAlternateColorCodes
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.TextValue

@ModuleInfo(name = "NameProtect", description = "Changes playernames clientside.", category = ModuleCategory.MISC)
class NameProtect : Module() {
    private val allPlayersValue = BoolValue("AllPlayers", false)

    private val fakeNameValue = TextValue("Protect-name", "&cLangYa")
    private val allPlayerName = TextValue("AllPlayerName", "&6SB")
    private val endWithColorReset = BoolValue("EndWithColorReset", true)


    @EventTarget
    fun onText(event: TextEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || (event.text!!.contains("§bDragLine")))
            return

        for (friend in LiquidBounce.fileManager.friendsConfig.friends)
            if (friend == null) {
                return
            } else {
                event.text =
                    StringUtils.replace(event.text, friend.playerName, translateAlternateColorCodes(friend.alias))
            }

        if (!state) return
        event.text = StringUtils.replace(
            event.text,
            thePlayer.name,
            translateAlternateColorCodes((fakeNameValue.get() + (if (endWithColorReset.get()) "&f" else "")))
        )

        if (allPlayersValue.get()) {
            for (playerInfo in mc.netHandler.playerInfoMap)
                event.text = StringUtils.replace(
                    event.text,
                    playerInfo.gameProfile.name,
                    translateAlternateColorCodes(allPlayerName.get() + (if (endWithColorReset.get()) "&f" else ""))
                )
        }
    }

}