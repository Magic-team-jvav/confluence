package org.confluence.terra_guns.common.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.common.init.TGTags;
import org.confluence.terra_guns.common.item.gun.BaseGun;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.confluence.terra_guns.common.init.TGItems.*;

public class TGItemTagsProvider extends ItemTagsProvider {
    public TGItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> completableFuture, @Nullable ExistingFileHelper helper) {
        super(output, provider, completableFuture, TerraGuns.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        BULLETS.getEntries().forEach(item -> tag(TGTags.BULLET).add(item.get()));
        GUNS.getEntries().forEach(item -> tag(TGTags.GUN).add(item.get()));
        tag(Tags.Items.RANGED_WEAPON_TOOLS).addTag(TGTags.GUN);

        tag(TGTags.SNOW_AMMO).add(Items.SNOWBALL);
        tag(TGTags.SEED_AMMO).addTag(Tags.Items.SEEDS);
        tag(TGTags.AMMO).addTags(TGTags.SEED_AMMO, TGTags.SNOW_AMMO, TGTags.BULLET);

        addAutomatic(MINISHARK);
        addAutomatic(SNOWBALL_CANNON);
        addAutomatic(BLOWGUN);

        addManual(HAND_GUN);
        addManual(PHOENIX_BLASTER);
        addManual(SHOTGUN);
        addManual(FLINTLOCK_PISTOL);
        addManual(BOOMSTICK);
        addManual(THE_UNDERTAKER);
        addManual(MUSKET);
    }

    protected void addAutomatic(Supplier<BaseGun> gunSupplier) {
        tag(TGTags.AUTOMATIC_GUN).add(gunSupplier.get());
    }

    protected void addManual(Supplier<BaseGun> gunSupplier) {
        tag(TGTags.MANUAL_GUN).add(gunSupplier.get());
    }
}
