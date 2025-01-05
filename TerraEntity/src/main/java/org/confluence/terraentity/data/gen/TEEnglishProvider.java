package org.confluence.terraentity.data.gen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.init.TEItems;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.confluence.terraentity.TerraEntity.MODID;

public class TEEnglishProvider extends LanguageProvider {
    public TEEnglishProvider(PackOutput output) {
        super(output, MODID, "en_us");
    }

    private static String toTitleCase(String raw) {
        return Arrays.stream(raw.split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    @Override
    protected void addTranslations() {

        TEItems.SPAWN_EGGS.getEntries().forEach(entity -> add(entity.get(), toTitleCase(entity.getId().getPath())));
        TEEntities.ENTITIES.getEntries().forEach(entity -> add(entity.get(), toTitleCase(entity.getId().getPath())));
        TEEffects.EFFECTS.getEntries().forEach(effect -> add(effect.get(), toTitleCase(effect.getId().getPath())));

        add("itemGroup.terraentity.title", "tab.terraentity.name");
        add("message.terraentity.boss_spawn", "%s Has Awoken!");
        add("message.terraentity.boss_leave", "%s Have Been Defeated!");
        add("message.terraentity.boss_discard", "Has Been Discarded！");

        // Config
        add("terra_entity.configuration.boss_clear_when_no_target", "Clear Boss When No Target");

    }
}
