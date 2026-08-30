package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BowItem;
import org.confluence.mod.common.entity.npc.BaseNPC;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

/// 保留 NPC 自身 Geo 移动动画，仅叠加手持、拉弓和挥击姿势。
public final class NPCHumanoidGeoModel<T extends BaseNPC> extends GeoNormalModel<T> {
    public NPCHumanoidGeoModel(ResourceLocation path) {
        super(path);
    }

    @Override
    public void setCustomAnimations(T npc, long instanceId, AnimationState<T> state) {
        super.setCustomAnimations(npc, instanceId, state);
        CoreGeoBone right = getAnimationProcessor().getBone("RightArm");
        CoreGeoBone left = getAnimationProcessor().getBone("LeftArm");
        if (right == null || left == null) return;

        float partialTick = state.getPartialTick();
        if (npc.isUsingItem() && npc.getUseItem().getItem() instanceof BowItem) {
            float progress = Mth.clamp((npc.getTicksUsingItem() + partialTick) / 5.0F, 0.0F, 1.0F);
            float pitch = Mth.lerp(partialTick, npc.xRotO, npc.getXRot()) * Mth.DEG_TO_RAD;
            float yaw = Mth.lerp(partialTick, npc.yBodyRotO - npc.yHeadRotO,
                    npc.yBodyRot - npc.yHeadRot) * Mth.DEG_TO_RAD;
            right.setRotX(Mth.lerp(progress, right.getRotX(), 1.5F - pitch));
            right.setRotY(Mth.lerp(progress, right.getRotY(), yaw));
            left.setRotX(Mth.lerp(progress, left.getRotX(), 1.3F - pitch));
            left.setRotY(Mth.lerp(progress, left.getRotY(), Math.max(yaw - 0.5F, -1.4F)));
            return;
        }

        float attack = npc.getAttackAnim(partialTick);
        if (attack > 0.0F) {
            float strike = Mth.sin(Mth.sqrt(attack) * Mth.PI);
            right.setRotX(right.getRotX() - strike * 1.2F);
            right.setRotY(right.getRotY() + strike * 0.25F);
        } else if (!npc.getMainHandItem().isEmpty()) {
            right.setRotX(0.3F + right.getRotX() * 0.5F);
        }
    }
}
