package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.NbtComponent;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.natural.VoidTreeRootBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VoidCrystalItem extends Item {
    public VoidCrystalItem() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (handleClear(stack, level, player)) {
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (player != null && player.isShiftKeyDown()) {
            if (handleClear(stack, level, player))
                return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!(state.getBlock() instanceof VoidTreeRootBlock)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;

        CompoundTag tag = LibUtils.getItemStackNbt(stack);
        Direction clickedFace = context.getClickedFace();

        if (!tag.contains("FirstPos")) {
            markPosition(stack, tag, pos, clickedFace, player, level);
        } else if (level instanceof ServerLevel serverLevel) {
            processLinking(stack, tag, pos, clickedFace, player, serverLevel);
        }

        return InteractionResult.SUCCESS;
    }

    private void markPosition(ItemStack stack, CompoundTag tag, BlockPos pos, Direction face, @Nullable Player player, Level level) {
        tag.putLong("FirstPos", pos.asLong());
        tag.putInt("FirstFace", face.get3DDataValue());
        stack.setData(ConfluenceMagicLib.NBT, new NbtComponent(tag));

        if (player != null) {
            player.displayClientMessage(Component.translatable("chat.confluence.crystal_marked").withStyle(ChatFormatting.AQUA), true);
            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 1.2F);
        }
    }

    private void processLinking(ItemStack stack, CompoundTag tag, BlockPos pos, Direction secondFace, @Nullable Player player, ServerLevel level) {
        BlockPos firstPos = BlockPos.of(tag.getLong("FirstPos"));
        Direction firstFace = Direction.from3DDataValue(tag.getInt("FirstFace"));

        if (player == null) return;
        if (pos.equals(firstPos)) {
            notifyError(player, level, pos, "chat.confluence.link_same_block");
            return;
        }
        if (!pos.closerThan(firstPos, 100)) {
            notifyError(player, level, pos, "chat.confluence.link_too_far");
            return;
        }

        if (secondFace != firstFace.getOpposite()) {
            notifyError(player, level, pos, "chat.confluence.link_not_opposite");
            return;
        }

        if (linkRoots(level, firstPos, pos, firstFace, secondFace)) {
            player.displayClientMessage(Component.translatable("chat.confluence.link_success").withStyle(ChatFormatting.GREEN), true);
            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.5F);

            if (!player.getAbilities().instabuild) stack.shrink(1);
            else updateNbt(stack, tag, true);
        } else {
            notifyError(player, level, pos, "chat.confluence.link_failed_generic");
        }
    }

    private boolean handleClear(ItemStack stack, Level level, Player player) {
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
        if (tag != null && tag.contains("FirstPos")) {
            if (!level.isClientSide) {
                updateNbt(stack, tag, true);
                player.displayClientMessage(Component.translatable("chat.confluence.crystal_cleared").withStyle(ChatFormatting.YELLOW), true);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BUNDLE_REMOVE_ONE, SoundSource.PLAYERS, 1.0F, 1.2F);
            }
            return true;
        }
        return false;
    }

    private void updateNbt(ItemStack stack, CompoundTag originalTag, boolean shouldClear) {
        CompoundTag workingTag = originalTag.copy();
        if (shouldClear) {
            workingTag.remove("FirstPos");
            workingTag.remove("FirstFace");
        }
        if (workingTag.isEmpty()) {
            stack.removeData(ConfluenceMagicLib.NBT);
        } else {
            stack.setData(ConfluenceMagicLib.NBT, new NbtComponent(workingTag));
        }
    }

    private void notifyError(@Nullable Player player, Level level, BlockPos pos, String translationKey) {
        if (player == null) return;
        player.displayClientMessage(Component.translatable(translationKey).withStyle(ChatFormatting.RED), true);
        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.PLAYERS, 1.0F, 0.5F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
        if (tag != null && tag.contains("FirstPos")) {
            BlockPos pos = BlockPos.of(tag.getLong("FirstPos"));
            Direction face = Direction.from3DDataValue(tag.getInt("FirstFace"));
            tooltip.add(Component.translatable("tooltip.confluence.void_crystal.pos", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.confluence.void_crystal.face", face.getSerializedName()).withStyle(ChatFormatting.DARK_PURPLE));
        } else {
            tooltip.add(Component.translatable("tooltip.confluence.void_crystal.empty").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static boolean linkRoots(ServerLevel level, BlockPos pos1, BlockPos pos2, Direction face1, Direction face2) {
        BlockState s1 = level.getBlockState(pos1);
        BlockState s2 = level.getBlockState(pos2);

        if (!(s1.getBlock() instanceof VoidTreeRootBlock root) || !(s2.getBlock() instanceof VoidTreeRootBlock))
            return false;

        level.setBlock(pos1, s1.setValue(VoidTreeRootBlock.CONNECTION_PROPERTIES.get(face1), VoidTreeRootBlock.ConnectType.CONNECT_BY_PORTAL), 3);
        level.setBlock(pos2, s2.setValue(VoidTreeRootBlock.CONNECTION_PROPERTIES.get(face2), VoidTreeRootBlock.ConnectType.CONNECT_BY_PORTAL), 3);

        if (level.getBlockEntity(pos1) instanceof VoidTreeRootBlock.BEntity be1 && level.getBlockEntity(pos2) instanceof VoidTreeRootBlock.BEntity be2) {
            be1.addLink(face1, pos2);
            be2.addLink(face2, pos1);

            level.scheduleTick(pos1, root, 1);
            level.scheduleTick(pos2, root, 1);
            return true;
        }
        return false;
    }
}
