package org.confluence.terra_guns.common.datagen.provider;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.common.init.TGItems;

public class TGItemModelProvider extends ItemModelProvider {
    public TGItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TerraGuns.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        TGItems.BULLETS.getEntries().forEach(item -> {
            if (item.get() instanceof BlockItem) return;
            try {
                String path = item.getId().getPath().toLowerCase();
                withExistingParent(path, "item/generated").texture("layer0", TerraGuns.asResource("item/" + path));
            } catch (Exception e) {
                TerraGuns.LOGGER.error(e.getMessage());
            }
        });
    }
}