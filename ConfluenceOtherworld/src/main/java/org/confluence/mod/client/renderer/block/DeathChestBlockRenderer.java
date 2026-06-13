package org.confluence.mod.client.renderer.block;

import net.minecraft.Util;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.DeathChestBlock;
import org.confluence.mod.common.init.block.ChestBlocks;

import java.util.Hashtable;
import java.util.function.BiFunction;

public class DeathChestBlockRenderer extends ChestRenderer<DeathChestBlock.BEntity> {
    private final BiFunction<Block, ChestType, Material> function;

    public DeathChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.function = new BiFunction<>() {
            private final Material[] defaultMaterials = new Material[]{
                    Sheets.CHEST_TRAP_LOCATION,
                    Sheets.CHEST_TRAP_LOCATION_LEFT,
                    Sheets.CHEST_TRAP_LOCATION_RIGHT
            };
            private final Hashtable<Block, Material[]> cache = Util.make(new Hashtable<>(), map -> {
                for (RegistryObject<DeathChestBlock> deathChest : ChestBlocks.DEATH_CHESTS) {
                    String chestName = deathChest.getId().getPath().replace("_chest", "");
                    ResourceLocation location = Confluence.asResource("entity/chest/" + chestName);
                    map.put(deathChest.get(), new Material[]{
                            new Material(Sheets.CHEST_SHEET, location),
                            new Material(Sheets.CHEST_SHEET, location.withSuffix("_left")),
                            new Material(Sheets.CHEST_SHEET, location.withSuffix("_right"))
                    });
                }
            });

            @Override
            public Material apply(Block block, ChestType chestType) {
                return cache.getOrDefault(block, defaultMaterials)[chestType.ordinal()];
            }
        };
    }

    @Override
    protected Material getMaterial(DeathChestBlock.BEntity blockEntity, ChestType chestType) {
        return function.apply(blockEntity.getBlockState().getBlock(), chestType);
    }
}
