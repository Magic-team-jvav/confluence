package org.confluence.mod.common.item.common;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.common.data.saved.HardmodeConvertor;

public class HardmodeConvertorItem extends CustomRarityItem {
    public HardmodeConvertorItem() {
        super(new Properties().stacksTo(1), ModRarity.MASTER);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel serverLevel))
            return InteractionResult.SUCCESS;
        MinecraftServer server = serverLevel.getServer();
        if (HardmodeConvertor.INSTANCE.isStarted()) {
            server.getPlayerList().broadcastSystemMessage(Component.translatable("event.confluence.hardmode_conversion.pass"), false);
            return InteractionResult.PASS;
        }
        HardmodeConvertor.INSTANCE.start(server, true);
        return InteractionResult.CONSUME;
    }
}
