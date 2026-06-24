package org.confluence.mod.common.item.hook;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.jetbrains.annotations.Nullable;

public class BaseHookItem extends Item {
    protected final int amount;
    protected final float range;
    protected final float velocity;
    protected final HookType type;
    protected final HookEntityFactory factory;

    public BaseHookItem(Properties properties, ModRarity rarity, int amount, float range, float velocity, HookType type, HookEntityFactory factory) {
        super(properties.component(ConfluenceMagicLib.MOD_RARITY, rarity).stacksTo(1));
        this.amount = amount;
        this.range = range;
        this.velocity = velocity;
        this.type = type;
        this.factory = factory;
    }

    public BaseHookItem(ModRarity rarity, int amount, float range, float velocity, HookType type, HookEntityFactory factory) {
        this(new Properties(), rarity, amount, range, velocity, type, factory);
    }

    public int getHookAmount() {
        return amount;
    }

    public float getHookRange() {
        return range;
    }

    public float getHookVelocity() {
        return velocity;
    }

    public AbstractHookEntity getHook(ItemStack itemStack, BaseHookItem item, Player player, Level level) {
        return factory.create(itemStack, item, player, level);
    }

    public HookType getHookType() {
        return type;
    }

    public boolean canHook(ServerLevel level, ExtraInventory extraInventory, ItemStack itemStack) {
        ListTag list = LibUtils.getItemStackNbt(itemStack).getList("hooks", Tag.TAG_COMPOUND);
        if (!list.isEmpty()) list.removeIf(tag -> getHookEntity(tag, level) == null);
        LibUtils.updateItemStackNbt(itemStack, tag -> {
            tag.put("hooks", list);
            extraInventory.setChanged();
        });
        if (this instanceof IHookFastThrow) return list.size() <= getHookAmount();
        return list.isEmpty() || list.stream().allMatch(tag -> {
            AbstractHookEntity hookEntity = getHookEntity(tag, level);
            return hookEntity == null || hookEntity.getHookState() == AbstractHookEntity.HookState.HOOKED;
        });
    }

    public void onUnequip(ServerPlayer serverPlayer, ItemStack newStack, ItemStack stack) {
        if (!ItemStack.isSameItem(newStack, stack) && LibUtils.getItemStackNbtNoCopy(stack).get("hooks") instanceof ListTag list) {
            discardAllHooks(list, serverPlayer.serverLevel());
        }
    }

    public static void discardAllHooks(ListTag list, ServerLevel level) {
        if (!list.isEmpty()) list.removeIf(tag -> {
            AbstractHookEntity hookEntity = getHookEntity(tag, level);
            if (hookEntity != null) hookEntity.discard();
            return true;
        });
    }

    @Nullable
    public static AbstractHookEntity getHookEntity(Tag tag, Level level) {
        return level.getEntity(((CompoundTag) tag).getInt("id")) instanceof AbstractHookEntity hookEntity ? hookEntity : null;
    }

    public static boolean hasAnyHooked(Player player) {
        ItemStack hook = ExtraInventory.of(player).getHook(false);
        if (hook.isEmpty()) return false;
        CompoundTag nbt = LibUtils.getItemStackNbtIfPresent(hook);
        if (nbt == null) return false;
        ListTag list = nbt.getList("hooks", Tag.TAG_COMPOUND);
        return !list.isEmpty() && list.stream().anyMatch(tag -> {
            AbstractHookEntity hookEntity = getHookEntity(tag, player.level());
            return hookEntity != null && hookEntity.getHookState() == AbstractHookEntity.HookState.HOOKED;
        });
    }

    public enum HookType {
        SINGLE, // 只有一个钩爪
        SIMULTANEOUS, // 有多个钩爪,且可以同时保持
        INDIVIDUAL // 有多个钩爪,但只能保持其一
    }

    @FunctionalInterface
    public interface HookEntityFactory {
        AbstractHookEntity create(ItemStack itemStack, BaseHookItem item, Player player, Level level);
    }
}
