package org.confluence.terraentity.data.gen.tags;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.data.enchantment.TEEnchantments;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TEEnchantmentTagsProvider extends EnchantmentTagsProvider {
    public TEEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TerraEntity.MODID, existingFileHelper);
    }
    @Override
    protected void addTags(HolderLookup.Provider provider) {

        this.tag(EnchantmentTags.NON_TREASURE).add(
                TEEnchantments.MULTI_BOOMERANG,
                TEEnchantments.WHIP_SWEEP,
                TEEnchantments.SUMMONER_PACT
        );

//        this.tag(EnchantmentTags.ON_RANDOM_LOOT).add(
//                TEEnchantments.MULTI_BOOMERANG,
//                TEEnchantments.WHIP_SWEEP
//        );

    }
}
