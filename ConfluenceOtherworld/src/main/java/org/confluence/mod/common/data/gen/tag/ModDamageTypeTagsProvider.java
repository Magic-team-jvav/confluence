package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.common.LibTags;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.common.init.TCTags;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagsProvider extends DamageTypeTagsProvider {
    public ModDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper helper) {
        super(output, future, Confluence.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TCTags.HARMFUL_EFFECT).add(LibDamageTypes.ACID_VENOM, LibDamageTypes.CURSED_INFERNO, LibDamageTypes.FROST_BURN);
        tag(LibTags.DamageTypes.AS_MELEE_ATTACK).add(LibDamageTypes.SWORD_PROJECTILE, DamageTypes.MOB_ATTACK, DamageTypes.MOB_ATTACK_NO_AGGRO, DamageTypes.PLAYER_ATTACK, DamageTypes.STING);
        tag(PortTags.DamageTypes.IS_MAGIC).add(LibDamageTypes.MAGICAL_PROJECTILE);
        tag(DamageTypeTags.IS_PROJECTILE).add(LibDamageTypes.GUN_BULLET, LibDamageTypes.MAGICAL_PROJECTILE);
        tag(DamageTypeTags.BYPASSES_ARMOR).add(LibDamageTypes.DUNGEON_GUARDIAN);
    }
}
