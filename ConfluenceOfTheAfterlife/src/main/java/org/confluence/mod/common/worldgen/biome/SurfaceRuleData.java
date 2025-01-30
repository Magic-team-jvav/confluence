package org.confluence.mod.common.worldgen.biome;

import com.xiaohunao.isekai_invaded.common.data.gen.provider.IITagProviders;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.block.NatureBlocks;

public class SurfaceRuleData {
    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource CLAY = makeStateRule(Blocks.CLAY);

    private static final SurfaceRules.RuleSource CORRUPT_GRASS_BLOCK = makeStateRule(NatureBlocks.CORRUPT_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource EBONY_STONE = makeStateRule(NatureBlocks.EBONY_STONE.get());

    private static final SurfaceRules.RuleSource TR_CRIMSON_GRASS_BLOCK = makeStateRule(NatureBlocks.TR_CRIMSON_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource TR_CRIMSON_STONE = makeStateRule(NatureBlocks.TR_CRIMSON_STONE.get());

    private static final SurfaceRules.RuleSource MUSHROOM_GRASS_BLOCK = makeStateRule(NatureBlocks.MUSHROOM_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource MUD = makeStateRule(Blocks.MUD);
    private static final SurfaceRules.RuleSource ASH_BLOCK = makeStateRule(NatureBlocks.ASH_BLOCK.get());
    private static final SurfaceRules.RuleSource ASH_GRASS_BLOCK = makeStateRule(NatureBlocks.ASH_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource DIATOMACEOUS = makeStateRule(NatureBlocks.DIATOMACEOUS.get());

    // 主世界 =============================================================
    public static SurfaceRules.RuleSource makeOverWorldRules() {

        // 判断 =============================================================
        //表面判断
        SurfaceRules.ConditionSource isAtOrAboveWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.ConditionSource isUnderWaterLevel = SurfaceRules.waterStartCheck(-6, -1);
        SurfaceRules.ConditionSource isHole = SurfaceRules.hole();
        //渐变层随机种子
        SurfaceRules.ConditionSource bedrockRoofSeed = SurfaceRules.verticalGradient("minecraft:bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.belowTop(0));
        SurfaceRules.ConditionSource bedrockFloorSeed = SurfaceRules.verticalGradient("minecraft:bedrock_floor", VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(5));
        SurfaceRules.ConditionSource deepslateSeed = SurfaceRules.verticalGradient("minecraft:deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8));
        SurfaceRules.ConditionSource grassSeed = SurfaceRules.verticalGradient("minecraft:bedrock_floor", VerticalAnchor.absolute(48), VerticalAnchor.absolute(52));
        //群系组合
        SurfaceRules.ConditionSource isOcean = SurfaceRules.isBiome(Biomes.OCEAN, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN);

        // 生成 =============================================================
        SurfaceRules.RuleSource corruptGrassSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, CORRUPT_GRASS_BLOCK), DIRT);
        SurfaceRules.RuleSource trCrimsonGrassSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, TR_CRIMSON_GRASS_BLOCK), DIRT);
        SurfaceRules.RuleSource mushroomSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isUnderWaterLevel, MUSHROOM_GRASS_BLOCK), MUD);
        return SurfaceRules.sequence(
                //腐化
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.THE_CORRUPTION),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                SurfaceRules.ifTrue(SurfaceRules.not(grassSeed),
                                                        corruptGrassSurface
                                                )
                                        )
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                        EBONY_STONE
                                )
                        )
                ),

                //猩红
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.TR_CRIMSON),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(SurfaceRules.not(isHole),
                                                SurfaceRules.ifTrue(SurfaceRules.not(grassSeed),
                                                        trCrimsonGrassSurface
                                                )
                                        )
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.not(deepslateSeed),
                                        TR_CRIMSON_STONE
                                )
                        )
                ),

                //发光蘑菇地
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.GLOWING_MUSHROOM),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        mushroomSurface
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                        MUD
                                )
                        )
                )
        );
    }

    // 地狱 =============================================================
    public static SurfaceRules.RuleSource makeNetherRules() {

        // 判断 =============================================================
        //表面判断
        SurfaceRules.ConditionSource isAtOrAboveWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.ConditionSource isUnderWaterLevel = SurfaceRules.waterStartCheck(-6, -1);
        SurfaceRules.ConditionSource isHole = SurfaceRules.hole();
        //渐变层随机种子
        SurfaceRules.ConditionSource bedrockRoofSeed = SurfaceRules.verticalGradient("minecraft:bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.belowTop(0));
        SurfaceRules.ConditionSource bedrockFloorSeed = SurfaceRules.verticalGradient("minecraft:bedrock_floor", VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(5));

        // 生成 =============================================================
        SurfaceRules.RuleSource ashGrassSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, ASH_GRASS_BLOCK), ASH_BLOCK);
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

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}