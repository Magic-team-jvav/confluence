package org.confluence.mod.integration.jade;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.common.block.functional.*;
import org.confluence.mod.common.entity.TreasureBagItemEntity;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import snownee.jade.api.*;

@WailaPlugin
public class ModJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(NetworkComponentProvider.INSTANCE, AbstractMechanicalBlock.Entity.class);
        registration.registerBlockDataProvider(NetworkComponentProvider.INSTANCE, DeathChestBlock.Entity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(NetworkComponentProvider.INSTANCE, AbstractMechanicalBlock.class);
        registration.registerBlockComponent(NetworkComponentProvider.INSTANCE, DeathChestBlock.class);
        registration.registerBlockComponent(NetworkComponentProvider.INSTANCE, SignalPressurePlateBlock.class);
        registration.registerBlockComponent(NetworkComponentProvider.INSTANCE, BehaviourPressurePlateBlock.class);
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
            if (accessor instanceof BlockAccessor blockAccessor) {
                Player player = accessor.getPlayer();
                if (player.isCreative()) return accessor;
                if (blockAccessor.getBlock() == FunctionalBlocks.OAK_LOG_BOULDER.get()) {
                    return registration.blockAccessor().from(blockAccessor).blockState(Blocks.OAK_LOG.defaultBlockState()).build();
                }
                if (blockAccessor.getBlock() instanceof ISimulatorBlock simulatorBlock) {
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
