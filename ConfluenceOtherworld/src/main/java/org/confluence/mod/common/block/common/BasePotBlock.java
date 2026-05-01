package org.confluence.mod.common.block.common;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.CoinPortalEntity;
import org.confluence.mod.common.gameevent.GoblinArmyGameEvent;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.ArrowItems;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.worldgen.secret_seed.ForTheWorthy;
import org.confluence.mod.util.DateUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.init.entity.TEBossEntities;
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state;
    }

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public @Nullable PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return PathType.BLOCKED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return voxelShape;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
        dropSequence(level, pos);
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        dropSequence(level, pos);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        BlockPos blockPos = hit.getBlockPos();
        Entity entity = LibUtils.getOwner(projectile.getOwner());
        if (level.destroyBlock(blockPos, true, entity)) {
            if (entity instanceof Player player) {
                player.awardStat(Stats.BLOCK_MINED.get(this));
            }
            dropSequence(level, blockPos);
        }
    }

    private void dropSequence(Level level, BlockPos blockPos) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        Vec3 center = blockPos.getCenter();
        if (summonHole(serverLevel, center)) return;
        if (dropGoldKey(serverLevel, blockPos, center)) return;
        if (ForTheWorthy.summonPoweredCreeper(serverLevel, blockPos)) return;
        if (dropPotion(serverLevel, blockPos, center)) return;
        if (dropWormhole(serverLevel, center)) return;
        boolean flag = switch (serverLevel.random.nextInt(7)) {
            case 0 -> dropHeart(serverLevel, blockPos, center);
            case 1 -> dropTorch(serverLevel, blockPos, center);
            case 2 -> dropAmmo(serverLevel, center);
            case 3 -> dropHeal(serverLevel, blockPos, center);
            case 4 -> dropBomb(serverLevel, blockPos, center);
            case 5 -> dropRope(serverLevel, blockPos, center);
            case 6 -> dropMoney(serverLevel, blockPos, center);
            default -> false;
        };
        if (!flag) dropMoney(serverLevel, blockPos, center);
    }

    private boolean summonHole(ServerLevel level, Vec3 center) {
        if (level.random.nextFloat() < moneyHoleChance) {
            CoinPortalEntity moneyHole = new CoinPortalEntity(level, center);
            moneyHole.setDeltaMovement(0.0, 0.2, 0.0);
            level.addFreshEntity(moneyHole);
            return true;
        }
        return false;
    }

    private boolean dropGoldKey(ServerLevel level, BlockPos blockPos, Vec3 center) {
        if (level.random.nextFloat() < 0.0286F) {
            Structure structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(ModStructures.Keys.DUNGEON);
            if (structure != null) {
                int chunkX = SectionPos.blockToSectionCoord(blockPos.getX());
                int chunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());
                LongSet structureRefs = level.getChunk(chunkX, chunkZ, ChunkStatus.STRUCTURE_REFERENCES).getReferencesForStructure(structure);
                for (long i : structureRefs) {
                    SectionPos sectionPos = SectionPos.of(new ChunkPos(i), level.getMinSection());
                    StructureStart structureStart = level.structureManager().getStartForStructure(sectionPos, structure, level.getChunk(sectionPos.x(), sectionPos.z(), ChunkStatus.STRUCTURE_STARTS));
                    if (structureStart != null && structureStart.isValid() && structureStart.getBoundingBox().isInside(blockPos)) { // getBoundingBox已优化过缓存
                        LibUtils.createItemEntity(ToolItems.GOLDEN_DUNGEON_KEY.toStack(), center, level, 0);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean dropPotion(ServerLevel level, BlockPos blockPos, Vec3 center) {
        if (level.random.nextFloat() < (LibUtils.isAtLeastExpert(level, blockPos) ? 0.0444F : 0.0222F)) {
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
                    case 10 -> BATTLE_POTION.get();
                    case 11 -> HEART_REACH_POTION.get();
                    case 12 -> TITAN_POTION.get();
                    default -> null;
                };
                if (level.random.nextFloat() < 0.2F) {
                    // todo 返回药水
                }
            } else if (y <= OverworldUtils.getUndergroundY()) {
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
            } else if (y <= OverworldUtils.getSurfaceY()) {
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
            } else if (y <= OverworldUtils.getSpaceY()) {
                item = switch (level.random.nextInt(10)) {
                    case 0 -> IRON_SKIN_POTION.get();
                    case 1 -> SHINE_POTION.get();
                    case 2 -> NIGHT_OWL_POTION.get();
                    case 3 -> SWIFTNESS_POTION.get();
                    case 4 -> MINING_POTION.get();
                    case 5 -> CALMING_POTION.get();
                    case 6 -> BUILDER_POTION.get();
                    default -> RECALL_POTION.get();
                };
            }
            if (item != null) {
                LibUtils.createItemEntity(item.getDefaultInstance(), center, level, 0);
                return true;
            }
        }
        return false;
    }

    private boolean dropWormhole(ServerLevel level, Vec3 center) {
        if (level.players().size() > 1 && level.random.nextFloat() < 0.0333F) {
            LibUtils.createItemEntity(WORMHOLE_POTION.toStack(), center, level, 0);
            return true;
        }
        return false;
    }

    private boolean dropHeart(ServerLevel level, BlockPos blockPos, Vec3 center) {
        Optional<? extends Player> optional = level.players().stream().min((a, b) -> (int) (a.distanceToSqr(center) - b.distanceToSqr(center)));
        if (optional.isPresent()) {
            Player player = optional.get();
            if (player.getHealth() < player.getMaxHealth()) {
                int amount = 1;
                if (level.random.nextBoolean()) amount++;
                if (LibUtils.isAtLeastExpert(level, blockPos)) {
                    if (level.random.nextBoolean()) amount++;
                    if (level.random.nextBoolean()) amount++;
                }
                LibUtils.createItemEntity(DateUtils.getHeartItem(), amount, center, level, 0);
            } else if (player.getInventory().hasAnyMatching(itemStack -> itemStack.getCount() < 20 && itemStack.is(ModTags.Items.TORCH))) {
                return dropTorch(level, blockPos, center);
            } else {
                return dropMoney(level, blockPos, center);
            }
        }
        return false;
    }

    // todo 掉火把
    private boolean dropTorch(ServerLevel level, BlockPos blockPos, Vec3 center) {
//        boolean tundra = this == TUNDRA_POTS.get();
        int amount = /*tundra ? level.random.nextInt(2, 7) : */level.random.nextInt(4, 13);
        Item item;
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
        item = Items.TORCH;
//            }
//        }
        LibUtils.createItemEntity(item, amount, center, level, 0);
        return true;
    }

    private boolean dropAmmo(ServerLevel level, Vec3 center) {
        int amount = level.random.nextInt(10, 21);
        Item item;
        boolean isHardmode = KillBoard.INSTANCE.getGamePhase().isHardmode();
        if (level.random.nextBoolean()) {
            item = isHardmode ? ConsumableItems.GRENADE.get() : ConsumableItems.SHURIKEN.get();
        } else if (level.dimension() == Level.NETHER) {
            item = ArrowItems.HELLFIRE_ARROW.get();
        } else if (isHardmode) {
            if (level.random.nextBoolean()) {
                item = ArrowItems.UNHOLY_ARROW.get();
            } else {
                item = level.random.nextBoolean() ? TGItems.SILVER_BULLET.get() : TGItems.TUNGSTEN_BULLET.get();
            }
        } else {
            item = Items.ARROW;
        }
        LibUtils.createItemEntity(item, amount, center, level, 0);
        return true;
    }

    private boolean dropHeal(ServerLevel level, BlockPos blockPos, Vec3 center) {
        Item item;
        if (level.dimension() == Level.NETHER || KillBoard.INSTANCE.getGamePhase().isHardmode()) {
            item = PotionItems.HEALING_POTION.get();
        } else {
            item = PotionItems.LESSER_HEALING_POTION.get();
        }
        int amount = 1;
        if (LibUtils.isAtLeastExpert(level, blockPos) && level.random.nextFloat() < 0.3333F) {
            amount++;
        }
        LibUtils.createItemEntity(item, amount, center, level, 0);
        return true;
    }

    private boolean dropBomb(ServerLevel level, BlockPos blockPos, Vec3 center) {
        Item item;
        if (this == UNDERGROUND_DESERT_POT.get()) {
            item = ConsumableItems.SCARAB_BOMB.get();
        } else if (level.dimension() == OverworldUtils.dimension()) {
            item = ConsumableItems.BOMB.get();
        } else {
            return dropRope(level, blockPos, center);
        }
        LibUtils.createItemEntity(item, level.random.nextInt(1, LibUtils.isAtLeastExpert(level, blockPos) ? 5 : 8), center, level, 0);
        return true;
    }

    private boolean dropRope(ServerLevel level, BlockPos blockPos, Vec3 center) {
        if (level.dimension() == Level.NETHER || KillBoard.INSTANCE.getGamePhase().isHardmode()) {
            return dropMoney(level, blockPos, center);
        } else {
            LibUtils.createItemEntity(ModBlocks.ROPE.get().asItem(), level.random.nextInt(5, 11), center, level, 0);
            return true;
        }
    }

    private boolean dropMoney(ServerLevel level, BlockPos blockPos, Vec3 center) {
        if (!CommonConfigs.ENEMY_DROPS_MONEY.get()) return false;
        float random = level.random.nextFloat();
        float ratio = 1.0F;
        double y = center.y;
        if (y <= OverworldUtils.getUndergroundY()) {
            ratio = 1.25F;
        } else if (y <= OverworldUtils.getSurfaceY()) {
            ratio = 0.75F;
        } else if (y <= OverworldUtils.getSpaceY()) {
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
        if (LibUtils.isAtLeastExpert(level, blockPos)) {
            ratio *= 2.5F;
            random = level.random.nextFloat();
            if (random < 0.25F) {
                ratio *= 1.25F;
            } else if (random < 0.5F) {
                ratio *= 1.25F;
            } else {
                ratio *= 1.75F;
            }
        }
        int defeated = KillBoard.INSTANCE.countDefeated(
                TEBossEntities.EYE_OF_CTHULHU.get(),
                TEBossEntities.EATER_OF_WORLDS.get(),
                TEBossEntities.BRAIN_OF_CTHULHU.get(),
                TEBossEntities.QUEEN_BEE.get(),
                TEBossEntities.SKELETRON.get(),
                TEBossEntities.THE_TWINS.get(),
                TEBossEntities.PLANTERA.get()
        ) + KillBoard.INSTANCE.countDefeated(
                GoblinArmyGameEvent.KEY
        );
        for (int i = 0; i < defeated; i++) {
            ratio *= 1.1F; // todo 毁灭者、机械骷髅王、石巨人、海盗入侵、雪人军团
        }
        ratio *= moneyRatio;
        int amount = (int) Math.ceil(level.random.nextInt(80, 358) * ratio);
        ModUtils.dropMoney(amount, center.x, y, center.z, level);
        return true;
    }
}
