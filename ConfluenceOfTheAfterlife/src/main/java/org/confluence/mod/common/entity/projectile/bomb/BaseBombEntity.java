package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.util.ModUtils;
import org.joml.Vector3f;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class BaseBombEntity extends ThrowableItemProjectile {
    public static final ResourceLocation PARTICLE = Confluence.asResource("bomb_lead");
    public static final float DIAMETER = 0.375F;
    public float rotateO = 0.0F;
    public float rotate = 0.0F;
    public Vector3f rotation = new Vector3f();
    public ParticleEmitter emitter;

    protected int delay = 60;
    protected float blastPower = 3.0F;
    protected double bounceFactor = 0.2;
    protected double frictionFactor = 0.9;

    public BaseBombEntity(EntityType<? extends BaseBombEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BaseBombEntity(EntityType<? extends BaseBombEntity> pEntityType, LivingEntity pShooter) {
        super(pEntityType, pShooter, pShooter.level());
    }

    public BaseBombEntity(LivingEntity pShooter) {
        this(ModEntities.BOMB_ENTITY.get(), pShooter);
    }

    @Override
    protected Item getDefaultItem() {
        return ConsumableItems.BOMB.get();
    }

    /**
     * 子类可以覆盖的方法，在弹跳前触发
     */
    protected void blockHitCallBack(BlockHitResult hitBlock) {}

    /**
     * 子类可以覆盖的方法，定义炸弹如何爆炸
     */
    protected void explodeFunction() {
        level().explode(this, getX(), getY(), getZ(), blastPower, Level.ExplosionInteraction.BLOCK);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        blockHitCallBack(pResult);
        Vec3 motion = ModUtils.relativeScale(getDeltaMovement(), pResult.getDirection().getAxis(), -bounceFactor);
        if (Math.abs(motion.y) < 0.03) motion = new Vec3(motion.x, 0.0, motion.z);
        setDeltaMovement(motion.scale(frictionFactor));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            float s = (float) getDeltaMovement().length();
            float r = 2.0F * s / DIAMETER;
            if (rotate > Mth.TWO_PI) this.rotate -= Mth.TWO_PI;
            this.rotateO = rotate;
            this.rotate += r / Mth.PI;
            rotation.set(0.0, 0.0, rotate);
            createEmitter();
        } else if (this.delay-- < 0) {
            explodeFunction();
            discard();
        }
    }

    protected void createEmitter() {
        if (emitter == null) {
            this.emitter = new ParticleEmitter(level(), position(), getLeadParticle());
            emitter.offsetRot.set(0.0, Mth.HALF_PI, 0.0);
            emitter.offsetPos = new Vec3(0.0, DIAMETER, 0.0);
            emitter.parentRotation = rotation;
            emitter.attached = this;
            PSGameClient.LOADER.addEmitter(emitter, false);
        }
    }

    @Override
    public double getDefaultGravity() {
        return 0.05;
    }

    protected ResourceLocation getLeadParticle() {
        return PARTICLE;
    }
}
