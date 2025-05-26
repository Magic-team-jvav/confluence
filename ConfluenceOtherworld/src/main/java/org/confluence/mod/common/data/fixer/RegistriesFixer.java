package org.confluence.mod.common.data.fixer;

import com.mojang.datafixers.DSL;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.ModItems;

import java.util.function.Supplier;

import static org.confluence.mod.common.init.block.ModBlocks.BLOCK_ENTITIES;

public class RegistriesFixer {
    public static final DeferredBlock<FixedBaseChestBlock> BASE_CHEST_BLOCK = ModBlocks.BLOCKS.register("base_chest_block", FixedBaseChestBlock::new);
    public static final Supplier<BlockEntityType<FixedBaseChestBlock.Entity>> BASE_CHEST_BLOCK_ENTITY = BLOCK_ENTITIES.register("base_chest_block_entity", () -> BlockEntityType.Builder.of(FixedBaseChestBlock.Entity::new, BASE_CHEST_BLOCK.get()).build(DSL.remainderType()));
    public static final DeferredBlock<FixedDeathChestBlock> DEATH_CHEST_BLOCK = ModBlocks.BLOCKS.register("death_chest_block", FixedDeathChestBlock::new);
    public static final Supplier<BlockEntityType<FixedDeathChestBlock.Entity>> DEATH_CHEST_BLOCK_ENTITY = BLOCK_ENTITIES.register("death_chest_block_entity", () -> BlockEntityType.Builder.of(FixedDeathChestBlock.Entity::new, DEATH_CHEST_BLOCK.get()).build(DSL.remainderType()));

    public static void init() {
        ModItems.HIDDEN.register("base_chest_block", () -> new FixedBaseChestBlock.Item(BASE_CHEST_BLOCK.get()));
        ModItems.HIDDEN.register("death_chest_block", () -> new FixedBaseChestBlock.Item(DEATH_CHEST_BLOCK.get()));
    }
}
