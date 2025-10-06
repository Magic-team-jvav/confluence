package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.hook.HookType;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.hook.dynamic.ISerializableHook;
import com.xiaohunao.equipment_benediction.common.init.EBRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.equipment_set.hook.*;

public final class ModHookTypes {
    public static final DeferredRegister<HookType<?>> TYPES = DeferredRegister.create(EBRegistries.Keys.HOOK_TYPES, Confluence.MODID);

    public static final DeferredHolder<HookType<?>, HookType<ManaConsumeHook>> MANA_CONSUME = register("mana_consume", ManaConsumeHook.class);
    public static final DeferredHolder<HookType<?>, HookType<AdditionalManaHook>> ADDITIONAL_MANA = register("additional_mana", AdditionalManaHook.class);
    public static final DeferredHolder<HookType<?>, HookType<FishingPowerHook>> FISHING_POWER = register("fishing_power", FishingPowerHook.class);
    public static final DeferredHolder<HookType<?>, HookType<LivingFreezeHook>> LIVING_FREEZE = register("living_freeze", LivingFreezeHook.class);
    public static final DeferredHolder<HookType<?>, HookType<SkipAmmoConsumeHook>> SKIP_AMMO_CONSUME = register("skip_ammo_consume", SkipAmmoConsumeHook.class);

    private static <T extends IHook> DeferredHolder<HookType<?>, HookType<T>> register(String id, Class<T> hookClass) {
        return TYPES.register(id, () -> HookType.createHook(EquipmentBenediction.asResource(id), hookClass));
    }

    private static <T extends ISerializableHook> DeferredHolder<HookType<?>, HookType<T>> registerSerializable(String id, Class<T> hookClass, MapCodec<T> codec) {
        return TYPES.register(id, () -> HookType.createSerializableHook(EquipmentBenediction.asResource(id), hookClass, codec));
    }
}
