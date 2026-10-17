package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;

import java.util.concurrent.CompletableFuture;

public class ModSoulSkillTagsProvider extends TagsProvider<SoulSkill> {
    public ModSoulSkillTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper helper) {
        super(output, ModSoulSkills.REGISTRY_KEY, lookup, Confluence.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (var supplier : ModSoulSkills.getAllSkills()) {
            SoulSkill skill = supplier.get();
            TagKey<SoulSkill> tag = switch (ModSoulSkills.getCategory(supplier)) {
                case SOUL -> ModTags.SoulSkills.SOUL;
                case BLOOD_NATURE -> ModTags.SoulSkills.BLOOD_NATURE;
                case STAR_LURE -> ModTags.SoulSkills.STAR_LURE;
                case EMPTY -> null;
            };
            if (tag != null) {
                tag(tag).add(ResourceKey.create(ModSoulSkills.REGISTRY_KEY, skill.getId()));
            }
        }
        // 引魂：迷魂孢雾、亵渎之魂、自然之惩戒、魂引天星、引魂激荡、强力引魂、引魂
        tag(ModTags.SoulSkills.SOUL_LURE)
                .add(key(ModSoulSkills.CONFUSE_SPORES.get()))
                .add(key(ModSoulSkills.PROFANE_SOUL.get()))
                .add(key(ModSoulSkills.NATURES_WRATH.get()))
                .add(key(ModSoulSkills.STAR_CALL.get()))
                .add(key(ModSoulSkills.LURE_SURGE.get()))
                .add(key(ModSoulSkills.ENHANCED_LURE.get()))
                .add(key(ModSoulSkills.SOUL_LURE.get()));
    }

    private static ResourceKey<SoulSkill> key(SoulSkill skill) {
        return ResourceKey.create(ModSoulSkills.REGISTRY_KEY, skill.getId());
    }
}
