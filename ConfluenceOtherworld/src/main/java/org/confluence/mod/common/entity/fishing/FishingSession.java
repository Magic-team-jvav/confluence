package org.confluence.mod.common.entity.fishing;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.BaitItems;
import org.confluence.mod.common.item.fishing.DevFishingRod;
import org.confluence.mod.common.item.fishing.IBait;
import org.confluence.terra_curio.util.TCUtils;

/// 一次抛竿的服务端状态。鱼饵留在背包，成功捕获后只结算一次，取消和断线不扣鱼饵。
public final class FishingSession {
    private final ItemStack rod;
    private final ItemStack bait;
    private final InteractionHand hand;
    private boolean settled;
    private float lastSentPower = Float.NaN;

    public FishingSession(Player player, ItemStack rod, InteractionHand hand) {
        this.rod = rod;
        this.hand = hand;
        this.bait = IBait.getFirstBait(player.getInventory());
    }

    /// 无饵抛竿有效；钓竿须留在原来的手中，已绑定的鱼饵被移走或耗尽则终止垂钓。
    public boolean isValid(Player player) {
        if (player.getItemInHand(hand) != rod || rod.isEmpty()) return false;
        // 开始抛竿时没有鱼饵也可正常钓鱼；已绑定鱼饵耗尽不能按无饵会话放行。
        if (isDeveloper() || bait == ItemStack.EMPTY) return true;
        if (bait.isEmpty()) return false;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++)
            if (player.getInventory().getItem(slot) == bait) return true;
        return false;
    }

    public ItemStack rod() {return rod;}

    public ItemStack bait() {return bait;}

    public boolean isDeveloper() {return rod.getItem() instanceof DevFishingRod;}

    /// 仪表仅在数值变化时同步，避免每个 tick 发送相同渔力。
    public boolean updateDisplayedPower(float power) {
        if (Float.compare(lastSentPower, power) == 0) return false;
        lastSentPower = power;
        return true;
    }

    /// 成功取得物品或钓上敌怪后执行；实际消耗始终作用于本次抛竿绑定的背包堆叠。
    public void consumeBait(Player player) {
        if (settled) return;
        settled = true;
        if (bait.isEmpty() || !isValid(player) || isDeveloper() || player.hasInfiniteMaterials())
            return;
        float chance = bait.is(BaitItems.GOLD_WORM.get()) ? 0.05F : 1.0F / (1 + IBait.of(bait).getBaitBonus() * 100 / 6);
        if (TCUtils.hasType(player, AccessoryItems.TACKLE$BOX)) chance *= 2.0F / 3.0F;
        if (player.getRandom().nextFloat() < chance) bait.shrink(1);
    }

    /// 无活动鱼钩时供渔力显示和物品提示选择钓竿，优先主手。
    public static ItemStack heldRod(Player player) {
        if (player.getMainHandItem().getItem() instanceof FishingRodItem)
            return player.getMainHandItem();
        if (player.getOffhandItem().getItem() instanceof FishingRodItem)
            return player.getOffhandItem();
        return ItemStack.EMPTY;
    }
}
