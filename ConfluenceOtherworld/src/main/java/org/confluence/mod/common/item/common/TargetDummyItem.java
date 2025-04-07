package org.confluence.mod.common.item.common;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.common.entity.TargetDummyEntity;
import org.confluence.mod.common.init.ModEntities;

public class TargetDummyItem extends CustomRarityItem {
    public TargetDummyItem() {
        super(new Properties(), ModRarity.BLUE);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Vec3 pos = context.getClickLocation();
        TargetDummyEntity entity = new TargetDummyEntity(ModEntities.TARGET_DUMMY.get(), context.getLevel());
        entity.setPos(pos);
        context.getLevel().addFreshEntity(entity);
        context.getItemInHand().consume(1, context.getPlayer());
        return super.useOn(context);
    }
}
