package org.confluence.mod.common.item.hook;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.entity.hook.LunarHookEntity;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.util.TCUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class LunarHookItem extends BaseHookItem implements IHookFastThrow {
    public LunarHookItem() {
        super(ModRarity.RED, 4, 22.92F, 1.8F, HookType.SIMULTANEOUS, (itemStack, item, player, level) -> {
            ListTag list = TCUtils.getItemStackNbt(itemStack).getList("hooks", Tag.TAG_COMPOUND);
            AtomicBoolean nebula = new AtomicBoolean(true);
            AtomicBoolean solar = new AtomicBoolean(true);
            AtomicBoolean stardust = new AtomicBoolean(true);
            AtomicBoolean vortex = new AtomicBoolean(true);
            list.forEach(tag -> {
                AbstractHookEntity hookEntity = getHookEntity(tag, (ServerLevel) level);
                if (hookEntity instanceof LunarHookEntity lunarHookEntity) {
                    switch (lunarHookEntity.getVariant()) {
                        case NEBULA -> nebula.set(false);
                        case SOLAR -> solar.set(false);
                        case STARDUST -> stardust.set(false);
                        case VORTEX -> vortex.set(false);
                    }
                }
            });
            if (nebula.get()) return new LunarHookEntity(item, player, level, LunarHookEntity.Variant.NEBULA);
            if (solar.get()) return new LunarHookEntity(item, player, level, LunarHookEntity.Variant.SOLAR);
            if (stardust.get()) return new LunarHookEntity(item, player, level, LunarHookEntity.Variant.STARDUST);
            if (vortex.get()) return new LunarHookEntity(item, player, level, LunarHookEntity.Variant.VORTEX);
            return new LunarHookEntity(item, player, level, LunarHookEntity.Variant.NEBULA);
        });
    }
}
