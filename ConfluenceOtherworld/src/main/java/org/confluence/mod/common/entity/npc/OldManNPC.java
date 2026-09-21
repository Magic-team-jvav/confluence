package org.confluence.mod.common.entity.npc;

import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.entity.boss.Skeletron;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;
import org.confluence.mod.common.entity.npc.house.House;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.network.s2c.OpenNPCDialogPacketS2C;
import org.confluence.mod.util.ModUtils;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.jetbrains.annotations.Nullable;

/// 老人 —— 地牢入口的诅咒 NPC。
public class OldManNPC extends BaseNPC {
    @Nullable
    private GlobalPos dungeonEntrance;

    public OldManNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile combatProfile) {
        super(type, level, combatProfile);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new NPCTradeGoal(this));
        goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 1.0));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    /// 老人守在所属地牢入口，不参与城镇住房分配。
    @Override
    protected void tickFindHouse(ServerLevel level) {}

    @Override
    public void setHouse(House house) {
        super.setHouse(House.EMPTY);
        if (dungeonEntrance != null) restrictTo(dungeonEntrance.pos(), 8);
    }

    @Nullable
    public GlobalPos getDungeonEntrance() {
        return dungeonEntrance;
    }

    public void setDungeonEntrance(GlobalPos entrance) {
        dungeonEntrance = entrance;
        spawnAtPos = entrance.pos();
        restrictTo(entrance.pos(), 8);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (dungeonEntrance != null)
            GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, dungeonEntrance).result().ifPresent(value -> tag.put("DungeonEntrance", value));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("DungeonEntrance"))
            GlobalPos.CODEC.parse(NbtOps.INSTANCE, tag.get("DungeonEntrance")).result().ifPresent(this::setDungeonEntrance);
    }

    /// 老人免疫敌怪及其弹体造成的伤害，但仍会受到环境、陷阱和无主爆炸伤害。
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !(source.getEntity() instanceof Enemy) && super.hurt(source, amount);
    }

    public boolean canSummonSkeletron() {
        long dayTime = level().dayTime() % 24000;
        return dayTime >= 12000 || dayTime < 200;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            InteractionResult commonResult = handleCommonInteraction(serverPlayer, hand);
            if (commonResult != null) return commonResult;
            recordInteraction(serverPlayer);
            PortPacketDistributor.sendToPlayer(serverPlayer, new OpenNPCDialogPacketS2C(getId()));
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    public void summonSkeletron(ServerPlayer player) {
        if (!canSummonSkeletron() || getInteractingPlayer() != player || player.level() != level() || !isAlive())
            return;
        ServerLevel serverLevel = (ServerLevel) level();
        if (!serverLevel.getEntitiesOfClass(Skeletron.class, getBoundingBox().inflate(256.0), Skeletron::isAlive).isEmpty())
            return;
        Skeletron skeletron = new Skeletron(BossEntities.SKELETRON.get(), level());
        skeletron.finalizeSpawn(serverLevel, level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.EVENT, null, null);
        ModUtils.summonBoss(serverLevel, blockPosition(), skeletron, player);
        if (serverLevel.getEntity(skeletron.getUUID()) != skeletron) return;
        NPCSpawner.INSTANCE.oldManSummoned(this, skeletron);
        NPCSpawner.INSTANCE.onNPCRemoved(this);
        discard();
    }
}
