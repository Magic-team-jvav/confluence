package org.confluence.mod.common.worldgen.biome;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.block.NatureBlocks;

public class SurfaceRuleData {
    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource SNOW = makeStateRule(Blocks.SNOW_BLOCK);
    private static final SurfaceRules.RuleSource PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
    private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
    private static final SurfaceRules.RuleSource RED_SAND = makeStateRule(Blocks.RED_SAND);
    private static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);
    private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
    //private static final SurfaceRules.RuleSource RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
    private static final SurfaceRules.RuleSource HARDENED_SAND = makeStateRule(NatureBlocks.HARDENED_SAND_BLOCK.get());
    private static final SurfaceRules.RuleSource RED_HARDENED_SAND = makeStateRule(NatureBlocks.HARDENED_RED_SAND_BLOCK.get());
    private static final SurfaceRules.RuleSource MOIST_SAND = makeStateRule(NatureBlocks.MOISTENED_SAND_BLOCK.get());
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource JUNGLE_GRASS_BLOCK = makeStateRule(NatureBlocks.JUNGLE_GRASS_BLOCK.get());
    //private static final SurfaceRules.RuleSource CLAY = makeStateRule(Blocks.CLAY);

    private static final SurfaceRules.RuleSource CORRUPT_GRASS_BLOCK = makeStateRule(NatureBlocks.CORRUPT_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource EBONSTONE = makeStateRule(NatureBlocks.EBONSTONE.get());
    private static final SurfaceRules.RuleSource EBONSAND = makeStateRule(NatureBlocks.EBONSAND.get());

    private static final SurfaceRules.RuleSource CRIMSON_GRASS_BLOCK = makeStateRule(NatureBlocks.CRIMSON_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource CRIMSTONE = makeStateRule(NatureBlocks.CRIMSTONE.get());
    private static final SurfaceRules.RuleSource CRIMSAND = makeStateRule(NatureBlocks.CRIMSAND.get());

    private static final SurfaceRules.RuleSource MUSHROOM_GRASS_BLOCK = makeStateRule(NatureBlocks.MUSHROOM_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource MUD = makeStateRule(Blocks.MUD);
    private static final SurfaceRules.RuleSource ASH_BLOCK = makeStateRule(NatureBlocks.ASH_BLOCK.get());
    private static final SurfaceRules.RuleSource ASH_GRASS_BLOCK = makeStateRule(NatureBlocks.ASH_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource DIATOMACEOUS = makeStateRule(NatureBlocks.DIATOMACEOUS.get());

    // 判断 =============================================================
    //表面判断
    private static final SurfaceRules.ConditionSource isAtOrAboveWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);
    //private static final SurfaceRules.ConditionSource isUnderWaterLevel = SurfaceRules.waterStartCheck(-6, -1);
    private static final SurfaceRules.ConditionSource isHole = SurfaceRules.not(SurfaceRules.abovePreliminarySurface());
    //渐变层随机种子
    private static final SurfaceRules.ConditionSource bedrockRoofSeed = SurfaceRules.verticalGradient("minecraft:bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.belowTop(0));
    private static final SurfaceRules.ConditionSource bedrockFloorSeed = SurfaceRules.verticalGradient("minecraft:bedrock_floor", VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(5));
    private static final SurfaceRules.ConditionSource deepslateSeed = SurfaceRules.verticalGradient("minecraft:deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8));
    private static final SurfaceRules.ConditionSource grassSeed = SurfaceRules.verticalGradient("minecraft:bedrock_floor", VerticalAnchor.absolute(48), VerticalAnchor.absolute(52));
    private static final SurfaceRules.ConditionSource sandSeed = SurfaceRules.verticalGradient("minecraft:deepslate", VerticalAnchor.absolute(20), VerticalAnchor.absolute(25));
    private static final SurfaceRules.ConditionSource redSandSeed = SurfaceRules.verticalGradient("minecraft:deepslate", VerticalAnchor.absolute(74), VerticalAnchor.absolute(76));
    private static final SurfaceRules.ConditionSource iceSeed = SurfaceRules.verticalGradient("minecraft:deepslate", VerticalAnchor.absolute(20), VerticalAnchor.absolute(35));
    private static final SurfaceRules.ConditionSource mushroomSeed = SurfaceRules.verticalGradient("minecraft:deepslate", VerticalAnchor.absolute(40), VerticalAnchor.absolute(42));
    //群系组合
    private static final SurfaceRules.ConditionSource isOcean = SurfaceRules.isBiome(Biomes.OCEAN, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN);
    private static final SurfaceRules.ConditionSource sandOcean = SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN);
    private static final SurfaceRules.ConditionSource snowy = SurfaceRules.isBiome(Biomes.ICE_SPIKES, Biomes.GROVE);
    private static final SurfaceRules.ConditionSource dirtSnowy = SurfaceRules.isBiome(Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA);

    // 生成 =============================================================
    private static final SurfaceRules.RuleSource corruptGrassSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, CORRUPT_GRASS_BLOCK), EBONSAND);
    private static final SurfaceRules.RuleSource crimsonGrassSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, CRIMSON_GRASS_BLOCK), CRIMSAND);
    private static final SurfaceRules.RuleSource mushroomSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, MUSHROOM_GRASS_BLOCK), MUD);
    private static final SurfaceRules.RuleSource ashGrassSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, ASH_GRASS_BLOCK), ASH_BLOCK);
    private static final SurfaceRules.RuleSource jungleGrassSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, JUNGLE_GRASS_BLOCK), MUD);
    private static final SurfaceRules.RuleSource grassSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, GRASS_BLOCK), DIRT);

    // confluence =============================================================
    //主世界
    public static SurfaceRules.RuleSource makeConfluenceOverWorldRules() {
        return SurfaceRules.sequence(
                //腐化
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.THE_CORRUPTION),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.not(grassSeed),
                                        SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                SurfaceRules.sequence(
                                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                                corruptGrassSurface
                                                        ),
                                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, 1, CaveSurface.FLOOR),
                                                                DIRT
                                                        )
                                                )
                                        )
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                        EBONSTONE
                                )
                        )
                ),

                //猩红
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.THE_CRIMSON),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.not(grassSeed),
                                        SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                SurfaceRules.sequence(
                                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                                crimsonGrassSurface
                                                        ),
                                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, 1, CaveSurface.FLOOR),
                                                                DIRT
                                                        )
                                                )
                                        )
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                        CRIMSTONE
                                )
                        )
                ),

                //发光蘑菇地
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.GLOWING_MUSHROOM),
                        SurfaceRules.ifTrue(
                                mushroomSeed,
                                SurfaceRules.ifTrue(
                                        SurfaceRules.not(bedrockFloorSeed),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                        mushroomSurface
                                                ),
                                                MUD
                                        )
                                )
                        )
                )
        );
    }

    //地狱
    public static SurfaceRules.RuleSource makeConfluenceNetherRules() {
        return SurfaceRules.sequence(
                //灰烬森林
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.ASH_FOREST),
                        SurfaceRules.ifTrue(bedrockRoofSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, ashGrassSurface),
                                                ASH_BLOCK
                                        )
                                )
                        )
                ),

                //灰烬荒原
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.ASH_WASTELAND),
                        SurfaceRules.ifTrue(bedrockRoofSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        ASH_BLOCK
                                )
                        )
                )
        );
    }


    // minecraft =============================================================
    //主世界
    public static SurfaceRules.RuleSource makeMinecraftOverWorldRules() {
        return SurfaceRules.sequence(
                //海洋
                SurfaceRules.ifTrue(isOcean,
                        SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(sandSeed,
                                                HARDENED_SAND
                                        ),
                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(6, false, 3, CaveSurface.FLOOR),
                                                SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                        SurfaceRules.sequence(
                                                                SurfaceRules.ifTrue(sandOcean,
                                                                        MOIST_SAND
                                                                ),
                                                                GRAVEL
                                                        )
                                                )
                                        ),
                                        DIATOMACEOUS
                                )
                        )
                ),

                //丛林
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        jungleGrassSurface
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        MUD
                                )
                        )
                ),

                //沙漠
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.DESERT),
                        SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, 0, CaveSurface.FLOOR),
                                                SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                        SAND
                                                )
                                        ),
                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(10, false, 5, CaveSurface.FLOOR),
                                                SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                        SANDSTONE
                                                )
                                        ),
                                        HARDENED_SAND
                                )
                        )
                ),

                //恶地
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS),
                        SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, 0, CaveSurface.FLOOR),
                                                SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                        SurfaceRules.ifTrue(redSandSeed,
                                                                RED_SAND
                                                        )
                                                )
                                        ),
                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(10, false, 5, CaveSurface.FLOOR),
                                                SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                        SurfaceRules.bandlands()
                                                )
                                        ),
                                        RED_HARDENED_SAND
                                )
                        )
                ),

                //雪原
                SurfaceRules.ifTrue(dirtSnowy,
                        SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(iceSeed,
                                                PACKED_ICE
                                        ),
                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(6, false, 1, CaveSurface.FLOOR),
                                                SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                        SurfaceRules.sequence(
                                                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                                        grassSurface
                                                                ),
                                                                DIRT
                                                        )
                                                )
                                        ),
                                        SNOW
                                )
                        )
                ),

                //冰刺
                SurfaceRules.ifTrue(snowy,
                        SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(iceSeed,
                                                PACKED_ICE
                                        ),
                                        SNOW
                                )
                        )
                )
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}