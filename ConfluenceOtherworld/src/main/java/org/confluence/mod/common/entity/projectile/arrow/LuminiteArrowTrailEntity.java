package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/// 夜明箭尾迹：逐刻重放夜明箭记录的飞行轨迹。
///
/// 生成后先隐藏 0.5 秒，再沿轨迹逐点移动；命中敌怪造成与箭相同的基础伤害，
/// 每命中一次伤害降低 4%，最多命中 4 个目标，最后一次命中 0.75 秒后消失。
public class LuminiteArrowTrailEntity extends BaseArrowEntity {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/arrow/luminite_arrow.png");
    private static final int START_DELAY = 10;
    private static final int MAX_HITS = 4;
    private static final float POWER_AFTER_HIT = 0.96F;
    private static final int LINGER_TICKS = 15;
    private static final double HIT_INFLATE = 0.45;

    private final List<Vec3> path = new ArrayList<>();
    private final Set<UUID> hitTargets = new HashSet<>();
    private float baseDamage;
    private int hits;
    private int delay = START_DELAY;
    private int index;
    private int linger = -1;

    public LuminiteArrowTrailEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    /// 由夜明箭在消失时调用。
    public void setup(@Nullable Entity owner, List<Vec3> recordedPath, float damage) {
        setOwner(owner);
        this.path.clear();
        this.path.addAll(recordedPath);
        this.baseDamage = damage;
        if (!path.isEmpty()) {
            setPos(path.get(0));
        }
    }

    @Override
    protected float getCalculatedDamage() {
        float damage = baseDamage;
        for (int i = 0; i < hits; ++i) {
            damage *= POWER_AFTER_HIT;
        }
        return damage;
    }

    @Override
    public void tick() {
        if (level().isClientSide) return;
        ++tickCount;
        if (delay > 0) {
            --delay;
            return;
        }
        if (linger > 0 && --linger == 0) {
            discard();
            return;
        }
        if (index >= path.size()) {
            discard();
            return;
        }
        setPos(path.get(index++));
        hurtNearby();
    }

    private void hurtNearby() {
        if (hits >= MAX_HITS) {
            discard();
            return;
        }
        AABB box = getBoundingBox().inflate(HIT_INFLATE);
        for (Entity candidate : LibEntityUtils.queryHitCandidates(level(), getOwner(), box, entity -> LibEntityUtils.effectTarget(entity) != null)) {
            if (hits >= MAX_HITS) break;
            LivingEntity living = LibEntityUtils.effectTarget(candidate);
            if (living == null || living == getOwner() || !living.isAlive()) continue;
            if (!LibEntityUtils.allowsAttack(getOwner(), candidate)) continue;
            if (!hitTargets.add(LibEntityUtils.dedupeIdentity(candidate).getUUID())) continue;
            living.hurt(getDamageSource(), getCalculatedDamage());
            ++hits;
            this.linger = LINGER_TICKS;
        }
        if (hits >= MAX_HITS) {
            this.linger = LINGER_TICKS;
        }
    }

    /// 尾迹在服务端逐刻传送，客户端只跟着同步位置渲染，不自己跑物理。
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("TrailDamage", baseDamage);
        tag.putInt("TrailHits", hits);
        tag.putInt("TrailDelay", delay);
        tag.putInt("TrailIndex", index);
        tag.putInt("TrailLinger", linger);
        ListTag pathTag = new ListTag();
        for (Vec3 point : path) {
            CompoundTag pointTag = new CompoundTag();
            pointTag.putDouble("X", point.x);
            pointTag.putDouble("Y", point.y);
            pointTag.putDouble("Z", point.z);
            pathTag.add(pointTag);
        }
        tag.put("TrailPath", pathTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.baseDamage = tag.getFloat("TrailDamage");
        this.hits = tag.getInt("TrailHits");
        this.delay = tag.getInt("TrailDelay");
        this.index = tag.getInt("TrailIndex");
        this.linger = tag.getInt("TrailLinger");
        this.path.clear();
        ListTag pathTag = tag.getList("TrailPath", Tag.TAG_COMPOUND);
        for (int i = 0; i < pathTag.size(); ++i) {
            CompoundTag pointTag = pathTag.getCompound(i);
            path.add(new Vec3(pointTag.getDouble("X"), pointTag.getDouble("Y"), pointTag.getDouble("Z")));
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public ResourceLocation getTexturePath() {
        return TEXTURE;
    }
}
