package org.confluence.mod.common.item.whip;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import org.confluence.lib.api.projectile.ProjectileCost;
import org.confluence.lib.api.projectile.ProjectileDamageChannel;
import org.confluence.lib.api.projectile.ProjectileFireAction;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.api.projectile.ProjectileFireTrigger;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.lib.api.projectile.ProjectileWeaponAction;
import org.confluence.lib.api.projectile.ServerProjectileFireService;
import org.confluence.mod.api.whip.WhipAppearance;
import org.confluence.mod.api.whip.WhipDefinition;
import org.confluence.mod.common.entity.projectile.whip.WhipAttackEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 鞭子物品的公共实现。
 *
 * <p>普通鞭子只需要提供 {@link WhipDefinition}。左右键配置、服务端去重、冷却、耐久、召唤伤害与暴击
 * 快照以及攻击实体生成均由此类统一处理；复杂鞭子可以通过定义中的两类效果接口扩展，不需要复制整套
 * 发射和碰撞代码。</p>
 */
public class BaseWhipItem extends Item implements ProjectileWeaponAction {
    private final WhipDefinition definition;
    private final WhipAppearance appearance;

    public BaseWhipItem(
            Properties properties,
            WhipDefinition definition,
            WhipAppearance appearance
    ) {
        super(properties);
        this.definition = Objects.requireNonNull(definition, "Whip definition must not be null");
        this.appearance = Objects.requireNonNull(
                appearance, "Whip appearance must not be null");
    }

    public WhipDefinition definition() {
        return definition;
    }

    public WhipAppearance appearance() {
        return appearance;
    }

    /**
     * 右键模式通过原版物品入口提交动作；左键模式由统一客户端输入包提交。
     * 客户端配置层会阻止未选中的按键进入这里，因此服务端只需验证有限触发类型。
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            ServerProjectileFireService.fire(serverPlayer, hand, ProjectileFireTrigger.USE_PRESSED);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public @Nullable ProjectileFireAction createProjectileFireAction(ProjectileFireContext context) {
        Objects.requireNonNull(context, "Projectile fire context must not be null");
        if (context.trigger() != ProjectileFireTrigger.ATTACK_PRESSED
                && context.trigger() != ProjectileFireTrigger.USE_PRESSED) {
            return null;
        }

        AtomicReference<WhipAttackEntity> created = new AtomicReference<>();
        int durationTicks = resolveDurationTicks(context.player());
        return ProjectileFireAction.builder(
                        ProjectileDamageChannel.SUMMON,
                        ProjectileCost.none(),
                        (fireContext, snapshot) -> {
                            WhipAttackEntity attack = new WhipAttackEntity(
                                    ModEntities.WHIP_ATTACK.get(),
                                    fireContext.level()
                            );
                            ServerPlayer player = fireContext.player();
                            HumanoidArm arm = fireContext.hand() == InteractionHand.MAIN_HAND
                                    ? player.getMainArm()
                                    : player.getMainArm().getOpposite();
                            attack.initialize(
                                    fireContext.weapon(),
                                    fireContext.viewVector(),
                                    arm,
                                    durationTicks);
                            created.set(attack);
                            return List.of(new ProjectileLaunch(
                                    attack,
                                    player.position()
                                            .add(0.0, player.getBbHeight() * 0.5F, 0.0)
                                            .add(playerHandOffset(player, arm)),
                                    fireContext.viewVector(),
                                    0.0F
                            ));
                        }
                )
                .baseDamage(definition.baseDamage())
                .baseVelocity(0.05F)
                .baseKnockback(0.0F)
                .triggers(context.trigger())
                .cooldownTicks(durationTicks)
                .successAction(fireContext -> finishSuccessfulAttack(fireContext, created.get()))
                .build();
    }

    /**
     * 按 1.21 的公式把玩家当前攻击速度换算为一次完整挥鞭所需的 tick 数。
     * 物品本身、词缀、盔甲和状态效果对攻击速度的修改都会在读取属性时自然合并。
     */
    public static int resolveDurationTicks(Player player) {
        Objects.requireNonNull(player, "Whip player must not be null");
        double attackSpeed = player.getAttributeValue(Attributes.ATTACK_SPEED);
        if (!Double.isFinite(attackSpeed) || attackSpeed <= 0.0) {
            throw new IllegalStateException("Whip attack speed must be finite and positive");
        }
        return Math.max(1, (int) (80.0 / attackSpeed));
    }

    /**
     * 计算本次挥鞭生成时的手部锚点。
     *
     * <p>这个点同时决定服务端命中曲线根部和客户端后续吸附根部的初始侧向，因此必须和渲染器使用同一套左右手约定。
     * 对右手来说，面向 +Z 时锚点应落在玩家视觉右侧，避免第三人称看到鞭子从左手侧甩出。</p>
     */
    private static Vec3 playerHandOffset(Player player, HumanoidArm arm) {
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        float yaw = player.yBodyRot * Mth.DEG_TO_RAD + 1.0F;
        double sin = Mth.sin(yaw);
        double cos = Mth.cos(yaw);
        float scale = player.getScale();
        double sideOffset = side * 0.25 * scale;
        double forwardOffset = 0.8 * scale;
        return new Vec3(
                -cos * sideOffset - sin * forwardOffset,
                0.0,
                -sin * sideOffset + cos * forwardOffset
        );
    }

    private void finishSuccessfulAttack(
            ProjectileFireContext context,
            @Nullable WhipAttackEntity attack
    ) {
        if (attack == null) {
            throw new IllegalStateException("Whip transaction completed without an attack entity");
        }
        ServerPlayer player = context.player();
        player.awardStat(Stats.ITEM_USED.get(this));
    }
}
