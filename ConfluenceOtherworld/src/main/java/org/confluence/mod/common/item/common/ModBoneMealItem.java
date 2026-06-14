package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.ConsumableItems;

import java.util.List;

public class ModBoneMealItem extends TooltipItem {
    public ModBoneMealItem(ModRarity rarity, String name) {
        super(new Properties(), rarity, "tooltip.item.confluence." + name + ".0");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            AABB aabb = player.getBoundingBox().inflate(2.5D);
            List<Entity> entities = level.getEntities(player, aabb, entity -> entity instanceof LivingEntity);
            for (Entity entity : entities) {
                if (entity.isAlive()) {
                    EntityType<?> entityType = entity.getType();
                    LivingEntity newEntity = null;
                    if (stack.is(ConsumableItems.BLOODSTAINED_POWDER.get()) && entityType == EntityType.CREEPER) {
                        entity.remove(Entity.RemovalReason.KILLED);
                        newEntity = TEMonsterEntities.BLOODY_SPORE.value().create(level);
                    } else if (stack.is(ConsumableItems.ROTTEN_BONE_DUST.get()) && entityType == EntityType.SKELETON) {
                        entity.remove(Entity.RemovalReason.KILLED);
                        newEntity = TEMonsterEntities.DECAYEDER.value().create(level);
                    }
                    if (newEntity != null) {
                        newEntity.setPos(entity.getX(), entity.getY(), entity.getZ());
                        level.addFreshEntity(newEntity);
                    }
                }
            }
        }
        if (!player.isCreative()) stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        BlockState state = level.getBlockState(context.getClickedPos());
        for (int i = 0; i < 128; i++) {
            if (!level.isClientSide) {
                BlockPos targetPos = context.getClickedPos().above();
                for (int j = 0; j < i / 16; j++) {
                    targetPos = targetPos.offset(level.random.nextInt(3) - 1, (level.random.nextInt(3) - 1) * level.random.nextInt(3) / 2, level.random.nextInt(3) - 1);
                    if (!level.getBlockState(targetPos.below()).is(state.getBlock()) ||
                            level.getBlockState(targetPos).isCollisionShapeFullBlock(level, targetPos)) {
                        break;
                    }
                }
                BlockState targetState = level.getBlockState(targetPos);
                if (targetState.isAir()) {
                    String blockType = null;
                    if (stack.is(ConsumableItems.BLOODSTAINED_POWDER.get()) &&
                            (state.is(NatureBlocks.CRIMSON_GRASS_BLOCK.get()) || state.is(NatureBlocks.CRIMSON_JUNGLE_GRASS_BLOCK.get()))) {
                        blockType = "crimson";
                    } else if (stack.is(ConsumableItems.ROTTEN_BONE_DUST.get()) &&
                            (state.is(NatureBlocks.CORRUPT_GRASS_BLOCK.get()) || state.is(NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK.get()))) {
                        blockType = "corrupt";
                    }
                    if (blockType != null) {
                        BlockState randomPlant = getRandomPlantState(level, blockType);
                        if (randomPlant.canSurvive(level, targetPos)) {
                            level.setBlock(targetPos, randomPlant, 3);
                        }
                    }
                }
            }
        }
        stack.shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private BlockState getRandomPlantState(Level level, String blockType) {
        int random = level.random.nextInt(100);
        BlockState blockState = Blocks.AIR.defaultBlockState();
        if (random < 90) {
            blockState = switch (blockType) {
                case "crimson" -> NatureBlocks.CRIMSON_GRASS.get().defaultBlockState();
                case "corrupt" -> NatureBlocks.CORRUPT_GRASS.get().defaultBlockState();
                default -> blockState;
            };
        } else if (random < 98) {
            blockState = switch (blockType) {
                case "crimson" -> NatureBlocks.VICIOUS_MUSHROOM.get().defaultBlockState();
                case "corrupt" -> NatureBlocks.VILE_MUSHROOM.get().defaultBlockState();
                default -> blockState;
            };
        } else {
            if ("crimson".equals(blockType) || "corrupt".equals(blockType)) {
                blockState = ModBlocks.DEATHWEED.get().defaultBlockState();
            }
        }
        return blockState;
    }
}
