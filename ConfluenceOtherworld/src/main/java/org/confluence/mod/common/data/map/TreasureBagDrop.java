package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.component.LootComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.item.common.TreasureBagItem;
import org.jetbrains.annotations.Nullable;

public record TreasureBagDrop(Item item) {
    public static final Codec<TreasureBagDrop> CODEC = BuiltInRegistries.ITEM.byNameCodec()
            .xmap(TreasureBagDrop::new, TreasureBagDrop::item);

    public static @Nullable ItemStack getTreasureBag(LivingEntity living) {
        if (!(living.level() instanceof ServerLevel serverLevel)) return null;
        TreasureBagDrop data = ModDataMaps.getEntityData(ModDataMaps.TREASURE_BAG, living);
        if (data == null || !(data.item() instanceof TreasureBagItem item)) return null;
        ItemStack stack = item.getDefaultInstance();
        ResourceLocation lootTable = item.lootTable.withSuffix(item.suffix.apply(serverLevel, living.blockPosition()));
        stack.setData(ModDataComponentTypes.LOOT, new LootComponent(lootTable));
        return stack;
    }
}
