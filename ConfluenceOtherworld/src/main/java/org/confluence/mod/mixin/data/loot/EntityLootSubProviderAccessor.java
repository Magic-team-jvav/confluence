package org.confluence.mod.mixin.data.loot;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Pseudo
@Mixin(targets = "net.minecraft.data.loot.EntityLootSubProvider")
public interface EntityLootSubProviderAccessor {
    @Accessor
    Map<EntityType<?>, Map<ResourceKey<LootTable>, LootTable.Builder>> getMap();

    @Accessor
    FeatureFlagSet getAllowed();
}
