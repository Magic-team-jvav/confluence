package org.confluence.mod.common.menu;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModTags;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/// 泰拉风格背包的服务端操作：收藏（Alt+点击）与整理背包。
public final class TerraInventoryActions {
    /// 主背包（不含快捷栏），与泰拉一致：整理不动快捷栏
    private static final int MAIN_START = Inventory.getSelectionSize();
    private static final int MAIN_END = Inventory.INVENTORY_SIZE;
    private static final TagKey<Item> ACCESSORY = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", "accessory"));

    /// 按类别（武器、工具、盔甲、饰品、弹药、消耗品、方块、其他、钱币），再按物品 ID、数量排序
    private static final Comparator<ItemStack> ORDER = Comparator
            .comparingInt(TerraInventoryActions::category)
            .thenComparing(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())
            .thenComparing(Comparator.comparingInt(ItemStack::getCount).reversed());

    public static boolean isFavorite(ItemStack stack) {
        return stack.has(ModDataComponentTypes.FAVORITE.get());
    }

    /// 只允许收藏玩家自己背包里的物品
    public static void toggleFavorite(ServerPlayer player, int slotIndex) {
        AbstractContainerMenu menu = player.containerMenu;
        if (slotIndex < 0 || slotIndex >= menu.slots.size()) return;
        Slot slot = menu.getSlot(slotIndex);
        if (slot.container != player.getInventory() || !slot.hasItem()) return;
        ItemStack stack = slot.getItem();
        if (isFavorite(stack)) {
            stack.remove(ModDataComponentTypes.FAVORITE.get());
        } else {
            stack.set(ModDataComponentTypes.FAVORITE.get(), Unit.INSTANCE);
        }
        slot.setChanged();
        menu.broadcastChanges();
    }

    /// 合并可堆叠的物品并排序；收藏的物品留在原位
    public static void sortInventory(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        boolean[] fixed = new boolean[MAIN_END];
        List<ItemStack> merged = new ArrayList<>();
        for (int i = MAIN_START; i < MAIN_END; i++) {
            ItemStack stack = inventory.items.get(i);
            if (stack.isEmpty()) continue;
            if (isFavorite(stack)) {
                fixed[i] = true;
                continue;
            }
            ItemStack loose = stack.copy();
            for (ItemStack target : merged) {
                if (loose.isEmpty()) break;
                if (ItemStack.isSameItemSameComponents(target, loose) && target.getCount() < target.getMaxStackSize()) {
                    int move = Math.min(loose.getCount(), target.getMaxStackSize() - target.getCount());
                    target.grow(move);
                    loose.shrink(move);
                }
            }
            if (!loose.isEmpty()) merged.add(loose);
        }
        merged.sort(ORDER);
        Iterator<ItemStack> iterator = merged.iterator();
        for (int i = MAIN_START; i < MAIN_END; i++) {
            if (fixed[i]) continue;
            inventory.items.set(i, iterator.hasNext() ? iterator.next() : ItemStack.EMPTY);
        }
        inventory.setChanged();
        player.containerMenu.broadcastChanges();
    }

    public static final byte LOOT_ALL = 0;
    public static final byte DEPOSIT_ALL = 1;
    public static final byte QUICK_STACK = 2;
    public static final byte SORT_CONTAINER = 3;
    public static final byte RESTOCK = 4;

    /// 泰拉箱子界面的按钮。只作用于当前打开的容器菜单；收藏的物品与快捷栏不会被存入
    public static void containerAction(ServerPlayer player, byte action) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu == player.inventoryMenu || !menu.stillValid(player)) return;
        Inventory inventory = player.getInventory();
        List<Slot> container = new ArrayList<>();
        List<Slot> playerSlots = new ArrayList<>();
        for (Slot slot : menu.slots) {
            if (slot.container instanceof org.confluence.mod.common.attachment.ExtraInventory) continue; // 存钱罐界面里玩家自己的钱币槽
            if (slot.container == inventory) {
                playerSlots.add(slot);
            } else {
                container.add(slot);
            }
        }
        if (container.isEmpty()) return;
        switch (action) {
            case LOOT_ALL -> {
                for (Slot slot : container) {
                    quickMoveAll(player, menu, slot);
                }
            }
            case DEPOSIT_ALL, QUICK_STACK -> {
                for (Slot slot : playerSlots) {
                    int index = slot.getContainerSlot();
                    if (index < MAIN_START || index >= MAIN_END || !slot.hasItem() || isFavorite(slot.getItem())) continue;
                    if (action == QUICK_STACK && container.stream().noneMatch(target -> ItemStack.isSameItemSameComponents(target.getItem(), slot.getItem()))) continue;
                    quickMoveAll(player, menu, slot);
                }
            }
            case SORT_CONTAINER -> {
                List<ItemStack> merged = new ArrayList<>();
                for (Slot slot : container) {
                    if (slot.hasItem()) mergeInto(merged, slot.getItem().copy());
                }
                merged.sort(ORDER);
                Iterator<ItemStack> iterator = merged.iterator();
                for (Slot slot : container) {
                    slot.set(iterator.hasNext() ? iterator.next() : ItemStack.EMPTY);
                }
            }
            case RESTOCK -> {
                for (Slot target : playerSlots) {
                    ItemStack stack = target.getItem();
                    if (stack.isEmpty() || !stack.isStackable()) continue;
                    for (Slot source : container) {
                        int missing = stack.getMaxStackSize() - stack.getCount();
                        if (missing <= 0) break;
                        ItemStack available = source.getItem();
                        if (!ItemStack.isSameItemSameComponents(available, stack) && !(isFavorite(stack) && sameIgnoringFavorite(available, stack))) continue;
                        int move = Math.min(missing, available.getCount());
                        available.shrink(move);
                        stack.grow(move);
                        source.setChanged();
                        target.setChanged();
                    }
                }
            }
            default -> {
                return;
            }
        }
        menu.broadcastChanges();
    }

    // ---------- 泰拉背包里的单格操作与“快速堆叠到附近箱子” ----------
    public static final byte SLOT_TRASH = 0;
    public static final byte SLOT_EQUIP = 1;
    public static final byte QUICK_STACK_NEARBY = 2;
    /// 泰拉的范围约 12 格；这里与原版箱子的可交互距离一致
    public static final int NEARBY_RADIUS = 8;

    public static void slotAction(ServerPlayer player, byte action, int slotIndex) {
        if (action == QUICK_STACK_NEARBY) {
            quickStackNearby(player);
            return;
        }
        if (!(player.containerMenu instanceof ExtraInventoryMenu menu)) return;
        if (!menu.getCarried().isEmpty() || !menu.isPlayerInventorySlot(slotIndex)) return;
        Slot source = menu.getSlot(slotIndex);
        ItemStack stack = source.getItem();
        if (stack.isEmpty()) return;
        switch (action) {
            case SLOT_TRASH -> { // Ctrl+点击：放进垃圾桶（替换原来的东西），收藏的不行
                if (isFavorite(stack)) return;
                menu.getSlot(org.confluence.mod.common.attachment.ExtraInventory.TRASH_START).set(stack);
                source.set(ItemStack.EMPTY);
            }
            case SLOT_EQUIP -> { // 右键：装备，和装备着的交换
                int targetIndex = menu.equipTarget(player, stack);
                if (targetIndex < 0) return;
                Slot target = menu.getSlot(targetIndex);
                ItemStack one = stack.split(1);
                ItemStack old = target.getItem();
                target.setByPlayer(one);
                if (!old.isEmpty()) {
                    if (stack.isEmpty()) {
                        source.set(old);
                    } else if (!player.getInventory().add(old)) {
                        player.drop(old, false);
                    }
                }
                source.setChanged();
            }
            default -> {
                return;
            }
        }
        menu.broadcastChanges();
    }

    /// 背包主区（不含快捷栏、收藏）里，附近箱子/木桶已经有的物品放进去：先补满同种堆叠，再占空格
    public static void quickStackNearby(ServerPlayer player) {
        net.minecraft.world.level.Level level = player.level();
        net.minecraft.core.BlockPos center = player.blockPosition();
        List<net.minecraft.world.Container> containers = new ArrayList<>();
        for (net.minecraft.core.BlockPos pos : net.minecraft.core.BlockPos.betweenClosed(center.offset(-NEARBY_RADIUS, -NEARBY_RADIUS, -NEARBY_RADIUS), center.offset(NEARBY_RADIUS, NEARBY_RADIUS, NEARBY_RADIUS))) {
            if (!level.isLoaded(pos)) continue;
            net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof net.minecraft.world.level.block.entity.ChestBlockEntity || blockEntity instanceof net.minecraft.world.level.block.entity.BarrelBlockEntity)) continue;
            net.minecraft.world.level.block.state.BlockState state = blockEntity.getBlockState();
            if (state.hasProperty(org.confluence.mod.common.block.common.BaseChestBlock.UNLOCKED) && !state.getValue(org.confluence.mod.common.block.common.BaseChestBlock.UNLOCKED)) continue; // 上锁的箱子
            if (((net.minecraft.world.RandomizableContainer) blockEntity).getLootTable() != null) continue; // 还没打开过的战利品箱
            if (!net.minecraft.world.Container.stillValidBlockEntity(blockEntity, player, NEARBY_RADIUS + 2)) continue;
            containers.add((net.minecraft.world.Container) blockEntity);
        }
        if (containers.isEmpty()) return;
        Inventory inventory = player.getInventory();
        boolean moved = false;
        for (int i = MAIN_START; i < MAIN_END; i++) {
            ItemStack stack = inventory.items.get(i);
            if (stack.isEmpty() || isFavorite(stack)) continue;
            for (net.minecraft.world.Container container : containers) {
                if (stack.isEmpty()) break;
                if (!containsSame(container, stack)) continue;
                int before = stack.getCount();
                insert(container, stack);
                if (stack.getCount() != before) {
                    container.setChanged();
                    moved = true;
                }
            }
        }
        if (moved) {
            inventory.setChanged();
            player.containerMenu.broadcastChanges();
            level.playSound(null, center, net.minecraft.sounds.SoundEvents.ITEM_PICKUP, net.minecraft.sounds.SoundSource.PLAYERS, 0.4F, 0.8F);
        }
    }

    private static boolean containsSame(net.minecraft.world.Container container, ItemStack stack) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (ItemStack.isSameItemSameComponents(container.getItem(i), stack)) return true;
        }
        return false;
    }

    private static void insert(net.minecraft.world.Container container, ItemStack stack) {
        for (int i = 0; i < container.getContainerSize() && !stack.isEmpty(); i++) {
            ItemStack target = container.getItem(i);
            if (!ItemStack.isSameItemSameComponents(target, stack)) continue;
            int move = Math.min(stack.getCount(), Math.min(target.getMaxStackSize(), container.getMaxStackSize()) - target.getCount());
            if (move <= 0) continue;
            target.grow(move);
            stack.shrink(move);
        }
        for (int i = 0; i < container.getContainerSize() && !stack.isEmpty(); i++) {
            if (!container.getItem(i).isEmpty() || !container.canPlaceItem(i, stack)) continue;
            container.setItem(i, stack.split(Math.min(stack.getCount(), container.getMaxStackSize())));
        }
    }

    private static void quickMoveAll(ServerPlayer player, AbstractContainerMenu menu, Slot slot) {
        for (int guard = 0; guard < 64 && slot.hasItem(); guard++) {
            if (menu.quickMoveStack(player, slot.index).isEmpty()) break;
        }
    }

    private static void mergeInto(List<ItemStack> merged, ItemStack loose) {
        for (ItemStack target : merged) {
            if (loose.isEmpty()) return;
            if (ItemStack.isSameItemSameComponents(target, loose) && target.getCount() < target.getMaxStackSize()) {
                int move = Math.min(loose.getCount(), target.getMaxStackSize() - target.getCount());
                target.grow(move);
                loose.shrink(move);
            }
        }
        if (!loose.isEmpty()) merged.add(loose);
    }

    private static boolean sameIgnoringFavorite(ItemStack a, ItemStack b) {
        ItemStack copy = b.copy();
        copy.remove(ModDataComponentTypes.FAVORITE.get());
        return ItemStack.isSameItemSameComponents(a, copy);
    }

    public static int category(ItemStack stack) {
        Item item = stack.getItem();
        if (stack.is(ModTags.Items.COINS)) return 8;
        if (isWeapon(stack, item)) return 0;
        if (item instanceof DiggerItem || item instanceof ShearsItem || item instanceof FishingRodItem || item instanceof FlintAndSteelItem) return 1;
        if (item instanceof ArmorItem) return 2;
        if (stack.is(ACCESSORY)) return 3;
        if (stack.is(ModTags.Items.AMMO) || item instanceof ArrowItem) return 4;
        if (stack.has(DataComponents.FOOD) || item instanceof PotionItem) return 5;
        if (item instanceof BlockItem) return 6;
        return 7;
    }

    private static boolean isWeapon(ItemStack stack, Item item) {
        if (item instanceof SwordItem || item instanceof ProjectileWeaponItem || item instanceof TridentItem || item instanceof MaceItem) return true;
        if (item instanceof DiggerItem) return false;
        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        return modifiers.modifiers().stream().anyMatch(entry -> entry.attribute().is(Attributes.ATTACK_DAMAGE));
    }

    private TerraInventoryActions() {}
}
