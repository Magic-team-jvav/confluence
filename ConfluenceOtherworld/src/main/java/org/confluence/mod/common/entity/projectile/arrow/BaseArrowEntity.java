package org.confluence.mod.common.entity.projectile.arrow;

import PortLib.extensions.net.minecraft.world.item.enchantment.EnchantmentHelper.PortEnchantmentHelperExtension;
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
import org.confluence.mod.mixed.IAbstractArrow;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import org.mesdag.portlib.wrapper.world.entity.projectile.PortAbstractArrow;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BaseArrowEntity extends PortAbstractArrow {
    private ParticleEmitter emitter;

    private int penetrate;
    private final Set<UUID> havenBeen = new HashSet<>();
    public boolean fullPull;

    private int autoDiscardTick;

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

    private void init() {
        this.autoDiscardTick = getAutoDiscardTick();
        IAbstractArrow.of(this).confluence$setDamageNotAffectedBySpeedBonus(true);
    }

    // region getters

    @Override
    public double getDefaultGravity() {
        return 0.05;
    }

    protected int getLuminance() {
        return 0;
    }

    protected float getSpeedFactor() {
        return 1;
    }

    protected int getPenetrationCount() {
        return 0;
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

    // endregion

    public boolean hasAutoDiscard() {
        return autoDiscardTick < 1200;
    }

    public void setAutoDiscard(int tick) {
        this.autoDiscardTick = tick;
    }

    @Override
    public void onAddedToWorld() {
        if (getDefaultGravity() == 0) this.setNoGravity(true);
        super.onAddedToWorld();
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        this.setDeltaMovement(getDeltaMovement().scale(getSpeedFactor()));
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
        if (!(entity instanceof LivingEntity living)) {
            super.onHitEntity(result);
            return;
        }
        if (!havenBeen.add(living.getUUID())) return;
        Entity owner = this.getOwner();
        DamageSource damagesource = this.damageSources().arrow(this, owner != null ? owner : this);
        if (entity.hurt(damagesource, getCalculatedDamage())) {
            this.playSound(getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.doPostHurtEffects(living);
            if (!this.level().isClientSide) living.setArrowCount(living.getArrowCount() + 1);
            this.doKnockback(living, damagesource);
            if (!this.level().isClientSide && owner instanceof LivingEntity) {
                PortEnchantmentHelperExtension.doPostAttackEffects((ServerLevel) level(), entity, damagesource);
            }
            if (living != owner && living instanceof Player && owner instanceof ServerPlayer player && !this.isSilent()) {
                player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
            }
            penetrate++;
            if (!canPenetrate()) discard();
        } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
            if (!this.level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (this.pickup == Pickup.ALLOWED) this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }
        }
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        if (getOwner() instanceof LivingEntity owner) {
            onHit(owner, living, fullPull);
        }
        super.doPostHurtEffects(living);
    }

    protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {}

    private static SoundEvent getSound() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return Items.ARROW.getDefaultInstance();
    }

    @Override
    public void tick() {
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
        return super.canHitEntity(target) && !this.havenBeen.contains(target.getUUID());
    }
}
