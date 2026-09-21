package org.confluence.mod.common.entity.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.common.init.ModSoundEvents;

public class Gnome extends BaseWarriorMonster {
    public Gnome(EntityType<? extends Gnome> type, Level level) {
        super(type, level, 0, LandAnimationProfile.NONE, LandSoundProfile.ROUTINE, 1.2, true);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server) || !isAlive() || !onGround() || !server.isDay()
                || !server.canSeeSky(blockPosition()) || !server.getFluidState(blockPosition()).isEmpty()
                || !server.getBlockState(blockPosition()).canBeReplaced()) return;
        if (server.setBlockAndUpdate(blockPosition(), DecorativeBlocks.GARDEN_GNOME.get().defaultBlockState())) {
            Bestiary.INSTANCE.updateEntry(this, true);
            var player = server.getNearestPlayer(this, 32);
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)
                AchievementUtils.awardAchievement(serverPlayer, "heliophobia");
            discard();
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROUTINE_DEATH.get();
    }

}
