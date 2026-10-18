package org.confluence.mod.client.model.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.confluence.mod.common.entity.soulcerer.SoulStingProjectile;

/// 魂师毒刺弹幕的模型：网格与 `HornetStingerProjectileModel` 完全一致，复用同一份烘焙层。
public final class SoulStingProjectileModel extends EntityModel<SoulStingProjectile> {
    private final ModelPart root;

    public SoulStingProjectileModel(ModelPart root) {
        this.root = root.getChild("stinger");
    }

    @Override
    public void setupAnim(SoulStingProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
