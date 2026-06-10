package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.hook.*;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.hook.BaseHookItem;
import org.confluence.mod.common.item.hook.FishHookItem;
import org.confluence.mod.common.item.hook.LunarHookItem;
import org.confluence.mod.common.item.hook.WebSlingerItem;

public class HookItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<BaseHookItem> GRAPPLING_HOOK = ITEMS.register("grappling_hook", () -> new BaseHookItem(ModRarity.BLUE, 1, 12.5F, 1.15F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.GRAPPLING)));
    public static final RegistryObject<BaseHookItem> AMETHYST_HOOK = ITEMS.register("amethyst_hook", () -> new BaseHookItem(ModRarity.BLUE, 1, 12.5F, 1.0F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.AMETHYST)));
    public static final RegistryObject<BaseHookItem> TOPAZ_HOOK = ITEMS.register("topaz_hook", () -> new BaseHookItem(ModRarity.BLUE, 1, 13.75F, 1.05F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.TOPAZ)));
    public static final RegistryObject<BaseHookItem> SAPPHIRE_HOOK = ITEMS.register("sapphire_hook", () -> new BaseHookItem(ModRarity.BLUE, 1, 15.0F, 1.1F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.SAPPHIRE)));
    public static final RegistryObject<BaseHookItem> JADE_HOOK = ITEMS.register("jade_hook", () -> new BaseHookItem(ModRarity.BLUE, 1, 16.25F, 1.15F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.JADE)));
    public static final RegistryObject<BaseHookItem> RUBY_HOOK = ITEMS.register("ruby_hook", () -> new BaseHookItem(ModRarity.BLUE, 1, 17.5F, 1.2F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.RUBY)));
    public static final RegistryObject<BaseHookItem> AMBER_HOOK = ITEMS.register("amber_hook", () -> new BaseHookItem(ModRarity.BLUE, 1, 18.33F, 1.25F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.AMBER)));
    public static final RegistryObject<BaseHookItem> DIAMOND_HOOK = ITEMS.register("diamond_hook", () -> new BaseHookItem(ModRarity.BLUE, 1, 19.42F, 1.25F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new BaseHookEntity(item, player, level, BaseHookEntity.Variant.DIAMOND)));
    public static final RegistryObject<BaseHookItem> WEB_SLINGER = ITEMS.register("web_slinger", WebSlingerItem::new);
    public static final RegistryObject<BaseHookItem> SKELETRON_HAND = ITEMS.register("skeletron_hand", () -> new BaseHookItem(ModRarity.GREEN, 2, 14.58F, 1.5F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.SKELETRON_HAND.get(), item, player, level)));
    public static final RegistryObject<BaseHookItem> SLIME_HOOK = ITEMS.register("slime_hook", () -> new BaseHookItem(ModRarity.ORANGE, 3, 12.5F, 1.3F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.SLIME_HOOK.get(), item, player, level)));
    public static final RegistryObject<BaseHookItem> FISH_HOOK = ITEMS.register("fish_hook", FishHookItem::new);
    public static final RegistryObject<BaseHookItem> IVY_WHIP = ITEMS.register("ivy_whip", () -> new BaseHookItem(ModRarity.ORANGE, 3, 18.67F, 1.3F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.IVY_WHIP.get(), item, player, level)));
    public static final RegistryObject<BaseHookItem> BAT_HOOK = ITEMS.register("bat_hook", () -> new BaseHookItem(ModRarity.ORANGE, 1, 20.83F, 1.35F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.BAT_HOOK.get(), item, player, level)));
    public static final RegistryObject<BaseHookItem> CANDY_CANE_HOOK = ITEMS.register("candy_cane_hook", () -> new BaseHookItem(ModRarity.LIME, 1, 16.67F, 1.15F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.CANDY_CANE_HOOK.get(), item, player, level)));
    public static final RegistryObject<BaseHookItem> DUAL_HOOK = ITEMS.register("dual_hook", () -> new BaseHookItem(ModRarity.LIGHT_RED, 2, 18.33F, 1.4F, BaseHookItem.HookType.INDIVIDUAL, (itemStack, item, player, level) -> {
        CompoundTag tag = LibUtils.getItemStackNbt(itemStack);
        boolean isRed = tag.getBoolean("isRed");
        LibUtils.updateItemStackNbt(itemStack, nbt -> nbt.putBoolean("isRed", !isRed));
        return new DualHookEntity(item, player, level, isRed ? DualHookEntity.Variant.RED : DualHookEntity.Variant.BLUE);
    }));
    public static final RegistryObject<BaseHookItem> HOOK_OF_DISSONANCE = ITEMS.register("hook_of_dissonance", () -> new BaseHookItem(ModRarity.PINK, 1, 20.0F, 1.6F, BaseHookItem.HookType.SINGLE, (itemStack, item, player, level) -> new HookOfDissonanceEntity(item, player, level)));
    public static final RegistryObject<BaseHookItem> THORN_HOOK = ITEMS.register("thorn_hook", () -> new BaseHookItem(ModRarity.LIGHT_PURPLE, 3, 20.0F, 1.6F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.THORN_HOOK.get(), item, player, level)));
    public static final RegistryObject<BaseHookItem> ILLUMINANT_HOOK = ITEMS.register("illuminant_hook", () -> new BaseHookItem(ModRarity.LIGHT_PURPLE, 3, 20.0F, 1.5F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new MimicHookEntity(item, player, level, MimicHookEntity.Variant.ILLUMINANT)));
    public static final RegistryObject<BaseHookItem> WORM_HOOK = ITEMS.register("worm_hook", () -> new BaseHookItem(ModRarity.LIGHT_PURPLE, 3, 20.0F, 1.5F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new MimicHookEntity(item, player, level, MimicHookEntity.Variant.WORM)));
    public static final RegistryObject<BaseHookItem> TENDON_HOOK = ITEMS.register("tendon_hook", () -> new BaseHookItem(ModRarity.LIGHT_PURPLE, 3, 20.0F, 1.5F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new MimicHookEntity(item, player, level, MimicHookEntity.Variant.TENDON)));
    public static final RegistryObject<BaseHookItem> SPOOKY_HOOK = ITEMS.register("spooky_hook", () -> new BaseHookItem(ModRarity.LIME, 3, 22.92F, 1.55F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.SPOOKY_HOOK.get(), item, player, level)));
    public static final RegistryObject<BaseHookItem> CHRISTMAS_HOOK = ITEMS.register("christmas_hook", () -> new BaseHookItem(ModRarity.LIME, 3, 22.92F, 1.55F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.CHRISTMAS_HOOK.get(), item, player, level)));
    public static final RegistryObject<BaseHookItem> ANTI_GRAVITY_HOOK = ITEMS.register("anti_gravity_hook", () -> new BaseHookItem(ModRarity.LIME, 3, 20.83F, 1.4F, BaseHookItem.HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.ANTI_GRAVITY_HOOK.get(), item, player, level)));
    public static final RegistryObject<BaseHookItem> LUNAR_HOOK = ITEMS.register("lunar_hook", LunarHookItem::new);
    public static final RegistryObject<BaseHookItem> STATIC_HOOK = ITEMS.register("static_hook", () -> new BaseHookItem(ModRarity.RED, 2, 25, 2, BaseHookItem.HookType.INDIVIDUAL, (itemStack, item, player, level) -> {
        throw new UnsupportedOperationException("Static Hook Can Not Use Right Now"); // todo
    }));
}
