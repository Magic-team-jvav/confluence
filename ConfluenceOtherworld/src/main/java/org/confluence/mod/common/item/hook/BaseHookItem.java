package org.confluence.mod.common.item.hook;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    /// 构造新钩爪时提供成功后将被淘汰的旧实体 UUID。
    ///
    /// <p>普通钩爪无需关心该提示；需要选择唯一外观的实现可据此计算“提交后的列表视图”，
    /// 但旧实体和物品 NBT 仍只能在新实体成功加入世界后真正修改。</p>
    public AbstractHookEntity getHook(
            ItemStack itemStack,
            BaseHookItem item,
            Player player,
            Level level,
            @Nullable UUID pendingEviction
    ) {
        return getHook(itemStack, item, player, level);
    }

    public HookType getHookType() {
        return type;
    }

    /// 未完成的注册物品可覆写为不可发射，避免网络入口调用占位工厂。
    public boolean isThrowAvailable() {
        return true;
    }

    public boolean canHook(ServerLevel level, Player player, ExtraInventory extraInventory, ItemStack itemStack) {
        ListTag list = sanitizeHooks(level, player, itemStack);
        LibUtils.updateItemStackNbt(itemStack, tag -> {
            tag.put("hooks", list);
            extraInventory.setChanged();
        });
        if (this instanceof IHookFastThrow) return list.size() <= getHookAmount();
        return list.isEmpty() || list.stream().allMatch(tag -> {
            AbstractHookEntity hookEntity = getHookEntity(tag, level, player);
            return hookEntity == null || hookEntity.getHookState() == AbstractHookEntity.HookState.HOOKED;
        });
    }

    public void onUnequip(Player player, ItemStack newStack, ItemStack stack) {
        // 回调已经表示原物品栈离开槽位；即使替换成同种物品，也不能让旧栈的实体继续存活。
        if (player.level() instanceof ServerLevel level
                && LibUtils.getItemStackNbtNoCopy(stack).get("hooks") instanceof ListTag list) {
            discardAllHooks(list, level, player);
        }
    }

    public static void discardAllHooks(ListTag list, ServerLevel level, Player player) {
        for (int index = 0; index < list.size(); index++) {
            AbstractHookEntity hookEntity = getHookEntity(list.get(index), level, player);
            if (hookEntity != null) hookEntity.discard();
        }
        list.clear();
    }

    /// 建立可在客户端按编号定位、在服务端按 UUID 验证的当前格式条目。
    public static CompoundTag createHookEntry(AbstractHookEntity hook) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("id", hook.getId());
        tag.putUUID("uuid", hook.getUUID());
        return tag;
    }

    @Nullable
    public static AbstractHookEntity getHookEntity(Tag tag, Level level) {
        if (!(tag instanceof CompoundTag compound)
                || !compound.contains("id", Tag.TAG_INT)
                || !compound.hasUUID("uuid")) {
            return null;
        }
        UUID uuid = compound.getUUID("uuid");
        Entity entity = level.getEntity(compound.getInt("id"));
        AbstractHookEntity hookEntity = entity instanceof AbstractHookEntity hook ? hook : null;
        if (hookEntity == null || !uuid.equals(hookEntity.getUUID())) {
            if (!(level instanceof ServerLevel serverLevel)
                    || !(serverLevel.getEntity(uuid) instanceof AbstractHookEntity resolved)) {
                return null;
            }
            hookEntity = resolved;
        }
        return hookEntity;
    }

    @Nullable
    public static AbstractHookEntity getHookEntity(Tag tag, Level level, Player player) {
        AbstractHookEntity hook = getHookEntity(tag, level);
        return hook != null && hook.getOwner() == player ? hook : null;
    }

    public static boolean hasAnyHooked(Player player) {
        ItemStack hook = ExtraInventory.of(player).getHook(false);
        if (hook.isEmpty()) return false;
        CompoundTag nbt = LibUtils.getItemStackNbtIfPresent(hook);
        if (nbt == null) return false;
        ListTag list = nbt.getList("hooks", Tag.TAG_COMPOUND);
        for (int index = 0; index < list.size(); index++) {
            AbstractHookEntity hookEntity = getHookEntity(list.get(index), player.level(), player);
            if (hookEntity != null && hookEntity.getHookState() == AbstractHookEntity.HookState.HOOKED)
                return true;
        }
        return false;
    }

    /// 判断指定实体是否仍由玩家当前装备的物品栈登记。
    ///
    /// <p>实体编号只用于同一客户端运行期内快速定位，最终仍同时核对 UUID、玩家所有权和实体实例，
    /// 防止物品被移动、替换或复制后留下无人管理的钩爪。</p>
    public static boolean containsHook(ItemStack stack, Level level, Player player, AbstractHookEntity expected) {
        CompoundTag nbt = LibUtils.getItemStackNbtIfPresent(stack);
        if (nbt == null) return false;
        ListTag list = nbt.getList("hooks", Tag.TAG_COMPOUND);
        for (int index = 0; index < list.size(); index++) {
            if (getHookEntity(list.get(index), level, player) == expected) return true;
        }
        return false;
    }

    /// 重建可信活动列表并清除损坏、重复、越权和超量条目。
    ///
    /// <p>不迁移只含旧整数编号的条目：1.20 使用全新当前格式。超量但仍有效的钩爪
    /// 会进入收回阶段，避免从物品列表移除后遗留为无人管理的活动实体。</p>
    private ListTag sanitizeHooks(ServerLevel level, Player player, ItemStack itemStack) {
        ListTag source = LibUtils.getItemStackNbt(itemStack).getList("hooks", Tag.TAG_COMPOUND);
        ListTag sanitized = new ListTag();
        Set<UUID> seen = new HashSet<>();
        int limit = amount;
        for (Tag tag : source) {
            AbstractHookEntity hook = getHookEntity(tag, level, player);
            if (hook == null || !seen.add(hook.getUUID())) continue;
            if (sanitized.size() >= limit) {
                hook.setHookState(AbstractHookEntity.HookState.POP);
                continue;
            }
            sanitized.add(createHookEntry(hook));
        }
        return sanitized;
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
