package org.confluence.mod.mixin.world.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshotCarrier;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// 让连弩烟花与箭矢使用同一当前格式冻结战斗快照。
@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin implements ProjectileCombatSnapshotCarrier {
    @Unique
    private static final String confluence$SNAPSHOT_KEY = "ConfluenceProjectileSnapshot";
    @Unique
    private @Nullable ProjectileCombatSnapshot confluence$combatSnapshot;
    @Unique
    private boolean confluence$invalidCombatSnapshot;

    @Override
    public @Nullable ProjectileCombatSnapshot getProjectileCombatSnapshot() {
        return confluence$combatSnapshot;
    }

    @Override
    public void setProjectileCombatSnapshot(ProjectileCombatSnapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Firework projectile combat snapshot must not be null");
        }
        confluence$combatSnapshot = snapshot;
        confluence$invalidCombatSnapshot = false;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveSnapshot(CompoundTag tag, CallbackInfo ci) {
        if (confluence$combatSnapshot != null) {
            tag.put(confluence$SNAPSHOT_KEY, confluence$combatSnapshot.toTag());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readSnapshot(CompoundTag tag, CallbackInfo ci) {
        confluence$combatSnapshot = null;
        confluence$invalidCombatSnapshot = false;
        if (tag.contains(confluence$SNAPSHOT_KEY, CompoundTag.TAG_COMPOUND)) {
            try {
                confluence$combatSnapshot = ProjectileCombatSnapshot.fromTag(
                        tag.getCompound(confluence$SNAPSHOT_KEY));
            } catch (RuntimeException exception) {
                confluence$invalidCombatSnapshot = true;
                Confluence.LOGGER.error(
                        "Discarding firework with invalid projectile combat snapshot", exception);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void discardInvalidSnapshot(CallbackInfo ci) {
        FireworkRocketEntity self = (FireworkRocketEntity) (Object) this;
        if (confluence$invalidCombatSnapshot && !self.level().isClientSide) {
            self.discard();
            ci.cancel();
        }
    }
}
