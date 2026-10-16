package org.confluence.mod.common.init.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.hook.*;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.hook.BaseHookItem;
import org.confluence.mod.common.item.hook.FishHookItem;
import org.confluence.mod.common.item.hook.LunarHookItem;
import org.confluence.mod.common.item.hook.WebSlingerItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class HookItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaseHookItem> GRAPPLING_HOOK = registerItem("grappling_hook", "抓钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 12.5F, 1.15F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.GRAPPLING)));
    public static final PortDeferredItem<BaseHookItem> AMETHYST_HOOK = registerItem("amethyst_hook", "紫晶钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 12.5F, 1.0F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.AMETHYST)));
    public static final PortDeferredItem<BaseHookItem> TOPAZ_HOOK = registerItem("topaz_hook", "黄玉钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 13.75F, 1.05F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.TOPAZ)));
    public static final PortDeferredItem<BaseHookItem> SAPPHIRE_HOOK = registerItem("sapphire_hook", "蓝玉钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 15.0F, 1.1F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.SAPPHIRE)));
    public static final PortDeferredItem<BaseHookItem> JADE_HOOK = registerItem("jade_hook", "翡翠钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 16.25F, 1.15F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.JADE)));
    public static final PortDeferredItem<BaseHookItem> RUBY_HOOK = registerItem("ruby_hook", "红玉钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 17.5F, 1.2F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.RUBY)));
    public static final PortDeferredItem<BaseHookItem> AMBER_HOOK = registerItem("amber_hook", "琥珀钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 18.33F, 1.25F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.AMBER)));
    public static final PortDeferredItem<BaseHookItem> DIAMOND_HOOK = registerItem("diamond_hook", "钻石钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 19.42F, 1.25F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.DIAMOND)));
    public static final PortDeferredItem<BaseHookItem> WEB_SLINGER = registerItem("web_slinger", "蛛丝吊索", WebSlingerItem::new);
    public static final PortDeferredItem<BaseHookItem> SKELETRON_HAND = registerItem("skeletron_hand", "骷髅王之手", () -> new BaseHookItem(ModRarity.GREEN, 2, 14.58F, 1.5F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.SKELETRON_HAND.get(), item, player, level)));
    public static final PortDeferredItem<BaseHookItem> SLIME_HOOK = registerItem("slime_hook", "史莱姆钩", () -> new BaseHookItem(ModRarity.ORANGE, 3, 12.5F, 1.3F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.SLIME_HOOK.get(), item, player, level)));
    public static final PortDeferredItem<BaseHookItem> FISH_HOOK = registerItem("fish_hook", "鱼钩", FishHookItem::new);
    public static final PortDeferredItem<BaseHookItem> IVY_WHIP = registerItem("ivy_whip", "常春藤鞭", () -> new BaseHookItem(ModRarity.ORANGE, 3, 18.67F, 1.3F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.IVY_WHIP.get(), item, player, level)));
    public static final PortDeferredItem<BaseHookItem> BAT_HOOK = registerItem("bat_hook", "蝙蝠钩", () -> new BaseHookItem(ModRarity.ORANGE, 1, 20.83F, 1.35F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.BAT_HOOK.get(), item, player, level)));
    public static final PortDeferredItem<BaseHookItem> CANDY_CANE_HOOK = registerItem("candy_cane_hook", "糖棒钩", () -> new BaseHookItem(ModRarity.LIME, 1, 16.67F, 1.15F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.CANDY_CANE_HOOK.get(), item, player, level)));
    public static final PortDeferredItem<BaseHookItem> DUAL_HOOK = registerItem("dual_hook", "双钩", () -> new BaseHookItem(ModRarity.LIGHT_RED, 2, 18.33F, 1.4F, BaseHookItem.HookType.INDIVIDUAL, (itemStack, item, player, level) -> {
        CompoundTag tag = LibUtils.getItemStackNbt(itemStack);
        boolean isRed = tag.getBoolean("isRed");
        LibUtils.updateItemStackNbt(itemStack, nbt -> nbt.putBoolean("isRed", !isRed));
        return new DualHookEntity(item, player, level, isRed ? DualHookEntity.Variant.RED : DualHookEntity.Variant.BLUE);
    }));
    public static final PortDeferredItem<BaseHookItem> HOOK_OF_DISSONANCE = registerItemWithEnglishName("hook_of_dissonance", "失谐钩爪", "Hook of Dissonance", () -> new BaseHookItem(ModRarity.PINK, 1, 20.0F, 1.6F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new HookOfDissonanceEntity(item, player, level)));
    public static final PortDeferredItem<BaseHookItem> THORN_HOOK = registerItem("thorn_hook", "刺钩", () -> new BaseHookItem(ModRarity.LIGHT_PURPLE, 3, 20.0F, 1.6F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.THORN_HOOK.get(), item, player, level)));
    public static final PortDeferredItem<BaseHookItem> ILLUMINANT_HOOK = registerItem("illuminant_hook", "荧光钩", () -> new BaseHookItem(ModRarity.LIGHT_PURPLE, 3, 20.0F, 1.5F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new MimicHookEntity(item, player, level, MimicHookEntity.Variant.ILLUMINANT)));
    public static final PortDeferredItem<BaseHookItem> WORM_HOOK = registerItem("worm_hook", "蠕虫钩", () -> new BaseHookItem(ModRarity.LIGHT_PURPLE, 3, 20.0F, 1.5F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new MimicHookEntity(item, player, level, MimicHookEntity.Variant.WORM)));
    public static final PortDeferredItem<BaseHookItem> TENDON_HOOK = registerItem("tendon_hook", "肌腱钩", () -> new BaseHookItem(ModRarity.LIGHT_PURPLE, 3, 20.0F, 1.5F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new MimicHookEntity(item, player, level, MimicHookEntity.Variant.TENDON)));
    public static final PortDeferredItem<BaseHookItem> SPOOKY_HOOK = registerItem("spooky_hook", "阴森钩", () -> new BaseHookItem(ModRarity.LIME, 3, 22.92F, 1.55F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.SPOOKY_HOOK.get(), item, player, level)));
    public static final PortDeferredItem<BaseHookItem> CHRISTMAS_HOOK = registerItem("christmas_hook", "圣诞钩", () -> new BaseHookItem(ModRarity.LIME, 3, 22.92F, 1.55F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.CHRISTMAS_HOOK.get(), item, player, level)));
    public static final PortDeferredItem<BaseHookItem> ANTI_GRAVITY_HOOK = registerItemWithEnglishName("anti_gravity_hook", "反重力钩", "Anti-Gravity Hook", () -> new BaseHookItem(ModRarity.LIME, 3, 20.83F, 1.4F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.ANTI_GRAVITY_HOOK.get(), item, player, level)));
    public static final PortDeferredItem<BaseHookItem> LUNAR_HOOK = registerItem("lunar_hook", "月钩", LunarHookItem::new);
    public static final PortDeferredItem<BaseHookItem> STATIC_HOOK = registerItem("static_hook", "静止钩", () -> new BaseHookItem(ModRarity.RED, 2, 25, 2, BaseHookItem.HookType.INDIVIDUAL, (itemStack, item, player, level) -> {
        throw new UnsupportedOperationException("Static hook is not implemented");
    }));

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
