package org.confluence.mod.common.entity.npc;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkHooks;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.npc.InteractNPCEvent;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.data.saved.HouseHandler;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.npc.chat.ChatManager;
import org.confluence.mod.common.entity.npc.chat.NPCChat;
import org.confluence.mod.common.entity.npc.dialog.NPCDialogLoader;
import org.confluence.mod.common.entity.npc.house.House;
import org.confluence.mod.common.entity.npc.house.HouseValidater;
import org.confluence.mod.common.entity.npc.mood.MoodData;
import org.confluence.mod.common.entity.npc.mood.NPCMood;
import org.confluence.mod.common.entity.npc.trade.NPCTradeList;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.confluence.mod.common.entity.npc.trade.NPCTradeOffer;
import org.confluence.mod.network.s2c.OpenNPCDialogPacketS2C;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 城镇 NPC 的公共实体基础。
///
/// <p>本类统一管理房屋、区域、心情、基础移动以及服务端交互入口。商店报价仍由
/// 数据包提供，实体只负责决定本次访问允许进入会话快照的报价集合；默认实现保留
/// 全部报价，旅商等具有随机库存的 NPC 可以覆盖该选择步骤。</p>
public abstract class BaseNPC extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<CompoundTag> DATA_CHAT = SynchedEntityData.defineId(BaseNPC.class, EntityDataSerializers.COMPOUND_TAG);
    private static final RawAnimation WALK =
            RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation IDLE =
            RawAnimation.begin().thenLoop("misc.idle");

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
                    MemoryModuleType.NEAREST_HOSTILE,
                    MemoryModuleType.HURT_BY,
                    MemoryModuleType.HURT_BY_ENTITY
            );

    protected House house = House.EMPTY;
    protected NPCMood mood;
    @Nullable
    protected NPCChat currentChat;
    private int chatRevision;
    private int chatDisplayTicks;
    protected NPCSpawner.Region region = NPCSpawner.Region.ZERO;
    protected boolean shouldInteract;
    protected BlockPos spawnAtPos = BlockPos.ZERO;
    private boolean spawnAtPosInitialized;
    private boolean lifecycleRemoved;
    private int lastPanicHurtTimestamp;
    private long panicUntil;
    private boolean panicking;

    public BaseNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
        this.mood = new NPCMood(MoodData.getMoodsFor(type));
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

    // === Goals ===

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player || target instanceof BaseNPC) {
            return false;
        }
        return target.canBeSeenAsEnemy();
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target != null && canAttack(target) ? target : null);
    }

    protected boolean canFightHostiles() {
        return false;
    }

    protected double getHostileDetectionRange() {
        return 10.0;
    }

    public boolean isPanicking() {
        return panicking;
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

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_CHAT, new CompoundTag());
    }

    // === 房屋查找 ===

    private static final int HOUSE_CHECK_MASK = 511;

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        ServerLevel level = (ServerLevel) level();
        tickBrain(level);
        tickHostileActivity(level);
        tickHouse(level);
        if (!panicking && getTarget() == null) {
            tickWalkToHome(level);
        }
        tickMood();
        ChatManager.tickNPC(this);

        // 由于NPCHouseBehaviors#walkToHouse疑似不能触发，于是在tick里判断
        // 过远时传送回自己的出生点
        if (spawnAtPos != null &&
                (house == null || !house.contains(blockPosition())) &&
                level().getGameTime() % 100 == 2 && level().players().stream().noneMatch(player -> player.distanceToSqr(this) < 32 * 32)
        ) {
            double sqr = blockPosition().distSqr(spawnAtPos);
            if (sqr > 64 * 64 || (sqr > 500 && LibDateUtils.isNight(level()))) {
                teleportTo(spawnAtPos.getX(), spawnAtPos.getY(), spawnAtPos.getZ());
            }
        }
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
        if (level().isClientSide) {
            chatDisplayTicks = 100;
            return;
        }
        CompoundTag data = new CompoundTag();
        NPCChat.CODEC.encodeStart(NbtOps.INSTANCE, chat).result().ifPresent(encoded -> data.put("Chat", encoded));
        data.putInt("Revision", ++chatRevision);
        entityData.set(DATA_CHAT, data);
    }

    @Nullable
    public NPCChat getCurrentChat() {
        return currentChat;
    }

    public int getChatDisplayTicks() {
        return chatDisplayTicks;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key != DATA_CHAT || !level().isClientSide) return;
        CompoundTag data = entityData.get(DATA_CHAT);
        if (!data.contains("Chat", Tag.TAG_COMPOUND)) return;
        NPCChat.CODEC.parse(NbtOps.INSTANCE, data.get("Chat")).result().ifPresent(this::setCurrentChat);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && chatDisplayTicks > 0) chatDisplayTicks--;
    }

    @SuppressWarnings("unchecked")
    protected void tickBrain(ServerLevel level) {
        Brain<BaseNPC> brain = (Brain<BaseNPC>) getBrain();
        brain.tick(level, this);
    }

    @SuppressWarnings("unchecked")
    protected void tickHostileActivity(ServerLevel level) {
        Brain<BaseNPC> brain = (Brain<BaseNPC>) getBrain();
        NearestVisibleLivingEntities visible = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
        double detectionRangeSqr = getHostileDetectionRange() * getHostileDetectionRange();
        java.util.Optional<LivingEntity> hostile = visible.findClosest(target -> target instanceof Enemy && distanceToSqr(target) <= detectionRangeSqr);
        brain.setMemory(MemoryModuleType.NEAREST_HOSTILE, hostile);

        if (canFightHostiles()) {
            panicking = false;
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive() || !canAttack(target) || !hasLineOfSight(target) || distanceToSqr(target) > detectionRangeSqr) {
                setTarget(hostile.orElse(null));
            }
            return;
        }

        LivingEntity hurtBy = getLastHurtByMob();
        int hurtTimestamp = getLastHurtByMobTimestamp();
        if (hurtTimestamp != lastPanicHurtTimestamp) {
            lastPanicHurtTimestamp = hurtTimestamp;
            if (hurtBy instanceof Enemy) {
                panicUntil = level.getGameTime() + 100;
            }
        }
        if (hostile.isPresent()) {
            panicUntil = Math.max(panicUntil, level.getGameTime() + 20);
        }

        boolean shouldPanic = level.getGameTime() < panicUntil;
        if (!shouldPanic) {
            if (panicking) {
                getNavigation().stop();
            }
            panicking = false;
            return;
        }

        panicking = true;
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        LivingEntity threat = hostile.orElse(hurtBy instanceof Enemy ? hurtBy : null);
        if (threat != null && (getNavigation().isDone() || tickCount % 10 == 0)) {
            Vec3 destination = DefaultRandomPos.getPosAway(this, 16, 7, threat.position());
            if (destination != null) {
                getNavigation().moveTo(destination.x, destination.y, destination.z, 1.95);
            }
        }
    }

    /// 每 512 tick 重新验证已有房屋；没有房屋时从当前位置尝试发现。
    protected void tickHouse(ServerLevel level) {
        if ((tickCount & HOUSE_CHECK_MASK) != 0) return;
        boolean hadHouse = house.isValid();
        if (hadHouse && house.uuid().filter(getUUID()::equals).isEmpty()) {
            releaseHouse();
            return;
        }
        BlockPos start = hadHouse ? house.center() : blockPosition();
        if (!level.isLoaded(start)) return;
        HouseHandler.INSTANCE.removeHouse(level.dimension(), getUUID());
        HouseValidater.Result result = HouseValidater.scan(level, start);
        House found = result.make(getUUID());
        if (!found.isValid() || HouseHandler.INSTANCE.isOccupiedByOther(level.dimension(), found, getUUID())) {
            setHouse(House.EMPTY);
            return;
        }
        setHouse(found);
        HouseHandler.INSTANCE.setHouse(this, found);
    }

    public void releaseHouse() {
        HouseHandler.INSTANCE.removeHouse(level().dimension(), getUUID());
        setHouse(House.EMPTY);
    }

    /// 有 HOME 记忆时向家移动。
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
            NPCSpawner.Region newRegion = new NPCSpawner.Region(house.center());
            NPCSpawner.INSTANCE.moveNPCToAnotherRegion(this, region, newRegion);
        } else {
            getBrain().eraseMemory(MemoryModuleType.HOME);
        }
    }

    public House getHouse() {
        return house;
    }

    // === Region ===

    public NPCSpawner.Region getRegion() {
        return region;
    }

    public void setRegion(NPCSpawner.Region region) {
        this.region = region;
    }

    public boolean shouldInteract() {
        return shouldInteract;
    }

    public void setShouldInteract(boolean should) {
        this.shouldInteract = should;
    }

    public java.util.List<NPCTradeOffer> selectTradeOffers(
            java.util.List<NPCTradeOffer> offers) {
        return java.util.List.copyOf(offers);
    }

    // === 交互 ===

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.SUCCESS;
        if (!hasCustomName()) setCustomName(getType().getDescription());
        if (hand == InteractionHand.OFF_HAND) return InteractionResult.SUCCESS;
        setCustomNameVisible(true);

        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof ArmorItem armor) {
            swapItem(player, armor.getEquipmentSlot(), hand, stack);
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown()) {
            if (!stack.isEmpty()) swapItem(player, EquipmentSlot.MAINHAND, hand, stack);
            else removeHitEquipment(player, hand);
            return InteractionResult.SUCCESS;
        }

        recordInteraction(serverPlayer);
        InteractNPCEvent event = new InteractNPCEvent(this, serverPlayer);
        if (MinecraftForge.EVENT_BUS.post(event)) return event.getInteractionResult();
        event.execute(() -> performDefaultInteraction(serverPlayer));
        return event.getInteractionResult();
    }

    private void swapItem(Player player, EquipmentSlot slot, InteractionHand hand, ItemStack stack) {
        ItemStack previous = getItemBySlot(slot);
        setItemSlot(slot, stack.copy());
        player.setItemInHand(hand, previous);
    }

    private void removeHitEquipment(Player player, InteractionHand hand) {
        Vec3 from = player.getEyePosition();
        Vec3 hit = getBoundingBox().clip(from, from.add(player.getViewVector(0.5F).scale(6.0))).orElse(null);
        if (hit == null) return;
        AABB bounds = getBoundingBox();
        double relativeHeight = (hit.y - bounds.minY) / bounds.getYsize();
        EquipmentSlot slot;
        if (relativeHeight > 0.75) slot = EquipmentSlot.HEAD;
        else if (relativeHeight > 0.45) {
            double edge = Math.min(Math.min(hit.x - bounds.minX, bounds.maxX - hit.x), Math.min(hit.z - bounds.minZ, bounds.maxZ - hit.z));
            slot = edge < 0.15 ? EquipmentSlot.MAINHAND : EquipmentSlot.CHEST;
        } else if (relativeHeight > 0.2) slot = EquipmentSlot.LEGS;
        else slot = EquipmentSlot.FEET;
        ItemStack equipped = getItemBySlot(slot);
        if (equipped.isEmpty()) return;
        player.setItemInHand(hand, equipped);
        setItemSlot(slot, ItemStack.EMPTY);
    }

    private void recordInteraction(ServerPlayer player) {
        if (shouldInteract) {
            setShouldInteract(false);
            region = NPCSpawner.getNpcSpawnRegion(player);
            NPCSpawner.INSTANCE.setNPCAlive(region, getType(), true);
            NPCSpawner.INSTANCE.applyBenedictions(this);
            NPCSpawner.INSTANCE.addSpawned(getType());
            NPCSpawner.broadcastMessageToRegion(player.level(), this, Component.translatable("event.confluence.npc.arrived", getType().getDescription(), getName()).withColor(GlobalColors.NPC_ARRIVED.get()));
        }
        if (!Bestiary.INSTANCE.containsKey(this)) Bestiary.INSTANCE.updateEntry(this, false);
    }

    protected void performDefaultInteraction(ServerPlayer player) {
        var shop = NPCTradeList.getAvailableOffers(player, this);
        if (!shop.offers().isEmpty()) {
            NetworkHooks.openScreen(player, new SimpleMenuProvider((id, inv, ignored) -> new NPCTradeMenu(id, inv, this, shop.offers(), shop.revision()), getDisplayName()), buf -> buf.writeInt(getId()));
        } else if (NPCDialogLoader.getInstance().getDialog(getType()) != null) {
            Confluence.NETWORK_HANDLER.sendToPlayer(player, new OpenNPCDialogPacketS2C(getId()));
        }
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "movement",
                5,
                state -> state.setAndContinue(state.isMoving() ? WALK : IDLE)));
    }

    // === 持久化（Brain + House） ===

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (house.isValid()) {
            PortDataResultExtension.ifSuccess(House.CODEC.encodeStart(NbtOps.INSTANCE, house), t -> tag.put("House", t));
        }
        PortDataResultExtension.ifSuccess(NPCSpawner.Region.CODEC.encodeStart(NbtOps.INSTANCE, region), t -> tag.put("Region", t));
        tag.putBoolean("ShouldInteract", shouldInteract);
        PortDataResultExtension.ifSuccess(BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, spawnAtPos), t -> tag.put("SpawnAtPos", t));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("House")) {
            House.CODEC.parse(NbtOps.INSTANCE, tag.get("House"))
                    .result().ifPresent(this::setHouse);
        }
        if (tag.contains("Region")) {
            PortDataResultExtension.ifSuccess(NPCSpawner.Region.CODEC.parse(NbtOps.INSTANCE, tag.get("Region")), r -> this.region = r);
        }
        this.shouldInteract = tag.getBoolean("ShouldInteract");
        if (tag.contains("SpawnAtPos")) {
            PortDataResultExtension.ifSuccess(BlockPos.CODEC.parse(NbtOps.INSTANCE, tag.get("SpawnAtPos")), this::setSpawnAtPos);
        }
    }

    public BlockPos getSpawnAtPos() {
        return spawnAtPos;
    }

    public void setSpawnAtPos(BlockPos spawnAtPos) {
        this.spawnAtPos = spawnAtPos;
        this.spawnAtPosInitialized = true;
    }

    public boolean markLifecycleRemoved() {
        if (lifecycleRemoved) return false;
        lifecycleRemoved = true;
        return true;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!spawnAtPosInitialized) setSpawnAtPos(blockPosition());
    }
}
