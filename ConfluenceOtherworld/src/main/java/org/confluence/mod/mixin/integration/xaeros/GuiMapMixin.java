package org.confluence.mod.mixin.integration.xaeros;

import net.minecraft.client.player.RemotePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.network.c2s.WormholeToPlayerPacketC2S;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.element.HoveredMapElementHolder;
import xaero.map.gui.GuiMap;

@Pseudo
@Mixin(targets = "xaero.map.gui.GuiMap", remap = false)
public abstract class GuiMapMixin {
    @Shadow
    private HoveredMapElementHolder<?, ?> viewed;

    @Inject(method = "mouseClicked", at = @At(value = "FIELD", target = "Lxaero/map/gui/MapMouseButtonPress;pressedAtY:I", ordinal = 0, shift = At.Shift.AFTER))
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (viewed != null && viewed.getElement() instanceof RemotePlayer player) {
            PacketDistributor.sendToServer(new WormholeToPlayerPacketC2S(player.getUUID()));
            ((GuiMap) (Object) this).onClose();
        }
    }
}
