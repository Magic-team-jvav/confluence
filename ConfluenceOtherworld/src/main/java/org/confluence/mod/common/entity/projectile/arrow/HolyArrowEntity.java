package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.mana.HomingFallingStarProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.jetbrains.annotations.Nullable;

/// 圣箭：击中方块或敌怪时，从天而降两颗神圣之星。
///
/// 神圣之星直接复用星怒 / 星泣的坠星（`HomingFallingStarProjectile`），
/// 参数与它们完全一致：目标点上方 20 格生成、初速 2.0、逐刻 +0.04 重力、命中后立刻消失。
/// 与它们唯一的区别是：不追踪、伤害由圣箭给（箭矢基础伤害的 1/3）、并且换用神圣之星贴图。
/// 第一颗立刻生成；它被摧毁（命中方块或 1 个敌怪）后，在摧毁点生成第二颗。
///
/// 落点参考代达罗斯风暴弓的索敌：取「实际命中的部位」的包围盒中心，而不是主体包围盒；
/// 多部分生物（如蠕虫类 Boss）的主体包围盒常常只覆盖头部，按主体取点会导致每颗星都砸在头上。
public class HolyArrowEntity extends BaseArrowEntity {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/arrow/holy_arrow.png");
    /// 神圣之星伤害占箭矢基础伤害的比例。
    private static final float STAR_DAMAGE_RATIO = 1.0F / 3.0F;
    /// 本次命中实际打到的部位中心（与代达罗斯风暴弓 `angleHitTarget` 的取点方式一致）。
    private @Nullable Vec3 hitPartCenter;

    public HolyArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        addMissCallback(() -> spawnStars(position()));
    }

    public HolyArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
        addMissCallback(() -> spawnStars(position()));
    }

    @Override
    protected double getAdditionalKnockback() {
        return 1.0;
    }

    /// 箭矢基础伤害 = 武器给出的基础伤害 + 圣箭自身附加伤害。
    private float arrowBaseDamage() {
        float base = (float) getBaseDamage();
        if (getPickupItem().getItem() instanceof BaseTerraArrowItem arrowItem) {
            base += arrowItem.getAdditionalDamage();
        }
        return base;
    }

    /// 参考代达罗斯风暴弓：落点取实际命中部位的中心，避免多部分生物只按主体包围盒（头部）取点。
    @Override
    protected void onHitEntity(EntityHitResult result) {
        this.hitPartCenter = result.getEntity().getBoundingBox().getCenter();
        try {
            super.onHitEntity(result);
        } finally {
            this.hitPartCenter = null;
        }
    }

    @Override
    protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {
        spawnStars(hitPartCenter != null ? hitPartCenter : target.getBoundingBox().getCenter());
    }

    /// 生成第一颗神圣之星，并把它被摧毁的位置作为第二颗的落点。
    private void spawnStars(Vec3 at) {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        if (!(getOwner() instanceof Player owner)) return;
        spawnStar(serverLevel, owner, at, arrowBaseDamage() * STAR_DAMAGE_RATIO, true);
    }

    private void spawnStar(ServerLevel level, Player owner, Vec3 at, float damage, boolean chain) {
        HomingFallingStarProjectile star = HomingFallingStarProjectile.spawnAbove(
                ModEntities.HALLOW_STAR.get(), level, owner, at.x, at.y, at.z);
        star.setHoming(false);
        /// 箭矢命中留下的受击无敌帧会把坠星伤害吞掉，这里与星泣一样命中前清零。
        star.setIgnoreInvulnerability(true);
        star.setDamage(damage);
        if (chain) {
            star.setOnDestroyed(destroyedAt -> spawnStar(level, owner, destroyedAt, damage, false));
        }
        level.addFreshEntity(star);
    }

    @Override
    public ResourceLocation getTexturePath() {
        return TEXTURE;
    }
}
