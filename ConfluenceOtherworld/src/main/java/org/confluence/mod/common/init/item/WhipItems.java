package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.whip.*;
import org.confluence.mod.common.summoner.register.SummonerSummonMarks;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public final class WhipItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<LeatherWhipItem> LEATHER_WHIP = registerItem("leather_whip", "皮鞭", () -> new LeatherWhipItem(SummonerSummonMarks.LEATHER_WHIP));
    public static final DeferredItem<BaseWhipItem> SLUB_WHIP = registerItem("slub_whip", "竹节鞭", () -> new BaseWhipItem("slub_whip", 5F, 0.2F, 0.5F, 15, SummonerSummonMarks.SLUB_WHIP));
    public static final DeferredItem<BaseWhipItem> RUBY_WHIP = registerItem("ruby_whip", "红玉鞭", () -> new BaseWhipItem("ruby_whip", 9.7F, 0.5F, 0.8F, 15, SummonerSummonMarks.RUBY_WHIP));
    public static final DeferredItem<BaseWhipItem> AMBER_WHIP = registerItem("amber_whip", "琥珀鞭", () -> new BaseWhipItem("amber_whip", 9.7F, 0.5F, 0.8F, 15, SummonerSummonMarks.AMBER_WHIP));
    public static final DeferredItem<BaseWhipItem> TOPAZ_WHIP = registerItem("topaz_whip", "黄玉鞭", () -> new BaseWhipItem("topaz_whip", 9.5F, 0.5F, 0.8F, 15, SummonerSummonMarks.TOPAZ_WHIP));
    public static final DeferredItem<BaseWhipItem> JADE_WHIP = registerItem("jade_whip", "翡翠鞭", () -> new BaseWhipItem("jade_whip", 9.6F, 0.5F, 0.8F, 15, SummonerSummonMarks.JADE_WHIP));
    public static final DeferredItem<BaseWhipItem> DIAMOND_WHIP = registerItem("diamond_whip", "钻石鞭", () -> new BaseWhipItem("diamond_whip", 9.8F, 0.5F, 0.8F, 15, SummonerSummonMarks.DIAMOND_WHIP));
    public static final DeferredItem<BaseWhipItem> SAPPHIRE_WHIP = registerItem("sapphire_whip", "蓝玉鞭", () -> new BaseWhipItem("sapphire_whip", 9.6F, 0.5F, 0.8F, 15, SummonerSummonMarks.SAPPHIRE_WHIP));
    public static final DeferredItem<BaseWhipItem> AMETHYST_WHIP = registerItem("amethyst_whip", "紫晶鞭", () -> new BaseWhipItem("amethyst_whip", 9.5F, 0.5F, 0.8F, 15, SummonerSummonMarks.AMETHYST_WHIP));
    public static final DeferredItem<SwampWhipItem> SWAMP_WHIP = registerItem("swamp_whip", "沼泽藤蔓", () -> new SwampWhipItem(SummonerSummonMarks.SWAMP_WHIP));
    public static final DeferredItem<SnapthornItem> SNAPTHORN = registerItem("snapthorn", "荆鞭", () -> new SnapthornItem(SummonerSummonMarks.SNAPTHORN));
    public static final DeferredItem<BaseWhipItem> SPINAL_TAP = registerItem("spinal_tap", "脊柱骨鞭", () -> new BaseWhipItem("spinal_tap", 26F, 0.8F, 1.6F, 13, SummonerSummonMarks.SPINAL_TAP) {
        @Override
        public float damageFalloff() {return 0.9F;}

        @Override
        public float minimumDamageMultiplier() {return 0.0F;}
    });
    public static final DeferredItem<FirecrackerItem> FIRECRACKER = registerItem("firecracker", "鞭炮", () -> new FirecrackerItem(SummonerSummonMarks.FIRECRACKER));

    private WhipItems() {}

    public static void init() {}

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
