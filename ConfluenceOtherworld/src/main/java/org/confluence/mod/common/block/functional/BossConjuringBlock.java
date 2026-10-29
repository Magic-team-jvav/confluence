package org.confluence.mod.common.block.functional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipBlockItem;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.SimpleGeoItemRenderer;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BossMultiplayerEnhancement;
import org.confluence.mod.common.entity.boss.TwinEyeEncounter;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.entity.BossEntities;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

/// 机械 Boss 召唤装置：夜间自动启动，动画与粒子预热 20 秒后召唤对应 Boss；潜行右键立即召唤并消耗自身。
public class BossConjuringBlock extends Block implements EntityBlock {
    /// 装置是否处于召唤预热中；服务端写入，客户端据此播放动画与粒子。
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    /// 预热时长：20 秒。
    public static final int WINDUP_TICKS = 400;
    /// 广播基站每隔一个动画循环（1 秒）放一次声响特效。
    private static final int ANTENNA_INTERVAL = 20;
    /// 天线动画到达最高点的 tick（0.625 秒处）。
    private static final int ANTENNA_PEAK_TICK = 12;
    /// 地震锤一次砸地动画的时长（0.7083 秒）。
    private static final int EARTHSHAKER_INTERVAL = 14;
    /// 召唤时把多远的玩家登记为遭遇参与者。
    private static final double SUMMONER_RANGE = 64.0;

    public static final VoxelShape EARTHSHAKER_SHAPE = box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    public static final VoxelShape LIGHTHOUSE_SHAPE = box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
    public static final VoxelShape BASE_STATION_SHAPE = box(2.0, 0.0, 5.0, 14.0, 14.0, 11.0);

    /// 灯头中心（模型像素换算到方块坐标）。
    private static final Vec3 LAMP_HEAD = new Vec3(0.5, 0.906, 0.5);
    /// 天线顶端：模型 x/z 中心为 (−2.5, 1.5) 像素，激活动画最高点再抬升 8 像素。
    private static final Vec3 ANTENNA_TIP = new Vec3(0.5 - 2.5 / 16.0, (14.0 + 8.0) / 16.0, 0.5 + 1.5 / 16.0);
    /// 地震锤贴地位置。
    private static final Vec3 GROUND_CENTER = new Vec3(0.5, 0.04, 0.5);

    private final Variant variant;
    private final VoxelShape shape;

    public BossConjuringBlock(Properties properties, Variant variant, VoxelShape shape) {
        super(properties);
        this.variant = variant;
        this.shape = shape;
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    public Variant getVariant() {
        return variant;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return LibUtils.getTicker(type, FunctionalBlocks.BOSS_CONJURING_ENTITY.get(), BEntity::tick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof BEntity entity)) return InteractionResult.PASS;
        if (!LibDateUtils.isNight(level)) {
            player.displayClientMessage(Component.translatable("message.confluence.boss_conjuring.night_only"), true);
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown()) {
            // 潜行右键立即召唤，成功后装置本身被消耗。
            if (variant.summon((ServerLevel) level, Vec3.atCenterOf(pos), player)) {
                level.removeBlock(pos, false);
                player.displayClientMessage(Component.translatable("message.confluence.boss_conjuring.summoned"), true);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getValue(ACTIVE)) {
            player.displayClientMessage(Component.translatable("message.confluence.boss_conjuring.counting",
                    Math.max(0, entity.remainingWindup(level)) / 20), true);
        } else {
            entity.beginWindup(level, pos, state);
            player.displayClientMessage(Component.translatable("message.confluence.boss_conjuring.activated", WINDUP_TICKS / 20), true);
        }
        return InteractionResult.SUCCESS;
    }

    /// 客户端表现：灯塔常驻旋转光、广播基站每秒一次声响、地震锤每轮砸地一次震荡波。
    private void clientEffects(BEntity entity, Level level, BlockPos pos) {
        Vec3 origin = Vec3.atLowerCornerOf(pos);
        int age = entity.clientAge();
        switch (variant) {
            case TWINS -> entity.ensureBeacon(level, origin.add(LAMP_HEAD));
            case SKELETON_PRIME -> {
                if (age % ANTENNA_INTERVAL == ANTENNA_PEAK_TICK) spawnEmitter(level, origin.add(ANTENNA_TIP), variant.particle());
            }
            case DESTROYER -> {
                if (age % EARTHSHAKER_INTERVAL == 0) spawnEmitter(level, origin.add(GROUND_CENTER), variant.particle());
            }
        }
    }

    private static void spawnEmitter(Level level, Vec3 pos, ResourceLocation particle) {
        ParticleEmitter emitter = new ParticleEmitter(level, pos, particle);
        emitter.hideOutline = true;
        MolangParticleEngine.INSTANCE._addEmitter(emitter);
    }

    /// 三个装置的差异全部收进枚举：资源名、粒子与召唤逻辑。
    public enum Variant {
        /// 地震锤 → 毁灭者。
        DESTROYER("earthshaker_device", "earthshaker_shockwave", "tooltip.confluence.earthshaker_device"),
        /// “灯塔”装置 → 双子魔眼。
        TWINS("dual_flash_lighthouse", "dual_flash_beacon", "tooltip.confluence.dual_flash_lighthouse"),
        /// “屠杀”广播基站-66.6MHz → 机械骷髅王。
        SKELETON_PRIME("phantom_base_station", "phantom_sound_wave", "tooltip.confluence.phantom_base_station");

        private final String path;
        private final String particle;
        private final String tooltipKey;

        Variant(String path, String particle, String tooltipKey) {
            this.path = path;
            this.particle = particle;
            this.tooltipKey = tooltipKey;
        }

        public String path() {
            return path;
        }

        public ResourceLocation particle() {
            return Confluence.asResource(particle);
        }

        public ResourceLocation geoModel() {
            return Confluence.asResource("geo/block/" + path + ".geo.json");
        }

        public ResourceLocation geoAnimation() {
            return Confluence.asResource("animations/block/" + path + ".animation.json");
        }

        public ResourceLocation geoTexture() {
            return Confluence.asResource("textures/block/" + path + ".png");
        }

        public List<Component> tooltips() {
            return List.of(
                    Component.translatable(tooltipKey + ".line1").withStyle(ChatFormatting.GRAY),
                    Component.translatable(tooltipKey + ".line2").withStyle(ChatFormatting.GRAY),
                    Component.translatable(tooltipKey + ".quote").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
            );
        }

        public boolean summon(ServerLevel level, Vec3 pos, @Nullable Player summoner) {
            return switch (this) {
                case TWINS -> TwinEyeEncounter.summon(level, pos, summoner);
                case DESTROYER -> summonSingle(level, BossEntities.THE_DESTROYER.get(), pos.add(0, 2.0, 0), summoner);
                case SKELETON_PRIME -> summonSingle(level, BossEntities.SKELETRON_PRIME.get(), pos.add(0, 3.0, 0), summoner);
            };
        }

        /// 与双子魔眼召唤一致：先完成生成事件与人数缩放，再把召唤者登记为本次遭遇参与者。
        private static boolean summonSingle(ServerLevel level, EntityType<? extends Mob> type, Vec3 pos, @Nullable Player summoner) {
            Mob boss = type.create(level);
            if (boss == null) return false;
            boss.setPos(pos.x, pos.y, pos.z);
            EventHooks.finalizeMobSpawn(boss, level, level.getCurrentDifficultyAt(boss.blockPosition()), MobSpawnType.SPAWN_EGG, null);
            if (boss.isSpawnCancelled()) {
                boss.discard();
                return false;
            }
            BossMultiplayerEnhancement.apply(boss, true);
            boss.setHealth(boss.getMaxHealth());
            if (summoner != null && boss instanceof BaseBoss baseBoss) baseBoss.initializeSummonedCombat(summoner);
            if (!level.addFreshEntity(boss)) {
                boss.discard();
                return false;
            }
            return true;
        }
    }

    public static class BEntity extends BlockEntity implements GeoBlockEntity {
        private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
        private static final RawAnimation ACTIVE_ANIMATION = RawAnimation.begin().thenLoop("active");

        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
        /// 预热开始的游戏刻；随方块实体持久化，读档后继续计时。
        private long activatedAt;
        private int clientAge;
        private @Nullable ParticleEmitter beacon;

        public BEntity(BlockPos pos, BlockState state) {
            super(FunctionalBlocks.BOSS_CONJURING_ENTITY.get(), pos, state);
        }

        public static void tick(Level level, BlockPos pos, BlockState state, BEntity entity) {
            if (level.isClientSide) {
                entity.clientTick(level, pos, state);
            } else {
                entity.serverTick((ServerLevel) level, pos, state);
            }
        }

        int clientAge() {
            return clientAge;
        }

        int remainingWindup(Level level) {
            return (int) Math.max(0, WINDUP_TICKS - (level.getGameTime() - activatedAt));
        }

        void beginWindup(Level level, BlockPos pos, BlockState state) {
            activatedAt = level.getGameTime();
            setChanged();
            level.setBlockAndUpdate(pos, state.setValue(ACTIVE, true));
        }

        private void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
            if (!(state.getBlock() instanceof BossConjuringBlock conjuring)) return;
            if (!state.getValue(ACTIVE)) {
                // 白天静置，入夜后自行启动。
                if (LibDateUtils.isNight(level)) beginWindup(level, pos, state);
                return;
            }
            if (!LibDateUtils.isNight(level)) {
                setChanged();
                level.setBlockAndUpdate(pos, state.setValue(ACTIVE, false));
                return;
            }
            if (remainingWindup(level) > 0) return;
            Player summoner = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SUMMONER_RANGE, false);
            if (conjuring.variant.summon(level, Vec3.atCenterOf(pos), summoner)) {
                level.removeBlock(pos, false);
            } else {
                // 召唤失败时重新计时，避免每刻重试。
                activatedAt = level.getGameTime();
                setChanged();
            }
        }

        private void clientTick(Level level, BlockPos pos, BlockState state) {
            if (!(state.getBlock() instanceof BossConjuringBlock conjuring)) return;
            if (!state.getValue(ACTIVE)) {
                clientAge = 0;
                releaseBeacon();
                return;
            }
            clientAge++;
            conjuring.clientEffects(this, level, pos);
        }

        void ensureBeacon(Level level, Vec3 pos) {
            if (beacon == null || beacon.isRemoved()) {
                beacon = new ParticleEmitter(level, pos, Variant.TWINS.particle());
                beacon.hideOutline = true;
                MolangParticleEngine.INSTANCE._addEmitter(beacon);
            }
            beacon.active = true;
        }

        private void releaseBeacon() {
            if (beacon != null) {
                beacon.remove();
                beacon = null;
            }
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            tag.putLong("ActivatedAt", activatedAt);
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            activatedAt = tag.getLong("ActivatedAt");
        }

        @Override
        public void setRemoved() {
            releaseBeacon();
            super.setRemoved();
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            controllers.add(new AnimationController<>(this, "main", 0, state ->
                    state.setAndContinue(state.getAnimatable().getBlockState().getValue(ACTIVE) ? ACTIVE_ANIMATION : IDLE)));
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }
    }

    public static class BItem extends TooltipBlockItem implements GeoItem {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
        private final Variant variant;

        public BItem(BossConjuringBlock block, ModRarity rarity) {
            super(block, new Properties(), rarity, block.getVariant().tooltips());
            this.variant = block.getVariant();
        }

        @Override
        public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
            consumer.accept(new SimpleGeoItemRenderer<BItem>(variant.geoModel(), variant.geoTexture(), variant.geoAnimation()));
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }
    }
}
