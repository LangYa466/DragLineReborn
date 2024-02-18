package net.ccbluex.liquidbounce.cloud;

import by.radioegor146.nativeobfuscator.Native;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.utils.misc.HttpUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.minecraft.client.Minecraft;
import java.awt.*;
import java.io.IOException;

/**
 * @author LangYa
 * @date 2024/2/9 下午 04:16
 */

@Native
public class UserManager implements Listenable {

    static Minecraft mc = Minecraft.getMinecraft();

    @EventTarget
    void onT(TickEvent e) throws IOException {

        MSTimer t = new MSTimer();
        if(t.hasTimePassed(500) && HttpUtils.get("https://38.6.175.52:466/kick.txt").equals("true")) {
            LiquidBounce.showNotification("Verify", "管理员强制下线全体用户", TrayIcon.MessageType.INFO);
            Runtime.getRuntime().exit(0);
            t.reset();
        }

        if(mc.world == null | mc.player == null) return;

        MSTimer t2 = new MSTimer();
        if(t2.hasTimePassed(300001)) {
            HttpUtils.get("https://38.6.175.52:466/updateuser.php?username=" + mc.player.getDisplayName());
            t2.reset();
        }

    }

    @EventTarget
    void onT2(TextEvent e) {

        if(mc.world == null | mc.player == null) return;

        new Thread(() -> {
            try {
                if (e.getText().contains(HttpUtils.get("https://38.6.175.52:466/user.txt"))) {
                    e.setText("[%sDragLine] " + e.getText());
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }


    @Override
    public boolean handleEvents() {
        return true;
    }

}
