package org.confluence.mod.common.item.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.mana.CloudProjectile;

import java.util.UUID;
import java.util.function.Consumer;

public class CloudRodItem extends ManaStaffItem<CloudProjectile> {
    private int maxCloud = 1;

    public CloudRodItem(Properties properties, ModRarity rarity, ProjectileFactory<CloudProjectile> factory, float damage, int manaCost, float rawVelocity, int cooldown) {
        super(properties, rarity, factory, damage, manaCost, rawVelocity, cooldown);
    }

    public CloudRodItem(ModRarity rarity, ProjectileFactory<CloudProjectile> factory, float damage, int manaCost, float rawVelocity, int cooldown, Consumer<ItemAttributeModifiers.Builder> consumer) {
        super(rarity, factory, damage, manaCost, rawVelocity, cooldown, consumer);
    }

    public CloudRodItem(ModRarity rarity, ProjectileFactory<CloudProjectile> factory, float damage, int manaCost, float rawVelocity, int cooldown, double critChance) {
        super(rarity, factory, damage, manaCost, rawVelocity, cooldown, critChance);
    }

    public CloudRodItem setMaxCloud(int maxCloud) {
        if (maxCloud < 1) throw new IllegalArgumentException("maxCount must be greater than 1, currently is '" + maxCloud + "'");
        this.maxCloud = maxCloud;
        return this;
    }

    @Override
    protected void beforeShoot(ServerPlayer player, ItemStack stack, CloudProjectile projectile) {
        super.beforeShoot(player, stack, projectile);
        CompoundTag tag = LibUtils.getItemStackNbtNoCopy(stack);
        ListTag clouds = tag.getList("Clouds", CompoundTag.TAG_INT_ARRAY);
        if (clouds.size() == maxCloud) {
            UUID removed = NbtUtils.loadUUID(clouds.removeFirst());
            Entity entity = player.serverLevel().getEntity(removed);
            if (entity != null) {
                entity.discard();
            }
        }

        double reach = 64;
        double squared = Mth.square(reach);
        Vec3 from = player.getEyePosition(1.0F);
        HitResult hitResult = player.pick(reach, 1.0F, false);
        double sqr = hitResult.getLocation().distanceToSqr(from);
        if (hitResult.getType() != HitResult.Type.MISS) {
            squared = sqr;
            reach = Math.sqrt(sqr);
        }
        Vec3 viewVector = player.getViewVector(1.0F);
        Vec3 to = from.add(viewVector.x * reach, viewVector.y * reach, viewVector.z * reach);
        AABB aabb = player.getBoundingBox().expandTowards(viewVector.scale(reach)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                player, from, to, aabb, entity -> !entity.isSpectator() && entity.isPickable(), squared
        );
        if (entityHitResult != null && entityHitResult.getLocation().distanceToSqr(from) < sqr) {
            if (entityHitResult.getEntity() instanceof LivingEntity living) {
                projectile.setTarget(living);
            }
        }
    }

    @Override
    protected void afterShoot(ServerPlayer player, ItemStack stack, CloudProjectile projectile) {
        super.afterShoot(player, stack, projectile);
        LibUtils.updateItemStackNbt(stack, tag -> {
            ListTag clouds = new ListTag();
            clouds.addAll(tag.getList("Clouds", Tag.TAG_INT_ARRAY));
            clouds.add(NbtUtils.createUUID(projectile.getUUID()));
            tag.put("Clouds", clouds);
        });
    }
}
