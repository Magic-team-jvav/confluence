package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Animal;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.animal.Cluckshroom;
import org.confluence.mod.common.entity.animal.GlowingMooshroom;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;

/**
 * 占位动画的牛、鸡模型使用各自的步态；蘑菇装饰保持父骨骼姿态。
 */
public final class MushroomAnimalModel<T extends Animal & GeoEntity> extends GeoNormalModel<T> {
    public MushroomAnimalModel(ResourceLocation path) {
        super(path, false);
    }

    @Override
    public ResourceLocation getTextureResource(T entity) {
        if (entity instanceof Cluckshroom chicken) {
            if (chicken.isGlowing())
                return Confluence.asResource("textures/entity/animal/cluckshroom/glowing.png");
            if (chicken.isBrown())
                return Confluence.asResource("textures/entity/animal/cluckshroom/brown.png");
        }
        return super.getTextureResource(entity);
    }

    @Override
    public void setCustomAnimations(T entity, long instanceId, AnimationState<T> state) {
        var look = state.getData(DataTickets.ENTITY_MODEL_DATA);
        getBone("head").ifPresent(head -> {
            var initial = head.getInitialSnapshot();
            // 牛的父骨骼绕 X 轴旋转了 -90°，局部 Z 轴才对应实体的水平转头轴。
            head.setRotX(initial.getRotX() + look.headPitch() * Mth.DEG_TO_RAD);
            if (entity instanceof GlowingMooshroom) {
                head.setRotY(initial.getRotY());
                head.setRotZ(initial.getRotZ() + look.netHeadYaw() * Mth.DEG_TO_RAD);
            } else {
                head.setRotY(initial.getRotY() + look.netHeadYaw() * Mth.DEG_TO_RAD);
                head.setRotZ(initial.getRotZ());
            }
        });
        float swing = Mth.cos(state.getLimbSwing() * 0.6662F) * 1.4F * state.getLimbSwingAmount();
        getBone("leg0").ifPresent(leg -> leg.setRotX(swing));
        getBone("leg1").ifPresent(leg -> leg.setRotX(-swing));
        getBone("leg2").ifPresent(leg -> leg.setRotX(-swing));
        getBone("leg3").ifPresent(leg -> leg.setRotX(swing));
        if (entity instanceof Cluckshroom chicken) {
            float flap = (Mth.sin(Mth.lerp(state.getPartialTick(), chicken.oFlap, chicken.flap)) + 1.0F) * Mth.lerp(state.getPartialTick(), chicken.oFlapSpeed, chicken.flapSpeed);
            getBone("wing0").ifPresent(wing -> wing.setRotZ(flap));
            getBone("wing1").ifPresent(wing -> wing.setRotZ(-flap));
        }
    }
}
