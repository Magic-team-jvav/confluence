package org.confluence.mod.common.block.natural.herbs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.particle.CrossDustParticleOptions;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.common.data.saved.MoonPhase;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.init.item.FoodItems;
import org.joml.Vector4f;

public class DeathWeed extends BaseHerbBlock {
    public static final IntegerProperty PROP_LIGHT = IntegerProperty.create("level", 0, 7);

    public DeathWeed() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).randomTicks().lightLevel(value -> value.getValue(AGE) == MAX_AGE ? value.getValue(PROP_LIGHT) : 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(PROP_LIGHT));
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return FoodItems.DEATHWEED_SEED.get();
    }

    @Override
    public boolean canBloom(ServerLevel level, BlockState state) {
        return LibDateUtils.isNight(level) && (MoonPhase.FULL_MOON.match(level) || BloodMoonGameEvent.INSTANCE.started());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos blockPos, RandomSource random) {
        if (getAge(state) != MAX_AGE) return;
        int r = random.nextInt(200);
        int brightness;
        if (r < 34) {
            boolean dark = r < 17;
            Vector4f curve = new Vector4f(0, 0.7f, 0.9f, 1);
            Vec3 pos = blockPos.getCenter().add(state.getOffset(level, blockPos)).offsetRandom(random, 0.3f);
            CrossDustParticleOptions particle;
            if (dark) {
                particle = new CrossDustParticleOptions(r < 12,
                        0x66DD99FF, 0x7f714E82, Vec3.ZERO.offsetRandom(level.random, level.random.nextFloat() * 0.04f + 0.01f).toVector3f(),
                        curve, level.random.nextFloat() * 0.6f + 0.6f, random.nextInt(20, 40), level.random.nextInt(-20, 20),
                        curve, true, true, false, level.random.nextBoolean());
            } else {
                curve = new Vector4f(0, 1, 1, 1);
                particle = new CrossDustParticleOptions(false,
                        0xffAF9FFF, 0xff9821FF, Vec3.ZERO.offsetRandom(level.random, level.random.nextFloat() * 0.01f + 0.01f).toVector3f(),
                        curve, level.random.nextFloat() * 0.6f + 0.3f, random.nextInt(20, 40), level.random.nextInt(-20, 20),
                        curve, true, true, false, false);
            }
            level.addParticle(particle, pos.x, pos.y, pos.z, 0, 0, 0);
        }
        if (r < 10) {
            level.setBlockAndUpdate(blockPos, state.setValue(PROP_LIGHT, 7));
        } else if (r < 160 && (brightness = state.getValue(PROP_LIGHT)) > 0) {
            level.setBlockAndUpdate(blockPos, state.setValue(PROP_LIGHT, brightness - 1));
        }
    }
}
