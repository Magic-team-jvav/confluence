package org.confluence.mod.common.data.gen.loot.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;

public class EMILootDataTableExclusions {
    public static final Codec<EMILootDataTableExclusions> CODEC = RecordCodecBuilder.create((tableExclusionsInstance) -> tableExclusionsInstance.group(
            Codec.list(Codec.STRING).fieldOf("exclusions").forGetter((instance) -> instance.excludedPaths)
    ).apply(tableExclusionsInstance, EMILootDataTableExclusions::new));

    public List<String> excludedPaths;

    public EMILootDataTableExclusions() {
        this.excludedPaths = new ArrayList<>();
    }

    EMILootDataTableExclusions(List<String> excludedPaths) {
        this.excludedPaths = excludedPaths;
    }
}
