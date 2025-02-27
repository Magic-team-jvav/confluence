package org.confluence.mod.common.data.gen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SlimeBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.OreBlocks;

import java.util.List;

import static org.confluence.mod.Confluence.MODID;


public class ModBlockStateProvider extends BlockStateProvider {
//    private static final String[] WOODS = Arrays.stream(LogBlocks.WoodSetType.values()).map(woodSetType -> woodSetType.name().toLowerCase()).toArray(String[]::new);

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        // 一般方块
        List<DeferredRegister.Blocks> normalBlocks = List.of(
                OreBlocks.BLOCKS,
                DecorativeBlocks.BLOCKS
        );
        normalBlocks.forEach(blocks -> blocks.getEntries().forEach(block -> {
            Block value = block.get();
            try {
                simpleBlock(value);
            } catch (Exception e) {
                Confluence.LOGGER.error(e.getMessage());
            }
        }));

    }

    private static boolean shouldSkip(Block block) {
        return
//               block instanceof CustomModel ||
               block instanceof SlimeBlock || block instanceof SignBlock || block instanceof LiquidBlock;
    }

//    private static ResourceLocation texture(String path, String regex) {
//        if (Arrays.stream(WOODS).anyMatch(path::contains)) {
//            return Confluence.asResource("block/" + path.replace(regex, "_planks"));
//        }
//        return Confluence.asResource("block/" + path.replace(regex, ""));
//    }

    private static ResourceLocation side(String path) {
        return Confluence.asResource("block/" + path + "_side");
    }

    private static ResourceLocation bottom(String path) {
        return Confluence.asResource("block/" + path + "_bottom");
    }

    private static ResourceLocation top(String path) {
        return Confluence.asResource("block/" + path + "_top");
    }

//    private void registerSignBlock(LogBlocks logBlocks) {
//        try {
//            signBlock(logBlocks.SIGN.getPrefab(), logBlocks.WALL_SIGN.getPrefab(), Confluence.asResource("block/" + logBlocks.id + "_planks"));
//        } catch (Exception e) {
//            Confluence.LOGGER.error(e.getMessage());
//        }
//    }
}
