package org.confluence.mod.common.entity.npc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProgression;
import org.confluence.mod.common.init.ModEffects;

/// 树妖在敌人接近时维持会扩张的守护结界。
public final class DryadNPC extends BaseNPC {
    private static final int WARD_DURATION = 190;
    private static final int EFFECT_DURATION = 40;
    private int wardTicks = -1;

    public DryadNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile combatProfile) {
        super(type, level, combatProfile);
    }

    /// 若当前没有结界则开始一次完整施法，重复攻击请求不会重置进度。
    public void startWard() {
        if (wardTicks < 0) wardTicks = 0;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (wardTicks < 0) return;
        tickWard((ServerLevel) level());
        if (++wardTicks >= WARD_DURATION) wardTicks = -1;
    }

    /// 刷新结界内的祝福与诅咒，并按每秒一次结算树妖之祸伤害。
    private void tickWard(ServerLevel level) {
        double radius = wardRadius(wardTicks);
        if (wardTicks >= 10 && wardTicks % 5 == 0) applyWardEffects(radius);
        if (wardTicks % 10 == 0) renderWard(level, radius);
    }

    /// 玩家和城镇 NPC 不要求视线；敌人必须能被树妖看见才会受到树妖之祸。
    private void applyWardEffects(double radius) {
        double radiusSqr = radius * radius;
        AABB area = getBoundingBox().inflate(radius);
        for (Player player : level().getEntitiesOfClass(Player.class, area,
                player -> player.isAlive() && !player.isSpectator() && distanceToSqr(player) <= radiusSqr)) {
            applyBlessing(player);
        }
        for (BaseNPC npc : level().getEntitiesOfClass(BaseNPC.class, area, npc -> npc.isAlive() && distanceToSqr(npc) <= radiusSqr)) {
            applyBlessing(npc);
        }
        for (LivingEntity enemy : level().getEntitiesOfClass(LivingEntity.class, area,
                enemy -> enemy instanceof Enemy && canAttack(enemy) && distanceToSqr(enemy) <= radiusSqr
                        && getSensing().hasLineOfSight(enemy))) {
            boolean afflicted = enemy.hasEffect(ModEffects.DRYADS_BANE.get()) || enemy.addEffect(
                    new MobEffectInstance(ModEffects.DRYADS_BANE.get(), EFFECT_DURATION, 0,
                            false, false, false), this);
            if (afflicted && wardTicks % 20 == 10) {
                float damage = (float) Math.floor(4 * NPCCombatProgression.damageMultiplier(this));
                enemy.hurt(damageSources().magic(), Math.max(1, damage));
            }
        }
    }

    /// 使用隐藏图标的短时效果承载结界数值，离开范围后自然失效。
    private void applyBlessing(LivingEntity living) {
        living.addEffect(new MobEffectInstance(ModEffects.DRYADS_BLESSING.get(), EFFECT_DURATION, 0, false, false, false), this);
    }

    /// 使用现有粒子绘制两层旋转边界，不引入缺失的额外素材。
    private void renderWard(ServerLevel level, double radius) {
        for (int i = 0; i < 16; i++) {
            double angle = Math.PI * 2 * i / 16 + wardTicks * 0.04;
            double x = getX() + Math.cos(angle) * radius;
            double z = getZ() + Math.sin(angle) * radius;
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, getY() + 0.25 + (i & 1), z, 1, 0, 0.1, 0, 0);
        }
    }

    /// 依照结界的四个阶段计算当前半径。
    private static double wardRadius(int ticks) {
        if (ticks < 34) return 18.75;
        if (ticks < 100) return Mth.lerp((ticks - 34) / 66.0, 18.75, 37.5);
        if (ticks < 167) return 37.5;
        return Mth.lerp((ticks - 167) / 23.0, 37.5, 63.75);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("DryadWardTicks", wardTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        wardTicks = tag.contains("DryadWardTicks", Tag.TAG_INT) ? Mth.clamp(tag.getInt("DryadWardTicks"), -1, WARD_DURATION - 1) : -1;
    }
}
