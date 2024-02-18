/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "MemoryFix", description = "MemoryFix", category = ModuleCategory.MISC)
class MemoryFix : Module() {
    override fun onEnable() {
        Runtime.getRuntime().gc()
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        Runtime.getRuntime().gc()
    }
}
