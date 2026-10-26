package org.confluence.mod.client.summoner.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.summoner.AttachmentEntityRenderDispatcher;
import org.confluence.mod.client.summoner.LyraRenderTypes;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.attachmentEntity.RenderKeyframe;
import org.confluence.mod.common.summoner.minion.TerraprismaMinion;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class TerraprismaTrailContext extends RibbonTrailContext<TerraprismaMinion> {

    private final Map<TerraprismaMinion, History> histories = new WeakHashMap<>();

    public TerraprismaTrailContext() {
        upOffset = 1.015f;
        downOffset = -0.5f;
        historyLength = 3;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<TerraprismaMinion> context) {
        if (!AttachmentEntityRenderDispatcher.isShadowPass() && context.color.getAlphaFloat() > 0f) {
            List<InterpolatedNode> nodes = buildSmoothNodes(context.entity, context.visualNode, context.partialTick);
            if (nodes.size() > 1) {
                double[] lengths = new double[nodes.size()];
                for (int i = 1; i < nodes.size(); i++) {
                    lengths[i] = lengths[i - 1] + nodes.get(i - 1).pos().distanceTo(nodes.get(i).pos());
                }
                double length = lengths[lengths.length - 1];
                List<Edge> edges = new ArrayList<>(nodes.size());
                for (int i = 0; i < nodes.size(); i++) {
                    float progress = length > 1.0E-6 ? (float) (lengths[i] / length) : (float) i / (nodes.size() - 1);
                    edges.add(edge(nodes.get(i), progress, context.color.argbInt(), context.color.getAlphaFloat()));
                }
                PoseStack.Pose pose = poseStack.last();
                Vec3 origin = context.visualNode.pos();
                VertexConsumer body = bufferSource.getBuffer(LyraRenderTypes.TERRAPRISMA_TRAIL);
                for (int i = 1; i < edges.size(); i++) {
                    Edge previous = edges.get(i - 1);
                    Edge current = edges.get(i);
                    quad(body, pose, origin, previous.tip, previous.base, current.base, current.tip);
                }
                VertexConsumer highlight = bufferSource.getBuffer(LyraRenderTypes.TERRAPRISMA_HIGHLIGHT);
                // 三次加色提交等价于原拖尾高亮的三倍强度，沿用现有顶点管线。
                for (int pass = 0; pass < 3; pass++) {
                    for (int i = 1; i < edges.size(); i++) {
                        Edge previous = edges.get(i - 1);
                        Edge current = edges.get(i);
                        quad(highlight, pose, origin, previous.highlightTip, previous.highlightBase, current.highlightBase, current.highlightTip);
                    }
                }
            }
        }
    }

    private Edge edge(InterpolatedNode node, float progress, int color, float opacity) {
        Vector3f direction = new Vector3f(0, 0, 1).rotate(node.rot());
        Vec3 axis = new Vec3(direction.x, direction.y, direction.z);
        Vec3 tip = node.pos().add(axis.scale(upOffset));
        float width = (upOffset - downOffset) * (1f - progress);
        float alpha = fadeOut.getFade(progress) * opacity;
        int bodyColor = packColor(color, alpha * 0.5f);
        int highlightColor = packColor(color, alpha * 0.35f);
        float margin = Math.min(0.008f, width * 0.15f);
        float height = Math.min(0.04f, width * 0.5f);
        return new Edge(
                new Vertex(tip, bodyColor, 0, progress),
                new Vertex(tip.subtract(axis.scale(width)), bodyColor, 1, progress),
                new Vertex(tip.subtract(axis.scale(margin)), highlightColor, 0, progress),
                new Vertex(tip.subtract(axis.scale(margin + height)), highlightColor, 1, progress));
    }

    private void quad(VertexConsumer buffer, PoseStack.Pose pose, Vec3 origin, Vertex tip, Vertex base, Vertex nextBase, Vertex nextTip) {
        vertex(buffer, pose, origin, tip);
        vertex(buffer, pose, origin, base);
        vertex(buffer, pose, origin, nextBase);
        vertex(buffer, pose, origin, nextTip);
    }

    private void vertex(VertexConsumer buffer, PoseStack.Pose pose, Vec3 origin, Vertex vertex) {
        Vec3 pos = vertex.pos.subtract(origin);
        buffer.vertex(pose.pose(), (float) pos.x, (float) pos.y, (float) pos.z)
                .color(vertex.color).uv(vertex.u, vertex.v)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
    }

    @Override
    protected List<InterpolatedNode> buildSmoothNodes(TerraprismaMinion entity, PathNode visualNode, float partialTick) {
        History history = histories.computeIfAbsent(entity, key -> new History());
        int tick = entity.getTickCount();
        if (tick != history.serverTick) {
            if (tick < history.serverTick) {
                history.samples.clear();
            }
            List<RenderKeyframe> frames = entity.getRenderKeyframes();
            if (frames.isEmpty()) {
                List<PathNode> nodes = entity.getHistoryNodes();
                frames = List.of(new RenderKeyframe(0f, nodes.get(Math.min(1, nodes.size() - 1))), new RenderKeyframe(1f, entity.getCurrentPathNode()));
            }
            for (RenderKeyframe frame : frames) {
                double time = tick - 1.0 + frame.partialTick();
                if (!history.samples.isEmpty() && history.samples.getLast().time == time) {
                    history.samples.removeLast();
                }
                history.samples.addLast(new Sample(time, frame.node()));
            }
            while (!history.samples.isEmpty() && history.samples.getFirst().time < tick - historyLength - 1) {
                history.samples.removeFirst();
            }
            history.serverTick = tick;
            history.clientTick = entity.getOwner().tickCount;
        }
        double time = tick - 1.0 + Math.min(1f, entity.getOwner().tickCount - history.clientTick + partialTick);
        double tail = time - historyLength;
        List<InterpolatedNode> nodes = new ArrayList<>();
        nodes.add(new InterpolatedNode(visualNode.pos(), visualNode.toQuaternion()));
        Sample newer = new Sample(time, visualNode);
        double length = 0;
        var samples = history.samples.descendingIterator();
        while (samples.hasNext()) {
            Sample sample = samples.next();
            if (sample.time < time) {
                boolean end = sample.time < tail;
                PathNode node = end ? sample.node.lerp(newer.node, (float) ((tail - sample.time) / (newer.time - sample.time))) : sample.node;
                double distance = newer.node.pos().distanceTo(node.pos());
                if (length + distance > 12) {
                    node = newer.node.lerp(node, (float) ((12 - length) / distance));
                    end = true;
                }
                nodes.add(new InterpolatedNode(node.pos(), node.toQuaternion()));
                if (end) {
                    break;
                }
                length += distance;
                newer = sample;
            }
        }
        return nodes;
    }

    private static class History {
        private final ArrayDeque<Sample> samples = new ArrayDeque<>();
        private int serverTick = -1;
        private int clientTick;
    }

    private record Sample(double time, PathNode node) {
    }

    private record Vertex(Vec3 pos, int color, float u, float v) {
    }

    private record Edge(Vertex tip, Vertex base, Vertex highlightTip, Vertex highlightBase) {
    }
}
