package org.confluence.terraentity.client.entity.renderer.mob;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Spider;
import org.confluence.terraentity.TerraEntity;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

@SuppressWarnings("removal")
public class ReplacedSpiderRenderer<T extends Spider> extends GeoReplacedEntityRenderer<T, ReplacedSpiderRenderer.Entity> {
    public ReplacedSpiderRenderer(EntityRendererProvider.Context renderManager, String texture, EntityType<? extends Spider> type) {
        super(renderManager, getModel(texture), new Entity(type));
    }

    private static GeoModel<Entity> getModel(String texture) {
        ResourceLocation TEXTURE = TerraEntity.space("textures/entity/replaced/" + texture + ".png");
        return new GeoModel<>() {
            private static final ResourceLocation MODEL = TerraEntity.space("geo/entity/spider.geo.json");
            private static final ResourceLocation ANIMATION = TerraEntity.space("animations/entity/spider.animation.json");

            @Override
            public ResourceLocation getModelResource(Entity animatable) {
                return MODEL;
            }

            @Override
            public ResourceLocation getTextureResource(Entity animatable) {
                return TEXTURE;
            }

            @Override
            public ResourceLocation getAnimationResource(Entity animatable) {
                return ANIMATION;
            }
        };
    }

    public static class Entity implements GeoReplacedEntity {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
        private final EntityType<? extends Spider> type;

        public Entity(EntityType<? extends Spider> type) {
            this.type = type;
        }

        @Override
        public EntityType<?> getReplacingEntityType() {
            return type;
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            controllers.add(new AnimationController<>(this, state -> {
                net.minecraft.world.entity.Entity entity = state.getData(DataTickets.ENTITY);
                if (entity instanceof Spider spider) {
                    if (!spider.isClimbing() && !spider.onGround()) {
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
