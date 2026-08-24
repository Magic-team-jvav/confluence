package org.confluence.mod.common.entity.npc;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.entity.CreatureDefinition;
import org.confluence.mod.common.data.entity.CreatureDefinitionLoader;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.data.saved.HouseHandler;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProgression;
import org.confluence.mod.common.entity.npc.ai.NPCDefenseGoal;
import org.confluence.mod.common.entity.npc.ai.NPCHurtRetreatGoal;
import org.confluence.mod.common.entity.npc.chat.ChatLine;
import org.confluence.mod.common.entity.npc.chat.ChatManager;
import org.confluence.mod.common.entity.npc.chat.NPCChat;
import org.confluence.mod.common.entity.npc.house.House;
import org.confluence.mod.common.entity.npc.house.HouseValidater;
import org.confluence.mod.common.entity.npc.mood.NPCMood;
import org.confluence.mod.common.entity.npc.trade.NPCTradeList;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.confluence.mod.common.entity.npc.trade.NPCTradeOffer;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.util.AchievementUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// 城镇 NPC 的公共实体基础。
public abstract class BaseNPC extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<CompoundTag> DATA_CHAT = SynchedEntityData.defineId(BaseNPC.class, EntityDataSerializers.COMPOUND_TAG);
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");

    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected static final ImmutableList<SensorType<? extends Sensor<? super BaseNPC>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY);

    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.HOME,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.HURT_BY_ENTITY
    );

    protected House house = House.EMPTY;
    protected NPCMood mood;
    @Nullable
    protected NPCChat currentChat;
    protected NPCSpawner.Region region = NPCSpawner.Region.ZERO;
    protected boolean shouldInteract;
    protected BlockPos spawnAtPos = BlockPos.ZERO;
    private boolean spawnAtPosInitialized;
    private final Map<ChatLine, Integer> chatCooldowns = new HashMap<>();
    private int chatForceCooldown = 50;
    private int chatDisplayTicks;
    @Nullable
    private Player tradingPlayer;
    private final NPCCombatProfile combatProfile;
    private int creatureDefinitionRevision = -1;
    private double healthRegenerationProgress;

    public BaseNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile combatProfile) {
        super(type, level);
        this.mood = new NPCMood(type);
        this.combatProfile = combatProfile;
        applyProfileAttributes();
        setHealth(getMaxHealth());
        ensureFixedWeapon();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_CHAT, new CompoundTag());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 250.0)
                .add(Attributes.ARMOR, 15.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 10.0);
    }

    // === Goals ===

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new NPCTradeGoal(this));
        this.goalSelector.addGoal(2, new NPCHurtRetreatGoal(this));
        this.goalSelector.addGoal(4, new NPCDefenseGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
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

    private static final int FIND_HOUSE_INTERVAL_MASK = 511;

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        ServerLevel level = (ServerLevel) level();
        tickBrain(level);
        tickFindHouse(level);
        tickWalkToHome(level);
        tickMood();
        tickHealthRegeneration();
        ChatManager.tickNPC(this);
        if (tickCount % 20 == 0) {
            if (creatureDefinitionRevision != CreatureDefinitionLoader.getRevision())
                applyCreatureDefinition();
            ensureFixedWeapon();
        }

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
        if (tickCount % 20 != 0) return;
        getBrain().getMemory(MemoryModuleType.HOME)
                .filter(home -> home.dimension().equals(level().dimension()))
                .map(GlobalPos::pos)
                .ifPresentOrElse(home -> mood.evaluate(level().getEntitiesOfClass(BaseNPC.class,
                                new AABB(home.offset(-16, -16, -16).getCenter(), home.offset(16, 16, 16).getCenter()))),
                        () -> mood.evaluate(getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElseGet(List::of)));
    }

    // === 对话 ===

    public void setCurrentChat(@Nullable NPCChat chat) {
        this.currentChat = chat;
        this.chatDisplayTicks = chat == null ? 0 : 100;
        if (!level().isClientSide) {
            CompoundTag encoded = chat == null ? new CompoundTag() : NPCChat.CODEC.encodeStart(NbtOps.INSTANCE, chat).result().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).orElseGet(CompoundTag::new);
            entityData.set(DATA_CHAT, encoded, true);
        }
    }

    @Nullable
    public NPCChat getCurrentChat() {
        return currentChat;
    }

    public int getChatDisplayTicks() {
        return chatDisplayTicks;
    }

    public void tickChatCooldowns(List<ChatLine> lines) {
        if (chatForceCooldown > 0) chatForceCooldown--;
        chatCooldowns.keySet().retainAll(lines);
        for (ChatLine line : lines) {
            chatCooldowns.putIfAbsent(line, Math.max(1, line.cooldownTicks()));
        }
        chatCooldowns.replaceAll((line, ticks) -> Math.max(0, ticks - 1));
    }

    public boolean canTriggerChat(ChatLine line) {
        return chatForceCooldown <= 0 && chatCooldowns.getOrDefault(line, Integer.MAX_VALUE) == 0;
    }

    public void markChatTriggered(ChatLine line) {
        chatForceCooldown = 50;
        chatCooldowns.put(line, Math.max(1, (int) (line.cooldownTicks() * (1.0F + random.nextFloat() * 0.5F))));
    }

    @SuppressWarnings("unchecked")
    protected void tickBrain(ServerLevel level) {
        Brain<BaseNPC> brain = (Brain<BaseNPC>) getBrain();
        brain.tick(level, this);
    }

    /// 每 512 tick 重新验证已有房屋；无房时从当前位置尝试发现房屋。
    protected void tickFindHouse(ServerLevel level) {
        if ((tickCount & FIND_HOUSE_INTERVAL_MASK) != 0) return;
        BlockPos scanPos = house.isValid() ? house.center() : blockPosition();
        if (!level.isLoaded(scanPos)) return;

        HouseHandler.INSTANCE.removeHouse(level.dimension(), getUUID());
        House found = HouseValidater.scan(level, scanPos).make(getUUID());
        if (found.isValid() && HouseHandler.INSTANCE.isOccupiedByOther(level.dimension(), found, getUUID())) {
            found = House.EMPTY;
        }
        setHouse(found);
        HouseHandler.INSTANCE.setHouse(this, found);
    }

    /// 有 HOME 记忆时向家移动。
    protected void tickWalkToHome(ServerLevel level) {
        if (!house.isValid() || tradingPlayer != null) return;
        BlockPos homePos = house.center();
        double distSq = blockPosition().distSqr(homePos);
        if (distSq < 4) return;
        if (!LibDateUtils.isNight(level) && distSq <= 400) return;
        if (getLastHurtByMob() != null && tickCount - getLastHurtByMobTimestamp() < 100) return;
        if (getTarget() == null && (tickCount % 20 == 0 || getNavigation().isDone())) {
            getNavigation().moveTo(homePos.getX() + 0.5, homePos.getY(), homePos.getZ() + 0.5, 1.0);
        }
    }

    // === 房屋 ===

    public void setHouse(House house) {
        this.house = house;
        if (house.isValid()) {
            this.spawnAtPos = house.center();
            this.spawnAtPosInitialized = true;
            getBrain().setMemory(MemoryModuleType.HOME, GlobalPos.of(level().dimension(), house.center()));
            NPCSpawner.Region newRegion = new NPCSpawner.Region(house.center());
            NPCSpawner.INSTANCE.moveNPCToAnotherRegion(this, region, newRegion);
            AchievementUtils.noHobo(this, newRegion);
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

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player || target instanceof BaseNPC) return false;
        return target.canBeSeenAsEnemy();
    }

    /// 在基础护甲和永久增益之外实时加入当前世界进度提供的 NPC 防御。
    @Override
    public int getArmorValue() {
        int armor = super.getArmorValue() + NPCCombatProgression.defenseBonus();
        if (hasEffect(ModEffects.DRYADS_BLESSING.get())) {
            armor += LibUtils.switchByDifficulty(level(), blockPosition(), 2, 7, 12, 12);
        }
        return armor;
    }

    /// 子类可在睡眠、被束缚等状态下临时关闭自卫目标。
    public boolean canDefendSelf() {
        return true;
    }

    /// 返回注册项为该 NPC 固定的自卫方式和默认数值。
    public NPCCombatProfile getCombatProfile() {
        return combatProfile;
    }

    /// 按注册默认值或数据包覆盖累计自然恢复，支持低于每秒 1 点的精确速率。
    private void tickHealthRegeneration() {
        if (getHealth() >= getMaxHealth()) {
            healthRegenerationProgress = 0;
            return;
        }
        healthRegenerationProgress += combatProfile.healthRegeneration(this) / 20.0;
        int amount = (int) healthRegenerationProgress;
        if (amount <= 0) return;
        setHealth(Math.min(getMaxHealth(), getHealth() + amount));
        healthRegenerationProgress -= amount;
    }

    /// 返回当前数据包重载轮次中该实体类型的数值覆盖。
    public CreatureDefinition creatureDefinition() {
        return CreatureDefinitionLoader.get(getType());
    }

    /// 恢复注册项规定的固有武器，并处理困难模式武器切换。
    private void ensureFixedWeapon() {
        net.minecraft.world.item.Item weapon = combatProfile.weapon().apply(this);
        if (getMainHandItem().is(weapon)) return;
        setItemSlot(EquipmentSlot.MAINHAND, weapon == net.minecraft.world.item.Items.AIR ? ItemStack.EMPTY : new ItemStack(weapon));
        setDropChance(EquipmentSlot.MAINHAND, 0);
    }

    /// 先恢复注册默认值再应用数据包覆盖，使删除覆盖文件也能回到默认状态。
    private void applyCreatureDefinition() {
        float oldHealth = getHealth();
        float oldMaxHealth = getMaxHealth();
        boolean wasFullHealth = Math.abs(oldHealth - oldMaxHealth) < 0.001F;
        applyProfileAttributes();
        CreatureDefinitionLoader.applyAttributes(this);
        setHealth(wasFullHealth ? getMaxHealth() : Math.min(oldHealth, getMaxHealth()));
        creatureDefinitionRevision = CreatureDefinitionLoader.getRevision();
    }

    /// 将注册 profile 中的全部实体属性默认值写入属性实例。
    private void applyProfileAttributes() {
        UUID obsoleteModifier = PortAttributeModifier.rl2uuid(Confluence.asResource("game_phase_modifier"));
        removeAttributeModifier(Attributes.MAX_HEALTH, obsoleteModifier);
        NPCCombatProfile.AttributesDefaults defaults = combatProfile.attributes();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(defaults.maxHealth());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(defaults.attackDamage());
        getAttribute(Attributes.ARMOR).setBaseValue(defaults.armor());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(defaults.movementSpeed());
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(defaults.followRange());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(defaults.knockbackResistance());
    }

    /// 移除旧版 NPC 阶段属性修饰器；属性不存在时安全跳过。
    private void removeAttributeModifier(Attribute attribute, UUID id) {
        AttributeInstance instance = getAttribute(attribute);
        if (instance != null) instance.removeModifier(id);
    }

    // === 交互 ===

    public List<NPCTradeOffer> selectTradeOffers(List<NPCTradeOffer> offers) {
        return List.copyOf(offers);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            initName();
            InteractionResult commonResult = handleCommonInteraction(serverPlayer, hand);
            if (commonResult != null) return commonResult;
            recordInteraction(serverPlayer);

            var shop = NPCTradeList.getAvailableOffers(serverPlayer, this);
            if (!shop.offers().isEmpty()) {
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((id, inv, ignored) -> new NPCTradeMenu(id, inv, this, shop.offers(), shop.revision()), getDisplayName()), buf -> buf.writeInt(getId()));
                setTradingPlayer(serverPlayer);
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    public @Nullable Player getTradingPlayer() {
        return tradingPlayer;
    }

    public void setTradingPlayer(@Nullable Player tradingPlayer) {
        this.tradingPlayer = tradingPlayer;
    }

    @Nullable
    protected InteractionResult handleCommonInteraction(ServerPlayer player, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND) return InteractionResult.SUCCESS;
        setCustomNameVisible(hasCustomName());
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof ArmorItem armor) {
            swapEquipment(player, hand, armor.getEquipmentSlot());
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown() && held.isEmpty()) {
            removeLookedAtEquipment(player, hand);
            return InteractionResult.PASS;
        }
        return null;
    }

    protected void recordInteraction(ServerPlayer player) {
        // 被"救援"的 NPC 首次交互时，将其正式加入区域
        if (shouldInteract) {
            setShouldInteract(false);
            region = NPCSpawner.getNpcSpawnRegion(player);
            NPCSpawner.INSTANCE.applyBenedictions(this);
            NPCSpawner.INSTANCE.addSpawned(getType());
            NPCSpawner.broadcastMessageToRegion(player.level(), this, Component.translatable("event.confluence.npc.arrived", getType().getDescription(), getName()).withColor(GlobalColors.NPC_ARRIVED.get()));
        }
        // 图鉴记录
        if (!Bestiary.INSTANCE.containsKey(this)) Bestiary.INSTANCE.updateEntry(this, false);
    }

    protected final void initName() {
        if (hasCustomName()) return;
        String name = NPCNames.Loader.getInstance().getRandomName(getType(), getRandom());
        if (name != null) setCustomName(Component.literal(name));
    }

    private void swapEquipment(Player player, InteractionHand hand, EquipmentSlot slot) {
        ItemStack npcItem = getItemBySlot(slot);
        setItemSlot(slot, player.getItemInHand(hand));
        player.setItemInHand(hand, npcItem);
    }

    private void removeLookedAtEquipment(Player player, InteractionHand hand) {
        Vec3 start = player.getEyePosition();
        getBoundingBox().clip(start, start.add(player.getViewVector(0.5F).scale(5))).ifPresent(hit -> {
            double height = hit.y - getY();
            if (height > 1.2) {
                moveEquipmentToHand(player, hand, EquipmentSlot.HEAD);
            } else if (height > 0.7) {
                double edgeX = Math.max(hit.x - getBoundingBox().minX, getBoundingBox().maxX - hit.x);
                double edgeZ = Math.max(hit.z - getBoundingBox().minZ, getBoundingBox().maxZ - hit.z);
                if (Math.min(edgeX, edgeZ) <= 0.5)
                    moveEquipmentToHand(player, hand, EquipmentSlot.CHEST);
            } else if (height > 0.3) {
                moveEquipmentToHand(player, hand, EquipmentSlot.LEGS);
            } else {
                moveEquipmentToHand(player, hand, EquipmentSlot.FEET);
            }
        });
    }

    private void moveEquipmentToHand(Player player, InteractionHand hand, EquipmentSlot slot) {
        ItemStack equipped = getItemBySlot(slot);
        if (equipped.isEmpty()) return;
        player.setItemInHand(hand, equipped);
        setItemSlot(slot, ItemStack.EMPTY);
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

    @Override
    public void tick() {
        super.tick();
        if (chatDisplayTicks > 0 && --chatDisplayTicks == 0) setCurrentChat(null);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_CHAT.equals(key) && level().isClientSide) {
            CompoundTag encoded = entityData.get(DATA_CHAT);
            this.currentChat = encoded.isEmpty() ? null : NPCChat.CODEC.parse(NbtOps.INSTANCE, encoded).result().orElse(null);
            this.chatDisplayTicks = currentChat == null ? 0 : 100;
        }
    }

    // === GeckoLib ===

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, state -> state.setAndContinue(state.isMoving() ? WALK : IDLE)));
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
            PortDataResultExtension.ifSuccess(BlockPos.CODEC.parse(NbtOps.INSTANCE, tag.get("SpawnAtPos")), r -> {
                this.spawnAtPos = r;
                this.spawnAtPosInitialized = true;
            });
        }
    }

    public BlockPos getSpawnAtPos() {
        return spawnAtPos;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide) {
            applyCreatureDefinition();
            ensureFixedWeapon();
        }
        if (!spawnAtPosInitialized) {
            this.spawnAtPos = blockPosition();
            this.spawnAtPosInitialized = true;
        }
    }
}
