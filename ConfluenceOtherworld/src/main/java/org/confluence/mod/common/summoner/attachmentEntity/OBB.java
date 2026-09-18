package org.confluence.mod.common.summoner.attachmentEntity;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class OBB {
    public final Vector3f center, extents, axisX, axisY, axisZ;

    public OBB(Vec3 centerPos, Vec3 size, float yaw, float pitch, float roll) {
        this.center = new Vector3f((float) centerPos.x, (float) centerPos.y, (float) centerPos.z);
        this.extents = new Vector3f((float) size.x / 2.0f, (float) size.y / 2.0f, (float) size.z / 2.0f);
        Quaternionf q = new Quaternionf().rotateY((float) Math.toRadians(-yaw)).rotateX((float) Math.toRadians(pitch)).rotateZ((float) Math.toRadians(roll));
        this.axisX = new Vector3f(1, 0, 0).rotate(q);
        this.axisY = new Vector3f(0, 1, 0).rotate(q);
        this.axisZ = new Vector3f(0, 0, 1).rotate(q);
    }

    public AABB getBoundingBox() {
        float dx = Math.abs(axisX.x * extents.x) + Math.abs(axisY.x * extents.y) + Math.abs(axisZ.x * extents.z);
        float dy = Math.abs(axisX.y * extents.x) + Math.abs(axisY.y * extents.y) + Math.abs(axisZ.y * extents.z);
        float dz = Math.abs(axisX.z * extents.x) + Math.abs(axisY.z * extents.y) + Math.abs(axisZ.z * extents.z);
        return new AABB(center.x - dx, center.y - dy, center.z - dz, center.x + dx, center.y + dy, center.z + dz);
    }

    public boolean intersects(AABB aabb) {
        Vector3f aC = new Vector3f((float) aabb.getCenter().x, (float) aabb.getCenter().y, (float) aabb.getCenter().z);
        Vector3f aE = new Vector3f((float) (aabb.getXsize() / 2), (float) (aabb.getYsize() / 2), (float) (aabb.getZsize() / 2));
        Vector3f t = new Vector3f(center).sub(aC);
        float[][] R = new float[3][3], absR = new float[3][3];
        Vector3f[] v = {axisX, axisY, axisZ};

        for (int i = 0; i < 3; i++) {
            R[0][i] = v[i].x;
            R[1][i] = v[i].y;
            R[2][i] = v[i].z;
            absR[0][i] = Math.abs(R[0][i]) + 1e-5f;
            absR[1][i] = Math.abs(R[1][i]) + 1e-5f;
            absR[2][i] = Math.abs(R[2][i]) + 1e-5f;
        }
        float[] tA = {t.x, t.y, t.z}, eA = {aE.x, aE.y, aE.z}, eB = {extents.x, extents.y, extents.z};

        for (int i = 0; i < 3; i++)
            if (Math.abs(tA[i]) > eA[i] + eB[0] * absR[i][0] + eB[1] * absR[i][1] + eB[2] * absR[i][2]) return false;
        for (int i = 0; i < 3; i++)
            if (Math.abs(tA[0] * R[0][i] + tA[1] * R[1][i] + tA[2] * R[2][i]) > eA[0] * absR[0][i] + eA[1] * absR[1][i] + eA[2] * absR[2][i] + eB[i])
                return false;

        if (Math.abs(tA[2] * R[1][0] - tA[1] * R[2][0]) > eA[1] * absR[2][0] + eA[2] * absR[1][0] + eB[1] * absR[0][2] + eB[2] * absR[0][1])
            return false;
        if (Math.abs(tA[2] * R[1][1] - tA[1] * R[2][1]) > eA[1] * absR[2][1] + eA[2] * absR[1][1] + eB[0] * absR[0][2] + eB[2] * absR[0][0])
            return false;
        if (Math.abs(tA[2] * R[1][2] - tA[1] * R[2][2]) > eA[1] * absR[2][2] + eA[2] * absR[1][2] + eB[0] * absR[0][1] + eB[1] * absR[0][0])
            return false;
        if (Math.abs(tA[0] * R[2][0] - tA[2] * R[0][0]) > eA[0] * absR[2][0] + eA[2] * absR[0][0] + eB[1] * absR[1][2] + eB[2] * absR[1][1])
            return false;
        if (Math.abs(tA[0] * R[2][1] - tA[2] * R[0][1]) > eA[0] * absR[2][1] + eA[2] * absR[0][1] + eB[0] * absR[1][2] + eB[2] * absR[1][0])
            return false;
        if (Math.abs(tA[0] * R[2][2] - tA[2] * R[0][2]) > eA[0] * absR[2][2] + eA[2] * absR[0][2] + eB[0] * absR[1][1] + eB[1] * absR[1][0])
            return false;
        if (Math.abs(tA[1] * R[0][0] - tA[0] * R[1][0]) > eA[0] * absR[1][0] + eA[1] * absR[0][0] + eB[1] * absR[2][2] + eB[2] * absR[2][1])
            return false;
        if (Math.abs(tA[1] * R[0][1] - tA[0] * R[1][1]) > eA[0] * absR[1][1] + eA[1] * absR[0][1] + eB[0] * absR[2][2] + eB[2] * absR[2][0])
            return false;
        if (Math.abs(tA[1] * R[0][2] - tA[0] * R[1][2]) > eA[0] * absR[1][2] + eA[1] * absR[0][2] + eB[0] * absR[2][1] + eB[1] * absR[2][0])
            return false;
        return true;
    }
}
