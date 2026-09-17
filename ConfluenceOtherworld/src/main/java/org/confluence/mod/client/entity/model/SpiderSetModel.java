package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.ClimbingSpider;
import software.bernie.geckolib.core.animation.AnimationState;

/// spider_set 中爬墙蜘蛛、黑隐士与丛林蜘蛛共用的骨架；各自使用素材提供的专用贴图。
public final class SpiderSetModel extends GeoNormalModel<ClimbingSpider> {
    private final ResourceLocation texture;

    public SpiderSetModel(String texturePath) {
        super(Confluence.asResource("wall_creeper"), false);
        texture = Confluence.asResource("textures/entity/" + texturePath + ".png");
    }

    @Override
    public ResourceLocation getTextureResource(ClimbingSpider entity) {
        return texture;
    }

    @Override
    public void setCustomAnimations(ClimbingSpider entity, long instanceId, AnimationState<ClimbingSpider> state) {
        super.setCustomAnimations(entity, instanceId, state);
        float amount = Math.min(1.0F, state.getLimbSwingAmount() * 2.0F);
        for (int leg = 1; leg <= 4; leg++) {
            float angle = Mth.sin(state.getLimbSwing() * 1.5F + leg * Mth.PI * 0.5F) * amount * 0.3F;
            setLeg("left_leg_" + leg, angle);
            setLeg("right_leg_" + leg, -angle);
        }
    }

    private void setLeg(String name, float angle) {
        getBone(name).ifPresent(bone -> {
            if (bone.getInitialSnapshot() != null) {
                bone.setRotY(bone.getInitialSnapshot().getRotY() + angle);
            }
        });
    }
}
