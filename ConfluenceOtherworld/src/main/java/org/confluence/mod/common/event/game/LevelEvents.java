package org.confluence.mod.common.event.game;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.attachment.ChunkBrushData;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.data.map.BlockBreakSpawns;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.data.saved.SpaceSpawner;
import org.confluence.mod.common.entity.projectile.bomb.BaseBombEntity;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.gameevent.GoblinArmyGameEvent;
import org.confluence.mod.common.gameevent.MeteorShowerGameEvent;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.item.axe.LucyTheAxe;
import org.confluence.mod.common.item.common.StaffOfRegrowth;
import org.confluence.mod.common.worldgen.secret_seed.BoulderWorld;
import org.confluence.mod.common.worldgen.secret_seed.NoTraps;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.level.*;

public final class LevelEvents {

    public static void init() {
        PortEventHandler.addListener(LevelEvents::blockDrops);
        PortEventHandler.addListener(LevelEvents::farmlandTrample);
        PortEventHandler.addListener(LevelEvents::modifyCustomSpawners);
    }

    public static void explosion$Detonate(PortExplosionEvent.PortDetonate event) {
        BaseBombEntity.itemInvulnerableToExplosion(event.getExplosion().getDirectSourceEntity(), event.getAffectedEntities());
        NoTraps.entityInvulnerableToExplosion(event.getLevel(), event.getAffectedEntities());
    }

    public static void block$ToolModification(PortBlockEvent.PortBlockToolModificationEvent event) {
        if (event.getItemAbility() == ItemAbilities.AXE_STRIP) {
            BlockState originalState = event.getState();
            Block block = LogBlockSet.WRAPPED_STRIP_TABLE.get(originalState.getBlock());
            if (block != null) {
                event.setFinalState(block.defaultBlockState().trySetValue(RotatedPillarBlock.AXIS, originalState.getValue(RotatedPillarBlock.AXIS)));
            }
        }
    }

    public static void blockDrops(PortBlockDropsEvent event) {
        ItemStack tool = event.getTool();
        Entity breaker = event.getBreaker();
        BlockState state = event.getState();
        if (breaker instanceof LivingEntity living && !tool.is(Items.SHEARS)) {
            if (state.is(ModTags.Blocks.VINES) && TCUtils.hasType(living, AccessoryItems.VINE$ROPE)) {
                event.setCanceled(true);
                Block.popResource(event.getLevel(), event.getPos(), ModBlocks.VINE_ROPE.get().asItem().getDefaultInstance());
            }
        }
        // 再生法杖/再生之斧 时运
        if (tool.is(ModTags.Items.CROP_FORTUNE) && breaker != null && (state.is(BlockTags.CROPS) || state.getBlock() instanceof CropBlock)) {
            StaffOfRegrowth.increaseDrops(breaker, tool, event.getDrops().stream().map(ItemEntity::getItem));
        }
    }

    public static void chunkWatch$Watch(PortChunkWatchEvent.PortWatch event) {
        BrushData data = ChunkBrushData.of(event.getLevel()).getDataMap().get(event.getPos());
        if (data != null && !data.colors().isEmpty()) {
            data.ensureValid(event.getLevel());
            BrushingColorPacketS2C.sendToClient(event.getPlayer(), event.getPos(), data, false);
        }
    }

    public static void block$Break(PortBlockEvent.PortBreakEvent event) {
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
            LucyTheAxe.onDestroyBlock(serverPlayer, state);
        }
    }

    public static void farmlandTrample(PortBlockEvent.PortFarmlandTrampleEvent event) {
        if (event.getEntity() instanceof Player player && !PlayerSpecialData.of(player).isCouldDamageEnvironment()) {
            event.setCanceled(true);
        }
    }

    public static void modifyCustomSpawners(PortModifyCustomSpawnersEvent event) {
        if (event.getLevel().dimension() == OverworldUtils.dimension()) {
            event.addCustomSpawner(BloodMoonGameEvent.INSTANCE.spawner);
            event.addCustomSpawner(GoblinArmyGameEvent.INSTANCE.spawner);
            event.addCustomSpawner(MeteorShowerGameEvent.INSTANCE.spawner);
            event.addCustomSpawner(new SpaceSpawner());
        }
    }
}
