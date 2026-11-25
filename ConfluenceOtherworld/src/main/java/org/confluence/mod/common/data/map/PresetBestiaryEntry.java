package org.confluence.mod.common.data.map;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.init.ModDataMaps;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public record PresetBestiaryEntry(Either<Bestiary.Entry, Map<String, Bestiary.Entry>> either) {
    public static final Codec<PresetBestiaryEntry> CODEC = Codec.either(
            Bestiary.Entry.CODEC, Codec.unboundedMap(Codec.STRING, Bestiary.Entry.CODEC)
    ).xmap(PresetBestiaryEntry::new, PresetBestiaryEntry::either);

    public PresetBestiaryEntry(Bestiary.Entry entry) {
        this(Either.left(entry));
    }

    public PresetBestiaryEntry(Map<String, Bestiary.Entry> map) {
        this(Either.right(map));
    }

    public static @Nullable Bestiary.Entry getEntry(LivingEntity living, String key) {
        PresetBestiaryEntry preset = ModDataMaps.getEntityData(ModDataMaps.BESTIARY_ENTRY, living.getType());
        if (preset == null) return null;
        Bestiary.Entry entry = preset.either.map(Function.identity(), map -> map.get(key));
        if (entry == null) return null;
        entry = entry.copy();
        entry.key = key;
        return entry;
    }
}
