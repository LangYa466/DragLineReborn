/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue

@ModuleInfo(name = "SuperKB", description = "Increases knockback dealt to other entities.", category = ModuleCategory.COMBAT)
class SuperKnockback : Module() {

    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val onlySprintValue = BoolValue("OnlySprint", true)

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (classProvider.isEntityLivingBase(event.targetEntity)) {
            if (event.targetEntity!!.asEntityLivingBase().hurtTime > hurtTimeValue.get())
                return

            val player = mc.thePlayer ?: return

            if(onlySprintValue.get() && !player.sprinting) return

            if (player.sprinting)
                mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(player, ICPacketEntityAction.WAction.STOP_SPRINTING))

            mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(player, ICPacketEntityAction.WAction.START_SPRINTING))
            mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(player, ICPacketEntityAction.WAction.STOP_SPRINTING))
            mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(player, ICPacketEntityAction.WAction.START_SPRINTING))
            player.sprinting = true
            player.serverSprintState = true
        }
    }

    override val tag: String
        get() = hurtTimeValue.get().toString()

}