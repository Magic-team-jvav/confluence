package org.confluence.mod.common.worldgen.biome.injector;

import org.confluence.mod.Confluence;

import java.util.concurrent.atomic.AtomicBoolean;

/// 注入链自检。
///
/// 群系与地表规则都靠 mixin 环绕原方法，一旦某一环断掉，现象是「什么都没发生」——
/// 既不报错也很难从游戏里判断到底是「规则没注册」「注册了但被覆盖」还是「注入点没命中」。
/// 这里用一次性日志把最后一环（注入点是否真的替换了规则源）钉死。
///
/// 调试完可以连同调用处一起删掉，对功能没有影响。
public final class InjectionProbe {
    private static final AtomicBoolean BIOME_APPLIED = new AtomicBoolean();
    private static final AtomicBoolean SURFACE_APPLIED = new AtomicBoolean();
    private static final AtomicBoolean SURFACE_MISSING = new AtomicBoolean();

    private InjectionProbe() {}

    /// 群系注入确实替换了一次结果。
    public static void biomeApplied() {
        if (BIOME_APPLIED.compareAndSet(false, true)) {
            Confluence.LOGGER.info("[probe] biome injection is live: a biome region has taken over a worldgen position");
        }
    }

    /// 地表规则注入确实替换了一次规则源。
    public static void surfaceApplied() {
        if (SURFACE_APPLIED.compareAndSet(false, true)) {
            Confluence.LOGGER.info("[probe] surface rule injection is live: composed rules are in effect");
        }
    }

    /// 注入点命中了，但这个生成器上没有拼好的规则 —— 说明 install() 没给到它。
    public static void surfaceMissing() {
        if (SURFACE_MISSING.compareAndSet(false, true)) {
            Confluence.LOGGER.warn("[probe] surface rule injection is installed but a chunk generator had no composed rules; vanilla rules are being used for it");
        }
    }
}
