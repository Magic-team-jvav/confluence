package org.confluence.terraentity.client.boss.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.init.model.WhipModelRegister;
import org.confluence.terraentity.entity.boss.thetwins.TheTwins;
import org.confluence.terraentity.init.item.TEWhipItems;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TheTwinsRenderer extends EntityRenderer<TheTwins> {

    static ResourceLocation empty = TerraEntity.space("");
    ItemStack stack = TEWhipItems.AMBER_WHIP.toStack();
    public TheTwinsRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TheTwins entity) {
        return empty;
    }



    @Override
    public void render(@NotNull TheTwins entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if(entity.spazmatism!= null && entity.spazmatism.isAlive() && entity.retinazer!= null && entity.retinazer.isAlive()) {
            poseStack.pushPose();
            Vec3 curPos = entity.getPosition(partialTick).add(0, -entity.spazmatism.getBbHeight() * 0.5f, 0);
            Vec3 from = entity.spazmatism.getPosition(partialTick);
            Vec3 to = entity.retinazer.getPosition(partialTick);


            Vec3 delta1 = from.subtract(curPos);
            poseStack.translate(delta1.x, delta1.y, delta1.z);

            Vec3 dir = to.subtract(from);
            poseStack.mulPose(new Quaternionf().rotateTo(new Vector3f(0,1,0), dir.toVector3f()));

            double length = dir.length();
            poseStack.mulPose(Axis.YN.rotation((entity.tickCount + partialTick) * 0.1f));
            for(double i = 0; i < length - 1; i+= 1) {
                poseStack.translate(0,1,0);
                poseStack.mulPose(Axis.YN.rotation(0.1f));

                ModelResourceLocation modelResourceLocation = WhipModelRegister.getInstance().getModelResourceLocation(stack.getItem());
                if(modelResourceLocation != null){
                    // 当是模型的时候
                    BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelResourceLocation);
                    for (RenderType rendertype : model.getRenderTypes(stack, false)) {
                        VertexConsumer vertexconsumer = ItemRenderer.getFoilBuffer(bufferSource, rendertype, false, stack.isEnchanted());
                        Minecraft.getInstance().getItemRenderer().renderModelLists(
                                model, stack, packedLight, OverlayTexture.NO_OVERLAY,
                                poseStack, vertexconsumer);
                    }
                }else{
                    Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.BAMBOO.defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
                }

            }


            poseStack.popPose();
        }

    }
}
