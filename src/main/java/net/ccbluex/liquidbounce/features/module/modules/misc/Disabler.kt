package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.minecraft.client.gui.GuiDownloadTerrain
import net.minecraft.network.INetHandler
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.*
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.injection.backend.wrap
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.CopyOnWriteArrayList

@ModuleInfo(name = "Disabler", description = "Fuck Hyt", category = ModuleCategory.EXPLOIT)
class Disabler : Module() {

    val post = BoolValue("PostReset", true)
    private val autoBlockFix = BoolValue("RotationPlace", true)
    private val badPacketA = BoolValue("RefreshInv", true)
    private val fastBreak = BoolValue("FastBreak", false)
    private val c0B = BoolValue("InvalidC0B", false)

    val modeValue = ListValue("Mode", arrayOf("GrimAC"), "GrimAC")

    private var lastSlot: Int = -1
    private var lastAction = ""
    private var canSprint = false

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        val thePlayer = mc.thePlayer ?: return
        val our = classProvider.isItemFood(thePlayer.heldItem?.item)
                || classProvider.isItemPotion(thePlayer.heldItem?.item)
                || classProvider.isItemBucketMilk(thePlayer.heldItem?.item)
                || classProvider.isItemBow(thePlayer.heldItem?.item)

        val killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
        if (packet is CPacketPlayerTryUseItemOnBlock && ( killAura.currentTarget != null || our) && autoBlockFix.get()) {
            event.cancelEvent()
        }

        if (fastBreak.get()) {
            val pw = event.packet.unwrap()
            if (pw is CPacketPlayerDigging && pw.action == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                val connection = mc.unwrap().connection ?: return
                connection.sendPacket(
                    CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
                        pw.position.add(0, 500, 0),
                        pw.facing
                    )
                )
            }
        }

        if (c0B.get()) {
            canSprint = !(!MovementUtils.isMoving
                    || (thePlayer.movementInput.moveForward < 0.8f
                    || thePlayer.isInLava
                    || thePlayer.isInWater
                    || thePlayer.isInWeb
                    || thePlayer.isOnLadder))

            if (packet is CPacketEntityAction) {
                if (packet.action.name == lastAction) {
                    event.cancelEvent()
                } else {
                    if (!canSprint && packet.action == CPacketEntityAction.Action.START_SPRINTING) {
                        event.cancelEvent()
                    } else {
                        lastAction = packet.action.name
                    }
                }
            }
        }

        if(badPacketA.get() && packet is CPacketHeldItemChange){
            val slot: Int = packet.slotId
            if (slot == this.lastSlot && slot != -1) {
                event.cancelEvent()
            }

            this.lastSlot = packet.slotId
        }

        if (packet is SPacketConfirmTransaction) {
            if (grimPost() && packet.actionNumber < 0) {
                pingPackets.add(packet.actionNumber.toInt())
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        lastSlot = -1
    }

    companion object {

        // Static
        @JvmStatic
        var storedPackets: MutableList<Packet<INetHandler>> = CopyOnWriteArrayList()

        @JvmStatic
        var pingPackets: ConcurrentLinkedDeque<Int> = ConcurrentLinkedDeque()

        @JvmStatic
        private var lastResult = false
    }

    fun grimPost(): Boolean {
        val thePlayer = mc.thePlayer ?: return false
        val disabler = LiquidBounce.moduleManager.getModule(Disabler::class.java) as Disabler
        val result = disabler.state && modeValue.get() == "GrimAC" && post.get()
                && mc.thePlayer != null
                && thePlayer.entityAlive
                && thePlayer.ticksExisted >= 10
                && mc.currentScreen !is GuiDownloadTerrain

        if (lastResult && !result) {
            lastResult = false
            mc2.addScheduledTask { processPackets() }
        }

        return result.also { lastResult = it }
    }

    fun processPackets() {
        if (storedPackets.isNotEmpty()) {
            for (packet in storedPackets) {
                val event = PacketEvent(packet.wrap())
                LiquidBounce.eventManager.callEvent(event)
                if (event.isCancelled) {
                    continue
                }

                packet.processPacket(mc2.connection as INetHandler)
            }

            storedPackets.clear()
        }
    }

    fun grimPostDelay(packet: Packet<*>): Boolean {
        val thePlayer = mc.thePlayer ?: return false

        if (mc.currentScreen is GuiDownloadTerrain) {
            return false
        }

        if (packet is SPacketEntityVelocity) {
            val sPacketEntityVelocity: SPacketEntityVelocity = packet
            return sPacketEntityVelocity.entityID == thePlayer.entityId
        }

        return packet is SPacketExplosion
                || packet is SPacketConfirmTransaction
                || packet is SPacketPlayerPosLook
                || packet is SPacketEntityEquipment
                || packet is SPacketBlockChange
                || packet is SPacketMultiBlockChange
                || packet is SPacketKeepAlive
                || packet is SPacketUpdateHealth
                || packet is SPacketEntity
                || packet is SPacketSpawnMob
                || packet is SPacketCustomPayload
    }

    fun fixC0F(packet: CPacketConfirmTransaction) {
        val id: Int = packet.uid.toInt()
        if (id >= 0 || pingPackets.isEmpty()) {
            mc2.connection!!.sendPacket(packet)
        } else {
            do {
                val current: Int = pingPackets.first
                mc2.connection!!.sendPacket(CPacketConfirmTransaction(packet.windowId, current.toShort(), true))
                pingPackets.pollFirst()
                if (current == id) {
                    break
                }

            } while (!pingPackets.isEmpty())
        }
    }

    override val tag: String
        get() = "GrimAC"
}