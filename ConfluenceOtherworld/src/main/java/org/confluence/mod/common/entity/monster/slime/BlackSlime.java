package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

/**
 * 黑史莱姆 —— 大小 1=Baby, 2=普通, 3=大型, 4=母体。
 * 攻击附加黑暗效果，母体死亡时分裂出 1-3 只小史莱姆。
 */
public class BlackSlime extends BaseSlime {
    private static final int BABY_SIZE = 1;
    private static final int MOTHER_SIZE = 4;
    private int slimeSize = 2;

    public BlackSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0x2D2D3A, false, 30);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data,
                                        @Nullable CompoundTag tag) {
        int size = level.getRandom().nextInt(4) + 1;
        applySizeStats(size);
        return super.finalizeSpawn(level, difficulty, spawnType, data, tag);
    }

    private void applySizeStats(int size) {
        double health;
        double damage;
        int armor;
        switch (size) {
            case BABY_SIZE:
                health = 15.0; damage = 4.0; armor = 2; break;
            case 2:
                health = 25.0; damage = 6.0; armor = 4; break;
            case 3:
                health = 40.0; damage = 8.0; armor = 5; break;
            case MOTHER_SIZE:
                health = 58.0; damage = 10.0; armor = 7; break;
            default:
                health = 25.0; damage = 6.0; armor = 4; break;
        }
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
        this.getAttribute(Attributes.ARMOR).setBaseValue((double) armor);
        this.setHealth(this.getMaxHealth());
        this.slimeSize = size;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(6.0f, 4, 25.0f, 30);
    }

    @Override
    protected void onAttackTarget(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 160, 0));
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide && slimeSize >= MOTHER_SIZE) {
            int babies = 1 + random.nextInt(3);
            for (int i = 0; i < babies; i++) {
                BlackSlime baby = (BlackSlime) this.getType().create(level());
                if (baby != null) {
                    baby.applySizeStats(BABY_SIZE);
                    baby.setPos(getX() + random.nextGaussian() * 0.5,
                            getY(),
                            getZ() + random.nextGaussian() * 0.5);
                    level().addFreshEntity(baby);
                }
            }
        }
    }
}
