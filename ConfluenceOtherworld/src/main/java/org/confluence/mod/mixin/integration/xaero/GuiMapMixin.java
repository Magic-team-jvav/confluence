package org.confluence.mod.mixin.integration.xaero;

import net.minecraft.client.player.RemotePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.confluence.mod.network.c2s.WormholeToPlayerPacketC2S;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.element.HoveredMapElementHolder;
import xaero.map.gui.GuiMap;
import xaero.map.radar.tracker.PlayerTrackerMapElement;

import java.util.UUID;

@Pseudo
@Mixin(targets = "xaero.map.gui.GuiMap", remap = false)
public abstract class GuiMapMixin {
    @Shadow
    private HoveredMapElementHolder<?, ?> viewed;

    @Inject(method = "mouseClicked", at = @At(value = "FIELD", target = "Lxaero/map/gui/MapMouseButtonPress;pressedAtY:I", ordinal = 0, shift = At.Shift.AFTER))
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (CompatibilityHandler.isXaerosMapWormholePotion() && viewed != null) {
            UUID uuid;
            if (viewed.getElement() instanceof RemotePlayer player) {
                uuid = player.getUUID();
            } else if (viewed.getElement() instanceof PlayerTrackerMapElement<?> element) {
                uuid = element.getPlayerId();
            } else {
                return;
            }
            PacketDistributor.sendToServer(new WormholeToPlayerPacketC2S(uuid, WormholeToPlayerPacketC2S.ByMod.XAEROS_MAP));
            ((GuiMap) (Object) this).onClose();
        }
    }
}
