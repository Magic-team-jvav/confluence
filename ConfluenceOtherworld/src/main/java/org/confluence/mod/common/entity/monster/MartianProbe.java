package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Comparator;
import java.util.UUID;

/** Scans survival players and escapes upward after detecting one. */
public final class MartianProbe extends BaseFlyingMonster {
    public static final double SEARCH_RADIUS = 20.0D;
    private static final double LOCK_RELEASE_RADIUS = 48.0D;
    private static final double APPROACH_SPEED = 0.65D;
    private static final double HOVER_SPEED = 0.22D;
    public static final double SCAN_CONE_HEIGHT = 14.0D;
    public static final double SCAN_CONE_BASE_RADIUS = 8.0D;
    public static final double SCAN_CAP_SPHERE_RADIUS = 17.0D;
    /** The sphere intersects the cone's bottom circle at depth 14 and closes two blocks below it. */
    public static final double SCAN_CAP_CENTER_DEPTH = SCAN_CONE_HEIGHT
            - Math.sqrt(SCAN_CAP_SPHERE_RADIUS * SCAN_CAP_SPHERE_RADIUS
            - SCAN_CONE_BASE_RADIUS * SCAN_CONE_BASE_RADIUS);
    public static final double SCAN_MAX_DEPTH = SCAN_CAP_CENTER_DEPTH + SCAN_CAP_SPHERE_RADIUS;
    private static final EntityDataAccessor<Integer> DATA_STATE = SynchedEntityData.defineId(MartianProbe.class, EntityDataSerializers.INT);
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("misc.idle");
    private static final MartianProbeEventTrigger NO_EVENT = player -> false;
    private static volatile MartianProbeEventTrigger eventTrigger = NO_EVENT;

    public enum State {
        // Keep the first four ordinals stable for existing entity NBT.
        IDLE, SCANNING, ALERT, FLEEING, APPROACHING, EXPOSING;

        public static State fromOrdinal(int ordinal) {
            return ordinal < 0 || ordinal >= values().length ? IDLE : values()[ordinal];
        }
    }

    private State state = State.IDLE;
    private int stateTicks;
    private @Nullable UUID triggeringPlayerUUID;
    private @Nullable Player triggeringPlayer;

    public MartianProbe(EntityType<? extends MartianProbe> type, Level level) {
        super(type, level);
        setDiscardFriction(true);
    }

    /** The event module may register this hook later; null restores the safe no-op. */
    public static void setEventTrigger(@Nullable MartianProbeEventTrigger trigger) {
        eventTrigger = trigger == null ? NO_EVENT : trigger;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_STATE, State.IDLE.ordinal());
    }

    private void setState(State next) {
        state = next == null ? State.IDLE : next;
        entityData.set(DATA_STATE, state.ordinal());
    }

    public State getProbeState() {
        return level().isClientSide ? State.fromOrdinal(entityData.get(DATA_STATE)) : state;
    }

    @Override
    protected BTRoot createBT() {
        FlyWanderAction wander = new FlyWanderAction(this, 0.25D, 12);
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    private boolean wandering;

                    @Override
                    public BTStatus execute() {
                        if (state == State.IDLE || state == State.SCANNING) {
                            if (!wandering) {
                                wander.start();
                                wandering = true;
                            }
                            return wander.execute();
                        }
                        stopWandering();
                        return BTStatus.RUNNING;
                    }

                    @Override
                    public void stop() {
                        stopWandering();
                    }

                    private void stopWandering() {
                        if (!wandering) return;
                        wander.stop();
                        wandering = false;
                    }
                };
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !isAlive()) return;

        // ALERT is retained as an NBT-compatible legacy state from the previous implementation.
        if (state == State.ALERT) setState(State.FLEEING);

        if (state == State.IDLE || state == State.SCANNING) {
            noPhysics = false;
            setState(State.SCANNING);
            Player found = level().players().stream()
                    .filter(MartianProbe::isEligibleTarget)
                    .filter(this::isInsideSearchCircle)
                    .min(Comparator.comparingDouble(this::horizontalDistanceToSqr)).orElse(null);
            if (found != null) {
                triggeringPlayer = found;
                triggeringPlayerUUID = found.getUUID();
                stateTicks = 0;
                setState(State.APPROACHING);
            } else {
                return;
            }
        }

        resolveTriggeringPlayer();
        Player player = triggeringPlayer;
        if (player == null || !isEligibleTarget(player)
                || state != State.FLEEING && horizontalDistanceToSqr(player) > LOCK_RELEASE_RADIUS * LOCK_RELEASE_RADIUS) {
            if (state == State.FLEEING) {
                discard();
            } else {
                triggeringPlayer = null;
                triggeringPlayerUUID = null;
                stateTicks = 0;
                setState(State.SCANNING);
            }
            return;
        }

        if (state == State.APPROACHING || state == State.EXPOSING) {
            tickApproachAndExposure(player);
            if (state != State.FLEEING) return;
        }

        tickFleeing(player);
    }

    private void tickApproachAndExposure(Player player) {
        Vec3 destination = new Vec3(player.getX(), player.getY() + 8.0D, player.getZ());
        double distanceToDestination = position().distanceToSqr(destination);
        if (distanceToDestination > 2.25D) {
            stateTicks = 0;
            if (state != State.APPROACHING) setState(State.APPROACHING);
            steerToward(destination, APPROACH_SPEED);
            return;
        }

        steerToward(destination, HOVER_SPEED);
        if (!isInsideScanVolume(player) || !hasLineOfSight(player)) {
            stateTicks = 0;
            if (state != State.APPROACHING) setState(State.APPROACHING);
            return;
        }

        if (state != State.EXPOSING) {
            stateTicks = 0;
            setState(State.EXPOSING);
        }
        if (++stateTicks >= 30) {
            stateTicks = 0;
            setState(State.FLEEING);
        }
    }

    private void steerToward(Vec3 destination, double maxSpeed) {
        Vec3 displacement = destination.subtract(position());
        double distance = displacement.length();
        Vec3 desiredVelocity = distance < 1.0E-4D ? Vec3.ZERO
                : displacement.scale(Math.min(maxSpeed, distance * 0.25D) / distance);
        setDeltaMovement(getDeltaMovement().scale(0.68D).add(desiredVelocity.scale(0.32D)));
        hasImpulse = true;
    }

    private void tickFleeing(Player player) {
        noPhysics = true;
        getNavigation().stop();
        setDeltaMovement(getDeltaMovement().scale(0.8D).add(0.0D, 0.12D, 0.0D));
        if (distanceToSqr(player) > 64.0D * 64.0D || getY() >= level().getMaxBuildHeight() - 4) {
            if (player instanceof ServerPlayer serverPlayer) eventTrigger.requestStart(serverPlayer);
            discard();
        }
    }

    private boolean isInsideSearchCircle(Player player) {
        return horizontalDistanceToSqr(player) <= SEARCH_RADIUS * SEARCH_RADIUS;
    }

    private double horizontalDistanceToSqr(Player player) {
        double dx = player.getX() - getX();
        double dz = player.getZ() - getZ();
        return dx * dx + dz * dz;
    }

    private boolean isInsideScanVolume(Player player) {
        Vec3 relative = player.getEyePosition().subtract(getEyePosition());
        double depth = -relative.y;
        if (depth <= 0.0D || depth > SCAN_MAX_DEPTH) return false;

        double allowedRadiusSquared;
        if (depth <= SCAN_CONE_HEIGHT) {
            double radius = SCAN_CONE_BASE_RADIUS * depth / SCAN_CONE_HEIGHT;
            allowedRadiusSquared = radius * radius;
        } else {
            double fromSphereCenter = depth - SCAN_CAP_CENTER_DEPTH;
            allowedRadiusSquared = SCAN_CAP_SPHERE_RADIUS * SCAN_CAP_SPHERE_RADIUS
                    - fromSphereCenter * fromSphereCenter;
        }
        return relative.x * relative.x + relative.z * relative.z <= allowedRadiusSquared;
    }

    private static boolean isEligibleTarget(@Nullable Player player) {
        return player != null && !player.isRemoved() && player.isAlive()
                && !player.isSpectator() && !player.isCreative();
    }

    private void resolveTriggeringPlayer() {
        if (triggeringPlayer != null && !triggeringPlayer.isRemoved() && triggeringPlayer.level() == level()) return;
        triggeringPlayer = triggeringPlayerUUID == null ? null : level().getPlayerByUUID(triggeringPlayerUUID);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "probe", 0, animation -> animation.setAndContinue(IDLE_ANIMATION)));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MartianState", state.ordinal());
        tag.putInt("StateTicks", stateTicks);
        if (triggeringPlayerUUID != null) tag.putUUID("TriggeringPlayer", triggeringPlayerUUID);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setState(tag.contains("MartianState") ? State.fromOrdinal(tag.getInt("MartianState")) : State.IDLE);
        stateTicks = tag.contains("StateTicks") ? Math.max(0, tag.getInt("StateTicks")) : 0;
        triggeringPlayerUUID = tag.hasUUID("TriggeringPlayer") ? tag.getUUID("TriggeringPlayer") : null;
        triggeringPlayer = null;
        if ((state == State.APPROACHING || state == State.EXPOSING || state == State.FLEEING)
                && triggeringPlayerUUID == null) {
            setState(State.SCANNING);
            stateTicks = 0;
        }
    }
}
