package org.confluence.mod.client.renderer.entity.bestiary;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Zombie;

public class SlimeZombieRenderer extends ZombieRenderer {
    private Slime slime;
    private boolean failed = false;

    public SlimeZombieRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Zombie zombie, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (!failed && slime == null) {
            this.slime = TEMonsterEntities.BLUE_SLIME.get().create(zombie.level());
            if (slime == null) this.failed = true;
        }
        poseStack.pushPose();
        poseStack.translate(0, -0.5F, 0);
        poseStack.scale(0.9F, 0.9F, 0.9F);
        if (!failed) {
            slime.setXRot(zombie.getXRot());
            slime.setYRot(zombie.getYRot());
            slime.yBodyRot = zombie.yBodyRot;
            slime.yHeadRot = zombie.yHeadRot;
            entityRenderDispatcher.render(slime, zombie.getX(), zombie.getY() + 1.5, zombie.getZ(), entityYaw, partialTicks, poseStack, buffer, packedLight);
            slime.yBodyRotO = zombie.yBodyRotO;
            slime.yHeadRotO = zombie.yHeadRotO;
        }
        super.render(zombie, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
