package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.entity.soulcerer.SpiritSurgeEntity;
import org.confluence.mod.util.PlayerUtils;

/// 灵魂激荡类技能：生成一圈向外扩散的冲击波。
///
/// 半径、纵向范围、持续时间与可选连锁弹幕全部由构造参数给出，普通激荡 / 强力激荡 /
/// 引魂激荡 / 灵魂引发共用同一个类。
public class SoulSurgeSkill extends SoulSkill {
    protected final double radius;
    protected final double verticalRange;
    protected final int surgeDuration;
    protected final int chainCount;
    protected final float chainDamage;

    public SoulSurgeSkill(ResourceLocation icon, ResourceLocation id, float damage, float soulCost, int cooldown,
                          double radius, double verticalRange, int surgeDuration, int chainCount, float chainDamage) {
        super(icon, id, damage, Kind.ACTIVE, soulCost, cooldown);
        this.radius = radius;
        this.verticalRange = verticalRange;
        this.surgeDuration = surgeDuration;
        this.chainCount = chainCount;
        this.chainDamage = chainDamage;
    }

    /// 服务端创建冲击波实体；客户端不预测伤害。
    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (player instanceof ServerPlayer serverPlayer) {
            float damage = PlayerUtils.getSoulSkillDamage(player, skillStack);
            serverPlayer.level().addFreshEntity(new SpiritSurgeEntity(serverPlayer, damage, radius, verticalRange, surgeDuration, chainCount, chainDamage));
        }
    }
}
