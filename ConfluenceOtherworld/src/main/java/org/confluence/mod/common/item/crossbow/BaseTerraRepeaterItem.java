package org.confluence.mod.common.item.crossbow;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import org.confluence.lib.util.LivingEntityDelayRun;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * 泰拉连弩基类
 */
public class BaseTerraRepeaterItem extends BaseTerraBowItem {
    /**
     * 每一击退转换速度
     */
    public static final double KNOCKBACK_SPEED = 0.1;
    /**
     * 击退
     */
    private final float baseKnockback;
    /**
     * 装弹速度
     */
    private final int baseReloadSpeed;
    /**
     * 射击间隔
     *
     */
    private final int baseShootInterval;
    /**
     * 容量，每64一个格子
     */
    private final int baseCapacity;
    /**
     * 基础箭矢速度
     */
    private final float baseArrowSpeed;
    /**
     * 连发个数（每次射击会射出多少支箭，每个间隔一帧）
     */
    private final RandomCount baseBurstCount;
    /**
     * 并发个数（同时射出多少支箭，有一定的散射角度）
     */
    private final RandomCount baseConcurrentCount;
    /**
     * 弹药限制
     */
    private final AmmunitionRestrictions ammunitionRestrictions;

    public BaseTerraRepeaterItem(Properties properties, float baseDamage, BaseTerraBowItem.Builder bowBuilder, Builder repeaterBuilder) {
        super(properties, baseDamage, bowBuilder);
        this.baseReloadSpeed = repeaterBuilder.reloadSpeed;
        this.baseShootInterval = repeaterBuilder.shootInterval;
        this.baseCapacity = repeaterBuilder.capacity;
        this.baseArrowSpeed = repeaterBuilder.arrowSpeed;
        this.baseBurstCount = repeaterBuilder.burstCount;
        this.baseConcurrentCount = repeaterBuilder.concurrentCount;
        this.ammunitionRestrictions = repeaterBuilder.ammunitionRestrictions;
        this.baseKnockback = repeaterBuilder.knockback;
    }

    public BaseTerraRepeaterItem(float baseDamage, BaseTerraBowItem.Builder bowBuilder, Builder repeaterBuilder) {
        this(new Properties(), baseDamage, bowBuilder, repeaterBuilder);
    }

    //region 获取加成后的值
    public float getKnockback(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return this.baseKnockback;
    }

    public int getReloadSpeed(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return this.baseReloadSpeed;
    }

    public int getShootInterval(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return this.baseShootInterval;
    }

    public int getCapacity(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return this.baseCapacity;
    }

    public float getArrowSpeed(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return this.baseArrowSpeed;
    }

    public RandomCount getBurstCount(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return this.baseBurstCount;
    }

    public RandomCount getConcurrentCount(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return this.baseConcurrentCount;
    }

    public float getDamage(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return this.baseDamage;
    }
    //endregion

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    @Override
    protected void shoot(@NotNull ServerLevel level, @NotNull LivingEntity shooter, @NotNull InteractionHand hand, @NotNull ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
        int countCount = baseBurstCount.getCount(shooter.getRandom());
        if (countCount > 1) {
            LivingEntityDelayRun.Run build = LivingEntityDelayRun.createTimingRunBilder().tickRun((tick, maxTick, playerTimingRun) -> {
                if (tick < maxTick) {
                    super.shoot(level, shooter, hand, weapon, projectileItems, velocity, inaccuracy, isCrit, target);
                }
                return tick - 1;
            }).build(countCount);
            LivingEntityDelayRun.getInstance(shooter).addTimingRun(hand, build);
        }
        super.shoot(level, shooter, hand, weapon, projectileItems, velocity, inaccuracy, isCrit, target);
    }

    @Override
    public @NotNull Predicate<ItemStack> getSupportedHeldProjectiles(@NotNull ItemStack stack) {
        return (itemStack) -> ammunitionRestrictions.test(itemStack, stack);
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles(@NotNull ItemStack stack) {
        return (itemStack) -> ammunitionRestrictions.test(itemStack, stack);
    }

    public static class Builder {
        public static final AmmunitionRestrictions DEFAULT_AMMUNITION_RESTRICTIONS =
                (ammunitionStack, weaponStack) -> ammunitionStack.is(ItemTags.ARROWS) || ammunitionStack.is(Items.FIREWORK_ROCKET);
        public static final AmmunitionRestrictions DEFAULT_AMMUNITION_RESTRICTIONS_ARROWS =
                (ammunitionStack, weaponStack) -> ammunitionStack.is(ItemTags.ARROWS) || ammunitionStack.is(Items.FIREWORK_ROCKET);
        public static final AmmunitionRestrictions DEFAULT_AMMUNITION_RESTRICTIONS_FIREWORK_ROCKET =
                (ammunitionStack, weaponStack) -> ammunitionStack.is(Items.FIREWORK_ROCKET);
        /**
         * 装弹速度
         */
        private int reloadSpeed = 40;
        /**
         * 射击间隔
         *
         */
        private int shootInterval = 5;
        /**
         * 容量，每64一个格子
         */
        private int capacity = 5;
        /**
         * 基础箭矢速度
         */
        private float arrowSpeed = 1;
        /**
         * 击退
         */
        private float knockback = 0;
        /**
         * 连发个数（每次射击会射出多少支箭，每个间隔一帧）
         */
        private RandomCount burstCount = RandomCount.DEFAULT;
        /**
         * 并发个数（同时射出多少支箭，有一定的散射角度）
         */
        private RandomCount concurrentCount = RandomCount.DEFAULT;
        /**
         * 弹药限制
         */
        private AmmunitionRestrictions ammunitionRestrictions = DEFAULT_AMMUNITION_RESTRICTIONS;

        /**
         * 装弹速度
         */
        public Builder reloadTick(int reloadSpeed) {
            this.reloadSpeed = reloadSpeed;
            return this;
        }

        /**
         * 射击间隔
         */
        public Builder shootInterval(int shootInterval) {
            this.shootInterval = shootInterval;
            return this;
        }

        /**
         * 容量，每64一个格子
         */
        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        /**
         * 基础箭矢速度
         */
        public Builder arrowSpeed(float arrowSpeed) {
            this.arrowSpeed = arrowSpeed;
            return this;
        }

        /**
         * 连发个数（每次射击会射出多少支箭，每个间隔一帧）
         */
        public Builder burstCount(RandomCount burstCount) {
            this.burstCount = burstCount;
            return this;
        }

        /**
         * 连发个数（每次射击会射出多少支箭，每个间隔一帧）在区间中随机
         */
        public Builder burstCount(int min, int max) {
            this.burstCount = RandomCount.createRangeRandom(min, max);
            return this;
        }

        /**
         * 连发个数（每次射击会射出多少支箭，每个间隔一帧）在数组中随机
         */
        public Builder burstCount(int[] number) {
            this.burstCount = RandomCount.createArrayRandom(number);
            return this;
        }

        /**
         * 连发个数（每次射击会射出多少支箭，每个间隔一帧）固定
         */
        public Builder burstCount(int number) {
            this.burstCount = RandomCount.createNonRandom(number);
            return this;
        }

        /**
         * 并发个数（同时射出多少支箭，有一定的散射角度）
         */
        public Builder concurrentCount(RandomCount concurrentCount) {
            this.concurrentCount = concurrentCount;
            return this;
        }

        /**
         * 并发个数（同时射出多少支箭，有一定的散射角度）在区间中随机
         */
        public Builder concurrentCount(int min, int max) {
            this.concurrentCount = RandomCount.createRangeRandom(min, max);
            return this;
        }

        /**
         * 并发个数（同时射出多少支箭，有一定的散射角度）在数组中随机
         */
        public Builder concurrentCount(int[] number) {
            this.concurrentCount = RandomCount.createArrayRandom(number);
            return this;
        }

        /**
         * 并发个数（同时射出多少支箭，有一定的散射角度）固定
         */
        public Builder concurrentCount(int number) {
            this.concurrentCount = RandomCount.createNonRandom(number);
            return this;
        }

        /**
         * 弹药限制
         */
        public Builder ammunitionRestrictions(AmmunitionRestrictions ammunitionRestrictions) {
            this.ammunitionRestrictions = ammunitionRestrictions;
            return this;
        }

        /**
         * 击退
         */
        public Builder knockback(float knockback) {
            this.knockback = knockback;
            return this;
        }
    }

    @FunctionalInterface
    public interface AmmunitionRestrictions {
        boolean test(ItemStack ammunitionStack, ItemStack weaponStack);
    }
}
