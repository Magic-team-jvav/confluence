package org.confluence.mod.client.summoner;

public class ModelContext {

    public float scaleX = 1.0F;
    public float scaleY = 1.0F;
    public float scaleZ = 1.0F;

    public float translateX = 0.0F;
    public float translateY = 0.0F;
    public float translateZ = 0.0F;

    public float yawOffset = 0.0F;
    public float pitchOffset = 0.0F;
    public float rollOffset = 0.0F;

    public float alphaDistanceFactor = 1.0F;

    public ModelContext scale(float scale) {
        return scale(scale, scale, scale);
    }

    public ModelContext scale(float scaleX, float scaleY, float scaleZ) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        return this;
    }

    public ModelContext translateOffset(float x, float y, float z) {
        this.translateX = x;
        this.translateY = y;
        this.translateZ = z;
        return this;
    }

    public ModelContext rotationOffset(float yaw, float pitch, float roll) {
        this.yawOffset = yaw;
        this.pitchOffset = pitch;
        this.rollOffset = roll;
        return this;
    }

    public ModelContext alphaDistanceFactor(float factor) {
        this.alphaDistanceFactor = factor;
        return this;
    }
}
