package org.confluence.mod.mixin.integration.particlestorm;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/// ParticleStorm 的 GeckoLib 重载回调列表不是线程安全的：每创建一个 GeckoLib 的 AnimatableInstanceCache / AnimationController
/// 都会往这个静态 ArrayList 里加一项，单人游戏里内置服务器线程也会创建它们；
/// 而客户端线程在资源重载后的第一个 tick 遍历这个列表（afterReload）。两边碰上就是
/// ConcurrentModificationException，进入世界时偶发崩溃（2026-11-09）。
/// 这里让添加、清空都在锁里进行，遍历时先在锁里拷一份快照再逐个执行。
@Mixin(targets = "org.mesdag.particlestorm.api.geckolib.GeckoLibHelper", remap = false)
public abstract class GeckoLibHelperMixin {
    @Shadow
    @Final
    private static List<Runnable> runners;

    @Inject(method = "addReloadCallback", at = @At("HEAD"), cancellable = true)
    private static void confluence$addSafely(Runnable runnable, CallbackInfo ci) {
        synchronized (runners) {
            runners.add(runnable);
        }
        ci.cancel();
    }

    @Inject(method = "clearReloadCallbacks", at = @At("HEAD"), cancellable = true)
    private static void confluence$clearSafely(CallbackInfo ci) {
        synchronized (runners) {
            runners.clear();
        }
        ci.cancel();
    }

    @Inject(method = "afterReload", at = @At("HEAD"), cancellable = true)
    private static void confluence$runSnapshot(CallbackInfo ci) {
        Runnable[] snapshot;
        synchronized (runners) {
            snapshot = runners.toArray(new Runnable[0]);
        }
        for (Runnable runnable : snapshot) runnable.run();
        ci.cancel();
    }
}
