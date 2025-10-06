package org.confluence.mod.mixin.integration.xaero;

import com.google.common.collect.Iterables;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xaero.hud.category.rule.resolver.ObjectCategoryRuleResolver;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.state.RadarList;

import java.util.Map;

@Pseudo
@Mixin(targets = "xaero.hud.minimap.radar.state.RadarStateUpdater", remap = false)
public abstract class RadarStateUpdaterMixin {
    @Shadow
    @Final
    private Map<EntityRadarCategory, RadarList> updateMap;

    @ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;"))
    private Iterable<Entity> appendPlayers(Iterable<Entity> original, @Local(argsOnly = true) ClientLevel world, @Local(argsOnly = true) Player player, @Local EntityRadarCategory rootCategory, @Local ObjectCategoryRuleResolver categoryRuleResolver) {
        if (CompatibilityHandler.isXaerosMapWormholePotion() && Minecraft.getInstance().isMultiplayerServer()) {
            for (AbstractClientPlayer clientPlayer : world.players()) {
                if (clientPlayer instanceof RemotePlayer remotePlayer) {
                    EntityRadarCategory entityCategory = categoryRuleResolver.resolve(rootCategory, remotePlayer, player);
                    RadarList radarList = updateMap.get(entityCategory);
                    radarList.add(remotePlayer);
                }
            }
            return Iterables.filter(original, entity -> !(entity instanceof RemotePlayer));
        }
        return original;
    }
}
