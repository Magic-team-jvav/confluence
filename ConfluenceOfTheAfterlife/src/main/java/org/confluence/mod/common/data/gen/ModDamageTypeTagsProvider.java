package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.terra_curio.common.init.TCTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagsProvider extends DamageTypeTagsProvider {
    public ModDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper helper) {
        super(output, future, Confluence.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(TCTags.HARMFUL_EFFECT).add(
                ModDamageTypes.ACID_VENOM,
                ModDamageTypes.CURSED_INFERNO,
                ModDamageTypes.FROST_BURN
        );
    }
}
