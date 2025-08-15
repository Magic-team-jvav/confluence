package org.confluence.mod.common.attachment;

import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.mod.common.item.hook.BaseHookItem;
import org.confluence.mod.network.s2c.ExtraInventoryStackPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terra_curio.TerraCurio;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ReExtraInventory implements INBTSerializable<CompoundTag> {
    public static final int SIZE_VANITY_ARMOR = 4;
    public static final int SIZE_COINS = 4;
    public static final int SIZE_AMMO = 4;
    public static final int SIZE_EQUIPMENT = 5;
    public static final int SIZE_TRASH = 1;
    public static final int SIZE_DYE_EXCEPT_ACCESSORY_DYE = SIZE_VANITY_ARMOR + SIZE_EQUIPMENT;
    public static final int SIZE_EXCEPT_ACCESSORY_DYE = SIZE_VANITY_ARMOR + SIZE_COINS + SIZE_AMMO + SIZE_EQUIPMENT + SIZE_TRASH + SIZE_DYE_EXCEPT_ACCESSORY_DYE;

    public static final int PET_INDEX = 0;
    public static final int LIGHT_PET_INDEX = 1;
    public static final int MINECART_INDEX = 2;
    public static final int HOOK_INDEX = 3;
    public static final int MOUNT_INDEX = 4;

    public static final StreamCodec<RegistryFriendlyByteBuf, ReExtraInventory> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ReExtraInventory decode(RegistryFriendlyByteBuf buffer) {
            ReExtraInventory extraInventory = new ReExtraInventory(false);
            int accessoryDye = buffer.readVarInt();
            int size = SIZE_EXCEPT_ACCESSORY_DYE + accessoryDye;
            extraInventory.initialized = true;
            extraInventory.accessoryDye = NonNullList.withSize(accessoryDye, ItemStack.EMPTY);
            extraInventory.previousStacks = NonNullList.withSize(size, ItemStack.EMPTY);
            extraInventory.dirty = false;
            List<ItemStack> list = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buffer);
            for (int i = 0; i < size; i++) {
                extraInventory.setItem(i, list.get(i));
            }
            return extraInventory;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ReExtraInventory extraInventory) {
            buffer.writeVarInt(extraInventory.getSizeAccessoryDye());
            int size = extraInventory.size();
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                list.add(i, extraInventory.getItem(i));
            }
            ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buffer, list);
        }
    };

    private final NonNullList<IStackWithDye> vanityArmor = NonNullList.withSize(SIZE_VANITY_ARMOR, StackWithDye.DEFAULT);
    private final NonNullList<ItemStack> coin = NonNullList.withSize(SIZE_COINS, ItemStack.EMPTY);
    private final NonNullList<ItemStack> ammo = NonNullList.withSize(SIZE_AMMO, ItemStack.EMPTY);
    private final NonNullList<IStackWithDye> equipment = NonNullList.withSize(SIZE_EQUIPMENT, StackWithDye.DEFAULT);
    private ItemStack trash = ItemStack.EMPTY;
    private NonNullList<ItemStack> accessoryDye;

    private transient boolean initialized = false;
    private transient NonNullList<ItemStack> previousStacks;
    private transient boolean dirty = true;

    public ReExtraInventory(boolean init) {
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
        setDirty();
    }

    public ItemStack getCoins(int index) {
        validateIndex(index, SIZE_COINS);
        return coin.get(index);
    }

    public void setCoins(int index, ItemStack stack) {
        validateIndex(index, SIZE_COINS);
        coin.set(index, stack);
        setDirty();
    }

    public ItemStack getAmmo(int index) {
        validateIndex(index, SIZE_AMMO);
        return ammo.get(index);
    }

    public void setAmmo(int index, ItemStack stack) {
        validateIndex(index, SIZE_AMMO);
        ammo.set(index, stack);
        setDirty();
    }

    public ItemStack getEquipment(int index, boolean dye) {
        validateIndex(index, SIZE_EQUIPMENT);
        IStackWithDye stack = equipment.get(index);
        return dye ? stack.getDye() : stack.getStack();
    }

    public void setEquipment(int index, ItemStack stack, boolean dye) {
        validateIndex(index, SIZE_EQUIPMENT);
        if (dye) equipment.set(index, equipment.get(index).setDye(stack));
        else equipment.set(index, equipment.get(index).setStack(stack));
        setDirty();
    }

    public ItemStack getTrash() {
        return trash;
    }

    public void setTrash(ItemStack stack) {
        this.trash = stack;
        setDirty();
    }

    public ItemStack getAccessoryDye(int index) {
        validateIndex(index, accessoryDye.size());
        return accessoryDye.get(index);
    }

    public void setAccessoryDye(int index, ItemStack stack) {
        validateIndex(index, accessoryDye.size());
        accessoryDye.set(index, stack);
        setDirty();
    }

    private static void validateIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new RuntimeException("Slot " + index + " not in valid range - [0," + size + ")");
        }
    }

    public void sync(ServerPlayer player) {
        initialize(player);
        if (dirty) {
            for (int i = 0; i < size(); i++) {
                ItemStack itemStack = getItem(i);
                ItemStack previous = previousStacks.get(i);
                if (!ItemStack.matches(itemStack, previous)) {
                    ExtraInventoryStackPacketS2C.sendToPlayersTrackingEntityAndSelf(player, player, accessoryDye.size(), i, itemStack);
                    if (previous.getItem() instanceof BaseHookItem hookItem) {
                        hookItem.onUnequip(player, itemStack, previous);
                    }
                    previousStacks.set(i, itemStack.copy());
                }
            }
            this.dirty = false;

            checkAchievements(player);
        }
    }

    private void checkAchievements(ServerPlayer player) {
        if (vanityArmor.stream().noneMatch(IStackWithDye::isDyeEmpty) &&
                equipment.stream().noneMatch(IStackWithDye::isDyeEmpty) &&
                accessoryDye.stream().noneMatch(ItemStack::isEmpty)
        ) AchievementUtils.awardAchievement(player, "dye_hard");

        if (vanityArmor.stream().noneMatch(IStackWithDye::isStackEmpty)) {
            AchievementUtils.awardAchievement(player, "fashion_statement");
        }

        if (!getEquipment(HOOK_INDEX, false).isEmpty()) {
            AchievementUtils.awardAchievement(player, "hold_on_tight");
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
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
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
        return ItemStack.OPTIONAL_CODEC.encodeStart(ops, stack).result().orElseGet(CompoundTag::new);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        if (nbt.getBoolean("confluence:fixed")) {
            RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
            ListTag t = nbt.getList("VanityArmor", Tag.TAG_COMPOUND);
            for (int i = 0; i < vanityArmor.size(); i++) vanityArmor.set(i, StackWithDye.DEFAULT.decode(t.getCompound(i), ops));
            decodeList(coin, nbt.getList("Coin", Tag.TAG_COMPOUND), ops);
            decodeList(ammo, nbt.getList("Ammo", Tag.TAG_COMPOUND), ops);
            t = nbt.getList("Equipment", Tag.TAG_COMPOUND);
            for (int i = 0; i < equipment.size(); i++) equipment.set(i, StackWithDye.DEFAULT.decode(t.getCompound(i), ops));
            this.trash = decode(nbt.getCompound("Trash"), ops);
            t = nbt.getList("AccessoryDye", Tag.TAG_COMPOUND);
            encodeList(this.accessoryDye = NonNullList.withSize(t.size(), ItemStack.EMPTY), ops);
        } else { // todo 1.3.0时删除
            int size = nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : size();
            ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < tagList.size(); i++) {
                CompoundTag itemTags = tagList.getCompound(i);
                int slot = itemTags.getInt("Slot");
                if (slot >= 0 && slot < size) {
                    ItemStack.parse(provider, itemTags).ifPresent(stack -> {
                        if (slot < 4) setVanityArmor(slot, stack, false);
                        else if (slot < 8) setCoins(slot - 4, stack);
                        else if (slot < 12) setAmmo(slot - 8, stack);
                        else if (slot == 12) setEquipment(PET_INDEX, stack, false);
                        else if (slot == 13) setEquipment(LIGHT_PET_INDEX, stack, false);
                        else if (slot == 14) setEquipment(MINECART_INDEX, stack, false);
                        else if (slot == 15) setEquipment(HOOK_INDEX, stack, false);
                        else if (slot == 16) setTrash(stack);
                        else if (slot < 21) setVanityArmor(slot - 17, stack, true);
                        else if (slot == 21) setEquipment(PET_INDEX, stack, true);
                        else if (slot == 22) setEquipment(LIGHT_PET_INDEX, stack, true);
                        else if (slot == 23) setEquipment(MINECART_INDEX, stack, true);
                        else if (slot == 24) setEquipment(HOOK_INDEX, stack, true);
                        else if (slot < 25 + accessoryDye.size()) setAccessoryDye(slot - 25, stack);
                    });
                }
            }
        }
    }

    private static void decodeList(NonNullList<ItemStack> list, ListTag tag, DynamicOps<Tag> ops) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, decode(tag.getCompound(i), ops));
        }
    }

    private static ItemStack decode(CompoundTag tag, DynamicOps<Tag> ops) {
        return ItemStack.OPTIONAL_CODEC.parse(ops, tag).result().orElse(ItemStack.EMPTY);
    }

    public int size() {
        return SIZE_EXCEPT_ACCESSORY_DYE + accessoryDye.size();
    }

    public ItemStack getItem(int index) {
        if (index < SIZE_VANITY_ARMOR) return vanityArmor.get(index).getStack();
        index -= SIZE_VANITY_ARMOR;
        if (index < SIZE_VANITY_ARMOR) return vanityArmor.get(index).getDye();
        index -= SIZE_VANITY_ARMOR;
        if (index < SIZE_COINS) return coin.get(index);
        index -= SIZE_COINS;
        if (index < SIZE_AMMO) return ammo.get(index);
        index -= SIZE_AMMO;
        if (index < SIZE_EQUIPMENT) return equipment.get(index).getStack();
        index -= SIZE_EQUIPMENT;
        if (index < SIZE_EQUIPMENT) return equipment.get(index).getDye();
        index -= SIZE_EQUIPMENT;
        if (index < SIZE_TRASH) return trash;
        index -= SIZE_TRASH;
        if (index < accessoryDye.size()) return accessoryDye.get(index);
        return ItemStack.EMPTY;
    }

    public void setItem(int index, ItemStack stack) {
        if (index < SIZE_VANITY_ARMOR) setVanityArmor(index, stack, false);
        index -= SIZE_VANITY_ARMOR;
        if (index < SIZE_VANITY_ARMOR) setVanityArmor(index, stack, true);
        index -= SIZE_VANITY_ARMOR;
        if (index < SIZE_COINS) coin.set(index, stack);
        index -= SIZE_COINS;
        if (index < SIZE_AMMO) ammo.set(index, stack);
        index -= SIZE_AMMO;
        if (index < SIZE_EQUIPMENT) equipment.set(index, equipment.get(index).setStack(stack));
        index -= SIZE_EQUIPMENT;
        if (index < SIZE_EQUIPMENT) equipment.set(index, equipment.get(index).setDye(stack));
        index -= SIZE_EQUIPMENT;
        if (index < SIZE_TRASH) this.trash = stack;
        index -= SIZE_TRASH;
        if (index < accessoryDye.size()) accessoryDye.set(index, stack);
    }

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

    public void setDirty() {
        this.dirty = true;
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
            tag.put("Stack", ReExtraInventory.encode(getStack(), ops));
            tag.put("Dye", ReExtraInventory.encode(getDye(), ops));
            return tag;
        }

        default IStackWithDye decode(CompoundTag tag, DynamicOps<Tag> ops) {
            return setStack(ReExtraInventory.decode(tag.getCompound("Stack"), ops))
                    .setDye(ReExtraInventory.decode(tag.getCompound("Dye"), ops));
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

    public static class WrappedStackWithDye implements IStackWithDye {
        private final Consumer<ItemStack> setter;
        private final Supplier<ItemStack> getter;
        private ItemStack dye;

        public WrappedStackWithDye(Consumer<ItemStack> setter, Supplier<ItemStack> getter) {
            this.setter = setter;
            this.getter = getter;
        }

        public IStackWithDye setStack(ItemStack stack) {
            setter.accept(stack);
            return this;
        }

        public ItemStack getStack() {
            return getter.get();
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
