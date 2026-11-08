package org.confluence.mod.common.menu;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.terra_curio.TerraCurio;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.List;

/// 泰拉 1.4.4 的“装备栏位”（Loadouts）：三套装备，每套包括盔甲、时装盔甲、盔甲染料、饰品、时装饰品和饰品染料。
/// 切换时把身上的这些存进当前套，再把目标套穿上（存着的那份同时清掉，物品不会复制）。
/// 宠物/钩爪/坐骑等装备栏、背包不受影响。数据在 PlayerPersisted 里，死亡重生后保留（与泰拉一致，栏位里的东西不掉落）。
public final class TerraLoadouts {
    public static final int COUNT = 3;
    private static final String KEY = "confluence_loadouts";
    private static final EquipmentSlot[] ARMOR = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    /// 客户端：当前是第几套（服务端登录和切换时同步）
    public static int clientIndex = 0;

    private TerraLoadouts() {}

    private static CompoundTag root(Player player) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
        CompoundTag root = persisted.getCompound(KEY);
        persisted.put(KEY, root);
        return root;
    }

    public static int current(Player player) {
        return root(player).getInt("current");
    }

    public static void switchTo(ServerPlayer player, int target) {
        if (target < 0 || target >= COUNT) return;
        CompoundTag root = root(player);
        int current = root.getInt("current");
        if (target == current) return;
        HolderLookup.Provider registries = player.registryAccess();
        root.put("set" + current, capture(player, registries));
        apply(player, root.getCompound("set" + target), registries);
        root.remove("set" + target);
        root.putInt("current", target);
        player.inventoryMenu.broadcastChanges();
        player.containerMenu.broadcastChanges();
        org.confluence.mod.network.s2c.LoadoutIndexPacketS2C.send(player, target);
    }

    // ---------- 存 ----------
    private static CompoundTag capture(ServerPlayer player, HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        List<ItemStack> armor = new ArrayList<>();
        for (EquipmentSlot slot : ARMOR) armor.add(player.getItemBySlot(slot));
        tag.put("armor", save(armor, registries));
        ExtraInventory extra = ExtraInventory.of(player);
        tag.put("vanity", save(range(extra, ExtraInventory.VANITY_ARMOR_START, ExtraInventory.SIZE_VANITY_ARMOR), registries));
        tag.put("armorDyes", save(range(extra, ExtraInventory.VANITY_ARMOR_DYE_START, ExtraInventory.SIZE_VANITY_ARMOR), registries));
        tag.put("accessoryDyes", save(range(extra, ExtraInventory.ACCESSORY_DYE_START, extra.getSizeAccessoryDye()), registries));
        ICurioStacksHandler accessories = accessories(player);
        if (accessories != null) {
            tag.put("accessories", save(stacks(accessories.getStacks()), registries));
            if (accessories.hasCosmetic()) tag.put("cosmetics", save(stacks(accessories.getCosmeticStacks()), registries));
        }
        return tag;
    }

    private static List<ItemStack> range(ExtraInventory extra, int start, int size) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < size; i++) list.add(extra.getItem(start + i));
        return list;
    }

    private static List<ItemStack> stacks(IDynamicStackHandler handler) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) list.add(handler.getStackInSlot(i));
        return list;
    }

    private static ListTag save(List<ItemStack> stacks, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (ItemStack stack : stacks) list.add(stack.saveOptional(registries));
        return list;
    }

    // ---------- 穿 ----------
    private static void apply(ServerPlayer player, CompoundTag tag, HolderLookup.Provider registries) {
        List<ItemStack> leftovers = new ArrayList<>();
        List<ItemStack> armor = load(tag, "armor", registries);
        for (int i = 0; i < ARMOR.length; i++) player.setItemSlot(ARMOR[i], get(armor, i));
        ExtraInventory extra = ExtraInventory.of(player);
        setRange(extra, ExtraInventory.VANITY_ARMOR_START, ExtraInventory.SIZE_VANITY_ARMOR, load(tag, "vanity", registries), leftovers);
        setRange(extra, ExtraInventory.VANITY_ARMOR_DYE_START, ExtraInventory.SIZE_VANITY_ARMOR, load(tag, "armorDyes", registries), leftovers);
        setRange(extra, ExtraInventory.ACCESSORY_DYE_START, extra.getSizeAccessoryDye(), load(tag, "accessoryDyes", registries), leftovers);
        extra.setDirty();
        ICurioStacksHandler accessories = accessories(player);
        if (accessories != null) {
            setHandler(accessories.getStacks(), load(tag, "accessories", registries), leftovers);
            if (accessories.hasCosmetic()) setHandler(accessories.getCosmeticStacks(), load(tag, "cosmetics", registries), leftovers);
        } else {
            leftovers.addAll(load(tag, "accessories", registries));
            leftovers.addAll(load(tag, "cosmetics", registries));
        }
        // 套里存的比现在的格子多（例如恶魔之心的额外饰品槽没了）：放回背包，放不下就掉在脚下
        for (ItemStack stack : leftovers) {
            if (!stack.isEmpty()) player.getInventory().placeItemBackInInventory(stack);
        }
    }

    private static void setRange(ExtraInventory extra, int start, int size, List<ItemStack> stacks, List<ItemStack> leftovers) {
        for (int i = 0; i < size; i++) extra.setItem(start + i, get(stacks, i));
        for (int i = size; i < stacks.size(); i++) leftovers.add(stacks.get(i));
    }

    private static void setHandler(IDynamicStackHandler handler, List<ItemStack> stacks, List<ItemStack> leftovers) {
        for (int i = 0; i < handler.getSlots(); i++) handler.setStackInSlot(i, get(stacks, i));
        for (int i = handler.getSlots(); i < stacks.size(); i++) leftovers.add(stacks.get(i));
    }

    private static ItemStack get(List<ItemStack> stacks, int index) {
        return index < stacks.size() ? stacks.get(index) : ItemStack.EMPTY;
    }

    private static List<ItemStack> load(CompoundTag tag, String key, HolderLookup.Provider registries) {
        List<ItemStack> list = new ArrayList<>();
        ListTag items = tag.getList(key, Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) list.add(ItemStack.parseOptional(registries, items.getCompound(i)));
        return list;
    }

    private static ICurioStacksHandler accessories(Player player) {
        return CuriosApi.getCuriosInventory(player).map(handler -> handler.getCurios().get(TerraCurio.CURIO_SLOT)).orElse(null);
    }
}
