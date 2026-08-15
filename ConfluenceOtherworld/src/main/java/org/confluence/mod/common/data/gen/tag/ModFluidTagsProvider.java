package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terra_curio.common.init.TCTags;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagsProvider extends FluidTagsProvider {
    public ModFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, Confluence.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 公共蜂蜜标签同时包含静止与流动流体，供跨模组配方和流体判断复用。
        tag(PortTags.Fluids.HONEY).add(
                ModFluids.HONEY.fluid().get(),
                ModFluids.HONEY.flowing().get()
        );
        tag(ModTags.Fluids.SHIMMER).add(
                ModFluids.SHIMMER.fluid().get(),
                ModFluids.SHIMMER.flowing().get()
        );

        // 钓鱼判定接受水、熔岩、蜂蜜和微光；not_lava 则明确排除熔岩。
        tag(ModTags.Fluids.FISHING_ABLE).addTags(
                FluidTags.WATER,
                FluidTags.LAVA,
                PortTags.Fluids.HONEY,
                ModTags.Fluids.SHIMMER
        );
        tag(ModTags.Fluids.NOT_LAVA).addTags(
                FluidTags.WATER,
                PortTags.Fluids.HONEY,
                ModTags.Fluids.SHIMMER
        );

        // TerraCurio 只声明能力标签；本体负责把自身流体追加进去，不修改饰品模块。
        tag(TCTags.WATER_LIKE_WALK).add(
                ModFluids.HONEY.fluid().get(),
                ModFluids.SHIMMER.fluid().get()
        );
    }
}
