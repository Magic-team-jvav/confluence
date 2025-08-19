package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.mixin.client.accessor.LivingEntityRendererAccessor;

public class ZombieArmRenderer {
    private static ZombieArmRenderer INSTANCE;
    private static final ResourceLocation ZOMBIE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");
    private final HumanoidModel<AbstractClientPlayer> zombieModel;

    private ZombieArmRenderer() {
        this.zombieModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ZOMBIE));
    }

    public void render(PlayerRenderer playerRenderer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Player player, float partialTick) {
        PlayerModel<AbstractClientPlayer> playerModel = playerRenderer.getModel();
        zombieModel.setAllVisible(false);
        if (player.getItemInHand(InteractionHand.MAIN_HAND).is(SwordItems.ZOMBIE_ARM)) {
            playerModel.rightArm.visible = false;
            playerModel.rightSleeve.visible = false;
            zombieModel.rightArm.visible = true;
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).is(SwordItems.ZOMBIE_ARM)) {
            playerModel.leftArm.visible = false;
            playerModel.leftSleeve.visible = false;
            zombieModel.leftArm.visible = true;
        }
        playerModel.copyPropertiesTo(zombieModel);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(zombieModel.renderType(ZOMBIE_LOCATION));
        poseStack.pushPose();
        float f8 = player.getScale();
        poseStack.scale(f8, f8, f8);
        float f = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
        LivingEntityRendererAccessor accessor = (LivingEntityRendererAccessor) playerRenderer;
        float f9 = accessor.callGetBob(player, partialTick);
        accessor.callSetupRotations(player, poseStack, f9, f, partialTick, f8);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        accessor.callScale(player, poseStack, partialTick);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        zombieModel.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    public boolean renderHand(PlayerRenderer playerRenderer, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, HumanoidArm humanoidArm) {
        ModelPart rendererArm;
        if (humanoidArm == HumanoidArm.RIGHT && player.getItemInHand(InteractionHand.MAIN_HAND).is(SwordItems.ZOMBIE_ARM)) {
            rendererArm = zombieModel.rightArm;
        } else if (humanoidArm == HumanoidArm.LEFT && player.getItemInHand(InteractionHand.OFF_HAND).is(SwordItems.ZOMBIE_ARM)) {
            rendererArm = zombieModel.leftArm;
        } else {
            return false;
        }
        PlayerModel<AbstractClientPlayer> playermodel = playerRenderer.getModel();
        playerRenderer.setModelProperties(player);
        playermodel.attackTime = 0.0F;
        playermodel.crouching = false;
        playermodel.swimAmount = 0.0F;
        playermodel.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        rendererArm.xRot = 0.0F;
        rendererArm.render(poseStack, buffer.getBuffer(RenderType.entitySolid(ZOMBIE_LOCATION)), combinedLight, OverlayTexture.NO_OVERLAY);
        return true;
    }

    public static ZombieArmRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ZombieArmRenderer();
        }
        return INSTANCE;
    }
}
