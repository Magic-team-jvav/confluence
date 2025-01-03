package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModFluids;
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
    public static final Supplier<BottomlessBucketItem> BOTTOMLESS_SHIMMER_BUCKET = ITEMS.register("bottomless_shimmer_bucket", () -> new BottomlessBucketItem(ModFluids.SHIMMER.fluid().get(), ModRarity.RED));

    public static final Supplier<Item> GOLDEN_KEY = ITEMS.register("golden_key", () -> new CustomRarityItem(ModRarity.WHITE));
    public static final Supplier<Item> SHADOW_KEY = ITEMS.register("shadow_key", () -> new CustomRarityItem(ModRarity.WHITE));

    public static final Supplier<WrenchItem> RED_WRENCH = ITEMS.register("red_wrench", () -> new WrenchItem(0xFF0000));
    public static final Supplier<WrenchItem> GREEN_WRENCH = ITEMS.register("green_wrench", () -> new WrenchItem(0x00FF00));
    public static final Supplier<WrenchItem> BLUE_WRENCH = ITEMS.register("blue_wrench", () -> new WrenchItem(0x0000FF));
    public static final Supplier<WrenchItem> YELLOW_WRENCH = ITEMS.register("yellow_wrench", () -> new WrenchItem(0xFFFF00));
    public static final Supplier<WireCutterItem> WIRE_CUTTER = ITEMS.register("wire_cutter", WireCutterItem::new);

    public static final Supplier<EncumberingStoneItem> ENCUMBERING_STONE = ITEMS.register("encumbering_stone", EncumberingStoneItem::new);
    public static final Supplier<MagicMirror> ICE_MIRROR = ITEMS.register("ice_mirror", () -> new MagicMirror(ModRarity.BLUE));
    public static final Supplier<MagicConch> MAGIC_CONCH = ITEMS.register("magic_conch", () -> new MagicConch(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final Supplier<DemonConch> DEMON_CONCH = ITEMS.register("demon_ocnch", DemonConch::new);

    public static final Supplier<FertilizerItem> FERTILIZER = ITEMS.register("fertilizer", FertilizerItem::new);
}
