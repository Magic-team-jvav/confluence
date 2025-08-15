package org.confluence.mod.common.attachment;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.lang3.stream.Streams;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.item.hook.BaseHookItem;
import org.confluence.mod.network.s2c.ExtraInventoryStackPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terra_curio.TerraCurio;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;
import java.util.function.Predicate;

public class ExtraInventory extends ItemStackHandler implements Container {
    public static final int SIZE_VANITY_ARMOR = 4;
    public static final int SIZE_COINS = 4;
    public static final int SIZE_AMMO = 4;
    public static final int SIZE_EQUIPMENT = 4;
    public static final int SIZE_TRASH = 1;
    public static final int SIZE_DYE_EXCEPT_ACCESSORY_DYE = SIZE_VANITY_ARMOR + SIZE_EQUIPMENT;
    public static final int SIZE_EXCEPT_ACCESSORY_DYE = SIZE_VANITY_ARMOR + SIZE_COINS + SIZE_AMMO + SIZE_EQUIPMENT + SIZE_TRASH + SIZE_DYE_EXCEPT_ACCESSORY_DYE;

    public static final int COINS_START = SIZE_VANITY_ARMOR;
    public static final int AMMO_START = COINS_START + SIZE_COINS;
    public static final int EQUIPMENT_START = AMMO_START + SIZE_AMMO;
    public static final int TRASH_START = EQUIPMENT_START + SIZE_EQUIPMENT;
    public static final int DYE_START = TRASH_START + SIZE_TRASH;

    public static final StreamCodec<RegistryFriendlyByteBuf, ExtraInventory> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ExtraInventory decode(RegistryFriendlyByteBuf buffer) {
            ExtraInventory extraInventory = new ExtraInventory(false);
            int accessoryDye = buffer.readVarInt();
            extraInventory.sizeAccessoryDye = accessoryDye;
            extraInventory.initialized = true;
            int size = SIZE_EXCEPT_ACCESSORY_DYE + accessoryDye;
            extraInventory.previousStacks = NonNullList.withSize(size, ItemStack.EMPTY);
            extraInventory.dirty = false;
            List<ItemStack> list = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buffer);
            if (list instanceof NonNullList<ItemStack> nonNullList) {
                extraInventory.stacks = nonNullList;
            } else {
                extraInventory.stacks = NonNullList.copyOf(list);
            }
            return extraInventory;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ExtraInventory extraInventory) {
            buffer.writeVarInt(extraInventory.sizeAccessoryDye);
            ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buffer, extraInventory.stacks);
        }
    };

    private transient int sizeAccessoryDye = 0;
    private transient boolean initialized = false;
    private transient NonNullList<ItemStack> previousStacks;
    private transient boolean dirty = true;

    public ExtraInventory(boolean init) {
        super(SIZE_EXCEPT_ACCESSORY_DYE);
        if (init) {
            this.previousStacks = NonNullList.withSize(SIZE_EXCEPT_ACCESSORY_DYE, ItemStack.EMPTY);
        }
    }

    public int getSizeAccessoryDye() {
        return sizeAccessoryDye;
    }

    public ItemStack getVanityArmor(int index) {
        validateIndex(index, SIZE_VANITY_ARMOR);
        return getItem(index);
    }

    public List<ItemStack> getVanityArmor() {
        return stacks.subList(0, SIZE_VANITY_ARMOR);
    }

    public ItemStack getCoins(int index) {
        validateIndex(index, SIZE_COINS);
        return getItem(COINS_START + index);
    }

    public List<ItemStack> getCoins() {
        return stacks.subList(COINS_START, COINS_START + SIZE_COINS);
    }

    public ItemStack getAmmo(int index) {
        validateIndex(index, SIZE_AMMO);
        return getItem(AMMO_START + index);
    }

    public List<ItemStack> getAmmo() {
        return stacks.subList(AMMO_START, AMMO_START + SIZE_AMMO);
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

    public ItemStack getTrash() {
        return getItem(TRASH_START);
    }

    public ItemStack getVanityArmorDye(int index) {
        validateIndex(index, SIZE_VANITY_ARMOR);
        return getItem(DYE_START + index);
    }

    public List<ItemStack> getVanityArmorDye() {
        return stacks.subList(DYE_START, DYE_START + SIZE_VANITY_ARMOR);
    }

    public ItemStack getPetDye() {
        return getItem(DYE_START + SIZE_VANITY_ARMOR);
    }

    public ItemStack getLightPetDye() {
        return getItem(DYE_START + SIZE_VANITY_ARMOR + 1);
    }

    public ItemStack getMinecartDye() {
        return getItem(DYE_START + SIZE_VANITY_ARMOR + 2);
    }

    public ItemStack getHookDye() {
        return getItem(DYE_START + SIZE_VANITY_ARMOR + 3);
    }

    public ItemStack getAccessoryDye(int index) {
        validateIndex(index, sizeAccessoryDye);
        return getItem(DYE_START + SIZE_DYE_EXCEPT_ACCESSORY_DYE + index);
    }

    private static void validateIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new RuntimeException("Slot " + index + " not in valid range - [0," + size + ")");
        }
    }

    public void sync(ServerPlayer serverPlayer) {
        initialize(serverPlayer);
        if (dirty) {
            boolean dyeHard = true;
            boolean fashionStatement = true;
            boolean holdOnTight = true;

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

                if (dyeHard && i >= DYE_START && itemStack.isEmpty()) dyeHard = false;
                if (fashionStatement && i < COINS_START && itemStack.isEmpty()) fashionStatement = false;
                if (holdOnTight && i == EQUIPMENT_START + 3 && itemStack.isEmpty()) holdOnTight = false;
            }
            this.dirty = false;

            if (dyeHard) AchievementUtils.awardAchievement(serverPlayer, "dye_hard");
            if (fashionStatement && Streams.of(serverPlayer.getArmorSlots()).noneMatch(ItemStack::isEmpty)) {
                AchievementUtils.awardAchievement(serverPlayer, "fashion_statement");
            }
            if (holdOnTight) AchievementUtils.awardAchievement(serverPlayer, "hold_on_tight");
        }
    }

    public void initialize(ServerPlayer player) {
        if (!initialized) {
            updateAccessorySize(player, CuriosApi.getCuriosInventory(player).map(handler -> {
                ICurioStacksHandler accessory = handler.getCurios().get(TerraCurio.CURIO_SLOT);
                return accessory == null ? 0 : accessory.getSlots();
            }).orElse(0));
            this.initialized = true;
        }
    }

    public void updateAccessorySize(Player player, int accessoryDye) {
        setAccessoryDyes(player, accessoryDye);
        this.previousStacks = NonNullList.withSize(SIZE_EXCEPT_ACCESSORY_DYE + accessoryDye, ItemStack.EMPTY);
    }

    public void setAccessoryDyes(Player player, int size) {
        int sizeCurrent = SIZE_EXCEPT_ACCESSORY_DYE + size;
        int sizeBefore = stacks.size();
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(sizeCurrent, ItemStack.EMPTY);
        if (!player.isLocalPlayer() && sizeBefore > sizeCurrent) {
            for (ItemStack remain : stacks.subList(sizeCurrent, sizeBefore)) {
                if (!remain.isEmpty()) player.drop(remain, true);
            }
        }
        for (int i = 0; i < sizeCurrent; i++) {
            if (i >= sizeBefore) continue;
            itemStacks.set(i, stacks.get(i));
        }
        this.stacks = itemStacks;
        this.sizeAccessoryDye = size;
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
    public ItemStack getItem(int slot) {
        return getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack itemStack = getItem(slot);
        setItem(slot, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
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
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        stacks.clear();
    }

    public void copyFrom(ExtraInventory other) {
        this.sizeAccessoryDye = other.sizeAccessoryDye;
        this.initialized = other.initialized;
        this.previousStacks = other.previousStacks;
        this.dirty = other.dirty;
        this.stacks = other.stacks;
    }

    public static ItemStack getProjectile(ItemStack projectile, ItemStack weapon, LivingEntity living) {
        if (projectile.isEmpty() && weapon.getItem() instanceof ProjectileWeaponItem weaponItem && living instanceof Player player) {
            Predicate<ItemStack> predicate = weaponItem.getSupportedHeldProjectiles(weapon);
            ExtraInventory extraInventory = ExtraInventory.of(player);
            for (int i = 0; i < SIZE_AMMO; i++) {
                ItemStack ammo = extraInventory.getAmmo(i);
                if (predicate.test(ammo)) {
                    extraInventory.setChanged();
                    return ammo;
                }
            }
        }
        return projectile;
    }

    public static ExtraInventory of(LivingEntity living) {
        return living.getData(ModAttachmentTypes.EXTRA_INVENTORY);
    }
}
