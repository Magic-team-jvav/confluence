package org.confluence.mod.client.gui.character;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.client.character.CharacterAppearance;
import org.confluence.mod.client.character.CharacterSkins;

/// 在界面里画一个角色小人（没有实体，主菜单里也能用）：按外观生成的皮肤，或启动器皮肤。
/// 变换与原版背包里画玩家的方式一致。
public final class CharacterPreview {
    private static PlayerModel<LivingEntity> wide;
    private static PlayerModel<LivingEntity> slim;

    private CharacterPreview() {}

    /// @param feetY 脚底的屏幕 y；@param size 每格方块多少像素；@param yaw 0 = 面朝屏幕
    public static void render(GuiGraphics guiGraphics, CharacterAppearance appearance, int centerX, int feetY, float size, float yaw) {
        Minecraft minecraft = Minecraft.getInstance();
        if (wide == null) {
            wide = new PlayerModel<>(minecraft.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
            slim = new PlayerModel<>(minecraft.getEntityModels().bakeLayer(ModelLayers.PLAYER_SLIM), true);
        }
        ResourceLocation texture;
        boolean useSlim;
        if (appearance == null || appearance.launcherSkin) {
            PlayerSkin skin = minecraft.getSkinManager().getInsecureSkin(minecraft.getGameProfile());
            texture = skin.texture();
            useSlim = skin.model() == PlayerSkin.Model.SLIM;
        } else {
            texture = CharacterSkins.texture(appearance);
            useSlim = appearance.slim;
        }
        PlayerModel<LivingEntity> model = useSlim ? slim : wide;
        model.setAllVisible(true);
        model.young = false;
        model.crouching = false;
        model.rightArm.zRot = 0.1F;
        model.leftArm.zRot = -0.1F;

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(centerX, feetY, 100.0F);
        pose.scale(size, size, -size);
        pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
        pose.mulPose(Axis.YP.rotationDegrees(yaw));
        pose.scale(-1.0F, -1.0F, 1.0F);
        pose.scale(0.9375F, 0.9375F, 0.9375F); // 与玩家渲染器相同
        pose.translate(0.0F, -1.501F, 0.0F);
        Lighting.setupForEntityInInventory();
        model.renderToBuffer(pose, guiGraphics.bufferSource().getBuffer(model.renderType(texture)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        guiGraphics.flush();
        Lighting.setupFor3DItems();
        pose.popPose();
    }
}
