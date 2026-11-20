package org.confluence.mod.common.effect.harmful;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/** Uses the player's horizontal movement intent, so blocked input still counts but forced travel does not. */
public final class ElectrifiedEffect extends MobEffect {
    private static final Map<ServerPlayer, InputState> PLAYER_INPUT = Collections.synchronizedMap(new WeakHashMap<>());

    private record InputState(boolean moving, long gameTime) {}

    public ElectrifiedEffect() {
        super(MobEffectCategory.HARMFUL, 0x66D9FF);
    }

    public static void recordHorizontalInput(ServerPlayer player, boolean moving) {
        if (player != null) PLAYER_INPUT.put(player, new InputState(moving, player.level().getGameTime()));
    }

    public static float damagePerSecond(boolean moving) {
        return moving ? 3.2F : 0.8F;
    }

    private static boolean hasHorizontalMovement(LivingEntity living) {
        if (living instanceof ServerPlayer player) {
            InputState state = PLAYER_INPUT.get(player);
            long elapsed = state == null ? Long.MAX_VALUE : player.level().getGameTime() - state.gameTime();
            return state != null && state.moving() && elapsed >= 0 && elapsed <= 3;
        }
        double horizontalSpeedSquared = living.getDeltaMovement().x * living.getDeltaMovement().x
                + living.getDeltaMovement().z * living.getDeltaMovement().z;
        return horizontalSpeedSquared > 1.0E-4D;
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        if (living != null && !living.level().isClientSide) {
            living.hurt(living.damageSources().magic(), damagePerSecond(hasHorizontalMovement(living)));
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
