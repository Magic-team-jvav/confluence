package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.language.*;

import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends LanguageProvider {

    private final boolean chinese;
    private final CompletableFuture<HolderLookup.Provider> lookup;

    public ModLanguageProvider(PackOutput output, String locale, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, Confluence.MODID, locale);
        chinese = "zh_cn".equals(locale);
        this.lookup = lookup;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return lookup.thenCompose(registries -> super.run(cache));
    }

    @Override
    protected void addTranslations() {
        Confluence.LANGUAGE_PROVIDERS.forEach(provider -> provider.accept(this::addTranslation));
        ItemGroupLanguageSubProvider.addTranslations(this::addTranslation);
        ItemLanguageSubProvider.addTranslations(this::addTranslation);
        EntityLanguageSubProvider.addTranslations(this::addTranslation);
        TooltipLanguageSubProvider.addTranslations(this::addTranslation);
        ArmorBonusLanguageSubProvider.addTranslations(this::addTranslation);
        InterfaceLanguageSubProvider.addTranslations(this::addTranslation);
        WorldLanguageSubProvider.addTranslations(this::addTranslation);
        PrefixLanguageSubProvider.addTranslations(this::addTranslation);
        SoundLanguageSubProvider.addTranslations(this::addTranslation);
        IntegrationLanguageSubProvider.addTranslations(this::addTranslation);
        ConfigurationLanguageSubProvider.addTranslations(this::addTranslation);
        AchievementsLanguageSubProvider.addTranslations(this::addTranslation);
        BestiaryLanguageSubProvider.addTranslations(this::addTranslation);
        NPCDialogProvider.addTranslations(this::add, !chinese);
        LucyTheAxeLanguageSubProvider.addTranslations(this::add, !chinese);
    }

    private void addTranslation(String key, String english, String chinese) {
        add(key, this.chinese ? chinese : english);
    }
}
