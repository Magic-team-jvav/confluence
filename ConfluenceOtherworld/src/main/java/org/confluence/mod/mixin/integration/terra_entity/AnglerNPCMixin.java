package org.confluence.mod.mixin.integration.terra_entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.integration.terra_entity.IAbstractTerraNPC;
import org.confluence.terraentity.entity.npc.AnglerNPC;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AnglerNPC.class, remap = false)
public abstract class AnglerNPCMixin implements SelfGetter<AnglerNPC> {
    @Inject(method = "mobInteract", at = @At(value = "RETURN", ordinal = 0))
    private void setAnglerAdded(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof ServerPlayer serverPlayer) {
            AnglerNPC npc = confluence$self();
            NPCSpawner.Region region = new NPCSpawner.Region(NPCSpawner.getNpcSpawnPos(serverPlayer));
            NPCSpawner.INSTANCE.moveNPCToAnotherRegion(npc, IAbstractTerraNPC.of(npc).confluence$getRegion(), region);
            NPCSpawner.broadcastMessageToRegion(player.level(), npc, Component.translatable("event.confluence.npc.arrived", npc.getType().getDescription(), npc.getName()).withColor(GlobalColors.NPC_ARRIVED.get()));
        }
    }

    @Inject(method = "checkDespawn", at = @At("TAIL"))
    private void onRemove(CallbackInfo ci) {
        AnglerNPC npc = confluence$self();
        if (npc.isRemoved()) {
            NPCSpawner.INSTANCE.setNPCAlive(((IAbstractTerraNPC) npc).confluence$getRegion(), npc.getType(), false);
        }
    }
}
