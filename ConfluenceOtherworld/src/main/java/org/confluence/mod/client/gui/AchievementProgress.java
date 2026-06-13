package org.confluence.mod.client.gui;

import PortLib.extensions.net.minecraft.advancements.AdvancementProgress.PortAdvancementProgressExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;

import java.util.Map;

public class AchievementProgress extends AdvancementProgress {
    public static final Codec<AdvancementProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PortAdvancementProgressExtension.criteriaCodec().optionalFieldOf("criteria", Map.of()).forGetter(progress -> progress.criteria),
            Codec.BOOL.fieldOf("done").forGetter(AdvancementProgress::isDone)
    ).apply(instance, AchievementProgress::new));
    private final boolean isDone;

    public AchievementProgress(Map<String, CriterionProgress> criteria, boolean isDone) {
        super(criteria);
        this.isDone = isDone;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }
}
