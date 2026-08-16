package org.confluence.mod.common.item.hook;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.entity.hook.LunarHookEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class LunarHookItem extends BaseHookItem implements IHookFastThrow {
    public LunarHookItem() {
        super(ModRarity.RED, 4, 22.92F, 1.8F, HookType.SIMULTANEOUS,
                (itemStack, item, player, level) -> createHook(
                        itemStack, item, player, level, null));
    }

    @Override
    public AbstractHookEntity getHook(
            ItemStack itemStack,
            BaseHookItem item,
            Player player,
            Level level,
            @Nullable UUID pendingEviction
    ) {
        return createHook(itemStack, item, player, level, pendingEviction);
    }

    /// 按成功提交后的列表选择尚未占用的月钩外观；待淘汰实体仍留在世界中，直至新实体生成成功。
    private static LunarHookEntity createHook(
            ItemStack itemStack,
            BaseHookItem item,
            Player player,
            Level level,
            @Nullable UUID pendingEviction
    ) {
        ListTag list = LibUtils.getItemStackNbtNoCopy(itemStack)
                .getList("hooks", Tag.TAG_COMPOUND);
        AtomicBoolean nebula = new AtomicBoolean(true);
        AtomicBoolean solar = new AtomicBoolean(true);
        AtomicBoolean stardust = new AtomicBoolean(true);
        AtomicBoolean vortex = new AtomicBoolean(true);
        list.forEach(tag -> {
            if (tag instanceof net.minecraft.nbt.CompoundTag compound
                    && pendingEviction != null
                    && compound.hasUUID("uuid")
                    && pendingEviction.equals(compound.getUUID("uuid"))) {
                return;
            }
            AbstractHookEntity hookEntity = getHookEntity(tag, level, player);
            if (hookEntity instanceof LunarHookEntity lunarHookEntity) {
                switch (lunarHookEntity.getVariant()) {
                    case NEBULA -> nebula.set(false);
                    case SOLAR -> solar.set(false);
                    case STARDUST -> stardust.set(false);
                    case VORTEX -> vortex.set(false);
                }
            }
        });
        if (nebula.get())
            return new LunarHookEntity(item, player, level, LunarHookEntity.Variant.NEBULA);
        if (solar.get())
            return new LunarHookEntity(item, player, level, LunarHookEntity.Variant.SOLAR);
        if (stardust.get())
            return new LunarHookEntity(item, player, level, LunarHookEntity.Variant.STARDUST);
        if (vortex.get())
            return new LunarHookEntity(item, player, level, LunarHookEntity.Variant.VORTEX);
        return new LunarHookEntity(item, player, level, LunarHookEntity.Variant.NEBULA);
    }
}
