package org.confluence.mod.integration.jade;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.block.ISimulatorBlock;
import org.confluence.mod.common.block.common.TombstoneBlock;
import org.confluence.mod.common.block.functional.AbstractMechanicalBlock;
import org.confluence.mod.common.block.functional.BehaviourPressurePlateBlock;
import org.confluence.mod.common.block.functional.DeathChestBlock;
import org.confluence.mod.common.block.functional.SignalPressurePlateBlock;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.confluence.mod.common.entity.TreasureBagItemEntity;
import org.confluence.mod.common.init.block.ChestBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.integration.create.ponder.PonderHelper;
import snownee.jade.api.*;

import java.util.IdentityHashMap;
import java.util.Map;

@WailaPlugin
public final class ModJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(NetworkComponentProvider.INSTANCE, AbstractMechanicalBlock.BEntity.class);
        registration.registerBlockDataProvider(NetworkComponentProvider.INSTANCE, DeathChestBlock.BEntity.class);
        registration.registerBlockDataProvider(TombstoneInfoProvider.INSTANCE, TombstoneBlock.BEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(NetworkComponentProvider.INSTANCE, AbstractMechanicalBlock.class);
        registration.registerBlockComponent(NetworkComponentProvider.INSTANCE, DeathChestBlock.class);
        registration.registerBlockComponent(NetworkComponentProvider.INSTANCE, SignalPressurePlateBlock.class);
        registration.registerBlockComponent(NetworkComponentProvider.INSTANCE, BehaviourPressurePlateBlock.class);
        if (PonderHelper.IS_LOADED) {
            registration.registerBlockComponent(PonderComponentProvider.INSTANCE, AltarBlock.class);
        }
        registration.registerBlockComponent(TombstoneInfoProvider.INSTANCE, TombstoneBlock.class);

        Map<Block, BlockState> hideMap = new IdentityHashMap<>();
        hideMap.put(ChestBlocks.DEATH_GOLDEN_CHEST.get(), ChestBlocks.GOLDEN_CHEST.get().defaultBlockState());
        hideMap.put(ChestBlocks.DEATH_WOODEN_CHEST.get(), Blocks.CHEST.defaultBlockState());
        hideMap.put(FunctionalBlocks.OAK_LOG_BOULDER.get(), Blocks.OAK_LOG.defaultBlockState());
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
            if (accessor instanceof BlockAccessor blockAccessor) {
                BlockState source = blockAccessor.getBlockState();
                BlockState target = GlobalCloakData.INSTANCE.getTarget(source);
                if (source != target) {
                    return registration.blockAccessor().from(blockAccessor).blockState(target).build();
                }
            }
            return accessor;
        });
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
            if (accessor instanceof BlockAccessor blockAccessor) {
                Player player = accessor.getPlayer();
                if (player.isCreative()) return accessor;
                Block block = blockAccessor.getBlock();
                if (block instanceof ISimulatorBlock simulatorBlock) {
                    return registration.blockAccessor().from(blockAccessor).blockState(simulatorBlock.getSimulatedBlock(true)).build();
                }
                BlockState blockState = hideMap.get(block);
                if (blockState != null) {
                    return registration.blockAccessor().from(blockAccessor).blockState(blockState).build();
                }
            } else if (accessor instanceof EntityAccessor entityAccessor) {
                if (entityAccessor.getEntity() instanceof TreasureBagItemEntity entity && !entity.isOwner(accessor.getPlayer())) {
                    return null;
                }
            }
            return accessor;
        });
    }
}
