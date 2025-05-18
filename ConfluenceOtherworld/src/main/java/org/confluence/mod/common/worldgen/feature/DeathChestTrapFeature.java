package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.storage.loot.LootTable;
import org.confluence.lib.util.FeatureUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.mixed.IBaseContainerBlockEntity;

import java.util.Optional;

public class DeathChestTrapFeature extends Feature<DeathChestTrapFeature.Config> {
    public DeathChestTrapFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        RandomSource random = pContext.random();
        BlockPos blockPos = pContext.origin();
        if (!FeatureUtils.isPosAir(level, blockPos)) return false;

        BlockPos.MutableBlockPos mutablePos = blockPos.mutable();
        for (int v = 1; v <= config.maxSearchDown && FeatureUtils.isPosAir(level, mutablePos); ++v) {
            mutablePos.move(0, -1, 0);
        }
        if (level.isStateAtPosition(mutablePos, BlockBehaviour.BlockStateBase::liquid)) return false;

        BlockPos chestPos = mutablePos.above();
        BlockState chestState = StructurePiece.reorient(level, chestPos, FunctionalBlocks.DEATH_CHEST_BLOCK.get().defaultBlockState().setValue(BaseChestBlock.UNLOCKED, true));
        if (FeatureUtils.safeSetBlock(level, chestPos, chestState, ModFeatures.IS_REPLACEABLE)) {
            RandomizableContainer.setBlockEntityLootTable(level, random, chestPos, config.lootTable);
            INetworkEntity chest = ModFeatures.getNetworkEntity(level, chestPos);
            if (chest != null && chest.getSelf() instanceof BaseChestBlock.Entity entity) {
                if (ModSecretSeeds.NO_TRAPS.match(level.getLevel().getServer()) && random.nextBoolean()) {
                    entity.variant = BaseChestBlock.Variant.UNLOCKED_NORMAL;
                } else {
                    entity.variant = BaseChestBlock.Variant.UNLOCKED_GOLDEN;
                }
                ((IBaseContainerBlockEntity) entity).confluence$setCustomName(Component.translatable("block.confluence.base_chest_block." + entity.variant.getSerializedName()));
                boolean b = placeDartTraps(config, level, chestPos, chest);
                boolean b1 = placeBoulders(config, random, level, chestPos, chest);
                boolean b2 = placeTNTs(config, random, level, chestPos, chest);
                return b || b1 || b2;
            }
        }
        return false;
    }

    private static boolean placeTNTs(Config config, RandomSource random, WorldGenLevel level, BlockPos chestPos, INetworkEntity chest) {
        boolean succeed = false;
        BlockState blockState = FunctionalBlocks.INSTANTANEOUS_EXPLOSION_TNT.get().defaultBlockState();
        int amount = config.tntAmount;
        int half = amount / 2;
        for (BlockPos pos : BlockPos.randomBetweenClosed(random, amount, chestPos.getX() - half, chestPos.getY() - 3, chestPos.getZ() - half, chestPos.getX() + half, chestPos.getY() - 2, chestPos.getZ() + half)) {
            BlockPos.MutableBlockPos mutable = pos.mutable();
            for (Direction direction : LibUtils.DIRECTIONS) {
                if (direction == Direction.UP) continue;
                if (level.isStateAtPosition(pos.relative(direction), BlockBehaviour.BlockStateBase::isAir)) {
                    mutable.move(direction.getOpposite());
                }
            }
            if (FeatureUtils.safeSetBlock(level, mutable, blockState, ModFeatures.IS_REPLACEABLE)) {
                INetworkEntity tnt = ModFeatures.getNetworkEntity(level, mutable);
                if (tnt != null) {
                    tnt.connectTo(0x0000FF, chestPos, chest);
                    succeed = true;
                }
            }
        }
        return succeed;
    }

    private static boolean placeBoulders(Config config, RandomSource random, WorldGenLevel level, BlockPos chestPos, INetworkEntity chest) {
        boolean succeed = false;
        int maxBoulderHeight = config.maxBoulderHeight;
        int amount = config.boulderAmount;
        int half = amount / 2;
        for (BlockPos pos : BlockPos.randomBetweenClosed(random, amount, chestPos.getX() - half, chestPos.getY(), chestPos.getZ() - half, chestPos.getX() + half, chestPos.getY(), chestPos.getZ() + half)) {
            Optional<Column> optionalColumn = Column.scan(level, pos, maxBoulderHeight, BlockBehaviour.BlockStateBase::isAir, ModFeatures.IS_BASE_STONE);
            if (optionalColumn.isPresent() && optionalColumn.get() instanceof Column.Range range && range.height() > 4) {
                BlockPos boulderPos = pos.atY(range.ceiling());
                if (FeatureUtils.safeSetBlock(level, boulderPos, ModFeatures.getBoulder(level, random, config.boulder), ModFeatures.IS_REPLACEABLE)) {
                    INetworkEntity boulder = ModFeatures.getNetworkEntity(level, boulderPos);
                    if (boulder != null) {
                        boulder.connectTo(0xFF0000, chestPos, chest);
                        succeed = true;
                    }
                }
            }
        }
        return succeed;
    }

    private static boolean placeDartTraps(Config config, WorldGenLevel level, BlockPos chestPos, INetworkEntity chest) {
        boolean succeed = false;
        int maxDartDistance = config.maxDartDistance;
        dir:
        for (Direction direction : LibUtils.HORIZONTAL) {
            BlockPos.MutableBlockPos copy = chestPos.mutable();
            Direction opposite = direction.getOpposite();
            BlockPos firstFindPos = null;
            INetworkEntity firstFind = null;

            Direction right = direction.getClockWise();
            Direction left = right.getOpposite();
            BlockPos rightBottom = copy.relative(right);
            BlockPos leftTop = copy.above(2).relative(left);
            for (BlockPos pos : BlockPos.betweenClosed(leftTop, rightBottom)) {
                BlockPos.MutableBlockPos mutable = pos.mutable().move(direction);
                int h;
                for (h = 1; h <= maxDartDistance && FeatureUtils.isPosAir(level, mutable); ++h) {
                    mutable.move(direction);
                }
                if (h >= 16 && !level.isStateAtPosition(mutable, blockState -> blockState.isAir() || blockState.getCollisionShape(level, mutable).isEmpty())) {
                    BlockState dartTrap = ModFeatures.getDartTrap(level, mutable, opposite);
                    if (FeatureUtils.safeSetBlock(level, mutable, dartTrap, ModFeatures.IS_REPLACEABLE)) {
                        INetworkEntity dart = ModFeatures.getNetworkEntity(level, mutable);

                        BlockPos connectPos;
                        INetworkEntity connectEntity;
                        if (firstFindPos == null) {
                            firstFindPos = mutable;
                            connectPos = chestPos;
                        } else {
                            connectPos = firstFindPos;
                        }
                        if (firstFind == null) {
                            firstFind = dart;
                            connectEntity = chest;
                        } else {
                            connectEntity = firstFind;
                        }
                        if (dart != null) {
                            dart.connectTo(0x00FF00, connectPos, connectEntity);
                            succeed = true;
                        }
                    } else {
                        continue dir;
                    }
                }
            }
        }
        return succeed;
    }

    public record Config(int maxDartDistance, BlockState boulder, int boulderAmount, int maxBoulderHeight, int tntAmount, int maxSearchDown,
                         ResourceKey<LootTable> lootTable) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("max_dart_distance", 24).forGetter(Config::maxDartDistance),
                BlockState.CODEC.fieldOf("boulder").orElseGet(() -> FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState()).forGetter(Config::boulder),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("boulder_amount", 5).forGetter(Config::boulderAmount),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("max_boulder_height", 64).forGetter(Config::maxBoulderHeight),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("tnt_amount", 3).forGetter(Config::tntAmount),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("max_search_down", 32).forGetter(Config::maxSearchDown),
                ResourceKey.codec(Registries.LOOT_TABLE).lenientOptionalFieldOf("loot_table", ModLootTables.CAVE_CHESTS).forGetter(Config::lootTable)
        ).apply(instance, Config::new));
    }
}
