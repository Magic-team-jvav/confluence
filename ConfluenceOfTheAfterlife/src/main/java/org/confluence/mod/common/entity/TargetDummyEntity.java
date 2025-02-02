package org.confluence.mod.common.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.common.particle.DamageIndicatorOptions;
import org.confluence.mod.mixed.IDamageSource;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.jetbrains.annotations.NotNull;

public class TargetDummyEntity extends LivingEntity {

    public TargetDummyEntity(EntityType<TargetDummyEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player){
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof PickaxeItem){
                this.remove(RemovalReason.DISCARDED);
                return true;
            }
        }

        float damage = amount;
        boolean crit = false;
        if (!TCAttributes.hasCustomAttribute(TCAttributes.CRIT_CHANCE) && source.getEntity() instanceof Player player) {
            double chance = player.getAttributeValue(TCAttributes.CRIT_CHANCE);
            if (this.level().random.nextFloat() < chance) {
                damage *= 2;
                player.crit(this);
                crit = true;
            }
        }
        if (source.getDirectEntity() instanceof AbstractArrow arrow) {
            crit |= arrow.isCritArrow();
        }
        crit |= ((IDamageSource) source).confluence$isCritical();
        float roundedAmount = Math.round(damage * 10) / 10f;
        int intAmount = (int) roundedAmount;
        String text = roundedAmount % 1 == 0 ? String.valueOf(intAmount) : String.valueOf(roundedAmount);
        Component damageComponent = Component.literal(text)
                .withStyle(crit ? ChatFormatting.DARK_RED : ChatFormatting.GOLD, ChatFormatting.BOLD);


        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F); // playBrokenSound
        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()),
                    this.getX(),
                    this.getY(0.6666666666666666),
                    this.getZ(),
                    10,
                    this.getBbWidth() / 4.0F,
                    this.getBbHeight() / 4.0F,
                    this.getBbWidth() / 4.0F, 0.05
            );
            sl.sendParticles(
                    new DamageIndicatorOptions(damageComponent, crit),
                    this.position().x,
                    this.getBoundingBoxForCulling().maxY,
                    this.position().z, 1, 0.1, 0.1, 0.1, 0);

        }  // showParticles
        return super.hurt(source, amount <= 1000000F ? 0 : amount);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return NonNullList.create();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.MAX_HEALTH, 1024.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }
}
