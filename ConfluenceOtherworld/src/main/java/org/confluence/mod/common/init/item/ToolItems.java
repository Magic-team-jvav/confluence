package org.confluence.mod.common.init.item;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.item.common.*;
import org.confluence.mod.common.item.storage.VoidBagItem;
import org.confluence.terra_curio.common.item.MagicMirror;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

@ParametersAreNonnullByDefault
public class ToolItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<VoidBagItem> VOID_BAG = registerItem("void_bag", "虚空袋", VoidBagItem::new);

    public static final DeferredItem<HoneyBucketItem> HONEY_BUCKET = registerItem("honey_bucket", "蜂蜜桶", HoneyBucketItem::new);
    public static final DeferredItem<VoidBucketItem> VOID_BUCKET = registerItem("void_bucket", "虚空桶", VoidBucketItem::new);
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_WATER_BUCKET = registerItem("bottomless_water_bucket", "无底水桶", () -> new BottomlessBucketItem(Fluids.WATER, ModRarity.LIME));
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_LAVA_BUCKET = registerItem("bottomless_lava_bucket", "无底熔岩桶", () -> new BottomlessBucketItem(Fluids.LAVA, ModRarity.LIME));
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_HONEY_BUCKET = registerItem("bottomless_honey_bucket", "无底蜂蜜桶", () -> new BottomlessBucketItem(ModFluids.HONEY.fluid().get(), ModRarity.LIME));
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_SHIMMER_BUCKET = registerItem("bottomless_shimmer_bucket", "无底微光桶", () -> new BottomlessBucketItem(ModFluids.SHIMMER.fluid().get(), ModRarity.RED));

    public static final DeferredItem<SpongeItem> SUPER_ABSORBANT_SPONGE = registerItem("super_absorbant_sponge", "超级吸收绵", () -> new SpongeItem(ModRarity.LIME, "super_absorbant_sponge", 2, state -> state.is(Blocks.WATER) || state.is(ModBlocks.SHIMMER)));
    public static final DeferredItem<SpongeItem> HONEY_ABSORBANT_SPONGE = registerItem("honey_absorbant_sponge", "蜂蜜吸收绵", () -> new SpongeItem(ModRarity.LIME, "honey_absorbant_sponge", 2, state -> state.is(ModBlocks.HONEY)));
    public static final DeferredItem<SpongeItem> LAVA_ABSORBANT_SPONGE = registerItem("lava_absorbant_sponge", "熔岩吸收绵", () -> new SpongeItem(ModRarity.LIME, "lava_absorbant_sponge", 2, state -> state.is(Blocks.LAVA)));
    public static final DeferredItem<SpongeItem> ULTRA_ABSORBANT_SPONGE = registerItem("ultra_absorbant_sponge", "超强吸收绵", () -> new SpongeItem(ModRarity.YELLOW, "ultra_absorbant_sponge", 2, state -> state.is(Blocks.WATER) || state.is(ModBlocks.SHIMMER) || state.is(ModBlocks.HONEY) || state.is(Blocks.LAVA)));

    public static final DeferredItem<CustomRarityItem> GOLDEN_DUNGEON_KEY = registerItem("golden_dungeon_key", "地牢金钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.WHITE));
    public static final DeferredItem<CustomRarityItem> GOLDEN_KEY = registerItem("golden_key", "金钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.WHITE));
    public static final DeferredItem<CustomRarityItem> SHADOW_KEY = registerItem("shadow_key", "暗影钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.WHITE));
    public static final DeferredItem<CustomRarityItem> TEMPLE_KEY = registerItem("temple_key", "神庙钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.LIME));

    public static final DeferredItem<CustomRarityItem> JUNGLE_KEY = registerItem("jungle_key", "丛林钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> CORRUPTION_KEY = registerItem("corruption_key", "腐化钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> CRIMSON_KEY = registerItem("crimson_key", "猩红钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> HALLOWED_KEY = registerItem("hallowed_key", "神圣钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> FROZEN_KEY = registerItem("frozen_key", "冰冻钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> DESERT_KEY = registerItem("desert_key", "沙漠钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> OCEAN_KEY = registerItem("ocean_key", "海洋钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> UNIVERSE_KEY = registerItem("universe_key", "太空钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> RUST_IRON_KEY = registerItem("rust_iron_key", "生锈的铁钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> MECHANIC_SAFE_KEY = registerItem("mechanic_safe_key", "机械师安全钥匙", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));

    public static final DeferredItem<CustomRarityItem> KEY_OF_LIGHT = registerItemWithEnglishName("key_of_light", "光明钥匙", "Key of Light", () -> new CustomRarityItem(new Item.Properties(), ModRarity.WHITE));
    public static final DeferredItem<CustomRarityItem> KEY_OF_NIGHT = registerItemWithEnglishName("key_of_night", "夜光钥匙", "Key of Night", () -> new CustomRarityItem(new Item.Properties(), ModRarity.WHITE));

    public static final DeferredItem<WrenchItem> RED_WRENCH = registerItem("red_wrench", "红扳手", () -> new WrenchItem(0xFF0000));
    public static final DeferredItem<WrenchItem> GREEN_WRENCH = registerItem("green_wrench", "绿扳手", () -> new WrenchItem(0x00FF00));
    public static final DeferredItem<WrenchItem> BLUE_WRENCH = registerItem("blue_wrench", "蓝扳手", () -> new WrenchItem(0x0000FF));
    public static final DeferredItem<WrenchItem> YELLOW_WRENCH = registerItem("yellow_wrench", "黄扳手", () -> new WrenchItem(0xFFFF00));
    public static final DeferredItem<WireCutterItem> WIRE_CUTTER = registerItem("wire_cutter", "钢丝钳", WireCutterItem::new);

    public static final DeferredItem<MagicMirror> ICE_MIRROR = registerItem("ice_mirror", "冰雪镜", () -> new MagicMirror(ModRarity.BLUE));
    public static final DeferredItem<MagicConch> MAGIC_CONCH = registerItem("magic_conch", "魔法海螺", () -> new MagicConch(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final DeferredItem<DemonConch> DEMON_CONCH = registerItem("demon_conch", "恶魔海螺", DemonConch::new);

    public static final DeferredItem<BugNetItem> BUG_NET = registerItem("bug_net", "虫网", () -> new BugNetItem(ModRarity.BLUE, 0.5, living -> LibEntityUtils.isAnimal(living) && !living.getType().is(ModTags.EntityTypes.LAVA_BUG_NET_ALLOWS)));
    public static final DeferredItem<BugNetItem> LAVAPROOF_BUG_NET = registerItem("lavaproof_bug_net", "防熔岩虫网", () -> new BugNetItem(ModRarity.ORANGE, 0.5, LibEntityUtils::isAnimal));
    public static final DeferredItem<BugNetItem> GOLDEN_BUG_NET = registerItem("golden_bug_net", "金虫网", () -> new BugNetItem(ModRarity.QUEST, 1.1, LibEntityUtils::isAnimal));
    public static final DeferredItem<BugNetItem> DEV_BUG_NET = registerItem("dev_bug_net", "开发者虫网", () -> new BugNetItem(ModRarity.MASTER, Double.MAX_VALUE, living -> !(living instanceof Player)));

    public static final DeferredItem<RopeCoilItem> ROPE_COIL = registerItem("rope_coil", "绳圈", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.ROPE.get()));
    public static final DeferredItem<RopeCoilItem> VINE_ROPE_COIL = registerItem("vine_rope_coil", "藤蔓绳圈", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.VINE_ROPE.get()));
    public static final DeferredItem<RopeCoilItem> SILK_ROPE_COIL = registerItem("silk_rope_coil", "丝绸绳圈", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.SILK_ROPE.get()));
    public static final DeferredItem<RopeCoilItem> WEB_ROPE_COIL = registerItem("web_rope_coil", "蛛丝绳圈", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.WEB_ROPE.get()));

    public static final DeferredItem<CustomRarityItem> METEOR_COMPASS = registerItem("meteor_compass", "陨石指南针", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final DeferredItem<BinocularsItem> BINOCULARS = registerItem("binoculars", "双筒望远镜", BinocularsItem::new);
    public static final DeferredItem<NPCInvitationItem> NPC_INVITATION = registerItem("npc_invitation", "NPC请柬", NPCInvitationItem::new);
    public static final DeferredItem<DungeonCompass> DUNGEON_COMPASS = registerItem("dungeon_compass", "地牢指南针", DungeonCompass::new);

    public static final DeferredItem<MagicDropperItem> EMPTY_DROPPER = registerItem("empty_dropper", "空滴管", () -> new MagicDropperItem(null) {
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        }
    });
    public static final DeferredItem<MagicDropperItem> MAGIC_SAND_DROPPER = registerItem("magic_sand_dropper", "魔法沙粒滴管", () -> new MagicDropperItem(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState())));
    public static final DeferredItem<MagicDropperItem> MAGIC_HONEY_DROPPER = registerItem("magic_honey_dropper", "魔法蜂蜜滴管", () -> new MagicDropperItem(ParticleTypes.DRIPPING_HONEY));
    public static final DeferredItem<MagicDropperItem> MAGIC_LAVA_DROPPER = registerItem("magic_lava_dropper", "魔法熔岩滴管", () -> new MagicDropperItem(ParticleTypes.DRIPPING_LAVA));
    public static final DeferredItem<MagicDropperItem> MAGIC_WATER_DROPPER = registerItem("magic_water_dropper", "魔法水滴管", () -> new MagicDropperItem(ParticleTypes.DRIPPING_WATER));

    public static final DeferredItem<EncumberingStoneItem> ENCUMBERING_STONE = registerItem("encumbering_stone", "负重石", EncumberingStoneItem::new);
    public static final DeferredItem<GuideToCritterCompanionshipItem> GUIDE_TO_CRITTER_COMPANIONSHIP = registerItemWithEnglishName("guide_to_critter_companionship", "小动物友谊指南", "Guide to Critter Companionship", GuideToCritterCompanionshipItem::new);
    public static final DeferredItem<GuideToEnvironmentalPreservationItem> GUIDE_TO_ENVIRONMENTAL_PRESERVATION = registerItemWithEnglishName("guide_to_environmental_preservation", "环境保护指南", "Guide to Environmental Preservation", GuideToEnvironmentalPreservationItem::new);
    public static final DeferredItem<GuideToPeacefulCoexistenceItem> GUIDE_TO_PEACEFUL_COEXISTENCE = registerItemWithEnglishName("guide_to_peaceful_coexistence", "和平共处指南", "Guide to Peaceful Coexistence", GuideToPeacefulCoexistenceItem::new);
    public static final DeferredItem<StaffOfRegrowth> STAFF_OF_REGROWTH = registerItemWithEnglishName("staff_of_regrowth", "再生法杖", "Staff of Regrowth", StaffOfRegrowth::new); // 再生法杖

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
