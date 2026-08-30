package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
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
import org.confluence.lib.util.LibUtils;
import org.jetbrains.annotations.Nullable;

/// 黑史莱姆 —— 大小 1=Baby, 2=普通, 3=大型, 4=母体。
/// 攻击附加黑暗效果，母体死亡时分裂出 2-4 只普通黑史莱姆。
public class BlackSlime extends BaseSlime {
    private static final int BABY_SIZE = 1;
    private static final int MOTHER_SIZE = 4;

    public BlackSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0x373535, false);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data,
                                        @Nullable CompoundTag tag) {
        float specialMultiplier = difficulty.getSpecialMultiplier();
        float motherChance = specialMultiplier <= 0.5F ? 0.15F
                : specialMultiplier <= 1.0F ? 0.60F : 0.85F;
        int size = level.getRandom().nextFloat() < motherChance ? MOTHER_SIZE : 2;
        applySizeStats(size);
        return super.finalizeSpawn(level, difficulty, spawnType, data, tag);
    }

    private void applySizeStats(int size) {
        double health;
        double damage;
        int armor;
        if (size == MOTHER_SIZE) {
            health = 58.0;
            damage = 10.0;
            armor = 7;
        } else {
            health = 25.0;
            damage = 6.0;
            armor = 4;
        }
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
        this.getAttribute(Attributes.ARMOR).setBaseValue((double) armor);
        this.setHealth(this.getMaxHealth());
        setSlimeSize(size);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(6.0f, 4, 25.0f);
    }

    /// 是否为击杀母体后生成的小史莱姆。
    public boolean isBabySlime() {
        return getSlimeSize() == BABY_SIZE;
    }

    /// 是否为会在死亡后分裂的史莱姆之母。
    public boolean isMotherSlime() {
        return getSlimeSize() == MOTHER_SIZE;
    }

    @Override
    public Component getName() {
        if (!hasCustomName()) {
            if (isBabySlime()) {
                return Component.translatable("entity.confluence.baby_slime");
            }
            if (isMotherSlime()) {
                return Component.translatable("entity.confluence.mother_slime");
            }
        }
        return super.getName();
    }

    @Override
    protected void onAttackTarget(LivingEntity target) {
        if (LibUtils.isMaster(level(), blockPosition()) || (LibUtils.isAtLeastExpert(level(), blockPosition()) && random.nextBoolean())) {
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 300, 0), this);
        }
    }

    /// 母体只在死亡实体真正移除时分裂，数量、位置和子代尺寸沿用原版史莱姆。
    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide && !isRemoved() && isDeadOrDying() && isMotherSlime()) {
            int babies = 2 + random.nextInt(3);
            float horizontalOffset = getBbWidth() / 4.0F;
            float verticalOffset = getBbHeight() / 8.0F;
            for (int i = 0; i < babies; i++) {
                BlackSlime child = (BlackSlime) getType().create(level());
                if (child == null) continue;
                if (isPersistenceRequired()) child.setPersistenceRequired();
                if (hasCustomName()) child.setCustomName(getCustomName());
                child.setNoAi(isNoAi());
                child.setInvulnerable(isInvulnerable());
                child.setSlimeSize(2);
                float xOffset = (i % 2 - 0.5F) * horizontalOffset;
                float zOffset = (i / 2 - 0.5F) * horizontalOffset;
                child.moveTo(getX() + xOffset, getY() + verticalOffset, getZ() + zOffset, random.nextFloat() * 360.0F, 0.0F);
                if (!level().addFreshEntity(child)) child.discard();
            }
        }
        super.remove(reason);
    }
}
