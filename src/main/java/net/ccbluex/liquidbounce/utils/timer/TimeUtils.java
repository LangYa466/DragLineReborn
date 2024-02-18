/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils.timer;

import net.ccbluex.liquidbounce.utils.misc.RandomUtils;

public final class TimeUtils {
    private long lastMS;

    public static long randomDelay(final int minDelay, final int maxDelay) {
        return RandomUtils.nextInt(minDelay, maxDelay);
    }

    public static long randomClickDelay(final int minCPS, final int maxCPS) {
        return (long) ((Math.random() * (1000 / minCPS - 1000 / maxCPS + 1)) + 1000 / maxCPS);
    }

    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public boolean hasReached(double milliseconds) {
        return (double) (this.getCurrentMS() - this.lastMS) >= milliseconds;
    }
    public boolean hasreached(long milliseconds) {
        return (double) (this.getCurrentMS() - this.lastMS) >= milliseconds;
    }
    public void reset() {
        this.lastMS = this.getCurrentMS();
    }

    public boolean delay(float milliSec) {
        return (float) (getTime() - this.lastMS) >= milliSec;
    }

    public static long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public boolean sleep(final long time) {
        if (getTime() >= time) {
            reset();
            return true;
        }
        return false;
    }
    public boolean sleep(final double time) {
        if (getTime() >= time) {
            reset();
            return true;
        }
        return false;
    }
}