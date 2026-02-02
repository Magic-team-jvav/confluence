package org.confluence.lib.mixin;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin {
    @Inject(method = "parse", at = @At("TAIL"))
    private static <T> void decode(Dynamic<T> tag, LevelSettings levelSettings, PrimaryLevelData.SpecialWorldProperty specialWorldProperty, WorldOptions worldOptions, Lifecycle worldGenSettingsLifecycle, CallbackInfoReturnable<PrimaryLevelData> cir) {
        for (IGlobalData data : IGlobalData.DAT) {
            data.decode(tag.get(data.serializeKey()).orElseEmptyMap());
        }
    }

    @Inject(method = "setTagData", at = @At("TAIL"))
    private void encode(RegistryAccess registry, CompoundTag nbt, CompoundTag playerNBT, CallbackInfo ci) {
        for (IGlobalData data : IGlobalData.DAT) {
            CompoundTag tag = new CompoundTag();
            data.encode(tag);
            nbt.put(data.serializeKey(), tag);
        }
    }
}
