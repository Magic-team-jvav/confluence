package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.WormPartGeoModel;
import org.confluence.mod.common.entity.monster.BaseWormMonster;
import org.confluence.mod.common.entity.monster.BaseWormPart;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/// 在同一个体节实体类型上选择各蠕虫家族的模型，并处理飞龙模型内部的体节分组。
///
/// <p>普通蠕虫分别使用身体和尾部文件；飞龙则与 1.21 实现一致，复用 {@code wyvern.geo.json}，
/// 根据体节位置仅显示普通身体、带翼身体或尾部中的一个分组。</p>
public final class WormPartRenderer extends GeoNormalRenderer<BaseWormPart> {
    private final WormPartGeoModel<BaseWormPart> wormModel;

    public WormPartRenderer(EntityRendererProvider.Context context) {
        this(context, new WormPartGeoModel<>(
                Confluence.asResource("geo/entity/giant_worm_segment.geo.json"),
                Confluence.asResource("textures/entity/giant_worm_segment.png"),
                Confluence.asResource("geo/entity/giant_worm_tail.geo.json"),
                Confluence.asResource("textures/entity/giant_worm_tail.png")));
    }

    private WormPartRenderer(EntityRendererProvider.Context context, WormPartGeoModel<BaseWormPart> model) {
        super(context, model);
        this.wormModel = model;
    }

    @Override
    public RenderType getRenderType(BaseWormPart segment, ResourceLocation texture, @Nullable MultiBufferSource buffers, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void preRender(
            PoseStack poseStack,
            BaseWormPart segment,
            BakedGeoModel model,
            MultiBufferSource buffers,
            VertexConsumer buffer,
            boolean reRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        if (wormModel.usesWyvernGeometry(segment)) {
            boolean tail = segment.isTail();
            boolean wing = !tail && (segment.getSegmentIndex() == 3 || segment.getSegmentIndex() == 9);
            setHidden(model, "Bone", true);
            setHidden(model, "Bone2", tail || wing);
            setHidden(model, "Bone3", tail || !wing);
            setHidden(model, "Bone4", !tail);

            BaseWormMonster owner = segment.getOwner();
            ResourceLocation ownerId = owner == null
                    ? null
                    : BuiltInRegistries.ENTITY_TYPE.getKey(owner.getType());
            if (ownerId != null && "arch_wyvern".equals(ownerId.getPath())) {
                poseStack.scale(1.25F, 1.25F, 1.25F);
            }
        }
        super.preRender(poseStack, segment, model, buffers, buffer, reRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private static void setHidden(BakedGeoModel model, String name, boolean hidden) {
        model.getBone(name).ifPresent(bone -> bone.setHidden(hidden));
    }
}
