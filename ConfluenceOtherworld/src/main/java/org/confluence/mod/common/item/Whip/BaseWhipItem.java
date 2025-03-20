package org.confluence.mod.common.item.Whip;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.WhipEntity;
import org.confluence.mod.common.init.ModEntities;

public class BaseWhipItem extends Item {

    public BaseWhipItem(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(!level.isClientSide){
            WhipEntity whipEntity = ModEntities.WHIP_PROJECTILE.get().create(level);
            whipEntity.setOwner(player);
            whipEntity.setPos(player.position().add(0,1,0));
            whipEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0, 1.0F);
            level.addFreshEntity(whipEntity);
            player.getCooldowns().addCooldown(this, 20);
        }
        return super.use(level, player, usedHand);
    }
}