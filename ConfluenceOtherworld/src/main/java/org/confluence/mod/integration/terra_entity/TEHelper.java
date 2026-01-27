package org.confluence.mod.integration.terra_entity;

import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.animal.SimpleVariantAnimal;

import java.util.Map;

public final class TEHelper {
    // 替换配方
    public static void processRecipes(Map<ResourceLocation, JsonElement> objectMap) {
        objectMap.entrySet().removeIf(entry -> TerraEntity.MODID.equals(entry.getKey().getNamespace()));
    }

    // 重定向战利品表
    public static void redirectLootTable() {
        for (Map.Entry<ResourceKey<EntityType<?>>, EntityType<?>> entry : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
            ResourceLocation id = entry.getKey().location();
            if (TerraEntity.MODID.equals(id.getNamespace())) {
                entry.getValue().lootTable = Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/" + id.getPath());
            }
        }
    }

    /// [蠕虫](https://terraria.wiki.gg/zh/wiki/%E8%A0%95%E8%99%AB)
    public static boolean wormSpawnRules(EntityType<SimpleVariantAnimal> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof Level level1)) return false;
        int y = pos.getY();
        int surfaceY = OverworldUtils.getSurfaceY();
        if (y > surfaceY && y < OverworldUtils.getSpaceY() && ModUtils.isRainingAt(level1, pos)) {
            return true;
        }
        return y > OverworldUtils.getUndergroundY() && y < surfaceY;
    }

    public static void finalizeWormSpawn(SimpleVariantAnimal worm) {
        // todo 墓地变为蝇蛆
    }
}
