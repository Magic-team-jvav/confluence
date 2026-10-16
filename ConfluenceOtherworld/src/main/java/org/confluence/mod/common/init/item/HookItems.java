package org.confluence.mod.common.init.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.hook.*;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.hook.BaseHookItem;
import org.confluence.mod.common.item.hook.FishHookItem;
import org.confluence.mod.common.item.hook.LunarHookItem;
import org.confluence.mod.common.item.hook.WebSlingerItem;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class HookItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseHookItem> GRAPPLING_HOOK = registerItem("grappling_hook", "抓钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 12.5F, 1.15F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.GRAPPLING)));
    public static final DeferredItem<BaseHookItem> AMETHYST_HOOK = registerItem("amethyst_hook", "紫晶钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 12.5F, 1.0F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.AMETHYST)));
    public static final DeferredItem<BaseHookItem> TOPAZ_HOOK = registerItem("topaz_hook", "黄玉钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 13.75F, 1.05F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.TOPAZ)));
    public static final DeferredItem<BaseHookItem> SAPPHIRE_HOOK = registerItem("sapphire_hook", "蓝玉钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 15.0F, 1.1F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.SAPPHIRE)));
    public static final DeferredItem<BaseHookItem> JADE_HOOK = registerItem("jade_hook", "翡翠钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 16.25F, 1.15F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.JADE)));
    public static final DeferredItem<BaseHookItem> RUBY_HOOK = registerItem("ruby_hook", "红玉钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 17.5F, 1.2F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.RUBY)));
    public static final DeferredItem<BaseHookItem> AMBER_HOOK = registerItem("amber_hook", "琥珀钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 18.33F, 1.25F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.AMBER)));
    public static final DeferredItem<BaseHookItem> DIAMOND_HOOK = registerItem("diamond_hook", "钻石钩", () -> new BaseHookItem(ModRarity.BLUE, 1, 19.42F, 1.25F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.DIAMOND)));
    public static final DeferredItem<BaseHookItem> WEB_SLINGER = registerItem("web_slinger", "蛛丝吊索", WebSlingerItem::new);
    public static final DeferredItem<BaseHookItem> SKELETRON_HAND = registerItem("skeletron_hand", "骷髅王之手", () -> new BaseHookItem(ModRarity.GREEN, 2, 14.58F, 1.5F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.SKELETRON_HAND.get(), item, player, level)));
    public static final DeferredItem<BaseHookItem> SLIME_HOOK = registerItem("slime_hook", "史莱姆钩", () -> new BaseHookItem(ModRarity.ORANGE, 3, 12.5F, 1.3F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.SLIME_HOOK.get(), item, player, level)));
    public static final DeferredItem<BaseHookItem> FISH_HOOK = registerItem("fish_hook", "鱼钩", FishHookItem::new);
    public static final DeferredItem<BaseHookItem> IVY_WHIP = registerItem("ivy_whip", "常春藤鞭", () -> new BaseHookItem(ModRarity.ORANGE, 3, 18.67F, 1.3F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.IVY_WHIP.get(), item, player, level)));
    public static final DeferredItem<BaseHookItem> BAT_HOOK = registerItem("bat_hook", "蝙蝠钩", () -> new BaseHookItem(ModRarity.ORANGE, 1, 20.83F, 1.35F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.BAT_HOOK.get(), item, player, level)));
    public static final DeferredItem<BaseHookItem> CANDY_CANE_HOOK = registerItem("candy_cane_hook", "糖棒钩", () -> new BaseHookItem(ModRarity.LIME, 1, 16.67F, 1.15F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.CANDY_CANE_HOOK.get(), item, player, level)));
    public static final DeferredItem<BaseHookItem> DUAL_HOOK = registerItem("dual_hook", "双钩", () -> new BaseHookItem(ModRarity.LIGHT_RED, 2, 18.33F, 1.4F, BaseHookItem.HookType.INDIVIDUAL, (itemStack, item, player, level) -> {
        CompoundTag tag = LibUtils.getItemStackNbt(itemStack);
        boolean isRed = tag.getBoolean("isRed");
        LibUtils.updateItemStackNbt(itemStack, nbt -> nbt.putBoolean("isRed", !isRed));
        return new DualHookEntity(item, player, level, isRed ? DualHookEntity.Variant.RED : DualHookEntity.Variant.BLUE);
    }));
    public static final DeferredItem<BaseHookItem> HOOK_OF_DISSONANCE = registerItemWithEnglishName("hook_of_dissonance", "失谐钩爪", "Hook of Dissonance", () -> new BaseHookItem(ModRarity.PINK, 1, 20.0F, 1.6F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new HookOfDissonanceEntity(item, player, level)));
    public static final DeferredItem<BaseHookItem> THORN_HOOK = registerItem("thorn_hook", "刺钩", () -> new BaseHookItem(ModRarity.LIGHT_PURPLE, 3, 20.0F, 1.6F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.THORN_HOOK.get(), item, player, level)));
    public static final DeferredItem<BaseHookItem> ILLUMINANT_HOOK = registerItem("illuminant_hook", "荧光钩", () -> new BaseHookItem(ModRarity.LIGHT_PURPLE, 3, 20.0F, 1.5F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new MimicHookEntity(item, player, level, MimicHookEntity.Variant.ILLUMINANT)));
    public static final DeferredItem<BaseHookItem> WORM_HOOK = registerItem("worm_hook", "蠕虫钩", () -> new BaseHookItem(ModRarity.LIGHT_PURPLE, 3, 20.0F, 1.5F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new MimicHookEntity(item, player, level, MimicHookEntity.Variant.WORM)));
    public static final DeferredItem<BaseHookItem> TENDON_HOOK = registerItem("tendon_hook", "肌腱钩", () -> new BaseHookItem(ModRarity.LIGHT_PURPLE, 3, 20.0F, 1.5F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new MimicHookEntity(item, player, level, MimicHookEntity.Variant.TENDON)));
    public static final DeferredItem<BaseHookItem> SPOOKY_HOOK = registerItem("spooky_hook", "阴森钩", () -> new BaseHookItem(ModRarity.LIME, 3, 22.92F, 1.55F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.SPOOKY_HOOK.get(), item, player, level)));
    public static final DeferredItem<BaseHookItem> CHRISTMAS_HOOK = registerItem("christmas_hook", "圣诞钩", () -> new BaseHookItem(ModRarity.LIME, 3, 22.92F, 1.55F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.CHRISTMAS_HOOK.get(), item, player, level)));
    public static final DeferredItem<BaseHookItem> ANTI_GRAVITY_HOOK = registerItemWithEnglishName("anti_gravity_hook", "反重力钩", "Anti-Gravity Hook", () -> new BaseHookItem(ModRarity.LIME, 3, 20.83F, 1.4F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.ANTI_GRAVITY_HOOK.get(), item, player, level)));
    public static final DeferredItem<BaseHookItem> LUNAR_HOOK = registerItem("lunar_hook", "月钩", LunarHookItem::new);
    public static final DeferredItem<BaseHookItem> STATIC_HOOK = registerItem("static_hook", "静止钩", () -> new BaseHookItem(ModRarity.RED, 2, 25, 2, BaseHookItem.HookType.INDIVIDUAL, (itemStack, item, player, level) -> {
        throw new UnsupportedOperationException("Static Hook Can Not Use Right Now"); // todo
    }));

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
