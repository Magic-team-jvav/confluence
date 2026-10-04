package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.mod.common.entity.npc.TownSlimeRescue;

/// 旧摇摇箱使用普通木箱外观，不借用宝箱怪模型。
public final class OldShakingChestRenderer extends EntityRenderer<TownSlimeRescue> {
    private final ItemRenderer itemRenderer;
    private final ItemStack chest = new ItemStack(Items.CHEST);

    public OldShakingChestRenderer(EntityRendererProvider.Context context) {
        super(context);
        itemRenderer = context.getItemRenderer();
        shadowRadius = 0.4F;
    }

    @Override
    public void render(TownSlimeRescue entity, float yaw, float partialTick, PoseStack poses,
                       MultiBufferSource buffers, int light) {
        if (!entity.isInvisible()) {
            poses.pushPose();
            poses.mulPose(Axis.YP.rotationDegrees(180.0F - Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot())));
            poses.translate(0.0, 0.5, 0.0);
            itemRenderer.renderStatic(chest, ItemDisplayContext.NONE, light, LivingEntityRenderer.getOverlayCoords(entity, 0.0F),
                    poses, buffers, entity.level(), entity.getId());
            poses.popPose();
        }
        super.render(entity, yaw, partialTick, poses, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(TownSlimeRescue entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
