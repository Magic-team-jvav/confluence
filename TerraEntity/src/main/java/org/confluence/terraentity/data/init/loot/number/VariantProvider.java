package org.confluence.terraentity.data.init.loot.number;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.confluence.terraentity.data.init.loot.TELootParams;
import org.confluence.terraentity.init.TELoots;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

public record VariantProvider() implements NumberProvider {

    public static final Supplier<VariantProvider> INSTANCE = Suppliers.memoize(VariantProvider::new);

    public static final MapCodec<VariantProvider> CODEC = Codec.of(Encoder.empty(), Decoder.unit(INSTANCE));

    public @NotNull LootNumberProviderType getType() {
        return TELoots.TELootNumberProviders.VARIANT.get();
    }

    @Override
    public float getFloat(LootContext context) {
        Integer var3 = context.getParamOrNull(TELootParams.VARIANT);
        if (var3 == null) {
            return -1;
        }
//        provider.getFloat()
        return (float) var3;
    }

    @Override
    public @NotNull Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(TELootParams.VARIANT);
    }
}