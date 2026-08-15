package org.confluence.mod.common.item.gun;

import net.minecraft.world.damagesource.DamageSource;
import org.confluence.lib.api.projectile.ProjectileDamageChannel;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.item.mana.ManaProjectileCost;

/**
 * 使用魔力而非实体弹药的枪械基类。
 *
 * <p>伤害通道固定为 {@link ProjectileDamageChannel#MAGIC}，因此只在发射快照中应用一次魔法伤害
 * 属性，不会再叠加远程伤害或远程弹速。魔力成本与现有法杖保持同一套前缀、附魔、饰品、自动
 * 药水和魔力修补规则，同时支持整批弹丸生成失败时精确回滚。</p>
 */
public class ManaGunItem extends BaseGun {
    public ManaGunItem(
            Properties properties,
            int cooldown,
            float damage,
            float velocity,
            float knockback,
            float critical,
            int penetrate,
            float inaccuracy,
            ModRarity rarity,
            int manaCost
    ) {
        super(new Builder(cooldown, damage, velocity)
                .knockback(knockback)
                .critical(critical)
                .penetrate(penetrate)
                .inaccuracy(inaccuracy)
                .rarity(rarity)
                .manaCost(manaCost)
                .bulletFactory((player, bullet) -> new BaseBulletEntity(player, bullet) {
                    @Override
                    public DamageSource getDamageSource() {
                        return ModDamageTypes.of(
                                level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
                    }
                })
                .properties(properties));
    }

    /**
     * 魔力枪只选择 MAGIC 主通道，弹速保持武器声明值。
     */
    @Override
    protected ProjectileDamageChannel damageChannel() {
        return ProjectileDamageChannel.MAGIC;
    }

    /**
     * 为每次请求创建独占的可回滚魔力成本，绝不把事务状态保存在物品单例中。
     */
    @Override
    protected ShotCost createShotCost(ProjectileFireContext context, net.minecraft.world.item.ItemStack selectedAmmo) {
        ManaProjectileCost cost = new ManaProjectileCost(getManaCost(), this::isManaFree);
        return new ShotCost(cost, cost::finishSuccessfulAction);
    }

    /**
     * 特殊武器可声明当前请求免魔力；默认始终收费。
     *
     * <p>该判断在服务端成本准备阶段使用，客户端不会参与。</p>
     */
    protected boolean isManaFree(ProjectileFireContext context) {
        return false;
    }
}
