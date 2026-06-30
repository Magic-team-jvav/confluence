package org.confluence.mod.common.entity.npc;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.confluence.mod.common.data.saved.HouseHandler;
import org.confluence.mod.common.entity.npc.chat.ChatManager;
import org.confluence.mod.common.entity.npc.chat.NPCChat;
import org.confluence.mod.common.entity.npc.house.House;
import org.confluence.mod.common.entity.npc.house.HouseValidater;
import org.confluence.mod.common.entity.npc.mood.NPCMood;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

/**
 * NPC 基类 —— Brain 驱动 + GeckoLib 人形动画。
 * P0 阶段仅 IDLE activity（漫步 + 注视），Schedule / 防御 后续补。
 */
public abstract class BaseNPC extends PathfinderMob implements GeoEntity {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected static final ImmutableList<SensorType<? extends Sensor<? super BaseNPC>>> SENSOR_TYPES =
            ImmutableList.of(
                    SensorType.NEAREST_LIVING_ENTITIES,
                    SensorType.NEAREST_PLAYERS,
                    SensorType.HURT_BY
            );

    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES =
            ImmutableList.of(
                    MemoryModuleType.HOME,
                    MemoryModuleType.WALK_TARGET,
                    MemoryModuleType.LOOK_TARGET,
                    MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                    MemoryModuleType.NEAREST_LIVING_ENTITIES,
                    MemoryModuleType.HURT_BY_ENTITY
            );

    protected House house = House.EMPTY;
    protected NPCMood mood = new NPCMood();
    @Nullable
    protected NPCChat currentChat;

    @SuppressWarnings("this-escape")
    public BaseNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    // === Brain ===

    @Override
    protected Brain.Provider<?> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        Brain<BaseNPC> brain = (Brain<BaseNPC>) brainProvider().makeBrain(dynamic);
        registerBrainGoals(brain);
        return brain;
    }

    protected void registerBrainGoals(Brain<BaseNPC> brain) {}

    // === 房屋查找 ===

    private static final int FIND_HOUSE_INTERVAL = 600;

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        tickBrain((ServerLevel) level());
        if (!house.isValid()) {
            tickFindHouse((ServerLevel) level());
        }
        tickWalkToHome((ServerLevel) level());
        tickMood();
        ChatManager.tickNPC(this);
    }

    // === 心情 ===

    public NPCMood getMood() {
        return mood;
    }

    protected void tickMood() {
        if (tickCount % 1200 == 0) { // 每 60 秒
            mood.evaluate(level().getEntitiesOfClass(BaseNPC.class,
                    getBoundingBox().inflate(16),
                    n -> n != this));
        }
    }

    // === 对话 ===

    public void setCurrentChat(NPCChat chat) {
        this.currentChat = chat;
    }

    @Nullable
    public NPCChat getCurrentChat() {
        return currentChat;
    }

    @SuppressWarnings("unchecked")
    protected void tickBrain(ServerLevel level) {
        Brain<BaseNPC> brain = (Brain<BaseNPC>) getBrain();
        brain.tick(level, this);
    }

    /**
     * 每 600 tick 在当前位置及周边采样，尝试发现房屋。
     */
    protected void tickFindHouse(ServerLevel level) {
        if (tickCount % FIND_HOUSE_INTERVAL != 0) return;
        HouseValidater.Result result = HouseValidater.scan(level, blockPosition());
        if (result.isValid()) {
            House found = new House(Optional.of(getUUID()), result.min(), result.max());
            setHouse(found);
            HouseHandler.INSTANCE.setHouse(this, found);
        }
    }

    /**
     * 有 HOME 记忆时向家移动。
     */
    protected void tickWalkToHome(ServerLevel level) {
        if (!house.isValid()) return;
        BlockPos homePos = house.center();
        double distSq = blockPosition().distSqr(homePos);
        if (distSq < 4) return;
        if (level.isNight() && distSq > 400) {
            teleportTo(homePos.getX() + 0.5, homePos.getY(), homePos.getZ() + 0.5);
            return;
        }
        getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                new WalkTarget(homePos, 0.8F, 2));
    }

    // === 房屋 ===

    public void setHouse(House house) {
        this.house = house;
        if (house.isValid()) {
            getBrain().setMemory(MemoryModuleType.HOME,
                    GlobalPos.of(level().dimension(), house.center()));
        } else {
            getBrain().eraseMemory(MemoryModuleType.HOME);
        }
    }

    public House getHouse() {
        return house;
    }

    // === 交互 ===

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer sp) {
            NetworkHooks.openScreen(sp,
                    new SimpleMenuProvider((id, inv, p) -> new NPCTradeMenu(id, inv, this),
                            getDisplayName()),
                    buf -> buf.writeInt(getId()));
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    // === 杂项 ===

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    // === GeckoLib ===

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    // === 持久化（Brain + House） ===

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (house.isValid()) {
            House.CODEC.encodeStart(NbtOps.INSTANCE, house)
                    .result().ifPresent(t -> tag.put("House", t));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("House")) {
            House.CODEC.parse(NbtOps.INSTANCE, tag.get("House"))
                    .result().ifPresent(this::setHouse);
        }
    }
}
