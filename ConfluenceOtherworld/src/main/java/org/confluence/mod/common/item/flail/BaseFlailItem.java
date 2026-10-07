package org.confluence.mod.common.item.flail;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.BaseFlailItemRenderer;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

/// 链锤物品的共享输入与状态转换入口。
///
/// 按下主动作键时创建并旋转链锤，松开时投出；再次按下可让投出或回收中的链锤落入停留阶段，
/// 再次松开则收回。
public class BaseFlailItem extends TooltipItem implements GeoItem {
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final Parameters parameters;

    public BaseFlailItem(Parameters parameters, ModRarity rarity) {
        super(new Properties().stacksTo(1), rarity, "");
        this.parameters = parameters;
    }

    public Parameters parameters() {
        return parameters;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    /// 按下主动作键时创建链锤，或让已投出与回收中的链锤落入停留阶段。
    public static void press(Player player, ItemStack stack) {
        if (!(stack.getItem() instanceof BaseFlailItem item)) return;
        Parameters parameters = item.parameters();
        BaseFlailEntity existing = findExistingFlail(player);
        if (existing != null) {
            if (existing.getPhase() == BaseFlailEntity.PHASE_THROWN || existing.getPhase() == BaseFlailEntity.PHASE_RETRACT) {
                existing.playerDrop();
            }
            return;
        }

        spawnFlail(player, stack, parameters);
    }

    /// 松开主动作键时投出旋转中的链锤，或收回停留中的链锤。
    public static void release(Player player, ItemStack stack) {
        if (!(stack.getItem() instanceof BaseFlailItem item)) return;
        Parameters parameters = item.parameters();
        BaseFlailEntity existing = findExistingFlail(player);
        if (existing == null) {
            return;
        }
        if (existing.getPhase() == BaseFlailEntity.PHASE_SPIN) {
            existing.launch(player);
            player.getCooldowns().addCooldown(stack.getItem(), parameters.getCooldown(player));
        } else if (existing.getPhase() == BaseFlailEntity.PHASE_STAY) {
            existing.forceRetract();
        } else if (existing.getPhase()
                == BaseFlailEntity.PHASE_RETRACT) {
            existing.playerDrop();
        }
    }

    /// 查找当前玩家唯一仍在世界中的链锤实体。
    public static @Nullable BaseFlailEntity findExistingFlail(Player player) {
        return player.level().getEntitiesOfClass(BaseFlailEntity.class, player.getBoundingBox().inflate(30.0), entity -> entity.getOwner() == player)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private static @Nullable BaseFlailEntity spawnFlail(Player player, ItemStack stack, Parameters parameters) {
        if (!(stack.getItem() instanceof BaseFlailItem item)) return null;
        EntityType<?> entityType = item.getFlailEntityType(parameters);
        if (entityType == null) return null;
        Entity entity = entityType.create(player.level());
        if (!(entity instanceof BaseFlailEntity flail)) return null;

        if (parameters.behavior().launchMode() || item.isProjectileMode(stack)) {
            flail.initLaunch(player, stack, parameters);
        } else {
            flail.init(player, stack, parameters);
        }
        player.level().addFreshEntity(flail);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), parameters.getSoundEvent(), SoundSource.PLAYERS, 1.0F, 1.0F);
        return flail;
    }

    protected @Nullable EntityType<?> getFlailEntityType(Parameters parameters) {
        return ForgeRegistries.ENTITY_TYPES.getValue(parameters.projType());
    }

    public boolean isAutoSwing() {
        return parameters.behavior().autoSwing();
    }

    public boolean isProjectileMode(ItemStack stack) {
        return false;
    }

    public float getLaunchDamageRatio(ItemStack stack) {
        return 1.0F;
    }

    public boolean canAutoSwing(Player player) {
        if (player.getCooldowns().isOnCooldown(this)) return false;
        if (!(player.getMainHandItem().getItem() instanceof BaseFlailItem item)) return false;
        Parameters parameters = item.parameters();
        int maxActive = parameters.behavior().autoSwingMaxActive();
        if (maxActive <= 0) return true;
        return player.level().getEntitiesOfClass(
                BaseFlailEntity.class,
                player.getBoundingBox().inflate(parameters.maxDistance() + 2.0),
                entity -> entity.getOwner() == player && parameters.equals(entity.parameters())
        ).size() < maxActive;
    }

    public boolean tryAutoSwing(ServerPlayer player, ItemStack stack) {
        if (!(stack.getItem() instanceof BaseFlailItem item)) return false;
        Parameters parameters = item.parameters();
        if (!canAutoSwing(player) || spawnFlail(player, stack, parameters) == null) {
            return false;
        }
        int interval = parameters.getAutoSwingInterval(player);
        if (interval > 0) {
            player.getCooldowns().addCooldown(this, interval);
        }
        return true;
    }

    /// 链锤实体成功造成伤害后的物品扩展点。
    ///
    /// 普通链锤保持空实现；拥有点燃、减益或附属弹幕的链锤通过具体物品子类覆盖。
    /// 该回调只在服务端真实伤害成功后执行一次，不参与组件序列化。
    public void onFlailHit(Player owner, LivingEntity target, BaseFlailEntity flail) {
    }

    /// 持有连枷时始终禁用挖掘
    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    /// 手持时仅由公共 Geo 渲染器绘制连枷手柄。
    ///
    /// 弹头和锁链属于世界中的连枷实体，不能再次作为完整物品贴在玩家手上；物品栏、掉落物
    /// 与展示框仍由物品模型中的二维图标负责。
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BaseFlailItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new BaseFlailItemRenderer();
                }
                return renderer;
            }
        });
    }

    /// 链锤的固定物品参数；由物品持有，不写入 ItemStack 数据组件。
    ///
    /// @param damageFactor 伤害系数，基于玩家对应攻击属性计算
    /// @param spinRadius   旋转阶段绕玩家手部运动的半径
    /// @param spinSpeed    每 tick 增加的旋转弧度
    /// @param throwSpeed   投出时的初始速度
    /// @param maxDistance  自动进入收回阶段的最大距离
    /// @param retractSpeed 收回速度
    /// @param gravity      停留阶段的重力加速度
    /// @param cooldown     投出后的物品冷却 tick
    /// @param bounceFactor 碰撞后的速度保留比例
    /// @param maxBounces   最大反弹次数
    /// @param soundEvent   使用音效
    /// @param projType     链锤实体类型
    /// @param ballTexture  球体模型纹理
    /// @param chainTexture 链条分段纹理
    /// @param behavior     发射与自动挥舞行为
    public record Parameters(
            float damageFactor,
            float spinRadius,
            float spinSpeed,
            float throwSpeed,
            float maxDistance,
            float retractSpeed,
            float gravity,
            int cooldown,
            float bounceFactor,
            int maxBounces,
            ResourceLocation soundEvent,
            ResourceLocation projType,
            ResourceLocation ballTexture,
            ResourceLocation chainTexture,
            Behavior behavior) {

        public static final Supplier<Parameters> MACE = preset("mace", 11.0F, 1.2F, 1.2F, 1.2F, 8.0F, 1.0F, 0.05F, false);
        public static final Supplier<Parameters> FLAMING_MACE = preset("flaming_mace", 11.0F, 1.2F, 1.2F, 1.2F, 8.0F, 1.0F, 0.05F, false);
        public static final Supplier<Parameters> WIND_ANCHOR = preset("wind_anchor", 13.0F, 1.2F, 0.9F, 1.0F, 10.0F, 0.9F, 0.05F, true);
        public static final Supplier<Parameters> GUARDIAN_FLAIL = preset("guardian_flail", 15.0F, 1.3F, 1.3F, 1.3F, 11.0F, 1.2F, 0.04F, true, ModEntities.GUARDIAN_FLAIL_ENTITY.getId());
        public static final Supplier<Parameters> ANCIENT_GUARDIAN_FLAIL = preset("ancient_guardian_flail", 15.0F, 1.3F, 1.3F, 1.3F, 14.0F, 1.2F, 0.04F, true, ModEntities.ANCIENT_GUARDIAN_FLAIL_ENTITY.getId());
        public static final Supplier<Parameters> BALL_O_HURT = preset("ball_o_hurt", 17.0F, 1.2F, 1.5F, 1.3F, 11.0F, 1.0F, 0.2F, true);
        public static final Supplier<Parameters> THE_MEATBALL = preset("the_meatball", 19.0F, 1.2F, 1.5F, 1.3F, 13.0F, 1.0F, 0.2F, true);
        public static final Supplier<Parameters> BLUE_MOON = preset("blue_moon", 29.0F, 1.2F, 1.5F, 1.3F, 20.0F, 1.0F, 0.2F, true);
        public static final Supplier<Parameters> SUNFURY = preset("sunfury", 34.0F, 1.2F, 1.5F, 1.3F, 23.0F, 1.0F, 0.2F, true);
        public static final Supplier<Parameters> DAO_OF_POW = preset("dao_of_pow", 52.0F, 1.2F, 1.5F, 1.3F, 26.0F, 1.0F, 0.2F, true);
        public static final Supplier<Parameters> FLOWER_POWER = preset("flower_power", 67.0F, 1.2F, 1.5F, 1.3F, 26.0F, 1.0F, 0.2F, true, ModEntities.FLOWER_POWER_FLAIL.getId());
        public static final Supplier<Parameters> DRIPPLER_CRIPPLER = preset("drippler_crippler", 55.0F, 1.2F, 1.5F, 1.3F, 20.0F, 1.0F, 0.2F, true, ModEntities.DRIPPLER_CRIPPLER_FLAIL.getId());
        public static final Supplier<Parameters> FLAIRON = preset("flairon", 67.0F, 1.2F, 1.8F, 1.8F, 25.0F, 1.5F, 0.2F, true, ModEntities.FLAIRON_FLAIL.getId());
        public static final Supplier<Parameters> CHAIN_KNIFE = () -> new Parameters(
                6.0F, 1.2F, 1.2F, 1.3F, 10.0F, 1.0F, 0.0F,
                20, 0.3F, 3, ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                ModEntities.CHAIN_KNIFE_FLAIL.getId(), Confluence.asResource("textures/entity/flail/chain_knife.png"),
                Confluence.asResource("textures/block/chain/chain_knife.png"),
                new Behavior(0.3F, true, 13, false, 0, false));
        public static final Supplier<Parameters> CHAIN_GUILLOTINES = launchedPreset(
                "chain_guillotines", 30.0F, 0.3F, 1.5F, 1.3F, 32.0F, 2.6F,
                new Behavior(0.3F, true, 13, true, 0, false));
        public static final Supplier<Parameters> GOLEM_FIST = launchedPreset(
                "golem_fist", 45.0F, 1.0F, 2.5F, 1.75F, 31.25F, 2.5F,
                new Behavior(1.0F, true, 8, true, 1, true));
        public static final Supplier<Parameters> KO_CANNON = launchedPreset(
                "ko_cannon", 20.0F, 0.45F, 2.0F, 0.95F, 17.0F, 2.5F,
                new Behavior(0.45F, true, 0, true, 1, true));
        public static final Supplier<Parameters> ANCHOR = () -> new Parameters(
                35.0F, 1.2F, 1.2F, 1.3F, 100.0F, 1.0F, 0.05F,
                20, 0.3F, 3, ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                ModEntities.ANCHOR_FLAIL.getId(), Confluence.asResource("textures/entity/flail/anchor.png"),
                Confluence.asResource("textures/block/chain/anchor.png"),
                new Behavior(0.8F, true, 13, false, 0, false));

        private static Supplier<Parameters> preset(String id, float damageFactor, float spinRadius, float spinSpeed, float throwSpeed, float maxDistance, float retractSpeed, float gravity, boolean customChain) {
            return preset(id, damageFactor, spinRadius, spinSpeed, throwSpeed, maxDistance, retractSpeed, gravity, customChain, ModEntities.FLAIL_ENTITY.getId());
        }

        private static Supplier<Parameters> preset(
                String id,
                float damageFactor,
                float spinRadius,
                float spinSpeed,
                float throwSpeed,
                float maxDistance,
                float retractSpeed,
                float gravity,
                boolean customChain,
                ResourceLocation entityType) {
            ResourceLocation ballTexture = Confluence.asResource("textures/entity/flail/" + id + ".png");
            ResourceLocation chainTexture = customChain
                    ? Confluence.asResource("textures/block/chain/" + id + ".png")
                    : ResourceLocation.withDefaultNamespace("textures/block/chain.png");
            return () -> new Parameters(damageFactor, spinRadius, spinSpeed, throwSpeed, maxDistance, retractSpeed, gravity, 20, 0.3F, 3, ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), entityType, ballTexture, chainTexture, Behavior.NORMAL);
        }

        private static Supplier<Parameters> launchedPreset(
                String id,
                float damageFactor,
                float knockback,
                float spinSpeed,
                float throwSpeed,
                float maxDistance,
                float retractSpeed,
                Behavior behavior) {
            ResourceLocation ballTexture = Confluence.asResource("textures/entity/flail/" + id + ".png");
            ResourceLocation chainTexture = Confluence.asResource("textures/block/chain/" + id + ".png");
            return () -> new Parameters(
                    damageFactor, 1.2F, spinSpeed, throwSpeed, maxDistance, retractSpeed, 0.0F,
                    20, 0.3F, 3, ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(), ballTexture, chainTexture,
                    new Behavior(knockback, behavior.launchMode(), behavior.autoSwingInterval(), behavior.autoSwing(), behavior.autoSwingMaxActive(), behavior.retractOnHitEntity()));
        }

        public SoundEvent getSoundEvent() {
            return BuiltInRegistries.SOUND_EVENT.get(soundEvent);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o instanceof Parameters other) {
                return damageFactor == other.damageFactor &&
                        spinRadius == other.spinRadius &&
                        spinSpeed == other.spinSpeed &&
                        throwSpeed == other.throwSpeed &&
                        maxDistance == other.maxDistance &&
                        retractSpeed == other.retractSpeed &&
                        gravity == other.gravity &&
                        cooldown == other.cooldown &&
                        bounceFactor == other.bounceFactor &&
                        maxBounces == other.maxBounces &&
                        soundEvent.equals(other.soundEvent) &&
                        projType.equals(other.projType) &&
                        ballTexture.equals(other.ballTexture) &&
                        chainTexture.equals(other.chainTexture) &&
                        behavior.equals(other.behavior);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = Float.hashCode(damageFactor);
            result = 31 * result + Float.hashCode(spinRadius);
            result = 31 * result + Float.hashCode(spinSpeed);
            result = 31 * result + Float.hashCode(throwSpeed);
            result = 31 * result + Float.hashCode(maxDistance);
            result = 31 * result + Float.hashCode(retractSpeed);
            result = 31 * result + Float.hashCode(gravity);
            result = 31 * result + cooldown;
            result = 31 * result + Float.hashCode(bounceFactor);
            result = 31 * result + maxBounces;
            result = 31 * result + soundEvent.hashCode();
            result = 31 * result + projType.hashCode();
            result = 31 * result + ballTexture.hashCode();
            result = 31 * result + chainTexture.hashCode();
            result = 31 * result + behavior.hashCode();
            return result;
        }

        /// 获取修正后的投掷速度（受远程速度属性影响）
        public float getVelocity(LivingEntity living) {
            float velocity = throwSpeed;
            AttributeInstance instance = living.getAttribute(LibAttributes.getRangedVelocity().value());
            if (instance != null) return velocity * (float) instance.getValue();
            return velocity;
        }

        /// 获取修正后的冷却时间（受攻击速度属性影响）
        public int getCooldown(LivingEntity living) {
            AttributeInstance instance = living.getAttribute(Attributes.ATTACK_SPEED);
            if (instance != null) return Math.max(cooldown - (int) (instance.getValue() / 3.0), 0);
            return cooldown;
        }

        /// 获取修正后的挥舞速度（受近战速度属性影响）
        public float getSpinSpeed(LivingEntity living) {
            AttributeInstance instance = living.getAttribute(Attributes.ATTACK_SPEED);
            if (instance != null) return spinSpeed * (float) instance.getValue() / 4.0f;
            return spinSpeed;
        }

        /// 获取自动挥舞间隔；0 表示由射弹回收时机限制射速。
        public int getAutoSwingInterval(LivingEntity living) {
            if (behavior.autoSwingInterval() <= 0) return 0;
            AttributeInstance instance = living.getAttribute(Attributes.ATTACK_SPEED);
            /// 1.20.1 的连枷没有覆盖物品基础攻速；沿用 getSpinSpeed 的 4.0 基准换算倍率。
            float multiplier = instance == null ? 1.0F : (float) instance.getValue() / 4.0F;
            return Math.max(2, Math.round(behavior.autoSwingInterval() / Math.max(0.05F, multiplier)));
        }
    }

    /// 链锤的发射、自动挥舞与命中收回规则。
    public record Behavior(
            float knockback,
            boolean launchMode,
            int autoSwingInterval,
            boolean autoSwing,
            int autoSwingMaxActive,
            boolean retractOnHitEntity) {
        public static final Behavior NORMAL = new Behavior(0.3F, false, 13, false, 0, false);
    }
}
