package org.confluence.mod.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.lightning.ArcSurgeLightning;
import org.confluence.mod.network.s2c.RedGloveArcPacketS2C;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.item.mana.RedGlovePattern;
import org.confluence.mod.common.summoner.particle.ZenithParticleOptions;

import java.util.*;

/** Authoritative discharge snapshots with a short, visual-only afterimage. */
public final class RedGloveVfx {
    private static final boolean DEBUG = Boolean.getBoolean("confluence.redGlove.probe");
    private static volatile int renderedFrames, lateVisibleFrames;
    private static int receivedPackets, spawnedZenithParticles;
    private static long clockDelta;
    private static long localTick;
    private static final class Cast {
        final long tick;
        final long seed;
        final long receivedTick;
        final List<ArcSurgeLightning.Event> batches = new ArrayList<>();
        final List<RedGlovePattern.Path> particlePaths = new ArrayList<>();
        boolean particlesEmitted;
        Cast(long tick, long seed, long receivedTick) { this.tick = tick; this.seed = seed; this.receivedTick = receivedTick; }
    }
    private static final Map<Integer, Cast> CASTS = new LinkedHashMap<>();
    private static final List<Cast> AFTERIMAGES = new ArrayList<>();
    private static ClientLevel level;
    private static final List<Particle> SPARKS = new ArrayList<>();
    private static final int PARTICLES_PER_CAST = 24, PARTICLES_PER_TICK = 48, MAX_PARTICLES = 256;
    public static void receive(RedGloveArcPacketS2C packet) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || !mc.level.dimension().location().equals(packet.dimension())) return;
        if (level != mc.level) { clear(); level = mc.level; }
        if (DEBUG) { receivedPackets++; clockDelta = level.getGameTime() - packet.tick(); }
        Cast current = CASTS.get(packet.casterId());
        if (current != null && current.tick > packet.tick()) return;
        if (current == null || current.tick < packet.tick()) {
            if (current != null) AFTERIMAGES.add(current);
            current = new Cast(packet.tick(), packet.seed(), localTick); CASTS.put(packet.casterId(), current);
        }
        try {
            current.batches.add(ArcSurgeLightning.createSnapshot(packet.paths(), packet.seed()));
            if (!current.particlesEmitted) current.particlePaths.addAll(packet.paths());
        }
        catch (Exception exception) { Confluence.LOGGER.error("Unable to create red glove discharge", exception); }
    }
    public static void tick(Minecraft mc) {
        if (level == null) return;
        if (mc.level != level) { clear(); return; }
        if (mc.isPaused()) return;
        localTick++;
        CASTS.values().removeIf(c -> localTick - c.receivedTick >= ArcSurgeLightning.RED_GLOVE_LIFETIME_TICKS);
        AFTERIMAGES.removeIf(c -> localTick - c.receivedTick >= ArcSurgeLightning.RED_GLOVE_LIFETIME_TICKS);
        SPARKS.removeIf(particle -> !particle.isAlive());
        int remaining = PARTICLES_PER_TICK;
        for (Cast cast : CASTS.values()) remaining -= emitParticles(mc, cast, remaining);
        for (int i = AFTERIMAGES.size() - 1; i >= 0 && remaining > 0; i--)
            remaining -= emitParticles(mc, AFTERIMAGES.get(i), remaining);
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
        if (DEBUG && frames.stream().anyMatch(f -> f.age() >= ArcSurgeLightning.SNAPSHOT_LIFETIME_TICKS
                && f.event().visibleAt(f.age()))) lateVisibleFrames++;
    }
    private record SparkSegment(Vec3 a, Vec3 b, double length) {}

    private static int emitParticles(Minecraft mc, Cast cast, int budget) {
        if (cast.particlesEmitted || budget <= 0) return 0;
        cast.particlesEmitted = true;
        if (localTick - cast.receivedTick > 4) { cast.particlePaths.clear(); return 0; }
        List<SparkSegment> segments = new ArrayList<>();
        double length = 0;
        for (var path : cast.particlePaths) for (int i = 1; i < path.points().size(); i++) {
            Vec3 a = path.points().get(i - 1), b = path.points().get(i);
            double size = a.distanceTo(b);
            if (size > 1.0E-6) { segments.add(new SparkSegment(a, b, size)); length += size; }
        }
        cast.particlePaths.clear();
        if (segments.isEmpty()) return 0;
        int count = Math.min(PARTICLES_PER_CAST, budget);
        var random = new SplittableRandom(cast.seed);
        int segment = 0, emitted = 0;
        double passed = 0;
        for (int i = 0; i < count; i++) {
            double distance = (i + random.nextDouble(0.15, 0.85)) * length / count;
            while (segment + 1 < segments.size() && passed + segments.get(segment).length < distance)
                passed += segments.get(segment++).length;
            SparkSegment line = segments.get(segment);
            Vec3 pos = line.a.lerp(line.b, Math.max(0, Math.min(1, (distance - passed) / line.length)));
            Vec3 velocity = new Vec3(random.nextDouble(-1, 1), random.nextDouble(-1, 1), random.nextDouble(-1, 1))
                    .normalize().scale(random.nextDouble(0.3, 0.6));
            // Same red hue, independently varied luminance; Zenith supplies its normal light/decay.
            double brightness = random.nextDouble(0.5, 1);
            int color = ((int) Math.round(255 * brightness) << 16)
                    | ((int) Math.round(28 * brightness) << 8) | (int) Math.round(40 * brightness);
            var options = new ZenithParticleOptions(color, random.nextInt(5, 15),
                    (float) random.nextDouble(0.5, 0.75), (float) random.nextDouble(0.01, 0.03));
            Particle particle = mc.particleEngine.createParticle(options, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
            if (particle != null) {
                if (SPARKS.size() >= MAX_PARTICLES) SPARKS.remove(0).remove();
                SPARKS.add(particle); emitted++; spawnedZenithParticles++;
            }
        }
        return emitted;
    }

    public static Map<String, Object> debugSnapshot() {
        return Map.ofEntries(Map.entry("receivedPackets", receivedPackets), Map.entry("renderedFrames", renderedFrames),
                Map.entry("activeCasters", CASTS.size()), Map.entry("afterimages", AFTERIMAGES.size()),
                Map.entry("activeSegments", CASTS.values().stream().flatMap(c -> c.batches.stream())
                        .mapToInt(ArcSurgeLightning.Event::segmentCount).sum()), Map.entry("submittedLights", 0),
                Map.entry("lifetimeTicks", ArcSurgeLightning.RED_GLOVE_LIFETIME_TICKS),
                Map.entry("zenithParticles", SPARKS.size()), Map.entry("spawnedZenithParticles", spawnedZenithParticles),
                Map.entry("lateVisibleFrames", lateVisibleFrames), Map.entry("rawClientServerClockDelta", clockDelta));
    }
    public static int renderedFrameCount() { return renderedFrames; }
    public static int lateVisibleFrameCount() { return lateVisibleFrames; }
    public static void clear() {
        CASTS.clear(); AFTERIMAGES.clear(); SPARKS.forEach(Particle::remove); SPARKS.clear(); level = null; localTick = 0;
    }
}
