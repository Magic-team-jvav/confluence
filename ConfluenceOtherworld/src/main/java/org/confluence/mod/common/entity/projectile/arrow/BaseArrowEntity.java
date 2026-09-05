package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.BaseMimic;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.mixed.IAbstractArrow;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import org.mesdag.portlib.diff.IPortEntity;
import org.mesdag.portlib.diff.IPortProjectile;
import org.mesdag.portlib.wrapper.common.extensions.IPortEnchantmentHelperExtension;
import org.mesdag.portlib.wrapper.world.entity.projectile.PortAbstractArrow;
import org.mesdag.portlib.wrapper.world.entity.projectile.PortProjectileDeflection;

import java.util.*;

/// 泰拉箭矢的通用运行实体。
///
/// PortLib 只补足 1.20 缺失的拾取物与发射武器字段；本类负责满蓄力、剩余寿命、穿透次数
/// 和已命中目标等具体玩法状态。箭矢跨区块重载后必须继续消耗原预算，不能重新获得命中次数或
/// 把同一目标当作首次命中。
public class BaseArrowEntity extends PortAbstractArrow {
    private static final String RUNTIME_TAG = "ConfluenceArrowRuntime";
    private static final int CURRENT_FORMAT_VERSION = 1;
    private ParticleEmitter emitter;

    private int penetrate;
    private final Set<UUID> havenBeen = new HashSet<>();
    private final List<ArrowHitEffect> weaponHitEffects = new ArrayList<>();
    public boolean fullPull;

    private int autoDiscardTick;
    private boolean invalidRuntimeState;

    public BaseArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        init();
    }

    public BaseArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, owner.level(), pickupItemStack, firedFromWeapon);
        init();
    }

    public BaseArrowEntity(EntityType<? extends AbstractArrow> entityType, double x, double y, double z, Level level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, x, y, z, level, pickupItemStack, firedFromWeapon);
        init();
    }

    protected void init() {
        this.autoDiscardTick = getAutoDiscardTick();
        IAbstractArrow.of(this).confluence$setDamageNotAffectedBySpeedBonus(true);
    }

    public void initializeProjectile(ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        setup(pickupItemStack, firedFromWeapon);
    }

    @Override
    public double getDefaultGravity() {
        return 0.05;
    }

    protected int getLuminance() {
        return 0;
    }

    protected int getPenetrationCount() {
        return 0;
    }

    protected double getAdditionalKnockback() {
        return 0.0;
    }

    protected int getAutoDiscardTick() {
        return 1200;
    }

    @Nullable
    protected ResourceLocation getParticleId() {
        return null;
    }

    @Nullable
    public ResourceLocation getTexturePath() {
        return null;
    }

    public boolean hasAutoDiscard() {
        return autoDiscardTick < 1200;
    }

    public void setAutoDiscard(int tick) {
        if (tick < 0) {
            throw new IllegalArgumentException("Arrow discard delay must be non-negative");
        }
        this.autoDiscardTick = tick;
    }

    @Override
    public void onAddedToWorld() {
        if (getDefaultGravity() == 0) setNoGravity(true);
        super.onAddedToWorld();
    }

    protected float capMaxSpeed(float length) {
        return Math.min(length, 3f);
    }

    protected float capMinSpeed(float f) {
        return Math.max(f, 0.5F);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        discard();
    }

    protected float getCalculatedDamage() {
        float speed = (float) this.getDeltaMovement().length();
        speed = capMaxSpeed(speed);
        double d0 = this.getBaseDamage();
        if (this.getWeaponItem() != null && this.level() instanceof ServerLevel) {
            int value = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.POWER_ARROWS, this.getWeaponItem());
            d0 *= (value * 0.1f + 1.0f);
        }
        speed = capMinSpeed(speed);
        int i = Mth.ceil(Mth.clamp((double) speed * d0, 0.0, 2.147483647E9));
        if (this.isCritArrow()) {
            long j = this.random.nextInt(i / 2 + 2);
            i = (int) Math.min(j + (long) i, 2147483647L);
        }
        return i;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        PortProjectileDeflection deflection = IPortEntity.of(entity).deflection(this);
        if (deflection != PortProjectileDeflection.NONE) {
            IPortProjectile portProjectile = IPortProjectile.of(this);
            if (entity != portProjectile.portlib$getLastDeflectedBy() && deflect(deflection, entity, entity, false)) {
                portProjectile.portlib$setLastDeflectedBy(entity);
            }
            return;
        }
        if (!(entity instanceof LivingEntity living)) {
            super.onHitEntity(result);
            return;
        }
        if (!havenBeen.add(living.getUUID())) return;
        DamageSource damageSource = getDamageSource();
        if (entity.hurt(damageSource, getCalculatedDamage())) {
            playSound(getSound(), 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
            Entity owner = getOwner();
            boolean reflectedByMimic = owner instanceof BaseMimic;
            // 宝箱怪反射的箭只造成折半后的直接伤害，不附加箭种减益或武器附魔效果。
            if (!reflectedByMimic) {
                doPostHurtEffects(living);
            }
            if (!level().isClientSide) {
                living.setArrowCount(living.getArrowCount() + 1);
            }
            doKnockback(living, damageSource);
            double additionalKnockback = getAdditionalKnockback();
            if (additionalKnockback > 0.0) {
                Vec3 knockback = getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(0.6 * additionalKnockback);
                if (knockback.lengthSqr() > 0.0) living.push(knockback.x, 0.1, knockback.z);
            }
            if (!reflectedByMimic && !level().isClientSide && owner instanceof LivingEntity) {
                IPortEnchantmentHelperExtension.doPostAttackEffects((ServerLevel) level(), entity, damageSource);
            }
            if (living != owner && living instanceof Player && owner instanceof ServerPlayer player && !isSilent()) {
                player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
            }
            penetrate++;
            if (!canPenetrate()) discard();
        } else {
            setDeltaMovement(getDeltaMovement().scale(-0.1D));
            setYRot(getYRot() + 180.0F);
            this.yRotO += 180.0F;
            if (!level().isClientSide && getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (pickup == Pickup.ALLOWED) spawnAtLocation(getPickupItem(), 0.1F);
            }
        }
    }

    @Override
    public void onDeflection(@Nullable Entity entity, boolean deflectedByPlayer) {
        if (entity instanceof BaseMimic) {
            setOwner(entity);
            setBaseDamage(getBaseDamage() * 0.5D);
            penetrate = getPenetrationCount();
        }
    }

    protected DamageSource getDamageSource() {
        return damageSources().arrow(this, getOwner());
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        if (getOwner() instanceof LivingEntity owner) {
            onHit(owner, living, fullPull);
            weaponHitEffects.forEach(effect -> effect.apply(owner, living, fullPull));
        }
        super.doPostHurtEffects(living);
    }

    protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {}

    public final void addWeaponHitEffect(ArrowHitEffect effect) {
        weaponHitEffects.add(effect);
    }

    @FunctionalInterface
    public interface ArrowHitEffect {
        void apply(LivingEntity owner, LivingEntity target, boolean fullPull);
    }

    protected SoundEvent getSound() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return Items.ARROW.getDefaultInstance();
    }

    @Override
    public void tick() {
        if (invalidRuntimeState && !level().isClientSide) {
            discard();
            return;
        }
        if (!level().isClientSide && tickCount > autoDiscardTick) discard();
        super.tick();
        if (level().isClientSide && emitter == null) {
            ResourceLocation location = getParticleId();
            if (location != null) {
                this.emitter = new ParticleEmitter(level(), position(), location);
                emitter.attachEntity(this);
                emitter.hideOutline = true;
                MolangParticleEngine.INSTANCE.addEmitter(emitter);
            }
        }
    }

    public boolean canPenetrate() {
        return penetrate + 1 <= getPenetrationCount();
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (getOwner() instanceof BaseNPC npc && target instanceof LivingEntity living && !npc.canAttack(living))
            return false;
        return super.canHitEntity(target) && !havenBeen.contains(target.getUUID());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag entityTag) {
        super.addAdditionalSaveData(entityTag);
        CompoundTag runtimeTag = new CompoundTag();
        runtimeTag.putInt("Version", CURRENT_FORMAT_VERSION);
        runtimeTag.putBoolean("FullPull", fullPull);
        runtimeTag.putInt("RemainingLifetime", Math.max(autoDiscardTick - tickCount, 0));
        runtimeTag.putInt("PenetratedCount", Math.max(penetrate, 0));

        ListTag targetsTag = new ListTag();
        for (UUID targetUuid : havenBeen) {
            targetsTag.add(NbtUtils.createUUID(targetUuid));
        }
        runtimeTag.put("HitTargets", targetsTag);
        entityTag.put(RUNTIME_TAG, runtimeTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag entityTag) {
        super.readAdditionalSaveData(entityTag);
        resetRuntimeState();
        restoreWeaponHitEffects();
        if (!entityTag.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            return;
        }
        try {
            CompoundTag runtimeTag = entityTag.getCompound(RUNTIME_TAG);
            requireTag(runtimeTag, "Version", Tag.TAG_INT);
            if (runtimeTag.getInt("Version") != CURRENT_FORMAT_VERSION) {
                throw new IllegalArgumentException("Unsupported arrow runtime state version");
            }
            requireTag(runtimeTag, "FullPull", Tag.TAG_BYTE);
            requireTag(runtimeTag, "RemainingLifetime", Tag.TAG_INT);
            requireTag(runtimeTag, "PenetratedCount", Tag.TAG_INT);
            requireTag(runtimeTag, "HitTargets", Tag.TAG_LIST);
            int remainingLifetime = runtimeTag.getInt("RemainingLifetime");
            int penetratedCount = runtimeTag.getInt("PenetratedCount");
            if (remainingLifetime < 0) {
                throw new IllegalArgumentException("Arrow remaining lifetime is out of range");
            }
            if (penetratedCount < 0) {
                throw new IllegalArgumentException("Arrow penetrated count is out of range");
            }
            Tag rawTargets = runtimeTag.get("HitTargets");
            if (!(rawTargets instanceof ListTag targetsTag) || !targetsTag.isEmpty() && targetsTag.getElementType() != Tag.TAG_INT_ARRAY) {
                throw new IllegalArgumentException("Arrow hit targets must be a UUID list");
            }
            for (Tag targetTag : targetsTag) {
                UUID targetUuid = NbtUtils.loadUUID(targetTag);
                if (!havenBeen.add(targetUuid)) {
                    throw new IllegalArgumentException("Arrow hit target UUIDs must be unique");
                }
            }
            this.fullPull = runtimeTag.getBoolean("FullPull");
            this.autoDiscardTick = remainingLifetime;
            this.penetrate = penetratedCount;
            this.tickCount = 0;
        } catch (RuntimeException exception) {
            resetRuntimeState();
            this.invalidRuntimeState = true;
        }
    }

    /// 缺少当前格式时保留安全默认值；存在但损坏的当前格式则在下一服务端 tick 销毁。
    private void resetRuntimeState() {
        this.fullPull = false;
        this.autoDiscardTick = Math.max(getAutoDiscardTick(), 0);
        this.penetrate = 0;
        this.havenBeen.clear();
        this.weaponHitEffects.clear();
        this.invalidRuntimeState = false;
    }

    private void restoreWeaponHitEffects() {
        ItemStack weapon = getWeaponItem();
        if (weapon == null || weapon.isEmpty()) return;
        if (weapon.getItem() instanceof BaseTerraBowItem bow) {
            bow.modifyArrowEntity(this);
        } else if (weapon.getItem() instanceof BaseTerraRepeaterItem repeater) {
            repeater.modifyArrowEntity(this);
        }
    }

    private static void requireTag(CompoundTag tag, String key, int expectedType) {
        if (!tag.contains(key, expectedType)) {
            throw new IllegalArgumentException("Missing or invalid arrow runtime field: " + key);
        }
    }
}
