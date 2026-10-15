package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.init.ModSoulSkills.SkillCategory;
import org.confluence.mod.common.soul.skill.SoulSkill;
import org.confluence.mod.common.soul.skill.SoulSkillTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ModSoulSkillTagsProvider extends TagsProvider<SoulSkill> {
    public ModSoulSkillTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, ModSoulSkills.REGISTRY_KEY, lookupProvider, Confluence.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        addCategory(SoulSkillTags.SOUL, SkillCategory.SOUL);
        addCategory(SoulSkillTags.BLOOD_NATURE, SkillCategory.BLOOD_NATURE);
        addCategory(SoulSkillTags.STAR_LURE, SkillCategory.STAR_LURE);
    }

    private void addCategory(TagKey<SoulSkill> tag, SkillCategory category) {
        for (Supplier<SoulSkill> skill : ModSoulSkills.getSkillsByCategory(category)) {
            tag(tag).add(ResourceKey.create(ModSoulSkills.REGISTRY_KEY, skill.get().getId()));
        }
    }
}
