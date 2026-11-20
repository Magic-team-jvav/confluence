package org.confluence.mod.client.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class EmpPulseRenderer {
    // 球面短暂标出本轮命中边界；中心固定在服务端施法时的位置。
    private static final int DISPLAY_TICKS = 20;
    private static final List<Pulse> pulses = new ArrayList<>();
    private static final List<Line> mesh = buildMesh();

    private record Pulse(Vec3 center, float radius, long start) {}

    private record Line(Vec3 a, Vec3 b, Vec3 normal) {
        Line(Vec3 a, Vec3 b) {this(a, b, b.subtract(a).normalize());}
    }

    public static void show(Vec3 center, float radius, long tick) {
        pulses.add(new Pulse(center, radius, tick));
    }

    public static void clear() {
        pulses.clear();
    }

    public static void render(PoseStack poses, Vec3 camera, Minecraft minecraft) {
        if (pulses.isEmpty() || minecraft.level == null) return;
        long now = minecraft.level.getGameTime();
        pulses.removeIf(pulse -> now - pulse.start >= DISPLAY_TICKS);
        if (pulses.isEmpty()) return;
        var buffers = minecraft.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());
        for (Pulse pulse : pulses) {
            int alpha = (int) (220 * (DISPLAY_TICKS - (now - pulse.start)) / DISPLAY_TICKS);
            poses.pushPose();
            poses.translate(pulse.center.x - camera.x, pulse.center.y - camera.y, pulse.center.z - camera.z);
            poses.scale(pulse.radius, pulse.radius, pulse.radius);
            for (Line line : mesh) {
                vertex(lines, poses.last(), line.a, line.normal, alpha);
                vertex(lines, poses.last(), line.b, line.normal, alpha);
            }
            poses.popPose();
        }
        buffers.endBatch(RenderType.lines());
    }

    private static List<Line> buildMesh() {
        List<Line> lines = new ArrayList<>();
        for (int lat = 0; lat < 24; lat++) {
            for (int lon = 0; lon < 48; lon++) {
                Vec3 point = point(lat, lon);
                if (lon % 4 == 0) lines.add(new Line(point, point(lat + 1, lon)));
                if (lat > 0 && lat % 3 == 0) lines.add(new Line(point, point(lat, lon + 1)));
            }
        }
        return List.copyOf(lines);
    }

    private static Vec3 point(int latitude, int longitude) {
        double lat = Math.PI * (latitude / 24.0 - 0.5);
        double lon = Math.PI * longitude / 24.0;
        return new Vec3(Math.cos(lat) * Math.cos(lon), Math.sin(lat), Math.cos(lat) * Math.sin(lon));
    }

    private static void vertex(VertexConsumer lines, PoseStack.Pose pose, Vec3 point, Vec3 normal, int alpha) {
        lines.vertex(pose.pose(), (float) point.x, (float) point.y, (float) point.z).color(110, 223, 255, alpha)
                .normal(pose.normal(), (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
    }
}
