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
import org.confluence.terra_curio.common.item.MagicMirror;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ToolItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<HoneyBucketItem> HONEY_BUCKET = ITEMS.register("honey_bucket", HoneyBucketItem::new);
    public static final DeferredItem<VoidBucketItem> VOID_BUCKET = ITEMS.register("void_bucket", VoidBucketItem::new);
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_WATER_BUCKET = ITEMS.register("bottomless_water_bucket", () -> new BottomlessBucketItem(Fluids.WATER, ModRarity.LIME));
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_LAVA_BUCKET = ITEMS.register("bottomless_lava_bucket", () -> new BottomlessBucketItem(Fluids.LAVA, ModRarity.LIME));
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_HONEY_BUCKET = ITEMS.register("bottomless_honey_bucket", () -> new BottomlessBucketItem(ModFluids.HONEY.fluid().get(), ModRarity.LIME));
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_SHIMMER_BUCKET = ITEMS.register("bottomless_shimmer_bucket", () -> new BottomlessBucketItem(ModFluids.SHIMMER.fluid().get(), ModRarity.RED));

    public static final DeferredItem<SpongeItem> SUPER_ABSORBANT_SPONGE = ITEMS.register("super_absorbant_sponge", () -> new SpongeItem(ModRarity.LIME, "super_absorbant_sponge", 2, state -> state.is(Blocks.WATER) || state.is(ModBlocks.SHIMMER)));
    public static final DeferredItem<SpongeItem> HONEY_ABSORBANT_SPONGE = ITEMS.register("honey_absorbant_sponge", () -> new SpongeItem(ModRarity.LIME, "honey_absorbant_sponge", 2, state -> state.is(ModBlocks.HONEY)));
    public static final DeferredItem<SpongeItem> LAVA_ABSORBANT_SPONGE = ITEMS.register("lava_absorbant_sponge", () -> new SpongeItem(ModRarity.LIME, "lava_absorbant_sponge", 2, state -> state.is(Blocks.LAVA)));
    public static final DeferredItem<SpongeItem> ULTRA_ABSORBANT_SPONGE = ITEMS.register("ultra_absorbant_sponge", () -> new SpongeItem(ModRarity.YELLOW, "ultra_absorbant_sponge", 2, state -> state.is(Blocks.WATER) || state.is(ModBlocks.SHIMMER) || state.is(ModBlocks.HONEY) || state.is(Blocks.LAVA)));

    public static final DeferredItem<CustomRarityItem> GOLDEN_DUNGEON_KEY = ITEMS.register("golden_dungeon_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.WHITE));
    public static final DeferredItem<CustomRarityItem> GOLDEN_KEY = ITEMS.register("golden_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.WHITE));
    public static final DeferredItem<CustomRarityItem> SHADOW_KEY = ITEMS.register("shadow_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.WHITE));
    public static final DeferredItem<CustomRarityItem> TEMPLE_KEY = ITEMS.register("temple_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.LIME));

    public static final DeferredItem<CustomRarityItem> JUNGLE_KEY = ITEMS.register("jungle_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> CORRUPTION_KEY = ITEMS.register("corruption_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> CRIMSON_KEY = ITEMS.register("crimson_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> HALLOWED_KEY = ITEMS.register("hallowed_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> FROZEN_KEY = ITEMS.register("frozen_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> DESERT_KEY = ITEMS.register("desert_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> OCEAN_KEY = ITEMS.register("ocean_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> UNIVERSE_KEY = ITEMS.register("universe_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> RUST_IRON_KEY = ITEMS.register("rust_iron_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));
    public static final DeferredItem<CustomRarityItem> MECHANIC_SAFE_KEY = ITEMS.register("mechanic_safe_key", () -> new CustomRarityItem(new Item.Properties(), ModRarity.YELLOW));

    public static final DeferredItem<CustomRarityItem> KEY_OF_LIGHT = ITEMS.register("key_of_light", () -> new CustomRarityItem(new Item.Properties(), ModRarity.WHITE));
    public static final DeferredItem<CustomRarityItem> KEY_OF_NIGHT = ITEMS.register("key_of_night", () -> new CustomRarityItem(new Item.Properties(), ModRarity.WHITE));

    public static final DeferredItem<WrenchItem> RED_WRENCH = ITEMS.register("red_wrench", () -> new WrenchItem(0xFF0000));
    public static final DeferredItem<WrenchItem> GREEN_WRENCH = ITEMS.register("green_wrench", () -> new WrenchItem(0x00FF00));
    public static final DeferredItem<WrenchItem> BLUE_WRENCH = ITEMS.register("blue_wrench", () -> new WrenchItem(0x0000FF));
    public static final DeferredItem<WrenchItem> YELLOW_WRENCH = ITEMS.register("yellow_wrench", () -> new WrenchItem(0xFFFF00));
    public static final DeferredItem<WireCutterItem> WIRE_CUTTER = ITEMS.register("wire_cutter", WireCutterItem::new);

    public static final DeferredItem<MagicMirror> ICE_MIRROR = ITEMS.register("ice_mirror", () -> new MagicMirror(ModRarity.BLUE));
    public static final DeferredItem<MagicConch> MAGIC_CONCH = ITEMS.register("magic_conch", () -> new MagicConch(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final DeferredItem<DemonConch> DEMON_CONCH = ITEMS.register("demon_conch", DemonConch::new);

    public static final DeferredItem<BugNetItem> BUG_NET = ITEMS.register("bug_net", () -> new BugNetItem(ModRarity.BLUE, 0.5, living -> LibEntityUtils.isAnimal(living) && !living.getType().is(ModTags.EntityTypes.LAVA_BUG_NET_ALLOWS)));
    public static final DeferredItem<BugNetItem> LAVAPROOF_BUG_NET = ITEMS.register("lavaproof_bug_net", () -> new BugNetItem(ModRarity.ORANGE, 0.5, LibEntityUtils::isAnimal));
    public static final DeferredItem<BugNetItem> GOLDEN_BUG_NET = ITEMS.register("golden_bug_net", () -> new BugNetItem(ModRarity.QUEST, 1.1, LibEntityUtils::isAnimal));
    public static final DeferredItem<BugNetItem> DEV_BUG_NET = ITEMS.register("dev_bug_net", () -> new BugNetItem(ModRarity.MASTER, Double.MAX_VALUE, living -> !(living instanceof Player)));

    public static final DeferredItem<RopeCoilItem> ROPE_COIL = ITEMS.register("rope_coil", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.ROPE.get()));
    public static final DeferredItem<RopeCoilItem> VINE_ROPE_COIL = ITEMS.register("vine_rope_coil", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.VINE_ROPE.get()));
    public static final DeferredItem<RopeCoilItem> SILK_ROPE_COIL = ITEMS.register("silk_rope_coil", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.SILK_ROPE.get()));
    public static final DeferredItem<RopeCoilItem> WEB_ROPE_COIL = ITEMS.register("web_rope_coil", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.WEB_ROPE.get()));

    public static final DeferredItem<CustomRarityItem> METEOR_COMPASS = ITEMS.register("meteor_compass", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final DeferredItem<BinocularsItem> BINOCULARS = ITEMS.register("binoculars", BinocularsItem::new);
    public static final DeferredItem<NPCInvitationItem> NPC_INVITATION = ITEMS.register("npc_invitation", NPCInvitationItem::new);
    public static final DeferredItem<DungeonCompass> DUNGEON_COMPASS = ITEMS.register("dungeon_compass", DungeonCompass::new);

    public static final DeferredItem<MagicDropperItem> EMPTY_DROPPER = ITEMS.register("empty_dropper", () -> new MagicDropperItem(null) {
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        }
    });
    public static final DeferredItem<MagicDropperItem> MAGIC_SAND_DROPPER = ITEMS.register("magic_sand_dropper", () -> new MagicDropperItem(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState())));
    public static final DeferredItem<MagicDropperItem> MAGIC_HONEY_DROPPER = ITEMS.register("magic_honey_dropper", () -> new MagicDropperItem(ParticleTypes.DRIPPING_HONEY));
    public static final DeferredItem<MagicDropperItem> MAGIC_LAVA_DROPPER = ITEMS.register("magic_lava_dropper", () -> new MagicDropperItem(ParticleTypes.DRIPPING_LAVA));
    public static final DeferredItem<MagicDropperItem> MAGIC_WATER_DROPPER = ITEMS.register("magic_water_dropper", () -> new MagicDropperItem(ParticleTypes.DRIPPING_WATER));

    public static final DeferredItem<EncumberingStoneItem> ENCUMBERING_STONE = ITEMS.register("encumbering_stone", EncumberingStoneItem::new);
    public static final DeferredItem<GuideToCritterCompanionshipItem> GUIDE_TO_CRITTER_COMPANIONSHIP = ITEMS.register("guide_to_critter_companionship", GuideToCritterCompanionshipItem::new);
    public static final DeferredItem<GuideToEnvironmentalPreservationItem> GUIDE_TO_ENVIRONMENTAL_PRESERVATION = ITEMS.register("guide_to_environmental_preservation", GuideToEnvironmentalPreservationItem::new);
    public static final DeferredItem<GuideToPeacefulCoexistenceItem> GUIDE_TO_PEACEFUL_COEXISTENCE = ITEMS.register("guide_to_peaceful_coexistence", GuideToPeacefulCoexistenceItem::new);
    public static final DeferredItem<StaffOfRegrowth> STAFF_OF_REGROWTH = ITEMS.register("staff_of_regrowth", StaffOfRegrowth::new); // 再生法杖
}
