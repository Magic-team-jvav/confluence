package org.confluence.mod.mixin.integration.terra_entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.integration.terra_entity.IAbstractTerraNPC;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.house.House;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractTerraNPC.class, remap = false)
public abstract class AbstractTerraNPCMixin implements IAbstractTerraNPC, SelfGetter<AbstractTerraNPC> {
    @Shadow
    public abstract void initName();

    @Unique
    private NPCSpawner.Region confluence$region = NPCSpawner.Region.ZERO;
    @Unique
    private boolean confluence$shouldInteract = false;

    @Override
    public void confluence$setRegion(NPCSpawner.Region region) {
        this.confluence$region = region;
    }

    @Override
    public NPCSpawner.Region confluence$getRegion() {
        return confluence$region;
    }

    @Override
    public void confluence$setShouldInteract(boolean should) {
        this.confluence$shouldInteract = should;
    }

    @Override
    public boolean confluence$shouldInteract() {
        return confluence$shouldInteract;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        initName();
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("confluence:region")) {
            this.confluence$region = NPCSpawner.Region.CODEC.parse(NbtOps.INSTANCE, tag.get("confluence:region")).result().orElse(NPCSpawner.Region.ZERO);
        }
        this.confluence$shouldInteract = tag.getBoolean("confluence:should_interact");
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void save(CompoundTag tag, CallbackInfo ci) {
        NPCSpawner.Region.CODEC.encodeStart(NbtOps.INSTANCE, confluence$region).ifSuccess(tag1 -> tag.put("confluence:region", tag1));
        tag.putBoolean("confluence:should_interact", confluence$shouldInteract);
    }

    @Inject(method = "setHouse", at = @At("HEAD"))
    private void setRegion(House house, CallbackInfo ci) {
        if (!house.isEmpty()) {
            NPCSpawner.Region region = new NPCSpawner.Region(house.center());
            NPCSpawner.INSTANCE.moveNPCToAnotherRegion(confluence$self(), confluence$getRegion(), region);
            AchievementUtils.noHobo(confluence$self(), region);
        }
    }

    @Inject(method = "removeWhenFarAway", at = @At("HEAD"), cancellable = true)
    private void denyRemove(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
