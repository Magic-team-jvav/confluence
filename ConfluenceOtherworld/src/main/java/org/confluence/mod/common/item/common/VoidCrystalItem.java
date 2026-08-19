package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
    private static final String RUNTIME_TAG = "ConfluenceVoidCrystalRuntime";
    private static final int RUNTIME_VERSION = 1;

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

        Direction clickedFace = context.getClickedFace();
        Mark mark = readMark(stack);
        if (mark == null) {
            markPosition(stack, pos, clickedFace, player, level);
        } else if (level instanceof ServerLevel serverLevel) {
            processLinking(stack, mark, pos, clickedFace, player, serverLevel);
        }

        return InteractionResult.SUCCESS;
    }

    private void markPosition(ItemStack stack, BlockPos pos, Direction face, @Nullable Player player, Level level) {
        writeMark(stack, new Mark(pos, face));

        if (player != null) {
            player.displayClientMessage(Component.translatable("chat.confluence.crystal_marked").withStyle(ChatFormatting.AQUA), true);
            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 1.2F);
        }
    }

    private void processLinking(ItemStack stack, Mark mark, BlockPos pos, Direction secondFace, @Nullable Player player, ServerLevel level) {
        BlockPos firstPos = mark.position();
        Direction firstFace = mark.face();

        if (player == null) return;
        if (!level.isInWorldBounds(firstPos) || !level.getWorldBorder().isWithinBounds(firstPos)) {
            clearMark(stack);
            notifyError(player, level, pos, "chat.confluence.link_failed_generic");
            return;
        }
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

            // 标记属于本次连接事务；堆叠中剩余的水晶不能继承已消费水晶的旧端点。
            clearMark(stack);
            if (!player.getAbilities().instabuild) stack.shrink(1);
        } else {
            notifyError(player, level, pos, "chat.confluence.link_failed_generic");
        }
    }

    private boolean handleClear(ItemStack stack, Level level, Player player) {
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
        if (tag != null && tag.contains(RUNTIME_TAG)) {
            if (!level.isClientSide) {
                clearMark(stack);
                player.displayClientMessage(Component.translatable("chat.confluence.crystal_cleared").withStyle(ChatFormatting.YELLOW), true);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BUNDLE_REMOVE_ONE, SoundSource.PLAYERS, 1.0F, 1.2F);
            }
            return true;
        }
        return false;
    }

    private void notifyError(@Nullable Player player, Level level, BlockPos pos, String translationKey) {
        if (player == null) return;
        player.displayClientMessage(Component.translatable(translationKey).withStyle(ChatFormatting.RED), true);
        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.PLAYERS, 1.0F, 0.5F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Mark mark = readMark(stack);
        if (mark != null) {
            BlockPos pos = mark.position();
            tooltip.add(Component.translatable("tooltip.confluence.void_crystal.pos", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.confluence.void_crystal.face", mark.face().getSerializedName()).withStyle(ChatFormatting.DARK_PURPLE));
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

    /// 只接受位置与面同时存在的当前版本快照，损坏或早期扁平字段不会默认成原点和 DOWN。
    static @Nullable Mark readMark(ItemStack stack) {
        CompoundTag itemTag = LibUtils.getItemStackNbtIfPresent(stack);
        if (itemTag == null || !itemTag.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) return null;
        CompoundTag runtime = itemTag.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT) || runtime.getInt("Version") != RUNTIME_VERSION || !runtime.contains("Position", Tag.TAG_LONG) || !runtime.contains("Face", Tag.TAG_INT)) {
            return null;
        }
        int faceId = runtime.getInt("Face");
        if (faceId < 0 || faceId >= Direction.values().length) return null;
        return new Mark(BlockPos.of(runtime.getLong("Position")), Direction.from3DDataValue(faceId));
    }

    /// 写入当前格式完整快照并删除旧扁平键；异常文本由调用边界保持英文。
    static void writeMark(ItemStack stack, Mark mark) {
        if (mark == null) {
            throw new IllegalArgumentException("Void crystal mark cannot be null");
        }
        LibUtils.updateItemStackNbt(stack, itemTag -> {
            itemTag.remove("FirstPos");
            itemTag.remove("FirstFace");
            CompoundTag runtime = new CompoundTag();
            runtime.putInt("Version", RUNTIME_VERSION);
            runtime.putLong("Position", mark.position().asLong());
            runtime.putInt("Face", mark.face().get3DDataValue());
            itemTag.put(RUNTIME_TAG, runtime);
        });
    }

    /// 仅移除本物品的运行状态，保留其他组件使用者的 NBT。
    static void clearMark(ItemStack stack) {
        CompoundTag itemTag = LibUtils.getItemStackNbtIfPresent(stack);
        if (itemTag == null) return;
        CompoundTag updated = itemTag.copy();
        updated.remove(RUNTIME_TAG);
        updated.remove("FirstPos");
        updated.remove("FirstFace");
        if (updated.isEmpty()) {
            stack.remove(ConfluenceMagicLib.NBT);
        } else {
            stack.set(ConfluenceMagicLib.NBT, new NbtComponent(updated));
        }
    }

    record Mark(BlockPos position, Direction face) {}
}
