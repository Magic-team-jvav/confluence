package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.whip.*;
import org.confluence.mod.common.summoner.register.SummonerSummonMarks;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public final class WhipItems {
    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<LeatherWhipItem> LEATHER_WHIP = registerItem("leather_whip", "皮鞭", () -> new LeatherWhipItem(SummonerSummonMarks.LEATHER_WHIP));
    public static final PortDeferredItem<BaseWhipItem> SLUB_WHIP = registerItem("slub_whip", "竹节鞭", () -> new BaseWhipItem("slub_whip", 5F, 0.2F, 0.5F, 15, SummonerSummonMarks.SLUB_WHIP));
    public static final PortDeferredItem<BaseWhipItem> RUBY_WHIP = registerItem("ruby_whip", "红玉鞭", () -> new BaseWhipItem("ruby_whip", 9.7F, 0.5F, 0.8F, 15, SummonerSummonMarks.RUBY_WHIP));
    public static final PortDeferredItem<BaseWhipItem> AMBER_WHIP = registerItem("amber_whip", "琥珀鞭", () -> new BaseWhipItem("amber_whip", 9.7F, 0.5F, 0.8F, 15, SummonerSummonMarks.AMBER_WHIP));
    public static final PortDeferredItem<BaseWhipItem> TOPAZ_WHIP = registerItem("topaz_whip", "黄玉鞭", () -> new BaseWhipItem("topaz_whip", 9.5F, 0.5F, 0.8F, 15, SummonerSummonMarks.TOPAZ_WHIP));
    public static final PortDeferredItem<BaseWhipItem> JADE_WHIP = registerItem("jade_whip", "翡翠鞭", () -> new BaseWhipItem("jade_whip", 9.6F, 0.5F, 0.8F, 15, SummonerSummonMarks.JADE_WHIP));
    public static final PortDeferredItem<BaseWhipItem> DIAMOND_WHIP = registerItem("diamond_whip", "钻石鞭", () -> new BaseWhipItem("diamond_whip", 9.8F, 0.5F, 0.8F, 15, SummonerSummonMarks.DIAMOND_WHIP));
    public static final PortDeferredItem<BaseWhipItem> SAPPHIRE_WHIP = registerItem("sapphire_whip", "蓝玉鞭", () -> new BaseWhipItem("sapphire_whip", 9.6F, 0.5F, 0.8F, 15, SummonerSummonMarks.SAPPHIRE_WHIP));
    public static final PortDeferredItem<BaseWhipItem> AMETHYST_WHIP = registerItem("amethyst_whip", "紫晶鞭", () -> new BaseWhipItem("amethyst_whip", 9.5F, 0.5F, 0.8F, 15, SummonerSummonMarks.AMETHYST_WHIP));
    public static final PortDeferredItem<SwampWhipItem> SWAMP_WHIP = registerItem("swamp_whip", "沼泽藤蔓", () -> new SwampWhipItem(SummonerSummonMarks.SWAMP_WHIP));
    public static final PortDeferredItem<SnapthornItem> SNAPTHORN = registerItem("snapthorn", "荆鞭", () -> new SnapthornItem(SummonerSummonMarks.SNAPTHORN));
    public static final PortDeferredItem<BaseWhipItem> SPINAL_TAP = registerItem("spinal_tap", "脊柱骨鞭", () -> new BaseWhipItem("spinal_tap", 26F, 0.8F, 1.6F, 13, SummonerSummonMarks.SPINAL_TAP) {
        @Override
        public float damageFalloff() {return 0.9F;}

        @Override
        public float minimumDamageMultiplier() {return 0.0F;}
    });
    public static final PortDeferredItem<FirecrackerItem> FIRECRACKER = registerItem("firecracker", "鞭炮", () -> new FirecrackerItem(SummonerSummonMarks.FIRECRACKER));
    /// 风鞭：与目标高度差越大伤害越高。
    public static final PortDeferredItem<WindWhipItem> WIND_WHIP = registerItem("wind_whip", "风鞭", () -> new WindWhipItem(SummonerSummonMarks.WIND_WHIP));
    /// 星惧：命中后冒出可追逐的坠星，接住获得「摘星」。
    public static final PortDeferredItem<StarFearWhipItem> STAR_FEAR = registerItem("star_fear", "星惧", () -> new StarFearWhipItem(SummonerSummonMarks.STAR_FEAR));

    private WhipItems() {}

    public static void init() {}

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
