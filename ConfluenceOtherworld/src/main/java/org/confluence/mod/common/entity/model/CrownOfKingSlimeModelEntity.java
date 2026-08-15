package org.confluence.mod.common.entity.model;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.entity.BossEntities;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 史莱姆王传送时脱落的王冠。
 *
 * <p>实体只负责同步位置、重力和落地后的滚动衰减。旋转轨迹沿用 1.21 侧实现，
 * 客户端渲染器根据连续状态绘制王冠，避免把纯视觉行为塞回战斗状态机。</p>
 */
public class CrownOfKingSlimeModelEntity extends Entity {
    public static final float RADIUS = 1.5F;
    private static final Vector3f UP = new Vector3f(0.0F, 1.0F, 0.0F);

    public final Vector3f rot;
    public Vector3f rotO;
    public Quaternionf quaternion;
    public float alpha;
    public float beta;
    public float radius;
    public float omega;
    public float omega1;
    public float omega2;
    public float rotate1;
    public float rotateO1;
    public float rotate2;
    public float rotateO2;
    public float height;

    public CrownOfKingSlimeModelEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        float rotation = Mth.PI * 0.25F;
        this.rot = new Vector3f(rotation, 0.0F, rotation);
        if (level.isClientSide) {
            this.rotO = new Vector3f(rot);
            this.quaternion = new Quaternionf();
            this.radius = 2.0F * RADIUS;
            this.rotate2 = random.nextFloat() * Mth.TWO_PI;
        }
    }

    public CrownOfKingSlimeModelEntity(Level level, Vec3 position) {
        this(BossEntities.CROWN_OF_KING_SLIME_MODEL.get(), level);
        setPos(position);
        setDeltaMovement(
                random.nextFloat() * (random.nextBoolean() ? 0.5 : -0.5),
                random.nextFloat() + 0.3,
                random.nextFloat() * (random.nextBoolean() ? 0.5 : -0.5));
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}

    @Override
    public void tick() {
        move(MoverType.SELF, getDeltaMovement());
        if (level().isClientSide) {
            rotO.set(rot);
            rotateO1 = rotate1;
            rotateO2 = rotate2;
        }

        alpha = new Vector3f(UP).rotateZ(rot.z).rotateX(rot.x).angle(UP);
        float sinAlpha = Mth.sin(alpha);
        height = sinAlpha * RADIUS;
        if (level().isClientSide && onGround()) {
            float cosAlpha = Mth.cos(alpha);
            beta = (float) Math.atan(cosAlpha / (radius / RADIUS - sinAlpha));
            float sinBeta = Mth.sin(beta);
            float cosBeta = Mth.cos(beta);
            float cosAlphaSubBeta = Mth.cos(alpha - beta);
            omega = Mth.sqrt((4.0F * 0.08F * sinAlpha * cosAlpha)
                    / (RADIUS * sinBeta * (cosAlphaSubBeta + 5.0F * cosAlpha * cosBeta)));
            omega1 = omega * cosAlphaSubBeta / cosAlpha;
            omega2 = omega * sinBeta / cosAlpha;
            rotate1 += omega1;
            rotate2 += omega2;
        }

        if (onGround()) {
            if (rot.x > Mth.EPSILON) {
                rot.x *= 0.96F;
            }
            if (rot.z > Mth.EPSILON) {
                rot.z *= 0.96F;
            }
            setDeltaMovement(0.0, getDeltaMovement().y, 0.0);
        }
        setDeltaMovement(getDeltaMovement().add(0.0, -0.08, 0.0));
        if (alpha < 0.05F) {
            discard();
        }
    }
}
