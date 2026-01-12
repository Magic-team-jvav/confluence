package org.confluence.mod.common.item.crossbow;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.DelayTaskHolder;
import org.confluence.mod.api.ITerraArrowProjectileWeaponItem;
import org.confluence.mod.common.entity.projectile.range.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.util.ModUtils;
import org.confluence.terraentity.api.item.ILeftClickStateItem;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.init.TEDataComponentTypes;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 泰拉连弩基类
 */
public class BaseTerraRepeaterItem extends CrossbowItem implements ITerraArrowProjectileWeaponItem<BaseTerraRepeaterItem>, ILeftClickStateItem {
    public static final String REPEATER_CONTINUOUS_SHOOTING = "repeater.continuous_shooting";
    public static final String REPEATER_SHOOTING = "repeater.shooting";
    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;
    private static final CrossbowItem.ChargingSounds DEFAULT_SOUNDS = new CrossbowItem.ChargingSounds(
            Optional.of(SoundEvents.CROSSBOW_LOADING_START),
            Optional.of(SoundEvents.CROSSBOW_LOADING_MIDDLE),
            Optional.of(SoundEvents.CROSSBOW_LOADING_END)
    );

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
     * 并发间隔（并发个数时，每个箭的间隔）
     */
    private final RandomCount concurrentInterval;
    /**
     * 弹药限制
     */
    private final AmmunitionRestrictions ammunitionRestrictions;
    /**
     * 并发角度（并发个数时，每个箭的偏移角度）
     */
    private final RandomCount concurrentAngle;

    private final float baseDamage;
    private final BaseArrowEntity.Builder arrowModifier;
    private final BaseTerraArrowItem.ModifyArrowBuilder modifyArrowBuilder;

    public BaseTerraRepeaterItem(Properties properties, float baseDamage, BaseTerraArrowItem.ModifyArrowBuilder modifyArrowBuilder, Builder repeaterBuilder) {
        super(modifyArrowBuilder.buildProperties(properties.stacksTo(1).component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY)));
        this.baseReloadSpeed = repeaterBuilder.reloadSpeed;
        this.baseShootInterval = repeaterBuilder.shootInterval;
        this.baseCapacity = repeaterBuilder.capacity;
        this.baseArrowSpeed = repeaterBuilder.arrowSpeed;
        this.baseBurstCount = repeaterBuilder.burstCount;
        this.baseConcurrentCount = repeaterBuilder.concurrentCount;
        this.ammunitionRestrictions = repeaterBuilder.ammunitionRestrictions;
        this.baseKnockback = repeaterBuilder.knockback;
        this.concurrentAngle = repeaterBuilder.concurrentAngle;
        this.concurrentInterval = repeaterBuilder.concurrentInterval;
        this.baseDamage = baseDamage;
        this.arrowModifier = new BaseArrowEntity.Builder();
        modifyArrowBuilder.modifyArrowBuilder.forEach(m -> m.accept(this.arrowModifier));
        this.modifyArrowBuilder = modifyArrowBuilder;
    }

    public BaseTerraRepeaterItem(float baseDamage, BaseTerraArrowItem.ModifyArrowBuilder bowModifyArrowBuilder, Builder repeaterBuilder) {
        this(new Properties(), baseDamage, bowModifyArrowBuilder, repeaterBuilder);
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

    public int getCapacity(@NotNull LivingEntity shooter, ItemStack weapon) {
        return this.baseCapacity;
    }

    public float getArrowSpeed(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return this.baseArrowSpeed;
    }

    public int getBurstCount(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return Math.round(this.baseBurstCount.getCount(shooter.getRandom()));
    }

    public int getConcurrentCount(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return Math.round(this.baseConcurrentCount.getCount(shooter.getRandom()));
    }

    public float getConcurrentAngle(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return this.concurrentAngle.getCount(shooter.getRandom());
    }

    public float getConcurrentInterval(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return this.concurrentInterval.getCount(shooter.getRandom());
    }

    public float getDamage(@NotNull LivingEntity shooter, @NotNull InteractionHand hand) {
        return this.baseDamage;
    }
    //endregion

    protected static Vector3f getProjectileShotVector(LivingEntity shooter, Vec3 distance, float angle) {
        Vector3f vector3f = distance.toVector3f().normalize();
        Vector3f vector3f1 = new Vector3f(vector3f).cross(new Vector3f(0.0F, 1.0F, 0.0F));
        if ((double) vector3f1.lengthSquared() <= 1.0E-7) {
            Vec3 vec3 = shooter.getUpVector(1.0F);
            vector3f1 = new Vector3f(vector3f).cross(vec3.toVector3f());
        }

        Vector3f vector3f2 = new Vector3f(vector3f).rotateAxis((float) (Math.PI / 2), vector3f1.x, vector3f1.y, vector3f1.z);
        return new Vector3f(vector3f).rotateAxis(angle * (float) (Math.PI / 180.0), vector3f2.x, vector3f2.y, vector3f2.z);
    }

    protected boolean tryLoadProjectiles(LivingEntity shooter, ItemStack weapon) {
        if (shooter.level().isClientSide) {
            return true;
        }
        int projectileCount = getCapacity(shooter, weapon);
        List<ItemStack> list = new ArrayList<>();

        if ((shooter instanceof Player player) && player.isCreative()) {
            ItemStack projectile = shooter.getProjectile(weapon);
            if (!projectile.isEmpty()) {
                int totalLoaded = 0;
                while (totalLoaded < projectileCount) {
                    ItemStack copy = projectile.copy();
                    int amountToAdd = Math.min(projectileCount - totalLoaded, copy.getMaxStackSize());
                    copy.setCount(amountToAdd);
                    list.add(copy);
                    totalLoaded += amountToAdd;

                    if (amountToAdd < copy.getMaxStackSize()) {
                        break; // 已达到容量限制
                    }
                }
            }
        } else {
            int totalLoaded = 0;
            while (totalLoaded < projectileCount) {
                ItemStack projectile = shooter.getProjectile(weapon);
                if (projectile.isEmpty()) {
                    break;
                }

                ItemStack itemStackLast;
                if (!list.isEmpty()) {
                    itemStackLast = list.getLast();
                    int maxStackSize = itemStackLast.getMaxStackSize();
                    int count = itemStackLast.getCount();
                    int spaceAvailable = maxStackSize - count;

                    if (spaceAvailable > 0 && ItemStack.isSameItemSameComponents(itemStackLast, projectile)) {
                        int amountToAdd = Math.min(spaceAvailable, projectile.getCount());
                        amountToAdd = Math.min(amountToAdd, projectileCount - totalLoaded);

                        projectile.shrink(amountToAdd);
                        itemStackLast.grow(amountToAdd);
                        totalLoaded += amountToAdd;

                        if (projectile.isEmpty()) {
                            continue;
                        }
                    }
                }

                int amountToSplit = Math.min(projectile.getCount(), Math.min(projectileCount - totalLoaded, projectile.getMaxStackSize()));
                ItemStack split = projectile.split(amountToSplit);
                if (!split.isEmpty()) {
                    list.add(split);
                    totalLoaded += amountToSplit;
                    continue;
                }
                break;
            }
        }

        if (list.isEmpty()) {
            return false;
        }

        weapon.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(list));
        return true;
    }

    public static boolean isCharged(ItemStack crossbowStack) {
        var projectiles = crossbowStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        return !projectiles.isEmpty();
    }

    protected float getShootingPower(ChargedProjectiles projectile, Player player, InteractionHand hand) {
        return getArrowSpeed(player, hand);
    }

    protected static float getShotPitch(RandomSource random, int index) {
        return index == 0 ? 1.0F : getRandomShotPitch((index & 1) == 1, random);
    }

    protected static float getRandomShotPitch(boolean isHighPitched, RandomSource random) {
        float f = isHighPitched ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    public static int getChargeDuration(ItemStack stack, LivingEntity shooter) {
        float f = EnchantmentHelper.modifyCrossbowChargingTime(stack, shooter, 1.25F);
        return Mth.floor(f * 20.0F);
    }

    protected CrossbowItem.ChargingSounds getChargingSounds(ItemStack stack) {
        return EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS).orElse(DEFAULT_SOUNDS);
    }

    protected static float getPowerForTime(int timeLeft, ItemStack stack, LivingEntity shooter) {
        float f = (float) timeLeft / (float) getChargeDuration(stack, shooter);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    /**
     * 获取一个弹药项目
     */
    private @Nullable ItemStack extractAmmoItem(ItemStack weapon) {
        var ammoContainer = getWeaponItemProjectiles(weapon);
        if (ammoContainer == null || ammoContainer.isEmpty()) {
            return null;
        }
        List<ItemStack> ammoItems = new ArrayList<>(ammoContainer.getItems());
        Iterator<ItemStack> iterator = ammoItems.iterator();
        while (iterator.hasNext()) {
            ItemStack currentAmmoStack = iterator.next();
            if (currentAmmoStack.isEmpty()) {
                iterator.remove();
                continue;
            }
            ItemStack extractedAmmo = currentAmmoStack.split(1);
            if (currentAmmoStack.isEmpty()) {
                iterator.remove();
            }
            weapon.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(ammoItems));
            return extractedAmmo;
        }
        return null;
    }

    public @Nullable ChargedProjectiles getWeaponItemProjectiles(@NotNull ItemStack stack) {
        return stack.get(DataComponents.CHARGED_PROJECTILES);
    }

    private static @NotNull InteractionHand getHand(Player player, ItemStack itemStack) {
        return player.getMainHandItem() == itemStack ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shootingPerform(Level level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, float velocity, float inaccuracy, @Nullable LivingEntity target) {
        if (!(level instanceof ServerLevel serverlevel)) {
            return false;
        }

        ItemStack itemStack = extractAmmoItem(weapon);
        if (itemStack == null) {
            return false;
        }

        this.shoot(serverlevel, shooter, hand, weapon, List.of(itemStack), velocity, inaccuracy, shooter instanceof Player, target);
        if (shooter instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, weapon);
            serverplayer.awardStat(Stats.ITEM_USED.get(weapon.getItem()));
        }

        return true;
    }

    protected boolean shootingPerformContinuousShooting(Player player, ItemStack itemStack, int countCount, DelayTaskHolder delayTaskHolder, InteractionHand hand, Level level, float shootingPower) {
        if (delayTaskHolder.containsTask(hand, REPEATER_CONTINUOUS_SHOOTING)) {
            return false;
        }
        delayTaskHolder.addTask(hand, REPEATER_CONTINUOUS_SHOOTING, org.confluence.lib.util.DelayTaskHolder.createTaskBilder()
                .repeatCount(countCount - 1)
                .removedTick(1)
                .resultRun((tick, maxTick, task) -> {
                    if (!shootingPerform(level, player, hand, itemStack, shootingPower, 1.0F, null)) {
                        task.remove();
                    }
                    return 0;
                }).build());
        return true;
    }

    @Override
    public void onLeftClick(Player player, ItemStack itemStack) {
        var chargedprojectiles = getWeaponItemProjectiles(itemStack);
        if (chargedprojectiles == null || chargedprojectiles.isEmpty()) {
            return;
        }

        InteractionHand hand = getHand(player, itemStack);
        DelayTaskHolder delayTaskHolder = DelayTaskHolder.of(player);
        Level level = player.level();
        if (!delayTaskHolder.containsTask(hand).isEmpty()) {
            return;
        } else {
//            if (!shootingPerform(level, player, hand, itemStack, getShootingPower(chargedprojectiles, player, hand), 1.0F, null)) {
//                return;
//            }
        }

        int countCount = getBurstCount(player, hand);
        float shootingPower = getShootingPower(chargedprojectiles, player, hand);
        if (countCount > 1) {
            shootingPerformContinuousShooting(player, itemStack, countCount, delayTaskHolder, hand, level, shootingPower);
        }

        delayTaskHolder.addTask(hand, REPEATER_SHOOTING, DelayTaskHolder.createTaskBilder()
                .repeatCount(-1)
                .removedTick(getShootInterval(player, hand))
                .resultRun((tick, maxTick, task) -> {
                    var projectiles = getWeaponItemProjectiles(itemStack);
                    if (projectiles == null || projectiles.isEmpty()) {
                        task.remove();
                        return 0;
                    }
                    if (countCount > 1) {
                        shootingPerformContinuousShooting(player, itemStack, countCount, delayTaskHolder, hand, level, shootingPower);
                    }
                    if (!shootingPerform(level, player, hand, itemStack, shootingPower, 1.0F, null)) {
                        task.remove();
                    }
                    return 0;
                }).build());
    }

    @Override
    public void onLeftRelease(Player player, ItemStack itemStack) {
        InteractionHand hand = getHand(player, itemStack);
        DelayTaskHolder delayTaskHolder = DelayTaskHolder.of(player);
        delayTaskHolder.removeTask(hand, REPEATER_SHOOTING);
//        delayTaskHolder.removeTask(hand, REPEATER_CONTINUOUS_SHOOTING);
    }

    @Override
    protected void shoot(@NotNull ServerLevel level, @NotNull LivingEntity shooter, @NotNull InteractionHand hand, @NotNull ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
        float processProjectileSpread = EnchantmentHelper.processProjectileSpread(level, weapon, shooter, 0.0F);
        int projectileItemsCount = projectileItems.size();

        float angleIncrement = projectileItemsCount == 1 ? 0.0F : 2.0F * processProjectileSpread / (float) (projectileItemsCount - 1);
        float initialAngleOffset = (float) ((projectileItemsCount - 1) % 2) * angleIncrement / 2.0F;

        int signFactor = shooter.getRandom().nextBoolean() ? 1 : -1;
        for (int itemstackIndex = 0; itemstackIndex < projectileItemsCount; itemstackIndex++) {
            ItemStack itemstack = projectileItems.get(itemstackIndex);
            if (itemstack.isEmpty()) {
                continue;
            }
            float projectileVelocity = velocity;
            if (itemstack.is(Items.FIREWORK_ROCKET)) {
                projectileVelocity = velocity * 0.507f;
            }
            int i = (itemstackIndex % 2 == 0 ? itemstackIndex - 1 : -itemstackIndex) * signFactor;
            int concurrentCount = getConcurrentCount(shooter, hand);

            int multiShootCount = !modifyArrowBuilder.canMultiShoot.test(itemstack) ? 1 : concurrentCount;
            int signFactor1 = shooter.getRandom().nextBoolean() ? 1 : -1;
            for (int projectileIndex = 0; projectileIndex < concurrentCount; projectileIndex++) {
                int i1 = (projectileIndex % 2 == 0 ? projectileIndex - 1 : -projectileIndex) * signFactor1;

                float angle = projectileIndex > 0 ? getConcurrentAngle(shooter, hand) * i : 0;
                float angleY = initialAngleOffset * angleIncrement + angle;

                Projectile projectile = createProjectile(level, shooter, weapon, itemstack, isCrit);
                if (projectile instanceof AbstractArrow abstractArrow) {
                    abstractArrow.setBaseDamage(getDamage(shooter, hand));
                }
                shootProjectile(shooter, projectile, itemstackIndex, projectileVelocity, inaccuracy + modifyArrowBuilder.inaccuracy, angleY, target);
                float y = itemstackIndex > 0 ? getConcurrentInterval(shooter, hand) * i : 0;
                float z = projectileIndex > 0 ? getConcurrentInterval(shooter, hand) * i1 : 0;
                Vec3 offset = projectileItemsCount <= multiShootCount ? new Vec3(0, y, z) : new Vec3(0, z, y);
                BaseTerraBowItem.transformAndApplyOffsetToProjectile(projectile, offset);
                BaseTerraBowItem.processArrowBaseEffects(shooter, hand, weapon, projectile, projectileIndex, multiShootCount);
                level.addFreshEntity(projectile);
            }

            weapon.hurtAndBreak(this.getDurabilityUse(itemstack), shooter, LivingEntity.getSlotForHand(hand));
            if (weapon.isEmpty()) {
                break;
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        // TODO 添加速度加成等文本
        tooltipComponents.add(BaseTerraBowItem.tooltip(BaseTerraBowItem.ATTACK_DAMAGE_TEXT).append(String.format("%.1f", this.baseDamage)).withColor(0x00FF00));
        if (modifyArrowBuilder.multiShoot > 1) {
            tooltipComponents.add(BaseTerraBowItem.tooltip(BaseTerraBowItem.MAX_COUNT_TEXT).append(String.format("%d", modifyArrowBuilder.multiShoot)).withColor(0x00FF00));
        }
        // 命中效果
        EffectStrategyComponent hitEffect = stack.get(TEDataComponentTypes.EFFECT_STRATEGY);
        if (hitEffect != null) {
            IEffectStrategy.appendDescription(tooltipComponents, hitEffect.effects(), BaseTerraBowItem.tooltip(BaseTerraBowItem.ON_HIT_EFFECTS_TEXT).withColor(0xFF00FF));
        }

        // 蓄满命中效果
        var fullPullHitEffect = stack.get(TEDataComponentTypes.BOW_FULL_CHARGE_EFFECT_STRATEGY);
        if (fullPullHitEffect != null) {
            IEffectStrategy.appendDescription(tooltipComponents, fullPullHitEffect.effects(), BaseTerraBowItem.tooltip(BaseTerraBowItem.BOW_FULL_PULL_ON_HIT_EFFECTS_TEXT).withColor(0xFF00FF));
        }

        // 木箭转化
        if (modifyArrowBuilder.entityTransform != null) {
            tooltipComponents.add(BaseTerraBowItem.tooltip(BaseTerraBowItem.ARROW_TRANSFORM_TEXT).append(modifyArrowBuilder.entityTransform.type().getDescription()).withColor(0xF1b0F4));
        } else {
            Item transformArrow = arrowModifier.getTransformArrow();
            if (transformArrow != null) {
                tooltipComponents.add(BaseTerraBowItem.tooltip(BaseTerraBowItem.ARROW_TRANSFORM_TEXT).append(Component.translatable(transformArrow.getDescriptionId())).withColor(0xF1b0F4));
            }
        }
        ChargedProjectiles chargedprojectiles = stack.get(DataComponents.CHARGED_PROJECTILES);
        if (chargedprojectiles == null || chargedprojectiles.isEmpty()) {
            return;
        }
        List<ItemStack> items = chargedprojectiles.getItems();
        tooltipComponents.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE));
        for (ItemStack stack1 : items) {
            tooltipComponents.add(Component.literal(" ")
                    .append(String.valueOf(stack1.getCount()))
                    .append(" ")
                    .append(stack1.getDisplayName()));
            if (!tooltipFlag.isAdvanced() || !stack1.is(Items.FIREWORK_ROCKET)) {
                continue;
            }
            List<Component> list = Lists.newArrayList();
            Items.FIREWORK_ROCKET.appendHoverText(stack1, context, list, tooltipFlag);
            if (list.isEmpty()) {
                continue;
            }
            list.replaceAll(sibling -> Component.literal("  ").append(sibling).withStyle(ChatFormatting.GRAY));
            tooltipComponents.addAll(list);
        }
    }

    @Override
    public @NotNull Predicate<ItemStack> getSupportedHeldProjectiles(@NotNull ItemStack stack) {
        return (itemStack) -> ammunitionRestrictions.test(itemStack, stack);
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles(@NotNull ItemStack stack) {
        return (itemStack) -> ammunitionRestrictions.test(itemStack, stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        var projectiles = getWeaponItemProjectiles(itemstack);
        if (projectiles == null || projectiles.isEmpty() && !player.getProjectile(itemstack).isEmpty()) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        return stack;
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        int i = this.getUseDuration(stack, entity) - count;
        float f = getPowerForTime(i, stack, entity);
        if (f < 1.0F || isCharged(stack) || !tryLoadProjectiles(entity, stack)) {
            return;
        }
        Level level = entity.level();
        WeaponStorage.of(entity).bowFullPull = true;
        if (level.isClientSide) {
            entity.playSound(ModSoundEvents.BOW_COOLDOWN_RECOVERY.get());
        }
        CrossbowItem.ChargingSounds crossbowitem$chargingsounds = this.getChargingSounds(stack);
        crossbowitem$chargingsounds.end().ifPresent(p_352852_ -> level.playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                p_352852_.value(),
                entity.getSoundSource(),
                1.0F,
                1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
        ));
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        Vector3f vector3f;
        if (target != null) {
            double d0 = target.getX() - shooter.getX();
            double d1 = target.getZ() - shooter.getZ();
            double d2 = Math.sqrt(d0 * d0 + d1 * d1);
            double d3 = target.getY(0.3333333333333333) - projectile.getY() + d2 * 0.2F;
            vector3f = getProjectileShotVector(shooter, new Vec3(d0, d3, d1), angle);
        } else {
            Vec3 vec3 = shooter.getUpVector(1.0F);
            Quaternionf quaternionf = new Quaternionf().setAngleAxis((double) (angle * (float) (Math.PI / 180.0)), vec3.x, vec3.y, vec3.z);
            Vec3 vec31 = shooter.getViewVector(1.0F);
            vector3f = vec31.toVector3f().rotate(quaternionf);
        }

        projectile.shoot((double) vector3f.x(), (double) vector3f.y(), (double) vector3f.z(), velocity, inaccuracy);
        float f = getShotPitch(shooter.getRandom(), index);
        shooter.level().playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, shooter.getSoundSource(), 1.0F, f);
    }

    @Override
    public void onUseTick(@NotNull Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if (level.isClientSide) {
            return;
        }
        float f = (float) (stack.getUseDuration(livingEntity) - count) / (float) getChargeDuration(stack, livingEntity);
        if (f < 0.2F) {
            WeaponStorage.of(livingEntity).bowFullPull = false;
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
        }

        CrossbowItem.ChargingSounds crossbowitem$chargingsounds = this.getChargingSounds(stack);
        if (f >= 0.2F && !this.startSoundPlayed) {
            this.startSoundPlayed = true;
            crossbowitem$chargingsounds.start().ifPresent(p_352849_ -> level.playSound(
                    null,
                    livingEntity.getX(),
                    livingEntity.getY(),
                    livingEntity.getZ(),
                    p_352849_.value(),
                    SoundSource.PLAYERS,
                    0.5F,
                    1.0F
            ));
        }

        if (f >= 0.5F && !this.midLoadSoundPlayed) {
            this.midLoadSoundPlayed = true;
            crossbowitem$chargingsounds.mid().ifPresent(p_352855_ -> level.playSound(
                    null,
                    livingEntity.getX(),
                    livingEntity.getY(),
                    livingEntity.getZ(),
                    p_352855_.value(),
                    SoundSource.PLAYERS,
                    0.5F,
                    1.0F
            ));
        }

        if (f >= 1) {
            livingEntity.stopUsingItem();
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return getChargeDuration(stack, entity) + 3;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(@NotNull ItemStack oldStack, @NotNull ItemStack newStack) {
        return ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public boolean canSwitchWithoutRelease(Player player, ItemStack itemStack) {
        return false;
    }

    @Override
    public BaseTerraArrowItem.ModifyArrowBuilder getModifyArrowBuilder() {
        return modifyArrowBuilder;
    }

    @Override
    public BaseArrowEntity.Builder getArrowModifier() {
        return arrowModifier;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.CROSSBOW;
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
        private float arrowSpeed = 3.15F;
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
         * 并发角度（并发个数时，每个箭的偏移角度）
         */
        private RandomCount concurrentAngle = RandomCount.DEFAULT_EMPTY;
        /**
         * 并发间隔（并发个数时，每个箭的间隔）
         */
        private RandomCount concurrentInterval = RandomCount.DEFAULT_EMPTY;
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

        public Builder burstCount(int burstCount) {
            this.burstCount = RandomCount.create(burstCount);
            return this;
        }

        /**
         * 并发个数（同时射出多少支箭，有一定的散射角度）
         */
        public Builder concurrentCount(RandomCount concurrentCount) {
            this.concurrentCount = concurrentCount;
            return this;
        }

        public Builder concurrentCount(int concurrentCount) {
            this.concurrentCount = RandomCount.create(concurrentCount);
            return this;
        }

        /**
         * 并发角度（并发个数时，每个箭的偏移角度）
         */
        public Builder concurrentAngle(RandomCount concurrentAngle) {
            this.concurrentAngle = concurrentAngle;
            return this;
        }

        public Builder concurrentAngle(float concurrentAngle) {
            this.concurrentAngle = RandomCount.create(concurrentAngle);
            return this;
        }

        /**
         * 并发间隔（并发个数时，每个间隔距离）
         */
        public Builder concurrentInterval(RandomCount concurrentInterval) {
            this.concurrentInterval = concurrentAngle;
            return this;
        }

        public Builder concurrentInterval(float concurrentInterval) {
            this.concurrentInterval = RandomCount.create(concurrentInterval);
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
