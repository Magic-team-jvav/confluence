package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.natural.VoidTreeRootBlock;

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

        if (player != null && player.isShiftKeyDown()) {
            if (handleClear(stack, level, player)) {
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof VoidTreeRootBlock)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;

        Direction clickedFace = context.getClickedFace();
        CompoundTag tag = LibUtils.getItemStackNbtNoCopy(stack);

        if (!tag.contains("FirstPos")) {
            recordPosition(tag, pos, clickedFace);
            if (player != null) {
                player.displayClientMessage(Component.translatable("chat.confluence.crystal_marked").withStyle(ChatFormatting.AQUA), true);
                player.containerMenu.broadcastChanges();
            }
        } else {
            BlockPos firstPos = BlockPos.of(tag.getLong("FirstPos"));
            Direction firstFace = Direction.from3DDataValue(tag.getInt("FirstFace"));

            if (!pos.closerThan(firstPos, 100)) {
                if (player != null)
                    player.displayClientMessage(Component.translatable("chat.confluence.link_too_far").withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }

            if (clickedFace != firstFace.getOpposite()) {
                if (player != null)
                    player.displayClientMessage(Component.translatable("chat.confluence.link_not_opposite").withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }

            if (linkRoots((ServerLevel) level, firstPos, pos, firstFace, clickedFace)) {
                if (player != null) {
                    player.displayClientMessage(Component.translatable("chat.confluence.link_success").withStyle(ChatFormatting.GREEN), true);
                    clearPosition(tag);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    player.containerMenu.broadcastChanges();
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    private boolean handleClear(ItemStack stack, Level level, Player player) {
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
        if (tag != null && tag.contains("FirstPos")) {
            if (!level.isClientSide) {
                clearPosition(tag);
                player.containerMenu.broadcastChanges();
                player.displayClientMessage(Component.translatable("chat.confluence.crystal_cleared").withStyle(ChatFormatting.YELLOW), true);
            }
            return true;
        }
        return false;
    }

    private void recordPosition(CompoundTag tag, BlockPos pos, Direction face) {
        tag.putLong("FirstPos", pos.asLong());
        tag.putInt("FirstFace", face.get3DDataValue());
    }

    private void clearPosition(CompoundTag tag) {
        tag.remove("FirstPos");
        tag.remove("FirstFace");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
        if (tag != null && tag.contains("FirstPos")) {
            BlockPos pos = BlockPos.of(tag.getLong("FirstPos"));
            Direction face = Direction.from3DDataValue(tag.getInt("FirstFace"));
            tooltip.add(Component.translatable("tooltip.confluence.void_crystal.pos", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.confluence.void_crystal.face", face.getName()).withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable("tooltip.confluence.void_crystal.clear_hint").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
        } else {
            tooltip.add(Component.translatable("tooltip.confluence.void_crystal.empty").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static boolean linkRoots(ServerLevel level, BlockPos pos1, BlockPos pos2, Direction face1, Direction face2) {
        BlockState state1 = level.getBlockState(pos1);
        BlockState state2 = level.getBlockState(pos2);

        if (state1.getBlock() instanceof VoidTreeRootBlock && state2.getBlock() instanceof VoidTreeRootBlock) {
            if (level.getBlockEntity(pos1) instanceof VoidTreeRootBlock.BEntity be1) {
                be1.setLinkedPos(pos2);
                level.setBlock(pos1, state1.setValue(VoidTreeRootBlock.CONNECTION_PROPERTIES.get(face1), VoidTreeRootBlock.ConnectType.CONNECT_BY_PORTAL), 3);
            }
            if (level.getBlockEntity(pos2) instanceof VoidTreeRootBlock.BEntity be2) {
                be2.setLinkedPos(pos1);
                level.setBlock(pos2, state2.setValue(VoidTreeRootBlock.CONNECTION_PROPERTIES.get(face2), VoidTreeRootBlock.ConnectType.CONNECT_BY_PORTAL), 3);
            }
            return true;
        }
        return false;
    }
}
