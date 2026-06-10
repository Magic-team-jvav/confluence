package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.block.common.BiomeChestBlock;
import org.confluence.mod.common.block.functional.DeathChestBlock;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.confluence.mod.common.init.block.ModBlocks.BLOCK_ENTITIES;

public class ChestBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Confluence.MODID);
    public static final ArrayList<RegistryObject<BaseChestBlock>> NORMAL_CHESTS = new ArrayList<>();
    public static final ArrayList<RegistryObject<DeathChestBlock>> DEATH_CHESTS = new ArrayList<>();

    public static final RegistryObject<BaseChestBlock> GOLDEN_CHEST = registerNormal("golden_chest", stack -> {
        if (stack.is(ToolItems.GOLDEN_KEY.get())) {
            stack.shrink(1);
            return true;
        }
        return false;
    });
    public static final RegistryObject<DeathChestBlock> DEATH_GOLDEN_CHEST = registerDeath("death_golden_chest");
    public static final RegistryObject<BaseChestBlock> SHADOW_CHEST = registerNormal("shadow_chest", stack -> stack.is(ToolItems.SHADOW_KEY.get()));
    public static final RegistryObject<BaseChestBlock> FROZEN_CHEST = registerNormal("frozen_chest", null);
    public static final RegistryObject<BaseChestBlock> IVY_CHEST = registerNormal("ivy_chest", null);
    public static final RegistryObject<BaseChestBlock> RICH_MAHOGANY_CHEST = registerNormal("rich_mahogany_chest", null);
    public static final RegistryObject<BaseChestBlock> MARBLE_CHEST = registerNormal("marble_chest", null);
    public static final RegistryObject<BaseChestBlock> GRANITE_CHEST = registerNormal("granite_chest", null);
    public static final RegistryObject<BaseChestBlock> WATER_CHEST = registerNormal("water_chest", null);
    public static final RegistryObject<BaseChestBlock> SKYWARE_CHEST = registerNormal("skyware_chest", null);
    public static final RegistryObject<DeathChestBlock> DEATH_WOODEN_CHEST = registerDeath("death_wooden_chest");
    public static final RegistryObject<BaseChestBlock> SANDSTONE_CHEST = registerNormal("sandstone_chest", null);
    public static final RegistryObject<BaseChestBlock> LIVING_WOOD_CHEST = registerNormal("living_wood_chest", null);
    public static final RegistryObject<BaseChestBlock> DUNGEON_CHEST = registerNormal("dungeon_chest", stack -> {
        if (stack.is(ToolItems.GOLDEN_DUNGEON_KEY.get())) {
            stack.shrink(1);
            return true;
        }
        return false;
    });
    public static final Supplier<BlockEntityType<BaseChestBlock.BEntity>> BASE_CHEST_ENTITY = ModBlocks.BLOCK_ENTITIES.register("base_chest_entity", () -> BlockEntityType.Builder.of(BaseChestBlock.BEntity::new, NORMAL_CHESTS.stream().map(RegistryObject::get).toArray(Block[]::new)).build(DSL.remainderType()));
    public static final Supplier<BlockEntityType<DeathChestBlock.BEntity>> DEATH_CHEST_ENTITY = ModBlocks.BLOCK_ENTITIES.register("death_chest_entity", () -> BlockEntityType.Builder.of(DeathChestBlock.BEntity::new, DEATH_CHESTS.stream().map(RegistryObject::get).toArray(Block[]::new)).build(DSL.remainderType()));

    public static final RegistryObject<BiomeChestBlock> JUNGLE_CHEST = registerWithItem("jungle_chest", () -> new BiomeChestBlock(stack -> stack.is(ToolItems.JUNGLE_KEY.get())));
    public static final RegistryObject<BiomeChestBlock> CORRUPTION_CHEST = registerWithItem("corruption_chest", () -> new BiomeChestBlock(stack -> stack.is(ToolItems.CORRUPTION_KEY.get())));
    public static final RegistryObject<BiomeChestBlock> CRIMSON_CHEST = registerWithItem("crimson_chest", () -> new BiomeChestBlock(stack -> stack.is(ToolItems.CRIMSON_KEY.get())));
    public static final RegistryObject<BiomeChestBlock> HALLOWED_CHEST = registerWithItem("hallowed_chest", () -> new BiomeChestBlock(stack -> stack.is(ToolItems.HALLOWED_KEY.get())));
    public static final RegistryObject<BiomeChestBlock> ICE_CHEST = registerWithItem("ice_chest", () -> new BiomeChestBlock(stack -> stack.is(ToolItems.FROZEN_KEY.get())));
    public static final RegistryObject<BiomeChestBlock> DESERT_CHEST = registerWithItem("desert_chest", () -> new BiomeChestBlock(stack -> stack.is(ToolItems.DESERT_KEY.get())));
    public static final RegistryObject<BiomeChestBlock> OCEAN_CHEST = registerWithItem("ocean_chest", () -> new BiomeChestBlock(stack -> stack.is(ToolItems.OCEAN_KEY.get())));
    public static final RegistryObject<BiomeChestBlock> UNIVERSE_CHEST = registerWithItem("universe_chest", () -> new BiomeChestBlock(stack -> stack.is(ToolItems.UNIVERSE_KEY.get())));
    public static final RegistryObject<BiomeChestBlock> MECHANIC_SAFE_CHEST = registerWithItem("mechanic_safe_chest", () -> new BiomeChestBlock(stack -> stack.is(ToolItems.MECHANIC_SAFE_KEY.get())));
    public static final Supplier<BlockEntityType<BiomeChestBlock.BEntity>> BIOME_CHEST_ENTITY = BLOCK_ENTITIES.register("biome_chest_entity", () -> BlockEntityType.Builder.of(BiomeChestBlock.BEntity::new, JUNGLE_CHEST.get(), CORRUPTION_CHEST.get(), CRIMSON_CHEST.get(), HALLOWED_CHEST.get(), ICE_CHEST.get(), DESERT_CHEST.get(), OCEAN_CHEST.get(), UNIVERSE_CHEST.get(), MECHANIC_SAFE_CHEST.get()).build(DSL.remainderType()));

    private static RegistryObject<BaseChestBlock> registerNormal(String id, @Nullable Predicate<ItemStack> predicate) {
        RegistryObject<BaseChestBlock> block = BLOCKS.register(id, () -> new BaseChestBlock(predicate == null ? null : (stack, state, level, pos, player, hand, hitResult) -> {
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
        ModItems.BLOCK_ITEMS.register(id, () -> new BaseChestBlock.BItem(block.get(), new Item.Properties()));
        NORMAL_CHESTS.add(block);
        return block;
    }

    private static RegistryObject<DeathChestBlock> registerDeath(String id) {
        RegistryObject<DeathChestBlock> block = registerWithItem(id, DeathChestBlock::new);
        DEATH_CHESTS.add(block);
        return block;
    }

    private static <B extends Block> RegistryObject<B> registerWithItem(String id, Supplier<B> supplier) {
        RegistryObject<B> block = BLOCKS.register(id, supplier);
        ModItems.BLOCK_ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
