package org.confluence.mod.common.entity.projectile.mana;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.color.FloatRGB;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseManaStaffProjectileEntity extends AbstractManaProjectile {
    protected static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(BaseManaStaffProjectileEntity.class, EntityDataSerializers.INT);
    protected int penetrateCount = 2;

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
        super.defineSynchedData(builder.define(DATA_VARIANT_ID, 0));
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

        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        if (!isNoGravity()) {
            setDeltaMovement(vec3.x, vec3.y - getGravity(), vec3.z);
        }
        setPos(offX, offY, offZ);

        if (level().isClientSide && (emitter == null || emitter.isRemoved())) {
            Variant variant = getVariant();
            ResourceLocation particleId;
            MolangExp expression = MolangExp.EMPTY;
            if (variant == Variant.FROST) {
                particleId = Confluence.asResource("frost_projectile");
            } else if (variant == Variant.SPARK) {
                particleId = Confluence.asResource("spark_projectile");
            } else if (variant == Variant.THUNDER_ZAPPER) {
                particleId = Confluence.asResource("thunder_zapper");
            } else {
                particleId = Confluence.asResource("base_mana_staff_projectile");
                expression = new MolangExp(Map.of(
                        "variable.red", Float.toString(variant.color.red()),
                        "variable.green", Float.toString(variant.color.green()),
                        "variable.blue", Float.toString(variant.color.blue())
                ));
            }
            this.emitter = new ParticleEmitter(level(), position(), particleId, expression);
            emitter.attachEntity(this);
            PSGameClient.LOADER.addEmitter(emitter, false);
        }

        if (tickCount > 200) discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (doPenetrateCheck(entity)) {
            doHurtAndKnockback(entity, 0.5, 0.2);
            doDiscardInMaxPenetrate(penetrateCount);
        }
    }

    @Override
    protected boolean doHurtAndKnockback(Entity target, double knockbackStrength, double knockbackMotionY) {
        float damage = getCalculatedDamage() * (1.0F + getAttackBonus());
        if (target.hurt(getDamagesource(), damage)) {
            float attackKnockback = getBaseKnockBack() * (1.0F + getKnockbackBonus());
            if ((attackKnockback > 0.0F && knockbackStrength > 0) || knockbackMotionY > 0) {
                VectorUtils.knockBackA2B(this, target, attackKnockback * knockbackStrength, knockbackMotionY);
            }
            afterHurtTarget(target);
            return true;
        }
        return false;
    }

    protected void afterHurtTarget(Entity target) {}

    protected float getAttackBonus() {
        return 0.0F;
    }

    protected float getKnockbackBonus() {
        return 0.0F;
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

    public record Variant(int id, String name, double gravity, float knockBack, FloatRGB color) implements StringRepresentable {
        public static final List<Variant> VALUES = new ArrayList<>();

        public static final Variant AMETHYST = register("amethyst", -1.0, 3.25F, 0.91765F, 0.41961F, 1F);
        public static final Variant TOPAZ = register("topaz", -1.0, 3.5F, 1F, 0.81569F, 0F);
        public static final Variant SAPPHIRE = register("sapphire", -1.0, 4.0F, 0.30196F, 0.65098F, 1F);
        public static final Variant JADE = register("jade", -1.0, 4.25F, 0.12941F, 0.72157F, 0.45098F);
        public static final Variant RUBY = register("ruby", -1.0, 4.75F, 0.92157F, 0.38824F, 0.45882F);
        public static final Variant AMBER = register("amber", -1.0, 4.75F, 0.98824F, 0.75686F, 0.17647F);
        public static final Variant DIAMOND = register("diamond", -1.0, 5.5F, 0.60392F, 1F, 0.86667F);
        public static final Variant FROST = register("frost", 0.04, 0.0F, 0, 0, 0);
        public static final Variant SPARK = register("spark", 0.04, 0.0F, 0, 0, 0);
        public static final Variant THUNDER_ZAPPER = register("thunder_zapper", -1.0, 0.0F, 0, 0, 0);

        public static final Codec<Variant> CODEC = StringRepresentable.fromValues(() -> VALUES.toArray(new Variant[0])).mapResult(new Codec.ResultFunction<>() {
            @Override
            public <T> DataResult<Pair<Variant, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<Variant, T>> a) {
                if (a.isError()) {
                    return DataResult.success(new Pair<>(JADE, input), Lifecycle.stable());
                }
                return a;
            }

            @Override
            public <T> DataResult<T> coApply(DynamicOps<T> ops, Variant input, DataResult<T> t) {
                return t;
            }
        });

        /**
         * @param rawKnockBack 换算前的击退
         */
        private static Variant register(String name, double gravity, float rawKnockBack, float red, float green, float blue) {
            Variant variant = new Variant(VALUES.size(), name, gravity, rawKnockBack / 8.0F, new FloatRGB(red, green, blue));
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
