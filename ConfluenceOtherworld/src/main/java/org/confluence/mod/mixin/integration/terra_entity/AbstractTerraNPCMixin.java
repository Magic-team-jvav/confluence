package org.confluence.mod.mixin.integration.terra_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.init.ModAchievements;
import org.confluence.mod.mixed.IAbstractTerraNPC;
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
public abstract class AbstractTerraNPCMixin implements IAbstractTerraNPC {
    @Shadow
    protected abstract void initName();

    @Unique
    private NPCSpawner.Region confluence$region = new NPCSpawner.Region(BlockPos.ZERO);

    @Override
    public void confluence$setRegion(NPCSpawner.Region region) {
        this.confluence$region = region;
    }

    @Override
    public NPCSpawner.Region confluence$getRegion() {
        return confluence$region;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(EntityType<? extends AbstractTerraNPC> entityType, Level level, CallbackInfo ci) {
        initName();
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("confluence:region")) {
            this.confluence$region = NPCSpawner.Region.CODEC.parse(NbtOps.INSTANCE, tag.get("confluence:region")).getOrThrow();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void save(CompoundTag tag, CallbackInfo ci) {
        tag.put("confluence:region", NPCSpawner.Region.CODEC.encodeStart(NbtOps.INSTANCE, confluence$region).getOrThrow());
    }

    @Inject(method = "setHouse", at = @At("HEAD"))
    private void setRegion(House house, CallbackInfo ci) {
        NPCSpawner.Region region = new NPCSpawner.Region(house.center());
        confluence$setRegion(region);
        ModAchievements.noHobo((AbstractTerraNPC) (Object) this, region);
    }

    @Inject(method = "removeWhenFarAway", at = @At("HEAD"), cancellable = true)
    private void denyRemove(double distanceToClosestPlayer, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
