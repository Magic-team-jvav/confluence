package org.confluence.mod.common.item.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.boss.TwinEyeEncounter;

/// 双子刷怪蛋直接生成两个眼球，不借助中间实体转发召唤。
public final class TwinEyeSpawnEggItem extends Item {
    public TwinEyeSpawnEggItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel() instanceof ServerLevel level) {
            Vec3 position = Vec3.atBottomCenterOf(context.getClickedPos().relative(context.getClickedFace()));
            if (!TwinEyeEncounter.summon(level, position, context.getPlayer()))
                return InteractionResult.FAIL;
            if (context.getPlayer() == null || !context.getPlayer().hasInfiniteMaterials())
                context.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
}
