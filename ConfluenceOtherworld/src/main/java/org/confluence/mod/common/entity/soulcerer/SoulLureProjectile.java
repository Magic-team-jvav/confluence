package org.confluence.mod.common.entity.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulLureProjectile extends Projectile {
    /// 粒子风暴定义的灵魂弹幕特效，拖尾与消失时的爆散都由该特效负责。
    public static final ResourceLocation PARTICLE = Confluence.asResource("soul_projectile");
    private float damage;
    private double travelledDistance;
    private @Nullable ParticleEmitter emitter;

    public SoulLureProjectile(EntityType<? extends SoulLureProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public SoulLureProjectile(LivingEntity owner, float damage) {
        this(ModEntities.SOUL_LURE_PROJECTILE.get(), owner.level());
        setOwner(owner);
        setPos(owner.getX(), owner.getEyeY(), owner.getZ());
        this.damage = damage;
        shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0, SoulSkillParameters.SOUL_LURE_SPEED, 0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    /// 每刻沿移动线段碰撞，先处理命中再检查最大距离；客户端维护灵魂弹幕粒子特效。
    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && (emitter == null || emitter.isRemoved())) {
            this.emitter = new ParticleEmitter(level(), position(), PARTICLE);
            emitter.attachEntity(this);
            emitter.hideOutline = true;
            MolangParticleEngine.INSTANCE.addEmitter(emitter);
        }
        Vec3 movement = getDeltaMovement();
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (!level().isClientSide && hit.getType() != HitResult.Type.MISS) {
            setPos(hit.getLocation());
            if (hit instanceof EntityHitResult entityHit) {
                entityHit.getEntity().hurt(LibDamageTypes.soul(level(), this, getOwner()), damage);
            }
            discard();
            return;
        }
        setPos(position().add(movement));
        travelledDistance += movement.length();
        if (!level().isClientSide && (travelledDistance >= SoulSkillParameters.SOUL_LURE_MAX_DISTANCE || tickCount >= SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS)) {
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getFloat("Damage");
        travelledDistance = tag.getDouble("TravelledDistance");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", damage);
        tag.putDouble("TravelledDistance", travelledDistance);
    }
}
