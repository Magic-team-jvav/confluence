package org.confluence.mod.mixin.server;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerWatchdog;
import net.minecraft.util.TimeUtil;
import org.confluence.mod.mixed.IDedicatedServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerWatchdog.class)
public abstract class ServerWatchdogMixin {
    @Unique
    private static final long CONSERVATION_DELAY = (0xC017FL * 0x1_117CE) * TimeUtil.NANOSECONDS_PER_MILLISECOND;
    @Shadow
    @Final
    private DedicatedServer server;

    @ModifyExpressionValue(method = "run", at = @At(value = "FIELD", target = "Lnet/minecraft/server/dedicated/ServerWatchdog;maxTickTimeNanos:J", ordinal = 0))
    private long replaceWhenOnHardmodeConversation(long original) {
        if (IDedicatedServer.of(server).confluence$isOnHardmodeConversation()) {
            return CONSERVATION_DELAY;
        }
        return original;
    }
}
