package org.confluence.mod.client.renderer;

import net.irisshaders.iris.api.v0.IrisApi;

public class VoidSeaIrisCompat {
    public static boolean isRenderingShadowPass() {
        return IrisApi.getInstance().isRenderingShadowPass();
    }
}
