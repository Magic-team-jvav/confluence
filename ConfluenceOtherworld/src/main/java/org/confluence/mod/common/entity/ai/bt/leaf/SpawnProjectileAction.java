package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/// 生成一个由具体生物配置好的实体弹幕。
///
/// 本节点只负责行为树的一次性执行语义和服务端入世，不假设弹幕种类、伤害、速度或命中特效。
/// 这些参数由实体自己的工厂方法保存，避免通用 AI 层逐渐堆积鸟妖、黄蜂、恶魔等具体玩法分支。
public final class SpawnProjectileAction extends BTNode {
    private final Mob shooter;
    private final Function<LivingEntity, @Nullable Projectile> projectileFactory;
    private boolean done;

    public SpawnProjectileAction(Mob shooter, Function<LivingEntity, @Nullable Projectile> projectileFactory) {
        this.shooter = Objects.requireNonNull(shooter, "shooter");
        this.projectileFactory = Objects.requireNonNull(projectileFactory, "projectileFactory");
    }

    @Override
    public void start() {
        done = false;
    }

    @Override
    public BTStatus execute() {
        if (done) {
            return BTStatus.SUCCESS;
        }
        LivingEntity target = shooter.getTarget();
        if (target == null || !target.isAlive()) {
            return BTStatus.FAILURE;
        }
        Projectile projectile = projectileFactory.apply(target);
        if (projectile == null || !shooter.level().addFreshEntity(projectile)) {
            if (projectile != null) projectile.discard();
            return BTStatus.FAILURE;
        }
        done = true;
        return BTStatus.SUCCESS;
    }
}
