package org.confluence.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import org.confluence.lib.mixed.IPoseStack;

public class AntiPushPoseStack extends PoseStack implements IPoseStack {
    @Override
    public void popPose() {
        popPose(false);
    }

    @Override
    public void pushPose() {
        pushPose(false);
    }

    public void popPose(boolean real) {
        if (real) {
            super.popPose();
        }
    }

    public void pushPose(boolean real) {
        if (real) {
            super.pushPose();
        }
    }

    @Override
    public boolean confluence$isAntiPush() {
        return true;
    }
}
