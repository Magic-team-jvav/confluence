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
    private static SurfaceRules.RuleSource state(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    private static final SurfaceRules.ConditionSource isAtOrAboveWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);
    private static final SurfaceRules.ConditionSource isHole = SurfaceRules.not(SurfaceRules.abovePreliminarySurface());

    private static final SurfaceRules.ConditionSource grassSeed = SurfaceRules.verticalGradient("minecraft:bedrock_floor", VerticalAnchor.absolute(48), VerticalAnchor.absolute(52));
    private static final SurfaceRules.ConditionSource deepslateSeed = SurfaceRules.verticalGradient("minecraft:deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8));
    private static final SurfaceRules.ConditionSource mushroomSeed = SurfaceRules.verticalGradient("minecraft:deepslate", VerticalAnchor.absolute(40), VerticalAnchor.absolute(42));
    private static final SurfaceRules.ConditionSource sandSeed = SurfaceRules.verticalGradient("minecraft:deepslate", VerticalAnchor.absolute(20), VerticalAnchor.absolute(25));
    private static final SurfaceRules.ConditionSource redSandSeed = SurfaceRules.verticalGradient("minecraft:deepslate", VerticalAnchor.absolute(74), VerticalAnchor.absolute(76));
    private static final SurfaceRules.ConditionSource iceSeed = SurfaceRules.verticalGradient("minecraft:deepslate", VerticalAnchor.absolute(20), VerticalAnchor.absolute(35));

    private static final SurfaceRules.ConditionSource bedrockRoofSeed = SurfaceRules.verticalGradient("minecraft:bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.belowTop(0));
    private static final SurfaceRules.ConditionSource bedrockFloorSeed = SurfaceRules.verticalGradient("minecraft:bedrock_floor", VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(5));

    // --- 组合规则 (改为方法获取，防止提前 .get()) ---

    private static SurfaceRules.RuleSource grassSurface() {
        return SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, state(Blocks.GRASS_BLOCK)), state(Blocks.DIRT));
    }

    private static SurfaceRules.RuleSource corruptGrassSurface() {
        return SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, state(NatureBlocks.CORRUPT_GRASS_BLOCK.get())), state(NatureBlocks.EBONSAND.get()));
    }

    private static SurfaceRules.RuleSource crimsonGrassSurface() {
        return SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, state(NatureBlocks.CRIMSON_GRASS_BLOCK.get())), state(NatureBlocks.CRIMSAND.get()));
    }

    private static SurfaceRules.RuleSource mushroomSurface() {
        return SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, state(NatureBlocks.MUSHROOM_GRASS_BLOCK.get())), state(Blocks.MUD));
    }

    private static SurfaceRules.RuleSource ashGrassSurface() {
        return SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, state(NatureBlocks.ASH_GRASS_BLOCK.get())), state(NatureBlocks.ASH_BLOCK.get()));
    }

    private static SurfaceRules.RuleSource chorusGrassSurface() {
        return SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, state(NatureBlocks.VOID_GRASS_BLOCK.get())), state(NatureBlocks.END_DIRT.get()));
    }

    private static SurfaceRules.RuleSource silverSoulGrassSurface() {
        return SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, state(NatureBlocks.MOONLIT_GRASS_BLOCK.get())), state(NatureBlocks.END_DIRT.get()));
    }

    private static SurfaceRules.RuleSource jungleGrassSurface() {
        return SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, state(NatureBlocks.JUNGLE_GRASS_BLOCK.get())), state(Blocks.MUD));
    }


    public static SurfaceRules.RuleSource makeConfluenceOverWorldRules() {
        return SurfaceRules.sequence(
                // 腐化
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.THE_CORRUPTION),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.not(grassSeed),
                                        SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                SurfaceRules.sequence(
                                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, corruptGrassSurface()),
                                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, 1, CaveSurface.FLOOR), state(Blocks.DIRT))
                                                )
                                        )
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed), state(NatureBlocks.EBONSTONE.get()))
                        )
                ),

                // 猩红
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.THE_CRIMSON),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.not(grassSeed),
                                        SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                SurfaceRules.sequence(
                                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, crimsonGrassSurface()),
                                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, 1, CaveSurface.FLOOR), state(Blocks.DIRT))
                                                )
                                        )
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed), state(NatureBlocks.CRIMSTONE.get()))
                        )
                ),

                // 发光蘑菇地
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.GLOWING_MUSHROOM),
                        SurfaceRules.ifTrue(mushroomSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, mushroomSurface()),
                                                state(Blocks.MUD)
                                        )
                                )
                        )
                )
        );
    }

    public static SurfaceRules.RuleSource makeConfluenceNetherRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.ASH_FOREST),
                        SurfaceRules.ifTrue(bedrockRoofSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, ashGrassSurface()),
                                                state(NatureBlocks.ASH_BLOCK.get())
                                        )
                                )
                        )
                ),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.ASH_WASTELAND),
                        SurfaceRules.ifTrue(bedrockRoofSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        state(NatureBlocks.ASH_BLOCK.get())
                                )
                        )
                )
        );
    }

    public static SurfaceRules.RuleSource makeConfluenceEndRules() {
        return SurfaceRules.sequence(
                // 紫颂森林
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.CHORUS_FOREST),
                        SurfaceRules.ifTrue(bedrockRoofSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, chorusGrassSurface()),
                                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, 1, CaveSurface.FLOOR), state(NatureBlocks.END_DIRT.get())),
                                                state(Blocks.END_STONE)
                                        )
                                )
                        )
                ),

                // 紫颂森林
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.CHORUS_PLAINS),
                        SurfaceRules.ifTrue(bedrockRoofSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, chorusGrassSurface()),
                                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, 1, CaveSurface.FLOOR), state(NatureBlocks.END_DIRT.get())),
                                                state(Blocks.END_STONE)
                                        )
                                )
                        )
                ),

                // 倒悬森林
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.INVERSE_FOREST),
                        SurfaceRules.ifTrue(bedrockRoofSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, state(NatureBlocks.INVERSE_GRASS_BLOCK.get())),
                                                state(Blocks.END_STONE)
                                        )
                                )
                        )
                ),

                // 倒悬平原
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.INVERSE_PLAINS),
                        SurfaceRules.ifTrue(bedrockRoofSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, state(NatureBlocks.INVERSE_GRASS_BLOCK.get())),
                                                state(Blocks.END_STONE)
                                        )
                                )
                        )
                ),

                // 月蚀森林
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.MOONBLIGHT_FOREST),
                        SurfaceRules.ifTrue(bedrockRoofSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, silverSoulGrassSurface()),
                                                state(Blocks.END_STONE)
                                        )
                                )
                        )
                ),

                // 月蚀平原
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.MOONBLIGHT_PLAINS),
                        SurfaceRules.ifTrue(bedrockRoofSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, silverSoulGrassSurface()),
                                                state(Blocks.END_STONE)
                                        )
                                )
                        )
                ),

                // 朔月滩涂
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.DARK_MOON_FLATS),
                        SurfaceRules.ifTrue(bedrockRoofSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, 1, CaveSurface.FLOOR), state(NatureBlocks.END_DIRT.get())),
                                                state(Blocks.END_STONE)
                                        )
                                )
                        )
                ),

                // 映月空海
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.MOONLIT_DRY_SEA),
                        SurfaceRules.ifTrue(bedrockRoofSeed,
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(6, false, 1, CaveSurface.FLOOR), state(NatureBlocks.END_DIRT.get())),
                                                state(Blocks.END_STONE)
                                        )
                                )
                        )
                )
        );
    }

    public static SurfaceRules.RuleSource makeMinecraftOverWorldRules() {
        SurfaceRules.ConditionSource isOcean = SurfaceRules.isBiome(Biomes.OCEAN, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN);
        SurfaceRules.ConditionSource sandOcean = SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN);

        return SurfaceRules.sequence(
                // 海洋
                SurfaceRules.ifTrue(isOcean,
                        SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(sandSeed, state(NatureBlocks.HARDENED_SAND_BLOCK.get())),
                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(6, false, 3, CaveSurface.FLOOR),
                                                SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                        SurfaceRules.sequence(
                                                                SurfaceRules.ifTrue(sandOcean, state(NatureBlocks.MOISTENED_SAND_BLOCK.get())),
                                                                state(Blocks.GRAVEL)
                                                        )
                                                )
                                        ),
                                        state(NatureBlocks.DIATOMACEOUS.get())
                                )
                        )
                ),

                // 丛林
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, jungleGrassSurface()),
                                SurfaceRules.ifTrue(SurfaceRules.not(bedrockFloorSeed), state(Blocks.MUD))
                        )
                ),

                // 沙漠
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.DESERT),
                        SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, 0, CaveSurface.FLOOR),
                                                SurfaceRules.ifTrue(SurfaceRules.not(isHole), state(Blocks.SAND))
                                        ),
                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(10, false, 5, CaveSurface.FLOOR),
                                                SurfaceRules.ifTrue(SurfaceRules.not(isHole), state(Blocks.SANDSTONE))
                                        ),
                                        state(NatureBlocks.HARDENED_SAND_BLOCK.get())
                                )
                        )
                ),

                // 恶地
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS),
                        SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, 0, CaveSurface.FLOOR),
                                                SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                        SurfaceRules.ifTrue(redSandSeed, state(Blocks.RED_SAND))
                                                )
                                        ),
                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(10, false, 5, CaveSurface.FLOOR),
                                                SurfaceRules.ifTrue(SurfaceRules.not(isHole), SurfaceRules.bandlands())
                                        ),
                                        state(NatureBlocks.HARDENED_RED_SAND_BLOCK.get())
                                )
                        )
                ),

                // 雪原
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA),
                        SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(iceSeed, state(Blocks.PACKED_ICE)),
                                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(6, false, 1, CaveSurface.FLOOR),
                                                SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                        SurfaceRules.sequence(
                                                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, grassSurface()),
                                                                state(Blocks.DIRT)
                                                        )
                                                )
                                        ),
                                        state(Blocks.SNOW_BLOCK)
                                )
                        )
                ),

                // 冰刺
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.ICE_SPIKES, Biomes.GROVE),
                        SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(iceSeed, state(Blocks.PACKED_ICE)),
                                        state(Blocks.SNOW_BLOCK)
                                )
                        )
                )
        );
    }
}
