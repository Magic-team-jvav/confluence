package org.confluence.mod.common.item.sword;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.sword.IchorStreamProjectile;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.entity.ModEntities;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

/// 舌锋剑：困难模式阔剑，命中敌怪后向前方以随机角度射出灵液流。
///
/// 剑本身给 5-9 秒灵液，灵液流给 3-5 秒灵液；重复赋予时重置时长。
public class BladetongueItem extends BaseSwordItem {
    /// 每次命中随机射出 1~3 道灵液流。
    public static final int MIN_STREAM_COUNT = 1;
    public static final int MAX_STREAM_COUNT = 3;
    public static final float STREAM_SPEED = 1.1F;
    public static final float YAW_SPREAD = 24.0F;
    public static final float PITCH_SPREAD = 14.0F;
    public static final int ICHOR_MIN_TICKS = 100;
    public static final int ICHOR_MAX_EXTRA_TICKS = 80;

    public BladetongueItem() {
        super(ModTiers.UNBREAKABLE, ModRarity.LIGHT_RED, 30, 2.6F, SwordDefinition.builder()
                .specialSweep(0.8F)
                .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4.0F, PortAttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    protected void onDamage(ItemStack weapon, LivingEntity attacker, LivingEntity victim, DamageSource source) {
        /// 已存在灵液时再次赋予是重置时长，因此先移除再添加。
        victim.removeEffect(ModEffects.ICHOR.get());
        victim.addEffect(new MobEffectInstance(ModEffects.ICHOR.get(), ICHOR_MIN_TICKS + attacker.getRandom().nextInt(ICHOR_MAX_EXTRA_TICKS)));
        if (!(attacker.level() instanceof ServerLevel level)) return;
        float damage = (float) attacker.getAttributeValue(LibAttributes.getAttackDamage()) * IchorStreamProjectile.DAMAGE_FACTOR;
        Vec3 origin = attacker.getEyePosition();
        int count = MIN_STREAM_COUNT + attacker.getRandom().nextInt(MAX_STREAM_COUNT - MIN_STREAM_COUNT + 1);
        for (int index = 0; index < count; index++) {
            IchorStreamProjectile projectile = ModEntities.ICHOR_STREAM.get().create(level);
            if (projectile == null) continue;
            projectile.setOwner(attacker);
            projectile.setDamage(damage);
            projectile.setPos(origin.x, origin.y - 0.1, origin.z);
            projectile.shootFromRotation(attacker,
                    attacker.getXRot() + (attacker.getRandom().nextFloat() - 0.5F) * PITCH_SPREAD,
                    attacker.getYRot() + (attacker.getRandom().nextFloat() - 0.5F) * YAW_SPREAD,
                    0.0F, STREAM_SPEED, 0.0F);
            level.addFreshEntity(projectile);
        }
    }
}
