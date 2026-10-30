package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.joml.Matrix4x3f;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.Map;

/// 星体研究汇编的法术射弹。
///
/// 分两种用途：
/// - {@link Variant#BASE} 是基础弹幕，只造成伤害；
/// - 其余五种是「附加效果弹幕」，命中后额外赋予对应的负面状态。
///
/// 外观由两层粒子叠加：
/// - 汇聚成团的符文（贴图取原版 `asciillager_stroke` 的 39 个符文，每颗粒子随机挑一个），
///   并按弹幕对应的效果色染色；
/// - 向外发散的星座粒子，每颗粒子随机挑一格星座图案。
public class StellarStudiesProjectile extends AbstractManaProjectile {
    protected static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(StellarStudiesProjectile.class, EntityDataSerializers.INT);
    /// 汇聚型符文粒子。
    public static final ResourceLocation RUNE_PARTICLE = Confluence.asResource("stellar_studies_projectile");
    /// 发散型星座粒子。
    public static final ResourceLocation CONSTELLATION_PARTICLE = Confluence.asResource("stellar_studies_constellation");
    /// 基础弹幕的存在时间。
    private static final int MAX_AGE = 60;

    /// 发散用的星座粒子发射器，与父类的符文发射器分开持有。
    private ParticleEmitter constellationEmitter;

    public StellarStudiesProjectile(EntityType<StellarStudiesProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    public StellarStudiesProjectile(LivingEntity living, Variant variant) {
        this(ModEntities.STELLAR_STUDIES_PROJECTILE.get(), living.level());
        setOwner(living);
        setVariant(variant);
        setPos(living.getX(), living.getEyeY() - 0.1, living.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder.define(DATA_VARIANT, Variant.BASE.ordinal()));
    }

    public void setVariant(Variant variant) {
        entityData.set(DATA_VARIANT, variant.ordinal());
    }

    public Variant getVariant() {
        return Variant.byId(entityData.get(DATA_VARIANT));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (level().isClientSide) {
            // 粒子要等同步数据落地之后再建，否则读档/远距离生成时可能挑错贴图。
            Variant variant = getVariant();
            if (emitter == null || emitter.isRemoved()) {
                /// 效果色通过自定义 molang 变量交给粒子，由 `particle_appearance_tinting` 给随机符文染色。
                this.emitter = new ParticleEmitter(level(), position(), RUNE_PARTICLE, new MolangExp(Map.of(
                        "variable.red", Float.toString(variant.red),
                        "variable.green", Float.toString(variant.green),
                        "variable.blue", Float.toString(variant.blue)
                )));
                initEmitter(emitter);
            }
            if (constellationEmitter == null || constellationEmitter.isRemoved()) {
                this.constellationEmitter = new ParticleEmitter(level(), position(), CONSTELLATION_PARTICLE);
                initEmitter(constellationEmitter);
            }
        }
        doSimpleMove();
        doAgeCheck(MAX_AGE);
    }

    private void initEmitter(ParticleEmitter particleEmitter) {
        particleEmitter.attachEntity(this);
        particleEmitter.hideOutline = true;
        particleEmitter.setLocalSpace(new Matrix4x3f().setTranslation(0, getBbHeight() * 0.5F, 0));
        MolangParticleEngine.INSTANCE.addEmitter(particleEmitter);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!level().isClientSide) discardInTicks(1);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (!doPenetrateCheck(entity)) return;
        doHurtAndKnockback(entity, 0.3, 0.15);
        // 基础弹幕与附加弹幕几乎同帧命中，目标此时还在受伤无敌帧里，hurt() 会失败；
        // 但「赋予效果」不应该被无敌帧吃掉，所以这里不看伤害是否生效，命中即上状态。
        if (entity instanceof LivingEntity living) {
            applyEffect(living);
        }
        doDiscardInMaxPenetrate(1);
    }

    private void applyEffect(LivingEntity target) {
        switch (getVariant()) {
            case BASE -> {}
            case LEVITATION -> target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, Variant.LEVITATION.durationTicks));
            case POISON -> target.addEffect(new MobEffectInstance(MobEffects.POISON, Variant.POISON.durationTicks));
            case FIRE -> target.igniteForSeconds(Variant.FIRE.durationTicks / 20.0F);
            case WITHER -> target.addEffect(new MobEffectInstance(MobEffects.WITHER, Variant.WITHER.durationTicks));
            case SLOW -> target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Variant.SLOW.durationTicks));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Variant", getVariant().getSerializedName());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setVariant(Variant.byName(compound.getString("Variant")));
    }

    /// 星体研究汇编的六种弹幕。
    ///
    /// 颜色决定汇聚符文的染色，也对应各自的效果；符文图案本身每颗粒子随机取一条。
    public enum Variant implements StringRepresentable {
        /// 基础弹幕，只造成伤害。
        BASE("base", 0, 1, 0xFFD776),
        /// 第一张书页赋予的漂浮弹幕。
        LEVITATION("levitation", 60, 1, 0x76FFC1),
        /// 第二张书页赋予的中毒弹幕。
        POISON("poison", 100, 2, 0x5CC612),
        /// 第三张书页赋予的着火弹幕。
        FIRE("fire", 100, 3, 0xC62012),
        /// 第四张书页赋予的凋零弹幕。
        WITHER("wither", 100, 4, 0x43345B),
        /// 第五张书页赋予的减速弹幕。
        SLOW("slow", 100, 5, 0x8DB6B6);

        private final String name;
        /// 命中后赋予的状态时长（tick）。
        public final int durationTicks;
        /// 需要先填充几张书页才会解锁。
        public final int requiredPages;
        /// 染色用的 0xRRGGBB。
        public final int color;
        /// 拆成粒子染色的三个 0~1 通道。
        public final float red;
        public final float green;
        public final float blue;

        Variant(String name, int durationTicks, int requiredPages, int color) {
            this.name = name;
            this.durationTicks = durationTicks;
            this.requiredPages = requiredPages;
            this.color = color;
            this.red = ((color >> 16) & 0xFF) / 255.0F;
            this.green = ((color >> 8) & 0xFF) / 255.0F;
            this.blue = (color & 0xFF) / 255.0F;
        }

        public static Variant byId(int id) {
            Variant[] values = values();
            return id >= 0 && id < values.length ? values[id] : BASE;
        }

        public static Variant byName(String name) {
            for (Variant variant : values()) {
                if (variant.name.equals(name)) return variant;
            }
            return BASE;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
