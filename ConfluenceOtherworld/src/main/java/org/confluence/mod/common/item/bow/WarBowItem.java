package org.confluence.mod.common.item.bow;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.item.ModItems;

/// 战弓：左键横扫近战、右键拉弓远程都能用。
///
/// 近战命中时有 50% 概率自动朝最近的敌人补上一箭。
public class WarBowItem extends BaseTerraBowItem {
    /// 自动补箭的搜索半径。
    private static final double AUTO_AIM_RANGE = 24.0;
    /// 触发自动补箭的概率。
    private static final float AUTO_ARROW_CHANCE = 0.5F;
    /// 自动补箭的箭速。
    private static final float AUTO_ARROW_VELOCITY = 2.8F;

    public WarBowItem(float baseDamage, int durability) {
        super(baseDamage, new Item.Properties()
                .durability(durability)
                .attributes(ModItems.createAttributes(ModTiers.IRON, 4.0F, -2.4F, ModItems.attributes(0.0, 0.0))));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (attacker.level().isClientSide || !(attacker instanceof ServerPlayer player)) return result;
        if (player.getRandom().nextFloat() >= AUTO_ARROW_CHANCE) return result;
        shootAtNearest(player, stack);
        return result;
    }

    /// 自动朝最近的敌人发射一支普通箭。
    private void shootAtNearest(ServerPlayer player, ItemStack weapon) {
        Level level = player.level();
        LivingEntity nearest = null;
        double best = AUTO_AIM_RANGE * AUTO_AIM_RANGE;
        for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, new AABB(player.blockPosition()).inflate(AUTO_AIM_RANGE))) {
            if (candidate == player || !candidate.isAlive() || candidate.isSpectator()) continue;
            if (!LibEntityUtils.canHit(player, candidate)) continue;
            double distance = candidate.distanceToSqr(player);
            if (distance < best) {
                best = distance;
                nearest = candidate;
            }
        }
        if (nearest == null) return;
        if (!(Items.ARROW instanceof ArrowItem arrowItem)) return;
        AbstractArrow arrow = arrowItem.createArrow(level, Items.ARROW.getDefaultInstance(), player, weapon);
        arrow.setBaseDamage(getFullDrawDamage() * 0.5F);
        arrow.setCritArrow(true);
        Vec3 direction = nearest.getEyePosition().subtract(player.getEyePosition()).normalize();
        arrow.shoot(direction.x, direction.y, direction.z, AUTO_ARROW_VELOCITY, 0.6F);
        level.addFreshEntity(arrow);
    }
}
