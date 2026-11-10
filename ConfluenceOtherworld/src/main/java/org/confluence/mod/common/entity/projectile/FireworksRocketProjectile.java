package org.confluence.mod.common.entity.projectile;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import javax.annotation.ParametersAreNonnullByDefault;

/// 烟火之箭的烟花火箭射弹：替换被使用法器后的下一支箭，命中即在大范围内爆炸。
///
/// 外观由粒子风暴的尾迹粒子负责，本体另有一份 Blockbench 模型；伤害走
/// {@link net.minecraft.world.damagesource.DamageTypes#MOB_PROJECTILE}，因此远程伤害加成照常生效。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireworksRocketProjectile extends DamageSettableProjectile {
    /// 尾迹粒子（粒子风暴定义），客户端跟随弹体持续发射。
    public static final ResourceLocation TRAIL_PARTICLE = Confluence.asResource("bomb_lead");
    /// 爆炸粒子（粒子风暴定义），命中或超时后在弹体位置一次性播放。
    public static final ResourceLocation EXPIRATION_PARTICLE = Confluence.asResource("fireworks_rocket_expiration");
    /// 最长飞行刻数：没打中任何东西也会在半空炸开。
    public static final int MAX_LIFE_TICKS = 100;
    private int life;
    private ParticleEmitter emitter;

    public FireworksRocketProjectile(EntityType<? extends FireworksRocketProjectile> entityType, Level level) {
        super(entityType, level);
        setDamage(SoulSkillParameters.FIREWORKS_ARROW_DAMAGE);
    }

    /// 由被替换的箭矢生成：位置、速度、朝向与主人全部沿用，保证弹道与手感一致。
    public FireworksRocketProjectile(AbstractArrow arrow) {
        this(ModEntities.FIREWORKS_ROCKET.get(), arrow.level());
        @Nullable Entity owner = arrow.getOwner();
        if (owner != null) {
            setOwner(owner);
        }
        setPos(arrow.position());
        setDeltaMovement(arrow.getDeltaMovement());
        setYRot(arrow.getYRot());
        setXRot(arrow.getXRot());
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            /// 每颗弹体只登记一次尾迹发射器，避免把同一张表反复写坏。
            if (emitter == null) {
                this.emitter = new ParticleEmitter(level(), position(), TRAIL_PARTICLE);
                emitter.attachEntity(this);
                MolangParticleEngine.INSTANCE.addEmitter(emitter);
            }
            return;
        }
        if (++life > MAX_LIFE_TICKS) {
            explode();
            return;
        }
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            setPos(hit.getLocation());
            explode();
            return;
        }
        setPos(position().add(getDeltaMovement()));
    }

    /// 大范围爆炸：只结算生物伤害与特效，不改动方块。
    private void explode() {
        Vec3 center = position();
        double radius = SoulSkillParameters.FIREWORKS_ARROW_RADIUS;
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class,
                new AABB(center, center).inflate(radius), this::canHitEntity)) {
            target.hurt(getDamageSource(), damage);
        }
        if (level() instanceof ServerLevel serverLevel) {
            EmitterCreationPacketS2C.sendToAll(EXPIRATION_PARTICLE, center.toVector3f(), MolangExp.EMPTY, null);
            serverLevel.playSound(null, center.x, center.y, center.z,
                    SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 1.6F, 0.9F);
        }
        discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Life", life);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        life = tag.getInt("Life");
    }
}
