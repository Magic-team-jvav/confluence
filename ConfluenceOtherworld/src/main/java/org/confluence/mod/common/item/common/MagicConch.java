package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
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
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.network.c2s.ApplySelectionPacketC2S;
import org.confluence.mod.network.s2c.OpenSelectionsScreenPacketS2C;
import org.confluence.mod.util.OverworldUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.ArrayList;
import java.util.List;

import static org.confluence.lib.common.item.TooltipItem.getTooltipsFromString;

public class MagicConch extends CustomRarityItem implements ApplySelectionPacketC2S.ISelectable<BlockPos> {
    private static final String RUNTIME_TAG = "ConfluenceMagicConchRuntime";
    private static final int RUNTIME_VERSION = 1;
    public List<Component> tooltips = new ArrayList<>();

    public MagicConch(Properties properties, ModRarity rarity) {
        super(properties, rarity);
        tooltips = getTooltipsFromString("magic_conch", 1, ChatFormatting.GRAY);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(tooltips);
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() instanceof ServerPlayer serverPlayer && context.getHand() == InteractionHand.MAIN_HAND && checkAvailable(context)) {
            BlockPos clickedPos = context.getClickedPos();
            List<BlockPos> positions = readStoredPositions(context.getItemInHand());
            int existingIndex = positions.indexOf(clickedPos);
            if (existingIndex >= 0) {
                positions.remove(existingIndex);
            } else if (positions.size() < 2) {
                positions.add(clickedPos);
            } else {
                double distanceToFirst = clickedPos.distSqr(positions.get(0));
                double distanceToSecond = clickedPos.distSqr(positions.get(1));
                positions.set(distanceToFirst > distanceToSecond ? 1 : 0, clickedPos);
            }
            writeStoredPositions(context.getItemInHand(), positions);
            serverPlayer.sendSystemMessage(successStoreMessage(clickedPos), false);
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
            List<BlockPos> positions = readStoredPositions(itemStack);
            if (!positions.isEmpty()) {
                BlockPos pos1 = positions.get(0);
                BlockPos pos2 = positions.size() > 1 ? positions.get(1) : null;
                OpenSelectionsScreenPacketS2C.sendToClient(serverPlayer, new Component[]{
                        getMessage(pos1),
                        getMessage(pos2)
                }, new boolean[]{
                        true,
                        pos2 != null
                });
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide);
    }

    protected Component getMessage(@Nullable BlockPos pos) {
        String s = pos == null ? "unknown" : pos.toShortString();
        return Component.translatable("selections.confluence.magic_conch", s);
    }

    protected boolean checkAvailable(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Holder<Biome> biome = level.getBiome(pContext.getClickedPos());
        return pContext.getClickedFace() == Direction.UP && level.dimension() == OverworldUtils.dimension() &&
                (biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_BEACH) || biome.is(PortTags.Biomes.IS_STONY_SHORES));
    }

    @Override
    public @Nullable BlockPos getSelected(byte index, ItemStack itemStack) {
        List<BlockPos> positions = readStoredPositions(itemStack);
        return index >= 0 && index < positions.size() ? positions.get(index) : null;
    }

    @Override
    public void applySelected(byte index, ItemStack itemStack, ServerPlayer serverPlayer) {
        BlockPos pos = getSelected(index, itemStack);
        // 合法物品只会记录玩家点击过的位置；这里仍需防御手工篡改的极端坐标，
        // 避免选择包借物品 NBT 请求世界边界外区块。
        if (pos != null && serverPlayer.serverLevel().isInWorldBounds(pos) && serverPlayer.serverLevel().getWorldBorder().isWithinBounds(pos)) {
            serverPlayer.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        }
    }

    /// 读取当前版本唯一的海螺位置格式。
    ///
    /// <p>两个坐标必须从零开始连续出现；任一字段类型、版本或坐标损坏都会让整组状态失效，
    /// 避免损坏的第一个槽位永久占住容量，或被默认解释成世界原点。</p>
    static List<BlockPos> readStoredPositions(ItemStack stack) {
        CompoundTag itemTag = LibUtils.getItemStackNbtIfPresent(stack);
        if (itemTag == null || !itemTag.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            return new ArrayList<>();
        }
        CompoundTag runtime = itemTag.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT) || runtime.getInt("Version") != RUNTIME_VERSION || !runtime.contains("Count", Tag.TAG_INT)) {
            return new ArrayList<>();
        }
        int count = runtime.getInt("Count");
        if (count < 0 || count > 2) return new ArrayList<>();
        List<BlockPos> positions = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            String key = "Position" + index;
            if (!runtime.contains(key, Tag.TAG_COMPOUND)) return new ArrayList<>();
            CompoundTag positionTag = runtime.getCompound(key);
            // NbtUtils.readBlockPos 会把缺失或类型错误的坐标分量读成零，必须先逐项验型。
            if (!positionTag.contains("X", Tag.TAG_INT) || !positionTag.contains("Y", Tag.TAG_INT) || !positionTag.contains("Z", Tag.TAG_INT)) {
                return new ArrayList<>();
            }
            BlockPos position = NbtUtils.readBlockPos(positionTag);
            if (positions.contains(position)) return new ArrayList<>();
            positions.add(position);
        }
        return positions;
    }

    /// 写入完整快照并清除不再使用的早期扁平键；1.20 不承担旧格式迁移。
    static void writeStoredPositions(ItemStack stack, List<BlockPos> positions) {
        if (positions.size() > 2) {
            throw new IllegalArgumentException("Magic conch supports at most two positions");
        }
        LibUtils.updateItemStackNbt(stack, itemTag -> {
            itemTag.remove("pos1");
            itemTag.remove("pos2");
            CompoundTag runtime = new CompoundTag();
            runtime.putInt("Version", RUNTIME_VERSION);
            runtime.putInt("Count", positions.size());
            for (int index = 0; index < positions.size(); index++) {
                runtime.put("Position" + index, NbtUtils.writeBlockPos(positions.get(index)));
            }
            itemTag.put(RUNTIME_TAG, runtime);
        });
    }
}
