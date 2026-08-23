package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.ModDataProvider;

import java.util.concurrent.CompletableFuture;

/// 生成本体世界预设的原版分类标签。
///
/// 预设本身由内置注册表 Provider 生成，这里复用同一组 {@code ResourceKey} 将其加入世界
/// 创建界面的“普通”预设集合，避免注册 ID 与手写标签各自维护。
public final class ModWorldPresetTagsProvider extends TagsProvider<WorldPreset> {
    public ModWorldPresetTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper helper) {
        super(output, Registries.WORLD_PRESET, lookup, Confluence.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(WorldPresetTags.NORMAL).add(ModDataProvider.WorldPresetz.THE_CORRUPTION, ModDataProvider.WorldPresetz.THE_CRIMSON);
    }
}
