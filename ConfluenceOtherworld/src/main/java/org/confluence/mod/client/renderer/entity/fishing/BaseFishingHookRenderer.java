package org.confluence.mod.client.renderer.entity.fishing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.color.IntegerRGB;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.fishing.BaseFishingHookModel;
import org.confluence.mod.common.entity.fishing.AbstractFishingHook;
import org.confluence.mod.common.entity.fishing.BaseFishingHook;

public class BaseFishingHookRenderer<E extends BaseFishingHook> extends EntityRenderer<E> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            Confluence.asResource("textures/entity/fishing/wood.png"),
            Confluence.asResource("textures/entity/fishing/reinforced.png"),
            Confluence.asResource("textures/entity/fishing/fisher_of_souls.png"),
            Confluence.asResource("textures/entity/fishing/fleshcatcher.png"),
            Confluence.asResource("textures/entity/fishing/scarab.png"),
            Confluence.asResource("textures/entity/fishing/fiberglass.png"),
            Confluence.asResource("textures/entity/fishing/mechanics.png"),
            Confluence.asResource("textures/entity/fishing/sitting_ducks.png"),
            Confluence.asResource("textures/entity/fishing/golden.png")
    };
    private static final IntegerRGB[] COLORS = new IntegerRGB[]{
            IntegerRGB.BLACK,
            IntegerRGB.GRAY,
            IntegerRGB.PURPLE,
            IntegerRGB.LIGHT_RED,
            IntegerRGB.BLUE,
            IntegerRGB.GREEN,
            IntegerRGB.RED,
            IntegerRGB.WHITE,
            IntegerRGB.CYAN
    };
    private final BaseFishingHookModel[] MODELS;

    public BaseFishingHookRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.MODELS = new BaseFishingHookModel[]{
                new BaseFishingHookModel(pContext.bakeLayer(BaseFishingHookModel.WOOD)),
                new BaseFishingHookModel(pContext.bakeLayer(BaseFishingHookModel.REINFORCED)),
                new BaseFishingHookModel(pContext.bakeLayer(BaseFishingHookModel.FISHER_OF_SOULS)),
                new BaseFishingHookModel(pContext.bakeLayer(BaseFishingHookModel.FLESHCATCHER)),
                new BaseFishingHookModel(pContext.bakeLayer(BaseFishingHookModel.SCARAB)),
                new BaseFishingHookModel(pContext.bakeLayer(BaseFishingHookModel.FIBERGLASS)),
                new BaseFishingHookModel(pContext.bakeLayer(BaseFishingHookModel.MECHANICS)),
                new BaseFishingHookModel(pContext.bakeLayer(BaseFishingHookModel.SITTING_DUCKS)),
                new BaseFishingHookModel(pContext.bakeLayer(BaseFishingHookModel.GOLDEN))
        };
    }

    @Override
    public ResourceLocation getTextureLocation(BaseFishingHook pEntity) {
        return TEXTURES[pEntity.getVariant().getId()];
    }

    @Override
    public void render(E entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        int id = entity.getVariant().getId();
        BaseFishingHookModel model = MODELS[id];
        model.renderToBuffer(poseStack, buffer.getBuffer(model.renderType(TEXTURES[id])), packedLight, OverlayTexture.NO_OVERLAY);
        renderString(entityRenderDispatcher, entity, partialTicks, poseStack, buffer, COLORS[id].get());
    }

    static <E extends AbstractFishingHook> void renderString(EntityRenderDispatcher entityRenderDispatcher, E entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int color) {
        Player player = entity.getPlayerOwner();
        if (player != null) {
            color = 0xFF << 24 | (color & 0xFFFFFF);

            poseStack.pushPose();
            float f = player.getAttackAnim(partialTicks);
            float f1 = Mth.sin(Mth.sqrt(f) * (float) Math.PI);
            Vec3 vec3 = getPlayerHandPos(entityRenderDispatcher, player, f1, partialTicks);
            Vec3 vec31 = entity.getPosition(partialTicks).add(0.0, 0.25, 0.0);
            float f2 = (float) (vec3.x - vec31.x);
            float f3 = (float) (vec3.y - vec31.y);
            float f4 = (float) (vec3.z - vec31.z);
            VertexConsumer vertexconsumer1 = buffer.getBuffer(RenderType.lineStrip());
            PoseStack.Pose posestack$pose1 = poseStack.last();

            for (int j = 0; j <= 16; j++) {
                stringVertex(f2, f3, f4, vertexconsumer1, posestack$pose1, j * 0.0625F, (j + 1) * 0.0625F, color);
            }

            poseStack.popPose();
        }
    }

    private static Vec3 getPlayerHandPos(EntityRenderDispatcher entityRenderDispatcher, Player player, float p_340872_, float partialTick) {
        int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        ItemStack itemstack = player.getMainHandItem();
        if (!itemstack.canPerformAction(net.neoforged.neoforge.common.ItemAbilities.FISHING_ROD_CAST)) {
            i = -i;
        }

        if (entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
            double d4 = 960.0 / (double) entityRenderDispatcher.options.fov().get();
            Vec3 vec3 = entityRenderDispatcher
                    .camera
                    .getNearPlane()
                    .getPointOnPlane((float) i * 0.525F, -0.1F)
                    .scale(d4)
                    .yRot(p_340872_ * 0.5F)
                    .xRot(-p_340872_ * 0.7F);
            return player.getEyePosition(partialTick).add(vec3);
        } else {
            float f = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot) * (float) (Math.PI / 180.0);
            double d0 = Mth.sin(f);
            double d1 = Mth.cos(f);
            float f1 = player.getScale();
            double d2 = (double) i * 0.35 * (double) f1;
            double d3 = 0.8 * (double) f1;
            float f2 = player.isCrouching() ? -0.1875F : 0.0F;
            return player.getEyePosition(partialTick).add(-d1 * d2 - d0 * d3, (double) f2 - 0.45 * (double) f1, -d0 * d2 + d1 * d3);
        }
    }

    private static void stringVertex(float x, float y, float z, VertexConsumer consumer, PoseStack.Pose pose, float stringFraction, float nextStringFraction, int color) {
        float f = x * stringFraction;
        float f1 = y * (stringFraction * stringFraction + stringFraction) * 0.5F + 0.25F;
        float f2 = z * stringFraction;
        float f3 = x * nextStringFraction - f;
        float f4 = y * (nextStringFraction * nextStringFraction + nextStringFraction) * 0.5F + 0.25F - f1;
        float f5 = z * nextStringFraction - f2;
        float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
        f3 /= f6;
        f4 /= f6;
        f5 /= f6;
        consumer.addVertex(pose, f, f1, f2).setColor(color).setNormal(pose, f3, f4, f5);
    }
}
