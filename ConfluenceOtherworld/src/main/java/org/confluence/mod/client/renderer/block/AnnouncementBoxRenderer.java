package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.common.block.functional.announcement_box.AnnouncementBoxEntity;

@OnlyIn(Dist.CLIENT)
public class AnnouncementBoxRenderer implements BlockEntityRenderer<AnnouncementBoxEntity> {
    public AnnouncementBoxRenderer(BlockEntityRendererProvider.Context context) {
    }
    @Override
    public void render(AnnouncementBoxEntity announcementBoxEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {

    }
}