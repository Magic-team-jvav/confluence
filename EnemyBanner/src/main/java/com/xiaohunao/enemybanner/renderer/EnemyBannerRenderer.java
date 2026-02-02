package com.xiaohunao.enemybanner.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.xiaohunao.enemybanner.BannerRenderUtils;
import com.xiaohunao.enemybanner.EnemyBanner;
import com.xiaohunao.enemybanner.blocks.EnemyBannerBlock;
import com.xiaohunao.enemybanner.blocks.EnemyBannerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EnemyBannerRenderer implements BlockEntityRenderer<EnemyBannerBlockEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(EnemyBanner.MODID, "enemy_banner"), "main");

    public static final String FLAG = "flag";
    public static final String POLE = "pole";
    public static final String BAR = "bar";
    private final ModelPart flag;
    private final ModelPart pole;
    private final ModelPart bar;

    public EnemyBannerRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(LAYER_LOCATION);
        this.flag = modelpart.getChild(FLAG);
        this.pole = modelpart.getChild(POLE);
        this.bar = modelpart.getChild(BAR);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild(FLAG, CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild(POLE, CubeListBuilder.create().texOffs(44, 0).addBox(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild(BAR, CubeListBuilder.create().texOffs(0, 42).addBox(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void render(@NotNull EnemyBannerBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        VertexConsumer vertexConsumer = ModelBakery.BANNER_BASE.buffer(bufferSource, RenderType::entitySolid);
        BlockState blockState = blockEntity.getBlockState();
        Direction clickedFace = blockState.getValue(EnemyBannerBlock.CLICKED_FACE);

        float rotation = -RotationSegment.convertToDegrees(blockState.getValue(EnemyBannerBlock.ROTATION));//integer rotation_16 to float
        long gameTime = Objects.requireNonNull(blockEntity.getLevel()).getGameTime();

        poseStack.pushPose();
        if (Direction.UP == clickedFace){
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            this.pole.visible = true;
        } else if (Direction.DOWN == clickedFace){
            poseStack.translate(0.5F, -0.3F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            this.pole.visible = false;
        } else {
            poseStack.translate(0.5F, -0.16666667F, 0.5F);
            float f3 = -clickedFace.toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(f3));
            poseStack.translate(0.0F, -0.3125F, -0.4375F);
            this.pole.visible = false;
        }

        poseStack.pushPose();
        poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        pole.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        bar.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        BlockPos blockpos = blockEntity.getBlockPos();
        //动画
        float f2 = ((float)Math.floorMod(blockpos.getX() * 7L + blockpos.getY() * 9L + blockpos.getZ() * 13L + gameTime, 100L) + partialTick) / 100.0F;
        this.flag.xRot = (-0.0125F + 0.01F * Mth.cos(((float)Math.PI * 2F) * f2)) * (float)Math.PI;
        this.flag.y = -32.0F;

        BannerRenderUtils.renderBannerFlag(blockEntity.getParameters(), flag, poseStack, bufferSource, packedLight, packedOverlay);

        poseStack.popPose();
        poseStack.popPose();

    }

    public static void renderMonster(Monster monster, ModelPart flag, PoseStack poseStack, MultiBufferSource buffer, int packedLight){
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        poseStack.pushPose();
        flag.translateAndRotate(poseStack);
        poseStack.translate(0.0D, 0.0D, -0.15D);
        if (Minecraft.getInstance().level != null) {
            BannerRenderUtils.renderEntityToBanner(entityRenderDispatcher, monster,poseStack, buffer, packedLight);
        }
        poseStack.popPose();
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(EnemyBannerBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        boolean standing = blockEntity.getBlockState().getBlock() instanceof BannerBlock;
        return AABB.encapsulatingFullBlocks(pos, standing ? pos.above() : pos.below());
    }
}
