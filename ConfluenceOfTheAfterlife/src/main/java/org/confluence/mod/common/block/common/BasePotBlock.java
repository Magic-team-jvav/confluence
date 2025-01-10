package org.confluence.mod.common.block.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.entity.CoinPortalEntity;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ArrowItems;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.util.ModUtils;
import org.confluence.terra_guns.common.init.TGItems;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static org.confluence.mod.common.init.block.PotBlocks.UNDERGROUND_DESERT_POT;
import static org.confluence.mod.common.init.item.PotionItems.*;

public class BasePotBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final VoxelShape voxelShape;
    private final float moneyRatio;
    private final float moneyHoleChance;

    public BasePotBlock(float moneyRatio, float moneyHoleChance, VoxelShape voxelShape) {
        super(Properties.of().sound(SoundType.DECORATED_POT).instabreak().noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
        this.voxelShape = voxelShape;
        this.moneyRatio = moneyRatio;
        this.moneyHoleChance = moneyHoleChance;
        registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidstate = placeContext.getLevel().getFluidState(placeContext.getClickedPos());
        return defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return pState;
    }

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public @Nullable PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return PathType.BLOCKED;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return voxelShape;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
        pPlayer.awardStat(Stats.BLOCK_MINED.get(this));
        pPlayer.causeFoodExhaustion(0.005F);
        dropSequence(pLevel, pPos);
    }

    @Override
    public void wasExploded(Level pLevel, BlockPos pPos, Explosion pExplosion) {
        dropSequence(pLevel, pPos);
    }

    @Override
    public void onProjectileHit(Level pLevel, BlockState pState, BlockHitResult pHit, Projectile pProjectile) {
        BlockPos blockPos = pHit.getBlockPos();
        Entity entity = pProjectile.getOwner();
        if (pLevel.destroyBlock(blockPos, true, entity)) {
            if (entity instanceof Player player) {
                player.awardStat(Stats.BLOCK_MINED.get(this));
            }
            dropSequence(pLevel, blockPos);
        }
    }

    // todo 专家模式掉落
    private void dropSequence(Level level, BlockPos blockPos) {
        if (level.isClientSide) return;
        Vec3 center = blockPos.getCenter();
        if (summonHole(level, center)) return;
        // 如果罐子位于天然地牢墙前方且低于地表地层，有 1/35 (2.86%) 的几率掉落金钥匙。若掉落，则流程结束。
        // 如果玩家正在游玩秘密世界种子 for the worthy，有 1/4 (25%) 的几率掉落一个点燃的炸弹。若掉落，则流程结束。
        if (dropPotion(level, center)) return;
        if (dropWormhole(level, center)) return;
        boolean flag = switch (level.random.nextInt(7)) {
            case 0 -> dropHeart(level, blockPos, center);
            case 1 -> dropTorch(level, blockPos, center);
            case 2 -> dropAmmo(level, center);
            case 3 -> dropHeal(level, center);
            case 4 -> dropBomb(level, center);
            case 5 -> dropRope(level, center);
            case 6 -> dropMoney(level, center);
            default -> false;
        };
        if (!flag) dropMoney(level, center);
    }

    private boolean summonHole(Level level, Vec3 center) {
        if (level.random.nextFloat() < moneyHoleChance) {
            CoinPortalEntity moneyHole = new CoinPortalEntity(level, center);
            moneyHole.setDeltaMovement(0.0, 0.2, 0.0);
            level.addFreshEntity(moneyHole);
            return true;
        }
        return false;
    }

    private boolean dropPotion(Level level, Vec3 center) {
        // 专家模式 0.0444F
        if (level.random.nextFloat() < 0.0222F) {
            double y = center.y;
            Item item = null;
            if (level.dimension() == Level.NETHER) {
                item = switch (level.random.nextInt(14)) {
                    case 0 -> SPELUNKER_POTION.get();
                    case 1 -> FEATHERFALL_POTION.get();
                    case 2 -> MANA_REGENERATION_POTION.get();
                    case 3 -> OBSIDIAN_SKIN_POTION.get();
                    case 4 -> MAGIC_POWER_POTION.get();
                    case 5 -> INVISIBILITY_POTION.get();
                    case 6 -> HUNTER_POTION.get();
                    case 7 -> GRAVITATION_POTION.get();
                    case 8 -> THORNS_POTION.get();
                    case 9 -> WATER_WALKING_POTION.get();
                    // 战斗
                    case 11 -> HEART_REACH_POTION.get();
                    case 12 -> TITAN_POTION.get();
                    default -> null;
                };
                if (level.random.nextFloat() < 0.2F) {
                    // todo 返回药水
                }
            } else if (y <= 0.0) {
                item = switch (level.random.nextInt(15)) {
                    case 0 -> SPELUNKER_POTION.get();
                    case 1 -> FEATHERFALL_POTION.get();
                    case 2 -> NIGHT_OWL_POTION.get();
                    case 3, 4 -> WATER_WALKING_POTION.get();
                    case 5 -> ARCHERY_POTION.get();
                    case 6 -> GRAVITATION_POTION.get();
                    case 7 -> HUNTER_POTION.get();
                    case 8 -> INVISIBILITY_POTION.get();
                    case 9 -> THORNS_POTION.get();
                    case 10 -> MINING_POTION.get();
                    case 11 -> HEART_REACH_POTION.get();
                    case 12 -> FLIPPER_POTION.get();
                    case 13 -> DANGERSENSE_POTION.get();
                    default -> RECALL_POTION.get();
                };
            } else if (y <= 63.0) {
                item = switch (level.random.nextInt(11)) {
                    case 0 -> REGENERATION_POTION.get();
                    case 1 -> SHINE_POTION.get();
                    case 2 -> SWIFTNESS_POTION.get();
                    case 3 -> ARCHERY_POTION.get();
                    case 4 -> GILLS_POTION.get();
                    case 5 -> HUNTER_POTION.get();
                    case 6 -> MINING_POTION.get();
                    case 7 -> DANGERSENSE_POTION.get();
                    default -> RECALL_POTION.get();
                };
            } else if (y <= 240.0) {
                item = switch (level.random.nextInt(10)) {
                    case 0 -> IRON_SKIN_POTION.get();
                    case 1 -> SHINE_POTION.get();
                    case 2 -> NIGHT_OWL_POTION.get();
                    case 3 -> SWIFTNESS_POTION.get();
                    case 4 -> MINING_POTION.get();
                    // 镇静
                    case 6 -> BUILDER_POTION.get();
                    default -> RECALL_POTION.get();
                };
            }
            if (item != null) {
                ModUtils.createItemEntity(item, 1, center.x, y, center.z, level, 0);
                return true;
            }
        }
        return false;
    }

    private boolean dropWormhole(Level level, Vec3 center) {
        if (level.players().size() > 1 && level.random.nextFloat() < 0.0333F) {
            // todo 生成虫洞药水
            return true;
        }
        return false;
    }

    private boolean dropHeart(Level level, BlockPos blockPos, Vec3 center) {
        Optional<? extends Player> optional = level.players().stream().min((a, b) -> (int) (a.distanceToSqr(center) - b.distanceToSqr(center)));
        if (optional.isPresent()) {
            Player player = optional.get();
            if (player.getHealth() < player.getMaxHealth()) {
                int amount = 1;
                if (level.random.nextBoolean()) amount++;
                /* 在专家模式中，有 1/8 的几率掉落 1 个心，3/8 的几率掉落 2 个心，3/8 的几率掉落 3 个心，以及 1/8 的几率掉落 4 个心。*/
                ModUtils.createItemEntity(ModItems.HEART.get(), amount, center.x, center.y, center.z, level, 0);
            } else if (player.getInventory().hasAnyMatching(itemStack -> itemStack.getCount() < 20 && itemStack.is(ModTags.Items.TORCH))) {
                return dropTorch(level, blockPos, center);
            } else {
                return dropMoney(level, center);
            }
        }
        return false;
    }

    private boolean dropTorch(Level level, BlockPos blockPos, Vec3 center) {
//        boolean tundra = this == TUNDRA_POTS.get();
//        int amount = tundra ? level.random.nextInt(2, 7) : level.random.nextInt(4, 13);
//        Item item;
//        if (level.getFluidState(blockPos).is(FluidTags.WATER)) {
//            if (tundra) {
//                item = ModItems.STICKY_GLOW_STICK.get();
//            } else {
//                item = ModItems.GLOW_STICK.get();
//            }
//        } else {
//            if (tundra) {
//                item = Torches.ICE_TORCH.item.get();
//            } else if (this == TR_CRIMSON_POTS.get()) {
//                item = Torches.CRIMSON_TORCH.item.get();
//            } else if (this == JUNGLE_POTS.get()) {
//                item = Torches.JUNGLE_TORCH.item.get();
//            } else if (this == CORRUPTION_POTS.get()) {
//                item = Torches.CORRUPT_TORCH.item.get();
//            } else if (this == UNDERGROUND_DESERT_POTS.get()) {
//                item = Torches.DESERT_TORCH.item.get();
//            } else {
//                item = Items.TORCH;
//            }
//        }
//        ModUtils.createItemEntity(item, amount, center.x, center.y, center.z, level, 0); todo
        return true;
    }

    private boolean dropAmmo(Level level, Vec3 center) {
        int amount = level.random.nextInt(10, 21);
        Item item = Items.ARROW;
        boolean hardCore = ConfluenceData.get((ServerLevel) level).isHardcore();
        if (level.random.nextBoolean()) {
            item = hardCore ? ConsumableItems.GRENADE.get() : ConsumableItems.SHURIKEN.get();
        } else if (level.dimension() == Level.NETHER) {
            item = ArrowItems.HELLFIRE_ARROW.get();
        } else if (hardCore) {
            if (level.random.nextBoolean()) {
                item = ArrowItems.UNHOLY_ARROW.get();
            } else {
                item = level.random.nextBoolean() ? TGItems.SILVER_BULLET.get() : TGItems.TUNGSTEN_BULLET.get();
            }
        }
        ModUtils.createItemEntity(item, amount, center.x, center.y, center.z, level, 0);
        return true;
    }

    private boolean dropHeal(Level level, Vec3 center) {
        Item item;
        if (level.dimension() == Level.NETHER || ConfluenceData.get((ServerLevel) level).isHardcore()) {
            item = PotionItems.HEALING_POTION.get();
        } else {
            item = PotionItems.LESSER_HEALING_POTION.get();
        }
        // 在专家模式，有 1/3 的几率额外掉落 1 个
        ModUtils.createItemEntity(item, 1, center.x, center.y, center.z, level, 0);
        return true;
    }

    private boolean dropBomb(Level level, Vec3 center) {
        Item item;
        if (this == UNDERGROUND_DESERT_POT.get()) {
            item = ConsumableItems.SCARAB_BOMB.get();
        } else if (level.dimension() == Level.OVERWORLD) {
            item = ConsumableItems.BOMB.get();
        } else {
            return dropRope(level, center);
        }
        // 专家模式 1-7 个
        ModUtils.createItemEntity(item, level.random.nextInt(1, 5), center.x, center.y, center.z, level, 0);
        return true;
    }

    private boolean dropRope(Level level, Vec3 center) {
        if (level.dimension() == Level.NETHER || ConfluenceData.get((ServerLevel) level).isHardcore()) {
            return dropMoney(level, center);
        } else {
            ModUtils.createItemEntity(Blocks.SCAFFOLDING.asItem(), level.random.nextInt(5, 11), center.x, center.y, center.z, level, 0);
            return true;
        }
    }

    private boolean dropMoney(Level level, Vec3 center) {
        if (!CommonConfigs.DROP_MONEY.get()) return false;
        float random = level.random.nextFloat();
        float ratio = 1.0F;
        double y = center.y;
        if (y <= 0.0) {
            ratio = 1.25F;
        } else if (y <= 63.0) {
            ratio = 0.75F;
        } else if (y <= 240.0) {
            ratio = 0.5F;
        } else if (random < 0.05F) {
            ratio = Mth.nextFloat(level.random, 1.5F, 2.0F);
        } else if (random < 0.0625F) {
            ratio = Mth.nextFloat(level.random, 1.4F, 1.8F);
        } else if (random < 0.0833F) {
            ratio = Mth.nextFloat(level.random, 1.2F, 1.4F);
        } else if (random < 0.125F) {
            ratio = Mth.nextFloat(level.random, 1.1F, 1.2F);
        } else if (random < 0.25F) {
            ratio = Mth.nextFloat(level.random, 1.05F, 1.1F);
        }
        // 专家模式增益
        // 击败增益
        ratio *= moneyRatio;
        int amount = (int) Math.ceil(level.random.nextInt(8, 34) * ratio);
        ModUtils.dropMoney(amount, center.x, y, center.z, level);
        return true;
    }
}
