package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.lib.common.LibTags;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.terra_curio.common.init.TCTags;
import org.confluence.terraentity.init.TETags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagsProvider extends DamageTypeTagsProvider {
    public ModDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper helper) {
        super(output, future, Confluence.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TCTags.HARMFUL_EFFECT).add(
                ModDamageTypes.ACID_VENOM,
                ModDamageTypes.CURSED_INFERNO,
                TETags.DamageTypes.FROST_BURN
        );
        tag(LibTags.DamageTypes.AS_MELEE_ATTACK).add(
                ModDamageTypes.SWORD_PROJECTILE,
                DamageTypes.MOB_ATTACK,
                DamageTypes.MOB_ATTACK_NO_AGGRO,
                DamageTypes.PLAYER_ATTACK,
                DamageTypes.STING
        );
        tag(Tags.DamageTypes.IS_MAGIC).add(ModDamageTypes.MAGICAL_PROJECTILE);
        tag(DamageTypeTags.BYPASSES_ARMOR).add(TETags.DamageTypes.PASS_ARMOR);
    }
}
