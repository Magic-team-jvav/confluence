package org.confluence.mod.common.attachment;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.confluence.mod.common.item.hook.BaseHookItem;
import org.confluence.mod.network.s2c.ExtraInventoryStackPacketS2C;
import org.confluence.terra_curio.TerraCurio;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class ExtraInventory extends ItemStackHandler implements Container {
    public static final int SIZE_VANITY_ARMOR = 4;
    public static final int SIZE_COINS = 4;
    public static final int SIZE_AMMO = 4;
    public static final int SIZE_EQUIPMENT = 4;
    public static final int SIZE_DYE_EXCEPT_ACCESSORY_DYE = SIZE_VANITY_ARMOR + SIZE_EQUIPMENT;
    public static final int SIZE_EXCEPT_ACCESSORY_DYE = SIZE_VANITY_ARMOR + SIZE_COINS + SIZE_AMMO + SIZE_EQUIPMENT + SIZE_DYE_EXCEPT_ACCESSORY_DYE;

    public static final int VANITY_ARMOR_START = 0;
    public static final int COINS_START = VANITY_ARMOR_START + SIZE_VANITY_ARMOR;
    public static final int AMMO_START = COINS_START + SIZE_COINS;
    public static final int EQUIPMENT_START = AMMO_START + SIZE_AMMO;
    public static final int DYE_START = EQUIPMENT_START + SIZE_EQUIPMENT;

    private int sizeAccessoryDye = 0;
    private transient boolean initialized = false;
    private transient ServerPlayer serverPlayer;
    private transient NonNullList<ItemStack> previousStacks;
    private transient boolean dirty;

    public ExtraInventory() {
        super(SIZE_EXCEPT_ACCESSORY_DYE);
        this.previousStacks = NonNullList.withSize(SIZE_EXCEPT_ACCESSORY_DYE, ItemStack.EMPTY);
    }

    public void setAccessoryDyes(int size) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(SIZE_EXCEPT_ACCESSORY_DYE + size, ItemStack.EMPTY);
        for (int i = 0; i < stacks.size(); i++) {
            itemStacks.set(i, stacks.get(i));
        }
        this.stacks = itemStacks;
        this.sizeAccessoryDye = size;
    }

    public int getSizeAccessoryDye() {
        return sizeAccessoryDye;
    }

    public ItemStack getVanityArmor(int index) {
        validateIndex(index, SIZE_VANITY_ARMOR);
        return getItem(VANITY_ARMOR_START + index);
    }

    public ItemStack getCoins(int index) {
        validateIndex(index, SIZE_COINS);
        return getItem(COINS_START + index);
    }

    public ItemStack getAmmo(int index) {
        validateIndex(index, SIZE_AMMO);
        return getItem(AMMO_START + index);
    }

    public ItemStack getPet() {
        return getItem(EQUIPMENT_START);
    }

    public ItemStack getLightPet() {
        return getItem(EQUIPMENT_START + 1);
    }

    public ItemStack getMinecart() {
        return getItem(EQUIPMENT_START + 2);
    }

    public ItemStack getHook() {
        return getItem(EQUIPMENT_START + 3);
    }

    public ItemStack getVanityArmorDye(int index) {
        validateIndex(index, SIZE_VANITY_ARMOR);
        return getItem(DYE_START + VANITY_ARMOR_START + index);
    }

    public ItemStack getPetDye() {
        return getItem(DYE_START + EQUIPMENT_START);
    }

    public ItemStack getLightPetDye() {
        return getItem(DYE_START + EQUIPMENT_START + 1);
    }

    public ItemStack getMinecartDye() {
        return getItem(DYE_START + EQUIPMENT_START + 2);
    }

    public ItemStack getHookDye() {
        return getItem(DYE_START + EQUIPMENT_START + 3);
    }

    public ItemStack getAccessoryDye(int index) {
        validateIndex(index, sizeAccessoryDye);
        return getItem(DYE_START + SIZE_DYE_EXCEPT_ACCESSORY_DYE + index);
    }

    private void validateIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new RuntimeException("Slot " + index + " not in valid range - [0," + size + ")");
        }
    }

    public void sync(ServerPlayer serverPlayer) {
        initialize(serverPlayer);
        if (dirty) {
            for (int i = 0; i < getContainerSize(); i++) {
                ItemStack itemStack = getItem(i);
                ItemStack previous = previousStacks.get(i);
                if (!ItemStack.matches(itemStack, previous)) {
                    ExtraInventoryStackPacketS2C.sendToPlayersTrackingEntityAndSelf(serverPlayer, serverPlayer, sizeAccessoryDye, i, itemStack);
                    if (previous.getItem() instanceof BaseHookItem hookItem) {
                        hookItem.onUnequip(serverPlayer, itemStack, previous);
                    }
                    previousStacks.set(i, itemStack.copy());
                }
            }
            this.dirty = false;
        }
    }

    public void initialize(ServerPlayer serverPlayer) {
        this.serverPlayer = serverPlayer;
        if (!initialized) {
            int accessoryDye = CuriosApi.getCuriosInventory(serverPlayer).map(handler -> {
                ICurioStacksHandler accessory = handler.getCurios().get(TerraCurio.CURIO_SLOT);
                return accessory == null ? 0 : accessory.getSlots();
            }).orElse(0);
            setAccessoryDyes(accessoryDye);
            this.previousStacks = NonNullList.withSize(SIZE_EXCEPT_ACCESSORY_DYE + accessoryDye, ItemStack.EMPTY);
            for (int i = 0; i < stacks.size(); i++) {
                previousStacks.set(i, stacks.get(i).copy());
            }
            this.initialized = true;
        }
    }

    @Override
    public int getContainerSize() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return extractItem(slot, amount, false);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack itemStack = getItem(slot);
        setItem(slot, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        stacks.set(slot, stack);
        setChanged();
    }

    @Override
    public void setChanged() {
        this.dirty = true;
    }

    @Override
    protected void onContentsChanged(int slot) {
        setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        stacks.clear();
    }
}
