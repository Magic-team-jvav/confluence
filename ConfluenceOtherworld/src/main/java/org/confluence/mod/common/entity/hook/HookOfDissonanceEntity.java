package org.confluence.mod.common.entity.hook;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.hook.BaseHookItem;

public class HookOfDissonanceEntity extends AbstractHookEntity {
    public HookOfDissonanceEntity(EntityType<? extends AbstractHookEntity> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    public HookOfDissonanceEntity(BaseHookItem item, Player player, Level level) {
        super(ModEntities.HOOK_OF_DISSONANCE.get(), item, player, level);
    }

    @Override
    protected void onHooked(BlockHitResult hitResult, ItemStack itemStack) {
        if (getOwner() != null) {
            Vec3 vec3 = getDeltaMovement().normalize().scale(0.5);
            getOwner().teleportTo(getX() - vec3.x, getY() - vec3.y, getZ() - vec3.z);
        }
        super.onHooked(hitResult, itemStack);
    }
}
