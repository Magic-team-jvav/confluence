package org.confluence.mod.common.item.common;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModStructures;

import java.util.List;

/// 归巢蜜蜂：戴在头盔处时，第一人称视角会有一只原版蜜蜂指向已绑定的蜂巢结构（见 `HomingBeeRenderer`）。
///
/// 与地牢指南针同一套模型：**坐标只在获取时绑定一次**（从树妖处购买、或第一次戴到头上），
/// 之后固定写在物品 NBT 的 `pos` 里；渲染只读 NBT，**运行时不做任何结构搜索**。
/// 没绑定过坐标的蜜蜂只是不显示蜜蜂，不会在后台反复搜索。
public class HomingBeeItem extends CustomRarityItem {
    /// 蜂巢坐标，int[3]。
    public static final String POS_TAG = "pos";
    /// 目标所在维度，维度变化时重新绑定。
    public static final String DIMENSION_TAG = "dimension";
    /// 上次搜索时玩家所在的位置，避免在同一个地方反复触发结构搜索。
    private static final String QUERY_TAG = "queryPos";
    /// 距上次搜索超过该距离才重新搜索。
    private static final double RESEARCH_DISTANCE = 512.0;
    /// 结构搜索半径（区块），与地牢指南针一致。
    private static final int SEARCH_RADIUS = 100;

    public HomingBeeItem() {
        super(new Properties().stacksTo(1), ModRarity.GREEN);
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(CommonComponents.EMPTY);
        tooltipComponents.add(Component.translatable("item.modifiers.head").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.homing_bee.0").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    /// 一次性绑定最近的蜂巢：只在「从树妖处购买」和「第一次戴到头上」时调用。
    ///
    /// 已经绑定过（当前维度有坐标）就直接返回，坐标永不重算；没有蜂巢时按玩家移动距离节流，
    /// 避免在同一个位置反复做结构搜索。
    ///
    /// @return 是否成功绑定
    public static boolean bindHiveTarget(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof HomingBeeItem)) {
            return false;
        }
        ServerLevel level = player.serverLevel();
        String dimension = level.dimension().location().toString();
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
        if (tag != null && tag.getIntArray(POS_TAG).length == 3 && dimension.equals(tag.getString(DIMENSION_TAG))) {
            return false;
        }
        BlockPos origin = player.blockPosition();
        if (tag != null) {
            int[] query = tag.getIntArray(QUERY_TAG);
            if (query.length == 2) {
                double dx = origin.getX() - query[0];
                double dz = origin.getZ() - query[1];
                if (dx * dx + dz * dz < RESEARCH_DISTANCE * RESEARCH_DISTANCE) {
                    return false;
                }
            }
        }
        HolderSet<Structure> hive = HolderSet.direct(level.registryAccess().holderOrThrow(ModStructures.Keys.QUEEN_BEE_HIVE));
        Pair<BlockPos, Holder<Structure>> pair = level.getChunkSource().getGenerator()
                .findNearestMapStructure(level, hive, origin, SEARCH_RADIUS, false);
        LibUtils.updateItemStackNbt(stack, nbt -> {
            nbt.putIntArray(QUERY_TAG, new int[]{origin.getX(), origin.getZ()});
            if (pair == null) {
                nbt.remove(POS_TAG);
                nbt.remove(DIMENSION_TAG);
            } else {
                BlockPos pos = pair.getFirst();
                nbt.putIntArray(POS_TAG, new int[]{pos.getX(), pos.getY(), pos.getZ()});
                nbt.putString(DIMENSION_TAG, dimension);
            }
        });
        if (pair == null) {
            player.displayClientMessage(Component.translatable("message.confluence.hive_not_found").withStyle(ChatFormatting.RED), true);
            return false;
        }
        return true;
    }
}
