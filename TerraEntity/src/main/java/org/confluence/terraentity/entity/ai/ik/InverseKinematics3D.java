package org.confluence.terraentity.entity.ai.ik;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.*;

public class InverseKinematics3D {
    // 关节类定义
    public static class Joint {
        Vector3D axis;      // 旋转轴（单位向量）
        double length;      // 骨长
        public double angle;       // 当前角度（弧度）
        double minAngle;    // 最小角度限制
        double maxAngle;    // 最大角度限制

        public Joint(Vector3D axis, double length, double min, double max) {
            this.axis = axis.normalize();
            this.length = length;
            this.minAngle = min;
            this.maxAngle = max;
        }
    }

    private final Joint[] joints;  // 关节数组
    private static final int MAX_ITER = 1000;  // 最大迭代次数
    private static final double TOLERANCE = 1e-4;  // 收敛阈值

    public InverseKinematics3D(Joint[] joints) {
        this.joints = joints;
    }

    // 前向运动学计算末端位置
    private Vector3D forwardKinematics() {
        Vector3D position = Vector3D.ZERO;
        Rotation rotation = Rotation.IDENTITY;
        
        for (Joint joint : joints) {
            // 应用当前关节旋转
            Rotation jointRot = new Rotation(joint.axis, joint.angle, RotationConvention.VECTOR_OPERATOR);
            rotation = rotation.applyTo(jointRot);
            
            // 沿当前方向移动骨长
            Vector3D offset = rotation.applyTo(Vector3D.PLUS_K).scalarMultiply(joint.length);
            position = position.add(offset);
        }
        return position;
    }

    // 计算雅可比矩阵
    private RealMatrix computeJacobian() {
        final int dof = joints.length;
        RealMatrix jacobian = MatrixUtils.createRealMatrix(3, dof);
        Vector3D endPos = forwardKinematics();
        
        // 数值法近似雅可比
        final double delta = 1e-6;
        for (int i = 0; i < dof; i++) {
            // 扰动当前关节角度
            double originalAngle = joints[i].angle;
            joints[i].angle += delta;
            
            Vector3D newPos = forwardKinematics();
            Vector3D derivative = newPos.subtract(endPos).scalarMultiply(1/delta);
            
            // 恢复原始角度
            joints[i].angle = originalAngle;
            
            // 填充雅可比列
            jacobian.setColumn(i, new double[]{
                derivative.getX(), derivative.getY(), derivative.getZ()
            });
        }
        return jacobian;
    }

    // 核心求解方法
    public void solve(Vector3D target) {
        for (int iter = 0; iter < MAX_ITER; iter++) {
            Vector3D current = forwardKinematics();
            Vector3D error = target.subtract(current);
            
            // 检查收敛条件
            if (error.getNorm() < TOLERANCE) break;
            
            RealMatrix jacobian = computeJacobian();
            
            // 使用伪逆计算角度增量 (Δθ = J⁺ * Δx)
            SingularValueDecomposition svd = new SingularValueDecomposition(jacobian);
            RealMatrix jacobianPseudoInverse = svd.getSolver().getInverse();
            
            RealVector deltaTheta = jacobianPseudoInverse.operate(
                new ArrayRealVector(new double[]{error.getX(), error.getY(), error.getZ()})
            );
            
            // 应用角度更新并限制范围
            for (int i = 0; i < joints.length; i++) {
                joints[i].angle += deltaTheta.getEntry(i);
                // 应用角度限制
                joints[i].angle = Math.max(joints[i].minAngle, 
                    Math.min(joints[i].maxAngle, joints[i].angle));
            }
        }
    }

//    public static void main(String[] args) {
//        // 示例：3关节机械臂
//        Joint[] joints = {
//            new Joint(Vector3D.PLUS_K, 1.0, -Math.PI/2, Math.PI/2),  // 基座关节
//            new Joint(Vector3D.PLUS_J, 1.0, -Math.PI/3, Math.PI/3),  // 肘关节
//            new Joint(Vector3D.PLUS_I, 0.5, -Math.PI/4, Math.PI/4)   // 腕关节
//        };
//
//        InverseKinematics3D ikSolver = new InverseKinematics3D(joints);
//        Vector3D target = new Vector3D(1.5, 0.5, 0.0);  // 目标位置
//
//        ikSolver.solve(target);
//
//        // 输出结果
//        System.out.println("Final angles:");
//        for (int i = 0; i < joints.length; i++) {
//            System.out.printf("Joint %d: %.2f rad\n", i, joints[i].angle);
//        }
//        System.out.println("End position: " + ikSolver.forwardKinematics());
//    }
}