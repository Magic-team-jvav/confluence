package org.confluence.mod.client.graphics;

import java.lang.reflect.Method;

/// 是否开着 Iris/Oculus 的光影包（反射调用，没装就当没开）。开着时 Confluence 自己的画质后处理让位给光影包。
public final class ShaderPackDetector {
    private static Object api;
    private static Method inUse;
    private static boolean searched;
    /// 结果缓存一秒：每帧会问好几次，反射调用没必要每次都做
    private static boolean cached;
    private static long checkedAt;

    private ShaderPackDetector() {}

    public static boolean shaderPackInUse() {
        if (!searched) {
            searched = true;
            for (String name : new String[]{"net.irisshaders.iris.api.v0.IrisApi", "net.coderbot.iris.api.v0.IrisApi"}) {
                try {
                    Class<?> cls = Class.forName(name);
                    api = cls.getMethod("getInstance").invoke(null);
                    inUse = cls.getMethod("isShaderPackInUse");
                    break;
                } catch (ReflectiveOperationException | LinkageError ignored) {
                }
            }
        }
        if (api == null || inUse == null) return false;
        long now = System.nanoTime();
        if (now - checkedAt < 1_000_000_000L) return cached;
        checkedAt = now;
        try {
            cached = (boolean) inUse.invoke(api);
        } catch (ReflectiveOperationException e) {
            cached = false;
        }
        return cached;
    }
}
