package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.whip.WhipDamageSource;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.ArrayList;
import java.util.List;

/// 远古风暴：禁戒套装奖励召唤的沙尘龙卷。
///
/// 规则（与需求一致）：
/// * 召唤需要至少 `3 × 11` 格的空地；之后按实际可用空间在 `3~30` 格高、`1~3` 格宽之间取值，可造成伤害的体积上限为 `3 × 28`；
/// * 持续 15 秒，最多命中 30 次，每次命中带约 10 刻局部免伤；第 30 次命中后约 1 秒消失；
/// * 同一时间只存在一个：召唤新的时，旧的约 1 秒后消失；
/// * 玩家到风暴中列必须有未被阻挡的视线，视线被挡后约 1 秒消失；
/// * 伤害同时吃召唤与魔法加成（召唤伤害属性 × 魔法伤害属性）。
public class AncientStormEntity extends Projectile {
    /// 最短存活时间 15 秒。
    private static final int LIFETIME = 15 * 20;
    /// 命中上限。
    private static final int MAX_HITS = 30;
    /// 召唤所需的最小空间：3 格宽 × 11 格高。
    private static final int MIN_HEIGHT = 11;
    private static final int MIN_WIDTH = 3;
    /// 高度/宽度上限，以及可造成伤害的体积高度上限。
    private static final int MAX_HEIGHT = 30;
    private static final int MAX_WIDTH = 3;
    private static final int MAX_DAMAGE_HEIGHT = 28;
    /// 每次命中造成的局部免伤刻数。
    private static final int HIT_IMMUNITY_TICKS = 10;
    /// 命中判定的间隔（刻）。
    private static final int DAMAGE_INTERVAL = 2;
    /// 视线被挡多久后消散。
    private static final int SIGHT_LOST_TICKS = 20;
    /// 淡出时长。
    private static final int FADE_TICKS = 20;
    /// 基础接触伤害。
    public static final float BASE_DAMAGE = 20.0F;

    private static final EntityDataAccessor<Integer> DATA_HEIGHT = SynchedEntityData.defineId(AncientStormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WIDTH = SynchedEntityData.defineId(AncientStormEntity.class, EntityDataSerializers.INT);

    private int hits;
    /// 大于 0 表示正在淡出，减到 0 就消散。
    private int fadeTicks = -1;
    private int lostSightTicks;
    private float damage = BASE_DAMAGE;

    private final List<ParticleEmitter> emitters = new ArrayList<>();
    private boolean emittersBuilt;

    public AncientStormEntity(EntityType<? extends AncientStormEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
        setInvulnerable(true);
        this.noPhysics = true;
    }

    /// 在指定位置召唤一个远古风暴；空间不足时返回 `null`。
    ///
    /// 命中点本身可能是墙角/斜坡，直接在原地判定会经常「召唤失败」；这里先在命中点附近小幅搜索，
    /// 找到第一个满足「3 宽 × 11 高」的位置再召唤（没有则返回 `null`）。
    public static @Nullable AncientStormEntity summon(ServerLevel level, Vec3 pos, Player owner) {
        Vec3 spot = findSpawnSpot(level, pos);
        if (spot == null) {
            owner.displayClientMessage(Component.translatable("message.confluence.forbidden_set.no_space"), true);
            return null;
        }
        pos = spot;
        int height = clearHeight(level, pos);
        int width = clearWidth(level, pos);
        if (height < MIN_HEIGHT || width < MIN_WIDTH) return null;

        // 旧风暴立即进入淡出。
        for (AncientStormEntity old : level.getEntitiesOfClass(AncientStormEntity.class, AABB.ofSize(pos, 64, 64, 64),
                storm -> storm.getOwner() == owner)) {
            old.fadeOut();
        }

        AncientStormEntity storm = new AncientStormEntity(ModEntities.ANCIENT_STORM.get(), level);
        storm.setOwner(owner);
        storm.setPos(pos.x, pos.y, pos.z);
        storm.entityData.set(DATA_HEIGHT, Math.min(height, MAX_HEIGHT));
        storm.entityData.set(DATA_WIDTH, Math.min(width, MAX_WIDTH));
        storm.damage = baseDamage(owner);
        level.addFreshEntity(storm);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.SAND_BREAK, storm.getSoundSource(), 1.2F, 0.6F);
        return storm;
    }

    /// 远古风暴的伤害：基础 20 × 召唤伤害倍率 × 魔法伤害倍率。
    private static float baseDamage(Player owner) {
        float summon = (float) owner.getAttributeValue(LibAttributes.getSummonDamage());
        float magic = (float) owner.getAttributeValue(LibAttributes.getMagicDamage());
        return BASE_DAMAGE * summon * magic;
    }

    /// 从召唤点向上数连续可站立的空间高度。
    private static int clearHeight(Level level, Vec3 pos) {
        BlockPos base = BlockPos.containing(pos.x, pos.y + 0.1, pos.z);
        int height = 0;
        for (int i = 0; i < MAX_HEIGHT; ++i) {
            if (!level.isEmptyBlock(base.above(i))) break;
            ++height;
        }
        return height;
    }

    /// 横向可用宽度：左右各能放一格才算 3 格宽。
    private static int clearWidth(Level level, Vec3 pos) {
        BlockPos base = BlockPos.containing(pos.x, pos.y + 0.1, pos.z);
        int width = 1;
        if (level.isEmptyBlock(base.east()) && level.isEmptyBlock(base.west())) {
            width = 3;
        } else if (level.isEmptyBlock(base.east()) || level.isEmptyBlock(base.west())) {
            width = 2;
        }
        return width;
    }

    /// 在命中点及其附近（水平 ±1、向上最多 2 格）寻找满足召唤条件的空地。
    private static @Nullable Vec3 findSpawnSpot(Level level, Vec3 origin) {
        if (hasEnoughSpace(level, origin)) return origin;
        for (int dy = 0; dy <= 2; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dz = -1; dz <= 1; ++dz) {
                    if (dx == 0 && dz == 0 && dy == 0) continue;
                    Vec3 candidate = origin.add(dx, dy, dz);
                    if (hasEnoughSpace(level, candidate)) return candidate;
                }
            }
        }
        return null;
    }

    private static boolean hasEnoughSpace(Level level, Vec3 pos) {
        return clearHeight(level, pos) >= MIN_HEIGHT && clearWidth(level, pos) >= MIN_WIDTH;
    }

    private void fadeOut() {
        if (fadeTicks < 0 || fadeTicks > FADE_TICKS) fadeTicks = FADE_TICKS;
    }

    public int stormHeight() {
        return entityData.get(DATA_HEIGHT);
    }

    public int stormWidth() {
        return entityData.get(DATA_WIDTH);
    }

    private int damageHeight() {
        return Math.min(stormHeight(), MAX_DAMAGE_HEIGHT);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_HEIGHT, 0).define(DATA_WIDTH, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide && (DATA_HEIGHT.equals(key) || DATA_WIDTH.equals(key))) {
            emittersBuilt = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            clientParticles();
            return;
        }
        if (stormHeight() <= 0) {
            discard();
            return;
        }
        setPos(getX(), getY(), getZ());
        if (fadeTicks > 0) {
            if (--fadeTicks == 0) discard();
            return;
        }
        if (tickCount >= LIFETIME) {
            discard();
            return;
        }
        // 视线检查：玩家眼睛到风暴中列必须没有方块阻挡。
        if (getOwner() instanceof Player owner) {
            if (tickCount % 4 == 0) {
                Vec3 from = owner.getEyePosition();
                Vec3 to = new Vec3(getX(), getY() + damageHeight() * 0.5, getZ());
                HitResult hit = level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));
                if (hit.getType() != HitResult.Type.MISS && hit.getLocation().distanceToSqr(to) > 0.5D * 0.5D) {
                    lostSightTicks += 4;
                } else {
                    lostSightTicks = 0;
                }
                if (lostSightTicks >= SIGHT_LOST_TICKS) {
                    discard();
                    return;
                }
            }
        } else {
            discard();
            return;
        }
        if (tickCount % DAMAGE_INTERVAL == 0 && hits < MAX_HITS) {
            hurtNearby();
        }
    }

    /// 每 2 刻结算一次接触伤害；每个目标约 10 刻局部免伤，总命中次数上限 30。
    private void hurtNearby() {
        if (!(getOwner() instanceof Player owner)) return;
        int width = stormWidth();
        double halfWidth = width * 0.5;
        double height = damageHeight();
        AABB box = new AABB(getX() - halfWidth, getY(), getZ() - halfWidth, getX() + halfWidth, getY() + height, getZ() + halfWidth);
        WhipDamageSource source = new WhipDamageSource(owner, 0.0F);
        Immunity immunity = STORM_IMMUNITY;
        for (Entity candidate : LibEntityUtils.queryHitCandidates(level(), owner, box, entity -> LibEntityUtils.effectTarget(entity) != null)) {
            if (hits >= MAX_HITS) break;
            LivingEntity living = LibEntityUtils.effectTarget(candidate);
            if (living == null || living == owner || !living.isAlive()) continue;
            if (!LibEntityUtils.allowsAttack(owner, candidate)) continue;
            Immunity.hurt(immunity, candidate, living, source, damage);
            ++hits;
        }
        if (hits >= MAX_HITS) fadeOut();
    }

    private static final Immunity STORM_IMMUNITY = new Immunity() {
        @Override
        public Type confluence$getImmunityType() {
            return Type.LOCAL;
        }

        @Override
        public int confluence$getImmunityDuration(DamageSource source) {
            return HIT_IMMUNITY_TICKS;
        }
    };

    /// 客户端粒子：按体积铺若干层发射器，粒子 JSON 负责螺旋上升；体积变大时层数与横向发射器一起变多。
    private void clientParticles() {
        int height = stormHeight();
        int width = stormWidth();
        if (height <= 0 || width <= 0) return;
        if (!emittersBuilt) {
            clearEmitters();
            int layers = Math.max(1, height / 4);
            for (int layer = 0; layer < layers; ++layer) {
                double y = getY() + layer * 4.0;
                for (int column = 0; column < width; ++column) {
                    double offset = (column - (width - 1) * 0.5) * 0.7;
                    ParticleEmitter emitter = new ParticleEmitter(level(), new Vec3(getX() + offset, y, getZ() + offset),
                            Confluence.asResource("forbidden_storm"));
                    emitters.add(emitter);
                    /// ParticleStorm 的发射器必须显式加进引擎才会渲染。
                    MolangParticleEngine.INSTANCE.addEmitter(emitter);
                }
            }
            emittersBuilt = true;
        }
    }

    private void clearEmitters() {
        for (ParticleEmitter emitter : emitters) emitter.remove();
        emitters.clear();
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        clearEmitters();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Hits", hits);
        tag.putFloat("StormDamage", damage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        hits = tag.getInt("Hits");
        if (tag.contains("StormDamage")) damage = tag.getFloat("StormDamage");
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 32768.0;
    }
}
