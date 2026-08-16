package org.confluence.mod.common.item.mana;

import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.mana.CloudProjectile;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/// 在事务成功后替换旧云朵并记录新实体 UUID 的放置型法杖。
public class CloudRodItem extends ManaStaffItem<CloudProjectile> {
    private int maxCloud = 1;

    public CloudRodItem(Properties properties, ModRarity rarity, ProjectileFactory<CloudProjectile> factory,
                        float damage, int manaCost, float rawVelocity, int cooldown) {
        super(properties, rarity, factory, damage, manaCost, rawVelocity, cooldown);
    }

    public CloudRodItem(ModRarity rarity, ProjectileFactory<CloudProjectile> factory, float damage,
                        int manaCost, float rawVelocity, int cooldown,
                        Consumer<PortItemAttributeModifiers.Builder> consumer) {
        super(rarity, factory, damage, manaCost, rawVelocity, cooldown, consumer);
    }

    public CloudRodItem(ModRarity rarity, ProjectileFactory<CloudProjectile> factory, float damage,
                        int manaCost, float rawVelocity, int cooldown, double critChance) {
        super(rarity, factory, damage, manaCost, rawVelocity, cooldown, critChance);
    }

    public CloudRodItem setMaxCloud(int maxCloud) {
        if (maxCloud < 1) {
            throw new IllegalArgumentException("Maximum cloud count must be positive");
        }
        this.maxCloud = maxCloud;
        return this;
    }

    /// 目标搜索只读取服务端世界，不替换旧实体，也不写物品状态。
    @Override
    protected void configureProjectile(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            CloudProjectile projectile
    ) {
        double reach = 64.0;
        double squaredReach = Mth.square(reach);
        Vec3 from = context.eyePosition();
        HitResult blockHit = context.player().pick(reach, 1.0F, false);
        double blockDistance = blockHit.getLocation().distanceToSqr(from);
        if (blockHit.getType() != HitResult.Type.MISS) {
            squaredReach = blockDistance;
            reach = Math.sqrt(blockDistance);
        }
        Vec3 to = from.add(context.viewVector().scale(reach));
        AABB searchBox = context.player().getBoundingBox()
                .expandTowards(context.viewVector().scale(reach))
                .inflate(1.0);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                context.player(),
                from,
                to,
                searchBox,
                entity -> !entity.isSpectator() && entity.isPickable(),
                squaredReach);
        if (entityHit != null
                && entityHit.getLocation().distanceToSqr(from) < blockDistance
                && entityHit.getEntity() instanceof LivingEntity living) {
            projectile.setTarget(living);
        }
    }

    /// 新云朵已加入世界后才淘汰最旧云朵并记录 UUID。
    ///
    /// <p>这样即使实体生成被事件拒绝，也不会提前删掉玩家原有云朵或污染物品状态。</p>
    @Override
    protected void onSuccessfulShot(ProjectileFireContext context, CloudProjectile projectile) {
        ItemStack stack = context.currentWeaponForCommit();
        if (stack == null || stack.getItem() != this) {
            projectile.discard();
            return;
        }
        CompoundTag tag = LibUtils.getItemStackNbtNoCopy(stack);
        ListTag clouds = collectTrackedClouds(
                tag.getList("Clouds", Tag.TAG_INT_ARRAY), context.level(), context.player());
        while (clouds.size() >= maxCloud) {
            UUID removed = NbtUtils.loadUUID(clouds.remove(0));
            Entity oldCloud = context.level().getEntity(removed);
            if (oldCloud instanceof CloudProjectile cloud && cloud.getOwner() == context.player()) {
                oldCloud.discard();
            }
        }
        clouds.add(NbtUtils.createUUID(projectile.getUUID()));
        tag.put("Clouds", clouds);
    }

    /// 判断母云 UUID 是否仍被玩家背包中的任意云杖追踪。
    ///
    /// <p>这里不解析实体，也不加载区块。旧母云所在区块卸载期间，云杖可以安全淘汰它的 UUID；
    /// 旧实体以后重新加载时会自行发现引用已消失并销毁。</p>
    public static boolean isTrackedCloud(Player player, CloudProjectile cloud) {
        UUID cloudId = cloud.getUUID();
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!(stack.getItem() instanceof CloudRodItem)) continue;
            CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
            if (tag == null || !tag.contains("Clouds", Tag.TAG_LIST)) continue;
            ListTag clouds = tag.getList("Clouds", Tag.TAG_INT_ARRAY);
            for (Tag entry : clouds) {
                if (entry instanceof IntArrayTag array
                        && array.getAsIntArray().length == 4
                        && NbtUtils.loadUUID(array).equals(cloudId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /// 重建仍属于当前玩家的活动云朵列表。
    ///
    /// <p>UUID 数组必须恰好包含四个整数；重复、类型错误或已加载后确认属于其他玩家的实体会被丢弃。
    /// 当前未解析的 UUID 会保留而不强加载区块，直至容量淘汰；这样既保留双云语义，
    /// 也不会因复制或损坏数据删除其他玩家的云朵。</p>
    static ListTag collectTrackedClouds(ListTag source, ServerLevel level, Player player) {
        ListTag result = new ListTag();
        Set<UUID> seen = new HashSet<>();
        for (Tag entry : source) {
            if (!(entry instanceof IntArrayTag array) || array.getAsIntArray().length != 4) {
                continue;
            }
            UUID uuid = NbtUtils.loadUUID(array);
            Entity entity = level.getEntity(uuid);
            if (entity == null && seen.add(uuid)) {
                // 未加载实体保持 UUID 引用，不为验证所有权而强加载其区块。
                result.add(NbtUtils.createUUID(uuid));
            } else if (entity instanceof CloudProjectile cloud
                    && cloud.getOwner() == player
                    && !cloud.isRemoved()
                    && seen.add(uuid)) {
                result.add(NbtUtils.createUUID(uuid));
            }
        }
        return result;
    }
}
