package org.confluence.mod.common.entity.npc.ai;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.DryadNPC;
import org.confluence.mod.common.entity.projectile.*;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.mana.BallOfFireProjectile;
import org.confluence.mod.common.entity.projectile.mana.NPCShadowflameSkullProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.BoomerangItems;
import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.common.init.item.MaterialItems;

import java.util.function.Supplier;

/// 可由 NPC 注册项组合使用的独立自卫动作。
public final class NPCCombatActions {
    /// 向导使用的箭矢攻击，困难模式自动点燃箭矢。
    public static final NPCCombatProfile.Attack ARROW = (npc, target, values) -> shootArrow(npc, target, values, isHardmode());
    /// 军火商在困难模式切换迷你鲨后使用较低的单发伤害。
    public static final NPCCombatProfile.Attack ARMS_DEALER = NPCCombatActions::armsDealerAttack;
    /// 旅商根据世界阶段切换枪弹和弓矢的攻击。
    public static final NPCCombatProfile.Attack TRAVELING_MERCHANT = NPCCombatActions::travelingMerchantAttack;
    /// 哥布林工匠使用的尖球攻击。
    public static final NPCCombatProfile.Attack SPIKY_BALL = NPCCombatActions::spikyBall;
    /// 巫师使用的弹跳火球攻击。
    public static final NPCCombatProfile.Attack FIREBALL = NPCCombatActions::fireball;
    /// 衣商使用的追踪暗影焰骷髅攻击。
    public static final NPCCombatProfile.Attack SHADOWFLAME_SKULL = NPCCombatActions::shadowflameSkull;
    /// 机械师使用的战斗扳手攻击。
    public static final NPCCombatProfile.Attack COMBAT_WRENCH = NPCCombatActions::combatWrench;
    /// 使用 Attribute 伤害的近战攻击。
    public static final NPCCombatProfile.Attack MELEE = NPCCombatActions::melee;
    /// 树妖开始一次持续扩张的守护结界。
    public static final NPCCombatProfile.Attack DRYAD_WARD = NPCCombatActions::dryadWard;
    /// 松露人从周围发射孢子的特殊行为。
    public static final NPCCombatProfile.Attack TRUFFLE_SPORES = NPCCombatActions::truffleSpores;

    private NPCCombatActions() {}

    /// 创建使用指定物品外观和命中策略的虚拟投掷攻击。
    public static NPCCombatProfile.Attack thrown(Supplier<ItemStack> item, ResourceLocation effect) {
        return (npc, target, values) -> {
            NPCWeaponProjectile projectile = new NPCWeaponProjectile(npc, item.get(), values.damage(), effect);
            shootAt(projectile, npc, target, values.projectileSpeed(), 1);
        };
    }

    /// 返回当前世界进度是否已经进入困难模式，供注册项选择阶段武器。
    public static boolean isHardmode() {
        return KillBoard.INSTANCE.getGamePhase().isHardmode();
    }

    /// 发射使用公共泰拉箭实体的箭矢；向导在困难模式下可将其点燃。
    private static void shootArrow(BaseNPC npc, LivingEntity target, NPCCombatProfile.Values values, boolean flaming) {
        AbstractArrow arrow = new BaseArrowEntity(ModEntities.BASE_ARROW.get(), npc, new ItemStack(Items.ARROW), npc.getMainHandItem());
        arrow.setBaseDamage(values.damage() / values.projectileSpeed());
        if (flaming) arrow.setSecondsOnFire(5);
        shootAt(arrow, npc, target, values.projectileSpeed(), 1);
        npc.playSound(SoundEvents.ARROW_SHOOT, 1, 1);
    }

    /// 发射不消耗物品的基础枪弹，并沿用枪弹轨迹、伤害类型和客户端表现。
    private static void shootBullet(BaseNPC npc, LivingEntity target, NPCCombatProfile.Values values) {
        shootBullet(npc, target, values.damage(), values.projectileSpeed());
    }

    /// 军火商困难模式前造成 24 基础伤害，困难模式后切换为 15 基础伤害。
    private static void armsDealerAttack(BaseNPC npc, LivingEntity target, NPCCombatProfile.Values values) {
        float damage = isHardmode() ? values.damage() * 15.0F / 24.0F : values.damage();
        shootBullet(npc, target, damage, values.projectileSpeed());
    }

    /// 使用给定伤害和速度创建一枚不消耗物品的基础枪弹。
    private static void shootBullet(BaseNPC npc, LivingEntity target, float damage, double speed) {
        BaseBulletEntity bullet = new BaseBulletEntity(npc, GunItems.MUSKET_BULLET.toStack());
        bullet.setDamage(damage);
        bullet.setPenetrate(0);
        Vec3 velocity = target.getEyePosition().subtract(bullet.position()).normalize().scale(speed);
        bullet.setInitialVelocity(velocity);
        npc.level().addFreshEntity(bullet);
        npc.playSound(SoundEvents.CROSSBOW_SHOOT, 1, 1);
    }

    /// 旅商在困难模式前使用枪弹，进入困难模式后切换为不燃烧的弓矢攻击。
    private static void travelingMerchantAttack(BaseNPC npc, LivingEntity target, NPCCombatProfile.Values values) {
        if (isHardmode()) shootArrow(npc, target, values, false);
        else shootBullet(npc, target, values.damage(), values.projectileSpeed());
    }

    /// 发射可反弹并使用静态无敌帧的尖球实体。
    private static void spikyBall(BaseNPC npc, LivingEntity target, NPCCombatProfile.Values values) {
        SpikyBallProjectile projectile = new SpikyBallProjectile(npc);
        projectile.setDamage(values.damage());
        shootAt(projectile, npc, target, values.projectileSpeed(), 0.5F);
    }

    /// 发射沿用花之火弹跳和燃烧规则的火球。
    private static void fireball(BaseNPC npc, LivingEntity target, NPCCombatProfile.Values values) {
        BallOfFireProjectile projectile = new BallOfFireProjectile(npc);
        projectile.setOwner(npc);
        projectile.setPos(npc.getX(), npc.getEyeY() - 0.1, npc.getZ());
        projectile.setDamage(values.damage());
        shootAt(projectile, npc, target, values.projectileSpeed(), 1);
    }

    /// 发射会追踪目标并施加暗影焰的衣商专用骷髅。
    private static void shadowflameSkull(BaseNPC npc, LivingEntity target, NPCCombatProfile.Values values) {
        NPCShadowflameSkullProjectile projectile = new NPCShadowflameSkullProjectile(
                ModEntities.NPC_SHADOWFLAME_SKULL.get(), npc.level());
        projectile.setOwner(npc);
        projectile.setTarget(target);
        projectile.setPos(npc.getX(), npc.getEyeY() - 0.1, npc.getZ());
        projectile.setDamage(values.damage());
        projectile.shoot(target.getX() - npc.getX(), target.getEyeY() - npc.getEyeY(), target.getZ() - npc.getZ(),
                (float) values.projectileSpeed(), 1);
        npc.level().addFreshEntity(projectile);
    }

    /// 以注册项配置的数值发射机械师战斗扳手，并在到期或命中后返回。
    private static void combatWrench(BaseNPC npc, LivingEntity target, NPCCombatProfile.Values values) {
        BoomerangProjectile projectile = new BoomerangProjectile(ModEntities.BOOMERANG_PROJECTILE.get(), npc.level());
        projectile.configure(npc, BoomerangItems.COMBAT_WRENCH.toStack(), values.damage(),
                (float) values.projectileSpeed(), 1.5F, 12, 1);
        projectile.shoot(target.getX() - npc.getX(), target.getEyeY() - npc.getEyeY(), target.getZ() - npc.getZ(),
                (float) values.projectileSpeed(), 1);
        npc.level().addFreshEntity(projectile);
    }

    /// 执行不叠加手持物品属性的固定伤害近战，避免固有武器重复计算伤害。
    private static void melee(BaseNPC npc, LivingEntity target, NPCCombatProfile.Values values) {
        if (target.hurt(npc.damageSources().mobAttack(npc), values.damage())) {
            target.knockback(0.3, npc.getX() - target.getX(), npc.getZ() - target.getZ());
        }
        npc.swing(InteractionHand.MAIN_HAND);
    }

    /// 启动树妖实体维护的持续结界；普通 NPC 不会误用这项专属动作。
    private static void dryadWard(BaseNPC npc, LivingEntity ignored, NPCCombatProfile.Values values) {
        if (npc instanceof DryadNPC dryad) dryad.startWard();
    }

    /// 从松露人周围三个方向生成孢子外观弹体并汇聚攻击目标。
    private static void truffleSpores(BaseNPC npc, LivingEntity target, NPCCombatProfile.Values values) {
        for (int index = 0; index < 3; index++) {
            double angle = Math.PI * 2 * index / 3;
            NPCWeaponProjectile projectile = new NPCWeaponProjectile(npc, MaterialItems.GLOWING_MUSHROOM.toStack(),
                    values.damage(), NPCProjectileEffects.NONE);
            projectile.setPos(npc.getX() + Math.cos(angle), npc.getEyeY(), npc.getZ() + Math.sin(angle));
            shootAt(projectile, npc, target, values.projectileSpeed(), 1);
        }
    }

    /// 统一计算 NPC 弹体的抛射修正并加入世界，避免各动作重复生成逻辑。
    private static void shootAt(Projectile projectile, BaseNPC npc, LivingEntity target, double speed, float inaccuracy) {
        double dx = target.getX() - projectile.getX();
        double dy = target.getY(0.5) - projectile.getY();
        double dz = target.getZ() - projectile.getZ();
        projectile.shoot(dx, dy + Math.sqrt(dx * dx + dz * dz) * 0.1, dz, (float) speed, inaccuracy);
        npc.level().addFreshEntity(projectile);
    }
}
