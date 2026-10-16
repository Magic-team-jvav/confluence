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
import org.confluence.mod.common.attachment.PlayerPiggyBankContainer;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.network.s2c.PiggyBankTotalMoneyPacket;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

public class NPCReforgeMenu extends AbstractContainerMenu implements NPCServiceMenu {
    public static final int REFORGE_SLOT_X = 50;
    public static final int REFORGE_SLOT_Y = 70;
    public static final int INVENTORY_X = 8;
    public static final int INVENTORY_Y = 117;
    public static final int INVENTORY_COLUMN_SPACING = 18;
    public static final int INVENTORY_ROW_SPACING = 18;
    public static final int HOTBAR_X = 8;
    public static final int HOTBAR_Y = 175;
    public static final int HOTBAR_SPACING = 18;

    public static final int BEST_PREFIX_PAUSE_TICKS = 40;

    public static final int DATA_PREFIX_TYPE = 0;
    public static final int DATA_PREFIX_ID = 1;
    public static final int DATA_REFORGE_COST = 2;
    private final Player player;
    private final @Nullable BaseNPC npc;
    private final int[] data = {PrefixType.UNKNOWN.ordinal(), -1, 0x3F3F3F3F, 0};

    public record ReforgeResult(ItemStack item, boolean best) {}

    private final Deque<ReforgeResult> reforgeResults = new ArrayDeque<>();
    private int reforgeRevision;
    private long availableMoney;
    private long reforgeReadyAt;
    public final SimpleContainer container = new SimpleContainer(1);

    public NPCReforgeMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, null);
    }

    public NPCReforgeMenu(int containerId, Inventory inventory, @Nullable BaseNPC npc) {
        super(ModMenuTypes.REFORGE_MENU.get(), containerId);
        this.player = inventory.player;
        this.npc = npc;
        if (player instanceof ServerPlayer serverPlayer) {
            PiggyBankTotalMoneyPacket.sendToClient(serverPlayer, PlayerPiggyBankContainer.of(player), true);
        }
        container.addListener(this::slotsChanged);

        addSlot(new Slot(container, 0, REFORGE_SLOT_X, REFORGE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return PrefixUtils.couldReforge(itemStack);
            }

            @Override
            public void setChanged() {}
        });

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(inventory, l + k * 9 + 9, INVENTORY_X + l * INVENTORY_COLUMN_SPACING, INVENTORY_Y + k * INVENTORY_ROW_SPACING));
            }
        }
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(inventory, m, HOTBAR_X + m * HOTBAR_SPACING, HOTBAR_Y));
        }

        addDataSlot(new DataSlot() {
            @Override
            public int get() {return reforgeRevision;}

            @Override
            public void set(int value) {
                int revision = value & 0xFFFF;
                if (reforgeRevision != revision && player.level().isClientSide) {
                    reforgeResults.addLast(new ReforgeResult(getReforgeItem().copy(), (revision & 0x8000) != 0));
                }
                reforgeRevision = revision;
            }
        });
        addDataSlot(DataSlot.shared(data, DATA_PREFIX_TYPE));
        addDataSlot(DataSlot.shared(data, DATA_PREFIX_ID));
        for (int half = 0; half < 2; half++) {
            int shift = half * 16;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {return data[DATA_REFORGE_COST] >>> shift & 0xFFFF;}

                @Override
                public void set(int value) {
                    data[DATA_REFORGE_COST] = data[DATA_REFORGE_COST] & ~(0xFFFF << shift) | (value & 0xFFFF) << shift;
                }
            });
        }
        for (int part = 0; part < 4; part++) {
            int shift = part * 16;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {return (int) (availableMoney >>> shift & 0xFFFF);}

                @Override
                public void set(int value) {
                    availableMoney = availableMoney & ~(0xFFFFL << shift) | (long) (value & 0xFFFF) << shift;
                }
            });
        }
        addDataSlot(DataSlot.shared(data, 3));
    }

    @Override
    public void slotsChanged(Container container) {
        // 提前决定下一个词条
        if (!player.level().isClientSide) {
            ItemStack stack = getReforgeItem();
            if (PrefixUtils.couldReforge(stack)) {
                RandomSource random = RandomSource.create(stack.hashCode() | player.getRandom().nextInt());
                PrefixType type = PrefixUtils.getPrefixType(stack);
                data[DATA_PREFIX_TYPE] = type.ordinal();
                data[DATA_PREFIX_ID] = ModPrefix.ID_MAP.inverse().getOrDefault(type.randomPrefix(random, stack), -1);
                data[DATA_REFORGE_COST] = PrefixUtils.getReforgeCost(player, stack);
            } else {
                data[DATA_PREFIX_TYPE] = PrefixType.UNKNOWN.ordinal();
                data[DATA_PREFIX_ID] = -1;
                data[DATA_REFORGE_COST] = 0x3F3F3F3F;
            }
        }
        super.slotsChanged(container);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        // 重铸
        if (id != 0 || !stillValid(player) || player.containerMenu != this || getCooldownTicks() > 0
                || !player.level().isClientSide && player.level().getGameTime() < reforgeReadyAt)
            return false;
        if (!player.level().isClientSide) refreshCost();
        int cost = data[DATA_REFORGE_COST];
        if (cost >= 0x3F3F3F3F) return false;
        PrefixType type = PrefixType.byId(data[DATA_PREFIX_TYPE]);
        if (type == PrefixType.UNKNOWN) return false;
        ItemStack stack = getReforgeItem();
        if (PrefixUtils.couldReforge(stack)) {
            ModPrefix prefix = ModPrefix.ID_MAP.get(data[DATA_PREFIX_ID]);
            if (prefix == null) return false;
            if (!(player instanceof ServerPlayer)) {
                return getAvailableMoney() >= cost;
            } else if (!PlayerUtils.tryCostMoney(player, cost, true)) {
                return false;
            }
            PrefixUtils.setAndUpdate(stack, type, prefix);
            reforgeRevision = (reforgeRevision + 1) & 0x7FFF;
            if (type.isBestPrefix(prefix, stack)) {
                reforgeRevision |= 0x8000;
                reforgeReadyAt = player.level().getGameTime() + BEST_PREFIX_PAUSE_TICKS;
                data[3] = BEST_PREFIX_PAUSE_TICKS;
            }
            setRemoteSlot(0, stack);
            ((ServerPlayer) player).connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, stack));
            slotsChanged(container);
            broadcastChanges();
        }
        return true;
    }

    private void refreshCost() {
        ItemStack stack = getReforgeItem();
        data[DATA_REFORGE_COST] = PrefixUtils.couldReforge(stack) ? PrefixUtils.getReforgeCost(player, stack) : 0x3F3F3F3F;
    }

    @Override
    public void broadcastChanges() {
        if (!player.level().isClientSide) {
            refreshCost();
            availableMoney = PlayerUtils.getMoney(player, true);
            data[3] = (int) Math.max(0, reforgeReadyAt - player.level().getGameTime());
        }
        super.broadcastChanges();
    }

    public long getAvailableMoney() {return availableMoney;}

    public boolean isLastReforgeBest() {return (reforgeRevision & 0x8000) != 0;}

    public ReforgeResult pollReforgeResult() {return reforgeResults.pollFirst();}

    public int getReforgeRevision() {return reforgeRevision;}

    public int getCooldownTicks() {return data[3];}

    public ItemStack getReforgeItem() {
        return container.getItem(0);
    }

    public int getCost() {
        return data[DATA_REFORGE_COST];
    }

    public @Nullable BaseNPC getNPC() {
        return npc;
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
            } else if (index < 28) {
                if (!moveItemStackTo(itemstack1, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 38 && !moveItemStackTo(itemstack1, 1, 28, false)) {
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
        return player.level().isClientSide || npc != null && npc.isAlive()
                && player.isAlive()
                && player.level() == npc.level()
                && player.distanceToSqr(npc) <= 64.0;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        clearContainer(player, container);
        if (npc != null) npc.endServiceSession(player, this);
    }
}
