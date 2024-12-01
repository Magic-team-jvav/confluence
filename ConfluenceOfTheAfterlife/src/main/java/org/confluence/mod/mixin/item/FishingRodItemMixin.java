package org.confluence.mod.mixin.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.item.accessory.FishingBobber;
import org.confluence.mod.mixed.IFishingHook;
import org.confluence.mod.mixin.accessor.FishingHookAccessor;
import org.confluence.terra_curio.util.CuriosUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FishingRodItem.class)
public abstract class FishingRodItemMixin {
    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private Entity replaceHook(Entity entity) {
        if (!(entity instanceof FishingHook fishingHook)) return entity;
        Player playerOwner = fishingHook.getPlayerOwner();
        if (playerOwner == null) return entity;
        FishingBobber curio = CuriosUtils.findCurio(playerOwner, FishingBobber.class);
        if (curio != null) {
            fishingHook = new CurioFishingHook(
                    playerOwner,
                    fishingHook.level(),
                    ((FishingHookAccessor) fishingHook).getLuck(),
                    ((FishingHookAccessor) fishingHook).getLureSpeed(),
                    curio.variant
            );
        }
        if (TCUtils.hasAccessoriesType(playerOwner, AccessoryItems.LAVAPROOF$FISHING$HOOK)) {
            ((IFishingHook) fishingHook).confluence$setIsLavaHook();
        }
        return fishingHook;
    }
}
