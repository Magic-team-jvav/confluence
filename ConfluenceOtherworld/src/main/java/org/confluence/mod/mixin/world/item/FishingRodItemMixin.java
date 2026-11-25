package org.confluence.mod.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.entity.fishing.FishingSession;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.item.accessory.FishingBobber;
import org.confluence.mod.mixed.IFishingHook;
import org.confluence.mod.network.s2c.FishingPowerInfoPacketS2C;
import org.confluence.terra_curio.util.CuriosUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FishingRodItem.class)
public abstract class FishingRodItemMixin {
    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private Entity replaceHook(Entity entity, @Local(argsOnly = true) InteractionHand hand) {
        if (!(entity instanceof FishingHook hook)) return entity;
        Player player = hook.getPlayerOwner();
        if (player == null) return entity;
        FishingBobber curio = CuriosUtils.findCurio(player, FishingBobber.class);
        if (curio != null) {
            hook = new CurioFishingHook(player, hook.level(), hook.luck, hook.lureSpeed, curio.variant);
        }
        if (TCUtils.hasType(player, AccessoryItems.LAVAPROOF$FISHING$HOOK)) {
            IFishingHook.of(hook).confluence$setIsLavaHook();
        }
        IFishingHook.of(hook).confluence$setFishingSession(new FishingSession(player, player.getItemInHand(hand), hand));
        FishingPowerInfoPacketS2C.sendToClient((ServerPlayer) player);
        return hook;
    }
}
