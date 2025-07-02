package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.network.c2s.ApplySelectionPacketC2S;
import org.confluence.mod.network.s2c.OpenSelectionsScreenPacketS2C;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.confluence.lib.common.item.TooltipItem.getTooltipsFromString;

public class MagicConch extends CustomRarityItem implements ApplySelectionPacketC2S.ISelectable<BlockPos> {
    public List<Component> tooltips = new ArrayList<>();
    public MagicConch(Properties properties, ModRarity rarity) {
        super(properties, rarity);
        tooltips = getTooltipsFromString("magic_conch", 1, ChatFormatting.GRAY);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(tooltips);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() instanceof ServerPlayer serverPlayer && context.getHand() == InteractionHand.MAIN_HAND && checkAvailable(context)) {
            BlockPos clickedPos = context.getClickedPos();
            LibUtils.updateItemStackNbt(context.getItemInHand(), tag -> {
                if (!tag.contains("pos1")) {
                    tag.put("pos1", NbtUtils.writeBlockPos(clickedPos));
                } else if (!tag.contains("pos2")) {
                    tag.put("pos2", NbtUtils.writeBlockPos(clickedPos));
                } else {
                    BlockPos pos1 = NbtUtils.readBlockPos(tag, "pos1").orElse(BlockPos.ZERO);
                    BlockPos pos2 = NbtUtils.readBlockPos(tag, "pos2").orElse(BlockPos.ZERO);
                    if (pos1.equals(clickedPos)) {
                        tag.remove("pos1");
                    } else if (pos2.equals(clickedPos)) {
                        tag.remove("pos2");
                    } else {
                        double distanceToPos1 = clickedPos.distSqr(pos1);
                        double distanceToPos2 = clickedPos.distSqr(pos2);
                        tag.put(distanceToPos1 > distanceToPos2 ? "pos2" : "pos1", NbtUtils.writeBlockPos(clickedPos));
                    }
                }
                serverPlayer.sendSystemMessage(successStoreMessage(clickedPos), false);
            });
        }
        return InteractionResult.SUCCESS;
    }

    protected Component successStoreMessage(BlockPos pos) {
        return Component.translatable("chat.confluence.magic_conch", pos.toShortString());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            CompoundTag tag = LibUtils.getItemStackNbt(itemStack);
            if (tag.get("pos1") != null || tag.get("pos2") != null) {
                Optional<BlockPos> pos1 = NbtUtils.readBlockPos(tag, "pos1");
                Optional<BlockPos> pos2 = NbtUtils.readBlockPos(tag, "pos2");
                OpenSelectionsScreenPacketS2C.sendToClient(serverPlayer, new Component[]{
                        getMessage(pos1),
                        getMessage(pos2)
                }, new boolean[]{
                        pos1.isPresent(),
                        pos2.isPresent()
                });
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide);
    }

    protected Component getMessage(Optional<BlockPos> pos) {
        return Component.translatable("selections.confluence.magic_conch", pos.map(Vec3i::toShortString).orElse("unknown"));
    }

    protected boolean checkAvailable(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Holder<Biome> biome = level.getBiome(pContext.getClickedPos());
        return pContext.getClickedFace() == Direction.UP && level.dimension() == ModUtils.dimension() &&
                (biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_BEACH) || biome.is(Tags.Biomes.IS_STONY_SHORES));
    }

    @Override
    public @Nullable BlockPos getSelected(byte index, ItemStack itemStack) {
        CompoundTag tag = LibUtils.getItemStackNbt(itemStack);
        if (index == 0) {
            return NbtUtils.readBlockPos(tag, "pos1").orElse(null);
        }
        if (index == 1) {
            return NbtUtils.readBlockPos(tag, "pos2").orElse(null);
        }
        return null;
    }

    @Override
    public void applySelected(byte index, ItemStack itemStack, ServerPlayer serverPlayer) {
        BlockPos pos = getSelected(index, itemStack);
        if (pos != null) {
            serverPlayer.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        }
    }
}
