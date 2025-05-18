package org.confluence.mod.client.renderer.block;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.BaseChestBlock;

public class BaseChestBlockRenderer extends ChestRenderer<BaseChestBlock.Entity> {
    public static final Material LOCKED_GOLDEN = chest("locked_golden");
    public static final Material LOCKED_GOLDEN_LEFT = chest("locked_golden_left");
    public static final Material LOCKED_GOLDEN_RIGHT = chest("locked_golden_right");
    public static final Material UNLOCKED_GOLDEN = chest("unlocked_golden");
    public static final Material UNLOCKED_GOLDEN_LEFT = chest("unlocked_golden_left");
    public static final Material UNLOCKED_GOLDEN_RIGHT = chest("unlocked_golden_right");

    public static final Material LOCKED_SHADOW = chest("locked_shadow");
    public static final Material LOCKED_SHADOW_LEFT = chest("locked_shadow_left");
    public static final Material LOCKED_SHADOW_RIGHT = chest("locked_shadow_right");
    public static final Material UNLOCKED_SHADOW = chest("unlocked_shadow");
    public static final Material UNLOCKED_SHADOW_LEFT = chest("unlocked_shadow_left");
    public static final Material UNLOCKED_SHADOW_RIGHT = chest("unlocked_shadow_right");

    public static final Material UNLOCKED_FROZEN = chest("unlocked_frozen");
    public static final Material UNLOCKED_FROZEN_LEFT = chest("unlocked_frozen_left");
    public static final Material UNLOCKED_FROZEN_RIGHT = chest("unlocked_frozen_right");

    public static final Material UNLOCKED_LVY = chest("unlocked_lvy");
    public static final Material UNLOCKED_LVY_LEFT = chest("unlocked_lvy_left");
    public static final Material UNLOCKED_LVY_RIGHT = chest("unlocked_lvy_right");

    public static final Material UNLOCKED_WATER = chest("unlocked_water");
    public static final Material UNLOCKED_WATER_LEFT = chest("unlocked_water_left");
    public static final Material UNLOCKED_WATER_RIGHT = chest("unlocked_water_right");

    public static final Material UNLOCKED_SKYWARE = chest("unlocked_skyware");
    public static final Material UNLOCKED_SKYWARE_LEFT = chest("unlocked_skyware_left");
    public static final Material UNLOCKED_SKYWARE_RIGHT = chest("unlocked_skyware_right");


    public static final Material UNLOCKED_NORMAL = chest("unlocked_normal");
    public static final Material UNLOCKED_NORMAL_LEFT = chest("unlocked_normal_left");
    public static final Material UNLOCKED_NORMAL_RIGHT = chest("unlocked_normal_right");

    public static final Material UNLOCKED_SANDSTONE = chest("unlocked_sandstone");
    public static final Material UNLOCKED_SANDSTONE_LEFT = chest("unlocked_sandstone_left");
    public static final Material UNLOCKED_SANDSTONE_RIGHT = chest("unlocked_sandstone_right");

    public static final Material UNLOCKED_LIVING_WOOD = chest("unlocked_living_wood");
    public static final Material UNLOCKED_LIVING_WOOD_LEFT = chest("unlocked_living_wood_left");
    public static final Material UNLOCKED_LIVING_WOOD_RIGHT = chest("unlocked_living_wood_right");

    public static final Material LOCKED_DUNGEON = chest("locked_dungeon");
    public static final Material LOCKED_DUNGEON_LEFT = chest("locked_dungeon_left");
    public static final Material LOCKED_DUNGEON_RIGHT = chest("locked_dungeon_right");
    public static final Material UNLOCKED_DUNGEON = chest("unlocked_dungeon");
    public static final Material UNLOCKED_DUNGEON_LEFT = chest("unlocked_dungeon_left");
    public static final Material UNLOCKED_DUNGEON_RIGHT = chest("unlocked_dungeon_right");

    public BaseChestBlockRenderer(BlockEntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    protected Material getMaterial(BaseChestBlock.Entity blockEntity, ChestType chestType) {
        return switch (blockEntity.variant) {
            case UNLOCKED_GOLDEN -> chooseMaterial(chestType, UNLOCKED_GOLDEN, UNLOCKED_GOLDEN_LEFT, UNLOCKED_GOLDEN_RIGHT);
            case LOCKED_SHADOW -> chooseMaterial(chestType, LOCKED_SHADOW, LOCKED_SHADOW_LEFT, LOCKED_SHADOW_RIGHT);
            case UNLOCKED_SHADOW -> chooseMaterial(chestType, UNLOCKED_SHADOW, UNLOCKED_SHADOW_LEFT, UNLOCKED_SHADOW_RIGHT);
            case UNLOCKED_FROZEN -> chooseMaterial(chestType, UNLOCKED_FROZEN, UNLOCKED_FROZEN_LEFT, UNLOCKED_FROZEN_RIGHT);
            case UNLOCKED_LVY -> chooseMaterial(chestType, UNLOCKED_LVY, UNLOCKED_LVY_LEFT, UNLOCKED_LVY_RIGHT);
            case UNLOCKED_WATER -> chooseMaterial(chestType, UNLOCKED_WATER, UNLOCKED_WATER_LEFT, UNLOCKED_WATER_RIGHT);
            case UNLOCKED_SKYWARE -> chooseMaterial(chestType, UNLOCKED_SKYWARE, UNLOCKED_SKYWARE_LEFT, UNLOCKED_SKYWARE_RIGHT);
            case UNLOCKED_NORMAL -> chooseMaterial(chestType, UNLOCKED_NORMAL, UNLOCKED_NORMAL_LEFT, UNLOCKED_NORMAL_RIGHT);
            case UNLOCKED_SANDSTONE -> chooseMaterial(chestType, UNLOCKED_SANDSTONE, UNLOCKED_SANDSTONE_LEFT, UNLOCKED_SANDSTONE_RIGHT);
            case UNLOCKED_LIVING_WOOD -> chooseMaterial(chestType, UNLOCKED_LIVING_WOOD, UNLOCKED_LIVING_WOOD_LEFT, UNLOCKED_LIVING_WOOD_RIGHT);
            case LOCKED_DUNGEON -> chooseMaterial(chestType, LOCKED_DUNGEON, LOCKED_DUNGEON_LEFT, LOCKED_DUNGEON_RIGHT);
            case UNLOCKED_DUNGEON -> chooseMaterial(chestType, UNLOCKED_DUNGEON, UNLOCKED_DUNGEON_LEFT, UNLOCKED_DUNGEON_RIGHT);
            default -> chooseMaterial(chestType, LOCKED_GOLDEN, LOCKED_GOLDEN_LEFT, LOCKED_GOLDEN_RIGHT);
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
