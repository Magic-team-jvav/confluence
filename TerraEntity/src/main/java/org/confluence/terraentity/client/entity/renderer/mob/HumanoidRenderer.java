package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.confluence.terraentity.api.entity.animation.IUseItemAnimatable;
import org.confluence.terraentity.client.entity.layer.ArmorLayer;
import org.confluence.terraentity.client.entity.layer.BootArmorLayer;
import org.confluence.terraentity.client.entity.layer.ItemInHandLayer;
import org.confluence.terraentity.client.entity.model.GeoHumanoidModel;
import org.confluence.terraentity.client.entity.renderer.AnimatorRenderer;
import org.confluence.terraentity.client.util.DefaultBoneBoundIdents;
import org.confluence.terraentity.entity.animation.BoneStates;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import static org.confluence.terraentity.client.entity.layer.ItemInHandLayer.RIGHT_HAND;


/**
 * 人形怪渲染器，渲染手持物品、盔甲、使用弓、弩的硬编码动画和插值。需要骨骼符合命名要求{@link DefaultBoneBoundIdents 预定义骨骼名表}。
 * @param <T> 实体类型
 */
public class HumanoidRenderer<T extends Mob & GeoEntity & IUseItemAnimatable<BoneStates>> extends AnimatorRenderer<T> {

    protected float rightBoneRotX;
    private final BootArmorLayer<T> bootArmorLayer;

    public HumanoidRenderer(EntityRendererProvider.Context renderManager, GeoHumanoidModel<T> model, boolean ifRotX, float scale, float offsetY) {
        super(renderManager, model, ifRotX, scale, offsetY);
        this.addRenderLayer(new ArmorLayer<>(this));
        this.addRenderLayer(this.bootArmorLayer = new BootArmorLayer<>(this));
        this.addRenderLayer(new ItemInHandLayer<>(this));

    }

    public HumanoidRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        this(renderManager, path, 1.0F, 0.0F);
    }

    public HumanoidRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, float scale, float offsetY){
        this(renderManager, new GeoHumanoidModel<>(path), false, scale, offsetY);
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, @org.jetbrains.annotations.Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if(!isReRender){
            if(animatable.isLieDown()){
                poseStack.mulPose(Axis.XP.rotationDegrees(-90F));
                poseStack.translate(0,-1,0);
            }

            model.getBone(RIGHT_HAND).ifPresent(b->{
                rightBoneRotX = b.getRotX();
            });
        }
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

    }

    /**
     * 当模型有Boot时，就不需要BootLayer了
     */
    public HumanoidRenderer<T> removeBootLayer(){
        this.getRenderLayers().remove(bootArmorLayer);
        return this;
    }
}
