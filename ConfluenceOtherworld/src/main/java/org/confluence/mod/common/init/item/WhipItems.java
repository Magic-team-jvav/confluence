package org.confluence.mod.common.init.item;

import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.whip.WhipTagEffect;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.whip.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Function;
import java.util.function.Supplier;

public final class WhipItems {
    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<LeatherWhipItem> LEATHER_WHIP = register("leather_whip", LeatherWhipItem.TAG_DAMAGE, LeatherWhipItem::new);
    public static final PortDeferredItem<BaseWhipItem> SLUB_WHIP = register("slub_whip", 1.0F, tag -> new BaseWhipItem("slub_whip", 5F, 0.2F, 0.5F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> RUBY_WHIP = register("ruby_whip", 1.0F, tag -> new BaseWhipItem("ruby_whip", 9.7F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> AMBER_WHIP = register("amber_whip", 1.0F, tag -> new BaseWhipItem("amber_whip", 9.7F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> TOPAZ_WHIP = register("topaz_whip", 1.0F, tag -> new BaseWhipItem("topaz_whip", 9.5F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> JADE_WHIP = register("jade_whip", 1.0F, tag -> new BaseWhipItem("jade_whip", 9.6F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> DIAMOND_WHIP = register("diamond_whip", 1.0F, tag -> new BaseWhipItem("diamond_whip", 9.8F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> SAPPHIRE_WHIP = register("sapphire_whip", 1.0F, tag -> new BaseWhipItem("sapphire_whip", 9.6F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<BaseWhipItem> AMETHYST_WHIP = register("amethyst_whip", 1.0F, tag -> new BaseWhipItem("amethyst_whip", 9.5F, 0.5F, 0.8F, 15, tag));
    public static final PortDeferredItem<SwampWhipItem> SWAMP_WHIP = register("swamp_whip", SwampWhipItem.TAG_DAMAGE, SwampWhipItem::new);
    public static final PortDeferredItem<SnapthornItem> SNAPTHORN = register("snapthorn", SnapthornItem.TAG_DAMAGE, SnapthornItem::new);
    public static final PortDeferredItem<BaseWhipItem> SPINAL_TAP = register("spinal_tap", 4.0F, tag -> new BaseWhipItem("spinal_tap", 26F, 0.8F, 1.6F, 13, tag) {
        @Override
        public float damageFalloff() {return 0.9F;}

        @Override
        public float minimumDamageMultiplier() {return 0.0F;}
    });
    public static final PortDeferredItem<FirecrackerItem> FIRECRACKER = register("firecracker", FirecrackerItem.TAG_DAMAGE, FirecrackerItem::new);

    private WhipItems() {}

    public static void init() {}

    private static <T extends BaseWhipItem> PortDeferredItem<T> register(String name, float tagDamage, Function<Supplier<? extends WhipTagEffect>, T> factory) {
        RegistryObject<WhipTagEffect> tagEffect = ModEffects.registerWhipTag(name, tagDamage);
        return ITEMS.register(name, () -> factory.apply(tagEffect));
    }
}
