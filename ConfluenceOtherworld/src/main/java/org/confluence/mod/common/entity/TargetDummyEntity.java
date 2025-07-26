package org.confluence.mod.common.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.item.ToolItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TargetDummyEntity extends LivingEntity {
    public TargetDummyEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player) {
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof PickaxeItem && player.isShiftKeyDown()) {
                this.remove(RemovalReason.DISCARDED);
                LibUtils.createItemEntity(ToolItems.TARGET_DUMMY.get().getDefaultInstance(), position(), player.level(), 0);
                return true;
            } else if (player.isCreative() && player.isShiftKeyDown()){
                this.remove(RemovalReason.DISCARDED);
            }
        }
        if (source.is(DamageTypes.GENERIC_KILL)) {
            discard();
        }

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F); // playBrokenSound
        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.HAY_BLOCK.defaultBlockState()),
                    this.getX(),
                    this.getY(0.6666666666666666),
                    this.getZ(),
                    Math.min(100, (int) amount),
                    this.getBbWidth() / 4.0F,
                    this.getBbHeight() / 4.0F,
                    this.getBbWidth() / 4.0F, 0.05
            );
        }
        return super.hurt(source, amount);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return NonNullList.create();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {}

    public float damage;

    @Override
    public void tick() {
        if (getHealth() != getMaxHealth()) {
            damage = getMaxHealth() - getHealth();
            setHealth(getMaxHealth());
        }
        super.tick();
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.MAX_HEALTH, 1024.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        return ToolItems.TARGET_DUMMY.get().getDefaultInstance();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
}
