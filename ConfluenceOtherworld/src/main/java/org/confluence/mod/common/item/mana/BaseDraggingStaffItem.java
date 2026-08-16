package org.confluence.mod.common.item.mana;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.BaseDraggingProjectile;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.function.Consumer;

/// 持续按住使用键拖拽弹幕、松手后才按冻结弹速射出的法杖基类。
public class BaseDraggingStaffItem<E extends BaseDraggingProjectile> extends ManaStaffItem<E> {
    public BaseDraggingStaffItem(Properties properties, ModRarity rarity, ProjectileFactory<E> factory,
                                 float damage, int manaCost, float rawVelocity, int cooldown) {
        super(properties, rarity, factory, damage, manaCost, rawVelocity, cooldown);
    }

    public BaseDraggingStaffItem(ModRarity rarity, ProjectileFactory<E> factory, float damage,
                                 int manaCost, float rawVelocity, int cooldown,
                                 Consumer<PortItemAttributeModifiers.Builder> consumer) {
        super(rarity, factory, damage, manaCost, rawVelocity, cooldown, consumer);
    }

    public BaseDraggingStaffItem(ModRarity rarity, ProjectileFactory<E> factory, float damage,
                                 int manaCost, float rawVelocity, int cooldown, double critChance) {
        super(rarity, factory, damage, manaCost, rawVelocity, cooldown, critChance);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        super.use(level, player, usedHand);
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72_000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    /// 保留原有水平前置生成位置，垂直拖拽由弹幕实体在后续 tick 接管。
    @Override
    protected Vec3 launchPosition(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            E projectile
    ) {
        Vec3 horizontalView = context.player().calculateViewVector(0.0F, context.yaw());
        return new Vec3(
                context.player().getX() + horizontalView.x,
                context.player().getEyeY() - 0.1,
                context.player().getZ() + horizontalView.z);
    }

    /// 拖拽阶段必须静止；实体在玩家停止使用后自行恢复冻结弹速。
    @Override
    protected float velocityMultiplier(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            E projectile
    ) {
        return velocityMultiplier(0.0F);
    }

    /// 保留可搜索的显式零倍率声明，避免后续重构误把拖拽弹幕立即射出。
    private static float velocityMultiplier(float value) {
        return value;
    }

    /// 冷却由弹幕检测到松手时提交，而不是在拖拽开始时提交。
    @Override
    protected int resolveCooldown(ProjectileFireContext context) {
        return 0;
    }
}
