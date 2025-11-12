package org.confluence.mod.common.block.natural.herbs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.common.init.item.FoodItems;
import org.jetbrains.annotations.NotNull;

public class Fireblossom extends BaseHerbBlock {
    public Fireblossom(){
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).randomTicks().lightLevel(value -> value.getValue(AGE) == MAX_AGE ? 4 : 0));
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId(){
        return FoodItems.FIREBLOSSOM_SEED.get();
    }

    @Override
    public boolean canBloom(ServerLevel level, BlockState state){
        if (level.dimensionType().natural()) {
            return LibDateUtils.isWithinDayTime(LibDateUtils.getDayTime(15, 45), LibDateUtils._19$30, level) && !level.isRaining();
        }
        return true;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random){
        // TODO: 粒子
//        if(getAge(state) != MAX_AGE) return;
//        int r = random.nextInt(60);
//        if(r > 10) return;
//        Vec3 pos = pos.getCenter().add(state.getOffset(level, pos)).offsetRandom(random, 0.3f);
//        level.addParticle(ModParticles.FLAMEFLOWER_BLOOM.get(), pos.x, pos.y, pos.z, 0, 0.3f, 0);
    }
}
