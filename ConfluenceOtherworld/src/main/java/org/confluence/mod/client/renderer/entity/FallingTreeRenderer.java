package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.confluence.mod.common.entity.FallingTreeEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

/// 倒下的树：所有方块绕转轴（底边、朝倒向一侧）一起转，树叶按原来所在位置的群系着色
public class FallingTreeRenderer extends EntityRenderer<FallingTreeEntity> {
    private final RandomSource random = RandomSource.create();

    public FallingTreeRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull FallingTreeEntity entity, float entityYaw, float partialTick, @NotNull PoseStack pose,
                       @NotNull MultiBufferSource buffers, int packedLight) {
        int count = entity.blockCount();
        if (count == 0) return;
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher dispatcher = minecraft.getBlockRenderer();
        BlockPos base = entity.base();
        if (entity.colors == null || entity.colors.length != count) {
            entity.colors = new int[count];
            for (int i = 0; i < count; i++) {
                BlockPos original = base.offset(entity.offsetX(i), entity.offsetY(i), entity.offsetZ(i));
                entity.colors[i] = minecraft.getBlockColors().getColor(entity.state(i), entity.level(), original, 0);
            }
        }
        float time = entity.tickCount + partialTick;
        float angle = entity.angleAt(time);
        pose.pushPose();
        // 转轴本身的位移：翻过边缘、自由下落时整棵树跟着走
        float slide = entity.slideAt(time);
        pose.translate(entity.dirX() * slide, entity.liftAt(time) - entity.dropAt(time), entity.dirZ() * slide);
        // 绕水平轴 (dirZ, 0, -dirX) 转：竖直方向转向倒向
        pose.mulPose(new Quaternionf().rotateAxis(angle, entity.dirZ(), 0.0F, -entity.dirX()));
        float px = entity.pivotX(), py = entity.pivotY(), pz = entity.pivotZ();
        for (int i = 0; i < count; i++) {
            BlockState state = entity.state(i);
            if (state.getRenderShape() != RenderShape.MODEL) continue;
            BakedModel model = dispatcher.getBlockModel(state);
            int color = entity.colors[i];
            float r = 1.0F, g = 1.0F, b = 1.0F;
            if (color != -1) {
                r = (color >> 16 & 255) / 255.0F;
                g = (color >> 8 & 255) / 255.0F;
                b = (color & 255) / 255.0F;
            }
            pose.pushPose();
            pose.translate(entity.offsetX(i) - px, entity.offsetY(i) - py, entity.offsetZ(i) - pz);
            random.setSeed(42L);
            for (RenderType type : model.getRenderTypes(state, random, ModelData.EMPTY)) {
                dispatcher.getModelRenderer().renderModel(pose.last(), buffers.getBuffer(RenderTypeHelper.getEntityRenderType(type, false)),
                        state, model, r, g, b, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, type);
            }
            pose.popPose();
        }
        pose.popPose();
        super.render(entity, entityYaw, partialTick, pose, buffers, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FallingTreeEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
