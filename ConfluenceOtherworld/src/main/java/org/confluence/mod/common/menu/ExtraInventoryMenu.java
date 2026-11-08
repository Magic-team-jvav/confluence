package org.confluence.mod.common.menu;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.menu.ToggleSlot;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terra_curio.TerraCurio;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

import static org.confluence.mod.common.attachment.ExtraInventory.*;
import static org.confluence.mod.common.menu.TerraInventoryLayout.*;

/// 泰拉风格背包：额外槽位 + 饰品 + 原版背包 + 盔甲/副手 + 2×2 合成，布局见 {@link TerraInventoryLayout}。
/// 槽位顺序：额外槽位、饰品、主背包、快捷栏，之后才是盔甲、副手、合成结果与合成格（保持原有索引不变）。
public class ExtraInventoryMenu extends AbstractContainerMenu {
    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final ResourceLocation[] ARMOR_ICONS = {
            InventoryMenu.EMPTY_ARMOR_SLOT_HELMET, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS
    };

    public final ExtraInventory extraInventory;
    public final int invStart;
    private final int hotBar;
    private final int invEnd;
    public final int armorStart;
    public final int offhandIndex;
    private final Player player;

    public ExtraInventoryMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.EXTRA_INVENTORY.get(), containerId);
        Player player = inventory.player;
        this.player = player;
        this.extraInventory = ExtraInventory.of(player);
        Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
        int count = extraInventory.getContainerSize();
        for (int i = VANITY_ARMOR_START; i < count; i++) {
            if (i < VANITY_ARMOR_DYE_START) { // 0, 1, 2, 3
                addSlot(new ToggleSlot(extraInventory, i, VANITY_X, ARMOR_Y + i * CELL) { // vanity armor
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        if (stack.getItem() instanceof ArmorItem armorItem) {
                            EquipmentSlot equipmentSlot = armorItem.getEquipmentSlot();
                            int slotIndex = getSlotIndex();
                            if (slotIndex == 0) return equipmentSlot == EquipmentSlot.HEAD;
                            if (slotIndex == 1) return equipmentSlot == EquipmentSlot.CHEST;
                            if (slotIndex == 2) return equipmentSlot == EquipmentSlot.LEGS;
                            if (slotIndex == 3) return equipmentSlot == EquipmentSlot.FEET;
                        }
                        return false;
                    }

                    @Override
                    public boolean mayPickup(Player player) {
                        ItemStack itemstack = getItem();
                        return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) && super.mayPickup(player);
                    }
                });
            } else if (i < COINS_START) { // 4, 5, 6 ,7
                addSlot(visible(new DyeToggleSlot(extraInventory, i, DYE_X, ARMOR_Y + (i - VANITY_ARMOR_DYE_START) * CELL)));
            } else if (i < AMMO_START) { // 8, 9, 10, 11
                addSlot(new CoinsSlot(extraInventory, i, COINS_X, inventoryY(i - COINS_START)));
            } else if (i < EQUIPMENT_START) { // 12, 13, 14, 15
                addSlot(new Slot(extraInventory, i, AMMO_X, inventoryY(i - AMMO_START)) { // ammo
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.is(ModTags.Items.AMMO);
                    }
                });
            } else if (i < EQUIPMENT_DYE_START) { // 16, 17, 18, 19, 20
                int j = i - EQUIPMENT_START;
                addSlot(new ToggleSlot(extraInventory, i, EQUIPMENT_X, EQUIPMENT_Y + j * CELL) { // equipment
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.is(switch (j) {
                            case LIGHT_PET_INDEX -> ModTags.Items.LIGHT_PET;
                            case MINECART_INDEX -> ModTags.Items.MINECART;
                            case HOOK_INDEX -> ModTags.Items.HOOK;
                            case MOUNT_INDEX -> ModTags.Items.MOUNT;
                            default -> ModTags.Items.PET;
                        });
                    }
                });
            } else if (i < TRASH_START) { // 21, 22, 23, 24, 25
                int j = i - EQUIPMENT_DYE_START;
                addSlot(visible(new DyeToggleSlot(extraInventory, i, EQUIPMENT_DYE_X, EQUIPMENT_Y + j * CELL)));
            } else if (i < ACCESSORY_DYE_START) { // 26
                addSlot(new Slot(extraInventory, i, TRASH_X, TRASH_Y));
            } else { // 27...
                addSlot(visible(new DyeToggleSlot(extraInventory, i, DYE_X, ACCESSORY_Y + (i - ACCESSORY_DYE_START) * CELL)));
            }
        }
        if (optional.isPresent()) {
            ICurioStacksHandler accessory = optional.get().getCurios().get(TerraCurio.CURIO_SLOT);
            if (accessory != null) {
                ToggleCurioSlot.WrappedContainer container = new ToggleCurioSlot.WrappedContainer(accessory);
                count += accessory.getSlots();
                for (int j = 0; j < accessory.getSlots(); j++) {
                    addSlot(new ToggleCurioSlot(player, container, j, ARMOR_X, ACCESSORY_Y + j * CELL));
                }
            }
        }
        this.invStart = count;
        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(inventory, l + k * 9 + 9, inventoryX(l), inventoryY(k + 1)));
            }
        }
        this.hotBar = invStart + 27;
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(inventory, m, inventoryX(m), inventoryY(0)));
        }
        this.invEnd = hotBar + 9;

        // 盔甲与副手（原来只在原版背包里）
        this.armorStart = invEnd;
        for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            EquipmentSlot equipmentSlot = ARMOR_SLOTS[i];
            addSlot(new TerraArmorSlot(inventory, player, equipmentSlot, 39 - i, ARMOR_X, ARMOR_Y + i * CELL, ARMOR_ICONS[i]));
        }
        this.offhandIndex = armorStart + ARMOR_SLOTS.length;
        addSlot(new Slot(inventory, Inventory.SLOT_OFFHAND, OFFHAND_X, OFFHAND_Y) {
            @Override
            public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
                player.onEquipItem(EquipmentSlot.OFFHAND, oldStack, newStack);
                super.setByPlayer(newStack, oldStack);
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
        // 合成不再用 2×2 网格，而是泰拉式的配方列表（见 StationCrafting）
        // 泰拉的时装饰品：饰品左边一列（时装盔甲下面），用 Curios 的 cosmetic 栏位；放在列表最后，不影响前面的序号
        this.cosmeticStart = slots.size();
        if (optional.isPresent()) {
            ICurioStacksHandler accessory = optional.get().getCurios().get(TerraCurio.CURIO_SLOT);
            if (accessory != null && accessory.hasCosmetic()) {
                ToggleCurioSlot.WrappedContainer cosmetics = new ToggleCurioSlot.WrappedContainer(accessory.getCosmeticStacks());
                for (int j = 0; j < accessory.getCosmeticStacks().getSlots(); j++) {
                    addSlot(new ToggleCurioSlot(player, cosmetics, j, VANITY_X, ACCESSORY_Y + j * CELL).cosmetic());
                }
            }
        }
    }

    /// 时装饰品槽的起始序号（到列表末尾）
    public int cosmeticStart;

    private static ToggleSlot visible(ToggleSlot slot) {
        slot.setEnable(true); // 泰拉风格：染料槽与物品槽同时显示
        return slot;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index >= armorStart) { // 盔甲、副手
                if (!moveItemStackTo(itemstack1, invStart, invEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= invStart - extraInventory.getSizeAccessoryDye() && index < invStart) {
                if (!moveItemStackTo(itemstack1, invStart, invEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < extraInventory.getContainerSize()) {
                if (!moveItemStackTo(itemstack1, invStart, invEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveToArmor(player, itemstack1) && !moveItemStackTo(itemstack1, 0, invStart, false)) {
                if (index < hotBar) {
                    if (!moveItemStackTo(itemstack1, hotBar, invEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < invEnd && !moveItemStackTo(itemstack1, invStart, hotBar, false)) {
                    return ItemStack.EMPTY;
                }
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

    public boolean isPlayerInventorySlot(int index) {
        return index >= invStart && index < invEnd;
    }

    /// 泰拉的“右键装备”：盔甲 → 盔甲槽；宠物/钩爪/坐骑等 → 装备栏；饰品 → 第一个空饰品槽（都满时换第一个能换的）。
    /// 返回目标槽位序号，不能装备返回 -1。客户端用它判断右键是否装备，服务端用它执行。
    public int equipTarget(Player player, ItemStack stack) {
        if (stack.isEmpty()) return -1;
        EquipmentSlot equipmentSlot = player.getEquipmentSlotForItem(stack);
        if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
            for (int i = 0; i < ARMOR_SLOTS.length; i++) {
                if (ARMOR_SLOTS[i] == equipmentSlot) {
                    Slot target = slots.get(armorStart + i);
                    return target.mayPlace(stack) && (!target.hasItem() || target.mayPickup(player)) ? armorStart + i : -1;
                }
            }
        }
        int fallback = -1;
        for (int i = EQUIPMENT_START; i < EQUIPMENT_DYE_START; i++) {
            Slot target = slots.get(i);
            if (!target.isActive() || !target.mayPlace(stack)) continue;
            if (!target.hasItem()) return i;
            if (fallback < 0 && target.mayPickup(player)) fallback = i;
        }
        for (int i = extraInventory.getContainerSize(); i < invStart; i++) {
            Slot target = slots.get(i);
            if (!target.isActive() || !target.mayPlace(stack)) continue;
            if (!target.hasItem()) return i;
            if (fallback < 0 && target.mayPickup(player)) fallback = i;
        }
        return fallback;
    }

    /// Shift 点击盔甲时优先穿到身上（盔甲槽为空时）
    private boolean moveToArmor(Player player, ItemStack stack) {
        EquipmentSlot equipmentSlot = player.getEquipmentSlotForItem(stack);
        if (equipmentSlot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) return false;
        for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            if (ARMOR_SLOTS[i] == equipmentSlot) {
                int target = armorStart + i;
                return !slots.get(target).hasItem() && moveItemStackTo(stack, target, target + 1, false);
            }
        }
        return false;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        if (startIndex <= TRASH_START && endIndex >= TRASH_START) {
            return super.moveItemStackTo(stack, startIndex, TRASH_START, reverseDirection) ||
                    super.moveItemStackTo(stack, ACCESSORY_DYE_START, endIndex, reverseDirection);
        }
        return super.moveItemStackTo(stack, startIndex, endIndex, reverseDirection);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        boolean isThrow = clickType == ClickType.THROW;
        if ((slotId >= 0 && slotId < slots.size()) && (slotId == TRASH_START || isThrow)) {
            ClickAction action = button == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
            Slot slot = slots.get(isThrow ? TRASH_START : slotId);
            ItemStack carried = isThrow ? slots.get(slotId).getItem() : getCarried();
            ItemStack slotItem = slot.getItem();
            if (tryItemClickBehaviourOverride(player, action, slot, slotItem, carried)) return;
            if (isThrow) {
                slot.setByPlayer(carried);
                slots.get(slotId).setByPlayer(ItemStack.EMPTY);
            } else if (carried.isEmpty()) {
                int j3 = action == ClickAction.PRIMARY ? slotItem.getCount() : (slotItem.getCount() + 1) / 2;
                Optional<ItemStack> optional = slot.tryRemove(j3, Integer.MAX_VALUE, player);
                if (optional.isPresent()) {
                    ItemStack itemStack = optional.get();
                    setCarried(itemStack);
                    slot.onTake(player, itemStack);
                }
            } else if (ItemStack.isSameItemSameComponents(carried, slotItem)) {
                slotItem.grow(Math.min(slot.getMaxStackSize(slotItem) - slotItem.getCount(), carried.getCount()));
                setCarried(ItemStack.EMPTY);
            } else {
                slot.setByPlayer(carried);
                setCarried(ItemStack.EMPTY);
            }
        } else {
            super.clicked(slotId, button, clickType, player);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static class CoinsSlot extends Slot {
        public CoinsSlot(ExtraInventory extraInventory, int i, int x, int y) {
            super(extraInventory, i, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ModTags.Items.COINS);
        }
    }

    /// 原版 {@code ArmorSlot} 不是 public，这里照搬其行为
    public static class TerraArmorSlot extends Slot {
        private final Player owner;
        private final EquipmentSlot equipmentSlot;
        private final ResourceLocation emptyIcon;

        public TerraArmorSlot(Inventory inventory, Player owner, EquipmentSlot equipmentSlot, int index, int x, int y, ResourceLocation emptyIcon) {
            super(inventory, index, x, y);
            this.owner = owner;
            this.equipmentSlot = equipmentSlot;
            this.emptyIcon = emptyIcon;
        }

        @Override
        public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
            owner.onEquipItem(equipmentSlot, oldStack, newStack);
            super.setByPlayer(newStack, oldStack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return equipmentSlot == owner.getEquipmentSlotForItem(stack);
        }

        @Override
        public boolean mayPickup(Player player) {
            ItemStack itemstack = getItem();
            return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) && super.mayPickup(player);
        }

        @Override
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, emptyIcon);
        }
    }
}
