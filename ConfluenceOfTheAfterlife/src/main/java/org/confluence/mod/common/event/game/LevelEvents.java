package org.confluence.mod.common.event.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.entity.item.ItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
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
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.confluence.terra_curio.util.TCUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            ItemStack shears = Items.SHEARS.getDefaultInstance();
            if (blockState.is(ModTags.Blocks.VINES) && TCUtils.hasAccessoriesType(living, AccessoryItems.SHEARS$DIG)) {
                event.setCanceled(true);
                List<ItemStack> drops = Block.getDrops(blockState, level, pos, event.getBlockEntity(), living, shears);
                drops.forEach(itemStack -> Block.popResource(level, pos, itemStack));
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

    @SubscribeEvent
    public static void conversionSandEvent(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        ItemStack stack = event.getItemStack();
        BlockState state = level.getBlockState(pos);
        Player player = event.getEntity();
        PotionContents potioncontents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (!level.isClientSide && potioncontents.is(Potions.WATER)) {
            level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            player.setItemInHand(event.getHand(), ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(stack.getItem()));
            Map<Block, Block> sandMapping = new HashMap<>();
            sandMapping.put(Blocks.SAND, NatureBlocks.MOIST_SAND_BLOCK.get());
            sandMapping.put(Blocks.RED_SAND, NatureBlocks.RED_MOIST_SAND_BLOCK.get());
            sandMapping.put(NatureBlocks.EBONY_SAND.get(), NatureBlocks.EBONY_MOIST_SAND_BLOCK.get());
            sandMapping.put(NatureBlocks.PEARL_SAND.get(), NatureBlocks.PEARL_MOIST_SAND_BLOCK.get());
            sandMapping.put(NatureBlocks.TR_CRIMSON_SAND.get(), NatureBlocks.TR_CRIMSON_MOIST_SAND_BLOCK.get());
            Block newBlock = sandMapping.get(state.getBlock());
            if (newBlock != null) {
                level.setBlockAndUpdate(pos, newBlock.defaultBlockState());
            }
        }
    }
}
