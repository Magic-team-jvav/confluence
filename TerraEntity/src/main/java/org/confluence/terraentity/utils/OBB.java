
package org.confluence.terraentity.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.SliceShape;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector3f;

// Y ^       8   +-------+   7     axisY   axisZ
//   |          /|      /|             | /
//   |     4   +-------+ | 3           |/
//   |  Z      | |     | |             +-- axisX
//   |   /   5 | +-----|-+  6       Center
//   |  /      |/      |/
//   | /   1   +-------+   2
//   |/
//   +--------------------> X

/** 改自 {@link net.bettercombat.client.collision.OrientedBoundingBox} */
public class OBB extends SliceShape {
    public Vec3 center;
    public Vec3 extent;
    public Vec3 axisX;
    public Vec3 axisY;
    public Vec3 axisZ;
    public Vec3 scaledAxisX;
    public Vec3 scaledAxisY;
    public Vec3 scaledAxisZ;
    public Matrix3f rotation;
    public Vec3 vertex1;
    public Vec3 vertex2;
    public Vec3 vertex3;
    public Vec3 vertex4;
    public Vec3 vertex5;
    public Vec3 vertex6;
    public Vec3 vertex7;
    public Vec3 vertex8;
    public Vec3[] vertices;
    public Vec3 min;
    public Vec3 max;
    public AABB border;

    private OBB(){
        super(Shapes.block(), Direction.Axis.X, 0);
    }

    public OBB(Vec3 center, double x, double y, double z, float pitch, float yaw){
        this();
        this.rotation = new Matrix3f();
        this.center = center;
        this.extent = new Vec3(x / 2.0, y / 2.0, z / 2.0);
        this.axisZ = Vec3.directionFromRotation(pitch, yaw).normalize();
        this.axisY = Vec3.directionFromRotation(pitch + 90.0F, yaw).reverse().normalize();
        this.axisX = this.axisZ.cross(this.axisY);
    }

    public OBB(Vec3 center, Vec3 size, float yaw, float pitch){
        this(center, size.x, size.y, size.z, yaw, pitch);
    }

    public OBB(AABB box){
        this();
        this.rotation = new Matrix3f();
        this.center = new Vec3((box.maxX + box.minX) / 2.0, (box.maxY + box.minY) / 2.0, (box.maxZ + box.minZ) / 2.0);
        this.extent = new Vec3(Math.abs(box.maxX - box.minX) / 2.0, Math.abs(box.maxY - box.minY) / 2.0, Math.abs(box.maxZ - box.minZ) / 2.0);
        this.axisX = new Vec3(1.0, 0.0, 0.0);
        this.axisY = new Vec3(0.0, 1.0, 0.0);
        this.axisZ = new Vec3(0.0, 0.0, 1.0);
    }

    public OBB(OBB obb){
        this();
        this.rotation = new Matrix3f();
        this.center = obb.center;
        this.extent = obb.extent;
        this.axisX = obb.axisX;
        this.axisY = obb.axisY;
        this.axisZ = obb.axisZ;
    }

    public OBB copy(){
        return new OBB(this);
    }

    public OBB offsetAlongAxisX(double offset){
        this.center = this.center.add(this.axisX.scale(offset));
        return this;
    }

    public OBB offsetAlongAxisY(double offset){
        this.center = this.center.add(this.axisY.scale(offset));
        return this;
    }

    public OBB offsetAlongAxisZ(double offset){
        this.center = this.center.add(this.axisZ.scale(offset));
        return this;
    }

    public OBB offset(Vec3 offset){
        this.center = this.center.add(offset);
        return this;
    }

    public OBB scale(double scale){
        this.extent = this.extent.scale(scale);
        return this;
    }
    public OBB scale(double x, double y, double z){
        this.extent = this.extent.multiply(x, y, z);
        return this;
    }

    public OBB updateVertex(){
        if (FMLEnvironment.dist.isClient()) {
            Minecraft.getInstance().getProfiler().push("ObbUpdateVertex");
        }
        this.rotation.set(0, 0, (float) this.axisX.x);
        this.rotation.set(0, 1, (float) this.axisX.y);
        this.rotation.set(0, 2, (float) this.axisX.z);
        this.rotation.set(1, 0, (float) this.axisY.x);
        this.rotation.set(1, 1, (float) this.axisY.y);
        this.rotation.set(1, 2, (float) this.axisY.z);
        this.rotation.set(2, 0, (float) this.axisZ.x);
        this.rotation.set(2, 1, (float) this.axisZ.y);
        this.rotation.set(2, 2, (float) this.axisZ.z);
        this.scaledAxisX = this.axisX.scale(this.extent.x);
        this.scaledAxisY = this.axisY.scale(this.extent.y);
        this.scaledAxisZ = this.axisZ.scale(this.extent.z);
        this.vertex1 = this.center.subtract(this.scaledAxisZ).subtract(this.scaledAxisX).subtract(this.scaledAxisY);
        this.vertex2 = this.center.subtract(this.scaledAxisZ).add(this.scaledAxisX).subtract(this.scaledAxisY);
        this.vertex3 = this.center.subtract(this.scaledAxisZ).add(this.scaledAxisX).add(this.scaledAxisY);
        this.vertex4 = this.center.subtract(this.scaledAxisZ).subtract(this.scaledAxisX).add(this.scaledAxisY);
        this.vertex5 = this.center.add(this.scaledAxisZ).subtract(this.scaledAxisX).subtract(this.scaledAxisY);
        this.vertex6 = this.center.add(this.scaledAxisZ).add(this.scaledAxisX).subtract(this.scaledAxisY);
        this.vertex7 = this.center.add(this.scaledAxisZ).add(this.scaledAxisX).add(this.scaledAxisY);
        this.vertex8 = this.center.add(this.scaledAxisZ).subtract(this.scaledAxisX).add(this.scaledAxisY);
        this.vertices = new Vec3[]{this.vertex1, this.vertex2, this.vertex3, this.vertex4, this.vertex5, this.vertex6, this.vertex7, this.vertex8};

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        for(Vec3 v : vertices){
            minX = Math.min(minX, v.x);
            minY = Math.min(minY, v.y);
            minZ = Math.min(minZ, v.z);
            maxX = Math.max(maxX, v.x);
            maxY = Math.max(maxY, v.y);
            maxZ = Math.max(maxZ, v.z);
        }
        min = new Vec3(minX, minY, minZ);
        max = new Vec3(maxX, maxY, maxZ);
        border = new AABB(min, max);
        if (FMLEnvironment.dist.isClient()) {
            Minecraft.getInstance().getProfiler().pop();
        }
        return this;
    }

    public boolean contains(Vec3 point){
        Vector3f distance = point.subtract(this.center).toVector3f();
        distance.mulTranspose(this.rotation);
        return (double) Math.abs(distance.x()) < this.extent.x && (double) Math.abs(distance.y()) < this.extent.y && (double) Math.abs(distance.z()) < this.extent.z;
    }

    public boolean intersects(AABB boundingBox){
        OBB otherOBB = (new OBB(boundingBox)).updateVertex();
        return Intersects(this, otherOBB);
    }


    public boolean collide(AABB boundingBox, Vec3 myMotion, Vec3 othersMotion){
        return isColliding(this, new OBB(boundingBox).updateVertex(), myMotion, othersMotion);
    }

    public boolean intersects(OBB otherOBB){
        return Intersects(this, otherOBB);
    }

    public static boolean Intersects(OBB a, OBB b){
        if(Separated(a.vertices, b.vertices, a.scaledAxisX)){
            return false;
        }else if(Separated(a.vertices, b.vertices, a.scaledAxisY)){
            return false;
        }else if(Separated(a.vertices, b.vertices, a.scaledAxisZ)){
            return false;
        }else if(Separated(a.vertices, b.vertices, b.scaledAxisX)){
            return false;
        }else if(Separated(a.vertices, b.vertices, b.scaledAxisY)){
            return false;
        }else if(Separated(a.vertices, b.vertices, b.scaledAxisZ)){
            return false;
        }else if(Separated(a.vertices, b.vertices, a.scaledAxisX.cross(b.scaledAxisX))){
            return false;
        }else if(Separated(a.vertices, b.vertices, a.scaledAxisX.cross(b.scaledAxisY))){
            return false;
        }else if(Separated(a.vertices, b.vertices, a.scaledAxisX.cross(b.scaledAxisZ))){
            return false;
        }else if(Separated(a.vertices, b.vertices, a.scaledAxisY.cross(b.scaledAxisX))){
            return false;
        }else if(Separated(a.vertices, b.vertices, a.scaledAxisY.cross(b.scaledAxisY))){
            return false;
        }else if(Separated(a.vertices, b.vertices, a.scaledAxisY.cross(b.scaledAxisZ))){
            return false;
        }else if(Separated(a.vertices, b.vertices, a.scaledAxisZ.cross(b.scaledAxisX))){
            return false;
        }else if(Separated(a.vertices, b.vertices, a.scaledAxisZ.cross(b.scaledAxisY))){
            return false;
        }else{
            return !Separated(a.vertices, b.vertices, a.scaledAxisZ.cross(b.scaledAxisZ));
        }
    }

    private static boolean Separated(Vec3[] vertsA, Vec3[] vertsB, Vec3 axis){
        if(axis.equals(Vec3.ZERO)){
            return false;
        }else{
            double aMin = Double.POSITIVE_INFINITY;
            double aMax = Double.NEGATIVE_INFINITY;
            double bMin = Double.POSITIVE_INFINITY;
            double bMax = Double.NEGATIVE_INFINITY;

            for(int i = 0; i < 8; ++i){
                double aDist = vertsA[i].dot(axis);
                aMin = Math.min(aDist, aMin);
                aMax = Math.max(aDist, aMax);
                double bDist = vertsB[i].dot(axis);
                bMin = Math.min(bDist, bMin);
                bMax = Math.max(bDist, bMax);
            }

            double longSpan = Math.max(aMax, bMax) - Math.min(aMin, bMin);
            double sumSpan = aMax - aMin + bMax - bMin;
            return longSpan >= sumSpan;
        }
    }

    @Override
    public void forAllEdges(Shapes.@NotNull DoubleLineConsumer action){
        action.consume(vertex1.x, vertex1.y, vertex1.z, vertex2.x, vertex2.y, vertex2.z);
        action.consume(vertex2.x, vertex2.y, vertex2.z, vertex6.x, vertex6.y, vertex6.z);
        action.consume(vertex6.x, vertex6.y, vertex6.z, vertex5.x, vertex5.y, vertex5.z);
        action.consume(vertex5.x, vertex5.y, vertex5.z, vertex1.x, vertex1.y, vertex1.z);
        action.consume(vertex4.x, vertex4.y, vertex4.z, vertex3.x, vertex3.y, vertex3.z);
        action.consume(vertex3.x, vertex3.y, vertex3.z, vertex7.x, vertex7.y, vertex7.z);
        action.consume(vertex7.x, vertex7.y, vertex7.z, vertex8.x, vertex8.y, vertex8.z);
        action.consume(vertex8.x, vertex8.y, vertex8.z, vertex4.x, vertex4.y, vertex4.z);
        action.consume(vertex1.x, vertex1.y, vertex1.z, vertex4.x, vertex4.y, vertex4.z);
        action.consume(vertex2.x, vertex2.y, vertex2.z, vertex3.x, vertex3.y, vertex3.z);
        action.consume(vertex6.x, vertex6.y, vertex6.z, vertex7.x, vertex7.y, vertex7.z);
        action.consume(vertex5.x, vertex5.y, vertex5.z, vertex8.x, vertex8.y, vertex8.z);
    }

    public AABB getBorder(){
        if(border == null){
            updateVertex();
        }
        return border;
    }

    private Vec3[] getAxes(){
        return new Vec3[]{axisX, axisY, axisZ};
    }

    public OBB inflate(double x, double y, double z){
        extent = extent.add(x, y, z);
        return this;
    }

    public OBB inflate(double length){
        return inflate(length, length, length);
    }

    public static boolean isColliding(OBB obb1, OBB obb2, Vec3 motion1, Vec3 motion2){
        Vec3[] axes1 = obb1.getAxes();
        Vec3[] axes2 = obb2.getAxes();
//        Vec3 relativeVelocity = motion1.subtract(motion2);
        Vec3[] testAxes = new Vec3[15];
        int index = 0;
        for(Vec3 axis : axes1){
            testAxes[index++] = axis;
        }
        for(Vec3 axis : axes2){
            testAxes[index++] = axis;
        }
        for(Vec3 axis1 : axes1){
            for(Vec3 axis2 : axes2){
                testAxes[index++] = axis1.cross(axis2);
            }
        }
        for(Vec3 axis : testAxes){
            if(!overlapOnAxis(obb1, motion1, obb2, motion2, axis)){
                return false;
            }
        }
        return true;
    }


//    private static boolean overlapOnAxis(OBB obb1, OBB obb2, Vec3 axis, Vec3 relativeVelocity){
//        double[] range1 = getProjectionRange(obb1, axis);
//        double[] range2 = getProjectionRange(obb2, axis);
//
//        double velocityProjection = relativeVelocity.dot(axis);
//        if(velocityProjection < 0){
//            range1[0] += velocityProjection;
//        }else{
//            range1[1] += velocityProjection;
//        }
//
//        return range1[0] <= range2[1] && range1[1] >= range2[0];
//    }
//
//    private static double[] getProjectionRange(OBB obb, Vec3 axis){
//        double min = obb.vertices[0].dot(axis);
//        double max = min;
//
//        for(int i = 1; i < obb.vertices.TOTAL_LENGTH; i++){
//            double projection = obb.vertices[i].dot(axis);
//            if(projection < min){
//                min = projection;
//            }
//            if(projection > max){
//                max = projection;
//            }
//        }
//
//        return new double[]{min, max};
//    }

    private static boolean overlapOnAxis(OBB obb1, Vec3 velocity1, OBB obb2, Vec3 velocity2, Vec3 axis) {
        double[] range1 = getProjectionRange(obb1, velocity1, axis);
        double[] range2 = getProjectionRange(obb2, velocity2, axis);

        return range1[0] <= range2[1] && range1[1] >= range2[0];
    }

    private static double[] getProjectionRange(OBB obb, Vec3 velocity, Vec3 axis) {
        double min = obb.vertices[0].dot(axis);
        double max = min;

        for (int i = 1; i < obb.vertices.length; i++) {
            double projection = obb.vertices[i].dot(axis);
            if (projection < min) {
                min = projection;
            }
            if (projection > max) {
                max = projection;
            }
        }

        // 考虑速度对投影范围的影响
        double velocityProjection = velocity.dot(axis);
        if (velocityProjection < 0) {
            min += velocityProjection;
        } else {
            max += velocityProjection;
        }

        return new double[]{min, max};
    }
}
