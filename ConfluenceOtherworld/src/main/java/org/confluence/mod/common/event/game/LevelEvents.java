package org.confluence.mod.common.event.game;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
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
import org.confluence.mod.common.attachment.ChunkBrushData;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.data.map.BlockBreakSpawns;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.entity.projectile.bomb.BaseBombEntity;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.item.common.BaseAxeItem;
import org.confluence.mod.common.worldgen.secret_seed.BoulderWorld;
import org.confluence.mod.common.worldgen.secret_seed.NoTraps;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.confluence.terra_curio.util.TCUtils;

@EventBusSubscriber(modid = Confluence.MODID)
public final class LevelEvents {
    @SubscribeEvent
    public static void explosion$Detonate(ExplosionEvent.Detonate event) {
        BaseBombEntity.itemInvulnerableToExplosion(event.getExplosion().getDirectSourceEntity(), event.getAffectedEntities());
        NoTraps.entityInvulnerableToExplosion(event.getLevel(), event.getAffectedEntities());
    }

    @SubscribeEvent
    public static void block$ToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getItemAbility() == ItemAbilities.AXE_STRIP) {
            BlockState originalState = event.getState();
            Block block = LogBlockSet.WRAPPED_STRIP_TABLE.get(originalState.getBlock());
            if (block != null) {
                event.setFinalState(block.defaultBlockState().trySetValue(RotatedPillarBlock.AXIS, originalState.getValue(RotatedPillarBlock.AXIS)));
            }
        }
        if (event.getItemAbility() == ItemAbilities.SHOVEL_FLATTEN) {
            BlockState originalState = event.getState();
            if (originalState.is(NatureBlocks.JUNGLE_GRASS_BLOCK.get())) {
                event.setFinalState(NatureBlocks.JUNGLE_PATH.get().defaultBlockState());
            } else if (originalState.is(NatureBlocks.MUSHROOM_GRASS_BLOCK.get())) {
                event.setFinalState(NatureBlocks.MUSHROOM_PATH.get().defaultBlockState());
            } else if (originalState.is(NatureBlocks.ASH_GRASS_BLOCK.get())) {
                event.setFinalState(NatureBlocks.ASH_PATH.get().defaultBlockState());
            }
        }
    }

    @SubscribeEvent
    public static void blockDrops(BlockDropsEvent event) {
        ItemStack tool = event.getTool();
        Entity breaker = event.getBreaker();
        BlockState state = event.getState();
        if (breaker instanceof LivingEntity living && !tool.is(Items.SHEARS)) {
            if (state.is(ModTags.Blocks.VINES) && TCUtils.hasAccessoriesType(living, AccessoryItems.VINE$ROPE)) {
                event.setCanceled(true);
                Block.popResource(event.getLevel(), event.getPos(), ModBlocks.VINE_ROPE.get().asItem().getDefaultInstance());
            }
        }
        // 再生法杖/再生之斧 时运
        if (tool.is(ModTags.Items.CROP_FORTUNE) && breaker != null && (state.is(BlockTags.CROPS) || state.getBlock() instanceof CropBlock)) {
            BaseAxeItem.increaseDropsOnBlockBreak(breaker, tool, event.getDrops());
        }
    }

    @SubscribeEvent
    public static void chunkWatch$Watch(ChunkWatchEvent.Watch event) {
        BrushData data = ChunkBrushData.of(event.getLevel()).getDataMap().get(event.getPos());
        if (data != null && !data.colors().isEmpty()) {
            data.ensureValid(event.getLevel());
            BrushingColorPacketS2C.sendToClient(event.getPlayer(), event.getPos(), data, false);
        }
    }

    @SubscribeEvent
    public static void block$Break(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        BlockState state = event.getState();
        if (!PlayerSpecialData.of(player).isCouldDamageEnvironment() && state.is(ModTags.Blocks.ENVIRONMENTAL_PRESERVATION)) {
            event.setCanceled(true);
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (AltarBlock.hurtPlayerIfBrokenNotAllowed(serverPlayer, state)) {
                event.setCanceled(true);
                return;
            }

            BlockPos pos = event.getPos();
            NoTraps.dropBombWhenLeavesDestroy(serverPlayer, state, pos);
            BoulderWorld.createBoulderWhenBlockDestroy(serverPlayer, state, pos);
            BlockBreakSpawns.spawn(serverPlayer.serverLevel(), pos, state);
        }
    }

    @SubscribeEvent
    public static void farmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (event.getEntity() instanceof Player player && !PlayerSpecialData.of(player).isCouldDamageEnvironment()) {
            event.setCanceled(true);
        }
    }
}
