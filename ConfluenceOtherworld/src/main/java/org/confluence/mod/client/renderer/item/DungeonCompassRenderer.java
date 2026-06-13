package org.confluence.mod.client.renderer.item;

import PortLib.extensions.com.mojang.blaze3d.vertex.VertexConsumer.PortVertexConsumerExtension;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ToolItems;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.util.RenderUtils;

public class DungeonCompassRenderer {
    private static DungeonCompassRenderer INSTANCE;
    private final GeoBone bone;
    private final GeoBone outline;
    private final RenderType boneRenderType;
    private final RenderType outlineRenderType;

    private DungeonCompassRenderer() {
        BakedGeoModel geoModel = GeckoLibCache.getBakedModels().get(Confluence.asResource("geo/item/dungeon_compass.geo.json"));
        this.bone = geoModel.getBone("bone").orElseThrow();
        bone.updateScale(0.8F, 0.8F, 0.8F);
        this.outline = geoModel.getBone("outline").orElseThrow();
        outline.updateScale(0.8F, 0.8F, 0.8F);
        ResourceLocation texture = Confluence.asResource("textures/item/dungeon_compass.png");
        this.boneRenderType = RenderType.text(texture);
        this.outlineRenderType = RenderType.eyes(texture);
    }

    public static DungeonCompassRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DungeonCompassRenderer();
        }
        return INSTANCE;
    }

    public static void renderInWorld(PoseStack poseStack, LocalPlayer player, Minecraft minecraft) {
        ItemStack headItem = player.getInventory().armor.get(3);
        if (headItem.isEmpty() || !headItem.is(ToolItems.DUNGEON_COMPASS.get())) {
            return;
        }
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(headItem);
        if (tag == null) return;
        int[] pos = tag.getIntArray("pos");
        if (pos.length != 3) return;
        getInstance().render(
                poseStack,
                minecraft.renderBuffers().bufferSource(),
                player,
                pos[0],
                pos[2]
        );
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, AbstractClientPlayer player, int x, int z) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        RenderSystem.enableDepthTest();
        poseStack.pushPose();
        float ry = Mth.PI + player.getYRot() * Mth.DEG_TO_RAD;
        float cos = Mth.cos(ry);
        float sin = Mth.sin(ry);
        float mx = 0.75F;
        float mz = -1F;
        float vx = mx * cos + mz * -sin;
        float vz = mx * sin + mz * cos;
        poseStack.translate(vx, 0.15F, vz);
        float yaw = Mth.PI - (float) Mth.atan2(-x + player.getX(), z - player.getZ());

        renderGeoBone(bone, boneRenderType, poseStack, bufferSource, yaw, false);
        renderGeoBone(outline, outlineRenderType, poseStack, bufferSource, yaw, true);

        poseStack.popPose();
        RenderSystem.disableDepthTest();
    }

    private static void renderGeoBone(GeoBone geoBone, RenderType renderType, PoseStack poseStack, MultiBufferSource bufferSource, float yaw, boolean yOffset) {
        VertexConsumer buffer;
        poseStack.pushPose();
        if (yOffset) {
            geoBone.updatePosition(0, -1.75F, 0);
        }
        geoBone.setRotY(yaw);
        RenderUtils.prepMatrixForBone(poseStack, geoBone);
        buffer = bufferSource.getBuffer(renderType);
        for (GeoCube cube : geoBone.getCubes()) {
            poseStack.pushPose();
            RenderUtils.translateToPivotPoint(poseStack, cube);
            RenderUtils.rotateMatrixAroundCube(poseStack, cube);
            RenderUtils.translateAwayFromPivotPoint(poseStack, cube);

            Matrix3f normalisedPoseState = poseStack.last().normal();
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());

            for (GeoQuad quad : cube.quads()) {
                if (quad == null) continue;

                Vector3f normal = normalisedPoseState.transform(new Vector3f(quad.normal()));

                RenderUtils.fixInvertedFlatCube(cube, normal);
                for (GeoVertex vertex : quad.vertices()) {
                    Vector3f position = vertex.position();
                    Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));

                    PortVertexConsumerExtension.vertex(buffer, vector4f.x(), vector4f.y(), vector4f.z(), -1, vertex.texU(),
                            vertex.texV(), OverlayTexture.NO_OVERLAY, 0xF000F0, normal.x(), normal.y(), normal.z());
                }
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
