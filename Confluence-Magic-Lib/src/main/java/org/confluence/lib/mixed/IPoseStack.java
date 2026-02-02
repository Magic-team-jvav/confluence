package org.confluence.lib.mixed;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IPoseStack {
    default boolean confluence$isAntiPush() {
        return false;
    }

    static IPoseStack of(PoseStack poseStack) {
        return (IPoseStack) poseStack;
    }

    static boolean isAntiPush(PoseStack poseStack) {
        return of(poseStack).confluence$isAntiPush();
    }
}
