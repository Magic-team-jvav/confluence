package org.confluence.mod.client.renderer.entity.bullet;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.renderer.entity.bullet.effect.*;
import org.confluence.mod.common.item.gun.definition.BulletImpactEffect;
import org.mesdag.portlib.event.client.PortRenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BulletVfxManager {
    private static final List<ActiveBulletVfx> ACTIVE_EFFECTS = new ArrayList<>();
    private static final Map<BulletImpactEffect, BulletImpactVfx> IMPACT_EFFECTS = Map.of(
            BulletImpactEffect.SILVER_CROSS, SilverImpactVfx.INSTANCE,
            BulletImpactEffect.PARTY_CONFETTI, PartyConfettiVfx.INSTANCE,
            BulletImpactEffect.CRYSTAL_IMPACT, CrystalImpactVfx.INSTANCE,
            BulletImpactEffect.LUMINITE_IMPACT, LuminiteImpactVfx.INSTANCE,
            BulletImpactEffect.CHLOROPHYTE_IMPACT, ChlorophyteImpactVfx.INSTANCE);

    public static void add(ActiveBulletVfx effect) {
        ACTIVE_EFFECTS.add(effect);
    }

    public static void play(BulletImpactEffect effect, Vec3 position) {
        BulletImpactVfx vfx = IMPACT_EFFECTS.get(effect);
        if (vfx != null) vfx.play(position);
    }

    public static void tick() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            ACTIVE_EFFECTS.clear();
            return;
        }
        for (int index = ACTIVE_EFFECTS.size() - 1; index >= 0; index--) {
            if (!ACTIVE_EFFECTS.get(index).tick(level)) ACTIVE_EFFECTS.remove(index);
        }
    }

    public static void render(PortRenderLevelStageEvent event) {
        if (event.getStage() != PortRenderLevelStageEvent.Stage.AFTER_PARTICLES || ACTIVE_EFFECTS.isEmpty())
            return;
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPosition = event.getCamera().getPosition();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        for (ActiveBulletVfx effect : ACTIVE_EFFECTS)
            effect.render(poseStack, bufferSource, cameraPosition);
        bufferSource.endBatch(BulletRenderTypes.confetti());
    }

    private BulletVfxManager() {}
}
