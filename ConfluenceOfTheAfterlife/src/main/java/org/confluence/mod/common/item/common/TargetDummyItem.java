package org.confluence.mod.common.item.common;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.TargetDummyEntity;
import org.confluence.mod.common.init.ModEntities;

public class TargetDummyItem extends Item {
    public TargetDummyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Vec3 pos = context.getClickLocation();
        TargetDummyEntity entity = new TargetDummyEntity(ModEntities.TARGET_DUMMY.get(), context.getLevel());
        entity.setPos(pos);
        context.getLevel().addFreshEntity(entity);
        return super.useOn(context);
    }
}
