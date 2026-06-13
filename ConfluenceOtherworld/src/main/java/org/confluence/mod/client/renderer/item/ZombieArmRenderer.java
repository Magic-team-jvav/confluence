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
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.mixin.client.renderer.entity.LivingEntityRendererAccessor;

public class ZombieArmRenderer {
    private static ZombieArmRenderer INSTANCE;
    private static final ResourceLocation ZOMBIE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");
    private final HumanoidModel<AbstractClientPlayer> zombieModel;

    private ZombieArmRenderer() {
        this.zombieModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ZOMBIE));
    }

    public void render(PlayerRenderer playerRenderer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Player player, float partialTick) {
        PlayerModel<AbstractClientPlayer> playerModel = null;
        if (player.getMainHandItem().is(SwordItems.ZOMBIE_ARM.get())) {
            playerModel = playerRenderer.getModel();
            zombieModel.setAllVisible(false);
            playerModel.rightArm.visible = false;
            playerModel.rightSleeve.visible = false;
            zombieModel.rightArm.visible = true;
        }
        if (player.getOffhandItem().is(SwordItems.ZOMBIE_ARM.get())) {
            if (playerModel == null) {
                playerModel = playerRenderer.getModel();
                zombieModel.setAllVisible(false);
            }
            playerModel.leftArm.visible = false;
            playerModel.leftSleeve.visible = false;
            zombieModel.leftArm.visible = true;
        }
        if (playerModel == null) {
            return;
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
        zombieModel.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.popPose();
    }

    public boolean renderHand(PlayerRenderer playerRenderer, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, HumanoidArm humanoidArm) {
        ModelPart rendererArm;
        if (humanoidArm == HumanoidArm.RIGHT && player.getMainHandItem().is(SwordItems.ZOMBIE_ARM.get())) {
            rendererArm = zombieModel.rightArm;
            zombieModel.rightArm.visible = true;
        } else if (humanoidArm == HumanoidArm.LEFT && player.getOffhandItem().is(SwordItems.ZOMBIE_ARM.get())) {
            rendererArm = zombieModel.leftArm;
            zombieModel.leftArm.visible = true;
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
