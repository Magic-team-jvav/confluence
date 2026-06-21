package org.confluence.mod.common.block.common;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.terra_curio.common.effect.HoneyEffect;

import java.util.Map;

public class HoneyCauldronBlock extends AbstractCauldronBlock {
    public static final Map<Item, CauldronInteraction> INTERACTION_MAP = Util.make(CauldronInteraction.newInteractionMap(), map -> {
        map.put(Items.BUCKET, (blockState, level, blockPos, player, hand, itemStack) -> CauldronInteraction.fillBucket(
                blockState, level, blockPos, player, hand, itemStack,
                ToolItems.HONEY_BUCKET.get().getDefaultInstance(),
                state -> true,
                SoundEvents.BUCKET_FILL
        ));
        CauldronInteraction.addDefaultInteractions(map);
    });
    public static final CauldronInteraction FILL_HONEY = (blockState, level, blockPos, player, hand, itemStack) -> CauldronInteraction.emptyBucket(
            level, blockPos, player, hand, itemStack,
            ModBlocks.HONEY_CAULDRON.get().defaultBlockState(),
            SoundEvents.BUCKET_EMPTY_LAVA
    );

    public HoneyCauldronBlock(Properties properties) {
        super(properties, INTERACTION_MAP);
    }

    @Override
    protected double getContentHeight(BlockState blockState) {
        return 0.9375;
    }

    @Override
    public boolean isFull(BlockState blockState) {
        return true;
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!level.isClientSide && isEntityInsideContent(blockState, blockPos, entity) && entity instanceof LivingEntity living) {
            HoneyEffect.applyHoneyEffect(living);
        }
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return 3;
    }
}
