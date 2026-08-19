package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.biome.*;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

import static org.confluence.mod.Confluence.MODID;

/// Confluence 自定义群系的资源键与 TerraBlender 注册入口。
///
/// <p>群系本身由数据包 JSON 定义；本类另外负责把部分群系嵌入原版的多噪声参数空间，
/// 并把对应的地表规则交给 TerraBlender。如果只存在 JSON 而没有调用
/// {@link #registerRegionAndSurface()}，这些群系虽然能进入注册表，却不会自然出现在新区块中。</p>
public final class ModBiomes {
    public static final ResourceKey<Biome> THE_CORRUPTION = register("the_corruption");
    public static final ResourceKey<Biome> THE_CORRUPTION_DESERT = register("the_corruption_desert");
    public static final ResourceKey<Biome> THE_CORRUPTION_TUNDRA = register("the_corruption_tundra");
    public static final ResourceKey<Biome> THE_CRIMSON = register("the_crimson");
    public static final ResourceKey<Biome> THE_CRIMSON_DESERT = register("the_crimson_desert");
    public static final ResourceKey<Biome> THE_CRIMSON_TUNDRA = register("the_crimson_tundra");
    public static final ResourceKey<Biome> THE_HALLOW = register("the_hallow");
    public static final ResourceKey<Biome> THE_HALLOW_DESERT = register("the_hallow_desert");
    public static final ResourceKey<Biome> THE_HALLOW_TUNDRA = register("the_hallow_tundra");
    public static final ResourceKey<Biome> ASH_FOREST = register("ash_forest");
    public static final ResourceKey<Biome> ASH_WASTELAND = register("ash_wasteland");
    public static final ResourceKey<Biome> GLOWING_MUSHROOM = register("glowing_mushroom");
    public static final ResourceKey<Biome> CHORUS_FOREST = register("chorus_forest");
    public static final ResourceKey<Biome> CHORUS_PLAINS = register("chorus_plains");
    public static final ResourceKey<Biome> INVERSE_FOREST = register("inverse_forest");
    public static final ResourceKey<Biome> INVERSE_PLAINS = register("inverse_plains");
    public static final ResourceKey<Biome> MOONBLIGHT_FOREST = register("moonblight_forest");
    public static final ResourceKey<Biome> MOONBLIGHT_PLAINS = register("moonblight_plains");
    public static final ResourceKey<Biome> MOONLIT_DRY_SEA = register("moonlit_dry_sea");
    public static final ResourceKey<Biome> DARK_MOON_FLATS = register("dark_moon_flats");

    private static ResourceKey<Biome> register(String name) {
        return ResourceKey.create(Registries.BIOME, Confluence.asResource(name));
    }

    /// 注册五个会参与自然生成的区域，并安装三个维度的地表规则。
    ///
    /// <p>权重只决定同类 TerraBlender Region 之间被选中的相对频率；具体落点仍由各 Region
    /// 定义的温度、湿度、大陆性、侵蚀度、深度与怪异度范围约束。该方法只能在通用启动队列中调用一次。</p>
    public static void registerRegionAndSurface() {
        Regions.register(new TheCrimsonRegion(Confluence.asResource("the_crimson"), 1));
        Regions.register(new TheCorruptionRegion(Confluence.asResource("the_corruption"), 1));
        Regions.register(new GlowingMushroomRegion(Confluence.asResource("glowing_mushroom"), 2));
        Regions.register(new AshForestRegion(Confluence.asResource("ash_forest"), 1));
        Regions.register(new AshWastelandRegion(Confluence.asResource("ash_wasteland"), 1));

        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MODID, SurfaceRuleData.makeConfluenceOverWorldRules());
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.NETHER, MODID, SurfaceRuleData.makeConfluenceNetherRules());
        // TerraBlender 3.x 的 Forge 1.20.1 API 只支持主世界和下界类别。
        // 末地群系由 TheEndBiomeSourceMixin 选择，地表规则则由 NoiseGeneratorSettingsMixin
        // 对默认方块为末地石的噪声设置进行组合，避免伪造不存在的 RuleCategory.END。

        // 这组兼容规则在原版基岩层规则之前执行，使泰拉地表材料能覆盖匹配的原版群系。
        SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.OVERWORLD, SurfaceRuleManager.RuleStage.BEFORE_BEDROCK, 0, SurfaceRuleData.makeMinecraftOverWorldRules());
    }
}
