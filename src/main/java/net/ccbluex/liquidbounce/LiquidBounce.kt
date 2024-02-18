/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce

import by.radioegor146.nativeobfuscator.Native
import cn.langya.font.FontManager
import cn.langya.util.misc.JsonUtil

import net.ccbluex.liquidbounce.api.Wrapper
import net.ccbluex.liquidbounce.api.minecraft.util.IResourceLocation
import net.ccbluex.liquidbounce.cloud.UserManager
import net.ccbluex.liquidbounce.event.ClientShutdownEvent
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.features.special.DonatorCape
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.injection.backend.Backend
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.remapper.Remapper.loadSrg
import net.ccbluex.liquidbounce.tabs.BlocksTab
import net.ccbluex.liquidbounce.tabs.ExploitsTab
import net.ccbluex.liquidbounce.tabs.HeadsTab
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.hud.HUD.Companion.createDefault
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.ClassUtils.hasForge
import net.ccbluex.liquidbounce.utils.misc.HttpUtils
import org.lwjgl.opengl.Display
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

@Native
object LiquidBounce {

    // Client information
    const val CLIENT_NAME = "DragLine"
    const val CLIENT_VERSION = 1
    const val CLIENT_CREATOR = "CCBlueX"
    private const val MINECRAFT_VERSION = Backend.MINECRAFT_VERSION
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager

    // HUD & ClickGUI
    lateinit var hud: HUD

    lateinit var clickGui: ClickGui

    // Update information
    var latestVersion = 0

    // Menu Background
    var background: IResourceLocation? = null

    lateinit var wrapper: Wrapper
    private var city: String? = null

    @JvmStatic
    fun showNotification(title: String, text: String, type: TrayIcon.MessageType?) {
        val tray = SystemTray.getSystemTray()
        val image = Toolkit.getDefaultToolkit().createImage(javaClass.getResource("/assets/minecraft/liquidbounce/icon_32x32.png"))
        val trayIcon = TrayIcon(image, CLIENT_NAME)
        trayIcon.isImageAutoSize = true
        trayIcon.toolTip = "System tray icon demo"
        tray.add(trayIcon)
        trayIcon.displayMessage(title, text, type)
    }

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true
        //   Minecraft.getMinecraft().displayGuiScreen(GuiLoading())

        //     Thread {

        if(CLIENT_NAME != "DragLine") Runtime.getRuntime().exit(0)

        try {
            city = JsonUtil.getDataString(HttpUtils.get("https://yuym.cn/ipregion"),"regionName")
        } catch (_: Exception) { }

        // Create file manager
        fileManager = FileManager()

        // Crate event manager
        eventManager = EventManager()


        Thread {
            // Register listeners
            eventManager.registerListener(UserManager())
            eventManager.registerListener(RotationUtils())
            eventManager.registerListener(AntiForge())
            eventManager.registerListener(BungeeCordSpoof())
            eventManager.registerListener(DonatorCape())
            eventManager.registerListener(InventoryUtils())
            eventManager.registerListener(InventoryUtils2())
        }

        // Create command manager
        commandManager = CommandManager()

        // Load client fonts
        Fonts.loadFonts()

        // Load client fonts(fixed chinese show)
        FontManager.initFonts()

        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()

        // Remapper
        try {
            loadSrg()

            // ScriptManager
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to load scripts.", throwable)
        }

        // Register commands
        commandManager.registerCommands()

        Thread {
            // Load configs
            fileManager.loadConfigs(
                fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
                fileManager.friendsConfig, fileManager.xrayConfig, fileManager.shortcutsConfig
            )
        }

        // ClickGUI
        clickGui = ClickGui()
        fileManager.loadConfig(fileManager.clickGuiConfig)

        // Tabs (Only for Forge!)
        if (hasForge()) {
            BlocksTab()
            ExploitsTab()
            HeadsTab()
        }

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        // Disable optifine fastrender
        ClientUtils.disableFastRender()

        // Load generators
        GuiAltManager.loadGenerators()
        //    }

        //    wrapper.minecraft.displayGuiScreen(wrapper.classProvider.wrapGuiScreen(GuiMainMenu()))

        Display.setTitle("$CLIENT_NAME b$CLIENT_VERSION | $MINECRAFT_VERSION ${if (city == null) "" else "| ${city!!.replace("省","")}人"}")

        // Set is starting status
        isStarting = false
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        fileManager.saveAllConfigs()
    }

}