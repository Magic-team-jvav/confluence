package org.confluence.mod.common.entity.projectile.spear;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

/**
 * <h1>恶魂弹射物</h1>
 * 从锁定目标周围圆形区域（半径5）生成，沿水平方向飞向目标。
 * 可无限穿透、穿墙，每 4 tick 对同一实体最多造成一次伤害。
 */
public class GhastlyProjectile extends SpearProjectile {
    private static final int DAMAGE_INTERVAL = 4;
    private final Object2IntMap<Entity> lastHitTicks = new Object2IntOpenHashMap<>();
    private boolean hasPassedTarget = false;

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Confluence.asResource("ghastly_projectile"), "main");

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public GhastlyProjectile(EntityType<? extends GhastlyProjectile> entityType, Level level) {
        super(entityType, level);
        this.knockBack = 0.0f;
        this.baseKnockBack = 0.0f;
        this.config = new Config()
                    .damageFactor(0.9f)
                    .baseSpeed(0.5f)
                    .existTicks(10)
                    .pierceCount(Integer.MAX_VALUE);
    }


    /** 设置锁定目标 */
    public void setLockedTarget(LivingEntity target) {
        this.target = target;
    }

    @Override
    protected void updateMotion() {
        if (!hasPassedTarget && target != null && target.isAlive()) {
            Vec3 toTarget = target.position().subtract(position());
            double horizontalDistSqr = toTarget.x * toTarget.x + toTarget.z * toTarget.z;
            if (horizontalDistSqr > 0.25) {
                float speed = getBaseSpeed();
                Vec3 horizontalDir = new Vec3(toTarget.x, 0.0, toTarget.z).normalize();
                velocity = horizontalDir.scale(speed);
            }
            // 接近目标（≤1 格）时标记已穿透，之保持原方向不再索敌
            if (horizontalDistSqr <= 1.0) {
                hasPassedTarget = true;
            }
        }
    }

    @Override
    protected Vec3 initVelocity(LivingEntity owner, Vec3 direction, float speed) {
        return direction.scale(speed);
    }

    // ===== 穿透与穿墙 =====
    @Override
    protected void applyPenetration() {
    }
    @Override
    protected void onHitBlock(BlockHitResult result) {
    }

    // ===== 伤害与碰撞 =====
    @Override
    protected boolean canHitEntity(Entity target) {
        return target.isAlive()
                && target != getOwner()
                && target.isPickable()
                && LibUtils.canHitEntity(this, target);
    }

    /**
     * 检查伤害冷却后调用父类伤害逻辑。
     * 同一实体每 {@link #DAMAGE_INTERVAL} tick 最多受伤一次。
     */
    @Override
    protected boolean doHurt(Entity target) {
        int currentTick = tickCount;
        int lastTick = lastHitTicks.getOrDefault(target, -DAMAGE_INTERVAL);
        if (currentTick - lastTick < DAMAGE_INTERVAL) {
            return false;
        }
        lastHitTicks.put(target, currentTick);
        return super.doHurt(target);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            lastHitTicks.keySet().removeIf(e -> !e.isAlive() || e.isRemoved());
        }
    }

    /** 免疫所有外部伤害 */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    // ===== 渲染元数据 =====

    @Override
    public net.minecraft.resources.ResourceLocation getProjTexture() {
        return Confluence.asResource("textures/entity/ghastly_projectile.png");
    }

    @Override
    public ModelLayerLocation getModelLayer() {
        return LAYER_LOCATION;
    }

    @Override
    @Nullable
    protected net.minecraft.core.particles.ParticleOptions getTrailParticle() {
        return null;
    }
}
