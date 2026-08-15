package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.BaseSlimeModel;
import org.confluence.mod.common.entity.monster.slime.BaseSlime;

/**
 * 普通泰拉瑞亚史莱姆渲染器。
 *
 * <p>内核、面部和半透明外壳复刻 1.21 侧使用的原版史莱姆渲染结构。实体仍保留 1.20
 * 重写后的行为树与属性系统，客户端只读取视觉尺寸和挤压进度，不依赖原版 {@code Slime}
 * 实体类。</p>
 */
public final class BaseSlimeRenderer<T extends BaseSlime>
        extends MobRenderer<T, BaseSlimeModel<T>> {
    private final ResourceLocation texture;

    public BaseSlimeRenderer(EntityRendererProvider.Context context, String textureName) {
        super(context,
                new BaseSlimeModel<>(context.bakeLayer(BaseSlimeModel.INNER_LAYER)),
                0.25F);
        this.texture = Confluence.asResource(
                "textures/entity/slime/slime_" + textureName + ".png");
        addLayer(new BaseSlimeOuterLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(T slime) {
        return texture;
    }

    @Override
    protected void scale(T slime, PoseStack poseStack, float partialTick) {
        float size = slime.getVisualSize();
        shadowRadius = 0.25F * size;
        float squish = Mth.lerp(partialTick, slime.getOldSquish(), slime.getSquish())
                / (size * 0.5F + 1.0F);
        float inverse = 1.0F / (squish + 1.0F);
        poseStack.scale(inverse * size, size / inverse, inverse * size);
    }
}
