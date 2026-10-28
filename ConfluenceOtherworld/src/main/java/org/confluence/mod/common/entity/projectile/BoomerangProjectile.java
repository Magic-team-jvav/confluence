package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.item.boomerang.BoomerangItem;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4x3f;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import org.mesdag.portlib.event.entity.PortProjectileImpactEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BoomerangProjectile extends Projectile {
    private static final EntityDataAccessor<ItemStack> DATA_WEAPON = SynchedEntityData.defineId(BoomerangProjectile.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> DATA_RETURNING = SynchedEntityData.defineId(BoomerangProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(BoomerangProjectile.class, EntityDataSerializers.FLOAT);
    /// 本颗投掷物携带的符文序号，-1 表示该武器不用符文。同步给客户端是为了让粒子挑对贴图。
    private static final EntityDataAccessor<Integer> DATA_RUNE = SynchedEntityData.defineId(BoomerangProjectile.class, EntityDataSerializers.INT);

    private final Set<UUID> hitEntities = new HashSet<>();
    /// 仅用于错开不同回旋镖的视觉摆动相位，不参与服务端运动计算。
    private final int visualRotationOffset = random.nextInt(114514);
    /// 跟随本颗投掷物的符文粒子发射器，只在客户端持有。
    private @Nullable ParticleEmitter runeEmitter;
    private float backSpeed = 1.5F;
    private int forwardTicks = 15;
    private int penetration = 1;
    private boolean fire;

    public BoomerangProjectile(EntityType<? extends BoomerangProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_WEAPON, ItemStack.EMPTY);
        entityData.define(DATA_RETURNING, false);
        entityData.define(DATA_DAMAGE, 1.0F);
        entityData.define(DATA_RUNE, -1);
    }

    public void configure(LivingEntity owner, ItemStack weapon, BoomerangItem.Settings settings) {
        configure(owner, weapon, settings.damage(), settings.flySpeed(), settings.backSpeed(), settings.forwardTicks(), settings.penetration());
        this.fire = settings.fire();
        if (fire) setSecondsOnFire(4);
    }

    public void configure(LivingEntity owner, ItemStack weapon, float damage, float flySpeed, float backSpeed, int forwardTicks, int penetration) {
        setOwner(owner);
        setPos(owner.getX(), owner.getEyeY() - 0.15, owner.getZ());
        entityData.set(DATA_WEAPON, weapon.copyWithCount(1));
        float resolvedDamage = damage * (float) owner.getAttributeValue(LibAttributes.getAttackDamage());
        entityData.set(DATA_DAMAGE, Math.max(0, resolvedDamage));
        this.backSpeed = Math.max(0.01F, backSpeed);
        this.forwardTicks = Math.max(1, forwardTicks);
        this.penetration = Math.max(1, penetration);
        this.fire = false;
        entityData.set(DATA_RUNE, -1);
        setNoGravity(true);
    }

    public ItemStack getWeapon() {
        return entityData.get(DATA_WEAPON);
    }

    /// 由武器在发射瞬间调用一次；-1 表示这把回旋镖不带符文。
    public void setRune(int rune) {
        entityData.set(DATA_RUNE, rune);
    }

    public int getRune() {
        return entityData.get(DATA_RUNE);
    }

    public boolean belongsTo(Player player) {
        Entity owner = getOwner();
        return owner != null && owner.getUUID().equals(player.getUUID());
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        Entity owner = getOwner();
        return LibEntityUtils.canHit(owner, target)
                && !hitEntities.contains(LibEntityUtils.dedupeIdentity(target).getUUID())
                && super.canHitEntity(target);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level().isClientSide) {
            return;
        }
        Entity target = result.getEntity();
        Entity owner = getOwner();
        if (owner instanceof LivingEntity livingOwner) {
            DamageSource source = damageSources().mobProjectile(this, livingOwner);
            if (target.hurt(source, entityData.get(DATA_DAMAGE))) {
                hitEntities.add(LibEntityUtils.dedupeIdentity(target).getUUID());
                LivingEntity livingTarget = LibEntityUtils.effectTarget(target);
                if (livingTarget != null) {
                    Vec3 push = getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(0.35);
                    livingTarget.push(push.x, 0.1, push.z);
                    livingOwner.setLastHurtMob(livingTarget);
                    if (livingTarget.isAlive() && getWeapon().getItem() instanceof BoomerangItem boomerang)
                        boomerang.onProjectileHit(this, livingOwner, livingTarget);
                }
            }
        }
        if (--penetration <= 0) {
            startReturning();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!isReturning()) {
            playSound(SoundEvents.WOOD_HIT, 0.5F, 1.35F);
        }
        startReturning();
        super.onHitBlock(result);
    }

    @Override
    public void tick() {
        Entity owner = getOwner();
        if (!level().isClientSide && (owner == null || owner.isRemoved())) {
            discard();
            return;
        }
        super.tick();
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS && !PortProjectileImpactEvent.onProjectileImpact(this, hit)) {
            hitTargetOrDeflectSelf(hit);
        }
        if (!isReturning() && tickCount >= forwardTicks) {
            startReturning();
        }
        if (isReturning() && owner instanceof LivingEntity livingOwner) {
            Vec3 target = livingOwner.getEyePosition().subtract(0.0, 0.25, 0.0);
            Vec3 direction = target.subtract(position());
            if (direction.lengthSqr() < Mth.square(backSpeed * 0.75F)) {
                discard();
                return;
            }
            setDeltaMovement(direction.normalize().scale(backSpeed));
            noPhysics = true;
        }
        Vec3 motion = getDeltaMovement();
        if (level().isClientSide) {
            initRuneEmitter();
            if (getWeapon().getItem() instanceof BoomerangItem boomerang)
                boomerang.emitFlightParticles(this, motion);
        }
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        updateRotation();
    }

    /// 客户端每刻只在这里补建一次发射器：按 `isRemoved()` 反复重建会让短命弹幕把引擎的表踩崩
    /// （见 `SoulLureProjectile#tick` 上的注释），所以建好之后一律交给引擎和实体生命周期处理。
    private void initRuneEmitter() {
        if (runeEmitter != null || getRune() < 0) return;
        if (!(getWeapon().getItem() instanceof BoomerangItem boomerang)) return;
        ParticleEmitter emitter = boomerang.createRuneEmitter(this, getRune());
        if (emitter == null) return;
        emitter.attachEntity(this);
        emitter.hideOutline = true;
        emitter.setLocalSpace(new Matrix4x3f().setTranslation(0, getBbHeight() * 0.5F, 0));
        MolangParticleEngine.INSTANCE._addEmitter(emitter);
        this.runeEmitter = emitter;
    }

    private void startReturning() {
        entityData.set(DATA_RETURNING, true);
        noPhysics = true;
        hitEntities.clear();
    }

    public boolean isReturning() {
        return entityData.get(DATA_RETURNING);
    }

    public int getVisualRotationOffset() {
        return visualRotationOffset;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Weapon", getWeapon().save(new CompoundTag()));
        tag.putBoolean("Returning", isReturning());
        tag.putFloat("Damage", entityData.get(DATA_DAMAGE));
        tag.putFloat("BackSpeed", backSpeed);
        tag.putInt("ForwardTicks", forwardTicks);
        tag.putInt("Penetration", penetration);
        tag.putBoolean("Fire", fire);
        tag.putInt("Rune", getRune());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(DATA_WEAPON, ItemStack.of(tag.getCompound("Weapon")));
        entityData.set(DATA_RETURNING, tag.getBoolean("Returning"));
        entityData.set(DATA_DAMAGE, Math.max(0.0F, tag.getFloat("Damage")));
        backSpeed = Math.max(0.1F, tag.getFloat("BackSpeed"));
        forwardTicks = Math.max(1, tag.getInt("ForwardTicks"));
        penetration = Math.max(1, tag.getInt("Penetration"));
        fire = tag.getBoolean("Fire");
        entityData.set(DATA_RUNE, tag.contains("Rune") ? tag.getInt("Rune") : -1);
        setNoGravity(true);
        noPhysics = isReturning();
    }
}
