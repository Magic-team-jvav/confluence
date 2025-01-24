package org.confluence.mod.common.event.game;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.entity.projectile.bomb.ScarabBombEntity;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.confluence.terra_curio.util.TCUtils;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class LevelEvents {
    @SubscribeEvent
    public static void explosionDetonate(ExplosionEvent.Detonate event) {
        if (event.getExplosion().getDirectSourceEntity() instanceof ScarabBombEntity) {
            event.getAffectedEntities().removeIf(entity -> entity instanceof ItemEntity);
        }
    }

    @SubscribeEvent
    public static void blockToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getItemAbility() == ItemAbilities.AXE_STRIP) {
            BlockState originalState = event.getState();
            Block block = LogBlockSet.WRAPPED_STRIP_TABLE.get(originalState.getBlock());
            if (block != null) {
                event.setFinalState(block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, originalState.getValue(RotatedPillarBlock.AXIS)));
            }
        }
    }

    @SubscribeEvent
    public static void blockDropsEvent(BlockDropsEvent event) {
        if (event.getBreaker() instanceof LivingEntity living && !living.getMainHandItem().is(Items.SHEARS)) {
            ServerLevel level = event.getLevel();
            BlockState blockState = event.getState();
            BlockPos pos = event.getPos();
            if (blockState.is(ModTags.Blocks.VINES) && TCUtils.hasAccessoriesType(living, AccessoryItems.VINE$ROPE)) {
                event.setCanceled(true);
                Block.popResource(level, pos, ModBlocks.VINE_ROPE.get().asItem().getDefaultInstance());
            }
        }
    }

    @SubscribeEvent
    public static void chunkWatch$Watch(ChunkWatchEvent.Watch event) {
        BrushData data = event.getLevel().getData(ModAttachmentTypes.CHUNK_BRUSH_DATA).getDataMap().get(event.getPos());
        if (data != null && !data.colors().isEmpty()) {
            data.ensureValid(event.getLevel());
            BrushingColorPacketS2C.sendToClient(event.getPlayer(), event.getPos(), data, false);
        }
    }
}
