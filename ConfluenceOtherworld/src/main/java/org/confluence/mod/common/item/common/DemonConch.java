package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import org.confluence.lib.common.component.ModRarity;

import java.util.Optional;

public class DemonConch extends MagicConch {
    public DemonConch() {
        super(new Properties().stacksTo(1), ModRarity.LIGHT_RED);
    }

    protected Component getMessage(Optional<BlockPos> pos) {
        return Component.translatable("selections.confluence.demon_conch", pos.map(Vec3i::toShortString).orElse("unknown"));
    }

    @Override
    protected boolean checkAvailable(UseOnContext pContext) {
        return pContext.getLevel().getBlockState(pContext.getClickedPos()).is(Blocks.NETHER_PORTAL);
    }

    @Override
    protected Component successStoreMessage(BlockPos pos) {
        return Component.translatable("chat.confluence.demon_conch", pos.toShortString());
    }
}
