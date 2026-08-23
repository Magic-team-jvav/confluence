package org.confluence.mod.common.effect.harmful;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.confluence.mod.common.init.ModEffects;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

import java.util.UUID;

/// 标记正在参与血肉墙战斗的实体。
///
/// 归属写在受影响实体自身，而不是写进全局效果单例。这样同一服务器不同维度中的
/// 血肉墙不会互相覆盖目标，多人也能各自稳定解析到施加效果的那一只 Boss。
public class HorrifiedEffect extends PortMobEffect {
    private static final String WALL_UUID_TAG = "ConfluenceWallOfFlesh";

    public HorrifiedEffect() {
        super(MobEffectCategory.HARMFUL, 0xAB1122);
    }

    public static void bind(LivingEntity living, WallOfFlesh wall) {
        living.getPersistentData().putUUID(WALL_UUID_TAG, wall.getUUID());
    }

    public static boolean isBoundTo(LivingEntity living, WallOfFlesh wall) {
        CompoundTag data = living.getPersistentData();
        return data.hasUUID(WALL_UUID_TAG) && wall.getUUID().equals(data.getUUID(WALL_UUID_TAG));
    }

    public static @Nullable WallOfFlesh resolve(LivingEntity living) {
        if (!(living.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        CompoundTag data = living.getPersistentData();
        if (!data.hasUUID(WALL_UUID_TAG)) {
            return null;
        }
        UUID uuid = data.getUUID(WALL_UUID_TAG);
        Entity entity = serverLevel.getEntity(uuid);
        return entity instanceof WallOfFlesh wall && wall.isAlive() ? wall : null;
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        if (living.level().isClientSide) {
            return;
        }
        WallOfFlesh wall = resolve(living);
        if (wall == null || living instanceof Player player && (player.isCreative() || player.isSpectator())) {
            living.removeEffect(ModEffects.HORRIFIED.get());
            return;
        }

        if (living.level().dimension() != wall.level().dimension()) {
            living.kill();
            return;
        }
        if (!living.getBoundingBox().intersects(wall.getPursuitBox().inflate(12.0)) && !living.hasEffect(ModEffects.THE_TONGUE.get())) {
            living.addEffect(new MobEffectInstance(ModEffects.THE_TONGUE.get(), 60), wall);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
