package org.confluence.terra_curio.common.init;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.advancement.CuriosEquippedTrigger;

import java.util.function.Supplier;

public final class TCTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TYPES = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, TerraCurio.MODID);

    public final static Supplier<CuriosEquippedTrigger> CURIOS_EQUIPPED = TYPES.register("curios_equipped", CuriosEquippedTrigger::new);
}
