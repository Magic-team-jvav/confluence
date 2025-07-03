package org.confluence.mod.common.init.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.item.common.*;
import org.confluence.terra_curio.common.item.MagicMirror;

public class ToolItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<HoneyBucketItem> HONEY_BUCKET = ITEMS.register("honey_bucket", HoneyBucketItem::new);
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_WATER_BUCKET = ITEMS.register("bottomless_water_bucket", () -> new BottomlessBucketItem(Fluids.WATER, ModRarity.LIME));
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_LAVA_BUCKET = ITEMS.register("bottomless_lava_bucket", () -> new BottomlessBucketItem(Fluids.LAVA, ModRarity.LIME));
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_HONEY_BUCKET = ITEMS.register("bottomless_honey_bucket", () -> new BottomlessBucketItem(ModFluids.HONEY.fluid().get(), ModRarity.LIME));
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_SHIMMER_BUCKET = ITEMS.register("bottomless_shimmer_bucket", () -> new BottomlessBucketItem(ModFluids.SHIMMER.fluid().get(), ModRarity.RED));

    public static final DeferredItem<SpongeItem> SUPER_ABSORBANT_SPONGE = ITEMS.register("super_absorbant_sponge", () -> new SpongeItem(ModRarity.LIME, "super_absorbant_sponge", 2, state -> state.is(Blocks.WATER) || state.is(ModBlocks.SHIMMER)));
    public static final DeferredItem<SpongeItem> HONEY_ABSORBANT_SPONGE = ITEMS.register("honey_absorbant_sponge", () -> new SpongeItem(ModRarity.LIME, "honey_absorbant_sponge", 2, state -> state.is(ModBlocks.HONEY)));
    public static final DeferredItem<SpongeItem> LAVA_ABSORBANT_SPONGE = ITEMS.register("lava_absorbant_sponge", () -> new SpongeItem(ModRarity.LIME, "lava_absorbant_sponge", 2, state -> state.is(Blocks.LAVA)));
    public static final DeferredItem<SpongeItem> ULTRA_ABSORBANT_SPONGE = ITEMS.register("ultra_absorbant_sponge", () -> new SpongeItem(ModRarity.YELLOW, "ultra_absorbant_sponge", 2, state -> state.is(Blocks.WATER) || state.is(ModBlocks.SHIMMER) || state.is(ModBlocks.HONEY) || state.is(Blocks.LAVA)));

    public static final DeferredItem<TooltipItem> GOLDEN_DUNGEON_KEY = ITEMS.register("golden_dungeon_key", () -> new TooltipItem(new Item.Properties(), ModRarity.WHITE, TooltipItem.getTooltipsFromString("golden_dungeon_key", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<TooltipItem> GOLDEN_KEY = ITEMS.register("golden_key", () -> new TooltipItem(new Item.Properties(), ModRarity.WHITE, TooltipItem.getTooltipsFromString("golden_key", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<TooltipItem> SHADOW_KEY = ITEMS.register("shadow_key", () -> new TooltipItem(new Item.Properties(), ModRarity.WHITE, TooltipItem.getTooltipsFromString("shadow_key", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<TooltipItem> TEMPLE_KEY = ITEMS.register("temple_key", () -> new TooltipItem(new Item.Properties(), ModRarity.LIME, TooltipItem.getTooltipsFromString("temple_key", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<TooltipItem> JUNGLE_KEY = ITEMS.register("jungle_key", () -> new TooltipItem(new Item.Properties(), ModRarity.YELLOW, TooltipItem.getTooltipsFromString("jungle_key", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<TooltipItem> CORRUPTION_KEY = ITEMS.register("corruption_key", () -> new TooltipItem(new Item.Properties(), ModRarity.YELLOW, TooltipItem.getTooltipsFromString("corruption_key", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<TooltipItem> CRIMSON_KEY = ITEMS.register("crimson_key", () -> new TooltipItem(new Item.Properties(), ModRarity.YELLOW, TooltipItem.getTooltipsFromString("crimson_key", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<TooltipItem> HALLOWED_KEY = ITEMS.register("hallowed_key", () -> new TooltipItem(new Item.Properties(), ModRarity.YELLOW, TooltipItem.getTooltipsFromString("hallowed_key", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<TooltipItem> FROZEN_KEY = ITEMS.register("frozen_key", () -> new TooltipItem(new Item.Properties(), ModRarity.YELLOW, TooltipItem.getTooltipsFromString("frozen_key", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<TooltipItem> DESERT_KEY = ITEMS.register("desert_key", () -> new TooltipItem(new Item.Properties(), ModRarity.YELLOW, TooltipItem.getTooltipsFromString("desert_key", 1, ChatFormatting.GRAY)));

    public static final DeferredItem<WrenchItem> RED_WRENCH = ITEMS.register("red_wrench", () -> new WrenchItem(0xFF0000));
    public static final DeferredItem<WrenchItem> GREEN_WRENCH = ITEMS.register("green_wrench", () -> new WrenchItem(0x00FF00));
    public static final DeferredItem<WrenchItem> BLUE_WRENCH = ITEMS.register("blue_wrench", () -> new WrenchItem(0x0000FF));
    public static final DeferredItem<WrenchItem> YELLOW_WRENCH = ITEMS.register("yellow_wrench", () -> new WrenchItem(0xFFFF00));
    public static final DeferredItem<WireCutterItem> WIRE_CUTTER = ITEMS.register("wire_cutter", WireCutterItem::new);

    public static final DeferredItem<EncumberingStoneItem> ENCUMBERING_STONE = ITEMS.register("encumbering_stone", EncumberingStoneItem::new);
    public static final DeferredItem<MagicMirror> ICE_MIRROR = ITEMS.register("ice_mirror", () -> new MagicMirror(ModRarity.BLUE));
    public static final DeferredItem<MagicConch> MAGIC_CONCH = ITEMS.register("magic_conch", () -> new MagicConch(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final DeferredItem<DemonConch> DEMON_CONCH = ITEMS.register("demon_conch", DemonConch::new);

    public static final DeferredItem<BugNetItem> BUG_NET = ITEMS.register("bug_net", () -> new BugNetItem(ModRarity.BLUE, 0.5, living -> living instanceof Animal));
    public static final DeferredItem<BugNetItem> GOLDEN_BUG_NET = ITEMS.register("golden_bug_net", () -> new BugNetItem(ModRarity.QUEST, 1.1, living -> living instanceof Animal));
    public static final DeferredItem<BugNetItem> DEV_BUG_NET = ITEMS.register("dev_bug_net", () -> new BugNetItem(ModRarity.MASTER, Double.MAX_VALUE, living -> !(living instanceof Player)));

    public static final DeferredItem<RopeCoilItem> ROPE_COIL = ITEMS.register("rope_coil", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.ROPE.get()));
    public static final DeferredItem<RopeCoilItem> VINE_ROPE_COIL = ITEMS.register("vine_rope_coil", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.VINE_ROPE.get()));
    public static final DeferredItem<RopeCoilItem> SILK_ROPE_COIL = ITEMS.register("silk_rope_coil", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.SILK_ROPE.get()));
    public static final DeferredItem<RopeCoilItem> WEB_ROPE_COIL = ITEMS.register("web_rope_coil", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.WEB_ROPE.get()));

    public static final DeferredItem<TargetDummyItem> TARGET_DUMMY = ITEMS.register("target_dummy", TargetDummyItem::new);
    public static final DeferredItem<TooltipItem> METEOR_COMPASS = ITEMS.register("meteor_compass", () -> new TooltipItem(new Item.Properties().stacksTo(1), ModRarity.BLUE, Component.translatable("tooltip.item.confluence.meteor_compass.0").withStyle(ChatFormatting.RED)));
    public static final DeferredItem<BinocularsItem> BINOCULARS = ITEMS.register("binoculars", BinocularsItem::new);
    public static final DeferredItem<NPCInvitationItem> NPC_INVITATION = ITEMS.register("npc_invitation", NPCInvitationItem::new);
}
