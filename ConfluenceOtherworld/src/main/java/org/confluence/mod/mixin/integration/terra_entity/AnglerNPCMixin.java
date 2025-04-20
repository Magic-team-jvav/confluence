package org.confluence.mod.mixin.integration.terra_entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.mixed.IAbstractTerraNPC;
import org.confluence.terraentity.entity.npc.AnglerNPC;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AnglerNPC.class, remap = false)
public abstract class AnglerNPCMixin implements SelfGetter<AnglerNPC> {
    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lorg/confluence/terraentity/entity/npc/AnglerNPC;setWakeUp(Z)V"))
    private void setAnglerAdded(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof ServerPlayer serverPlayer) {
            NPCSpawner.INSTANCE.moveNPCToAnotherRegion(
                    confluence$self(),
                    IAbstractTerraNPC.of(confluence$self()).confluence$getRegion(),
                    new NPCSpawner.Region(NPCSpawner.getNpcSpawnPos(serverPlayer))
            );
        }
    }
}
