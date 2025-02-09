package org.confluence.mod.common.block.natural.herbs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
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
import org.confluence.mod.common.init.item.FoodItems;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Moonglow extends BaseHerbBlock {
	public static final IntegerProperty PROP_LIGHT = IntegerProperty.create("level", BRIGHTNESS, 5);


	public Moonglow(){
		super(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).randomTicks().lightLevel(value -> value.getValue(AGE) == MAX_AGE ? value.getValue(PROP_LIGHT) : 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder){
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(PROP_LIGHT);
	}


	@Override
	protected @NotNull ItemLike getBaseSeedId(){
		return FoodItems.MOONGLOW_SEED.get();
	}

	@Override
	public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom){
		if(getAge(pState) != MAX_AGE) return;
		int r = pRandom.nextInt(200);
		int brightness;
		if(r < 10){
			pLevel.setBlockAndUpdate(pPos, pState.setValue(PROP_LIGHT, 5));
			Vec3 center = pPos.getCenter().offsetRandom(pRandom,0.6f);
			pLevel.addParticle(new DustParticleOptions(new Vector3f(0, 0.7f, 1), 1), center.x, center.y / 2, center.z, 10,10,10);
			center = pPos.getCenter().offsetRandom(pRandom,0.6f);
			pLevel.addParticle(new DustParticleOptions(new Vector3f(0, 0.7f, 1), 1), center.x, center.y / 2, center.z, 10,10,10);
		}else if(r < 160 && (brightness = pState.getValue(PROP_LIGHT)) > BRIGHTNESS){
			pLevel.setBlockAndUpdate(pPos, pState.setValue(PROP_LIGHT, brightness - 1));
		}
	}


	@Override
	public boolean canBloom(ServerLevel world, BlockState state){
		return world.isNight();
	}
}
