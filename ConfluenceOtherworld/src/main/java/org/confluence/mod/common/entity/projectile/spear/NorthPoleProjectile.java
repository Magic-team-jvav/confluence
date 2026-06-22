package org.confluence.mod.common.entity.projectile.spear;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.common.init.entity.ModEntities;

/**
 * <h1>北极矛弹射物</h1>
 * 直线弹射物，有重力，不可穿墙，间歇生成子弹射物。
 */
public class NorthPoleProjectile extends SpearProjectile {
    /**
     * 子弹射物生成间隔（tick）
     */
    private static final int SUB_SPAWN_INTERVAL = 5;

    /**
     * 模型层定义（暂用风暴长矛模型）
     */
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Confluence.asResource("north_pole_projectile"), "main");

    /**
     * 模型网格定义（暂用风暴长矛模型网格）
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create()
                .texOffs(12, 10).addBox(-1.0F, -1.0F, -8.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 16).addBox(-1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.ZERO);
        bone.addOrReplaceChild("cube_ml_r1", CubeListBuilder.create()
                        .texOffs(0, 10).addBox(-1.0F, 2.0659F, -0.5303F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                        .texOffs(12, 16).addBox(-1.0F, -4.1213F, -3.5355F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        bone.addOrReplaceChild("cube_mm_r1", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.0F, -0.5858F, -4.0F, 2.0F, 2.0F, 8.0F, new CubeDeformation(-0.01F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    private int subSpawnTimer = 0;

    public NorthPoleProjectile(EntityType<? extends NorthPoleProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void updateMotion() {
        if (projComponent != null) {
            velocity = velocity.scale(projComponent.acceleration());
        }
    }

    @Override
    protected Vec3 initVelocity(LivingEntity owner, Vec3 direction, float speed) {
        return direction.scale(1.0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            subSpawnTimer++;
            if (subSpawnTimer >= SUB_SPAWN_INTERVAL) {
                subSpawnTimer = 0;
                spawnSubProjectile();
            }
        }
    }

    private void spawnSubProjectile() {
        NorthPoleSubProjectile sub = new NorthPoleSubProjectile(
                ModEntities.NORTH_POLE_SUB.get(), level());
        sub.setOwner(getOwner());
        sub.setWeapon(getWeaponItem());
        if (projComponent != null) {
            sub.setProjComponent(projComponent, getOwner() instanceof LivingEntity living ? living : null);
        }
        sub.setPos(getX(), getY(), getZ());
        // 子射物初始速度为零,仅受重力下落
        sub.velocity = new Vec3(0, 0, 0);
        sub.setDeltaMovement(Vec3.ZERO);
        level().addFreshEntity(sub);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    // ===== 渲染元数据 =====

    @Override
    public net.minecraft.resources.ResourceLocation getProjTexture() {
        return Confluence.asResource("textures/entity/storm_spear_shot_projectile.png");
    }

    @Override
    public ModelLayerLocation getModelLayer() {
        return LAYER_LOCATION;
    }

    @Override
    @org.jetbrains.annotations.Nullable
    protected net.minecraft.core.particles.ParticleOptions getTrailParticle() {
        return null;
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticleTypes.SNOW.get(),
                    getX(), getY(), getZ(), 10, 0.5, 0.5, 0.5, 0.05);
        }
    }
}
