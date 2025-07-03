package org.confluence.mod.integration.jade;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.confluence.lib.common.block.ISimulatorBlock;
import org.confluence.mod.common.block.common.TombstoneBlock;
import org.confluence.mod.common.block.functional.AbstractMechanicalBlock;
import org.confluence.mod.common.block.functional.BehaviourPressurePlateBlock;
import org.confluence.mod.common.block.functional.DeathChestBlock;
import org.confluence.mod.common.block.functional.SignalPressurePlateBlock;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.entity.TreasureBagItemEntity;
import org.confluence.mod.common.init.block.ChestBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.integration.create.ponder.PonderHelper;
import snownee.jade.api.*;

@WailaPlugin
public class ModJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(NetworkComponentProvider.INSTANCE, AbstractMechanicalBlock.Entity.class);
        registration.registerBlockDataProvider(NetworkComponentProvider.INSTANCE, DeathChestBlock.Entity.class);
        registration.registerBlockDataProvider(TombstoneInfoProvider.INSTANCE, TombstoneBlock.Entity.class);
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
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
            if (accessor instanceof BlockAccessor blockAccessor) {
                Player player = accessor.getPlayer();
                if (player.isCreative()) return accessor;
                Block block = blockAccessor.getBlock();
                if (block == FunctionalBlocks.OAK_LOG_BOULDER.get()) {
                    return registration.blockAccessor().from(blockAccessor).blockState(Blocks.OAK_LOG.defaultBlockState()).build();
                } else if (block == ChestBlocks.DEATH_WOODEN_CHEST.get()) {
                    return registration.blockAccessor().from(blockAccessor).blockState(Blocks.CHEST.defaultBlockState()).build();
                } else if (block instanceof ISimulatorBlock simulatorBlock) {
                    return registration.blockAccessor().from(blockAccessor).blockState(simulatorBlock.getSimulatedBlock(true)).build();
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
