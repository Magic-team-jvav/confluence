package org.confluence.mod.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.Confluence;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ReplacedSpiderRenderer<T extends LivingEntity> extends GeoReplacedEntityRenderer<T, ReplacedSpiderRenderer.SpiderReplacement> {
    public ReplacedSpiderRenderer(EntityRendererProvider.Context renderManager, String texture, EntityType<? extends LivingEntity> type) {
        super(renderManager, getModel(texture), new SpiderReplacement(type));
    }

    private static GeoModel<SpiderReplacement> getModel(String texture) {
        ResourceLocation textureLocation = Confluence.asResource("textures/entity/replaced/" + texture + ".png");
        return new GeoModel<>() {
            private static final ResourceLocation MODEL = Confluence.asResource("geo/entity/spider.geo.json");
            private static final ResourceLocation ANIMATION = Confluence.asResource("animations/entity/spider.animation.json");

            @Override
            public ResourceLocation getModelResource(SpiderReplacement animatable) {
                return MODEL;
            }

            @Override
            public ResourceLocation getTextureResource(SpiderReplacement animatable) {
                return textureLocation;
            }

            @Override
            public ResourceLocation getAnimationResource(SpiderReplacement animatable) {
                return ANIMATION;
            }
        };
    }

    public static class SpiderReplacement implements GeoReplacedEntity {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
        private final EntityType<? extends LivingEntity> type;

        public SpiderReplacement(EntityType<? extends LivingEntity> type) {
            this.type = type;
        }

        @Override
        public EntityType<?> getReplacingEntityType() {
            return type;
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            controllers.add(new AnimationController<>(this, state -> {
                var entity = state.getData(DataTickets.ENTITY);
                if (entity instanceof LivingEntity spider) {
                    if (!spider.onClimbable() && !spider.onGround()) {
                        return state.setAndContinue(DefaultAnimations.JUMP);
                    } else if (state.isMoving() && spider.getDeltaMovement().lengthSqr() > 0.115 * 0.115) {
                        return state.setAndContinue(DefaultAnimations.RUN);
                    }
                }
                if (state.isMoving()) {
                    return state.setAndContinue(DefaultAnimations.WALK);
                } else {
                    return state.setAndContinue(DefaultAnimations.IDLE);
                }
            }));
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }
    }
}
