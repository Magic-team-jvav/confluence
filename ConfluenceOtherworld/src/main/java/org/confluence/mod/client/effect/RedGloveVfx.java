package org.confluence.mod.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.lightning.ArcSurgeLightning;
import org.confluence.mod.network.s2c.RedGloveArcPacketS2C;
import org.mesdag.opallight.light.engine.DynamicLights;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import org.mesdag.particlestorm.particle.MolangParticleEngine;

import java.util.*;

/** Authoritative discharge snapshots with a short, visual-only afterimage. */
public final class RedGloveVfx {
    private static final boolean DEBUG = Boolean.getBoolean("confluence.redGlove.probe");
    private static volatile int renderedFrames;
    private static int receivedPackets, submittedLights;
    private static long clockDelta;
    private static long localTick;
    private static final class Cast {
        final long tick;
        final long seed;
        final long receivedTick;
        final List<ArcSurgeLightning.Event> batches = new ArrayList<>();
        Cast(long tick, long seed, long receivedTick) { this.tick = tick; this.seed = seed; this.receivedTick = receivedTick; }
    }
    private static final Map<Integer, Cast> CASTS = new LinkedHashMap<>();
    private static final List<Cast> AFTERIMAGES = new ArrayList<>();
    private static ClientLevel level;
    private static final List<ParticleEmitter> SPARKS = new ArrayList<>();
    public static void receive(RedGloveArcPacketS2C packet) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || !mc.level.dimension().location().equals(packet.dimension())) return;
        if (level != mc.level) { clear(); level = mc.level; }
        if (DEBUG) { receivedPackets++; clockDelta = level.getGameTime() - packet.tick(); }
        Cast current = CASTS.get(packet.casterId());
        boolean newPattern = current == null || current.seed != packet.seed();
        if (current != null && current.tick > packet.tick()) return;
        if (current == null || current.tick < packet.tick()) {
            if (current != null) AFTERIMAGES.add(current);
            current = new Cast(packet.tick(), packet.seed(), localTick); CASTS.put(packet.casterId(), current);
        }
        try {
            // Overlapping snapshots need distinct light IDs, including their fading afterimages.
            current.batches.add(ArcSurgeLightning.createSnapshot(packet.paths(), packet.seed()));
            if (newPattern && packet.paths().stream().anyMatch(path -> path.depth() == 0)) {
                if (SPARKS.size() >= 128) SPARKS.remove(0).remove();
                var emitter = new ParticleEmitter(level, packet.origin(), Confluence.asResource("arc_surge_sparks"));
                emitter.hideOutline = true; SPARKS.add(emitter); MolangParticleEngine.INSTANCE.addEmitter(emitter);
            }
        }
        catch (Exception exception) { Confluence.LOGGER.error("Unable to create red glove discharge", exception); }
    }
    public static void tick(Minecraft mc) {
        if (level == null) return;
        if (mc.level != level) { clear(); return; }
        if (mc.isPaused()) return;
        localTick++;
        CASTS.values().removeIf(c -> localTick - c.receivedTick >= ArcSurgeLightning.SNAPSHOT_LIFETIME_TICKS);
        AFTERIMAGES.removeIf(c -> localTick - c.receivedTick >= ArcSurgeLightning.SNAPSHOT_LIFETIME_TICKS);
        SPARKS.removeIf(ParticleEmitter::isRemoved);
    }
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || level == null) return;
        var frames = new ArrayList<ArcSurgeLightning.Frame>();
        for (Cast cast : AFTERIMAGES) {
            double age = Math.max(0, localTick - cast.receivedTick + event.getPartialTick());
            for (var batch : cast.batches) frames.add(new ArcSurgeLightning.Frame(batch, age));
        }
        for (Cast cast : CASTS.values()) {
            double age = Math.max(0, localTick - cast.receivedTick + event.getPartialTick());
            for (var batch : cast.batches) frames.add(new ArcSurgeLightning.Frame(batch, age));
        }
        ArcSurgeLightning.render(frames, event);
        if (DEBUG && !frames.isEmpty()) renderedFrames++;
    }
    public static void collectLights(ClientLevel world, DynamicLights.Sink sink) {
        if (world != level) return;
        int remaining = 64;
        for (Cast cast : CASTS.values()) {
            double age = Math.max(0, localTick - cast.receivedTick + Minecraft.getInstance().getFrameTime());
            for (var batch : cast.batches) remaining -= batch.collectLights(age, sink, remaining);
        }
        // Prioritize the current discharge; fading geometry shares the existing 64-light cap.
        for (int i = AFTERIMAGES.size() - 1; i >= 0 && remaining > 0; i--) {
            Cast cast = AFTERIMAGES.get(i);
            double age = Math.max(0, localTick - cast.receivedTick + Minecraft.getInstance().getFrameTime());
            for (var batch : cast.batches) remaining -= batch.collectLights(age, sink, remaining);
        }
        if (DEBUG) submittedLights = 64 - remaining;
    }
    public static Map<String, Object> debugSnapshot() {
        return Map.of("receivedPackets", receivedPackets, "renderedFrames", renderedFrames,
                "activeCasters", CASTS.size(), "activeSegments", CASTS.values().stream().flatMap(c -> c.batches.stream())
                        .mapToInt(ArcSurgeLightning.Event::segmentCount).sum(), "submittedLights", submittedLights,
                "rawClientServerClockDelta", clockDelta);
    }
    public static int renderedFrameCount() { return renderedFrames; }
    public static void clear() {
        CASTS.clear(); AFTERIMAGES.clear(); SPARKS.forEach(ParticleEmitter::remove); SPARKS.clear(); level = null; localTick = 0;
    }
}
