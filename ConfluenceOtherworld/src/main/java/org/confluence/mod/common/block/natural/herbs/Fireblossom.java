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
    public boolean canBloom(ServerLevel world, BlockState state){
        if (world.dimensionType().natural()) {
            return LibDateUtils.isWithinDayTime(LibDateUtils.getDayTime(15, 45), LibDateUtils._19$30, world) && !world.isRaining();
        }
        return true;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom){
        // TODO: 粒子
//        if(getAge(pState) != MAX_AGE) return;
//        int r = pRandom.nextInt(60);
//        if(r > 10) return;
//        Vec3 pos = pPos.getCenter().add(pState.getOffset(pLevel, pPos)).offsetRandom(pRandom, 0.3f);
//        pLevel.addParticle(ModParticles.FLAMEFLOWER_BLOOM.get(), pos.x, pos.y, pos.z, 0, 0.3f, 0);
    }
}
