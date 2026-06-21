package org.confluence.mod.common.item.common;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.entity.ThrownPowderEntity;

public class ThrownPowderItem extends Item {
    private final ISpreadable.Type type;

    public ThrownPowderItem(ISpreadable.Type type) {
        super(new Properties());
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            ThrownPowderEntity entity = new ThrownPowderEntity(level, type);
            entity.setPos(player.getX(), player.getEyeY() - 0.1F, player.getZ());
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.5F);
            level.addFreshEntity(entity);
            if (!player.hasInfiniteMaterials()) {
                itemStack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
