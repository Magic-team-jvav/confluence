package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.BaseSlimeModel;
import org.confluence.mod.common.entity.monster.slime.BaseSlime;

/// 泰拉瑞亚史莱姆族的通用渲染器。
///
/// 内核、面部和半透明外壳共用同一套缩放与挤压数据；具体实体可以声明自发光，
/// 渲染器不根据注册名猜测变体。这里只读取客户端视觉状态，不参与移动、碰撞或伤害结算。
public final class BaseSlimeRenderer<T extends BaseSlime> extends MobRenderer<T, BaseSlimeModel<T>> {
    private final ResourceLocation texture;

    public BaseSlimeRenderer(EntityRendererProvider.Context context, String textureName) {
        super(context, new BaseSlimeModel<>(context.bakeLayer(BaseSlimeModel.INNER_LAYER)), 0.25F);
        this.texture = Confluence.asResource("textures/entity/slime/slime_" + textureName + ".png");
        addLayer(new BaseSlimeOuterLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(T slime) {
        return texture;
    }

    @Override
    protected int getBlockLightLevel(T slime, BlockPos pos) {
        return slime.isFullBright() ? 15 : super.getBlockLightLevel(slime, pos);
    }

    @Override
    protected void scale(T slime, PoseStack poseStack, float partialTick) {
        float size = slime.getVisualSize();
        shadowRadius = 0.25F * size;
        poseStack.scale(0.999F, 0.999F, 0.999F);
        poseStack.translate(0.0F, 0.001F, 0.0F);
        float squish = Mth.lerp(partialTick, slime.getOldSquish(), slime.getSquish())
                / (size * 0.5F + 1.0F);
        float inverse = 1.0F / (squish + 1.0F);
        poseStack.scale(inverse * size, size / inverse, inverse * size);
    }
}
