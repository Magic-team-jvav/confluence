package org.confluence.mod.common.init.item;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.mod.common.item.common.*;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.item.MagicMirror;

import java.util.function.Supplier;

public class ToolItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<HoneyBucketItem> HONEY_BUCKET = ITEMS.register("honey_bucket", HoneyBucketItem::new);
    public static final Supplier<BottomlessBucketItem> BOTTOMLESS_WATER_BUCKET = ITEMS.register("bottomless_water_bucket", () -> new BottomlessBucketItem(Fluids.WATER, ModRarity.LIME));
    public static final Supplier<BottomlessBucketItem> BOTTOMLESS_LAVA_BUCKET = ITEMS.register("bottomless_lava_bucket", () -> new BottomlessBucketItem(Fluids.LAVA, ModRarity.LIME));
    public static final Supplier<BottomlessBucketItem> BOTTOMLESS_HONEY_BUCKET = ITEMS.register("bottomless_honey_bucket", () -> new BottomlessBucketItem(ModFluids.HONEY.fluid().get(), ModRarity.LIME));
    public static final DeferredItem<BottomlessBucketItem> BOTTOMLESS_SHIMMER_BUCKET = ITEMS.register("bottomless_shimmer_bucket", () -> new BottomlessBucketItem(ModFluids.SHIMMER.fluid().get(), ModRarity.RED));

    public static final Supplier<Item> GOLDEN_KEY = ITEMS.register("golden_key", () -> new CustomRarityItem(ModRarity.WHITE));
    public static final Supplier<Item> SHADOW_KEY = ITEMS.register("shadow_key", () -> new CustomRarityItem(ModRarity.WHITE));

    public static final DeferredItem<WrenchItem> RED_WRENCH = ITEMS.register("red_wrench", () -> new WrenchItem(0xFF0000));
    public static final DeferredItem<WrenchItem> GREEN_WRENCH = ITEMS.register("green_wrench", () -> new WrenchItem(0x00FF00));
    public static final DeferredItem<WrenchItem> BLUE_WRENCH = ITEMS.register("blue_wrench", () -> new WrenchItem(0x0000FF));
    public static final DeferredItem<WrenchItem> YELLOW_WRENCH = ITEMS.register("yellow_wrench", () -> new WrenchItem(0xFFFF00));
    public static final DeferredItem<WireCutterItem> WIRE_CUTTER = ITEMS.register("wire_cutter", WireCutterItem::new);

    public static final Supplier<EncumberingStoneItem> ENCUMBERING_STONE = ITEMS.register("encumbering_stone", EncumberingStoneItem::new);
    public static final Supplier<MagicMirror> ICE_MIRROR = ITEMS.register("ice_mirror", () -> new MagicMirror(ModRarity.BLUE));
    public static final Supplier<MagicConch> MAGIC_CONCH = ITEMS.register("magic_conch", () -> new MagicConch(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final Supplier<DemonConch> DEMON_CONCH = ITEMS.register("demon_ocnch", DemonConch::new);

    public static final Supplier<Item> HOUSE_DETECTOR = ITEMS.register("house_detector", () -> new HouseDetector(new Item.Properties().stacksTo(1)));
    public static final Supplier<BugNetItem> BUG_NET = ITEMS.register("bug_net", () -> new BugNetItem(ModRarity.BLUE, 0.5, living -> living instanceof Animal));
    public static final Supplier<BugNetItem> GOLDEN_BUG_NET = ITEMS.register("golden_bug_net", () -> new BugNetItem(ModRarity.QUEST, 1.1, living -> living instanceof Animal));
    public static final Supplier<BugNetItem> DEV_BUG_NET = ITEMS.register("dev_bug_net", () -> new BugNetItem(ModRarity.MASTER, Double.MAX_VALUE, living -> !(living instanceof Player)));

    public static final Supplier<RopeCoilItem> ROPE_COIL = ITEMS.register("rope_coil", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.ROPE.get()));
    public static final Supplier<RopeCoilItem> VINE_ROPE_COIL = ITEMS.register("vine_rope_coil", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.VINE_ROPE.get()));
    public static final Supplier<RopeCoilItem> SILK_ROPE_COIL = ITEMS.register("silk_rope_coil", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.SILK_ROPE.get()));
    public static final Supplier<RopeCoilItem> WEB_ROPE_COIL = ITEMS.register("web_rope_coil", () -> new RopeCoilItem(new Item.Properties(), ModBlocks.WEB_ROPE.get()));

    public static final Supplier<TargetDummyItem> TARGET_DUMMY = ITEMS.register("target_dummy", TargetDummyItem::new);
}
