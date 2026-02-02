package org.confluence.terra_furniture.common.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.init.TFBlocks;

public class TFEnglishProvider extends LanguageProvider {
    public TFEnglishProvider(PackOutput output) {
        super(output, TerraFurniture.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("creativetab.terra_furniture", "Terra Furniture");

        add("msg.terra_furniture.sit", "Something wrong happened, it's the reason why you can't sit on this block.");

        add("container.terra_furniture.glass_kiln", "Glass Kiln");
        add("container.terra_furniture.living_loom", "Living Loom");
        add("container.terra_furniture.ice_machine", "Ice Machine");
        add("container.terra_furniture.trash_can", "Trash Can");

        add("title.terra_furniture.glass_kiln", "Glass Kiln");
        add("title.terra_furniture.living_loom", "Living Loom");
        add("title.terra_furniture.ice_machine", "Ice Machine");

        add("info.terra_furniture.time", "Time: [%s:%s]");

        TFBlocks.BLOCKS.getEntries().forEach(block -> add(block.get(), LibUtils.toTitleCase(block.getId().getPath())));
    }
}
