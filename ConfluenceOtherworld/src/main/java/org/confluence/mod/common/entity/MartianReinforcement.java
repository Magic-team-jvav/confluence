package org.confluence.mod.common.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.confluence.mod.common.entity.monster.*;
import org.confluence.mod.common.gameevent.MartianEventHelper;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.ParticleStorm;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/** A reserved spawn slot, followed by a falling carrier. Passengers never accumulate fall damage. */
public class MartianReinforcement extends Entity {
    public static final int MONSTERS_PER_PLAYER = 40;
    public static final int WARNING_TICKS = 40;
    private static final EntityDataAccessor<Boolean> FALLING =
            SynchedEntityData.defineId(MartianReinforcement.class, EntityDataSerializers.BOOLEAN);
    private static final List<Supplier<? extends EntityType<? extends Mob>>> POOL = List.of(
            MonsterEntities.MARTIAN_ENGINEER, MonsterEntities.RAY_GUNNER,
            MonsterEntities.SCUTLIX, MonsterEntities.MARTIAN_OFFICER, MonsterEntities.BRAIN_SCRAMBLER,
            MonsterEntities.GRAY_GRUNT, MonsterEntities.GIGAZAPPER, MonsterEntities.GIGAZAPPER_AF2);
    private int age;
    private UUID caller;
    private boolean passengerHadNoAi;
    private boolean hitPlayer;

    public MartianReinforcement(EntityType<? extends MartianReinforcement> type, Level level) {
        super(type, level);
    }

    public static boolean hasCapacity(ServerLevel level) {
        long limit = (long) MONSTERS_PER_PLAYER * level.players().size();
        if (limit == 0) {
            return false;
        }
        int count = 0;
        for (Entity entity : level.getAllEntities()) {
            boolean monster = entity instanceof Mob mob && mob.isAlive()
                    && (mob instanceof MartianHumanoidMonster || mob instanceof MartianProbe
                    || mob instanceof Scutlix || mob instanceof MartianWalker || mob instanceof TeslaTurret);
            boolean reserved = entity instanceof MartianReinforcement drop && !drop.isRemoved() && !drop.isFalling();
            if ((monster || reserved) && ++count >= limit) {
                return false;
            }
        }
        return true;
    }

    public void initialize(MartianOfficer officer, Vec3 target) {
        caller = officer.getUUID();
        setPos(target);
        MartianEventHelper.inheritEventMembership(officer, this);
    }

    public boolean isFalling() {
        return entityData.get(FALLING);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(FALLING, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) {
            return;
        }
        age++;
        if (!isFalling()) {
            Entity officer = caller == null ? null : server.getEntity(caller);
            if (!(officer instanceof MartianOfficer) || !officer.isAlive()) {
                discard();
            } else if (age >= WARNING_TICKS) {
                spawnPassenger(server);
            }
            return;
        }
        if (!(getFirstPassenger() instanceof Mob mob) || !mob.isAlive() || age > WARNING_TICKS + 200) {
            discard();
            return;
        }
        setBoundingBox(mob.getDimensions(mob.getPose()).makeBoundingBox(position()));
        Vec3 start = position();
        Vec3 velocity = getDeltaMovement().add(0, -0.08, 0);
        setDeltaMovement(0, Math.max(-3, velocity.y), 0);
        move(MoverType.SELF, getDeltaMovement());
        positionRider(mob);
        if (!hitPlayer) {
            var victims = SweptContactAttack.findTargets(this, start, 0, 16,
                    entity -> entity instanceof Player player && player.isAlive() && !player.isCreative() && !player.isSpectator());
            for (Entity victim : victims) {
                victim.hurt(new DamageSource(level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.FALLING_ANVIL), this), 10.0F);
            }
            hitPlayer = !victims.isEmpty();
        }
        if (onGround() || isInWater() || isInLava()) {
            playLandingEffects(server, mob);
            discard();
            if (caller != null && server.getEntity(caller) instanceof Mob officer) {
                mob.setTarget(officer.getTarget());
            }
        }
    }

    private void playLandingEffects(ServerLevel server, Mob passenger) {
        float radius = Math.min(3.0F, Math.max(1.25F, passenger.getBbWidth() * 1.5F));
        // Keep the emitter in world space: the carrier is removed immediately after landing.
        EmitterCreationPacketS2C packet = new EmitterCreationPacketS2C(
                Confluence.asResource("martian_reinforcement_landing"),
                position().add(0, 0.05, 0).toVector3f(), new MolangExp("variable.radius", radius), -1);
        ParticleStorm.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> server.getChunkAt(blockPosition())), packet);
        SoundEvent sound = getLandingSound();
        if (sound != null) {
            server.playSound(null, getX(), getY(), getZ(), sound, SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }

    /** Return a registered sound once landing audio is available; null leaves the landing silent. */
    @Nullable
    protected SoundEvent getLandingSound() {
        return null;
    }

    private void spawnPassenger(ServerLevel server) {
        // The warning already owns one slot. Exclude it while checking the live population again.
        entityData.set(FALLING, true);
        if (!hasCapacity(server)) {
            discard();
            return;
        }
        Mob mob = POOL.get(random.nextInt(POOL.size())).get().create(server);
        if (mob == null) {
            discard();
            return;
        }
        Vec3 destination = position();
        boolean clear = false;
        for (int height = 24; height >= 6; height--) {
            double y = Math.min(server.getMaxBuildHeight() - mob.getBbHeight() - 1, destination.y + height);
            mob.setPos(destination.x, y, destination.z);
            if (server.hasChunkAt(mob.blockPosition()) && server.noCollision(mob)) {
                clear = true;
                break;
            }
        }
        if (!clear) {
            discard();
            return;
        }
        // Spawn only the chosen monster: a Scutlix must not auto-create an extra gunner beyond the cap.
        mob.finalizeSpawn(server, server.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.SPAWN_EGG, null, null);
        passengerHadNoAi = mob.isNoAi();
        mob.setNoAi(true);
        mob.setTarget(null);
        mob.setDeltaMovement(Vec3.ZERO);
        mob.fallDistance = 0;
        MartianEventHelper.inheritEventMembership(this, mob);
        setPos(mob.position());
        if (!server.addFreshEntity(mob)) {
            discard();
            return;
        }
        if (!mob.startRiding(this, true)) {
            mob.discard();
            discard();
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        // Entity's default implementation forwards its own fall damage to every passenger.
        return false;
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction move) {
        move.accept(passenger, getX(), getY(), getZ());
    }

    @Override
    public void remove(RemovalReason reason) {
        if (reason.shouldDestroy()) {
            for (Entity passenger : List.copyOf(getPassengers())) {
                if (passenger instanceof Mob mob) {
                    mob.setNoAi(passengerHadNoAi);
                    mob.fallDistance = 0;
                    mob.setDeltaMovement(Vec3.ZERO);
                }
                passenger.stopRiding();
                passenger.setPos(position());
            }
        }
        super.remove(reason);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", age);
        tag.putBoolean("Falling", isFalling());
        tag.putBoolean("PassengerHadNoAi", passengerHadNoAi);
        tag.putBoolean("HitPlayer", hitPlayer);
        if (caller != null) {
            tag.putUUID("Caller", caller);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        age = Math.max(0, Math.min(WARNING_TICKS + 200, tag.getInt("Age")));
        entityData.set(FALLING, tag.getBoolean("Falling"));
        passengerHadNoAi = tag.getBoolean("PassengerHadNoAi");
        hitPlayer = tag.getBoolean("HitPlayer");
        caller = tag.hasUUID("Caller") ? tag.getUUID("Caller") : null;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
