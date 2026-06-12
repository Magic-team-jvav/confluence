package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.mod.common.block.functional.enemybanner.AbstractEnemyBannerBlock;
import org.confluence.mod.common.init.block.ModBlocks;

public class EnemyBannerItemRenderer implements IClientItemExtensions {
    private final AbstractEnemyBannerBlock.BEntity fakeEntity;
    private BlockEntityWithoutLevelRenderer renderer;

    public EnemyBannerItemRenderer() {
        this.fakeEntity = new AbstractEnemyBannerBlock.BEntity(BlockPos.ZERO, ModBlocks.ENEMY_BANNER.get().defaultBlockState());
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (renderer == null) {
            Minecraft minecraft = Minecraft.getInstance();
            BlockEntityRenderDispatcher dispatcher = minecraft.getBlockEntityRenderDispatcher();
            this.renderer = new BlockEntityWithoutLevelRenderer(dispatcher, minecraft.getEntityModels()) {
                @Override
                public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
                    if (minecraft.level != null) {
                        fakeEntity.setLevel(minecraft.level);
                    }
                    fakeEntity.entryKey = AbstractEnemyBannerBlock.BItem.getEntryKey(stack);
                    BlockEntityRenderer<AbstractEnemyBannerBlock.BEntity> renderer1 = dispatcher.getRenderer(fakeEntity);
                    if (renderer1 != null) {
                        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
                        renderer1.render(fakeEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);
                    }
                }
            };
        }
        return renderer;
    }
}
