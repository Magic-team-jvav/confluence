package org.confluence.mod.common.item.crossbow;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.confluence.lib.util.DelayTaskHolder;
import org.confluence.lib.util.EnchantmentUtil;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.ITerraArrowProjectileWeaponItem;
import org.confluence.mod.common.component.RepeaterContents;
import org.confluence.mod.common.entity.projectile.range.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.common.item.tooltipcomponent.RepeaterComponent;
import org.confluence.mod.mixed.IAbstractArrow;
import org.confluence.mod.network.s2c.RepeaterShootingPayloadS2C;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.RepeaterContentsComponentHandler;
import org.confluence.terraentity.api.item.ILeftClickStateItem;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BaseTerraRepeaterItem extends CrossbowItem implements ITerraArrowProjectileWeaponItem<BaseTerraRepeaterItem>, ILeftClickStateItem {
    public static final String ATTACK_SPEED_TEXT = "attribute.name.repeater.attack_speed";
    public static final String KNOCKBACK_TEXT = "attribute.name.repeater.knockback";
    public static final String TORRENT_COUNT_TEXT = "attribute.name.repeater.torrent_count";
    public static final String CONCURRENCY_COUNT_TEXT = "attribute.name.repeater.concurrency_count";
    public static final String FIRING_INTERVAL_TEXT = "attribute.name.repeater.firing_interval";
    public static final String RELOAD_SPEED_TEXT = "attribute.name.repeater.reload_speed";
    public static final String ARROW_CAPACITY_TEXT = "attribute.name.repeater.arrow_capacity";

    public static final String REPEATER_CONTINUOUS_SHOOTING = "repeater.continuous_shooting";
    public static final String REPEATER_SHOOTING = "repeater.shooting";

    private static final ResourceLocation ID = Confluence.asResource(EquipmentSlotGroup.MAINHAND.getSerializedName());
    private static final ChargingSounds DEFAULT_SOUNDS = new ChargingSounds(
            Optional.of(SoundEvents.CROSSBOW_LOADING_START),
            Optional.of(SoundEvents.CROSSBOW_LOADING_MIDDLE),
            Optional.of(SoundEvents.CROSSBOW_LOADING_END)
    );

    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;

    /**
     * 基础伤害
     */
    private final float baseDamage;
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
     */
    private final int baseShootInterval;
    /**
     * 容量
     */
    private final int baseCapacity;
    /**
     * 基础箭矢速度
     */
    private final float baseArrowSpeed;
    /**
     * 连发个数（每次射击会射出多少支箭，每个间隔一帧）
     */
    private final IRandomCount baseBurstCount;
    /**
     * 并发个数（同时射出多少支箭，有一定的散射角度）
     */
    private final IRandomCount baseConcurrentCount;
    /**
     * 并发角度（并发个数时，每个箭的偏移角度）
     */
    private final IRandomCount baseConcurrentAngle;
    /**
     * 并发间隔（并发个数时，每个箭的间隔）
     */
    private final IRandomCount baseConcurrentInterval;
    /**
     * 弹药限制
     */
    private final AmmunitionRestrictions ammunitionRestrictions;

    private final BaseArrowEntity.Builder arrowModifier;
    private final BaseTerraArrowItem.ModifyArrowBuilder modifyArrowBuilder;

    /**
     * 构造连弩
     *
     * @param properties         物品属性
     * @param baseDamage         基础伤害
     * @param modifyArrowBuilder 箭矢修改构建器
     * @param repeaterBuilder    连弩构建器
     */
    public BaseTerraRepeaterItem(Properties properties, float baseDamage, BaseTerraArrowItem.ModifyArrowBuilder modifyArrowBuilder, Builder repeaterBuilder) {
        super(modifyArrowBuilder.buildProperties(properties.stacksTo(1)
                .component(ModDataComponentTypes.REPEATER_CONTENTS, RepeaterContents.fromItems(repeaterBuilder.capacity))
                .attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_KNOCKBACK,
                        new AttributeModifier(ID, repeaterBuilder.knockback, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()
                ))
        );
        this.baseReloadSpeed = repeaterBuilder.reloadSpeed;
        this.baseShootInterval = repeaterBuilder.shootInterval;
        this.baseCapacity = repeaterBuilder.capacity;
        this.baseArrowSpeed = repeaterBuilder.arrowSpeed;
        this.baseBurstCount = repeaterBuilder.burstCount;
        this.baseConcurrentCount = repeaterBuilder.concurrentCount;
        this.ammunitionRestrictions = repeaterBuilder.ammunitionRestrictions;
        this.baseKnockback = repeaterBuilder.knockback;
        this.baseConcurrentAngle = repeaterBuilder.concurrentAngle;
        this.baseConcurrentInterval = repeaterBuilder.concurrentInterval;
        this.baseDamage = baseDamage;
        this.arrowModifier = new BaseArrowEntity.Builder();
        modifyArrowBuilder.modifyArrowBuilder.forEach(m -> m.accept(this.arrowModifier));
        this.modifyArrowBuilder = modifyArrowBuilder;
    }

    /**
     * 构造连弩
     *
     * @param baseDamage            基础伤害
     * @param bowModifyArrowBuilder 箭矢修改构建器
     * @param repeaterBuilder       连弩构建器
     */
    public BaseTerraRepeaterItem(float baseDamage, BaseTerraArrowItem.ModifyArrowBuilder bowModifyArrowBuilder, Builder repeaterBuilder) {
        this(new Properties(), baseDamage, bowModifyArrowBuilder, repeaterBuilder);
    }

    public int getReloadSpeed(LivingEntity shooter, InteractionHand hand) {
        return getReloadSpeed(shooter, shooter.getItemInHand(hand));
    }

    ///  获取装填速度
    public int getReloadSpeed(LivingEntity shooter, ItemStack stack) {
        float f = EnchantmentHelper.modifyCrossbowChargingTime(stack, shooter, baseReloadSpeed / 20f);
        return Mth.floor(f * 20.0F);
    }

    public int getShootInterval(LivingEntity shooter, InteractionHand hand) {
        return getShootInterval(shooter, shooter.getItemInHand(hand));
    }

    /// 获取射击间隔
    public int getShootInterval(LivingEntity shooter, ItemStack stack) {
        return baseShootInterval;
    }

    public int getCapacity() {
        return this.baseCapacity;
    }

    public float getArrowSpeed(LivingEntity shooter, InteractionHand hand) {
        return this.baseArrowSpeed;
    }

    public int getBurstCount(LivingEntity shooter, InteractionHand hand) {
        int processProjectileCount;
        if (shooter.level() instanceof ServerLevel serverLevel) {
            ItemStack itemStack = shooter.getItemInHand(hand);
            int count = EnchantmentHelper.processProjectileCount(serverLevel, itemStack, shooter, 1);
            int level = EnchantmentUtil.getEnchantmentLevel(Enchantments.MULTISHOT, itemStack);
            processProjectileCount = count - level;
        } else {
            processProjectileCount = 0;
        }
        return Math.round(this.baseBurstCount.getCount(shooter.getRandom())) + processProjectileCount;
    }

    public int getConcurrentCount(LivingEntity shooter, InteractionHand hand) {
        return Math.round(this.baseConcurrentCount.getCount(shooter.getRandom()));
    }

    public float getConcurrentAngle(LivingEntity shooter, InteractionHand hand) {
        return this.baseConcurrentAngle.getCount(shooter.getRandom());
    }

    public float getConcurrentInterval(LivingEntity shooter, InteractionHand hand) {
        return this.baseConcurrentInterval.getCount(shooter.getRandom());
    }

    public float getDamage(LivingEntity shooter, InteractionHand hand) {
        return this.baseDamage;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (!(stack.getCapability(Capabilities.ItemHandler.ITEM) instanceof RepeaterContentsComponentHandler handler)) {
            return false;
        }

        if (other.isEmpty()) {
            if (!handler.isEmpty() && action == ClickAction.SECONDARY) {
                ItemStack itemStack = handler.extractItem(0, handler.getStackInSlot(0).getCount(), false);
                boolean isEmpty = itemStack.isEmpty();
                if (!isEmpty) {
                    playRemoveSound(player);
                }
                return !isEmpty && access.set(itemStack);
            }
            return false;
        }

        if (!getAllSupportedProjectiles(stack).test(other)) {
            return false;
        }

        int stackCount = switch (action) {
            case PRIMARY -> other.getCount();
            case SECONDARY -> 1;
        };

        ItemStack copy = other.copyWithCount(stackCount);

        boolean is = handler.insertItem(() -> copy, false);

        if (is) {
            playAerialShootingSound(player);
            other.setCount(other.getCount() - (stackCount - copy.getCount()));
        }

        return is;
    }

    public static boolean isCharged(ItemStack crossbowStack) {
        RepeaterContents contents = crossbowStack.get(ModDataComponentTypes.REPEATER_CONTENTS);
        return contents != null && !contents.isEmpty();
    }

    protected static Vector3f getProjectileShotVector(LivingEntity shooter, Vec3 distance, float angle) {
        Vector3f vector3f = distance.toVector3f().normalize();
        Vector3f vector3f1 = new Vector3f(vector3f).cross(new Vector3f(0.0F, 1.0F, 0.0F));
        if ((double) vector3f1.lengthSquared() <= 1.0E-7) {
            Vec3 vec3 = shooter.getUpVector(1.0F);
            vector3f1 = new Vector3f(vector3f).cross(vec3.toVector3f());
        }

        Vector3f vector3f2 = new Vector3f(vector3f).rotateAxis(Mth.HALF_PI, vector3f1.x, vector3f1.y, vector3f1.z);
        return new Vector3f(vector3f).rotateAxis(angle * Mth.DEG_TO_RAD, vector3f2.x, vector3f2.y, vector3f2.z);
    }

    protected float getShootingPower(RepeaterContentsComponentHandler handler, Player player, InteractionHand hand) {
        return getArrowSpeed(player, hand);
    }

    protected static float getShotPitch(RandomSource random, int index) {
        return index == 0 ? 1.0F : getRandomShotPitch((index & 1) == 1, random);
    }

    protected static float getRandomShotPitch(boolean isHighPitched, RandomSource random) {
        float f = isHighPitched ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    protected ChargingSounds getChargingSounds(ItemStack stack) {
        return EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS).orElse(DEFAULT_SOUNDS);
    }

    public @Nullable RepeaterContentsComponentHandler getHandler(ItemStack stack) {
        if (stack.getCapability(Capabilities.ItemHandler.ITEM) instanceof RepeaterContentsComponentHandler handler) {
            return handler;
        }
        return null;
    }

    private static InteractionHand getHand(Player player, ItemStack itemStack) {
        return player.getMainHandItem() == itemStack ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    private void playRemoveSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playAerialShootingSound(Entity entity) {
        entity.playSound(ModSoundEvents.REPEATER_ITEM_AERIAL_SHOOTING.get());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shootingPerform(Level level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, float velocity, float inaccuracy, @Nullable LivingEntity target, boolean isConsume) {
        if (!(level instanceof ServerLevel serverlevel)) {
            return false;
        }
        List<ItemStack> itemStacks = RepeaterContentsComponentHandler.extractItemList(weapon, 1, !isConsume);
        this.shoot(serverlevel, shooter, hand, weapon, itemStacks, velocity, inaccuracy, true, target);
        if (shooter instanceof ServerPlayer serverplayer) {
            // 告诉客户端玩家已发射
            RepeaterShootingPayloadS2C.sendToClient(serverplayer);
            CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, weapon);
            serverplayer.awardStat(Stats.ITEM_USED.get(weapon.getItem()));
        }

        return true;
    }

    protected boolean shootingPerformContinuousShooting(Player player, ItemStack itemStack, int countCount, DelayTaskHolder delayTaskHolder, InteractionHand hand, Level level, float shootingPower) {
        if (delayTaskHolder.containsTask(hand, REPEATER_CONTINUOUS_SHOOTING)) {
            return false;
        }
        delayTaskHolder.addTask(hand, REPEATER_CONTINUOUS_SHOOTING, DelayTaskHolder.createTaskBilder()
                .repeatCount(countCount - 1)
                .removedTick(1)
                .resultRun((tick, maxTick, task) -> {
                    if (!shootingPerform(level, player, hand, itemStack, shootingPower, 1.0F, null, true)) {
                        task.remove();
                    }
                    return 0;
                }).build());
        return true;
    }

    @Override
    protected void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
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
            float projectileVelocity = itemstack.is(Items.FIREWORK_ROCKET) ? velocity * 0.507f : velocity;
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
                BaseTerraBowItem.processArrowBaseEffects(shooter, hand, weapon, projectile, itemstackIndex, multiShootCount);
                if (projectile instanceof IAbstractArrow abstractArrow) {
                    abstractArrow.confluence$setDamageNotAffectedBySpeedBonus(true);
                    if (itemstackIndex > 0 || projectileIndex > 0) {
                        abstractArrow.confluence$setDisappearingOnGround(true);
                    }
                }
                level.addFreshEntity(projectile);
            }

            weapon.hurtAndBreak(this.getDurabilityUse(itemstack), shooter, LivingEntity.getSlotForHand(hand));
            if (weapon.isEmpty()) {
                break;
            }
        }
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
            Quaternionf quaternionf = new Quaternionf().setAngleAxis(angle * Mth.DEG_TO_RAD, vec3.x, vec3.y, vec3.z);
            Vec3 vec31 = shooter.getViewVector(1.0F);
            vector3f = vec31.toVector3f().rotate(quaternionf);
        }

        projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), velocity, inaccuracy);
        float f = getShotPitch(shooter.getRandom(), index);
        shooter.level().playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, shooter.getSoundSource(), 1.0F, f);
    }

    @Override
    public void onLeftClick(Player player, ItemStack itemStack) {
        var handler = getHandler(itemStack);
        if (handler == null) {
            return;
        }

        if (handler.isEmpty()) {
            playAerialShootingSound(player);
            return;
        }

        InteractionHand hand = getHand(player, itemStack);
        DelayTaskHolder delayTaskHolder = DelayTaskHolder.of(player);
        Level level = player.level();
        if (!delayTaskHolder.containsTask(hand).isEmpty()) {
            return;
        }

        int countCount = getBurstCount(player, hand);
        float shootingPower = getShootingPower(handler, player, hand);
        if (countCount > 1) {
            shootingPerformContinuousShooting(player, itemStack, countCount, delayTaskHolder, hand, level, shootingPower);
        }

        delayTaskHolder.addTask(hand, REPEATER_SHOOTING, DelayTaskHolder.createTaskBilder()
                .repeatCount(-1)
                .removedTick(getShootInterval(player, hand))
                .resultRun((tick, maxTick, task) -> {
                    var projectiles = getHandler(itemStack);
                    if (projectiles == null || projectiles.isEmpty()) {
                        task.remove();
                        return 0;
                    }
                    if (countCount > 1) {
                        shootingPerformContinuousShooting(player, itemStack, countCount, delayTaskHolder, hand, level, shootingPower);
                    }
                    if (!shootingPerform(level, player, hand, itemStack, shootingPower, 1.0F, null, true)) {
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
        delayTaskHolder.removeTask(hand, REPEATER_CONTINUOUS_SHOOTING);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        var projectiles = getHandler(itemstack);
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
        if (f < 1.0F || !tryLoadProjectiles(entity, stack)) {
            return;
        }
        Level level = entity.level();
        WeaponStorage.of(entity).bowFullPull = true;
        if (level.isClientSide) {
            entity.playSound(ModSoundEvents.BOW_COOLDOWN_RECOVERY.get());
        }
        ChargingSounds crossbowitem$chargingsounds = this.getChargingSounds(stack);
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

    protected boolean tryLoadProjectiles(LivingEntity shooter, ItemStack weapon) {
        if (shooter.level().isClientSide) {
            return true;
        }
        return RepeaterContentsComponentHandler.insertItem(weapon,
                () -> shooter.getProjectile(weapon),
                shooter instanceof Player player && player.isCreative());
    }

    protected float getPowerForTime(int timeLeft, ItemStack stack, LivingEntity shooter) {
        float f = (float) timeLeft / (float) getReloadSpeed(shooter, stack);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if (level.isClientSide) {
            return;
        }
        float f = (float) (stack.getUseDuration(livingEntity) - count) / (float) getReloadSpeed(livingEntity, stack);
        if (f < 0.2F) {
            WeaponStorage.of(livingEntity).bowFullPull = false;
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
        }

        ChargingSounds crossbowitem$chargingsounds = this.getChargingSounds(stack);
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
        return getReloadSpeed(entity, stack) + 3;
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles(ItemStack stack) {
        return (itemStack) -> ammunitionRestrictions.test(itemStack, stack);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles(ItemStack stack) {
        return (itemStack) -> ammunitionRestrictions.test(itemStack, stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
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
    public void appendHoverText(ItemStack weapon, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        BaseTerraArrowItem.addDamageHoverText(tooltipComponents, modifyArrowBuilder, baseDamage);
        // 箭矢容量
        tooltipComponents.add(tooltip(ARROW_CAPACITY_TEXT).append(getTotalSize(weapon).getItemsTotalCount() + "/" + baseCapacity).withStyle(ChatFormatting.DARK_GRAY));
        // 箭矢速度
        tooltipComponents.add(tooltip(ATTACK_SPEED_TEXT).append(String.valueOf(baseArrowSpeed)).withStyle(ChatFormatting.DARK_GRAY));
        // 击退
        tooltipComponents.add(tooltip(KNOCKBACK_TEXT).append(String.valueOf(baseKnockback)).withStyle(ChatFormatting.DARK_GRAY));
        if (!IRandomCount.is(baseBurstCount, 1)) {
            // 连发个数
            tooltipComponents.add(tooltip(TORRENT_COUNT_TEXT).append(IRandomCount.getString(baseBurstCount)).withStyle(ChatFormatting.DARK_GRAY));
        }
        if (!IRandomCount.is(baseConcurrentCount, 1)) {
            // 并发个数
            tooltipComponents.add(tooltip(CONCURRENCY_COUNT_TEXT).append(IRandomCount.getString(baseConcurrentCount)).withStyle(ChatFormatting.DARK_GRAY));
        }
        // 射击间隔
        tooltipComponents.add(tooltip(FIRING_INTERVAL_TEXT).append(String.valueOf(baseShootInterval / 20f)).withStyle(ChatFormatting.DARK_GRAY));
        // 装填速度
        tooltipComponents.add(tooltip(RELOAD_SPEED_TEXT).append(String.valueOf(baseReloadSpeed / 20f)).withStyle(ChatFormatting.DARK_GRAY));
        BaseTerraArrowItem.addHitEffectHoverText(weapon, tooltipComponents);
        BaseTerraArrowItem.addFullPullHitEffectHoverText(weapon, tooltipComponents);
        BaseTerraArrowItem.addEntityTransformHoverText(tooltipComponents, modifyArrowBuilder, arrowModifier);
    }

    private static RepeaterContents getTotalSize(ItemStack weapon) {
        return weapon.getComponents().getOrDefault(ModDataComponentTypes.REPEATER_CONTENTS.get(), RepeaterContents.EMPTY);
    }

    private static MutableComponent tooltip(String text) {
        return Component.translatable(text).append(": ");
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.ofNullable(stack.get(ModDataComponentTypes.REPEATER_CONTENTS)).map(RepeaterComponent::new);
    }

    public float getBaseKnockback() {
        return baseKnockback;
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
        private int reloadSpeed = Mth.floor(1.25F * 20);
        /**
         * 射击间隔
         */
        private int shootInterval = 5;
        /**
         * 容量
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
        private IRandomCount burstCount = IRandomCount.DEFAULT;
        /**
         * 并发个数（同时射出多少支箭，有一定的散射角度）
         */
        private IRandomCount concurrentCount = IRandomCount.DEFAULT;
        /**
         * 并发角度（并发个数时，每个箭的偏移角度）
         */
        private IRandomCount concurrentAngle = IRandomCount.DEFAULT_EMPTY;
        /**
         * 并发间隔（并发个数时，每个箭的间隔）
         */
        private IRandomCount concurrentInterval = IRandomCount.DEFAULT_EMPTY;
        /**
         * 弹药限制
         */
        private AmmunitionRestrictions ammunitionRestrictions = DEFAULT_AMMUNITION_RESTRICTIONS;

        public Builder reloadTick(int reloadSpeed) {
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

        public Builder burstCount(IRandomCount burstCount) {
            this.burstCount = burstCount;
            return this;
        }

        public Builder burstCount(int burstCount) {
            this.burstCount = IRandomCount.create(burstCount);
            return this;
        }

        public Builder concurrentCount(IRandomCount concurrentCount) {
            this.concurrentCount = concurrentCount;
            return this;
        }

        public Builder concurrentCount(int concurrentCount) {
            this.concurrentCount = IRandomCount.create(concurrentCount);
            return this;
        }

        public Builder concurrentAngle(IRandomCount concurrentAngle) {
            this.concurrentAngle = concurrentAngle;
            return this;
        }

        public Builder concurrentAngle(float concurrentAngle) {
            this.concurrentAngle = IRandomCount.create(concurrentAngle);
            return this;
        }

        public Builder concurrentInterval(IRandomCount concurrentInterval) {
            this.concurrentInterval = concurrentAngle;
            return this;
        }

        public Builder concurrentInterval(float concurrentInterval) {
            this.concurrentInterval = IRandomCount.create(concurrentInterval);
            return this;
        }

        public Builder ammunitionRestrictions(AmmunitionRestrictions ammunitionRestrictions) {
            this.ammunitionRestrictions = ammunitionRestrictions;
            return this;
        }

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
