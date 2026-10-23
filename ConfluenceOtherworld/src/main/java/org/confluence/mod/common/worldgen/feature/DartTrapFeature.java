package org.confluence.mod.common.worldgen.feature;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.confluence.lib.util.LibFeatureUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.ModFeatures;

import java.util.ArrayList;
import java.util.List;

public class DartTrapFeature extends Feature<DartTrapFeature.Config> {
    public DartTrapFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos blockPos = pContext.origin();
        if (!LibFeatureUtils.isPosAir(level, blockPos)) return false;
        BlockPos.MutableBlockPos mutablePos = blockPos.mutable();
        for (int v = 1; v <= config.maxSearchDown && LibFeatureUtils.isPosAir(level, mutablePos); ++v) {
            mutablePos.move(0, -1, 0);
        }
        if (!LibFeatureUtils.isPosSturdy(level, mutablePos, Direction.UP)) return false;
        BlockPos dartPos = mutablePos.offset(0, 2, 0);
        if (!LibFeatureUtils.isPosAir(level, dartPos)) return false;

        // 收集全部可用的朝向后再随机取一个。
        // 原来按 LibUtils.HORIZONTAL 的固定顺序取第一个可用方向，会让朝东时优先命中“东侧墙壁”，
        // 导致毒镖陷阱的朝向严重偏向某一侧（实测西向约占六成），必须打散。
        List<Candidate> candidates = new ArrayList<>(LibUtils.HORIZONTAL.length);
        for (Direction direction : LibUtils.HORIZONTAL) {
            BlockPos endpoint = findEndpoint(level, config, dartPos, direction);
            if (endpoint != null) candidates.add(new Candidate(direction, endpoint));
        }
        if (candidates.isEmpty()) return false;

        Tuple<BlockPos, BlockState> pressurePlate = ModFeatures.getPressurePlate(level, mutablePos);
        BlockPos platePos = pressurePlate.getA();
        BlockState oldPlateState = level.getBlockState(platePos);
        if (!LibFeatureUtils.safeSetBlock(level, platePos, pressurePlate.getB(), ModFeatures.IS_REPLACEABLE)) return false;
        RandomSource random = pContext.random();
        while (!candidates.isEmpty()) {
            Candidate candidate = candidates.remove(random.nextInt(candidates.size()));
            BlockState dartTrap = ModFeatures.getDartTrap(level, candidate.pos(), candidate.direction().getOpposite());
            if (!LibFeatureUtils.safeSetBlock(level, candidate.pos(), dartTrap, ModFeatures.IS_REPLACEABLE)) continue;
            INetworkEntity dart = ModFeatures.getNetworkEntity(level, candidate.pos());
            INetworkEntity plate = ModFeatures.getNetworkEntity(level, platePos);
            if (dart != null && plate != null) dart.connectTo(0x00FF00, platePos, plate);
            return true;
        }
        level.setBlock(platePos, oldPlateState, 2);
        return false;
    }

    /**
     * 从起点沿指定方向寻找可嵌入毒镖陷阱的墙壁，找不到时返回null。
     * <p>带方块实体的方块（压力板、箱子、刷怪笼、其它机关等）一律不作为墙壁，
     * 避免地物之间互相覆盖导致机关被破坏或触发失效。</p>
     */
    private static BlockPos findEndpoint(WorldGenLevel level, Config config, BlockPos dartPos, Direction direction) {
        BlockPos.MutableBlockPos copy = dartPos.mutable();
        int h;
        for (h = 1; h <= config.maxDartDistance && LibFeatureUtils.ensureCanWrite(level, copy) && LibFeatureUtils.isPosAir(level, copy); ++h) {
            copy.move(direction);
        }
        if (!LibFeatureUtils.ensureCanWrite(level, copy)) return null;
        if (h < 4) return null;
        if (level.isStateAtPosition(copy, blockState -> blockState.isAir() || blockState.hasBlockEntity() || blockState.getCollisionShape(level, copy).isEmpty())) return null;
        return copy.immutable();
    }

    public record Candidate(Direction direction, BlockPos pos) {}

    public record Config(int maxDartDistance, int maxSearchDown) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PortCodecExtension.lenientOptionalFieldOf(ExtraCodecs.POSITIVE_INT, "max_dart_distance", 24).forGetter(Config::maxDartDistance),
                PortCodecExtension.lenientOptionalFieldOf(ExtraCodecs.POSITIVE_INT, "max_search_down", 32).forGetter(Config::maxSearchDown)
        ).apply(instance, Config::new));
    }
}
