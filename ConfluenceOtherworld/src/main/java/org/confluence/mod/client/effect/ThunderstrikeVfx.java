package org.confluence.mod.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.lightning.ArcSurgeLightning;
import org.confluence.mod.common.item.mana.ThunderstrikeItem;
import org.confluence.mod.network.c2s.ThunderstrikeRangePacketC2S;
import org.confluence.mod.network.s2c.ThunderstrikePacketS2C;
import org.mesdag.opallight.light.engine.DynamicLights;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import org.mesdag.portlib.network.PortPacketDistributor;

import java.util.ArrayList;
import java.util.List;

public final class ThunderstrikeVfx {
    private record Strike(ArcSurgeLightning.Event effect, long tick) {}
    private static final List<Strike> STRIKES = new ArrayList<>();
    private static final List<ParticleEmitter> IMPACTS = new ArrayList<>();
    private static ClientLevel level;
    private static ParticleEmitter preview;
    private static long ticks;
    private static double lastSentRange = Double.NaN;
    private static int receivedPackets, renderedFrames, submittedLights;

    private ThunderstrikeVfx() {}

    public static boolean scroll(double delta) {
        var mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null || !Screen.hasShiftDown()
                || !ThunderstrikeItem.held(mc.player) || delta == 0) return false;
        double range = Mth.clamp(ThunderstrikeItem.range(mc.player) + Math.signum(delta), 1, ThunderstrikeItem.maxRange(mc.player));
        mc.player.getPersistentData().putDouble(ThunderstrikeItem.RANGE_KEY, range);
        sendRange(range);
        return true;
    }

    private static void sendRange(double range) {
        PortPacketDistributor.sendToServer(new ThunderstrikeRangePacketC2S(range));
        lastSentRange = range;
    }

    public static void tick(Minecraft mc) {
        if (mc.level != level) { clear(); level = mc.level; }
        if (mc.level == null || mc.isPaused()) return;
        ticks++;
        IMPACTS.removeIf(ParticleEmitter::isRemoved);
        STRIKES.removeIf(s -> ticks - s.tick >= ArcSurgeLightning.SNAPSHOT_LIFETIME_TICKS);
        if (mc.player == null || !mc.player.isAlive() || mc.player.isSpectator() || !ThunderstrikeItem.held(mc.player)) {
            if (preview != null) { preview.remove(); preview = null; }
            lastSentRange = Double.NaN;
            return;
        }
        double range = ThunderstrikeItem.range(mc.player);
        if (range != lastSentRange) sendRange(range);
        var target = ThunderstrikeItem.target(mc.player);
        if (preview == null || preview.isRemoved()) {
            preview = new ParticleEmitter(level, target, Confluence.asResource("thunderstrike_preview"));
            MolangParticleEngine.INSTANCE.addEmitter(preview);
        }
        preview.setPos(target);
    }

    public static void receive(ThunderstrikePacketS2C packet) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return;
        receivedPackets++;
        if (level != mc.level) { clear(); level = mc.level; }
        // Bound visual work even when many players cast together.
        if (STRIKES.size() >= 64) STRIKES.remove(0);
        STRIKES.add(new Strike(ArcSurgeLightning.createThunderstrike(packet.start(), packet.end(), packet.seed(), packet.spreadAxis()), ticks));
        var flash = new ParticleEmitter(level, packet.end(), Confluence.asResource("thunderstrike_impact"));
        if (IMPACTS.size() >= 64) IMPACTS.remove(0).remove();
        IMPACTS.add(flash);
        MolangParticleEngine.INSTANCE.addEmitter(flash);
    }

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || level == null) return;
        ArcSurgeLightning.render(STRIKES.stream().map(s -> new ArcSurgeLightning.Frame(s.effect,
                ticks - s.tick + event.getPartialTick())).toList(), event);
        if (!STRIKES.isEmpty()) renderedFrames++;
    }

    public static void collectLights(ClientLevel world, DynamicLights.Sink sink) {
        if (world != level) return;
        int budget = 64;
        for (var strike : STRIKES) {
            budget -= strike.effect.collectLights(ticks - strike.tick + Minecraft.getInstance().getFrameTime(), sink, budget);
            if (budget <= 0) break;
        }
        submittedLights = 64 - budget;
    }

    public static java.util.Map<String, Object> debugSnapshot() {
        return java.util.Map.of("receivedPackets", receivedPackets, "renderedFrames", renderedFrames,
                "activeStrikes", STRIKES.size(), "submittedLights", submittedLights,
                "preview", preview != null && !preview.isRemoved());
    }

    public static void clear() {
        STRIKES.clear();
        IMPACTS.forEach(ParticleEmitter::remove);
        IMPACTS.clear();
        if (preview != null) preview.remove();
        preview = null; level = null; ticks = 0; lastSentRange = Double.NaN;
    }
}
