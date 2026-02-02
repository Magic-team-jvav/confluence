package org.confluence.terraentity.mixin.client;

import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import org.confluence.terraentity.mixed.IBossHealthOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin implements IBossHealthOverlay {
    @Final
    @Shadow
    Map<UUID, LerpingBossEvent> events;

    @Override
    public Map<UUID, LerpingBossEvent> terra_entity$getEvents() {
        return events;
    }
}
