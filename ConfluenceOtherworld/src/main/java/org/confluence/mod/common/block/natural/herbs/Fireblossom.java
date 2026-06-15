package org.confluence.mod.common.block.natural.herbs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.particle.CrossDustParticleOptions;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.common.init.item.FoodItems;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Fireblossom extends BaseHerbBlock {
    public Fireblossom() {
        super(BlockBehaviour.Properties.copy(Blocks.DANDELION).randomTicks().lightLevel(value -> value.getValue(AGE) == MAX_AGE ? 4 : 0));
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return FoodItems.FIREBLOSSOM_SEED.get();
    }

    @Override
    public boolean canBloom(ServerLevel level, BlockState state) {
        if (level.dimensionType().natural()) {
            return LibDateUtils.isWithinDayTime(LibDateUtils.getDayTime(15, 45), LibDateUtils._19$30, level) && !level.isRaining();
        }
        return true;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos blockPos, RandomSource random) {
        if (getAge(state) != MAX_AGE) return;
        int r = random.nextInt(60);
        if (r > 10) return;
        Vec3 pos = blockPos.getCenter().add(state.getOffset(level, blockPos)).offsetRandom(random, 0.3f);
        boolean light = level.random.nextBoolean();
        Vector4f curve = new Vector4f(0, 0.33f, 0.66f, 1);
        CrossDustParticleOptions sparkParticle = new CrossDustParticleOptions(false, light ? 0xffFFFF4F : 0xb0FFCF4E,
                0xb0FF944F, new Vector3f((level.random.nextFloat() - 0.5f) / 20, 0.2f, (level.random.nextFloat() - 0.5f) / 20),
                curve, 1, 8, 20, curve,
                true, true, true, false);
        level.addParticle(sparkParticle, pos.x, pos.y, pos.z, 0, 0.3f, 0);
    }
}
