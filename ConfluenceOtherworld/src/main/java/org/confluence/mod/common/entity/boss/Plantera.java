package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.confluence.mod.common.entity.projectile.PlanteraProjectile;
import org.confluence.mod.common.entity.projectile.PlanteraProjectile.Variant;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.common.TreasureBagItem;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.OverworldUtils;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class Plantera extends BaseBoss implements GeoEntity {
    // 本体、花钩和藤蔓共用的模型倍率，同时用于注册碰撞箱。
    public static final float MODEL_SCALE = 1.5F;
    // 六根藤蔓沿花萼内侧分布，根部与花冠使用同一渲染姿态。
    private static final float CALYX_ATTACHMENT_RADIUS = 0.5F;
    // 本体最多同时伸出的花钩数量，每个花钩独占一个抓点。
    public static final int HOOK_COUNT = 6;
    // 二阶段分配在本体上的触手槽位数。
    static final int BODY_TENTACLE_COUNT = 8;
    // 每个花钩在二阶段能够附着的触手数。
    static final int TENTACLES_PER_HOOK = 3;
    // 本体槽位排在花钩槽位之前，死亡标记也使用同一套下标。
    static final int TENTACLE_COUNT = BODY_TENTACLE_COUNT + HOOK_COUNT * TENTACLES_PER_HOOK;
    /// 钩爪搜索与系绳最大长度均为 48 方块；移动速度和加速度单位为方块/tick。
    private static final double HOOK_SEARCH_RANGE = 48.0;
    /// 藤钩与本体之间允许的最大牵引长度，单位格。
    private static final double HOOK_TETHER_LENGTH = 48.0;
    /// 一阶段移动速度上限，单位格/tick。
    public static final double PHASE_ONE_MOVE_SPEED = 0.13;
    /// 二阶段移动速度上限，单位格/tick。
    public static final double PHASE_TWO_MOVE_SPEED = 0.26;
    /// 激怒状态的移动速度上限，单位格/tick。
    private static final double ENRAGED_MOVE_SPEED = 0.2;
    /// 追踪目标时每 tick 的速度调整幅度。
    private static final double MOVE_ACCELERATION = 0.1;
    /// 被击毁的触手开始重建前的基础等待时间，单位 tick。
    private static final int TENTACLE_REBUILD_BASE_COOLDOWN = 100;

    /// 每次寻找可用钩索锚点的随机尝试次数。
    private static final int HOOK_SEARCH_ATTEMPTS = 18;
    // 已有至少两个抓点时，远端花钩可以轮流换位。没有足够墙面的区域仍能逐步推进。
    private static final int MINIMUM_ANCHORED_HOOKS = 2;
    // 为空闲花钩重新分配抓点的间隔（tick）。
    private static final int HOOK_CYCLE_TICKS = 50;
    // 相邻花钩错开出手，让先落下的花钩先承担牵引。
    private static final int HOOK_CAST_SPACING = HOOK_CYCLE_TICKS / HOOK_COUNT;
    // 在抓取周期中点收回远端花钩，使其与向外伸展错开。
    private static final int HOOK_RETRACT_TICK = HOOK_CYCLE_TICKS / 2;
    /// 钩索从距离本体多少格处开始搜索。
    private static final double HOOK_SEARCH_START = 4.0;
    /// 二阶段移动加速度倍率。
    private static final double PHASE_TWO_ACCELERATION_MULTIPLIER = 1.15;

    /// 钩索拉扯移动时每 tick 保留的旧速度比例。
    private static final double HOOKED_MOVE_MOMENTUM = 0.85D;
    // 泰拉每秒 60 刻，MC 每秒 20 刻。普攻计数仍按泰拉刻计算。
    private static final int TERRARIA_TICKS_PER_GAME_TICK = 3;
    // 泰拉计数超过 80 点时开火；血量低于 90%、80%、70%、60% 各多推进一步。
    private static final int BASIC_FIRE_THRESHOLD = 80;
    // 毒种子额外扣除的普攻计时点数，实际等待随当前射速变化。
    private static final int POISON_COOLDOWN_COST = 30;
    // 刺球额外扣除的普攻计时点数，限制连续弹跳物的密度。
    private static final int THORN_COOLDOWN_COST = 120;
    // 低于此生命比例时普攻才会抽取毒种子或刺球。
    private static final float SPECIAL_SEED_THRESHOLD = 0.8F;
    // 种子出膛速度（格/tick）。
    private static final double SEED_SPEED = 1.1;
    // 刺球出膛速度（格/tick）。
    private static final double THORN_SPEED = 0.85;
    // 转阶段暂停战斗的 tick 数，血条帧率由客户端样式控制。
    private static final int TRANSFORM_TICKS = 24;
    // 一阶段两次特殊招式之间的追击时间（tick）。
    private static final int PHASE_ONE_RECOVERY = 95;
    // 二阶段选取下个招式前的近战追击时间（tick）。
    private static final int PHASE_TWO_RECOVERY = 58;
    // 花粉释放后记录命中的窗口（tick）。
    private static final int POLLEN_MISS_TICKS = 40;
    // 花粉未命中且联动抽取成功后，起风前的等待时间（tick）。
    private static final int POLLEN_COMBO_TICKS = 60;
    // 孢子释放后记录命中的窗口（tick）。
    private static final int SPORE_MISS_TICKS = 100;
    // 孢子未命中且联动抽取成功后，连射前的等待时间（tick）。
    private static final int SPORE_COMBO_TICKS = 160;
    // 一次喷泉向周围抛出的孢子数量。
    private static final int SPORE_COUNT = 20;
    // 独立连射或传奇联动的弹数。
    private static final int BARRAGE_SHOTS = 20;
    // 非传奇联动的弹数，为完整连射的三成。
    private static final int COMBO_SHOTS = 6;
    // 连射相邻两发的间隔（tick）。
    private static final int BARRAGE_INTERVAL = 3;
    // 同一目标连续无伤多少 tick 后允许强化攻击。
    private static final int UNHURT_TICKS = 400;
    // 成功见光回血后的冷却（tick）。
    private static final int SUNLIGHT_HEAL_COOLDOWN = 3600;
    // 寻光被打断后的冷却（tick）。
    private static final int SUNLIGHT_INTERRUPT_COOLDOWN = 6000;
    // 打断窗口内所需的有效受击次数。
    private static final int SUNLIGHT_HITS_TO_INTERRUPT = 10;
    // 传奇寻光打断窗口的长度（tick）。
    private static final int SUNLIGHT_INTERRUPT_TICKS = 120;
    // 寻光时向上移动的速度（格/tick）。
    private static final double SUNLIGHT_SPEED = 0.38;
    // 每 tick 的偏航与俯仰上限，停下连射时仍持续跟踪目标。
    private static final float TURN_DEGREES = 10;
    // 花冠与目标方向相差不超过 12 度才允许发射定向弹幕。
    private static final double FIRING_ALIGNMENT = Math.cos(Math.toRadians(12));
    // 每段冲刺锁定方向前的最短准备时间（tick）。
    private static final int DASH_WINDUP_TICKS = 10;
    // 每段冲刺持续时间（tick）。
    private static final int DASH_TICKS = 12;
    // 冲刺基础速度（格/tick），狂暴时另乘冲刺倍率。
    private static final double DASH_SPEED = 1.25;
    // 一次扇形撒播的落地种子数量。
    private static final int PLANTING_SEEDS = 5;
    // 浓花粉的伤害半径（格）。
    private static final double POLLEN_RADIUS = 5.6;
    // 被风吹散后花粉的伤害半径（格）。
    private static final double THIN_POLLEN_RADIUS = 8.4;
    // 浓花粉开始被吹向目标时的速度（格/tick）。
    private static final double POLLEN_WIND_SPEED = 0.8;
    // 花粉每 tick 保留的风速比例，越小越早停下。
    private static final double POLLEN_WIND_DECAY = 0.9;
    // 每团花粉的寿命（tick）。
    private static final int POLLEN_LIFETIME = 150;
    // 同一团花粉两次伤害检测之间的间隔（tick）。
    private static final int POLLEN_INTERVAL = 10;
    // 浓花粉的单次基础伤害，稀释后减半。
    private static final float POLLEN_DAMAGE = 6;
    // 浓花粉命中时施加减速、失明和中毒的时长（tick）。
    private static final int POLLEN_EFFECT_TICKS = 65;
    // 强化期间每次有效命中触发定身的概率。
    private static final float ROOT_CHANCE = 0.1F;
    // 基础定身时长（tick），传奇翻倍。
    private static final int ROOT_TICKS = 60;
    private static final ResourceLocation POLLEN_PARTICLE = Confluence.asResource("plantera_pollen_cloud_1");

    private static final String PHASE_TAG = "Phase";
    private static final String ENRAGED_TICKS_TAG = "EnragedTicks";
    private static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(Plantera.class, EntityDataSerializers.INT);

    public static final ResourceLocation PHASE_TWO_BAR = Confluence.asResource("plantera_phase2");
    public static final ResourceLocation TRANSITION_BAR = Confluence.asResource("plantera_transition");
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final List<PollenCloud> pollen = new ArrayList<>();
    private CombatState combatState = CombatState.RECOVER;
    private LivingEntity combatTarget;
    private float targetHealth;
    private float targetAbsorption;
    private int unhurtTicks;
    private int actionTicks;
    private int basicClock;
    private int sunlightCooldown;
    private final Deque<Integer> sunlightHits = new ArrayDeque<>();
    private int comboTicks;
    private boolean combo;
    private int pollenCast;
    private int comboPollenCast;
    private boolean skillHit;
    private int dashes;
    private boolean dashHit;
    private boolean dashCanSplit;
    private Vec3 dashVelocity = Vec3.ZERO;
    private Temperament appliedTemperament;
    private final PlanteraHook[] hooks = new PlanteraHook[HOOK_COUNT];
    private final PlanteraTentacle[] tentacles = new PlanteraTentacle[TENTACLE_COUNT];
    private final int[] regenerationTicks = new int[HOOK_COUNT + 1];
    private long destroyedTentacles;
    private int enragedTicks;

    public Plantera(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        setNoGravity(true);
        this.xpReward = 2000;
    }

    /// 世纪之花的位置由钩爪约束和阶段移动共同决定。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    protected ResourceLocation getBossBarStyle() {
        if (combatState == CombatState.TRANSFORM) return TRANSITION_BAR;
        return getPhase() == 0 ? super.getBossBarStyle() : PHASE_TWO_BAR;
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.GREEN;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PHASE, 0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(20);
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (!isAlive()) return;

        if (!level().isClientSide) {
            if (tickCount == 1 || tickCount % 20 == 0) ensureHooks();
            if (enragedTicks > 0) {
                enragedTicks--;
            }

            LivingEntity target = getTarget();
            if (target != null && (!OverworldUtils.isJungle(level().getBiome(target.blockPosition()))
                    || !OverworldUtils.belowSurface(target.getY())
                    || OverworldUtils.inUnderworld(target.level().dimension(), target.getY()))) {
                enrage();
            }

            // 第二阶段持续维护固定槽位的附着触手。
            if (getPhase() >= 1) {
                if (tickCount % 5 == 0) ensureTentacles();
                if (isExpert()) tickTentacleRegrowth();
            }

            updateHookCycle();
            updateStateAttributes();
            if (!tickCombat(target)) updateMovement();

        }
    }

    @Override
    protected void updateHealthPhase() {
        if (getPhase() == 0 && getHealth() / getMaxHealth() <= 0.5F) {
            entityData.set(DATA_PHASE, 1);
            begin(CombatState.TRANSFORM);
            broadcastPhaseTransition();
        }
    }

    public int getPhase() {
        return entityData.get(DATA_PHASE);
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (!super.hurt(source, damage)) return false;
        if (!level().isClientSide && isAlive() && source.getEntity() instanceof Player)
            onSunlightHit();
        return true;
    }

    public void plantTentacle(Vec3 position) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || getPhase() == 0) return;
        PlanteraTentacle tentacle = BossEntities.PLANTERA_TENTACLE.get().create(server);
        if (tentacle == null) return;
        tentacle.setPos(position);
        if (!tentacle.setMaster(this, position) || !server.addFreshEntity(tentacle))
            tentacle.discard();
    }

    private void onSunlightHit() {
        if (combatState != CombatState.SUNLIGHT) return;
        while (!sunlightHits.isEmpty() && actionTicks - sunlightHits.getFirst() >= SUNLIGHT_INTERRUPT_TICKS) {
            sunlightHits.removeFirst();
        }
        sunlightHits.addLast(actionTicks);
        if (sunlightHits.size() >= SUNLIGHT_HITS_TO_INTERRUPT) {
            sunlightCooldown = SUNLIGHT_INTERRUPT_COOLDOWN;
            begin(CombatState.RECOVER);
        }
    }

    public void reportSporeHit(LivingEntity victim) {
        if (victim instanceof Player && combatState == CombatState.SPORES) skillHit = true;
        applyRoot(victim);
    }

    public void reportAttackHit(LivingEntity victim) {
        applyRoot(victim);
    }

    private boolean tickCombat(LivingEntity currentTarget) {
        if (!(level() instanceof ServerLevel level)) return false;
        updateTarget(currentTarget);
        if (sunlightCooldown > 0) sunlightCooldown--;
        pollen.removeIf(cloud -> cloud.tick(level));
        Vec3 facing = currentTarget == null || combatState == CombatState.SUNLIGHT ? getDeltaMovement()
                : combatState == CombatState.DASH ? dashVelocity : aim(currentTarget);
        LibEntityUtils.turnEntityTowards(this, facing, TURN_DEGREES, TURN_DEGREES);
        actionTicks++;
        if (combatState == CombatState.TRANSFORM) {
            if (actionTicks >= TRANSFORM_TICKS) begin(CombatState.RECOVER);
            return holdPosition();
        }
        if (combatTarget == null || !combatTarget.isAlive()) {
            if (combatState != CombatState.RECOVER) begin(CombatState.RECOVER);
            return false;
        }
        return switch (combatState) {
            case RECOVER -> recover(level);
            case POLLEN -> waitForCombo(true);
            case SPORES -> waitForCombo(false);
            case BARRAGE -> barrage();
            case SUNLIGHT -> seekSunlight(level);
            case DASH_WINDUP -> windUpDash();
            case DASH -> dash();
            case SPLITTING_SEED -> {
                if (alignedForAttack()) {
                    fire(Variant.SPLITTING_SEED, getLookAngle().scale(SEED_SPEED * 0.8));
                    begin(CombatState.RECOVER);
                }
                yield holdPosition();
            }
            case PLANT_SEEDS -> plantSeeds();
            case LEAF -> throwLeaf();
            case TRANSFORM -> holdPosition();
        };
    }

    private void updateTarget(LivingEntity current) {
        if (current == null || current != combatTarget || current.getHealth() < targetHealth
                || current.getAbsorptionAmount() < targetAbsorption) unhurtTicks = 0;
        else unhurtTicks++;
        combatTarget = current;
        if (current != null) {
            targetHealth = current.getHealth();
            targetAbsorption = current.getAbsorptionAmount();
        }
    }

    private boolean recover(ServerLevel level) {
        if (getPhase() == 0) {
            combo = false;
            if (isLegendary() && sunlightCooldown == 0 && unhurtTicks >= UNHURT_TICKS && LibDateUtils.isDay(level)) {
                sunlightHits.clear();
                begin(CombatState.SUNLIGHT);
                return true;
            }
            tickBasic();
            if (actionTicks < PHASE_ONE_RECOVERY) return false;
            switch (getRandom().nextInt(3)) {
                case 0 -> {
                    releasePollen();
                    begin(CombatState.POLLEN);
                }
                case 1 -> {
                    releaseSpores();
                    begin(CombatState.SPORES);
                }
                default -> begin(CombatState.BARRAGE);
            }
            return holdPosition();
        }

        hitWithBody();
        if (actionTicks < PHASE_TWO_RECOVERY) return false;
        switch (getRandom().nextInt(5)) {
            case 0 -> startDash(1);
            case 1 -> begin(CombatState.PLANT_SEEDS);
            case 2 -> startDash(3);
            case 3 -> {
                releaseSpores();
                begin(CombatState.RECOVER);
            }
            default -> begin(CombatState.LEAF);
        }
        return false;
    }

    private void tickBasic() {
        float health = getHealth() / getMaxHealth();
        int acceleration = 1 + (isExpert() ? 1 : 0) + (isFtw() ? 1 : 0) + (isEnraged() ? 3 : 0);
        if (health < 0.9F) acceleration++;
        if (health < 0.8F) acceleration++;
        if (health < 0.7F) acceleration++;
        if (health < 0.6F) acceleration++;
        // 按泰拉的离散步长推进，越过阈值的那一刻清零并继续推进本刻剩余步数。
        for (int step = 0; step < TERRARIA_TICKS_PER_GAME_TICK; step++) {
            basicClock += acceleration;
            if (basicClock <= BASIC_FIRE_THRESHOLD) continue;
            int cooldownCost = shootBasic(combatTarget);
            if (cooldownCost < 0) {
                basicClock = BASIC_FIRE_THRESHOLD;
                return;
            }
            basicClock = -cooldownCost;
        }
    }

    private int shootBasic(LivingEntity victim) {
        if (getPhase() != 0 || getLookAngle().dot(aim(victim)) < FIRING_ALIGNMENT) return -1;
        Variant variant = Variant.SEED;
        int cooldownCost = 0;
        if (getHealth() < getMaxHealth() * SPECIAL_SEED_THRESHOLD) {
            if (getRandom().nextInt(isExpert() ? 2 : 4) == 0) {
                variant = Variant.POISON_SEED;
                cooldownCost = POISON_COOLDOWN_COST;
            } else if (getRandom().nextInt(isExpert() ? 6 : 8) == 0) {
                variant = Variant.THORN_BALL;
                cooldownCost = THORN_COOLDOWN_COST;
            }
        }
        double speed = variant == Variant.THORN_BALL ? THORN_SPEED : SEED_SPEED;
        fire(variant, getLookAngle().scale(speed));
        return cooldownCost;
    }

    private boolean waitForCombo(boolean cloud) {
        int miss = cloud ? POLLEN_MISS_TICKS : SPORE_MISS_TICKS;
        int delay = cloud ? POLLEN_COMBO_TICKS : SPORE_COMBO_TICKS;
        if (actionTicks == miss) {
            boolean hit = cloud ? pollen.stream().anyMatch(p -> p.cast == pollenCast && p.hitPlayer) : skillHit;
            float chance = cloud ? difficultyChance(0.35F, 0.50F, 0.75F) : difficultyChance(0.10F, 0.30F, 0.50F);
            if (!hit && getRandom().nextFloat() < chance) comboTicks = delay;
            else begin(CombatState.RECOVER);
        }
        if (comboTicks > 0 && actionTicks >= miss + comboTicks) {
            combo = true;
            comboPollenCast = cloud ? pollenCast : 0;
            begin(CombatState.BARRAGE);
        }
        return cloud && actionTicks < miss && holdPosition();
    }

    private float difficultyChance(float classic, float expert, float master) {
        return LibUtils.switchByDifficulty(level(), classic, expert, master);
    }

    private boolean isLegendary() {
        return LibUtils.switchByDifficulty(level(), false, false, false, true);
    }

    private boolean barrage() {
        if (!alignedForAttack()) {
            actionTicks--;
            return holdPosition();
        }
        int shots = combo && !isLegendary() ? COMBO_SHOTS : BARRAGE_SHOTS;
        if ((actionTicks - 1) % BARRAGE_INTERVAL == 0) {
            shootBasic(combatTarget);
            if (actionTicks == 1 && combo && comboPollenCast != 0) {
                for (PollenCloud cloud : pollen) {
                    if (cloud.cast == comboPollenCast) cloud.blow();
                }
            }
        }
        if (actionTicks >= shots * BARRAGE_INTERVAL) begin(CombatState.RECOVER);
        return holdPosition();
    }

    private boolean seekSunlight(ServerLevel level) {
        if (!LibDateUtils.isDay(level) || getY() >= level.getMaxBuildHeight()) {
            sunlightCooldown = SUNLIGHT_INTERRUPT_COOLDOWN;
            begin(CombatState.RECOVER);
            return holdPosition();
        }
        if (level.canSeeSky(blockPosition())) {
            setHealth(getMaxHealth());
            sunlightCooldown = SUNLIGHT_HEAL_COOLDOWN;
            begin(CombatState.RECOVER);
            return holdPosition();
        }
        if ((actionTicks - 1) % 30 == 0) releasePollen();
        setDeltaMovement(0, SUNLIGHT_SPEED, 0);
        return true;
    }

    private void releasePollen() {
        Vec3 center = getBoundingBox().getCenter();
        int cast = ++pollenCast;
        for (int index = 0; index < 8; index++) {
            double angle = index * Mth.TWO_PI / 8;
            pollen.add(new PollenCloud(cast, center.add(Math.cos(angle) * 3.2, 0, Math.sin(angle) * 3.2)));
        }
    }

    private void releaseSpores() {
        for (int index = 0; index < SPORE_COUNT; index++) {
            double angle = getRandom().nextDouble() * Mth.TWO_PI;
            double speed = 0.08 + getRandom().nextDouble() * 0.30;
            fire(Variant.SPORE, new Vec3(Math.cos(angle) * speed, 0.25 + getRandom().nextDouble() * 0.43, Math.sin(angle) * speed));
        }
    }

    private void startDash(int count) {
        dashes = count;
        dashHit = false;
        dashCanSplit = count == 1;
        begin(CombatState.DASH_WINDUP);
    }

    private boolean windUpDash() {
        if (actionTicks >= DASH_WINDUP_TICKS && alignedForAttack()) {
            dashVelocity = aim(combatTarget).scale(DASH_SPEED * (isEnraged() ? 1.4 : 1));
            begin(CombatState.DASH);
        }
        return holdPosition();
    }

    private boolean dash() {
        dashHit |= hitWithBody();
        if (actionTicks >= DASH_TICKS) {
            if (--dashes > 0) begin(CombatState.DASH_WINDUP);
            else {
                begin(!dashHit && dashCanSplit ? CombatState.SPLITTING_SEED : CombatState.RECOVER);
            }
            return holdPosition();
        }
        setDeltaMovement(dashVelocity);
        return true;
    }

    private boolean hitWithBody() {
        var hits = SweptContactAttack.findTargets(this, 0, SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                entity -> entity == combatTarget && LibEntityUtils.canMobHit(this, entity));
        if (hits.isEmpty() || !doHurtTarget(combatTarget)) return false;
        applyRoot(combatTarget);
        return true;
    }

    private boolean plantSeeds() {
        if (!alignedForAttack()) {
            actionTicks--;
            return holdPosition();
        }
        for (int index = 0; index < PLANTING_SEEDS; index++) {
            double spread = (index - (PLANTING_SEEDS - 1) * 0.5) * 0.16;
            fire(Variant.PLANTING_SEED, getLookAngle().yRot((float) spread).scale(0.65).add(0, 0.14, 0));
        }
        begin(CombatState.RECOVER);
        return holdPosition();
    }

    private boolean throwLeaf() {
        if (!alignedForAttack()) {
            actionTicks--;
            return holdPosition();
        }
        fire(Variant.LEAF, getLookAngle().yRot(0.65F).scale(0.75));
        begin(CombatState.RECOVER);
        return holdPosition();
    }

    private boolean alignedForAttack() {
        return getLookAngle().dot(aim(combatTarget)) >= FIRING_ALIGNMENT;
    }

    private Vec3 aim(LivingEntity victim) {
        return victim.getBoundingBox().getCenter().subtract(getBoundingBox().getCenter()).normalize();
    }

    private void fire(Variant variant, Vec3 velocity) {
        var type = switch (variant) {
            case THORN_BALL -> ModEntities.PLANTERA_THORN_BALL.get();
            case SPORE -> ModEntities.PLANTERA_SPORE.get();
            default -> ModEntities.PLANTERA_SEED.get();
        };
        PlanteraProjectile shot = type.create(level());
        if (shot == null) return;
        float damage = (float) (variant.baseDamage() * CreatureDifficultyScaling.attackMultiplier(this) * (isEnraged() ? 2 : 1));
        shot.launch(this, variant, getBoundingBox().getCenter(), velocity, damage);
        level().addFreshEntity(shot);
    }

    private boolean holdPosition() {
        setDeltaMovement(getDeltaMovement().scale(0.45));
        return true;
    }

    private void applyRoot(LivingEntity victim) {
        if (victim == combatTarget && unhurtTicks >= UNHURT_TICKS && getRandom().nextFloat() < ROOT_CHANCE) {
            victim.addEffect(new MobEffectInstance(ModEffects.WEBBED, isLegendary() ? ROOT_TICKS * 2 : ROOT_TICKS));
        }
    }

    private void begin(CombatState next) {
        combatState = next;
        actionTicks = 0;
        comboTicks = 0;
        skillHit = false;
    }

    private enum CombatState {
        // 追击并执行当前阶段的基础攻击，等待下一招。
        RECOVER,
        // 转入二阶段，暂停行动以播放血条过渡。
        TRANSFORM,
        // 等待花粉的命中判定以及可能的风压联动。
        POLLEN,
        // 等待孢子的命中判定以及可能的弹幕联动。
        SPORES,
        // 连续发射基础弹幕，联动时缩短发射序列。
        BARRAGE,
        // 传奇模式下上浮寻找露天位置回血。
        SUNLIGHT,
        // 锁定冲刺方向前给目标留出反应时间。
        DASH_WINDUP,
        // 按锁定方向执行一段近战冲刺。
        DASH,
        // 发射接触实心块后长出触手的种子。
        PLANT_SEEDS,
        // 单段冲刺落空后转向目标，瞄准再发射分裂种子。
        SPLITTING_SEED,
        // 抛出沿弧线飞行并折返的弱追踪叶片。
        LEAF
    }

    private void emitPollen(Vec3 center, double radius, boolean thinned) {
        MolangExp variables = new MolangExp(Map.of("variable.radius", Double.toString(radius),
                "variable.opacity", thinned ? "0.38" : "0.72"));
        EmitterCreationPacketS2C packet = new EmitterCreationPacketS2C(POLLEN_PARTICLE, center.toVector3f(), variables, -1);
        PacketDistributor.sendToPlayersTrackingEntity(this, packet);
    }

    private class PollenCloud {
        private final int cast;
        private Vec3 center;
        private Vec3 velocity = Vec3.ZERO;
        private int age;
        private boolean thinned;
        private boolean hitPlayer;

        private PollenCloud(int cast, Vec3 center) {
            this.cast = cast;
            this.center = center;
        }

        private void blow() {
            if (thinned || combatTarget == null) return;
            thinned = true;
            velocity = combatTarget.getBoundingBox().getCenter().subtract(center).normalize().scale(POLLEN_WIND_SPEED);
            age = Math.min(age, POLLEN_LIFETIME / 2);
        }

        private boolean tick(ServerLevel level) {
            if (++age >= POLLEN_LIFETIME) return true;
            center = center.add(velocity);
            velocity = velocity.scale(POLLEN_WIND_DECAY);
            double radius = thinned ? THIN_POLLEN_RADIUS : POLLEN_RADIUS;
            if ((age - 1) % POLLEN_INTERVAL == 0) emitPollen(center, radius, thinned);
            if (age % POLLEN_INTERVAL != 0) return false;
            for (Player player : level.getEntitiesOfClass(Player.class, new AABB(center, center).inflate(radius))) {
                if (player.distanceToSqr(center) > radius * radius || !LibEntityUtils.canMobHit(Plantera.this, player))
                    continue;
                if (!player.hurt(damageSources().mobAttack(Plantera.this), (float) (POLLEN_DAMAGE * (thinned ? 0.5 : 1.0) * CreatureDifficultyScaling.attackMultiplier(Plantera.this))))
                    continue;
                hitPlayer = true;
                if (!thinned) {
                    applyRoot(player);
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, POLLEN_EFFECT_TICKS, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, POLLEN_EFFECT_TICKS));
                    player.addEffect(new MobEffectInstance(MobEffects.POISON, POLLEN_EFFECT_TICKS));
                }
            }
            return false;
        }
    }

    void bindHook(PlanteraHook hook) {
        int index = hook.getHookIndex();
        if (index >= 0 && index < hooks.length) hooks[index] = hook;
    }

    void bindTentacle(PlanteraTentacle tentacle) {
        int slot = tentacle.getSlot();
        if (slot >= 0 && slot < tentacles.length) {
            tentacles[slot] = tentacle;
        }
    }

    PlanteraTentacle[] getTentacles() {
        return tentacles;
    }

    public PlanteraHook getHook(int index) {
        return index < 0 || index >= hooks.length ? null : hooks[index];
    }

    private void ensureHooks() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        for (int index = 0; index < hooks.length; index++) {
            PlanteraHook current = hooks[index];
            if (current != null && current.isAlive() && !current.isRemoved()) continue;

            PlanteraHook hook = BossEntities.PLANTERA_HOOK.get().create(level());
            if (hook == null) continue;
            hook.setPos(hookAttachment(index, 1.0F).subtract(0, hook.getBbHeight() * 0.5, 0));
            if (!hook.setMaster(this, index, null) || !serverLevel.addFreshEntity(hook)) {
                hooks[index] = null;
                hook.discard();
            }
        }
    }

    public Quaternionf flowerOrientation(float partialTick) {
        float yaw = Mth.rotLerp(partialTick, yRotO, getYRot());
        float pitch = Mth.rotLerp(partialTick, xRotO, getXRot());
        return new Quaternionf().rotationY(-yaw * Mth.DEG_TO_RAD).rotateX((90 + pitch) * Mth.DEG_TO_RAD);
    }

    public Vec3 hookAttachment(int index, float partialTick) {
        float angle = index * Mth.TWO_PI / Plantera.HOOK_COUNT;
        Vector3f offset = new Vector3f(Mth.cos(angle) * CALYX_ATTACHMENT_RADIUS, -1, Mth.sin(angle) * CALYX_ATTACHMENT_RADIUS);
        offset.mul(Plantera.MODEL_SCALE).rotate(flowerOrientation(partialTick));
        offset.y += getBbHeight() * 0.5F;
        return (partialTick == 1.0F ? position() : getPosition(partialTick)).add(new Vec3(offset.mul(getScale())));
    }

    boolean isValidHookAnchor(BlockPos anchor) {
        return level().hasChunkAt(anchor) && level().getBlockState(anchor).blocksMotion();
    }

    BlockPos findHookAnchor(int hookIndex) {
        Vec3 travel = getDeltaMovement();
        double heading = travel.horizontalDistanceSqr() > 1.0E-4 ? Math.atan2(travel.z, travel.x) : Math.toRadians(getYRot() + 90);
        double baseAngle = heading + hookIndex * Mth.TWO_PI / HOOK_COUNT;
        // 在各自扇区斜向探查洞壁、洞顶和地面，附近没有抓点时再向下寻找。
        for (int attempt = 0; attempt < HOOK_SEARCH_ATTEMPTS; attempt++) {
            double angle = baseAngle + (random.nextDouble() * 2 - 1) * Math.PI / HOOK_COUNT;
            double vertical = ((attempt / 3 + hookIndex) % 6 - 3 + random.nextDouble()) * 0.5;
            Vec3 direction = new Vec3(Math.cos(angle), vertical, Math.sin(angle)).normalize();
            Vec3 origin = hookAttachment(hookIndex, 1.0F);
            BlockHitResult hit = level().clip(new ClipContext(origin, origin.add(direction.scale(HOOK_SEARCH_RANGE)),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() == HitResult.Type.BLOCK && origin.distanceToSqr(hit.getLocation()) >= HOOK_SEARCH_START * HOOK_SEARCH_START
                    && isValidHookAnchor(hit.getBlockPos()) && !isHookAnchorOccupied(hit.getBlockPos()))
                return hit.getBlockPos();
        }
        for (int radius = 8; radius <= 24; radius += 4) {
            BlockPos column = BlockPos.containing(getX() + radius * Math.cos(baseAngle), getY(), getZ() + radius * Math.sin(baseAngle));
            for (int depth = 0; depth <= 32; depth++) {
                BlockPos ground = column.below(depth);
                if (isValidHookAnchor(ground) && !isValidHookAnchor(ground.above()) && !isHookAnchorOccupied(ground)) {
                    BlockHitResult hit = level().clip(new ClipContext(hookAttachment(hookIndex, 1.0F), Vec3.atCenterOf(ground),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                    if (hit.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(ground))
                        return ground;
                    break;
                }
            }
        }
        return null;
    }

    private boolean isHookAnchorOccupied(BlockPos pos) {
        for (PlanteraHook hook : hooks) {
            if (hook != null && !hook.isRemoved() && pos.equals(hook.getAnchor())) return true;
        }
        return false;
    }

    /// 以已抓牢花钩的中心限制追击目标点，再按阶段速度向该点移动。
    private void updateMovement() {
        LivingEntity target = getTarget();
        if (target == null) {
            setDeltaMovement(getDeltaMovement().scale(HOOKED_MOVE_MOMENTUM));
            return;
        }

        Vec3 desired = target.getBoundingBox().getCenter();
        Vec3 center = Vec3.ZERO;
        int anchors = 0;
        for (PlanteraHook hook : hooks) {
            if (hook == null || hook.isRemoved() || !hook.hasReachedAnchor()) continue;
            center = center.add(hook.getBoundingBox().getCenter());
            anchors++;
        }
        if (anchors > 0) {
            center = center.scale(1.0 / anchors);
            Vec3 reach = desired.subtract(center);
            if (reach.lengthSqr() > HOOK_TETHER_LENGTH * HOOK_TETHER_LENGTH) {
                desired = center.add(reach.normalize().scale(HOOK_TETHER_LENGTH));
            }
        }
        Vec3 acceleration = desired.subtract(getBoundingBox().getCenter()).normalize().scale(MOVE_ACCELERATION);

        double maximumSpeed = isEnraged()
                ? ENRAGED_MOVE_SPEED
                : getPhase() == 0
                ? PHASE_ONE_MOVE_SPEED
                : PHASE_TWO_MOVE_SPEED;
        maximumSpeed = stateParameters(isEnraged() ? Temperament.ENRAGED : Temperament.CALM)
                .behavior().moveSpeedOr(maximumSpeed);
        if (getPhase() >= 1 && getHealth() < getMaxHealth() * 0.25F) maximumSpeed *= 1.4;
        if (isFtw()) {
            maximumSpeed *= 1.15;
            acceleration = acceleration.scale(PHASE_TWO_ACCELERATION_MULTIPLIER);
        }
        Vec3 velocity = getDeltaMovement().add(acceleration);
        if (velocity.lengthSqr() > maximumSpeed * maximumSpeed) {
            velocity = velocity.normalize().scale(maximumSpeed);
        }
        setDeltaMovement(velocity);
    }

    /// 进入或刷新 200 tick 的狂暴窗口。只有首次进入时播放咆哮；
    /// 持续缺少锚点或玩家仍在范围外只刷新计时，不会每 tick 重复播放声音。
    private void enrage() {
        if (enragedTicks <= 0) {
            playSound(ModSoundEvents.ROAR.get());
        }
        enragedTicks = 200;
    }

    public boolean isEnraged() {
        return enragedTicks > 0;
    }

    private void updateStateAttributes() {
        Temperament state = isEnraged() ? Temperament.ENRAGED : Temperament.CALM;
        if (appliedTemperament != null && appliedTemperament != state)
            setSpecialState(appliedTemperament, false);
        appliedTemperament = state;
        setSpecialState(state, true);
    }

    private int bodyTentacleCount() {
        return BODY_TENTACLE_COUNT;
    }

    private int tentaclesPerHook() {
        return TENTACLES_PER_HOOK;
    }

    private int activeTentacleCount() {
        return bodyTentacleCount() + HOOK_COUNT * tentaclesPerHook();
    }

    /// 恢复尚未被击败的临时触手；已被击败的槽位不能因区块重载而复活。
    private void ensureTentacles() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        for (int slot = 0; slot < activeTentacleCount(); slot++) {
            if ((destroyedTentacles & 1L << slot) != 0) continue;
            PlanteraTentacle current = tentacles[slot];
            if (current != null && current.isAlive() && !current.isRemoved()) continue;
            spawnTentacle(serverLevel, slot);
        }
    }

    private boolean spawnTentacle(ServerLevel serverLevel, int slot) {
        PlanteraTentacle created = BossEntities.PLANTERA_TENTACLE.get().create(serverLevel);
        if (created == null) return false;
        created.setPos(getTentacleAnchor(slot));
        if (created.setMaster(this, slot) && serverLevel.addFreshEntity(created)) return true;
        removeSubEntity(created);
        tentacles[slot] = null;
        created.discard();
        return false;
    }

    // 本体独立计时。传奇的六个花钩也各自计时，互不占用再生间隔。
    private void tickTentacleRegrowth() {
        if (!(level() instanceof ServerLevel server)) return;
        for (int group = 0; group < (isLegendary() ? regenerationTicks.length : 1); group++) {
            int first = group == 0 ? 0 : BODY_TENTACLE_COUNT + (group - 1) * TENTACLES_PER_HOOK;
            int count = group == 0 ? BODY_TENTACLE_COUNT : TENTACLES_PER_HOOK;
            int alive = 0;
            int missing = -1;
            for (int slot = first; slot < first + count; slot++) {
                if ((destroyedTentacles & 1L << slot) != 0) missing = slot;
                else if (tentacles[slot] != null && tentacles[slot].isAlive()) alive++;
            }
            if (missing < 0) {
                regenerationTicks[group] = 0;
            } else if (++regenerationTicks[group] >= TENTACLE_REBUILD_BASE_COOLDOWN * (alive + 1)
                    && spawnTentacle(server, missing)) {
                destroyedTentacles &= ~(1L << missing);
                regenerationTicks[group] = 0;
            }
        }
    }

    /// 周期开始时为空闲花钩分配锚点，周期中点让离目标最远的一根换位。
    ///
    /// 钩爪不再定时瞬间改写锚点，而是完整经过空闲、伸展、抓牢和收回四态。
    /// 主体牵引只读取真正抓牢的钩爪，客户端因此能获得连续的伸缩运动。
    private void updateHookCycle() {
        LivingEntity target = getTarget();
        int cycleTick = tickCount % HOOK_CYCLE_TICKS;
        int index = cycleTick / HOOK_CAST_SPACING;
        if (index < hooks.length && cycleTick % HOOK_CAST_SPACING == 1) {
            PlanteraHook hook = hooks[index];
            if (hook != null && !hook.isRemoved() && hook.getState() == PlanteraHook.State.IDLE) {
                BlockPos anchor = findHookAnchor(index);
                if (anchor != null) {
                    hook.setAnchor(anchor);
                    hook.setState(PlanteraHook.State.EXTENDING);
                }
            }
        }
        if (target == null || cycleTick != HOOK_RETRACT_TICK) {
            return;
        }

        int grabbed = 0;
        PlanteraHook farthest = null;
        double farthestDistance = -1.0;
        for (PlanteraHook hook : hooks) {
            if (hook == null || hook.isRemoved() || hook.getState() != PlanteraHook.State.GRABBED) {
                continue;
            }
            grabbed++;
            double distance = hook.distanceToSqr(target);
            if (distance > farthestDistance) {
                farthestDistance = distance;
                farthest = hook;
            }
        }
        if (grabbed >= MINIMUM_ANCHORED_HOOKS && farthest != null) {
            farthest.setAnchor(null);
            farthest.setState(PlanteraHook.State.RETRACTING);
        }
    }

    /// 按渲染帧插值藤蔓的连接位置。客户端尚未收到对应花钩时，暂时连接到本体中心。
    public Vec3 tentacleAnchorForRender(int slot, float partialTick) {
        PlanteraHook hook = slot < BODY_TENTACLE_COUNT ? null : getHook((slot - BODY_TENTACLE_COUNT) / TENTACLES_PER_HOOK);
        return hook != null && !hook.isRemoved() ? hook.getPosition(partialTick).add(0, hook.getBbHeight() * 0.5, 0)
                : getPosition(partialTick).add(0, getBbHeight() * 0.5, 0);
    }

    Vec3 getTentacleAnchor(int slot) {
        if (slot < bodyTentacleCount()) {
            return getEyePosition();
        }
        int hookIndex = (slot - bodyTentacleCount()) / tentaclesPerHook();
        PlanteraHook hook = getHook(hookIndex);
        return hook == null || hook.isRemoved()
                ? hookAttachment(hookIndex, 1.0F)
                : hook.getEyePosition();
    }

    /// 返回触手所属本体或钩爪的当前速度。
    Vec3 getTentacleAnchorVelocity(int slot) {
        if (slot < bodyTentacleCount()) {
            return getDeltaMovement();
        }
        int hookIndex = (slot - bodyTentacleCount())
                / tentaclesPerHook();
        PlanteraHook hook = getHook(hookIndex);
        return hook == null || hook.isRemoved()
                ? getDeltaMovement()
                : hook.getDeltaMovement();
    }

    /// 用黄金角为刚生成且尚无朝向的触手提供稳定初始方向；进入运动后仍由
    /// 吸引和排斥公式连续调整，不会锁死在固定槽位上。
    Vec3 getTentacleBaseDirection(int slot) {
        int localSlot;
        int count;
        double phaseOffset;
        if (slot < bodyTentacleCount()) {
            localSlot = slot;
            count = bodyTentacleCount();
            phaseOffset = 0.0;
        } else {
            int hookIndex = (slot - bodyTentacleCount()) / tentaclesPerHook();
            localSlot = (slot - bodyTentacleCount()) % tentaclesPerHook();
            count = tentaclesPerHook();
            phaseOffset = hookIndex * Mth.TWO_PI / HOOK_COUNT;
        }
        double y = 1.0 - 2.0 * (localSlot + 0.5) / count;
        double radius = Math.sqrt(Math.max(0.0, 1.0 - y * y));
        double angle = localSlot * 2.399963229728653 + phaseOffset;
        return new Vec3(Math.cos(angle) * radius, y, Math.sin(angle) * radius);
    }

    void onTentacleDestroyed(int slot, PlanteraTentacle destroyed) {
        if (slot < 0 || slot >= tentacles.length || tentacles[slot] != destroyed) return;
        tentacles[slot] = null;
        destroyedTentacles |= 1L << slot;

    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(PHASE_TAG, getPhase());
        tag.putLong("DestroyedTentacles", destroyedTentacles);
        tag.putInt(ENRAGED_TICKS_TAG, enragedTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(DATA_PHASE, Mth.clamp(tag.getInt(PHASE_TAG), 0, 1));
        destroyedTentacles = tag.getLong("DestroyedTentacles");
        enragedTicks = Mth.clamp(tag.getInt(ENRAGED_TICKS_TAG), 0, 200);
        java.util.Arrays.fill(hooks, null);
        java.util.Arrays.fill(tentacles, null);
    }

    @Override
    protected Vec3 getDisengageMovement() {
        Vec3 movement = getDeltaMovement();
        return new Vec3(movement.x * 0.25D, -0.4D, movement.z * 0.25D);
    }

    public enum Temperament {
        // 未激怒的常态，使用普通战斗参数。
        CALM,
        // 激怒状态，使用激怒后的战斗参数。
        ENRAGED
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (level() instanceof ServerLevel serverLevel && !isAlive() && shouldShowMessage()) {
            if (serverLevel.getDifficulty() != Difficulty.PEACEFUL && tryBeginDeathRewardSettlement()) {
                KillBoard.INSTANCE.defeat(getType());
                for (var player : getOnlineCombatParticipants()) {
                    TreasureBagItem.createItemEntity(this, player);
                    AchievementUtils.awardAchievement(player, "the_great_southern_plantkill");
                }
            }
        }
    }

}
