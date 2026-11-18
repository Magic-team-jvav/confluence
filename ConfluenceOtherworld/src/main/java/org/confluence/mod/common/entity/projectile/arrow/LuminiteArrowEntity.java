package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/// 夜明箭：最多命中 4 个目标，每次命中伤害下降 4%；命中后 0.75 秒消失。
///
/// 消失约 0.5 秒后会放出一个「夜明箭尾迹」，尾迹逐刻重放箭矢记录下来的飞行轨迹，
/// 造成与箭相同的基础伤害、同样 4 次命中上限与每命中 -4% 衰减。
public class LuminiteArrowEntity extends BaseArrowEntity {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/arrow/luminite_arrow.png");
    /// 命中上限（穿透次数按「命中 - 1」算）。
    private static final int MAX_HITS = 4;
    /// 每次命中后的伤害保留比例。
    private static final float POWER_AFTER_HIT = 0.96F;
    /// 命中后存活刻数（0.75 秒）。
    private static final int LINGER_TICKS = 15;
    /// 轨迹最多记录的点数（按 tick 采样）。
    private static final int MAX_PATH_POINTS = 200;
    private static final String HIT_COUNT_TAG = "LuminiteHits";
    private static final String LINGER_TAG = "LuminiteLinger";
    private static final String PATH_TAG = "LuminitePath";

    private final List<Vec3> path = new ArrayList<>();
    private int hits;
    private int linger = -1;

    public LuminiteArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public LuminiteArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    protected int getPenetrationCount() {
        return MAX_HITS;
    }

    @Override
    protected double getAdditionalKnockback() {
        return 1.75;
    }

    @Override
    protected float getCalculatedDamage() {
        float damage = super.getCalculatedDamage();
        for (int i = 0; i < hits; ++i) {
            damage *= POWER_AFTER_HIT;
        }
        return damage;
    }

    @Override
    protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {
        ++hits;
        this.linger = LINGER_TICKS;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (linger > 0 && --linger == 0) {
            discard();
            return;
        }
        if (path.size() < MAX_PATH_POINTS) {
            path.add(position());
        }
    }

    /// 消失时把记录下来的轨迹交给尾迹射弹（尾迹自己再等 0.5 秒开始重放）。
    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        Level level = level();
        if (level.isClientSide || path.isEmpty() || !isRemoved()) return;
        LuminiteArrowTrailEntity trail = new LuminiteArrowTrailEntity(ModEntities.LUMINITE_ARROW_TRAIL.get(), level);
        trail.setup(getOwner(), path, trailDamage());
        level.addFreshEntity(trail);
    }

    /// 尾迹使用的伤害：箭矢基础伤害（武器基础 + 夜明箭附加），不含命中衰减。
    private float trailDamage() {
        float damage = (float) getBaseDamage();
        if (getPickupItem().getItem() instanceof org.confluence.mod.common.item.arrow.BaseTerraArrowItem arrowItem) {
            damage += arrowItem.getAdditionalDamage();
        }
        return damage;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(HIT_COUNT_TAG, hits);
        tag.putInt(LINGER_TAG, linger);
        ListTag pathTag = new ListTag();
        for (Vec3 point : path) {
            CompoundTag pointTag = new CompoundTag();
            pointTag.putDouble("X", point.x);
            pointTag.putDouble("Y", point.y);
            pointTag.putDouble("Z", point.z);
            pathTag.add(pointTag);
        }
        tag.put(PATH_TAG, pathTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.hits = tag.getInt(HIT_COUNT_TAG);
        this.linger = tag.getInt(LINGER_TAG);
        this.path.clear();
        ListTag pathTag = tag.getList(PATH_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < pathTag.size(); ++i) {
            CompoundTag pointTag = pathTag.getCompound(i);
            path.add(new Vec3(pointTag.getDouble("X"), pointTag.getDouble("Y"), pointTag.getDouble("Z")));
        }
    }

    @Override
    public ResourceLocation getTexturePath() {
        return TEXTURE;
    }
}
