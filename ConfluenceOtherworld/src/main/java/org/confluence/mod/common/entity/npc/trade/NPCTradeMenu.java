package org.confluence.mod.common.entity.npc.trade;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.util.PlayerMoneyTransaction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// NPC 商店的服务端容器。
///
/// <p>布局暂时复用四行箱子菜单，客户端只负责显示商品、分页按钮和玩家背包。真正的购买、出售与库存消耗
/// 都在服务端按当前打开的菜单会话执行，客户端槽位里的物品展示不能被当作成交授权。</p>
public class NPCTradeMenu extends ChestMenu {
    private static final int TRADE_ROWS = 4;
    private static final int TRADE_SIZE = 9 * TRADE_ROWS;
    private static final int DATA_PAGE = 0;
    private static final int DATA_PAGE_COUNT = 1;

    private final BaseNPC npc;
    private final @Nullable ServerPlayer player;
    private final Container tradeContainer;
    private final @Nullable NPCTradeSession session;
    private final SimpleContainerData pageData = new SimpleContainerData(2);

    public static NPCTradeMenu fromNetwork(
            int containerId,
            Inventory inventory,
            FriendlyByteBuf data) {
        int entityId = data.readInt();
        var entity = inventory.player.level().getEntity(entityId);
        if (!(entity instanceof BaseNPC npc)) {
            throw new IllegalArgumentException(
                    "NPC trade menu requires a valid NPC entity, got entity id "
                            + entityId);
        }
        return new NPCTradeMenu(containerId, inventory, npc);
    }

    public NPCTradeMenu(int containerId, Inventory inventory, BaseNPC npc) {
        this(containerId, inventory, npc, new SimpleContainer(TRADE_SIZE), null);
    }

    /// 使用服务端在交互发生时已经筛选完成的报价快照创建菜单。
    ///
    /// <p>生产环境通过此入口保证“判断是否需要打开商店”和“实际显示的报价”来自同一份快照；
    /// 测试也可直接注入多页报价，不需要修改全局重载表。</p>
    public NPCTradeMenu(
            int containerId,
            Inventory inventory,
            BaseNPC npc,
            List<NPCTradeOffer> definitions) {
        this(
                containerId,
                inventory,
                npc,
                new SimpleContainer(TRADE_SIZE),
                List.copyOf(definitions));
    }

    private NPCTradeMenu(
            int containerId,
            Inventory inventory,
            BaseNPC npc,
            Container tradeContainer,
            @Nullable List<NPCTradeOffer> definitions) {
        super(
                ModMenuTypes.NPC_TRADE.get(),
                containerId,
                inventory,
                tradeContainer,
                TRADE_ROWS);
        this.npc = npc;
        this.player = inventory.player instanceof ServerPlayer sp ? sp : null;
        this.tradeContainer = tradeContainer;
        this.session = player == null
                ? null
                : new NPCTradeSession(
                player,
                npc,
                definitions == null
                        ? NPCTradeList.getAvailableOffers(player, npc)
                        : definitions,
                this);
        addDataSlots(pageData);

        int offerCount = session == null ? 0 : session.size();
        pageData.set(
                DATA_PAGE_COUNT,
                Math.max(1, (offerCount + TRADE_SIZE - 1) / TRADE_SIZE));
        populatePage(0);
    }

    @Override
    public void clicked(
            int slotIndex,
            int button,
            ClickType clickType,
            Player player) {
        if (slotIndex < 0 || slotIndex >= TRADE_SIZE) {
            super.clicked(slotIndex, button, clickType, player);
            return;
        }
        if (!(player instanceof ServerPlayer serverPlayer)
                || !canUseThisMenu(serverPlayer)) {
            return;
        }

        ItemStack cursor = getCarried();
        int offerIndex = getCurrentPage() * TRADE_SIZE + slotIndex;
        boolean offerSlot =
                session != null && offerIndex >= 0 && offerIndex < session.size();

        if (!cursor.isEmpty() && !offerSlot) {
            long value = ValueComponent.getValueLong(cursor, 0);
            if (value <= 0) {
                return;
            }
            long price = Math.max(
                    1L,
                    Math.round(
                            value * (double) npc.getMood()
                                    .getSellPriceMultiplier()));
            if (PlayerMoneyTransaction.credit(serverPlayer, price, false)) {
                setCarried(ItemStack.EMPTY);
                broadcastChanges();
            }
        } else if (cursor.isEmpty() && offerSlot && session.purchase(offerIndex)) {
            populatePage(getCurrentPage());
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }
        if (!(player instanceof ServerPlayer serverPlayer)
                || !canUseThisMenu(serverPlayer)) {
            return ItemStack.EMPTY;
        }
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem().copy();
        if (index >= TRADE_SIZE) {
            long value = ValueComponent.getValueLong(stack, 0);
            if (value <= 0) {
                return ItemStack.EMPTY;
            }
            long price = Math.max(
                    1L,
                    Math.round(
                            value * (double) npc.getMood()
                                    .getSellPriceMultiplier()));
            if (!PlayerMoneyTransaction.credit(serverPlayer, price, false)) {
                return ItemStack.EMPTY;
            }
            slot.set(ItemStack.EMPTY);
            broadcastChanges();
            return stack;
        }
        // From trade slot → do nothing on shift-click (use normal click for buy/refund)
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return npc.isAlive()
                && player.isAlive()
                && player.level() == npc.level()
                && player.distanceToSqr(npc) <= 64;
    }

    @Override
    public boolean clickMenuButton(Player player, int page) {
        if (!(player instanceof ServerPlayer serverPlayer)
                || !canUseThisMenu(serverPlayer)) {
            return false;
        }
        if (page < 0 || page >= getPageCount() || page == getCurrentPage()) {
            return false;
        }
        populatePage(page);
        return true;
    }

    public BaseNPC getNPC() {
        return npc;
    }

    public int getCurrentPage() {
        return pageData.get(DATA_PAGE);
    }

    public int getPageCount() {
        return pageData.get(DATA_PAGE_COUNT);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (session != null) {
            session.invalidate();
        }
    }

    /// 用打开菜单时生成的服务端报价快照填充指定页面。
    private void populatePage(int page) {
        for (int slot = 0; slot < TRADE_SIZE; slot++) {
            tradeContainer.setItem(slot, ItemStack.EMPTY);
        }

        int firstOffer = page * TRADE_SIZE;
        int availableOffers = session == null ? 0 : session.size();
        int pageSize = Math.max(
                0,
                Math.min(TRADE_SIZE, availableOffers - firstOffer));
        for (int slot = 0; slot < pageSize; slot++) {
            tradeContainer.setItem(
                    slot,
                    session.getDisplayResult(firstOffer + slot));
        }
        pageData.set(DATA_PAGE, page);
        broadcastChanges();
    }

    /// 校验玩家仍然操作自己当前打开的这一份菜单。
    ///
    /// <p>距离、维度和实体存活由 {@link #stillValid(Player)} 判断；这里额外限制当前容器身份，
    /// 防止已经关闭或被替换的旧菜单继续接受点击、出售和翻页请求。</p>
    private boolean canUseThisMenu(ServerPlayer player) {
        return player.containerMenu == this && stillValid(player);
    }
}
