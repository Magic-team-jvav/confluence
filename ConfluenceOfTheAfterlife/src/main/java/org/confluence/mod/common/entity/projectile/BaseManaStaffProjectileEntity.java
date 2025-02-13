package org.confluence.mod.common.entity.projectile;

import com.mojang.serialization.Codec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.util.ModUtils;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.ArrayList;
import java.util.List;

public class BaseManaStaffProjectileEntity extends Projectile {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(BaseManaStaffProjectileEntity.class, EntityDataSerializers.INT);
    protected int penetrateCount = 2;
    protected List<Entity> penetrateList = new ArrayList<>();

    protected ParticleEmitter emitter;

    public BaseManaStaffProjectileEntity(EntityType<? extends BaseManaStaffProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BaseManaStaffProjectileEntity(LivingEntity living, Level level, Variant variant) {
        this(ModEntities.BASE_MANA_STAFF_PROJECTILE.get(), living, level, variant);
    }

    public BaseManaStaffProjectileEntity(EntityType<? extends BaseManaStaffProjectileEntity> entityType, LivingEntity living, Level level, Variant variant) {
        super(entityType, level);
        setOwner(living);
        setNoGravity(variant.gravity <= 0.0);
        setVariant(variant);
        setPos(living.getX(), living.getEyeY() - 0.1, living.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_VARIANT_ID, 0);
    }

    public void setVariant(Variant pVariant) {
        entityData.set(DATA_VARIANT_ID, pVariant.id);
    }

    public Variant getVariant() {
        return Variant.byId(entityData.get(DATA_VARIANT_ID));
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3 = getDeltaMovement();

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        checkInsideBlocks();
        HitResult.Type hitresult$type = hitresult.getType();
        if (hitresult$type == HitResult.Type.BLOCK || tickCount > 200) {
            discard();
            return;
        }
        if (hitresult$type == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitresult);
        }

        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        if (!isNoGravity()) {
            setDeltaMovement(vec3.x, vec3.y - getGravity(), vec3.z);
        }
        setPos(offX, offY, offZ);

        if (level().isClientSide && emitter == null) {
            this.emitter = new ParticleEmitter(level(), position(), getParticleId());
            emitter.attached = this;
            PSGameClient.LOADER.addEmitter(emitter, false);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (level().isClientSide) return;
        Entity entity = entityHitResult.getEntity();
        if (canAttack(entity)) {
            float damage = getBaseDamage() * (1.0F + getAttackBonus());
            if (entity.hurt(damageSources().indirectMagic(this, getOwner()), damage)) {
                float attackKnockback = getBaseKnockBack() * (1.0F + getKnockbackBonus());
                if (attackKnockback > 0.0F) {
                    ModUtils.knockBackA2B(this, entity, attackKnockback * 0.5, 0.2);
                }
            }
            penetrateList.add(entity);
            if (penetrateCount == penetrateList.size()) discard();
        }
    }

    protected float getAttackBonus() {
        return 0.0F;
    }

    protected float getKnockbackBonus() {
        return 0.0F;
    }

    protected ResourceLocation getParticleId() {
        return getVariant().particleId;
    }

    public boolean canAttack(Entity entity) {
        return entity != getOwner() && !penetrateList.contains(entity);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    protected float getBaseDamage() {
        return getVariant().damage;
    }

    @Override
    protected double getDefaultGravity() {
        return getVariant().gravity;
    }

    protected float getBaseKnockBack() {
        return getVariant().knockBack;
    }

    public static class Spark extends BaseManaStaffProjectileEntity {
        public Spark(LivingEntity living, Level level) {
            super(living, level, Variant.SPARK);
        }

        @Override
        protected void onHitEntity(EntityHitResult entityHitResult) {
            super.onHitEntity(entityHitResult);
            entityHitResult.getEntity().igniteForTicks(100);
        }
    }

    public static class Frost extends BaseManaStaffProjectileEntity {
        public Frost(LivingEntity living, Level level) {
            super(living, level, Variant.FROST);
        }

        @Override
        protected void onHitEntity(EntityHitResult entityHitResult) {
            super.onHitEntity(entityHitResult);
            if (entityHitResult.getEntity() instanceof LivingEntity living) {
                int duration = living.getRandom().nextFloat() < 2.0F / 3.0F ? 40 : 60;
                living.addEffect(new MobEffectInstance(ModEffects.FROST_BURN, duration));
            }
        }
    }

    public record Variant(int id, String name, float damage, double gravity, float knockBack, ResourceLocation particleId) implements StringRepresentable {
        public static final List<Variant> VALUES = new ArrayList<>();

        public static final Variant AMETHYST = register("amethyst", 6.0F, -1.0, 3.25F, Confluence.asResource("amethyst_projectile"));
        public static final Variant TOPAZ = register("topaz", 6.4F, -1.0, 3.5F, Confluence.asResource("topaz_projectile"));
        public static final Variant SAPPHIRE = register("sapphire", 7.2F, -1.0, 4.0F, Confluence.asResource("sapphire_projectile"));
        public static final Variant EMERALD = register("emerald", 7.6F, -1.0, 4.25F, Confluence.asResource("emerald_projectile"));
        public static final Variant RUBY = register("ruby", 8.4F, -1.0, 4.75F, Confluence.asResource("ruby_projectile"));
        public static final Variant AMBER = register("amber", 8.4F, -1.0, 4.75F, Confluence.asResource("amber_projectile"));
        public static final Variant DIAMOND = register("diamond", 9.2F, -1.0, 5.5F, Confluence.asResource("diamond_projectile"));
        public static final Variant FROST = register("frost", 3.0F, 0.04, 0.0F, Confluence.asResource("frost_projectile"));
        public static final Variant SPARK = register("spark", 2.6F, 0.2, 0.0F, Confluence.asResource("spark_projectile"));
        public static final Variant THUNDER_ZAPPER = register("thunder_zapper", 7.8F, -1.0, 0.0F, Confluence.asResource("thunder_zapper"));

        public static final Codec<Variant> CODEC = StringRepresentable.fromValues(() -> VALUES.toArray(Variant[]::new));

        /**
         * @param rawKnockBack 换算前的击退
         */
        private static Variant register(String name, float damage, double gravity, float rawKnockBack, ResourceLocation particleId) {
            Variant variant = new Variant(VALUES.size(), name, damage, gravity, rawKnockBack / 8.0F, particleId);
            VALUES.add(variant);
            return variant;
        }

        public static Variant byId(int id) {
            return VALUES.get(id);
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
