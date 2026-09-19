package org.confluence.mod.client.summoner.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BirdNestLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final NestRenderer renderer = new NestRenderer();

    public BirdNestLayer(PlayerRenderer renderer) {
        super(renderer);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!player.isInvisible() && !SummonerHelper.get(player).getEntityData().get(SummonerAttachmentEntityTypes.FINCH.get()).isEmpty()) {
            poseStack.pushPose();
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.translate(-0.5F, 0.2F, -0.5F);
            RenderType renderType = RenderType.entityCutoutNoCull(renderer.getTextureLocation(renderer.nestAnimatable));
            renderer.render(poseStack, renderer.nestAnimatable, bufferSource, renderType, bufferSource.getBuffer(renderType), packedLight);
            poseStack.popPose();
        }
    }

    private static final class NestRenderer extends GeoObjectRenderer<NestAnimatable> {
        private final NestAnimatable nestAnimatable = new NestAnimatable();

        private NestRenderer() {
            super(new GeoModel<>() {
                @Override
                public ResourceLocation getModelResource(NestAnimatable animatable) {
                    return Confluence.asResource("geo/" + "entity/summon/bird_nest" + ".geo.json");
                }

                @Override
                public ResourceLocation getTextureResource(NestAnimatable animatable) {
                    return Confluence.asResource("textures/" + "entity/summon/bird_nest" + ".png");
                }

                @Override
                public ResourceLocation getAnimationResource(NestAnimatable animatable) {
                    return Confluence.asResource("animations/" + "entity/summon/bird_nest" + ".animation.json");
                }
            });
        }
    }

    private static final class NestAnimatable implements GeoAnimatable {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }

        @Override
        public double getTick(Object object) {
            return 0;
        }
    }
}
