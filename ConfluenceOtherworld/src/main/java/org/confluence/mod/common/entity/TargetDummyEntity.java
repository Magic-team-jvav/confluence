package org.confluence.mod.common.entity;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
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

public class TargetDummyEntity extends Mob {
    public TargetDummyEntity(EntityType<TargetDummyEntity> type, Level level) {
        super(type, level);
    }

    public boolean shouldPlayAnimation;
    public boolean shouldPlayAnimationBack;

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!super.hurt(source, amount) && shouldPlayAnimation /*???*/) return false;
        shouldPlayAnimation = true;
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
        }  // showParticles
        return true;
    }

    @Override
    public void remove(RemovalReason reason) {
        for (var armor : this.getArmorSlots()){
            LibUtils.createItemEntity(armor, getX(), getY(), getZ(), level(), 0);
        }
        LibUtils.createItemEntity(this.getMainHandItem(), getX(), getY(), getZ(), level(), 0);
        LibUtils.createItemEntity(this.getOffhandItem(), getX(), getY(), getZ(), level(), 0);
        super.remove(reason);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        //TODO: 额外执行穿上盔甲（生存无影响）
        if (!player.isSpectator() && player.getAbilities().mayBuild && !level().isClientSide) {
            ItemStack itemstack = player.getItemInHand(hand);
            EquipmentSlot equipmentSlot = getEquipmentSlotForItem(itemstack);

            Level level = player.level();
            if (itemstack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
                equipmentSlot = this.getClickedSlot(vec);
                if (equipmentSlot == null) {
                    if (hasItemInSlot(EquipmentSlot.MAINHAND)) {
                        equipmentSlot = EquipmentSlot.MAINHAND;
                    } else {
                        equipmentSlot = EquipmentSlot.OFFHAND;
                    }
                }
                if (this.hasItemInSlot(equipmentSlot)) {
                    if (level.isClientSide) return InteractionResult.CONSUME;
                    this.swapItem(player, equipmentSlot, ItemStack.EMPTY, hand);
                }
            } else if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                if (level.isClientSide) return InteractionResult.CONSUME;
                this.swapItem(player, equipmentSlot, itemstack, hand);
            }
        }

        return InteractionResult.PASS;
    }

    private void swapItem(Player player, EquipmentSlot slot, ItemStack armor, InteractionHand hand) {
        ItemStack oldArmor = this.getItemBySlot(slot);
        player.setItemInHand(hand, oldArmor.copy());
        this.setItemSlotAndDropWhenKilled(slot, armor);
    }

    @Nullable
    private EquipmentSlot getClickedSlot(Vec3 vec3) {
        EquipmentSlot equipmentSlot = null;
        double d0 = vec3.y;
        double d1 = vec3.x;
        EquipmentSlot slot = EquipmentSlot.FEET;
        if (d0 >= 0.1D && d0 < 0.1D + (0.45D) && this.hasItemInSlot(slot)) {
            equipmentSlot = EquipmentSlot.FEET;
        } else if (d1 >= -0.3D && d0 >= 0.9D + (0.0D) && d0 < 0.9D + (0.7D) && this.hasItemInSlot(EquipmentSlot.CHEST)) {
            equipmentSlot = EquipmentSlot.CHEST;
        } else if (d0 >= 0.4D && d0 < 0.4D + (0.8D) && this.hasItemInSlot(EquipmentSlot.LEGS)) {
            equipmentSlot = EquipmentSlot.LEGS;
        } else if (d0 >= 1.6D && this.hasItemInSlot(EquipmentSlot.HEAD)) {
            equipmentSlot = EquipmentSlot.HEAD;
        }
        return equipmentSlot;
    }

    //@Override
    //public @Nullable Component getCustomName() {
    //    return Component.literal("go: " + shouldPlayAnimation + "  back: " + shouldPlayAnimationBack);
    //}

    public float damage;

    @Override
    public void tick() {
        if (shouldPlayAnimation && shouldPlayAnimationBack){
            shouldPlayAnimation = false;
            shouldPlayAnimationBack = false;
        }
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
}
