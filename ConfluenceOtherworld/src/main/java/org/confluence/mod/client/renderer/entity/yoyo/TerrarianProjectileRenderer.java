package org.confluence.mod.client.renderer.entity.yoyo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.common.entity.yoyo.TerrarianProjectile;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class TerrarianProjectileRenderer extends GeoEntityRenderer<TerrarianProjectile> {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/entity/yoyos.geo.json");
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/yoyos/terrarian_projectile.png");
    private static final Color COLOR = Color.ofRGBA(255, 255, 255, 255);

    public TerrarianProjectileRenderer(EntityRendererProvider.Context context) {
        super(context, new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(TerrarianProjectile projectile) {return MODEL;}

            @Override
            public ResourceLocation getTextureResource(TerrarianProjectile projectile) {return TEXTURE;}

            @Override
            public ResourceLocation getAnimationResource(TerrarianProjectile projectile) {return null;}
        });
        shadowRadius = 0;
    }

    @Override
    public Color getRenderColor(TerrarianProjectile projectile, float partialTick, int packedLight) {
        return COLOR;
    }

    @Override
    public RenderType getRenderType(TerrarianProjectile projectile, ResourceLocation texture, @Nullable MultiBufferSource buffers, float partialTick) {
        return RenderStateShardAccessor.entityGlow(texture);
    }

    @Override
    public void render(TerrarianProjectile projectile, float yaw, float partialTick, PoseStack poses, MultiBufferSource buffers, int light) {
        poses.pushPose();
        poses.translate(0, projectile.getBbHeight() * 0.5F, 0);
        poses.mulPose(Axis.XP.rotationDegrees((projectile.tickCount + partialTick) * 45));
        poses.translate(0, -0.5F, 0);
        super.render(projectile, yaw, partialTick, poses, buffers, 15728880);
        poses.popPose();
    }
}
