package org.confluence.terraentity.entity.model;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CrownOfKingSlimeModelEntity extends Entity {
    public static final float RADIUS = 1.5F;
    private static final Vector3f UP = new Vector3f(0.0F, 1.0F, 0.0F);
    public Vector3f rot;
    public Vector3f rotO;
    public Quaternionf quaternionf;

    public float alpha;
    public float beta;
    public float radius;
    public float omega;
    public float omega1;
    public float omega2;

    public float rotate1 = 0.0F;
    public float rotateO1 = 0.0F;
    public float rotate2 = 0.0F;
    public float rotateO2 = 0.0F;
    public float height = 0.0F;

    public CrownOfKingSlimeModelEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        float r = Mth.PI * 0.25F;
        this.rot = new Vector3f(r, 0, r);
        if (level().isClientSide) {
            this.rotO = new Vector3f(rot);
            this.quaternionf = new Quaternionf();
            this.radius = 2.0F * RADIUS;
            this.rotate2 = random.nextFloat() * Mth.TWO_PI;
        }
    }

    public CrownOfKingSlimeModelEntity(Level level, Vec3 pos) {
        this(TEBossEntities.CROWN_OF_KING_SLIME_MODEL.get(), level);
        setPos(pos);
        setDeltaMovement(
                random.nextFloat() * (random.nextBoolean() ? 0.5 : -0.5),
                random.nextFloat() + 0.3,
                random.nextFloat() * (random.nextBoolean() ? 0.5 : -0.5)
        );
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {}

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {}

    @Override
    public void tick() {
        move(MoverType.SELF, getDeltaMovement());
        if (level().isClientSide) {
            rotO.set(rot);
            this.rotateO1 = rotate1;
            this.rotateO2 = rotate2;
        }
        this.alpha = new Vector3f(UP).rotateZ(rot.z).rotateX(rot.x).angle(UP);
        float sinAlpha = Mth.sin(alpha);
        this.height = sinAlpha * RADIUS;
        if (level().isClientSide && onGround()) {
            float cosAlpha = Mth.cos(alpha);
            this.beta = (float) Math.atan(cosAlpha / (radius / RADIUS - sinAlpha));
            float sinBeta = Mth.sin(beta);
            float cosBeta = Mth.cos(beta);
            float cosAlphaSubBeta = Mth.cos(alpha - beta);
            this.omega = Mth.sqrt((4 * 0.08F * sinAlpha * cosAlpha) / (RADIUS * sinBeta * (cosAlphaSubBeta + 5 * cosAlpha * cosBeta)));
            this.omega1 = omega * cosAlphaSubBeta / cosAlpha;
            this.omega2 = omega * sinBeta / cosAlpha;

            this.rotate1 += omega1;
            this.rotate2 += omega2;
        }
        if (onGround()) {
            if (rot.x > Mth.EPSILON) rot.x *= 0.96F;
            if (rot.z > Mth.EPSILON) rot.z *= 0.96F;
            setDeltaMovement(0.0, getDeltaMovement().y, 0.0);
        }
        setDeltaMovement(getDeltaMovement().add(0.0, -0.08, 0.0));
        if (alpha < 0.05F) discard();
    }
}
