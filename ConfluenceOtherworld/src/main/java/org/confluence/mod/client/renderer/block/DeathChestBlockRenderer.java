package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.DeathChestBlock;
import org.confluence.terra_curio.client.handler.InformationHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeathChestBlockRenderer extends ChestRenderer<DeathChestBlock.Entity> { // 死人箱没有上锁变种
    public static final Material DEATH_GOLDEN = chest("death_golden");
    public static final Material DEATH_GOLDEN_LEFT = chest("death_golden_left");
    public static final Material DEATH_GOLDEN_RIGHT = chest("death_golden_right");

    public static final Material DEATH_SHADOW = chest("death_shadow");
    public static final Material DEATH_SHADOW_LEFT = chest("death_shadow_left");
    public static final Material DEATH_SHADOW_RIGHT = chest("death_shadow_right");

    public static final Material DEATH_FROZEN = chest("death_frozen");
    public static final Material DEATH_FROZEN_LEFT = chest("death_frozen_left");
    public static final Material DEATH_FROZEN_RIGHT = chest("death_frozen_right");

    public static final Material DEATH_LVY = chest("death_lvy");
    public static final Material DEATH_LVY_LEFT = chest("death_lvy_left");
    public static final Material DEATH_LVY_RIGHT = chest("death_lvy_right");

    public static final Material DEATH_WATER = chest("death_water");
    public static final Material DEATH_WATER_LEFT = chest("death_water_left");
    public static final Material DEATH_WATER_RIGHT = chest("death_water_right");

    public static final Material DEATH_SKYWARE = chest("death_skyware");
    public static final Material DEATH_SKYWARE_LEFT = chest("death_skyware_left");
    public static final Material DEATH_SKYWARE_RIGHT = chest("death_skyware_right");

    public static final Material DEATH_NORMAL = chest("death_normal");
    public static final Material DEATH_NORMAL_LEFT = chest("death_normal_left");
    public static final Material DEATH_NORMAL_RIGHT = chest("death_normal_right");

    public static final Material DEATH_SANDSTONE = chest("death_sandstone");
    public static final Material DEATH_SANDSTONE_LEFT = chest("death_sandstone_left");
    public static final Material DEATH_SANDSTONE_RIGHT = chest("death_sandstone_right");

    public static final Material DEATH_LIVING_WOOD = chest("death_living_wood");
    public static final Material DEATH_LIVING_WOOD_LEFT = chest("death_living_wood_left");
    public static final Material DEATH_LIVING_WOOD_RIGHT = chest("death_living_wood_right");

    public static final Material DEATH_DUNGEON = chest("death_dungeon");
    public static final Material DEATH_DUNGEON_LEFT = chest("death_dungeon_left");
    public static final Material DEATH_DUNGEON_RIGHT = chest("death_dungeon_right");


    public DeathChestBlockRenderer(BlockEntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public int getViewDistance() {
        return InformationHandler.hasMechanicalView() ? 256 : super.getViewDistance();
    }

    @Override
    public boolean shouldRenderOffScreen(DeathChestBlock.Entity pBlockEntity) {
        return InformationHandler.hasMechanicalView();
    }

    @Override
    public boolean shouldRender(DeathChestBlock.Entity pBlockEntity, Vec3 pCameraPos) {
        return InformationHandler.hasMechanicalView()
                ? pBlockEntity.getBlockPos().getCenter().multiply(1.0, 0.0, 1.0).closerThan(pCameraPos.multiply(1.0, 0.0, 1.0), getViewDistance())
                : super.shouldRender(pBlockEntity, pCameraPos);
    }

    @Override
    public void render(DeathChestBlock.Entity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        if (InformationHandler.hasMechanicalView()) {
            long gameTime = pBlockEntity.getLevel().getGameTime();
            Vec3 vec31 = pBlockEntity.getBlockPos().getCenter();
            for (Int2ObjectMap.Entry<Set<BlockPos>> entry : pBlockEntity.getConnectedPoses().int2ObjectEntrySet()) {
                int color = entry.getIntKey();
                for (BlockPos pos : entry.getValue()) {
                    pPoseStack.pushPose();
                    Vec3 subtract = pos.getCenter().subtract(vec31);
                    Vec3 normalize = subtract.normalize();
                    pPoseStack.translate(0.5, 0.5, 0.5);
                    pPoseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI - (float) Math.atan2(normalize.z, normalize.x)));
                    pPoseStack.mulPose(Axis.XP.rotation((float) Math.acos(normalize.y)));
                    pPoseStack.translate(-0.5, 0.0, -0.5);
                    int height = (int) Math.round(subtract.length());
                    BeaconRenderer.renderBeaconBeam(pPoseStack, pBuffer, BeaconRenderer.BEAM_LOCATION, pPartialTick, 1.0F, gameTime, 0, height, color, 0.2F, 0.25F);
                    pPoseStack.popPose();
                }
            }
        }
    }

    @Override
    protected Material getMaterial(DeathChestBlock.Entity blockEntity, ChestType chestType) {
        return switch (blockEntity.variant) {
            case UNLOCKED_SHADOW -> chooseMaterial(chestType, DEATH_SHADOW, DEATH_SHADOW_LEFT, DEATH_SHADOW_RIGHT);
            case UNLOCKED_FROZEN -> chooseMaterial(chestType, DEATH_FROZEN, DEATH_FROZEN_LEFT, DEATH_FROZEN_RIGHT);
            case UNLOCKED_WATER -> chooseMaterial(chestType, DEATH_WATER, DEATH_WATER_LEFT, DEATH_WATER_RIGHT);
            case UNLOCKED_SKYWARE -> chooseMaterial(chestType, DEATH_SKYWARE, DEATH_SKYWARE_LEFT, DEATH_SKYWARE_RIGHT);
            case UNLOCKED_LVY -> chooseMaterial(chestType, DEATH_LVY, DEATH_LVY_LEFT, DEATH_LVY_RIGHT);
            case UNLOCKED_NORMAL -> chooseMaterial(chestType, DEATH_NORMAL, DEATH_NORMAL_LEFT, DEATH_NORMAL_RIGHT);
            case UNLOCKED_SANDSTONE -> chooseMaterial(chestType, DEATH_SANDSTONE, DEATH_SANDSTONE_LEFT, DEATH_SANDSTONE_RIGHT);
            case UNLOCKED_LIVING_WOOD -> chooseMaterial(chestType, DEATH_LIVING_WOOD, DEATH_LIVING_WOOD_LEFT, DEATH_LIVING_WOOD_RIGHT);
            case UNLOCKED_DUNGEON -> chooseMaterial(chestType, DEATH_DUNGEON, DEATH_DUNGEON_LEFT, DEATH_DUNGEON_RIGHT);
            default -> chooseMaterial(chestType, DEATH_GOLDEN, DEATH_GOLDEN_LEFT, DEATH_GOLDEN_RIGHT);
        };
    }

    private static Material chest(String pChestName) {
        return new Material(Sheets.CHEST_SHEET, Confluence.asResource("entity/chest/" + pChestName));
    }

    private static Material chooseMaterial(ChestType pChestType, Material pDoubleMaterial, Material pLeftMaterial, Material pRightMaterial) {
        return switch (pChestType) {
            case LEFT -> pLeftMaterial;
            case RIGHT -> pRightMaterial;
            case SINGLE -> pDoubleMaterial;
        };
    }
}
