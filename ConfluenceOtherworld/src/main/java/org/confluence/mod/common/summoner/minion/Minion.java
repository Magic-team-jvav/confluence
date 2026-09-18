package org.confluence.mod.common.summoner.minion;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachment.TargetCache;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityDamageSource;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.lib.common.LibDamageTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public abstract class Minion extends AttachmentEntity {

    protected LivingEntity target;
    protected boolean targetChange = false;
    protected MinionSlotType slotType = MinionSlotType.None;
    protected int slotCost = 1;
    protected int order = 0;
    protected int sameSize = 1;

    public Minion(Holder<AttachmentEntityType<?>> type) {
        super(type);
    }

    public abstract int getSearchDistance();

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> target != null ? target.getId() : -1, (level, id) -> target = level.getEntity(id) instanceof LivingEntity living ? living : null);
        fields.field(LyraStreamCodecs.MINION_SLOT_TYPE, this::getSlotType, this::setSlotType);
        fields.field(LyraStreamCodecs.INT, this::getSlotCost, this::setSlotCost);
        fields.field(LyraStreamCodecs.INT, this::getOrder, this::setOrder);
        fields.field(LyraStreamCodecs.INT, this::getSameSize, this::setSameSize);
    }

    @Override
    public @NotNull DamageSource getDamageSource() {
        return new AttachmentEntityDamageSource(LibDamageTypes.of(getLevel(), LibDamageTypes.SUMMONER, null, owner).typeHolder(), null, owner, getPos(), this);
    }

    @Override
    public boolean isAlive() {
        return owner != null && owner.isAlive();
    }

    @Override
    public void onLevelChange() {
        init(getCurrentPathNode().modifyPos(getOwner().getBoundingBox().getCenter()));
        if (slotType == MinionSlotType.Sentry) {
            setRemove();
        }
    }

    public long getSameHash() {
        return Objects.hash(this.getType()) * 43L;
    }

    public int getSlotCost() {
        return slotCost;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setSameSize(int sameSize) {
        this.sameSize = sameSize;
    }

    public void setSlotCost(int slotCost) {
        this.slotCost = slotCost;
    }

    public int getSameSize() {
        return sameSize;
    }

    @Override
    public void tick() {
        super.tick();
        setTargetChange(false);
        setTarget(searchTarget());
        goalSelector.tick();
    }

    /**
     * 在所有者周围搜索有效目标
     */
    public LivingEntity searchTarget() {
        int distance = this.getSearchDistance();
        if (distance > 0 && owner != null) {
            TargetCache targetCache = getTargetCache();
            List<LivingEntity> targets = targetCache.getEntitiesInRadius(owner.getBoundingBox().getCenter(), distance, living -> targetCache.isVisibility(owner, living) && targetCache.isTarget(living));
            if (!targets.isEmpty()) {
                return targetCache.getNewTarget(this, targets, 0, true);
            }
        }
        return null;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public void setTarget(LivingEntity target) {
        if (this.target != target) {
            setTargetChange(true);
        }
        this.target = target;
    }

    public boolean isTargetChange() {
        return targetChange;
    }

    public void setTargetChange(boolean targetChange) {
        this.targetChange = targetChange;
    }

    public AttachmentEntityGoalSelector getGoalSelector() {
        return goalSelector;
    }

    public MinionSlotType getSlotType() {
        return slotType;
    }

    public void setSlotType(MinionSlotType slotType) {
        this.slotType = slotType;
    }

    public SoundSource getSoundSource() {
        return owner != null ? owner.getSoundSource() : SoundSource.MASTER;
    }
}
