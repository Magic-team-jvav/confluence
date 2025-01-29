package org.confluence.mod.integration.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.common.block.functional.AbstractMechanicalBlock;
import org.confluence.mod.common.block.functional.BehaviourPressurePlateBlock;
import org.confluence.mod.common.block.functional.DeathChestBlock;
import org.confluence.mod.common.block.functional.SignalPressurePlateBlock;
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
                if (blockAccessor.getBlockEntity() instanceof DeathChestBlock.Entity entity) { // 隐藏死人箱
                    CompoundTag serverData = blockAccessor.getServerData();
                    serverData.putString("givenName", "{\"translate\":\"block.confluence.base_chest_block." + entity.variant.getSerializedName() + "\"}");
                    return registration.blockAccessor().from(blockAccessor).serverData(serverData).build();
                }
                if (blockAccessor.getBlock() == FunctionalBlocks.OAK_LOG_BOULDER.get()) {
                    return registration.blockAccessor().from(blockAccessor).blockState(Blocks.OAK_LOG.defaultBlockState()).build();
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
