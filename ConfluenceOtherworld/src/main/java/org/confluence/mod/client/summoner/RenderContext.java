package org.confluence.mod.client.summoner;

import org.confluence.mod.client.summoner.trail.TrailContext;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import software.bernie.geckolib.util.Color;

/// 单次附件实体渲染共享的模型、拖尾、颜色和光照上下文。
public class RenderContext<T extends AttachmentEntity> {

    public final T entity;
    public final PathNode visualNode;
    public final float partialTick;

    public int packedLight;
    public Color color = Color.WHITE;
    public TrailContext<T> trail;
    public ModelContext model = new ModelContext();

    public RenderContext(T entity, PathNode visualNode, float partialTick, int packedLight) {
        this.entity = entity;
        this.visualNode = visualNode;
        this.partialTick = partialTick;
        this.packedLight = packedLight;
    }

    public RenderContext<T> packedLight(int packedLight) {
        this.packedLight = packedLight;
        return this;
    }

    public RenderContext<T> color(Color color) {
        this.color = color;
        return this;
    }

    public RenderContext<T> color(int argb) {
        this.color = Color.ofRGBA(
                ((argb >> 16) & 0xFF) / 255.0F,
                ((argb >> 8) & 0xFF) / 255.0F,
                (argb & 0xFF) / 255.0F,
                ((argb >> 24) & 0xFF) / 255.0F
        );
        return this;
    }

    public RenderContext<T> trail(TrailContext<T> trail) {
        this.trail = trail;
        return this;
    }

    public RenderContext<T> model(ModelContext model) {
        this.model = model;
        return this;
    }

    public boolean hasTrail() {
        return trail != null && trail.timer > 0;
    }

    @FunctionalInterface
    public interface ColorFunction<T extends AttachmentEntity> {
        int getColor(T entity, float progress, float partialTick);
    }

    @FunctionalInterface
    public interface FadeFunction {
        float getFade(float progress);
    }

    @FunctionalInterface
    public interface AlphaBoostFunction<T extends AttachmentEntity> {
        float getBoost(T entity, float progress);
    }

    @FunctionalInterface
    public interface BrightnessBoostFunction<T extends AttachmentEntity> {
        float getBoost(T entity, float progress);
    }
}
