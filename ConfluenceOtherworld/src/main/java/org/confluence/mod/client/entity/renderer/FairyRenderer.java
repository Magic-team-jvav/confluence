package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.CritterGeoModel;
import org.confluence.mod.common.entity.animal.BaseCritter;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

/// 妖精类生物使用的全亮负体积渲染器。
///
/// <p>主体始终按全亮光照绘制，轮廓骨骼由父类的独立重绘层处理。构造器同时接受固定资源路径
/// 和动态模型，以便变体生物根据自身状态选择模型与纹理。</p>
public class FairyRenderer<T extends Entity & GeoEntity> extends GeoNegativeVolumeRenderer<T> {
    public FairyRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        super(context, path);
    }

    public FairyRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    }

    /// 为模型和纹理均由实体变体动态提供的小动物创建仙灵渲染器。
    ///
    /// <p>占位资源只用于初始化 GeckoLib 模型对象，实际渲染时会由
    /// {@link CritterGeoModel} 查询实体当前的模型与纹理。集中在这里可以避免
    /// 客户端注册表把占位键误认为必须存在的最终资源文件。</p>
    public static <T extends BaseCritter> FairyRenderer<T> forCritter(EntityRendererProvider.Context context) {
        return new FairyRenderer<>(context, new CritterGeoModel<>(Confluence.asResource("geo/animal/dummy")));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model,
                               RenderType renderType, MultiBufferSource bufferSource,
                               VertexConsumer buffer, boolean isReRender, float partialTick,
                               int packedLight, int packedOverlay, float red, float green,
                               float blue, float alpha) {
        int effectiveLight = isReRender ? packedLight : 0xF000F0;
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, effectiveLight, packedOverlay, red, green, blue, alpha);
    }
}
