package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.block.functional.DeathChestBlock;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ChestBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    public static final ArrayList<DeferredBlock<BaseChestBlock>> NORMAL_CHESTS = new ArrayList<>();
    public static final ArrayList<DeferredBlock<DeathChestBlock>> DEATH_CHESTS = new ArrayList<>();

    public static final DeferredBlock<BaseChestBlock> GOLDEN_CHEST = registerNormal("golden_chest", stack -> {
        if (stack.is(ToolItems.GOLDEN_KEY)) {
            stack.shrink(1);
            return true;
        }
        return false;
    });
    public static final DeferredBlock<DeathChestBlock> DEATH_GOLDEN_CHEST = registerDeath("death_golden_chest");
    public static final DeferredBlock<BaseChestBlock> SHADOW_CHEST = registerNormal("shadow_chest", stack -> stack.is(ToolItems.SHADOW_KEY));
    public static final DeferredBlock<BaseChestBlock> FROZEN_CHEST = registerNormal("frozen_chest", null);
    public static final DeferredBlock<BaseChestBlock> LVY_CHEST = registerNormal("lvy_chest", null);
    public static final DeferredBlock<BaseChestBlock> WATER_CHEST = registerNormal("water_chest", null);
    public static final DeferredBlock<BaseChestBlock> SKYWARE_CHEST = registerNormal("skyware_chest", null);
    public static final DeferredBlock<DeathChestBlock> DEATH_WOODEN_CHEST = registerDeath("death_wooden_chest");
    public static final DeferredBlock<BaseChestBlock> SANDSTONE_CHEST = registerNormal("sandstone_chest", null);
    public static final DeferredBlock<BaseChestBlock> LIVING_WOOD_CHEST = registerNormal("living_wood_chest", null);
    public static final DeferredBlock<BaseChestBlock> DUNGEON_CHEST = registerNormal("dungeon_chest", stack -> stack.is(ToolItems.GOLDEN_DUNGEON_KEY));

    public static final Supplier<BlockEntityType<BaseChestBlock.Entity>> BASE_CHEST_ENTITY = ModBlocks.BLOCK_ENTITIES.register("base_chest_entity", () -> BlockEntityType.Builder.of(BaseChestBlock.Entity::new, NORMAL_CHESTS.stream().map(DeferredBlock::get).toArray(Block[]::new)).build(DSL.remainderType()));
    public static final Supplier<BlockEntityType<DeathChestBlock.Entity>> DEATH_CHEST_ENTITY = ModBlocks.BLOCK_ENTITIES.register("death_chest_entity", () -> BlockEntityType.Builder.of(DeathChestBlock.Entity::new, DEATH_CHESTS.stream().map(DeferredBlock::get).toArray(Block[]::new)).build(DSL.remainderType()));

    private static DeferredBlock<BaseChestBlock> registerNormal(String id, @Nullable Predicate<ItemStack> predicate) {
        DeferredBlock<BaseChestBlock> object = BLOCKS.register(id, () -> new BaseChestBlock(predicate == null ? null : (stack, state, level, pos, player, hand, hitResult) -> {
            if (predicate.test(stack)) {
                Direction relativeDir = ChestBlock.getConnectedDirection(state);
                boolean isDouble = state.getValue(ChestBlock.TYPE) != ChestType.SINGLE;
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.BLOCKS);
                    double posX = pos.getX() + 0.5;
                    double posZ = pos.getZ() + 0.5;
                    if (isDouble) {
                        posX += relativeDir.getStepX() * 0.5;
                        posZ += relativeDir.getStepZ() * 0.5;
                    }
                    serverLevel.sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CHAIN.defaultBlockState()),
                            posX, pos.getY() + 0.5, posZ, 200, 0.0625, 0.0625, 0.0625, 0.15
                    );
                    level.setBlockAndUpdate(pos, state.setValue(BaseChestBlock.UNLOCKED, true));
                }
                return true;
            }
            return false;
        }));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(object);
        NORMAL_CHESTS.add(object);
        return object;
    }

    private static DeferredBlock<DeathChestBlock> registerDeath(String id) {
        DeferredBlock<DeathChestBlock> object = BLOCKS.register(id, DeathChestBlock::new);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(object);
        DEATH_CHESTS.add(object);
        return object;
    }
}
