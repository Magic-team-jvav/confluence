package org.confluence.mod.client.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BowItem;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.entity.NpcEntities;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

/// 保留 NPC 自身 Geo 移动动画，仅叠加手持、拉弓和挥击姿势。
public final class NPCHumanoidGeoModel<T extends BaseNPC> extends GeoNormalModel<T> {
    private final ResourceLocation shimmerTexture;
    private final ResourceLocation shimmerModel;
    private final ResourceLocation shimmerAnimation;

    public NPCHumanoidGeoModel(ResourceLocation path) {
        super(path);
        shimmerTexture = path.withPrefix("textures/entity/").withSuffix("_shimmer.png");
        shimmerModel = path.withPrefix("geo/entity/").withSuffix("_shimmer.geo.json");
        shimmerAnimation = path.withPrefix("animations/entity/").withSuffix("_shimmer.animation.json");
    }

    /// 微光外观可只提供同布局贴图，或提供完整模型、贴图与动画；不使用缺资源的半套模型。
    @Override
    public ResourceLocation getTextureResource(T npc) {
        if (npc != null && npc.isShimmered() && hasResource(shimmerTexture)) {
            boolean model = hasResource(shimmerModel);
            boolean animation = hasResource(shimmerAnimation);
            if (model == animation) return shimmerTexture;
        }
        return super.getTextureResource(npc);
    }

    /// 普通外观与仅换贴图的变种继续共用原模型，完整微光资源才切换几何。
    @Override
    public ResourceLocation getModelResource(T npc) {
        return hasShimmerGeometry(npc) ? shimmerModel : super.getModelResource(npc);
    }

    /// 变种动画与变种几何成套使用，避免旧动画驱动不匹配的骨骼。
    @Override
    public ResourceLocation getAnimationResource(T npc) {
        return hasShimmerGeometry(npc) ? shimmerAnimation : super.getAnimationResource(npc);
    }

    private boolean hasShimmerGeometry(T npc) {
        return npc != null && npc.isShimmered() && hasResource(shimmerTexture)
                && hasResource(shimmerModel) && hasResource(shimmerAnimation);
    }

    /// 查询当前资源管理器，资源重载后即时生效，不缓存失效的“缺图”结论。
    private static boolean hasResource(ResourceLocation resource) {
        return Minecraft.getInstance().getResourceManager().getResource(resource).isPresent();
    }

    @Override
    public void setCustomAnimations(T npc, long instanceId, AnimationState<T> state) {
        super.setCustomAnimations(npc, instanceId, state);
        if (npc.getType() == NpcEntities.TAX_COLLECTOR.get()) return;
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
