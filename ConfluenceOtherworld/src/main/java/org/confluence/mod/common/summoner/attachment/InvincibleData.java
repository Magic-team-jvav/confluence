package org.confluence.mod.common.summoner.attachment;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.confluence.mod.common.summoner.SummonerAttachmentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class InvincibleData {
    public static void handler(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (!target.level().isClientSide() && event.getSource().getEntity() instanceof LivingEntity attacker && attacker != target) {
            get(target).recordHit(attacker.getUUID(), 100);
        }
    }

    private final Long2IntOpenHashMap hurtHistory = new Long2IntOpenHashMap();
    private final Long2IntOpenHashMap partialInvincibleFrames = new Long2IntOpenHashMap();
    private int globalInvincibleFrames;

    public void tick() {
        if (!hurtHistory.isEmpty()) {
            hurtHistory.replaceAll((key, value) -> value - 1);
            hurtHistory.values().removeIf(value -> value <= 0);
        }
        if (!partialInvincibleFrames.isEmpty()) {
            partialInvincibleFrames.replaceAll((key, value) -> value - 1);
            partialInvincibleFrames.values().removeIf(value -> value <= 0);
        }
        if (globalInvincibleFrames > 0) {
            globalInvincibleFrames--;
        }
    }

    public static InvincibleData get(LivingEntity living) {
        return living.getData(SummonerAttachmentTypes.INVINCIBLE_DATA);
    }

    public boolean hasAttack(UUID uuid) {
        return hurtHistory.containsKey(uuid.getMostSignificantBits());
    }

    public void recordHit(@NotNull UUID uuid, int ticks) {
        hurtHistory.put(uuid.getMostSignificantBits(), ticks);
    }

    public static AttackBuilder attack(LivingEntity target) {
        return new AttackBuilder(target);
    }

    public enum Type {
        PARTIAL,
        GLOBAL
    }

    public static final class AttackBuilder {
        private final LivingEntity target;
        private @Nullable UUID uuid;
        private int invincibleTime;
        private @Nullable DamageSource damageSource;
        private float damageAmount;
        private Type type = Type.PARTIAL;
        private @Nullable MobEffectInstance mobEffectInstance;

        private AttackBuilder(LivingEntity target) {
            this.target = target;
        }

        public AttackBuilder attacker(@Nullable UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public AttackBuilder damageSource(@Nullable DamageSource damageSource) {
            this.damageSource = damageSource;
            return this;
        }

        public AttackBuilder damageAmount(float damageAmount) {
            this.damageAmount = damageAmount;
            return this;
        }

        public AttackBuilder invincibleTime(int invincibleTime) {
            this.invincibleTime = invincibleTime;
            return this;
        }

        public AttackBuilder global() {
            this.type = Type.GLOBAL;
            return this;
        }

        public AttackBuilder effect(@Nullable MobEffectInstance mobEffectInstance) {
            this.mobEffectInstance = mobEffectInstance;
            return this;
        }

        public boolean apply() {
            if (!target.isAlive() || damageAmount <= 0) {
                return false;
            }

            InvincibleData data = get(target);
            boolean canDamage = uuid == null;
            if (!canDamage) {
                long uuidKey = uuid.getMostSignificantBits();
                canDamage = switch (type) {
                    case PARTIAL -> !data.partialInvincibleFrames.containsKey(uuidKey);
                    case GLOBAL -> data.globalInvincibleFrames <= 0;
                };
            }
            if (!canDamage) {
                return false;
            }

            Level level = target.level();
            if (damageSource == null) {
                damageSource = level.damageSources().generic();
            }
            int previousInvulnerableTime = target.invulnerableTime;
            target.invulnerableTime = 0;
            boolean hurt = target.hurt(damageSource, damageAmount);
            target.invulnerableTime = previousInvulnerableTime;
            if (!hurt) {
                return false;
            }

            if (mobEffectInstance != null) {
                target.addEffect(mobEffectInstance);
            }
            if (invincibleTime > 0) {
                if (type == Type.PARTIAL && uuid != null) {
                    data.partialInvincibleFrames.put(uuid.getMostSignificantBits(), invincibleTime);
                } else {
                    data.globalInvincibleFrames = invincibleTime;
                }
            }
            return true;
        }
    }
}
