package org.confluence.mod.common.item.sword;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.sword.MuramasaSlashProjectile;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.entity.ModEntities;

/// 村正：命中敌怪时额外生成一道蓝色的斩击射弹，角度小范围随机。
public class MuramasaItem extends BaseSwordItem {
    public static final float SLASH_DAMAGE_FACTOR = 0.5F;
    public static final float SLASH_SPEED = 1.35F;
    public static final float YAW_SPREAD = 18.0F;
    public static final float PITCH_SPREAD = 12.0F;

    public MuramasaItem() {
        super(ModTiers.UNBREAKABLE, ModRarity.WHITE, 15, 3, SwordDefinition.builder()
                .specialSweep(0.8F)
                .attribute(Attributes.ENTITY_INTERACTION_RANGE, 1.5F, AttributeModifier.Operation.ADD_VALUE)
                .attribute(Attributes.ATTACK_KNOCKBACK, 0.2F, AttributeModifier.Operation.ADD_VALUE)
                .tooltipImage());
    }

    @Override
    protected void onDamage(ItemStack weapon, LivingEntity attacker, LivingEntity victim, DamageSource source) {
        if (!(attacker.level() instanceof ServerLevel level)) return;
        MuramasaSlashProjectile slash = ModEntities.MURAMASA_SLASH.get().create(level);
        if (slash == null) return;
        slash.setOwner(attacker);
        slash.setDamage((float) attacker.getAttributeValue(LibAttributes.getAttackDamage()) * SLASH_DAMAGE_FACTOR);
        Vec3 origin = attacker.getEyePosition();
        slash.setPos(origin.x, origin.y - 0.1, origin.z);
        slash.shootFromRotation(attacker,
                attacker.getXRot() + (attacker.getRandom().nextFloat() - 0.5F) * PITCH_SPREAD,
                attacker.getYRot() + (attacker.getRandom().nextFloat() - 0.5F) * YAW_SPREAD,
                0.0F, SLASH_SPEED, 0.0F);
        level.addFreshEntity(slash);
    }
}
