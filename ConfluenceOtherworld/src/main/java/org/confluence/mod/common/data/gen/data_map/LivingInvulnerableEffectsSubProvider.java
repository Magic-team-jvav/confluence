package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.LivingInvulnerableEffects;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terraentity.init.entity.TEBossEntities;

import java.util.Objects;

public final class LivingInvulnerableEffectsSubProvider {
    public static void gather(ModDataMapProvider.Appender<Builder> appender) {
        appender.create() // todo 给牢镜看的：下方的Builder有四个预制方法，要活用，不会的问我。看完删
                .add(TEBossEntities.KING_SLIME, ModEffects.SHIMMER, TCEffects.CONFUSED, MobEffects.POISON);
    }

    public static class Builder extends DataMapProvider.Builder<LivingInvulnerableEffects, EntityType<?>> {
        public Builder() {
            super(ModDataMaps.LIVING_INVULNERABLE_EFFECTS);
        }

        public Builder add(IHolderExtension<EntityType<?>> holder, HolderSet<MobEffect> effects) {
            super.add(Objects.requireNonNull(holder.getKey()), new LivingInvulnerableEffects(effects), false);
            return this;
        }

        @SafeVarargs
        public final Builder add(IHolderExtension<EntityType<?>> holder, Holder<MobEffect>... effects) {
            super.add(Objects.requireNonNull(holder.getKey()), new LivingInvulnerableEffects(HolderSet.direct(effects)), false);
            return this;
        }

        public Builder add(TagKey<EntityType<?>> tagKey, HolderSet<MobEffect> effects) {
            super.add(tagKey, new LivingInvulnerableEffects(effects), false);
            return this;
        }

        @SafeVarargs
        public final Builder add(TagKey<EntityType<?>> tagKey, Holder<MobEffect>... effects) {
            super.add(tagKey, new LivingInvulnerableEffects(HolderSet.direct(effects)), false);
            return this;
        }
    }
}
