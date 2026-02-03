package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.client.handler.bestiary.ClientBestiaryEntry;
import org.confluence.mod.common.block.functional.enemybanner.AbstractEnemyBannerBlock;
import org.confluence.mod.common.block.functional.enemybanner.EnemyBannerBlock;
import org.confluence.mod.common.block.functional.enemybanner.WallEnemyBannerBlock;

public class EnemyBannerBlockRenderer implements BlockEntityRenderer<AbstractEnemyBannerBlock.BEntity> {
    private static final RenderType RENDER_TYPE = RenderType.entitySolid(Confluence.asResource("textures/entity/enemy_banner"));
    private final ModelPart flag;
    private final ModelPart pole;
    private final ModelPart bar;

    public EnemyBannerBlockRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart part = context.bakeLayer(ModelLayers.BANNER);
        this.flag = part.getChild("flag");
        this.pole = part.getChild("pole");
        this.bar = part.getChild("bar");
    }

    @Override
    public void render(AbstractEnemyBannerBlock.BEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float scale = 2.0F / 3.0F;
        poseStack.pushPose();
        if (blockEntity.getLevel() == null) {
            poseStack.translate(0.5F, 0.5F, 0.5F);
            pole.visible = true;
        } else {
            BlockState blockstate = blockEntity.getBlockState();
            if (blockstate.getBlock() instanceof EnemyBannerBlock) {
                poseStack.translate(0.5F, 0.5F, 0.5F);
                float yaw = -RotationSegment.convertToDegrees(blockstate.getValue(BannerBlock.ROTATION));
                poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
                pole.visible = true;
            } else {
                poseStack.translate(0.5F, -0.16666667F, 0.5F);
                float yaw = -blockstate.getValue(WallEnemyBannerBlock.FACING).toYRot();
                poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
                poseStack.translate(0.0F, -0.3125F, -0.4375F);
                pole.visible = false;
            }
        }

        poseStack.pushPose();
        poseStack.scale(scale, -scale, -scale);
        VertexConsumer consumer = bufferSource.getBuffer(RENDER_TYPE);
        pole.render(poseStack, consumer, packedLight, packedOverlay);
        bar.render(poseStack, consumer, packedLight, packedOverlay);
        flag.xRot = 0;
        flag.y = -32.0F;
        flag.render(poseStack, consumer, packedLight, packedOverlay);
        ClientBestiaryEntry entry = ClientBestiary.getInstance().getEntry(blockEntity.entryKey);
        if (entry != null) {
            LivingEntity living = entry.getRenderedEntity(Minecraft.getInstance().level);
            if (living != null) {
                poseStack.pushPose();
                scale = (float) living.getBoundingBox().getSize() * 0.85F;
                poseStack.scale(scale, scale, 0.02F);
                float y = -living.getBbHeight();
                poseStack.translate(0, y, -6.5F);
                poseStack.mulPose(Axis.XP.rotation(0.1F).rotateY(Mth.PI * 1.25F).rotateZ(Mth.PI));
                if (y > -1.6F) {
                    y -= y * 0.5F;
                }
                float x = living.getBbWidth() * 0.2F;
                poseStack.translate(x, y, 0);
                living.yHeadRot = living.yHeadRotO = 0;
                living.yBodyRot = living.yBodyRotO = 0;
                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(living)
                        .render(living, 0, partialTick, poseStack, bufferSource, packedLight);
                poseStack.popPose();
            }
        }
        poseStack.popPose();
        poseStack.popPose();
    }
}
