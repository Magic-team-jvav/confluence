package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.client.sound.BlizzardAmbientSound;
import org.confluence.mod.common.gameevent.BlizzardGameEvent;
import org.confluence.mod.util.OverworldUtils;

/// 暴风雪的环境风声。
///
/// 判定与事件本体一致：**雪原 + 下雨**（暴风雪与风速无关），并且要求**该玩家自己**站在雪原里
/// ——事件是全局的（只要有人进雪原就会开），但沙漠里的玩家不该听见暴风雪。
/// 露天与室内分别对应两个不同的音效。
///
/// 在两个音效之间切换、或暴风雪结束时，旧实例走 40 tick 淡出，避免硬切。
public final class BlizzardAmbienceHandler {
    private static BlizzardAmbientSound current;

    /// 在客户端 tick 中调用（见 {@code GameClientEvents}）。
    public static void tick(LocalPlayer player) {
        if (!ClientConfigs.blizzardAmbience) {
            reset();
            return;
        }
        if (isStopped()) current = null;

        if (!isBlizzardHere(player)) {
            if (current != null) {
                current.fadeOut();
                current = null;
            }
            return;
        }

        boolean indoor = !player.level().canSeeSky(player.blockPosition());
        if (current != null && current.isIndoor() == indoor) return;

        if (current != null) current.fadeOut(); // 室内外切换：旧的那个淡出
        current = new BlizzardAmbientSound(player, indoor);
        Minecraft.getInstance().getSoundManager().play(current);
    }

    public static void reset() {
        if (current != null) {
            current.stopNow();
            current = null;
        }
    }

    private static boolean isStopped() {
        return current != null && current.isStopped();
    }

    /// 该玩家所在位置此刻是否有暴风雪。
    private static boolean isBlizzardHere(LocalPlayer player) {
        if (!(player.level() instanceof ClientLevel level)) return false;
        if (!level.isRaining()) return false;
        if (!ClientGameEventSystem.isEventStarted(BlizzardGameEvent.KEY)) return false;
        return OverworldUtils.isSnowy(level.getBiome(player.blockPosition()));
    }

    private BlizzardAmbienceHandler() {}
}
