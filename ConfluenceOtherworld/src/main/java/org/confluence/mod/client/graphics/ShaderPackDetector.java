package org.confluence.mod.client.graphics;

import java.lang.reflect.Method;

/// 是否开着 Iris/Oculus 的光影包（反射调用，没装就当没开）。开着时 Confluence 自己的画质后处理让位给光影包。
public final class ShaderPackDetector {
    private static Object api;
    private static Method inUse;
    private static boolean searched;

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
        try {
            return (boolean) inUse.invoke(api);
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
}
