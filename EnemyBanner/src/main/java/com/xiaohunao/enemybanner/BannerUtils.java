package com.xiaohunao.enemybanner;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

public class BannerUtils {
    public static List<Entity> getEntityInRange(Level level, String EntityId, BlockPos pos, int range) {
        Optional<EntityType<?>> entityType = EntityType.byString(EntityId);
        return entityType.map(type -> getEntityInRange(level, type, pos, range)).orElseGet(List::of);
    }

    public static List<Entity> getEntityInRange(Level level, EntityType<?> entityType, BlockPos pos, int range) {
        AABB aabb = new AABB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range);
        return level.getEntities((Entity) null, aabb, entity -> entityType.equals(entity.getType()));
    }
}
