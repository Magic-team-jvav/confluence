package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.item.ConsumableItems;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class HopliteJavelin extends StraightMonsterProjectile implements ItemSupplier {
    public static final double GRAVITY = 0.025;
    private final Set<UUID> hitEntities = new HashSet<>();

    public HopliteJavelin(EntityType<? extends HopliteJavelin> type, Level level) {
        super(type, level);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        return velocity.add(0.0, -GRAVITY, 0.0);
    }

    @Override
    protected void finishEntityHit(EntityHitResult result) {
        hitEntities.add(result.getEntity().getUUID());
    }

    @Override
    public boolean canHitEntity(Entity target) {
        return !hitEntities.contains(target.getUUID()) && super.canHitEntity(target);
    }

    @Override
    public ItemStack getItem() {
        return ConsumableItems.JAVELIN.toStack();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag targets = new ListTag();
        hitEntities.stream().sorted().forEach(uuid -> targets.add(NbtUtils.createUUID(uuid)));
        tag.put("HitEntities", targets);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        hitEntities.clear();
        for (Tag target : tag.getList("HitEntities", Tag.TAG_INT_ARRAY)) {
            hitEntities.add(NbtUtils.loadUUID(target));
        }
    }
}
