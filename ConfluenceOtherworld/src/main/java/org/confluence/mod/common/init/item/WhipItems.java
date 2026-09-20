package org.confluence.mod.common.init.item;

import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.whip.*;
import org.confluence.mod.common.summoner.register.SummonerSummonMarks;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Function;
import java.util.function.Supplier;

public final class WhipItems {
    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<LeatherWhipItem> LEATHER_WHIP = register("leather_whip", SummonerSummonMarks.LEATHER_WHIP, LeatherWhipItem::new);
    public static final PortDeferredItem<BaseWhipItem> SLUB_WHIP = register("slub_whip", SummonerSummonMarks.SLUB_WHIP, tag -> new BaseWhipItem("slub_whip", 5F, 0.2F, 0.5F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> RUBY_WHIP = register("ruby_whip", SummonerSummonMarks.RUBY_WHIP, tag -> new BaseWhipItem("ruby_whip", 9.7F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> AMBER_WHIP = register("amber_whip", SummonerSummonMarks.AMBER_WHIP, tag -> new BaseWhipItem("amber_whip", 9.7F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> TOPAZ_WHIP = register("topaz_whip", SummonerSummonMarks.TOPAZ_WHIP, tag -> new BaseWhipItem("topaz_whip", 9.5F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> JADE_WHIP = register("jade_whip", SummonerSummonMarks.JADE_WHIP, tag -> new BaseWhipItem("jade_whip", 9.6F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> DIAMOND_WHIP = register("diamond_whip", SummonerSummonMarks.DIAMOND_WHIP, tag -> new BaseWhipItem("diamond_whip", 9.8F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> SAPPHIRE_WHIP = register("sapphire_whip", SummonerSummonMarks.SAPPHIRE_WHIP, tag -> new BaseWhipItem("sapphire_whip", 9.6F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> AMETHYST_WHIP = register("amethyst_whip", SummonerSummonMarks.AMETHYST_WHIP, tag -> new BaseWhipItem("amethyst_whip", 9.5F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<SwampWhipItem> SWAMP_WHIP = register("swamp_whip", SummonerSummonMarks.SWAMP_WHIP, SwampWhipItem::new);
    public static final PortDeferredItem<SnapthornItem> SNAPTHORN = register("snapthorn", SummonerSummonMarks.SNAPTHORN, SnapthornItem::new);
    public static final PortDeferredItem<BaseWhipItem> SPINAL_TAP = register("spinal_tap", SummonerSummonMarks.SPINAL_TAP, tag -> new BaseWhipItem("spinal_tap", 26F, 0.8F, 1.6F, 13, tag) {
        @Override
        public float damageFalloff() {return 0.9F;}

        @Override
        public float minimumDamageMultiplier() {return 0.0F;}
    });
    public static final PortDeferredItem<FirecrackerItem> FIRECRACKER = register("firecracker", SummonerSummonMarks.FIRECRACKER, FirecrackerItem::new);

    private WhipItems() {}

    public static void init() {}

    private static <T extends BaseWhipItem> PortDeferredItem<T> register(String name, RegistryObject<SummonMarkType> summonMarkType, Function<Supplier<? extends SummonMarkType>, T> factory) {
        return ITEMS.register(name, () -> factory.apply(summonMarkType));
    }
}
