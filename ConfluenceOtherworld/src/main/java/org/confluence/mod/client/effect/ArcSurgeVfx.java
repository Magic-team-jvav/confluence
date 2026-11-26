package org.confluence.mod.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.lightning.ArcSurgeLightning;
import org.confluence.mod.common.item.mana.ArcSurgePattern;
import org.confluence.mod.common.particle.ZenithParticleOptions;
import org.confluence.mod.network.s2c.ArcSurgePacketS2C;

import java.util.*;

/// Authoritative discharge snapshots with a short, visual-only afterimage.
public final class ArcSurgeVfx {
    private static final boolean DEBUG = Boolean.getBoolean("confluence.arcSurge.probe");
    private static volatile int renderedFrames, lateVisibleFrames, followedFrames, retractingFrames;
    private static int receivedPackets, spawnedZenithParticles;
    private static long clockDelta;
    private static long localTick;

    private static final class Cast {
        final long tick;
        final long seed;
        final long receivedTick;
        final int casterId;
        final Vec3 origin;
        ArcSurgeLightning.Event effect;
        final List<ArcSurgePattern.Path> particlePaths = new ArrayList<>();
        boolean particlesEmitted;

        Cast(ArcSurgePacketS2C packet) {
            tick = packet.tick();
            seed = packet.seed();
            receivedTick = localTick;
            casterId = packet.casterId();
            origin = new Vec3(packet.origin());
        }
    }

    private static final Map<Integer, Cast> CASTS = new LinkedHashMap<>();
    private static final List<Cast> AFTERIMAGES = new ArrayList<>();
    private static Level level;
    private static final List<Particle> SPARKS = new ArrayList<>();

    public static void receive(Player player, ArcSurgePacketS2C packet) {
        if (level != player.level()) {
            clear();
            level = player.level();
        }
        if (DEBUG) {
            receivedPackets++;
            clockDelta = level.getGameTime() - packet.tick();
        }
        Cast current = CASTS.get(packet.casterId());
        if (current != null && current.tick > packet.tick()) return;
        if (current == null || current.tick < packet.tick()) {
            if (current != null) AFTERIMAGES.add(current);
            current = new Cast(packet);
            CASTS.put(packet.casterId(), current);
        }
        current.particlePaths.addAll(packet.paths());
        current.effect = null; // Assemble all ordered packet batches before resolving shared junctions.
    }

    public static void tick(Minecraft mc) {
        if (level == null) return;
        if (mc.level != level) {
            clear();
            return;
        }
        if (mc.isPaused()) return;
        localTick++;
        CASTS.values().removeIf(c -> localTick - c.receivedTick >= ArcSurgeLightning.ARC_SURGE_LIFETIME_TICKS);
        AFTERIMAGES.removeIf(c -> localTick - c.receivedTick >= ArcSurgeLightning.ARC_SURGE_LIFETIME_TICKS);
        SPARKS.removeIf(particle -> !particle.isAlive());
        int remaining = ArcSurgeSparks.PER_TICK;
        for (Cast cast : CASTS.values()) remaining -= emitParticles(mc, cast, remaining);
        for (int i = AFTERIMAGES.size() - 1; i >= 0 && remaining > 0; i--)
            remaining -= emitParticles(mc, AFTERIMAGES.get(i), remaining);
    }

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || level == null)
            return;
        var frames = new ArrayList<ArcSurgeLightning.Frame>();
        for (Cast cast : AFTERIMAGES) {
            double age = Math.max(0, localTick - cast.receivedTick + event.getPartialTick().getGameTimeDeltaPartialTick(false));
            addFrame(cast, age, event.getPartialTick().getGameTimeDeltaPartialTick(false), frames);
        }
        for (Cast cast : CASTS.values()) {
            double age = Math.max(0, localTick - cast.receivedTick + event.getPartialTick().getGameTimeDeltaPartialTick(false));
            addFrame(cast, age, event.getPartialTick().getGameTimeDeltaPartialTick(false), frames);
        }
        ArcSurgeLightning.render(frames, event);
        if (DEBUG && !frames.isEmpty()) renderedFrames++;
        if (DEBUG && frames.stream().anyMatch(f -> f.age() >= ArcSurgeLightning.SNAPSHOT_LIFETIME_TICKS
                && f.event().visibleAt(f.age()))) lateVisibleFrames++;
        if (DEBUG && frames.stream().anyMatch(f -> f.age() > ArcSurgeLightning.ARC_SURGE_HOLD_TICKS
                && f.event().visibleAt(f.age()))) retractingFrames++;
    }

    private record SparkSegment(Vec3 a, Vec3 b, double length) {}

    private static ArcSurgeLightning.Event effect(Cast cast) {
        if (cast.effect == null) try {
            cast.effect = ArcSurgeLightning.createArcSurge(cast.particlePaths, cast.seed);
        } catch (Exception error) {
            Confluence.LOGGER.error("Unable to create red glove discharge", error);
        }
        return cast.effect;
    }

    private static Vec3 displacement(Cast cast, float partialTick) {
        var caster = level.getEntity(cast.casterId);
        if (caster == null) return Vec3.ZERO;
        return caster.getEyePosition(partialTick).add(caster.getViewVector(partialTick).scale(0.35))
                .add(0, -0.12, 0).subtract(cast.origin);
    }

    private static void addFrame(Cast cast, double age, float partialTick, List<ArcSurgeLightning.Frame> frames) {
        var effect = effect(cast);
        if (effect != null) {
            Vec3 delta = displacement(cast, partialTick);
            frames.add(new ArcSurgeLightning.Frame(effect, age, delta));
            if (DEBUG && effect.visibleAt(age) && delta.lengthSqr() > 0.25) followedFrames++;
        }
    }

    private static int emitParticles(Minecraft mc, Cast cast, int budget) {
        if (cast.particlesEmitted || budget <= 0) return 0;
        cast.particlesEmitted = true;
        if (localTick - cast.receivedTick > 4) return 0;
        List<SparkSegment> segments = new ArrayList<>();
        double length = 0;
        var effect = effect(cast);
        if (effect == null) return 0;
        for (var path : effect.particlePaths(displacement(cast, 1)))
            for (int i = 1; i < path.points().size(); i++) {
                Vec3 a = path.points().get(i - 1), b = path.points().get(i);
                double size = a.distanceTo(b);
                if (size > 1.0E-6) {
                    segments.add(new SparkSegment(a, b, size));
                    length += size;
                }
            }
        if (segments.isEmpty()) return 0;
        int count = Math.min(ArcSurgeSparks.PER_CAST, budget);
        var random = new SplittableRandom(cast.seed);
        int segment = 0, emitted = 0;
        double passed = 0;
        for (int i = 0; i < count; i++) {
            double distance = (i + random.nextDouble(0.15, 0.85)) * length / count;
            while (segment + 1 < segments.size() && passed + segments.get(segment).length < distance)
                passed += segments.get(segment++).length;
            SparkSegment line = segments.get(segment);
            Vec3 pos = line.a.lerp(line.b, Math.max(0, Math.min(1, (distance - passed) / line.length)));
            Vec3 velocity = ArcSurgeSparks.velocity(line.b.subtract(line.a), random);
            int color = ArcSurgeSparks.color(random);
            var options = new ZenithParticleOptions(color, random.nextInt(5, 15),
                    (float) random.nextDouble(0.5, 0.75), (float) random.nextDouble(0.01, 0.03));
            Particle particle = mc.particleEngine.createParticle(options, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
            if (particle != null) {
                if (SPARKS.size() >= ArcSurgeSparks.CAPACITY) SPARKS.remove(0).remove();
                SPARKS.add(particle);
                emitted++;
                spawnedZenithParticles++;
            }
        }
        return emitted;
    }

    public static Map<String, Object> debugSnapshot() {
        return Map.ofEntries(Map.entry("receivedPackets", receivedPackets), Map.entry("renderedFrames", renderedFrames),
                Map.entry("activeCasters", CASTS.size()), Map.entry("afterimages", AFTERIMAGES.size()),
                Map.entry("activeSegments", CASTS.values().stream().flatMap(c -> c.particlePaths.stream())
                        .mapToInt(p -> p.points().size() - 1).sum()), Map.entry("submittedLights", 0),
                Map.entry("lifetimeTicks", ArcSurgeLightning.ARC_SURGE_LIFETIME_TICKS),
                Map.entry("holdTicks", ArcSurgeLightning.ARC_SURGE_HOLD_TICKS),
                Map.entry("retractTicks", ArcSurgeLightning.ARC_SURGE_RETRACT_TICKS),
                Map.entry("particlesPerCast", ArcSurgeSparks.PER_CAST),
                Map.entry("zenithParticles", SPARKS.size()), Map.entry("spawnedZenithParticles", spawnedZenithParticles),
                Map.entry("lateVisibleFrames", lateVisibleFrames), Map.entry("followedFrames", followedFrames),
                Map.entry("retractingFrames", retractingFrames),
                Map.entry("rawClientServerClockDelta", clockDelta));
    }

    public static int renderedFrameCount() {return renderedFrames;}

    public static int lateVisibleFrameCount() {return lateVisibleFrames;}

    public static int followedFrameCount() {return followedFrames;}

    public static int retractingFrameCount() {return retractingFrames;}

    public static void clear() {
        CASTS.clear();
        AFTERIMAGES.clear();
        SPARKS.forEach(Particle::remove);
        SPARKS.clear();
        level = null;
        localTick = 0;
    }
}
