package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
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
                ModEnchantments.MYSTIC_SURGE
        );

        tag(ModTags.Enchantments.MANA_IO_EXCLUSIVE).add(ModEnchantments.MANA_REGENERATION, ModEnchantments.EFFICIENT_MAGIC);
        tag(ModTags.Enchantments.MENDING_EXCLUSIVE).add(ModEnchantments.MANA_MENDING, Enchantments.MENDING);
        tag(ModTags.Enchantments.MANA_AFFECTIVE_EXCLUSIVE).add(ModEnchantments.CELESTIAL_ABSORPTION);
        tag(ModTags.Enchantments.MAGIC_ATTACK_EXCLUSIVE).add(ModEnchantments.SPELL_DESPERATION, ModEnchantments.MYSTIC_SURGE);
        tag(EnchantmentTags.TREASURE).add(ModEnchantments.MANA_MENDING, ModEnchantments.CELESTIAL_ABSORPTION);
        tag(EnchantmentTags.ON_RANDOM_LOOT).add(ModEnchantments.MANA_MENDING);
        tag(EnchantmentTags.TRADEABLE).add(ModEnchantments.MANA_MENDING);
        tag(EnchantmentTags.NON_TREASURE).add(
                ModEnchantments.MANA_REGENERATION,
                ModEnchantments.EFFICIENT_MAGIC,
                ModEnchantments.ARCANE_PROTECTION,
                ModEnchantments.SPELL_DESPERATION,
                ModEnchantments.MYSTIC_SURGE
        );
        tag(EnchantmentTags.ARMOR_EXCLUSIVE).add(ModEnchantments.ARCANE_PROTECTION);
    }
}
