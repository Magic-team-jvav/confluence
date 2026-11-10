package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.DestroyerLaserProjectile;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.HashMap;
import java.util.Map;

/// 一阶段沿用原激光，二阶段使用绑定射弹的三层粒子。
public final class DestroyerLaserRenderer extends EntityRenderer<DestroyerLaserProjectile> {
    private static final String[] EFFECTS = {"destroyer_attack_small_laser", "destroyer_attack_small_laser_1", "destroyer_attack_small_laser_round"};
    private final LaserProjectileRenderer<DestroyerLaserProjectile> original;
    private final Map<Integer, ParticleEmitter[]> emitters = new HashMap<>();

    public DestroyerLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
        original = new LaserProjectileRenderer<>(context, 0xFFFFFF, 0xB5FFF0, 0x33E2DF);
    }

    @Override
    public boolean shouldRender(DestroyerLaserProjectile laser, Frustum frustum, double x, double y, double z) {
        return laser.isSmallLaser() ? super.shouldRender(laser, frustum, x, y, z) : original.shouldRender(laser, frustum, x, y, z);
    }

    @Override
    public void render(DestroyerLaserProjectile laser, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        emitters.values().removeIf(group -> group[0].isRemoved());
        if (!laser.isSmallLaser()) {
            original.render(laser, yaw, partialTick, pose, buffers, light);
            return;
        }
        Vec3 direction = laser.getDeltaMovement().normalize();
        if (direction.lengthSqr() < 1.0E-8) return;
        ParticleEmitter[] group = emitters.computeIfAbsent(laser.getId(), ignored -> createEmitters(laser));
        Quaternionf rotation = new Quaternionf().rotationTo(new Vector3f(0, 1, 0), direction.toVector3f());
        Matrix4x3f space = new Matrix4x3f().rotate(rotation);
        for (ParticleEmitter emitter : group) emitter.setLocalSpace(space, false);
    }

    private static ParticleEmitter[] createEmitters(DestroyerLaserProjectile laser) {
        ParticleEmitter[] result = new ParticleEmitter[EFFECTS.length];
        for (int i = 0; i < EFFECTS.length; i++) {
            ParticleEmitter emitter = new ParticleEmitter(laser.level(), laser.position(), Confluence.asResource(EFFECTS[i]));
            emitter.attachEntity(laser);
            // ParticleStorm 的形状方向不会随局部矩阵旋转，尾迹使用世界空间的反向速度。
            emitter.getVars().setValue("variable.laser_dx", instance -> (float) -laser.getDeltaMovement().normalize().x);
            emitter.getVars().setValue("variable.laser_dy", instance -> (float) -laser.getDeltaMovement().normalize().y);
            emitter.getVars().setValue("variable.laser_dz", instance -> (float) -laser.getDeltaMovement().normalize().z);
            for (int axis = 0; axis < 3; axis++) {
                for (int component = 0; component < 3; component++) {
                    int a = axis, c = component;
                    emitter.getVars().setValue("variable.laser_basis_" + axis + component, instance -> {
                        Vector3f direction = laser.getDeltaMovement().normalize().toVector3f();
                        Vector3f basis = new Vector3f(axisVector(a));
                        new Quaternionf().rotationTo(new Vector3f(0, 1, 0), direction).transform(basis);
                        return c == 0 ? basis.x : c == 1 ? basis.y : basis.z;
                    });
                }
            }
            MolangParticleEngine.INSTANCE.addEmitter(emitter);
            result[i] = emitter;
        }
        return result;
    }

    private static Vector3f axisVector(int axis) {
        return axis == 0 ? new Vector3f(1, 0, 0) : axis == 1 ? new Vector3f(0, 1, 0) : new Vector3f(0, 0, 1);
    }

    @Override
    public ResourceLocation getTextureLocation(DestroyerLaserProjectile laser) {
        return original.getTextureLocation(laser);
    }
}
