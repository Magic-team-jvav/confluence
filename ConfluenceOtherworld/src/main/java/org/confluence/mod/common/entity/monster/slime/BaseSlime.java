package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.CanBeHostileCondition;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.SlimeHopAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.BaseMonster;
import org.jetbrains.annotations.Nullable;

public class BaseSlime extends BaseMonster {
    protected final int slimeColor;
    protected final boolean passiveByDay;
    /** 泰拉瑞亚击退抗性百分比，-40 到 100+ */
    protected final float terrariaKbResist;
    private long lastHurtTime;

    public BaseSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level);
        this.slimeColor = 0x48E920;
        this.passiveByDay = false;
        this.terrariaKbResist = 0;
    }

    protected BaseSlime(EntityType<? extends BaseSlime> type, Level level, int slimeColor) {
        super(type, level);
        this.slimeColor = slimeColor;
        this.passiveByDay = false;
        this.terrariaKbResist = 0;
    }

    protected BaseSlime(EntityType<? extends BaseSlime> type, Level level,
                        int slimeColor, boolean passiveByDay) {
        super(type, level);
        this.slimeColor = slimeColor;
        this.passiveByDay = passiveByDay;
        this.terrariaKbResist = 0;
    }

    protected BaseSlime(EntityType<? extends BaseSlime> type, Level level,
                        int slimeColor, boolean passiveByDay, float terrariaKbResist) {
        super(type, level);
        this.slimeColor = slimeColor;
        this.passiveByDay = passiveByDay;
        this.terrariaKbResist = terrariaKbResist;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data,
                                        @Nullable CompoundTag tag) {
        double scale = switch (difficulty.getDifficulty()) {
            case PEACEFUL, EASY -> 0.75;
            case NORMAL -> 1.0;
            case HARD -> 1.5;
        };
        if (scale != 1.0) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                    this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * scale);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                    this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * scale);
            this.setHealth(this.getMaxHealth());
        }
        return super.finalizeSpawn(level, difficulty, spawnType, data, tag);
    }

    /**
     * 是否处于敌对状态。被动史莱姆在白天且露天时不会主动攻击，除非被激怒。
     */
    public boolean canBeHostile() {
        if (!passiveByDay) return true;
        if (level().isNight()) return true;
        if (!level().canSeeSky(blockPosition())) return true;
        if (level().getGameTime() - lastHurtTime < 200) return true;
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (result && !level().isClientSide) {
            lastHurtTime = level().getGameTime();
            if (source.getEntity() instanceof LivingEntity attacker) {
                setTarget(attacker);
            }
        }
        return result;
    }

    @Override
    protected BTRoot createBT() {
        BTNode combat = SequenceNode.of(
                new HasTargetCondition(this),
                new SlimeHopAction(this, true));

        BTNode idle = SequenceNode.of(
                new WaitAction(20 + random.nextInt(40)),
                new SlimeHopAction(this, false));

        if (passiveByDay) {
            return new BTRoot() {
                @Override
                protected BTNode createTree() {
                    return SelectorNode.of(
                            SequenceNode.of(new CanBeHostileCondition(BaseSlime.this), combat),
                            idle);
                }
            };
        } else {
            return new BTRoot() {
                @Override
                protected BTNode createTree() {
                    return SelectorNode.of(combat, idle);
                }
            };
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getTarget() == null && canBeHostile() && tickCount % 40 == 0) {
            setTarget(level().getNearestPlayer(this, 16));
        }
    }

    // === 子类可重写的行为钩子 ===

    /** 攻击目标后触发，用于附加效果（如冰霜减速） */
    protected void onAttackTarget(LivingEntity target) {}

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && target instanceof LivingEntity living) {
            onAttackTarget(living);
        }
        return result;
    }

    /** 是否免疫火焰伤害 */
    protected boolean isFireImmune() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return isFireImmune();
    }

    /** 在水中是否受伤 */
    protected boolean hurtByWater() {
        return false;
    }

    /** 是否免疫溺水 */
    protected boolean ignoreDrowning() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (onGround()) {
            resetFallDistance();
        }
        if (hurtByWater() && isInWater()) {
            hurt(damageSources().drown(), 2.0F);
        }
    }

    @Override
    public boolean isInWater() {
        if (ignoreDrowning()) return false;
        return super.isInWater();
    }

    @org.jetbrains.annotations.Nullable
    protected ParticleOptions getGelParticle() {
        return null;
    }

    // === 属性工厂方法 ===

    /**
     * @param terrariaKbResist 泰拉瑞亚击退抗性百分比，范围 -40 到 100+。
     *                         负值在 {@link #knockback} 中处理，非负值映射到 KNOCKBACK_RESISTANCE。
     */
    protected static AttributeSupplier.Builder createSlimeAttributes(
            float attackDamage, int armor, float maxHealth, float terrariaKbResist) {
        double mcKb = Math.max(0.0, terrariaKbResist / 100.0);
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, maxHealth)
                .add(Attributes.ATTACK_DAMAGE, attackDamage)
                .add(Attributes.ARMOR, (double) armor)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, mcKb);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (terrariaKbResist < 0) {
            super.knockback(strength * (1.0 - terrariaKbResist / 100.0), x, z);
        } else {
            super.knockback(strength, x, z);
        }
    }

    public static AttributeSupplier.Builder createGreenAttributes() {
        return createSlimeAttributes(3.0f, 0, 9.0f, -20);
    }

    public static AttributeSupplier.Builder createBlueAttributes() {
        return createSlimeAttributes(4.0f, 2, 16.0f, 0);
    }

    public static AttributeSupplier.Builder createRedAttributes() {
        return createSlimeAttributes(5.0f, 4, 25.0f, 10);
    }

    public static AttributeSupplier.Builder createPurpleAttributes() {
        return createSlimeAttributes(5.0f, 6, 25.0f, 10);
    }

    public static AttributeSupplier.Builder createYellowAttributes() {
        return createSlimeAttributes(6.0f, 7, 25.0f, 10);
    }

    public static AttributeSupplier.Builder createDungeonAttributes() {
        return createSlimeAttributes(15.6f, 2, 150.0f, 50);
    }

    public static AttributeSupplier.Builder createDesertAttributes() {
        return createSlimeAttributes(6.0f, 5, 21.0f, 20);
    }

    public static AttributeSupplier.Builder createJungleAttributes() {
        return createSlimeAttributes(12.0f, 6, 46.0f, 30);
    }

    public static AttributeSupplier.Builder createEvilAttributes() {
        return createSlimeAttributes(29.0f, 2, 58.0f, 40);
    }

    public static AttributeSupplier.Builder createGreenDumplingAttributes() {
        return createSlimeAttributes(5.0f, 0, 25.0f, -10);
    }

    public static AttributeSupplier.Builder createSwampAttributes() {
        return createSlimeAttributes(5.0f, 1, 25.0f, 10);
    }
}
