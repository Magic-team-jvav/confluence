package org.confluence.mod.common.summon.ground;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summon.GroundMeleeSummon;
import org.confluence.mod.common.summon.SummonPose;

/**
 * 铁傀儡召唤物运行实例。
 *
 * <p>逻辑保留 1.21 侧的铁傀儡式近战：主动接近目标，攻击时播放铁傀儡挥击音效，命中后给予上挑反馈。
 * 类型名使用 1.21 的 {@code i_32_iron_golem}，避免同一个召唤物在注册、同步和渲染层出现两套命名。</p>
 */
public final class IronGolemSummon extends GroundMeleeSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 8.0F;

    public IronGolemSummon(ServerPlayer owner, int slotCost, ProjectileCombatSnapshot snapshot, SummonPose initialPose) {
        super(Confluence.asResource("i_32_iron_golem"), owner, slotCost, snapshot, initialPose,
                1.5, 3.0, 32.0, 0.72, 0.72);
    }

    @Override
    protected void onAttackAttempt(LivingEntity target) {
        owner().level().playSound(null, net.minecraft.core.BlockPos.containing(position()), SoundEvents.IRON_GOLEM_ATTACK,
                SoundSource.NEUTRAL, 1.0F, 1.0F);
    }

    @Override
    protected void onSuccessfulHit(LivingEntity target) {
        double resistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.4 * Math.max(0.0, 1.0 - resistance), 0.0));
        target.hasImpulse = true;
    }
}
