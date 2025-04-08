package org.confluence.mod.common.item.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.data.saved.HardmodeConvertor;

public class HardmodeConvertorItem extends Item {
    public HardmodeConvertorItem() {
        super(new Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.MASTER));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel serverLevel) || HardmodeConvertor.INSTANCE.isStarted()) return InteractionResult.SUCCESS;
        HardmodeConvertor.INSTANCE.start(serverLevel.getServer());
        return InteractionResult.CONSUME;
    }
}
