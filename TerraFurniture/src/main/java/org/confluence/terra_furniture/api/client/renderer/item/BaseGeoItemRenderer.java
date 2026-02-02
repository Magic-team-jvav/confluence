
package org.confluence.terra_furniture.api.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.function.Consumer;

/**
 * 基础的Geo物品渲染器，提供默认的Geo可渲染物品手持显示，如Geo方块实体模型对应物
 */
public class BaseGeoItemRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {
    private final Consumer<BakedGeoModel> process;
    private final boolean isNegative;
    public BaseGeoItemRenderer(GeoModel<T> model, Consumer<BakedGeoModel> process, boolean isNegative) {
        super(model);
        this.process = process;
        this.isNegative = isNegative;
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        process.accept(model);
        if(isReRender) return;
        super.preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    @Nullable
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return this.isNegative ? RenderType.text(texture) : super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}

