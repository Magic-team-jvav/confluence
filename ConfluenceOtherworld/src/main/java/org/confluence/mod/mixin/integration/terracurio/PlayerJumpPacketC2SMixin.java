package org.confluence.mod.mixin.integration.terracurio;

import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.item.sword.MarlinSpikeSwordItem;
import org.confluence.terra_curio.network.c2s.PlayerJumpPacketC2S;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerJumpPacketC2S.class, remap = false)
public abstract class PlayerJumpPacketC2SMixin {
    /// 只有真正由多段跳饰品驱动的二段跳才会带上 {@link PlayerJumpPacketC2S#JUMP_BY_SELF}；
    /// 飞行、滑翔与一次性跳跃用的是 {@link PlayerJumpPacketC2S#RESET_FALL_DISTANCE}，因此不会被误判。
    @Inject(method = "work", at = @At("HEAD"), remap = false)
    private void marlinSpikeCharge(ServerPlayer player, CallbackInfo ci) {
        PlayerJumpPacketC2S self = (PlayerJumpPacketC2S) (Object) this;
        if ((self.jumpState() & PlayerJumpPacketC2S.JUMP_BY_SELF) != 0) {
            MarlinSpikeSwordItem.onMultiJump(player);
        }
    }
}
