package org.confluence.mod.common.entity.projectile.mana;

import com.mojang.serialization.Codec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.ArrayList;
import java.util.List;

public class BaseManaStaffProjectileEntity extends AbstractManaProjectile {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(BaseManaStaffProjectileEntity.class, EntityDataSerializers.INT);
    protected int penetrateCount = 2;
    protected List<Entity> penetrateList = new ArrayList<>();

    protected ParticleEmitter emitter;

    public BaseManaStaffProjectileEntity(EntityType<? extends BaseManaStaffProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BaseManaStaffProjectileEntity(LivingEntity living, Variant variant) {
        this(ModEntities.BASE_MANA_STAFF_PROJECTILE.get(), living, living.level(), variant);
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
    public void baseTick() {
        super.baseTick();

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        checkInsideBlocks();
        HitResult.Type hitresult$type = hitresult.getType();
        if (hitresult$type == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hitresult);
            discard();
            return;
        } else if (hitresult$type == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitresult);
        }

        Vec3 vec3 = getDeltaMovement();
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

        if (tickCount > 200) discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (level().isClientSide) return;
        Entity entity = entityHitResult.getEntity();
        if (canAttack(entity)) {
            float damage = getDamage() * (1.0F + getAttackBonus());
            if (entity.hurt(getDamagesource(), damage)) {
                float attackKnockback = getBaseKnockBack() * (1.0F + getKnockbackBonus());
                if (attackKnockback > 0.0F) {
                    VectorUtils.knockBackA2B(this, entity, attackKnockback * 0.5, 0.2);
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

    @Override
    protected double getDefaultGravity() {
        return getVariant().gravity;
    }

    protected float getBaseKnockBack() {
        return getVariant().knockBack;
    }

    public record Variant(int id, String name, double gravity, float knockBack, ResourceLocation particleId) implements StringRepresentable {
        public static final List<Variant> VALUES = new ArrayList<>();

        public static final Variant AMETHYST = register("amethyst", -1.0, 3.25F, Confluence.asResource("amethyst_projectile"));
        public static final Variant TOPAZ = register("topaz", -1.0, 3.5F, Confluence.asResource("topaz_projectile"));
        public static final Variant SAPPHIRE = register("sapphire", -1.0, 4.0F, Confluence.asResource("sapphire_projectile"));
        public static final Variant JADE = register("emerald", -1.0, 4.25F, Confluence.asResource("emerald_projectile"));
        public static final Variant RUBY = register("ruby", -1.0, 4.75F, Confluence.asResource("ruby_projectile"));
        public static final Variant AMBER = register("amber", -1.0, 4.75F, Confluence.asResource("amber_projectile"));
        public static final Variant DIAMOND = register("diamond", -1.0, 5.5F, Confluence.asResource("diamond_projectile"));
        public static final Variant FROST = register("frost", 0.04, 0.0F, Confluence.asResource("frost_projectile"));
        public static final Variant SPARK = register("spark", 0.2, 0.0F, Confluence.asResource("spark_projectile"));
        public static final Variant THUNDER_ZAPPER = register("thunder_zapper", -1.0, 0.0F, Confluence.asResource("thunder_zapper"));

        public static final Codec<Variant> CODEC = StringRepresentable.fromValues(() -> VALUES.toArray(Variant[]::new));

        /**
         * @param rawKnockBack 换算前的击退
         */
        private static Variant register(String name, double gravity, float rawKnockBack, ResourceLocation particleId) {
            Variant variant = new Variant(VALUES.size(), name, gravity, rawKnockBack / 8.0F, particleId);
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
