package org.confluence.mod.client.effect.lightning;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.mana.RedGlovePattern;
import org.mesdag.opallight.light.color.OpalColor;
import org.mesdag.opallight.light.engine.DynamicLights;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/** Host adapter for the minecraft-lightning skill's full geometry/material/composite pipeline. */
public final class ArcSurgeLightning {
    public static final int SNAPSHOT_LIFETIME_TICKS = 5;
    private static final Gson GSON = new Gson();
    private static LightningRecipe appearance;
    private static long nextLightEvent = 1;

    private ArcSurgeLightning() {}

    public static void reload() throws IOException {
        Minecraft minecraft = Minecraft.getInstance();
        Path config = minecraft.gameDirectory.toPath().resolve("config/confluence/arc_surge_lightning.json");
        LightningRecipe parsed;
        if (Files.isRegularFile(config)) {
            parsed = LightningRecipe.read(config);
        } else {
            var resource = minecraft.getResourceManager().getResource(Confluence.asResource("lightning/arc_surge.json"))
                    .orElseThrow(() -> new IOException("Missing arc surge lightning preset"));
            try (var reader = resource.openAsReader()) {
                parsed = LightningRecipe.parse(JsonParser.parseReader(reader));
            }
        }
        if (!parsed.mode.equals("strike")) throw new IllegalArgumentException("Arc surge recipe mode must be strike");
        appearance = parsed;
    }

    public static Event create(List<Vec3> guide, Vec3 normal, long seed) {
        return create(List.of(new Channel(guide, normal)), seed);
    }

    public record Channel(List<Vec3> guide, Vec3 normal) {
        public Channel { guide = List.copyOf(guide); }
    }

    public static Event create(List<Channel> channels, long seed) {
        if (appearance == null) throw new IllegalStateException("Load the arc surge recipe before creating an event");
        LightningRecipe recipe = LightningRecipe.parse(GSON.toJsonTree(appearance));
        recipe.seed = (int) (seed ^ (seed >>> 32));
        return new Event(channels, recipe);
    }

    public record Frame(Event event, double age) {}

    /** Draw the exact server damage polylines, without client-side random branches or reshaping. */
    public static Event createSnapshot(List<RedGlovePattern.Path> paths, long seed) throws IOException {
        return createSnapshot(paths, seed, nextLightEvent++ << 20);
    }
    public static Event createSnapshot(List<RedGlovePattern.Path> paths, long seed, long lightBase) throws IOException {
        if (appearance == null) reload();
        LightningRecipe recipe = LightningRecipe.parse(GSON.toJsonTree(appearance));
        recipe.seed = (int) (seed ^ (seed >>> 32));
        // Snapshot rendering starts at the return stroke, skipping leader progression.
        recipe.durationTicks = recipe.progressionTicks + SNAPSHOT_LIFETIME_TICKS;
        recipe.maxSegments = paths.stream().mapToInt(p -> p.points().size() - 1).sum() + paths.size() * 48 + 1;
        List<BoltGeometry.Segment> segments = new ArrayList<>();
        List<ArcSurgeEndpoints.Contact> contacts = new ArrayList<>();
        long mask = 0;
        for (var path : paths) {
            var points = path.points();
            float scale = path.depth() == 0 ? 1 : path.depth() == 1 ? 0.70f : 0.38f;
            float width = (float) recipe.strikeWidth * scale;
            for (int i = 1; i < points.size(); i++) {
                if (points.get(i - 1).distanceToSqr(points.get(i)) < 1.0E-12) continue;
                segments.add(new BoltGeometry.Segment(points.get(i - 1), points.get(i), width, width,
                        scale, (i - 1f) / (points.size() - 1), i / (float) (points.size() - 1), path.depth(), mask));
            }
            contacts.add(new ArcSurgeEndpoints.Contact(mask++, Vec3.ZERO));
        }
        var visible = BoltGeometry.sampled(recipe, segments);
        // Only small flare light rays, contained within the server's 0.16-block damage corridor.
        // All larger forks come exclusively from the server and participate in damage.
        var world = Minecraft.getInstance().level;
        var player = Minecraft.getInstance().player;
        var decorated = new ArcSurgeEndpoints(recipe.seed).apply(visible, recipe, contacts, 0.2,
                (a, b) -> world == null ? a : world.clip(new net.minecraft.world.level.ClipContext(a, b,
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        net.minecraft.world.level.ClipContext.Fluid.ANY, player)).getLocation(), false);
        return new Event(decorated, recipe, lightBase);
    }

    public static final class Event {
        private final LightningRecipe recipe;
        private final ArcSurgeNetwork geometry;
        private BoltGeometry sampled;
        private final long lightId;

        private Event(List<Channel> channels, LightningRecipe recipe) {
            this.recipe = recipe;
            lightId = nextLightEvent++ << 20;
            geometry = new ArcSurgeNetwork(channels, recipe);
        }
        private Event(BoltGeometry snapshot, LightningRecipe recipe, long lightBase) {
            this.recipe = recipe; geometry = null; sampled = snapshot; lightId = lightBase;
        }

        public double duration() { return recipe.durationTicks; }
        public int segmentCount() { return sampled == null ? 0 : sampled.segments().size(); }

        public boolean reaches(Vec3 target) {
            return sampled != null && sampled.segments().stream().anyMatch(s -> s.b().distanceToSqr(target) < 1.0E-8);
        }

        public void collectLights(double age, DynamicLights.Sink sink) {
            collectLights(age, sink, 40);
        }
        public int collectLights(double age, DynamicLights.Sink sink, int limit) {
            if (sampled == null || limit <= 0) return 0;
            int submitted = 0;
            for (var point : ArcSurgeLightSamples.sample(sampled, recipe, age)) {
                if (submitted >= limit) break;
                sink.point(lightId | point.id(), point.position(),
                        OpalColor.of(point.red(), point.green(), point.blue()), point.brightness());
                submitted++;
            }
            return submitted;
        }

        public void sample(double age, BiFunction<Vec3, Vec3, Vec3> clipper) {
            if (geometry == null) return;
            var world = Minecraft.getInstance().level;
            sampled = geometry.sample(age, world == null ? Long.MIN_VALUE : world.getGameTime(), clipper);
        }

        public Vec3 end(Vec3 fallback) {
            return sampled == null ? fallback : sampled.segments().stream().filter(s -> s.depth() == 0)
                    .reduce((a, b) -> b).map(BoltGeometry.Segment::b).orElse(fallback);
        }
    }

    public static void render(List<Frame> frames, RenderLevelStageEvent event) {
        if (frames.isEmpty()) return;
        List<LightningRender.BoltDraw> draws = new ArrayList<>();
        for (Frame frame : frames) {
            if (frame.event.sampled != null) draws.add(new LightningRender.BoltDraw(frame.event.sampled,
                    frame.event.recipe, frame.age + frame.event.recipe.progressionTicks));
        }
        PoseStack pose = new PoseStack();
        pose.last().pose().set(event.getPoseStack().last().pose());
        Vec3 camera = event.getCamera().getPosition();
        Runnable draw = () -> LightningRender.drawBatch(draws, pose, camera);
        if (!LightningShaderBridge.active() || !LightningShaderBridge.prepare(draw)) draw.run();
    }

    public static void release() {
        LightningHalo.release();
        LightningShaderBridge.release();
    }
}
