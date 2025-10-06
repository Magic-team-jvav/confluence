package org.confluence.mod.common.data.map;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.api.event.bestiary.RegisterBestiaryKeyEvent;
import org.confluence.mod.common.data.saved.BestiaryEntry;
import org.confluence.mod.common.init.ModDataMaps;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public record PresetBestiaryEntry(Either<BestiaryEntry, Map<String, BestiaryEntry>> either) {
    public static final Codec<PresetBestiaryEntry> CODEC = Codec.either(
            BestiaryEntry.CODEC, Codec.unboundedMap(Codec.STRING, BestiaryEntry.CODEC)
    ).xmap(PresetBestiaryEntry::new, PresetBestiaryEntry::either);

    public PresetBestiaryEntry(BestiaryEntry entry) {
        this(Either.left(entry));
    }

    public PresetBestiaryEntry(Map<String, BestiaryEntry> map) {
        this(Either.right(map));
    }

    public static @Nullable BestiaryEntry getEntry(LivingEntity living) {
        PresetBestiaryEntry preset = ModDataMaps.getEntityData(ModDataMaps.BESTIARY_ENTRY, living.getType());
        if (preset == null) return null;
        String key = RegisterBestiaryKeyEvent.getKey(living);
        BestiaryEntry entry = preset.either.map(Function.identity(), map -> map.get(key));
        if (entry == null) return null;
        entry = entry.copy();
        entry.key = key;
        return entry;
    }
}
