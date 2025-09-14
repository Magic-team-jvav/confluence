package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.BestiaryEntry;
import org.confluence.mod.common.init.ModDataMaps;

import java.util.Objects;

public final class BestiaryEntrySubProvider {
    public static void gather(ModDataMapProvider.Appender<Builder> appender) {
        appender.create();
    }

    public static class Builder extends DataMapProvider.Builder<BestiaryEntry, EntityType<?>> {
        public Builder() {
            super(ModDataMaps.BESTIARY_ENTRY);
        }

        public Builder add(IHolderExtension<EntityType<?>> holder, float maxHealth, float knockbackResistance, float attackDamage, float armor, int drops) {
            BestiaryEntry entry = new BestiaryEntry();
            entry.type = holder.getDelegate().value();
            entry.maxHealth = maxHealth;
            entry.knockbackResistance = knockbackResistance;
            entry.attackDamage = attackDamage;
            entry.armor = armor;
            entry.drops = drops;
            super.add(Objects.requireNonNull(holder.getKey()), entry, false);
            return this;
        }
    }
}
