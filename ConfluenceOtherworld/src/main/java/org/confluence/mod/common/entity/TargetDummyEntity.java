package org.confluence.mod.common.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
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
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.terra_curio.client.handler.InformationHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TargetDummyEntity extends LivingEntity {
    private int resetTimer;           // 重置计时器
    private float totalDamageAmount;  // 总伤害量
    public float damage;              // 当前伤害
    private float maxDamageAmount;    // 最大伤害量
    private float minDamageAmount;    // 最小伤害量

    // 添加常量定义
    private static final int RESET_INTERVAL_TICKS = 40;

    public TargetDummyEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean isDeadOrDying() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player && player.isCrouching() && player.onGround()) {
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof PickaxeItem) {
                discard();
                LibUtils.createItemEntity(ToolItems.TARGET_DUMMY.get().getDefaultInstance(), position(), player.level(), 0);
                return true;
            } else if (player.isCreative()) {
                discard();
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

    /**
     * 记录伤害数据
     */
    private void recordDamage(float amount) {
        maxDamageAmount = Math.max(maxDamageAmount, amount);
        minDamageAmount = amount > 0 ? Math.min(minDamageAmount, amount) : amount; // 修正最小伤害计算逻辑
        totalDamageAmount += amount;
        resetTimer = 0;
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

    @Override
    public void tick() {
        updateHealth();
        updateDamageStats();
        handleClientDisplay();
        handleResetLogic();

        super.tick();
    }

    /**
     * 更新生命值
     */
    private void updateHealth() {
        if (getHealth() != getMaxHealth()) {
            damage = getMaxHealth() - getHealth();
            // 记录伤害数据
            recordDamage(damage);
            setHealth(getMaxHealth());
        }
    }

    /**
     * 更新伤害统计
     */
    private void updateDamageStats() {
        resetTimer++;
    }

    /**
     * 处理客户端显示
     */
    private void handleClientDisplay() {
        if (level().isClientSide) {
            var dps = InformationHandler.getInformation()
                    .computeIfAbsent(InformationHandler.DPS_METER, (a) -> Component.literal(""));
            String text = String.format("Max: %.2f, Min: %.2f, Total: %.2f", maxDamageAmount, minDamageAmount == Float.MAX_VALUE ? 0.0f : minDamageAmount, totalDamageAmount);
            Minecraft.getInstance().gui.setOverlayMessage(Component.literal(text).append(", ").append(dps), false);
        }
    }

    /**
     * 处理重置逻辑
     */
    private void handleResetLogic() {
        if (resetTimer > RESET_INTERVAL_TICKS) {
            resetDamageStats();
        }
    }

    /**
     * 重置伤害统计数据
     */
    private void resetDamageStats() {
        totalDamageAmount = 0;
        minDamageAmount = Float.MAX_VALUE; // 初始化为最大值，以便第一次比较能正确设置最小值
        maxDamageAmount = 0;
        resetTimer = 0;
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
