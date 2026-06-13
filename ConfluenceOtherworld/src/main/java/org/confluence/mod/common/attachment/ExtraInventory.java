package org.confluence.mod.common.attachment;

import PortLib.extensions.net.minecraft.core.HolderLookup.PortHolderLookupExtension;
import PortLib.extensions.net.minecraft.world.entity.Entity.PortEntityExtension;
import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.confluence.mod.common.item.hook.BaseHookItem;
import org.confluence.mod.network.s2c.ExtraInventoryStackPacketS2C;
import org.confluence.mod.network.s2c.VisibilityPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terra_curio.TerraCurio;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.IPortNBTSerializable;
import org.mesdag.portlib.wrapper.common.util.PortTriState;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ExtraInventory implements Container, IPortNBTSerializable<CompoundTag> {
    public static final int SIZE_VANITY_ARMOR = 4;
    public static final int SIZE_COINS = 4;
    public static final int SIZE_AMMO = 4;
    public static final int SIZE_EQUIPMENT = 5;
    public static final int SIZE_TRASH = 1;
    public static final int SIZE_EXCEPT_ACCESSORY_DYE = SIZE_VANITY_ARMOR * 2 + SIZE_COINS + SIZE_AMMO + SIZE_EQUIPMENT * 2 + SIZE_TRASH;

    public static final int VANITY_ARMOR_START = 0;
    public static final int VANITY_ARMOR_DYE_START = VANITY_ARMOR_START + SIZE_VANITY_ARMOR;
    public static final int COINS_START = VANITY_ARMOR_DYE_START + SIZE_VANITY_ARMOR;
    public static final int AMMO_START = COINS_START + SIZE_COINS;
    public static final int EQUIPMENT_START = AMMO_START + SIZE_AMMO;
    public static final int EQUIPMENT_DYE_START = EQUIPMENT_START + SIZE_EQUIPMENT;
    public static final int TRASH_START = EQUIPMENT_DYE_START + SIZE_EQUIPMENT;
    public static final int ACCESSORY_DYE_START = TRASH_START + SIZE_TRASH;

    public static final int PET_INDEX = 0;
    public static final int LIGHT_PET_INDEX = 1;
    public static final int MINECART_INDEX = 2;
    public static final int HOOK_INDEX = 3;
    public static final int MOUNT_INDEX = 4;

    public static final int VANITY_HEAD_INDEX = 0;
    public static final int VANITY_CHEST_INDEX = 1;
    public static final int VANITY_LEGS_INDEX = 2;
    public static final int VANITY_FEET_INDEX = 3;

    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, ExtraInventory> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public ExtraInventory decode(PortRegistryFriendlyByteBuf buffer) {
            ExtraInventory extraInventory = new ExtraInventory(false);
            int accessoryDye = buffer.readVarInt();
            int size = SIZE_EXCEPT_ACCESSORY_DYE + accessoryDye;
            extraInventory.initialized = true;
            extraInventory.accessoryDye = NonNullList.withSize(accessoryDye, ItemStack.EMPTY);
            extraInventory.previousStacks = NonNullList.withSize(size, ItemStack.EMPTY);
            extraInventory.dirty = false;
            List<ItemStack> list = PortItemStackExtension.optionalListStreamCodec().decode(buffer);
            for (int i = 0; i < size; i++) {
                extraInventory.setItem(i, list.get(i));
            }
            return extraInventory;
        }

        @Override
        public void encode(PortRegistryFriendlyByteBuf buffer, ExtraInventory extraInventory) {
            buffer.writeVarInt(extraInventory.getSizeAccessoryDye());
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < extraInventory.getContainerSize(); i++) {
                list.add(i, extraInventory.getItem(i));
            }
            PortItemStackExtension.optionalListStreamCodec().encode(buffer, list);
        }
    };

    private NonNullList<IStackWithDye> vanityArmor = NonNullList.withSize(SIZE_VANITY_ARMOR, StackWithDye.DEFAULT);
    private NonNullList<ItemStack> coin = NonNullList.withSize(SIZE_COINS, ItemStack.EMPTY);
    private NonNullList<ItemStack> ammo = NonNullList.withSize(SIZE_AMMO, ItemStack.EMPTY);
    private NonNullList<IStackWithDye> equipment = NonNullList.withSize(SIZE_EQUIPMENT, StackWithDye.DEFAULT);
    private ItemStack trash = ItemStack.EMPTY;
    private NonNullList<ItemStack> accessoryDye;

    private transient boolean initialized = false;
    private transient NonNullList<ItemStack> previousStacks;
    private transient boolean dirty = true;
    private transient LivingEntity living;

    public ExtraInventory(boolean init) {
        if (init) {
            this.accessoryDye = NonNullList.withSize(0, ItemStack.EMPTY);
            this.previousStacks = NonNullList.withSize(SIZE_EXCEPT_ACCESSORY_DYE, ItemStack.EMPTY);
        }
    }

    public int getSizeAccessoryDye() {
        return accessoryDye.size();
    }

    public ItemStack getVanityArmor(int index, boolean dye) {
        validateIndex(index, SIZE_VANITY_ARMOR);
        IStackWithDye stack = vanityArmor.get(index);
        return dye ? stack.getDye() : stack.getStack();
    }

    public void setVanityArmor(int index, ItemStack stack, boolean dye) {
        validateIndex(index, SIZE_VANITY_ARMOR);
        if (dye) vanityArmor.set(index, vanityArmor.get(index).setDye(stack));
        else vanityArmor.set(index, vanityArmor.get(index).setStack(stack));
        setChanged();
    }

    public ItemStack getCoins(int index) {
        validateIndex(index, SIZE_COINS);
        return coin.get(index);
    }

    public List<ItemStack> getAllCoins() {
        return coin;
    }

    public void setCoins(int index, ItemStack stack) {
        validateIndex(index, SIZE_COINS);
        coin.set(index, stack);
        setChanged();
    }

    public ItemStack getAmmo(int index) {
        validateIndex(index, SIZE_AMMO);
        return ammo.get(index);
    }

    public List<ItemStack> getAllAmmo() {
        return ammo;
    }

    public void setAmmo(int index, ItemStack stack) {
        validateIndex(index, SIZE_AMMO);
        ammo.set(index, stack);
        setChanged();
    }

    public ItemStack getEquipment(int index, boolean dye) {
        validateIndex(index, SIZE_EQUIPMENT);
        IStackWithDye stack = equipment.get(index);
        return dye ? stack.getDye() : stack.getStack();
    }

    public ItemStack getPet(boolean dye) {
        return getEquipment(PET_INDEX, dye);
    }

    public ItemStack getLightPet(boolean dye) {
        return getEquipment(LIGHT_PET_INDEX, dye);
    }

    public ItemStack getMinecart(boolean dye) {
        return getEquipment(MINECART_INDEX, dye);
    }

    public ItemStack getHook(boolean dye) {
        return getEquipment(HOOK_INDEX, dye);
    }

    public void setEquipment(int index, ItemStack stack, boolean dye) {
        validateIndex(index, SIZE_EQUIPMENT);
        if (dye) equipment.set(index, equipment.get(index).setDye(stack));
        else equipment.set(index, equipment.get(index).setStack(stack));
        setChanged();
    }

    public ItemStack getTrash() {
        return trash;
    }

    public void setTrash(ItemStack stack) {
        this.trash = stack;
        setChanged();
    }

    public ItemStack getAccessoryDye(int index) {
        validateIndex(index, accessoryDye.size());
        return accessoryDye.get(index);
    }

    public void setAccessoryDye(int index, ItemStack stack) {
        validateIndex(index, accessoryDye.size());
        accessoryDye.set(index, stack);
        setChanged();
    }

    private static void validateIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new RuntimeException("Slot " + index + " not in valid range - [0," + size + ")");
        }
    }

    public void sync(ServerPlayer player) {
        initialize(player);
        if (!dirty) return;
        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack itemStack = getItem(i);
            ItemStack previous = previousStacks.get(i);
            if (!ItemStack.matches(itemStack, previous)) {
                ExtraInventoryStackPacketS2C.sendToPlayersTrackingEntityAndSelf(player, player, accessoryDye.size(), i, itemStack);
                if (previous.getItem() instanceof BaseHookItem hookItem) {
                    hookItem.onUnequip(player, itemStack, previous);
                }
                if (i - VANITY_ARMOR_START == VANITY_HEAD_INDEX) {
                    VisibilityPacketS2C.sendSunglasses(player, PortTriState.DEFAULT, itemStack.is(VanityArmorItems.SUNGLASSES.get())
                            ? PortTriState.TRUE : PortTriState.FALSE);
                }
                previousStacks.set(i, itemStack.copy());
            }
        }
        this.dirty = false;

        checkAchievements(player);
    }

    private void checkAchievements(ServerPlayer player) {
        if (vanityArmor.stream().noneMatch(IStackWithDye::isDyeEmpty) &&
                equipment.stream().noneMatch(IStackWithDye::isDyeEmpty) &&
                accessoryDye.stream().noneMatch(ItemStack::isEmpty)
        ) AchievementUtils.awardAchievement(player, "dye_hard");

        if (vanityArmor.stream().noneMatch(IStackWithDye::isStackEmpty)) {
            AchievementUtils.awardAchievement(player, "fashion_statement");
        }

        if (!getHook(false).isEmpty()) {
            AchievementUtils.awardAchievement(player, "hold_on_tight");
        }
    }

    // 需要晚于Curios Api
    public void initialize(ServerPlayer player) {
        if (initialized) return;
        updateAccessorySize(player, CuriosApi.getCuriosInventory(player).resolve().map(handler -> {
            ICurioStacksHandler accessory = handler.getCurios().get(TerraCurio.CURIO_SLOT);
            return accessory == null ? 0 : accessory.getSlots();
        }).orElse(0));
        this.initialized = true;
    }

    public void updateAccessorySize(Player player, int accessoryDye) {
        setAccessoryDyes(player, accessoryDye);
        this.previousStacks = NonNullList.withSize(SIZE_EXCEPT_ACCESSORY_DYE + accessoryDye, ItemStack.EMPTY);
    }

    public void setAccessoryDyes(Player player, int sizeCurrent) {
        int sizeBefore = accessoryDye.size();
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(sizeCurrent, ItemStack.EMPTY);
        if (!player.isLocalPlayer() && sizeBefore > sizeCurrent) {
            for (ItemStack remain : accessoryDye.subList(sizeCurrent, sizeBefore)) {
                if (!remain.isEmpty()) player.drop(remain, true);
            }
        }
        for (int i = 0; i < sizeCurrent; i++) {
            if (i >= sizeBefore) continue;
            itemStacks.set(i, accessoryDye.get(i));
        }
        this.accessoryDye = itemStacks;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        RegistryOps<Tag> ops = PortHolderLookupExtension.Provider.createSerializationContext(provider, NbtOps.INSTANCE);
        CompoundTag tag = new CompoundTag();
        ListTag t = new ListTag();
        for (IStackWithDye stack : vanityArmor) t.add(stack.encode(ops));
        tag.put("VanityArmor", t);
        tag.put("Coin", encodeList(coin, ops));
        tag.put("Ammo", encodeList(ammo, ops));
        t = new ListTag();
        for (IStackWithDye stack : equipment) t.add(stack.encode(ops));
        tag.put("Equipment", t);
        tag.put("Trash", encode(trash, ops));
        tag.put("AccessoryDye", encodeList(accessoryDye, ops));
        tag.putBoolean("confluence:fixed", true);
        return tag;
    }

    private static Tag encodeList(NonNullList<ItemStack> list, DynamicOps<Tag> ops) {
        ListTag tag = new ListTag();
        for (ItemStack stack : list) tag.add(encode(stack, ops));
        return tag;
    }

    private static Tag encode(ItemStack stack, DynamicOps<Tag> ops) {
        return PortItemStackExtension.optionalCodec().encodeStart(ops, stack).result().orElseGet(CompoundTag::new);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        if (nbt.getBoolean("confluence:fixed")) {
            RegistryOps<Tag> ops = PortHolderLookupExtension.Provider.createSerializationContext(provider, NbtOps.INSTANCE);
            ListTag t = nbt.getList("VanityArmor", Tag.TAG_COMPOUND);
            for (int i = 0; i < vanityArmor.size(); i++) {
                vanityArmor.set(i, StackWithDye.DEFAULT.decode(t.getCompound(i), ops));
            }
            decodeList(coin, nbt.getList("Coin", Tag.TAG_COMPOUND), ops);
            decodeList(ammo, nbt.getList("Ammo", Tag.TAG_COMPOUND), ops);
            t = nbt.getList("Equipment", Tag.TAG_COMPOUND);
            for (int i = 0; i < equipment.size(); i++) {
                equipment.set(i, StackWithDye.DEFAULT.decode(t.getCompound(i), ops));
            }
            this.trash = decode(nbt.getCompound("Trash"), ops);
            t = nbt.getList("AccessoryDye", Tag.TAG_COMPOUND);
            encodeList(this.accessoryDye = NonNullList.withSize(t.size(), ItemStack.EMPTY), ops);
        }
    }

    private static void decodeList(NonNullList<ItemStack> list, ListTag tag, DynamicOps<Tag> ops) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, decode(tag.getCompound(i), ops));
        }
    }

    private static ItemStack decode(CompoundTag tag, DynamicOps<Tag> ops) {
        return PortItemStackExtension.optionalCodec().parse(ops, tag).result().orElse(ItemStack.EMPTY);
    }

    public void copyFrom(ExtraInventory other) {
        this.vanityArmor = other.vanityArmor;
        this.coin = other.coin;
        this.ammo = other.ammo;
        this.equipment = other.equipment;
        this.trash = other.trash;
        this.accessoryDye = other.accessoryDye;

        this.initialized = other.initialized;
        this.previousStacks = other.previousStacks;
        this.dirty = other.dirty;
    }

    public void setDirty() {
        this.dirty = true;
    }

    @Override
    public int getContainerSize() {
        return SIZE_EXCEPT_ACCESSORY_DYE + accessoryDye.size();
    }

    @Override
    public ItemStack getItem(int index) {
        if (index < VANITY_ARMOR_DYE_START) {
            return getVanityArmor(index - VANITY_ARMOR_START, false);
        } else if (index < COINS_START) {
            return getVanityArmor(index - VANITY_ARMOR_DYE_START, true);
        } else if (index < AMMO_START) {
            return getCoins(index - COINS_START);
        } else if (index < EQUIPMENT_START) {
            return getAmmo(index - AMMO_START);
        } else if (index < EQUIPMENT_DYE_START) {
            return getEquipment(index - EQUIPMENT_START, false);
        } else if (index < TRASH_START) {
            return getEquipment(index - EQUIPMENT_DYE_START, true);
        } else if (index < ACCESSORY_DYE_START) {
            return getTrash();
        } else if (index < getContainerSize()) {
            return getAccessoryDye(index - ACCESSORY_DYE_START);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = removeItemNoUpdate(slot);
        setDirty();
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        setItem(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index < VANITY_ARMOR_DYE_START) {
            setVanityArmor(index - VANITY_ARMOR_START, stack, false);
        } else if (index < COINS_START) {
            setVanityArmor(index - VANITY_ARMOR_DYE_START, stack, true);
        } else if (index < AMMO_START) {
            setCoins(index - COINS_START, stack);
        } else if (index < EQUIPMENT_START) {
            setAmmo(index - AMMO_START, stack);
        } else if (index < EQUIPMENT_DYE_START) {
            setEquipment(index - EQUIPMENT_START, stack, false);
        } else if (index < TRASH_START) {
            setEquipment(index - EQUIPMENT_DYE_START, stack, true);
        } else if (index < ACCESSORY_DYE_START) {
            setTrash(stack);
        } else if (index < getContainerSize()) {
            setAccessoryDye(index - ACCESSORY_DYE_START, stack);
        }
    }

    @Override
    public boolean isEmpty() {
        for (IStackWithDye stack : vanityArmor) {
            if (!stack.isEmpty()) return false;
        }
        for (ItemStack stack : coin) {
            if (!stack.isEmpty()) return false;
        }
        for (ItemStack stack : ammo) {
            if (!stack.isEmpty()) return false;
        }
        for (IStackWithDye stack : equipment) {
            if (!stack.isEmpty()) return false;
        }
        for (ItemStack stack : accessoryDye) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public void setChanged() {
        setDirty();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        vanityArmor.clear();
        coin.clear();
        ammo.clear();
        equipment.clear();
        this.trash = ItemStack.EMPTY;
        accessoryDye.clear();
    }

    public static ItemStack getProjectile(ItemStack projectile, ItemStack weapon, LivingEntity living) {
        if (projectile.isEmpty() && weapon.getItem() instanceof ProjectileWeaponItem weaponItem && living instanceof Player player) {
            Predicate<ItemStack> predicate = weaponItem.getSupportedHeldProjectiles();
            ExtraInventory extraInventory = of(player);
            for (int i = 0; i < SIZE_AMMO; i++) {
                ItemStack ammo = extraInventory.getAmmo(i);
                if (!predicate.test(ammo)) continue;
                extraInventory.setChanged();
                return ammo;
            }
        }
        return projectile;
    }

    public static ExtraInventory of(LivingEntity living) {
        ExtraInventory extraInventory = PortEntityExtension.getAttach(living, ModAttachmentTypes.EXTRA_INVENTORY);
        extraInventory.living = living;
        return extraInventory;
    }

    public interface IStackWithDye {
        IStackWithDye setStack(ItemStack stack);

        ItemStack getStack();

        IStackWithDye setDye(ItemStack dye);

        ItemStack getDye();

        default boolean isStackEmpty() {
            return getStack().isEmpty();
        }

        default boolean isDyeEmpty() {
            return getDye().isEmpty();
        }

        default boolean isEmpty() {
            return isStackEmpty() && isDyeEmpty();
        }

        default Tag encode(DynamicOps<Tag> ops) {
            CompoundTag tag = new CompoundTag();
            tag.put("Stack", ExtraInventory.encode(getStack(), ops));
            tag.put("Dye", ExtraInventory.encode(getDye(), ops));
            return tag;
        }

        default IStackWithDye decode(CompoundTag tag, DynamicOps<Tag> ops) {
            return setStack(ExtraInventory.decode(tag.getCompound("Stack"), ops))
                    .setDye(ExtraInventory.decode(tag.getCompound("Dye"), ops));
        }
    }

    public record StackWithDye(ItemStack stack, ItemStack dye) implements IStackWithDye {
        public static final StackWithDye DEFAULT = new StackWithDye(ItemStack.EMPTY, ItemStack.EMPTY);

        public IStackWithDye setStack(ItemStack stack) {
            return new StackWithDye(stack, dye);
        }

        public ItemStack getStack() {
            return stack;
        }

        public IStackWithDye setDye(ItemStack dye) {
            return new StackWithDye(stack, dye);
        }

        public ItemStack getDye() {
            return dye;
        }
    }

    public class CurioStackWithDye implements IStackWithDye {
        private final int slot;
        private final ICurioStacksHandler handler;
        private ItemStack dye;

        public CurioStackWithDye(int slot, ICurioStacksHandler handler, IStackWithDye original) {
            this.slot = slot;
            this.handler = handler;
            setStack(original.getStack());
            setDye(original.getDye());
        }

        public IStackWithDye setStack(ItemStack stack) {
            ItemStack from = getStack();
            handler.getStacks().setStackInSlot(slot, stack);
            if (living != null) {
                PortEventHandler.postEvent(new CurioChangeEvent(living, handler.getIdentifier(), slot, from, stack));
            }
            return this;
        }

        public ItemStack getStack() {
            return handler.getStacks().getStackInSlot(slot);
        }

        public IStackWithDye setDye(ItemStack dye) {
            this.dye = dye;
            return this;
        }

        public ItemStack getDye() {
            return dye;
        }
    }
}
