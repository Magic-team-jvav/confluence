package org.confluence.terraentity.entity.monster.slime;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.color.FloatRGB;
import org.confluence.terraentity.config.ServerConfig;
import org.confluence.terraentity.entity.boss.KingSlime;
import org.confluence.terraentity.entity.util.DeathAnimOptions;
import org.confluence.terraentity.init.TEParticles;
import org.confluence.terraentity.init.TETags;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.mixin.accessor.SlimeAccessor;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn;

public class BaseSlime extends Slime implements DeathAnimOptions {
    static FloatRGB SlimeColor_Green = FloatRGB.fromInteger(0x48E920);
    static FloatRGB SlimeColor_Blue = FloatRGB.fromInteger(0x73bcf4);
    static FloatRGB SlimeColor_Purple = FloatRGB.fromInteger(0xf334f8);
    static FloatRGB SlimeColor_Pink = FloatRGB.fromInteger(0xFF87B3);
    static FloatRGB SlimeColor_Jungle = FloatRGB.fromInteger(0x9ae920);
    static FloatRGB SlimeColor_Ice = FloatRGB.fromInteger(0xB3F0EA);

    public static float slimeWaterMoveSpeed = 0.2f;

    private final int size;
    private final FloatRGB color;
    private int honeySoakTime;

    public BaseSlime(EntityType<? extends Slime> slime, Level level, int color, int size) {
        super(slime, level);
        this.size = size;
        // setSize在constructor中调用时size还没更新，再变一遍
        setSize(size, false);
        if (getType() == TEMonsterEntities.CRIMSLIME.get()) {
            setSize(getRandom().nextInt(1, 4), false);
        }
        this.color = FloatRGB.fromInteger(color);
        this.honeySoakTime = 0;

    }

    Predicate<FloatRGB> colorTest = c ->
            c.equals(SlimeColor_Green) ||
            c.equals(SlimeColor_Blue) ||
            c.equals(SlimeColor_Purple) ||
            c.equals(SlimeColor_Pink) ||
            c.equals(SlimeColor_Ice) ||
            c.equals(SlimeColor_Jungle) ;

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.removeAllGoals(gt -> true);
        this.targetSelector.addGoal(1, new KingSlime.HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (liv) -> {
            return Math.abs(liv.getY() - this.getY()) <= 4.0 && (!colorTest.test(this.color) || this.level().isNight());
        }));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }


    public static AttributeSupplier.Builder createSlimeAttributes(float attackDamage, int armor, float maxHealth) {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, attackDamage)
                .add(Attributes.ARMOR, armor)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, slimeWaterMoveSpeed)
                .add(Attributes.MAX_HEALTH, maxHealth);
    }

    public static boolean checkSlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level level)) {
            return false;
        }
        if (ServerConfig.SPAWN_WITHOUT_LIGHT.get()) {
            if (!checkMobSpawnRules(type, pLevel, pSpawnType, pPos, pRandom)) {
                return false;
            }
        } else {
            if (!isDarkEnoughToSpawn(pLevel, pPos, pRandom) || !checkMobSpawnRules(type, pLevel, pSpawnType, pPos, pRandom)) {
                return false;
            }
        }


        if (type == TEMonsterEntities.YELLOW_SLIME.get() || type == TEMonsterEntities.RED_SLIME.get() || type == TEMonsterEntities.DESERT_SLIME.get()) {
            return pLevel.getBrightness(LightLayer.SKY, pPos) == 0 && pPos.getY() > 30;
        } else if (type == TEMonsterEntities.BLACK_SLIME.get() || type == TEMonsterEntities.DUNGEON_SLIME.get()) {
            return pLevel.getBrightness(LightLayer.SKY, pPos) == 0 && pPos.getY() <= 30;
        } else if (type == TEMonsterEntities.LAVA_SLIME.get()) {  // 新增岩浆史莱姆的限制条件
            int y = pPos.getY();
            return y >= 30 && y <= 100;
        } else if (type == TEMonsterEntities.BLUE_SLIME.get() || type == TEMonsterEntities.GREEN_SLIME.get() || type == TEMonsterEntities.PURPLE_SLIME.get()
                || type == TEMonsterEntities.ICE_SLIME.get() || type == TEMonsterEntities.JUNGLE_SLIME.get()
                || type == TEMonsterEntities.PINK_SLIME.get() || type == TEMonsterEntities.SWAMP_SLIME.get() || type == TEMonsterEntities.TROPIC_SLIME.get()) {
            int y = pPos.getY();
            return y > 30 && y < 260 && level.isDay() && pLevel.canSeeSky(pPos);
        }

        // 剩下的条件用方块的isValidSpawn方法
        return false;
    }

    @Override
    public void tick() {
        resetFallDistance();
        if (onGround() && !((SlimeAccessor) this).isWasOnGround()) {
            int i = getSize();
            for (int j = 0; j < i * 8; ++j) {
                float f = random.nextFloat() * Mth.TWO_PI;
                float f1 = random.nextFloat() * 0.5F + 0.5F;
                float f2 = Mth.sin(f) * (float) i * 0.5F * f1;
                float f3 = Mth.cos(f) * (float) i * 0.5F * f1;
                level().addParticle(TEParticles.ITEM_GEL.get(), getX() + (double) f2, getY(), getZ() + (double) f3, color.red(), color.green(), color.blue());
            }
        }
        if (this.getType().equals(TEMonsterEntities.LAVA_SLIME.get())) {
            if (isInWater()) {
                this.hurt(this.level().damageSources().freeze(), 0.8F);
            }
        }
        if (!level().isClientSide && tickCount % 20 == 0 && (this.getType().equals(TEMonsterEntities.GREEN_SLIME.get()) ||
                this.getType().equals(TEMonsterEntities.BLUE_SLIME.get()) ||
                this.getType().equals(TEMonsterEntities.PURPLE_SLIME.get()))) {
            addHoneySoakTime();
        }
        if (this.getVehicle() != null) {
            this.setYRot(this.getVehicle().getYRot());
        }
        super.tick();
    }

    private void addHoneySoakTime() {
        if (!level().isClientSide && level().getBlockState(this.blockPosition()).is(TETags.Blocks.HONEY)) {
            honeySoakTime++;
            if (honeySoakTime >= 120) {
                HoneySlime slime = TEMonsterEntities.HONEY_SLIME.get().create(level());
                if (slime != null) {
                    slime.setSize(2, true);
                    slime.setPos(this.position());
                    slime.setXRot(this.getXRot());
                    slime.setYRot(this.getYRot());
                    level().addFreshEntity(slime);
                }
                this.remove(Entity.RemovalReason.DISCARDED);
            }
        } else {
            honeySoakTime = 0;
        }
    }

    public Vec3 getVehicleAttachmentPoint(Entity entity) {
        return super.getVehicleAttachmentPoint(entity).add(0, 0.7f, 0);
    }

    @Override
    protected boolean spawnCustomParticles() {
        return true;
    }

    @Override
    public void setSize(int pSize, boolean resetHealth) {
        int i = Mth.clamp(size, 1, 127);
        entityData.set(ID_SIZE, i);
        reapplyPosition();
        refreshDimensions();
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2F + 0.1F * i);
        if (resetHealth) {
            this.setHealth(this.getMaxHealth());
        }
        this.xpReward = i;
    }

    @Override
    public void remove(@NotNull RemovalReason removalReason) {
        brain.clearMemories();
        setRemoved(removalReason);
//        invalidateCaps();
    }

    @Override
    public float[] getBloodColor() {
        return color.mixture(new FloatRGB(0, 0, 0), 0.5f).toArray();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (getType() == TEMonsterEntities.TROPIC_SLIME.get() && pSource.is(DamageTypes.DROWN)) {
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public boolean isInWater() {
        if (getType() == TEMonsterEntities.TROPIC_SLIME.get()) {
            return false;
        }
        return super.isInWater();
    }

    @Override
    protected boolean isDealsDamage() {
        return this.isEffectiveAi();
    }

    @Override
    protected void dealDamage(@NotNull LivingEntity pLivingEntity) {
        if (isAlive()) {
            if (this.isAlive() && this.isWithinMeleeAttackRange(pLivingEntity) && this.hasLineOfSight(pLivingEntity) && pLivingEntity.hurt(damageSources().mobAttack(this), getAttackDamage())) {
                playSound(SoundEvents.SLIME_ATTACK, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
                DamageSource damagesource = this.damageSources().mobAttack(this);
                if (this.level() instanceof ServerLevel serverlevel)
                    EnchantmentHelper.doPostAttackEffects(serverlevel, pLivingEntity, damagesource);
                if (getType() == TEMonsterEntities.ICE_SLIME.get()) {
                    if (TEUtils.isMaster(level(), blockPosition()) || (TEUtils.isAtLeastExpert(level(), blockPosition()) && level().random.nextBoolean())) {
                        pLivingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0), this);
                    }
                } else if (getType() == TEMonsterEntities.LAVA_SLIME.get()) {
                    pLivingEntity.setRemainingFireTicks(100);
                }
            }
        }
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (level() instanceof ServerLevel level && getType() == TEMonsterEntities.LAVA_SLIME.get() && TEUtils.isAtLeastExpert(level, blockPosition())) {
            BlockPos containing = BlockPos.containing(position());
            BlockState blockState = level.getBlockState(containing);
            if (blockState.canBeReplaced(Fluids.LAVA) && !blockState.getFluidState().isSourceOfType(Fluids.LAVA)) {
                level.setBlock(containing, Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 14), Block.UPDATE_ALL);
                level.scheduleTick(containing, Blocks.LAVA, 2);
            }
        }
    }

    @Override
    public boolean isInWall() { // 防止骑僵尸时窒息
        Entity vehicle = getControlledVehicle();
        return vehicle == null ? super.isInWall() : vehicle.isInWall();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || (source.is(DamageTypeTags.IS_FIRE) && getType() == TEMonsterEntities.LAVA_SLIME.get());
    }
}
