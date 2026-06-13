package org.confluence.mod.common.entity.projectile.range.arrow;

import PortLib.extensions.net.minecraft.world.item.enchantment.EnchantmentHelper.PortEnchantmentHelperExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.confluence.mod.mixed.IAbstractArrow;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BaseArrowEntity extends AbstractArrow {
    protected float minSpeedAttackFactor = 0.5f; // 速度影响伤害的最小系数
    @Nullable Item arrowItem;
    private ParticleEmitter emitter;

    public static class Factory {
        public String path;
        public Supplier<Builder> attr;

        Factory() {}

        public static Factory create(String path, Supplier<Builder> type) {
            Factory t = new Factory();
            t.path = path;
            t.attr = type;
            return t;
        }
    }

    private static final EntityDataAccessor<String> TEXTURE_PATH = SynchedEntityData.defineId(BaseArrowEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_PARTICLE_ID = SynchedEntityData.defineId(BaseArrowEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_LUMINANCE = SynchedEntityData.defineId(BaseArrowEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_GRAVITY = SynchedEntityData.defineId(BaseArrowEntity.class, EntityDataSerializers.FLOAT);
    public String texturePath = "";
    private int penetrate = 0;
    private final List<LivingEntity> havenBeen = new ArrayList<>();//标记不能重复穿透
    public @NotNull Builder modify = new Builder();
    private Factory baseArrowFactory;
    public boolean fullPull = false;

    public BaseArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        IAbstractArrow.of(this).confluence$setDamageNotAffectedBySpeedBonus(true);
    }

    /// 自定义箭矢
    ///
    /// @param owner           发射者
    /// @param pickupItemStack 捡起的物品
    /// @param firedFromWeapon 发射的武器
    /// @param arrow           预定义的箭的类型
    public BaseArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon, @Nullable BaseTerraArrowItem arrow) {
        super(entityType, owner, owner.level(), pickupItemStack, firedFromWeapon);
        this.arrowItem = arrow;
        this.baseArrowFactory = arrow == null ? null : arrow.getModifier();
        if (baseArrowFactory == null) { // 这时候应该为实体的木箭转化
            this.modify = new Builder();
        } else {
            this.modify = baseArrowFactory.attr.get();
        }
        IAbstractArrow.of(this).confluence$setDamageNotAffectedBySpeedBonus(true);
    }

    /// 自定义箭矢
    ///
    /// @param owner           发射者
    /// @param pickupItemStack 捡起的物品
    /// @param firedFromWeapon 发射的武器
    /// @param arrow           预定义的箭的类型
    /// @param modifyConsumer  属性额外修饰
    public BaseArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon, @NotNull BaseTerraArrowItem arrow, BaseTerraArrowItem.ModifyArrowBuilder modifyConsumer) {
        this(entityType, owner, pickupItemStack, firedFromWeapon, arrow);
        if (modifyConsumer != null) {
            modifyConsumer.applyModifiers(modify);
        }
        if ((modify.type & Tag.auto_discard) != 0) {
            this.pickup = Pickup.DISALLOWED;
        }
    }

    /// 自定义箭矢
    ///
    /// @param pickupItemStack 捡起的物品
    /// @param firedFromWeapon 发射的武器
    /// @param arrow           预定义的箭的类型
    public BaseArrowEntity(EntityType<? extends AbstractArrow> entityType, double x, double y, double z, Level level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon, @Nullable BaseTerraArrowItem arrow) {
        super(entityType, x, y, z, level, pickupItemStack, firedFromWeapon);
        this.arrowItem = arrow;
        this.baseArrowFactory = arrow == null ? null : arrow.getModifier();
        if (baseArrowFactory == null) { // 这时候应该为实体的木箭转化 -- 保留注释
            this.modify = new Builder();
        } else {
            this.modify = baseArrowFactory.attr.get();
        }
        IAbstractArrow.of(this).confluence$setDamageNotAffectedBySpeedBonus(true);
    }

    public void modify(Consumer<Builder> consumer) {
        consumer.accept(modify);
    }

    @Override
    public void onAddedToLevel() {
        if ((modify.type & Tag.no_gravity) != 0) this.setNoGravity(true);
        if (baseArrowFactory != null) {
            entityData.set(TEXTURE_PATH, baseArrowFactory.path);
            this.texturePath = baseArrowFactory.path;
        }
        if (modify.particleId != null) {
            entityData.set(DATA_PARTICLE_ID, modify.particleId.toString());
        }
        entityData.set(DATA_LUMINANCE, modify.luminance);
        entityData.set(DATA_GRAVITY, modify.gravity);

        super.onAddedToLevel();
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        this.setDeltaMovement(getDeltaMovement().scale(modify.speedFactor * (level().isRaining() ? modify.speedUpInRain : 1)));
    }

    protected float getSpeedDamageFactor(float length) {
        return Math.min(length, 3f);
    }

    protected float getSpeedDamageMinFactor(float f) {
        return Math.max(f, minSpeedAttackFactor);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        discard(); // todo 叶绿箭落地要弹不消失
    }

    @Override
    protected double getDefaultGravity() {
        return modify.gravity;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();

        if (!(entity instanceof LivingEntity living)) {
            super.onHitEntity(result);
            return;
        }
        if (havenBeen.contains(living)) return;
        float f = (float) this.getDeltaMovement().length();
        // 限制速度增伤
        f = getSpeedDamageFactor(f);
        Entity entity1 = this.getOwner();
        DamageSource damagesource = this.damageSources().arrow(this, entity1 != null ? entity1 : this);

        // 附魔增伤
        double d0 = this.getBaseDamage();
        if (this.getWeaponItem() != null) {
            Level var9 = this.level();
            if (var9 instanceof ServerLevel) {
                int value = PortEnchantmentHelperExtension.getEnchantmentLevel(Enchantments.POWER, this.getWeaponItem());
                d0 *= (value * 0.1f + 1.0f);
            }
        }

        //速度修正系数影响的最小速度值
        f = getSpeedDamageMinFactor(f);
        int i = Mth.ceil(Mth.clamp((double) f * d0, 0.0, 2.147483647E9));

        //暴击增伤
        if (this.isCritArrow()) {
            long j = this.random.nextInt(i / 2 + 2);
            i = (int) Math.min(j + (long) i, 2147483647L);
        }
        // 雨天伤害修正
        float fi = this.level().isRaining() ? modify.damageInRain + i : i;

        // 重复穿透
        havenBeen.add(living);

        if (entity.hurt(damagesource, fi + modify.base_damage)) {
            if ((modify.type & Tag.cause_fire) != 0) {
                entity.setRemainingFireTicks(this.getRemainingFireTicks() + modify.causeFireTick - tickCount);
            }
            this.playSound(getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.doPostHurtEffects(living);
            if (!this.level().isClientSide) {
                living.setArrowCount(living.getArrowCount() + 1);
            }
            //todo 击退
            this.doKnockback(living, damagesource);
            if (modify.knockBack > 0) {
                double d1 = modify.knockBack;
                Vec3 vec3 = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale(0.6 * d1);
                if (vec3.lengthSqr() > 0.0D) {
                    living.push(vec3.x, 0.1D, vec3.z);
                }
            }

            //箭药水效果
            if (!this.level().isClientSide && entity1 instanceof LivingEntity) {
                PortEnchantmentHelperExtension.doPostAttackEffects((ServerLevel) level(), entity, damagesource);
            }

            //命中自己
            if (living != entity1 && living instanceof Player && entity1 instanceof ServerPlayer player && !this.isSilent()) {
                player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
            }
            penetrate++;
            if (!canPenetrate()) {
                discard();
            }
        } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
            if (!this.level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }
            }
        }
    }

    @Override
    public double getBaseDamage() {
        return super.getBaseDamage();
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        if (getOwner() instanceof LivingEntity owner) {
            modify.onHitEffects.forEach(effect -> effect.applyAll(owner, living));
            if (fullPull) {
                modify.fullPullHitEffects.forEach(effect -> effect.applyAll(owner, living));
            }
        }
        super.doPostHurtEffects(living);
    }

    private static SoundEvent getSound() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        // 构造延迟
        return Items.ARROW.getDefaultInstance();
    }

    @Override
    public void tick() {
        if (!level().isClientSide && tickCount > modify.auto_discard_tick) discard();

        super.tick();

        if (level().isClientSide && emitter == null) {
            String s = entityData.get(DATA_PARTICLE_ID);
            if (!s.isEmpty()) {
                ResourceLocation location = ResourceLocation.tryParse(s);
                if (location != null) {
                    this.emitter = new ParticleEmitter(level(), position(), location);
                    emitter.attachEntity(this);
                    emitter.hideOutline = true;
                    MolangParticleEngine.INSTANCE.addEmitter(emitter);
                }
            }
        }
    }

    public boolean canPenetrate() {
        return penetrate + 1 <= modify.penetration_count;
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder
                .define(TEXTURE_PATH, "")
                .define(DATA_PARTICLE_ID, "")
                .define(DATA_LUMINANCE, 0)
                .define(DATA_GRAVITY, 0.05F)
        );
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (level().isClientSide) {
            this.texturePath = entityData.get(TEXTURE_PATH);
            this.modify.luminance = entityData.get(DATA_LUMINANCE);
            this.modify.gravity = entityData.get(DATA_GRAVITY);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        if (this.getPickupItemStackOrigin().isEmpty()) {
            if (this.arrowItem != null) {
                this.setPickupItemStack(arrowItem.getDefaultInstance());
            } else {
                this.setPickupItemStack(getDefaultPickupItem());
            }
        }
        if (modify.particleId != null) {
            tag.putString("ParticleId", modify.particleId.toString());
        }
        tag.putInt("Luminance", modify.luminance);
        tag.putFloat("Gravity", modify.gravity);
        super.addAdditionalSaveData(tag);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && !this.havenBeen.contains(target);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (getPickupItem().getItem() instanceof BaseTerraArrowItem arrow && arrow.getModifier() != null) {
            texturePath = arrow.getModifier().path;
            entityData.set(TEXTURE_PATH, texturePath);
        }
        String string = tag.getString("ParticleId");
        if (!string.isEmpty()) {
            entityData.set(DATA_PARTICLE_ID, string);
        }
        entityData.set(DATA_LUMINANCE, tag.getInt("Luminance"));
        entityData.set(DATA_GRAVITY, tag.getFloat("Gravity"));
    }

    @Override
    protected float getWaterInertia() {
        return modify.speedUpInWater * super.getWaterInertia();
    }


    /**
     * 能力表
     **/
    public static class Tag {
        public static final int penetration = 1;//可穿透
        public static final int no_gravity = 2;//无重力
        public static final int auto_discard = 4;//超过时间自动消失
        public static final int cause_fire = 8;//火焰附加

    }

    public static class Builder {
        private int type = 0;
        public int penetration_count = 0;
        private float gravity = 0.05F;
        private int auto_discard_tick = 1200;
        public float base_damage = 2;
        private float speedFactor = 1;
        private float knockBack = 0;
        private int causeFireTick = 0;
        private int luminance = 0;

        // 雨天增益
        private float speedUpInRain = 1;
        private float speedUpInWater = 1;
        private float damageInRain = 0;

        private BaseTerraArrowItem transformArrow = null;
        public List<EffectStrategyComponent> onHitEffects = new ArrayList<>();
        public List<EffectStrategyComponent> fullPullHitEffects = new ArrayList<>();
        private @Nullable ResourceLocation particleId;

        public Builder addFullPullHitEffect(EffectStrategyComponent component) {
            this.fullPullHitEffects.add(component);
            return this;
        }

        public Builder addOnHitEffect(EffectStrategyComponent component) {
            this.onHitEffects.add(component);
            return this;
        }

        public Builder setDamage(float damage) {//基本伤害
            base_damage = damage;
            return this;
        }

        public Builder setKnockBack(float factor) {//击退修正系数
            this.knockBack = factor;
            return this;
        }

        public Builder setSpeedUpInRain(float speedUp) {//水下和雨天加速
            this.speedUpInRain = speedUp;
            return this;
        }

        public Builder setSpeedInertiaInWater(float speedUp) {//水下加速
            this.speedUpInWater = speedUp;
            return this;
        }

        public Builder setDamageInRain(float damage) {//水下和雨天伤害修正
            this.damageInRain = damage;
            return this;
        }

        public Builder setPenetration(int count) {//穿透次数
            type |= Tag.penetration;
            penetration_count = count;
            if (count > 1) {
                type |= Tag.auto_discard;
                auto_discard_tick = Math.min(auto_discard_tick, 120);
            }
            return this;
        }

        public Builder setGravity(float gravity) {//重力
            if (gravity == 0.0F) type |= Tag.no_gravity;
            this.gravity = gravity;
            return this;
        }

        public Builder setAutoDiscard(int tick) {//消失tick
            type |= Tag.auto_discard;
            auto_discard_tick = tick;
            return this;
        }

        public Builder setSpeedFactor(float factor) {//初始速度修正系数
            speedFactor = factor;
            return this;
        }

        public Builder setCauseFire(int tick) {//初始火焰增加
            type |= Tag.cause_fire;
            this.causeFireTick = tick;
            this.setLuminance(Math.max(this.luminance, 8));
            return this;
        }

        public Builder setTransformArrow(BaseTerraArrowItem arrow) {
            this.transformArrow = arrow;
            return this;
        }

        public Builder setLuminance(int luminance) {
            this.luminance = luminance;
            return this;
        }

        public Builder setParticleId(ResourceLocation particleId) {
            this.particleId = particleId;
            return this;
        }

        public int getLuminance() {
            return luminance;
        }

        public int getType() {
            return type;
        }

        @Nullable
        public BaseTerraArrowItem getTransformArrow() {
            return transformArrow;
        }
    }
}
