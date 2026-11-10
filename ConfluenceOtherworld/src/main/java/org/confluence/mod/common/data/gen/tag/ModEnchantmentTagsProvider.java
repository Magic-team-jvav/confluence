package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.lib.common.LibTags;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEnchantments;
import org.confluence.mod.common.init.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModEnchantmentTagsProvider extends EnchantmentTagsProvider {
    public ModEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Confluence.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(EnchantmentTags.TOOLTIP_ORDER).add(
                ModEnchantments.MANA_REGENERATION,
                ModEnchantments.EFFICIENT_MAGIC,
                ModEnchantments.MANA_MENDING,
                ModEnchantments.CELESTIAL_ABSORPTION,
                ModEnchantments.SOOTHED_MANA,
                ModEnchantments.ARCANE_PROTECTION,
                ModEnchantments.SPELL_DESPERATION,
                ModEnchantments.MYSTIC_SURGE,
                ModEnchantments.WHIP_SWEEP,
                ModEnchantments.MULTI_BOOMERANG,
                ModEnchantments.SUMMONER_PACT,
                ModEnchantments.FLAIL_WIND_BURST,
                ModEnchantments.FLAIL_TURBINE,
                ModEnchantments.BEAST_LORD,
                ModEnchantments.ARTIFACT_COOLDOWN,
                ModEnchantments.FOOD_RESERVE,
                ModEnchantments.RECKLESS,
                ModEnchantments.SOUL_CONDUIT,
                ModEnchantments.SOUL_BIND,
                ModEnchantments.BEAST_INSPIRE,
                ModEnchantments.DEFLECT,
                ModEnchantments.POTION_SHIELD,
                ModEnchantments.SELFLESS_SACRIFICE,
                ModEnchantments.SURPRISE_GIFT,
                ModEnchantments.DEATH_BARGAIN,
                ModEnchantments.SOUL_HASTE,
                ModEnchantments.SPEED_SYNERGY,
                ModEnchantments.ARTIFACT_SYNERGY,
                ModEnchantments.CRITICAL_STRIKE,
                ModEnchantments.VIGOROUS,
                ModEnchantments.SOUL_RESONANCE,
                ModEnchantments.VOID_STRIKE,
                ModEnchantments.FLANKING_STRIKE,
                ModEnchantments.STUN,
                ModEnchantments.SOUL_SIPHON,
                ModEnchantments.BURST_BOWSTRING,
                ModEnchantments.COOLDOWN_SHOT,
                ModEnchantments.MULTI_CHARGE,
                ModEnchantments.RAPID_CHARGE,
                ModEnchantments.SUPER_CHARGE
        );

        tag(ModTags.Enchantments.MANA_IO_EXCLUSIVE).add(ModEnchantments.MANA_REGENERATION, ModEnchantments.EFFICIENT_MAGIC);
        tag(ModTags.Enchantments.MENDING_EXCLUSIVE).add(ModEnchantments.MANA_MENDING, Enchantments.MENDING);
        tag(ModTags.Enchantments.MANA_AFFECTIVE_EXCLUSIVE).add(ModEnchantments.CELESTIAL_ABSORPTION);
        tag(ModTags.Enchantments.MAGIC_ATTACK_EXCLUSIVE).add(ModEnchantments.SPELL_DESPERATION, ModEnchantments.MYSTIC_SURGE);
        tag(ModTags.Enchantments.FLAIL_EXCLUSIVE).add(ModEnchantments.FLAIL_WIND_BURST, ModEnchantments.FLAIL_TURBINE);
        tag(ModTags.Enchantments.BEAST_EXCLUSIVE).add(ModEnchantments.BEAST_LORD, ModEnchantments.BEAST_INSPIRE);
        tag(ModTags.Enchantments.FOOD_RESERVE_EXCLUSIVE).add(ModEnchantments.FOOD_RESERVE, ModEnchantments.SURPRISE_GIFT);
        tag(ModTags.Enchantments.CHARGE_EXCLUSIVE).add(ModEnchantments.MULTI_CHARGE, ModEnchantments.SUPER_CHARGE);
        tag(EnchantmentTags.TREASURE).add(ModEnchantments.MANA_MENDING, ModEnchantments.CELESTIAL_ABSORPTION);
        tag(EnchantmentTags.ON_RANDOM_LOOT).add(ModEnchantments.MANA_MENDING);
        tag(EnchantmentTags.TRADEABLE).add(ModEnchantments.MANA_MENDING);
        tag(EnchantmentTags.NON_TREASURE).add(
                ModEnchantments.MANA_REGENERATION,
                ModEnchantments.EFFICIENT_MAGIC,
                ModEnchantments.ARCANE_PROTECTION,
                ModEnchantments.SPELL_DESPERATION,
                ModEnchantments.MYSTIC_SURGE,
                ModEnchantments.WHIP_SWEEP,
                ModEnchantments.MULTI_BOOMERANG,
                ModEnchantments.SUMMONER_PACT,
                ModEnchantments.FLAIL_WIND_BURST,
                ModEnchantments.FLAIL_TURBINE
        );
        tag(EnchantmentTags.ARMOR_EXCLUSIVE).add(ModEnchantments.ARCANE_PROTECTION);

        /// 新一轮附魔还没定稿：附魔书共用 `minecraft:enchanted_book` 一个物品，物品标签区分不了具体是哪一本，
        /// 所以用附魔标签 `confluence:wip` 标记，`WipNotDisplayOutput` 读到该标签就跳过（开发环境仍然显示）。
        tag(LibTags.Enchantments.WIP).add(
                ModEnchantments.FOOD_RESERVE,
                ModEnchantments.RECKLESS,
                ModEnchantments.SOUL_CONDUIT,
                ModEnchantments.SOUL_BIND,
                ModEnchantments.BEAST_INSPIRE,
                ModEnchantments.DEFLECT,
                ModEnchantments.POTION_SHIELD,
                ModEnchantments.SELFLESS_SACRIFICE,
                ModEnchantments.SURPRISE_GIFT,
                ModEnchantments.DEATH_BARGAIN,
                ModEnchantments.SOUL_HASTE,
                ModEnchantments.SPEED_SYNERGY,
                ModEnchantments.ARTIFACT_SYNERGY,
                ModEnchantments.CRITICAL_STRIKE,
                ModEnchantments.VIGOROUS,
                ModEnchantments.SOUL_RESONANCE,
                ModEnchantments.VOID_STRIKE,
                ModEnchantments.FLANKING_STRIKE,
                ModEnchantments.STUN,
                ModEnchantments.SOUL_SIPHON,
                ModEnchantments.BURST_BOWSTRING,
                ModEnchantments.COOLDOWN_SHOT,
                ModEnchantments.MULTI_CHARGE,
                ModEnchantments.RAPID_CHARGE,
                ModEnchantments.SUPER_CHARGE
        );
    }
}
