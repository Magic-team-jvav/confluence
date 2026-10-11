package org.confluence.mod.client.sound;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.confluence.mod.common.init.ModSoundEvents;

/// 暴风雪的环境风声。
///
/// 跟随玩家循环播放，**露天用 {@code blizzard_strong_loop}、室内用 {@code blizzard_inside_building_loop}**
/// ——这两个音效在本模组里早已存在（`ModSoundEvents` + `sounds.json`）但此前没有任何逻辑引用，
/// 这里把它们接起来。
///
/// 进入/退出用 40 tick 淡入淡出（与 {@code BiomeAmbientSoundsHandler.LoopSoundInstance} 同款做法），
/// 免得暴风雪开始/结束或玩家进屋时声音硬切。
public class BlizzardAmbientSound extends AbstractTickableSoundInstance {
    private static final int FADE_TICKS = 40;
    /// 最大音量。暴风雪是环境音，不该盖过其他声音。
    private static final float MAX_VOLUME = 0.65F;

    private final LocalPlayer player;
    private final boolean indoor;
    private int fade = 0;
    private boolean fadingOut = false;

    public BlizzardAmbientSound(LocalPlayer player, boolean indoor) {
        super(indoor ? ModSoundEvents.BLIZZARD_INSIDE_BUILDING_LOOP.get() : ModSoundEvents.BLIZZARD_STRONG_LOOP.get(),
                SoundSource.WEATHER, SoundInstance.createUnseededRandom());
        this.player = player;
        this.indoor = indoor;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
        this.relative = false;
        followPlayer();
    }

    public boolean isIndoor() {
        return indoor;
    }

    /// 请求淡出；淡完之后自行停止。
    public void fadeOut() {
        this.fadingOut = true;
    }

    /// 立即停止，不淡出（退出世界/关闭配置时用）。`stop()` 是 protected，所以这里开一个口子。
    public void stopNow() {
        stop();
    }

    @Override
    public void tick() {
        if (player.isRemoved() || player.level() == null) {
            stop();
            return;
        }
        followPlayer();

        if (fadingOut) {
            fade = Math.max(0, fade - 1);
            if (fade == 0) {
                stop();
                return;
            }
        } else {
            fade = Math.min(FADE_TICKS, fade + 1);
        }
        volume = MAX_VOLUME * Mth.clamp((float) fade / FADE_TICKS, 0.0F, 1.0F);
    }

    private void followPlayer() {
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }
}
