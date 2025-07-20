package org.confluence.mod.mixin.integration.heaven_destiny_moment.terra_moment;


import com.xiaohunao.heaven_destiny_moment.common.context.condition.player.PlayerCondition;
import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstance;
import com.xiaohunao.terra_moment.common.moment.Instance.BloodMoonInstance;
import com.xiaohunao.terra_moment.common.moment.Instance.SlimeRainInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(SlimeRainInstance.class)
public class SlimeRainInstanceMixin {
    @Inject(method = "canCreate", at = @At("HEAD"), cancellable = true)
    private void confluence$canCreate(Map<UUID, MomentInstance> runMoments, Level level, @Nullable BlockPos pos, @Nullable ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        SlimeRainInstance slimeRainInstance = (SlimeRainInstance)(Object) this;
        boolean everBeneficial = PlayerCondition.Type.ANY.matches(slimeRainInstance, pos, player, (momentInstance, pos1, serverPlayer) -> {
            EverBeneficial data = serverPlayer.getData(ModAttachmentTypes.EVER_BENEFICIAL);
            return data != null && data.getUsedLifeCrystals() >= 2;
        });
        cir.setReturnValue(everBeneficial);
    }
}
