package org.confluence.mod.common.menu;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.jetbrains.annotations.Nullable;

public class NPCReforgeMenu extends AbstractContainerMenu {
    public static final int DATA_PREFIX_TYPE = 0;
    public static final int DATA_PREFIX_ID = 1;
    public static final int DATA_REFORGE_COST = 2;
    private final Player player;
    private final @Nullable BaseNPC npc;
    private final int[] data = {PrefixType.UNKNOWN.ordinal(), -1, 0x3F3F3F3F};
    public final SimpleContainer container = new SimpleContainer(1);

    public NPCReforgeMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, null);
    }

    /// 创建由 NPC 交易会话授权的重铸菜单。
    ///
    /// 客户端注册工厂使用无 NPC 的构造器；服务端生产路径必须保留来源 NPC，
    /// 以便实体死亡、跨维度或玩家离开交互距离时立即关闭菜单。
    public NPCReforgeMenu(int containerId, Inventory inventory, @Nullable BaseNPC npc) {
        super(ModMenuTypes.REFORGE_MENU.get(), containerId);
        this.player = inventory.player;
        this.npc = npc;
        container.addListener(this::slotsChanged);

        addSlot(new Slot(container, 0, 39, 44) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return PrefixUtils.couldReforge(itemStack);
            }

            @Override
            public void setChanged() {}
        });

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(inventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(inventory, m, 8 + m * 18, 142));
        }

        addDataSlot(DataSlot.shared(data, DATA_PREFIX_TYPE));
        addDataSlot(DataSlot.shared(data, DATA_PREFIX_ID));
        addDataSlot(DataSlot.shared(data, DATA_REFORGE_COST));
    }

    @Override
    public void slotsChanged(Container container) {
        // 提前决定下一个词条
        if (!player.level().isClientSide) {
            ItemStack itemStack = getReforgeItem();
            if (PrefixUtils.couldReforge(itemStack)) {
                RandomSource randomSource = RandomSource.create(itemStack.hashCode() | player.getRandom().nextInt());
                PrefixType prefixType = PrefixUtils.getPrefixType(itemStack);
                data[0] = prefixType.ordinal();
                data[1] = ModPrefix.ID_MAP.inverse().getOrDefault(prefixType.randomPrefix(randomSource), -1);
                data[2] = PrefixUtils.getReforgeCost(player, itemStack);
            } else {
                data[0] = PrefixType.UNKNOWN.ordinal();
                data[1] = -1;
                data[2] = 0x3F3F3F3F;
            }
        }
        super.slotsChanged(container);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        // 重铸
        if (id != 0) return false;
        int cost = data[DATA_REFORGE_COST];
        if (cost >= 0x3F3F3F3F) return false;
        PrefixType prefixType = PrefixType.byId(data[DATA_PREFIX_TYPE]);
        if (prefixType == PrefixType.UNKNOWN) return false;
        ItemStack itemStack = getReforgeItem();
        if (PrefixUtils.couldReforge(itemStack)) {
            ModPrefix modPrefix = ModPrefix.ID_MAP.get(data[DATA_PREFIX_ID]);
            if (modPrefix == null) return false;
            if (!(player instanceof ServerPlayer)) {
                return PlayerUtils.getMoney(player, true) >= cost;
            } else if (!PlayerUtils.tryCostMoney(player, cost, true)) {
                return false;
            }
            PrefixUtils.setAndUpdate(itemStack, prefixType, modPrefix);
            setRemoteSlot(0, itemStack);
            ((ServerPlayer) player).connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemStack));
            slotsChanged(container);
        }
        return true;
    }

    public ItemStack getReforgeItem() {
        return container.getItem(0);
    }

    public int getCost() {
        return data[DATA_REFORGE_COST];
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!moveItemStackTo(itemstack1, 1, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (PrefixUtils.couldReforge(itemstack1)) {
                if (!moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 1 && index < 28) {
                if (!moveItemStackTo(itemstack1, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 28 && index < 38 && !moveItemStackTo(itemstack1, 1, 28, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            broadcastChanges();
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return npc == null || npc.isAlive()
                && player.isAlive()
                && player.level() == npc.level()
                && player.distanceToSqr(npc) <= 64.0;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        clearContainer(player, container);
    }
}
