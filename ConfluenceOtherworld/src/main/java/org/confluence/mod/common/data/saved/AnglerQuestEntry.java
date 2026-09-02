package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public record AnglerQuestEntry(ItemStack fish, AnglerCatchCondition condition) {
    public static final Codec<AnglerQuestEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("fish").forGetter(AnglerQuestEntry::fish),
            AnglerCatchCondition.CODEC.fieldOf("condition").forGetter(AnglerQuestEntry::condition)
    ).apply(instance, AnglerQuestEntry::new));

    public AnglerQuestEntry {
        if (fish.isEmpty()) throw new IllegalArgumentException("Angler quest fish cannot be empty");
        fish = fish.copy();
        condition = Objects.requireNonNull(condition, "Angler quest catch condition");
    }

    public boolean canBeCaught(FishingHook hook) {
        return condition.matches(hook);
    }
}
