package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.register.SummonerSummonMarks;
import org.confluence.mod.common.item.whip.*;

public final class WhipItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<LeatherWhipItem> LEATHER_WHIP = ITEMS.register("leather_whip", () -> new LeatherWhipItem(SummonerSummonMarks.LEATHER_WHIP));
    public static final DeferredItem<BaseWhipItem> SLUB_WHIP = ITEMS.register("slub_whip", () -> new BaseWhipItem("slub_whip", 5F, 0.2F, 0.5F, 15, SummonerSummonMarks.SLUB_WHIP));
    public static final DeferredItem<BaseWhipItem> RUBY_WHIP = ITEMS.register("ruby_whip", () -> new BaseWhipItem("ruby_whip", 9.7F, 0.5F, 0.8F, 15, SummonerSummonMarks.RUBY_WHIP));
    public static final DeferredItem<BaseWhipItem> AMBER_WHIP = ITEMS.register("amber_whip", () -> new BaseWhipItem("amber_whip", 9.7F, 0.5F, 0.8F, 15, SummonerSummonMarks.AMBER_WHIP));
    public static final DeferredItem<BaseWhipItem> TOPAZ_WHIP = ITEMS.register("topaz_whip", () -> new BaseWhipItem("topaz_whip", 9.5F, 0.5F, 0.8F, 15, SummonerSummonMarks.TOPAZ_WHIP));
    public static final DeferredItem<BaseWhipItem> JADE_WHIP = ITEMS.register("jade_whip", () -> new BaseWhipItem("jade_whip", 9.6F, 0.5F, 0.8F, 15, SummonerSummonMarks.JADE_WHIP));
    public static final DeferredItem<BaseWhipItem> DIAMOND_WHIP = ITEMS.register("diamond_whip", () -> new BaseWhipItem("diamond_whip", 9.8F, 0.5F, 0.8F, 15, SummonerSummonMarks.DIAMOND_WHIP));
    public static final DeferredItem<BaseWhipItem> SAPPHIRE_WHIP = ITEMS.register("sapphire_whip", () -> new BaseWhipItem("sapphire_whip", 9.6F, 0.5F, 0.8F, 15, SummonerSummonMarks.SAPPHIRE_WHIP));
    public static final DeferredItem<BaseWhipItem> AMETHYST_WHIP = ITEMS.register("amethyst_whip", () -> new BaseWhipItem("amethyst_whip", 9.5F, 0.5F, 0.8F, 15, SummonerSummonMarks.AMETHYST_WHIP));
    public static final DeferredItem<SwampWhipItem> SWAMP_WHIP = ITEMS.register("swamp_whip", () -> new SwampWhipItem(SummonerSummonMarks.SWAMP_WHIP));
    public static final DeferredItem<SnapthornItem> SNAPTHORN = ITEMS.register("snapthorn", () -> new SnapthornItem(SummonerSummonMarks.SNAPTHORN));
    public static final DeferredItem<BaseWhipItem> SPINAL_TAP = ITEMS.register("spinal_tap", () -> new BaseWhipItem("spinal_tap", 26F, 0.8F, 1.6F, 13, SummonerSummonMarks.SPINAL_TAP) {
        @Override
        public float damageFalloff() {return 0.9F;}

        @Override
        public float minimumDamageMultiplier() {return 0.0F;}
    });
    public static final DeferredItem<FirecrackerItem> FIRECRACKER = ITEMS.register("firecracker", () -> new FirecrackerItem(SummonerSummonMarks.FIRECRACKER));

    private WhipItems() {}

    public static void init() {}
}
