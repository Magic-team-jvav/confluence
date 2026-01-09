package org.confluence.mod.common.item.bow.repeater;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.common.item.bow.RandomCount;

/**
 * 泰拉连弩基类
 */
public class BaseTerraRepeaterItem extends BaseTerraBowItem {
    /**
     * 装弹速度
     */
    private final int reloadSpeed;
    /**
     * 射击间隔
     *
     */
    private final int shootInterval;
    /**
     * 容量
     */
    private final int capacity;
    /**
     * 基础箭矢速度
     */
    private final float arrowSpeed;
    /**
     * 连发个数（每次射击会射出多少支箭，每个间隔一帧
     */
    private final RandomCount burstCount;
    /**
     * 并发个数（同时射出多少支箭，有一定的散射角度）
     */
    private final RandomCount concurrentCount;
    /**
     * 弹药限制
     */
    private final AmmunitionRestrictions ammunitionRestrictions;

    public BaseTerraRepeaterItem(Properties properties, float baseDamage, BaseTerraBowItem.Builder bowBuilder, Builder repeaterBuilder) {
        super(properties, baseDamage, bowBuilder);
        this.reloadSpeed = repeaterBuilder.reloadSpeed;
        this.shootInterval = repeaterBuilder.shootInterval;
        this.capacity = repeaterBuilder.capacity;
        this.arrowSpeed = repeaterBuilder.arrowSpeed;
        this.burstCount = repeaterBuilder.burstCount;
        this.concurrentCount = repeaterBuilder.concurrentCount;
        this.ammunitionRestrictions = repeaterBuilder.ammunitionRestrictions;
    }

    public BaseTerraRepeaterItem(float baseDamage, BaseTerraBowItem.Builder bowBuilder, Builder repeaterBuilder) {
        this(new Properties(), baseDamage, bowBuilder, repeaterBuilder);
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    public static class Builder {
        public static final AmmunitionRestrictions DEFAULT_AMMUNITION_RESTRICTIONS =
                (ammunitionStack, weaponStack, livingEntity) -> ammunitionStack.is(ItemTags.ARROWS) || ammunitionStack.is(Items.FIREWORK_ROCKET);
        public static final AmmunitionRestrictions DEFAULT_AMMUNITION_RESTRICTIONS_ARROWS =
                (ammunitionStack, weaponStack, livingEntity) -> ammunitionStack.is(ItemTags.ARROWS) || ammunitionStack.is(Items.FIREWORK_ROCKET);
        public static final AmmunitionRestrictions DEFAULT_AMMUNITION_RESTRICTIONS_FIREWORK_ROCKET =
                (ammunitionStack, weaponStack, livingEntity) -> ammunitionStack.is(Items.FIREWORK_ROCKET);
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
         * 容量
         */
        private int capacity = 3;
        /**
         * 基础箭矢速度
         */
        private float arrowSpeed = 1;
        /**
         * 连发个数（每次射击会射出多少支箭，每个间隔一帧
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

        public Builder reloadSpeed(int reloadSpeed) {
            this.reloadSpeed = reloadSpeed;
            return this;
        }

        public Builder shootInterval(int shootInterval) {
            this.shootInterval = shootInterval;
            return this;
        }

        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder arrowSpeed(float arrowSpeed) {
            this.arrowSpeed = arrowSpeed;
            return this;
        }

        public Builder burstCount(RandomCount burstCount) {
            this.burstCount = burstCount;
            return this;
        }

        public Builder burstCount(int min, int max) {
            this.burstCount = RandomCount.createRangeRandom(min, max);
            return this;
        }

        public Builder burstCount(int[] number) {
            this.burstCount = RandomCount.createArrayRandom(number);
            return this;
        }

        public Builder burstCount(int number) {
            this.burstCount = RandomCount.createNonRandom(number);
            return this;
        }

        public Builder concurrentCount(RandomCount concurrentCount) {
            this.concurrentCount = concurrentCount;
            return this;
        }

        public Builder concurrentCount(int min, int max) {
            this.concurrentCount = RandomCount.createRangeRandom(min, max);
            return this;
        }

        public Builder concurrentCount(int[] number) {
            this.concurrentCount = RandomCount.createArrayRandom(number);
            return this;
        }

        public Builder concurrentCount(int number) {
            this.concurrentCount = RandomCount.createNonRandom(number);
            return this;
        }

        public Builder ammunitionRestrictions(AmmunitionRestrictions ammunitionRestrictions) {
            this.ammunitionRestrictions = ammunitionRestrictions;
            return this;
        }
    }

    @FunctionalInterface
    public interface AmmunitionRestrictions {
        boolean test(ItemStack ammunitionStack, ItemStack weaponStack, LivingEntity livingEntity);
    }
}
