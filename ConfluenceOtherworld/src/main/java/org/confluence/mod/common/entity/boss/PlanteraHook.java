package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.function.IntFunction;

/// 世纪之花用于移动的六个花钩之一。
///
/// 抓钩锚定方块并为主体提供移动目标；它不独立持有 Boss 生命周期，
/// 主体消失、脱战或重新构建附属实体时必须一并清理。
public class PlanteraHook extends BaseLivingBossPart<Plantera> implements GeoEntity {
    private static final String HOOK_INDEX_TAG = "HookIndex";
    private static final String ANCHOR_TAG = "Anchor";
    private static final String STATE_TAG = "State";
    /// 藤钩向目标位置移动的基础速度，单位格/tick。
    private static final double HOOK_SPEED = 2.0;
    // 花钩逐步转向飞行或受力方向，抓住方块时也保留入射姿态的过渡。
    private static final float HOOK_TURN_DEGREES_PER_TICK = 24.0F;

    /// 钩索回到锚点时每 tick 保留的位置修正比例。
    private static final double FOLLOW_MOMENTUM = 0.9;

    private static final EntityDataAccessor<Integer> HOOK_INDEX = SynchedEntityData.defineId(PlanteraHook.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> ANCHOR = SynchedEntityData.defineId(PlanteraHook.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(PlanteraHook.class, EntityDataSerializers.INT);

    // 接触伤害按花钩独立计时，伸缩中的路径也参与命中检测。
    private static final int CONTACT_COOLDOWN = 20;
    // 二阶段花钩命中后，减速和流血各持续的 tick 数。
    private static final int CONTACT_DEBUFF_TICKS = 80;
    private int contactCooldown;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PlanteraHook(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public void setMaster(Plantera master, int hookIndex, @Nullable BlockPos anchor) {
        entityData.set(HOOK_INDEX, Mth.clamp(hookIndex, 0, Plantera.HOOK_COUNT - 1));
        setAnchor(anchor);
        setState(anchor == null ? State.IDLE : State.EXTENDING);
        bindTo(master);
        master.bindHook(this);
    }

    public int getHookIndex() {
        return entityData.get(HOOK_INDEX);
    }

    public @Nullable BlockPos getAnchor() {
        return entityData.get(ANCHOR).orElse(null);
    }

    public void setAnchor(@Nullable BlockPos anchor) {
        entityData.set(ANCHOR, Optional.ofNullable(anchor == null ? null : anchor.immutable()));
    }

    State getState() {
        return State.fromOrdinal(entityData.get(STATE));
    }

    void setState(State state) {
        entityData.set(STATE, state.ordinal());
    }

    public boolean hasReachedAnchor() {
        return getState() == State.GRABBED;
    }

    @Override
    protected void definePartSynchedData(SynchedEntityData.Builder builder) {
        builder.define(HOOK_INDEX, 0);
        builder.define(ANCHOR, Optional.empty());
        builder.define(STATE, State.IDLE.ordinal());
    }

    @Override
    protected Class<Plantera> getOwnerType() {
        return Plantera.class;
    }

    @Override
    protected void tickPart(Plantera master) {
        master.bindHook(this);
        if (level().isClientSide) return;

        if (contactCooldown > 0) contactCooldown--;
        Vec3 previous = position();
        State state = getState();
        BlockPos anchor = getAnchor();
        if ((state == State.EXTENDING || state == State.GRABBED) && (anchor == null || !master.isValidHookAnchor(anchor))) {
            setAnchor(null);
            setState(State.RETRACTING);
            state = State.RETRACTING;
        }

        Vec3 targetCenter = state == State.IDLE || state == State.RETRACTING
                ? master.hookAttachment(getHookIndex(), 1.0F)
                : attachmentPoint(anchor, master);
        Vec3 offset = targetCenter.subtract(getBoundingBox().getCenter());
        if (offset.lengthSqr() <= HOOK_SPEED * HOOK_SPEED) {
            if (state == State.RETRACTING) {
                setState(State.IDLE);
            } else if (state == State.EXTENDING) {
                setState(State.GRABBED);
                spawnThorns(master, anchor);
            }
            Vec3 movement = anchor == null ? offset : offset.scale(FOLLOW_MOMENTUM);
            setDeltaMovement(movement);
            move(MoverType.SELF, movement);
            if (getState() == State.GRABBED) {
                // 沿藤蔓受力方向倾斜，并保留朝向抓附面的分量，让钩尖扣住方块。
                Vec3 intoFace = Vec3.atCenterOf(anchor).subtract(targetCenter).normalize();
                Vec3 alongVine = targetCenter.subtract(master.hookAttachment(getHookIndex(), 1.0F)).normalize();
                updateRotation(intoFace.add(alongVine));
            } else {
                updateRotation(movement);
            }
            attackContacts(master, previous);
            return;
        }
        if (offset.lengthSqr() < 1.0E-4) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        Vec3 movement = offset.normalize().scale(HOOK_SPEED);
        setDeltaMovement(movement);
        move(MoverType.SELF, movement);
        updateRotation(movement);
        attackContacts(master, previous);
    }

    /// FTW 钩爪抓牢时，在相邻空位长出可被攻击清除的粉色荆棘。
    private void spawnThorns(Plantera master, BlockPos anchor) {
        if (!master.isFtw() || anchor == null || !level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
            return;
        for (Direction direction : Direction.values()) {
            BlockPos pos = anchor.relative(direction);
            if (!level().hasChunkAt(pos) || !level().getBlockState(pos).isAir()) continue;
            level().setBlock(pos, NatureBlocks.PLANTERA_THORN.get().getStateForPlacement(level(), pos), 3);
        }
    }

    private Vec3 attachmentPoint(BlockPos anchor, Plantera master) {
        Vec3 center = Vec3.atCenterOf(anchor);
        Vec3 towardBody = master.getBoundingBox().getCenter().subtract(center);
        Direction face = null;
        double best = Double.NEGATIVE_INFINITY;
        for (Direction side : Direction.values()) {
            if (level().getBlockState(anchor.relative(side)).blocksMotion()) continue;
            double score = towardBody.dot(Vec3.atLowerCornerOf(side.getNormal()));
            if (score > best) {
                best = score;
                face = side;
            }
        }
        return face == null ? center : center.add(Vec3.atLowerCornerOf(face.getNormal()).scale(0.5 + getBbWidth() * 0.35));
    }

    private void updateRotation(Vec3 movement) {
        if (movement.lengthSqr() < 1.0E-7) {
            return;
        }
        LibEntityUtils.turnEntityTowards(this, movement, HOOK_TURN_DEGREES_PER_TICK, HOOK_TURN_DEGREES_PER_TICK);
    }

    @Override
    protected void readPartSaveData(CompoundTag tag) {
        entityData.set(HOOK_INDEX, Mth.clamp(tag.getInt(HOOK_INDEX_TAG), 0, Plantera.HOOK_COUNT - 1));
        setAnchor(tag.contains(ANCHOR_TAG) ? BlockPos.of(tag.getLong(ANCHOR_TAG)) : null);
        setState(State.fromOrdinal(tag.getInt(STATE_TAG)));
    }

    @Override
    protected void addPartSaveData(CompoundTag tag) {
        tag.putInt(HOOK_INDEX_TAG, getHookIndex());
        tag.putInt(STATE_TAG, getState().ordinal());
        BlockPos anchor = getAnchor();
        if (anchor != null) tag.putLong(ANCHOR_TAG, anchor.asLong());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    private void attackContacts(Plantera master, Vec3 previous) {
        if (contactCooldown > 0 || master.getTarget() == null || getState() == State.IDLE) return;
        for (var entity : SweptContactAttack.findTargets(this, previous, 0,
                SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE, candidate -> candidate == master.getTarget())) {
            if (!(entity instanceof LivingEntity victim) || !victim.hurt(damageSources().mobAttack(master),
                    (float) getAttributeValue(Attributes.ATTACK_DAMAGE))) continue;
            contactCooldown = CONTACT_COOLDOWN;
            master.reportAttackHit(victim);
            if (master.getPhase() != 0 && master.isExpert()) {
                victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, CONTACT_DEBUFF_TICKS));
                victim.addEffect(new MobEffectInstance(ModEffects.BLEEDING, CONTACT_DEBUFF_TICKS));
            }
            return;
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    enum State {
        // 等待主体分配下一个落点。
        IDLE,
        // 向锚点伸出，抵达后允许牵引。
        EXTENDING,
        // 抓住锚点，供主体计算移动方向。
        GRABBED,
        // 收回钩爪，为下一轮抓取腾出位置。
        RETRACTING;

        private static final IntFunction<State> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);

        private static State fromOrdinal(int ordinal) {
            return BY_ID.apply(ordinal);
        }
    }
}
