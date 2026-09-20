package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.PartHitTarget;

/// 原生多部件茎部，由头部创建、定位与销毁，不独立生成或同步。
public class PlantStemPart extends PartEntity<Snatcher> implements PartHitTarget {
    private final int index;

    public PlantStemPart(Snatcher parent, int index) {
        super(parent);
        this.index = index;
        noPhysics = true;
    }

    /// 等分茎长，相邻受击盒连续衔接，厚度为头部宽度的一半。
    public void updatePosition() {
        Snatcher plant = getParent();
        Vec3 origin = plant.position().add(plant.stemOffset(1.0F));
        Vec3 step = plant.getAnchor().subtract(origin).scale(1.0 / plant.getParts().length);
        Vec3 start = origin.add(step.scale(index));
        Vec3 end = origin.add(step.scale(index + 1));
        xo = xOld = getX();
        yo = yOld = getY();
        zo = zOld = getZ();
        setPos(start.add(end).scale(0.5));
        setBoundingBox(new AABB(start, end).inflate(plant.getBbWidth() * 0.25));
    }

    /// 名称随所属物种区分，继续使用各物种已有的体节翻译。
    @Override
    protected Component getTypeName() {
        return Component.translatable(getParent().getType().getDescriptionId() + "_segment");
    }

    /// 普通、专家、大师分别减伤 30%、50%、80%，头部继续处理防御和受击反馈。
    @Override
    public boolean hurt(DamageSource source, float amount) {
        Snatcher plant = getParent();
        if (level().isClientSide || !isAttackable() || isInvulnerableTo(source)) return false;
        float multiplier = LibUtils.isMaster(level(), plant.blockPosition()) ? 0.2F : LibUtils.isAtLeastExpert(level(), plant.blockPosition()) ? 0.5F : 0.7F;
        return plant.hurt(source, amount * multiplier);
    }

    @Override
    public boolean isPickable() {return isAttackable();}

    @Override
    public boolean isAttackable() {
        return !isRemoved() && getParent().isAlive() && getParent().isAnchored();
    }

    @Override
    public boolean canBeHitByProjectile() {return isAttackable();}

    @Override
    public boolean is(Entity entity) {return this == entity || getParent() == entity;}

    @Override
    public Entity damageRecipient() {return this;}

    @Override
    public Entity encounterOwner() {return getParent();}

    @Override
    public Entity dedupeIdentity() {return getParent();}

    @Override
    public boolean acceptsDirectHit() {return isAttackable();}

    @Override
    public boolean shouldBeSaved() {return false;}

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
