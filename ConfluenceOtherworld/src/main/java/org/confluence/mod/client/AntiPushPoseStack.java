package org.confluence.mod.client;

import com.mojang.blaze3d.vertex.PoseStack;

public class AntiPushPoseStack extends PoseStack {
    @Override
    public void popPose(){
        popPose(false);
    }

    @Override
    public void pushPose(){
        pushPose(false);
    }

    public void popPose(boolean real){
        if(real){
            super.popPose();
        }
    }
    public void pushPose(boolean real){
        if(real){
            super.pushPose();
        }
    }
}
