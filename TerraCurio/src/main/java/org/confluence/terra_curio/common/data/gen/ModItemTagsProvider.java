package org.confluence.terra_curio.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> b, @Nullable ExistingFileHelper helper) {
        super(output, provider, b, TerraCurio.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        IntrinsicTagAppender<Item> accessory = tag(TCTags.ACCESSORY);
        accessory.add(TCItems.BASE_POINT.get());
        accessory.add(TCItems.EVERLASTING.get());
        TCItems.CURIOS.getEntries().forEach(item -> accessory.add(item.get()));
        tag(TCTags.DIVING).add(
                TCItems.DIVING_HELMET.get(),
                TCItems.DIVING_GEAR.get(),
                TCItems.JELLYFISH_DIVING_GEAR.get(),
                TCItems.ARCTIC_DIVING_GEAR.get()
        );
        tag(TCTags.ANY_SANDSTORM_BALLOONS).add(
                TCItems.SANDSTORM_IN_A_BALLOON.get(),
                TCItems.YELLOW_HORSESHOE_BALLOON.get()
        );
        tag(TCTags.ANY_BLIZZARD_BALLOONS).add(
                TCItems.BLIZZARD_IN_A_BALLOON.get(),
                TCItems.WHITE_HORSESHOE_BALLOON.get()
        );
        tag(TCTags.ANY_CLOUD_BALLOONS).add(
                TCItems.CLOUD_IN_A_BALLOON.get(),
                TCItems.BLUE_HORSESHOE_BALLOON.get()
        );
    }
}
