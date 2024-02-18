package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.network.play.client.CPacketAnimation

@ModuleInfo(name = "AuraRangeHelper", description = "colorbyte", category = ModuleCategory.COMBAT)
class AuraRangeHelper : Module() {
    private var enablegroundrange = BoolValue("EnableGroundRange", true)
    private var enablegroundblockrange = BoolValue("EnableGroundBlockRange", true)
    private var enableairrange = BoolValue("EnableAirRange", true)
    private var enableairblockrange = BoolValue("EnableAirBlockRange", true)
    private var groundrange = FloatValue("GroundRange", 3.10F, 1.00F, 4.00F)// .displayable { enablegroundrange.get() }
    private var groundblockrange =
        FloatValue("GroundBlockRange", 3.20F, 1.00F, 4.00F)// .displayable { enablegroundblockrange.get() }
    private var airrange = FloatValue("AirRange", 2.96F, 1.00F, 4.00F)// .displayable { enableairrange.get() }
    private var airblockrange =
        FloatValue("AirBlockRange", 3.20F, 1.00F, 4.00F)// .displayable { enableairblockrange.get() }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
         val aura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura

        if (!aura.state) return
        if (mc.thePlayer!!.onGround) {
            if (enablegroundrange.get()) aura.rangeValue.set(groundrange.get())
            if (enablegroundblockrange.get()) aura.blockRangeValue.set(groundblockrange.get())
        } else {
            if (enableairrange.get()) aura.rangeValue.set(airrange.get())
            if (enableairblockrange.get()) aura.blockRangeValue.set(airblockrange.get())
        }

    }

    @EventTarget
    fun onPacket(event : PacketEvent)
    {
         val aura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura

        if(event.packet.unwrap() is CPacketAnimation && !aura.blockingStatus && aura.currentTarget!!.hurtTime > 0 && aura.currentTarget!!.motionY > 0 && !aura.currentTarget!!.isInWater && !aura.currentTarget!!.sneaking) {
            aura.currentTarget = null
            aura.blockingStatus = true
            ClientUtils.displayChatMessage("$name -> AntiFakeAttack")
        }

        if(aura.currentTarget != null) {
            aura.blockingStatus = false
        }

    }
}
