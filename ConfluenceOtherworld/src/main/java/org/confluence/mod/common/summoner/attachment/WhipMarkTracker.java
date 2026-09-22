package org.confluence.mod.common.summoner.attachment;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityDamageSource;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.summonMark.SummonMarkInstance;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.attachment.IPortAttachmentHolder;
import org.mesdag.portlib.attachment.PortAttachmentSyncHandler;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;

import java.util.*;

public class WhipMarkTracker {

    private final Player owner;
    private final Map<SummonMarkType, SummonMarkInstance> marks = new HashMap<>();
    private LivingEntity target = null;

    public WhipMarkTracker(IPortAttachmentHolder owner) {
        if (owner instanceof Player player) {
            this.owner = player;
        } else {
            throw new IllegalArgumentException(owner + " is not a valid WhipMarkTracker");
        }
    }

    public void tick() {
        if (!owner.level().isClientSide()) {
            if (target != null && target.isAlive()) {
                marks.values().removeIf(instance -> {
                    instance.getType().tick(this, instance, target, owner);
                    instance.setDuration(instance.getDuration() - 1);
                    return instance.getDuration() <= 0;
                });
            } else {
                marks.clear();
            }
            if (!marks.isEmpty()) {
                owner.syncData(SummonerAttachmentTypes.SUMMON_MARK_DATA.get());
            } else {
                target = null;
            }
        } else {
            if (target != null && !target.isAlive()) {
                target = null;
            }
        }
    }

    public void tracker(LivingEntity target, SummonMarkInstance instance) {
        if (owner != target) {
            this.target = target;
            marks.put(instance.getType(), instance);
        }
    }

    public @Nullable SummonMarkInstance getInstance(SummonMarkType type) {
        return marks.get(type);
    }

    public float getDamageModifier(AttachmentEntityDamageSource source, float damage) {
        if (!marks.isEmpty()) {
            float additionalDamage = 0;
            float additionalArmorPierce = 0;
            float criticalHitRate = 0;
            for (SummonMarkType summonMarkType : marks.keySet()) {
                if (summonMarkType.additionalDamage() > additionalDamage) {
                    additionalDamage = summonMarkType.additionalDamage();
                }
                if (summonMarkType.additionalArmorPierce() > additionalArmorPierce) {
                    additionalArmorPierce = summonMarkType.additionalArmorPierce();
                }
                if (summonMarkType.criticalHitRate() > criticalHitRate) {
                    criticalHitRate = summonMarkType.criticalHitRate();
                }
            }
            if (source.getAttachmentEntity().getPrefix() instanceof ModPrefix.Summon summon) {
                additionalDamage += summon.tagDamage();
            }
            damage += additionalDamage;
            source.setArmorPenetration(source.getArmorPenetration() + additionalArmorPierce);
            if (criticalHitRate > owner.getRandom().nextFloat()) {
                damage *= 2;
            }
        }
        return damage;
    }

    public Player getOwner() {
        return owner;
    }

    public List<SummonMarkInstance> getSummonMarkInstances() {
        return marks.values().stream().toList();
    }

    public @Nullable LivingEntity getMarkTarget() {
        return target;
    }

    public boolean isSummonMarkTarget(LivingEntity living) {
        return target != null && target == living;
    }

    public static final class SyncHandler implements PortAttachmentSyncHandler<WhipMarkTracker> {

        @Override
        public void write(PortRegistryFriendlyByteBuf buf, WhipMarkTracker data, boolean initialSync) {
            buf.writeInt(data.target == null ? -1 : data.target.getId());
        }

        @Override
        public WhipMarkTracker read(@NotNull IPortAttachmentHolder holder, @NotNull PortRegistryFriendlyByteBuf buf, @Nullable WhipMarkTracker oldData) {
            WhipMarkTracker data = oldData == null ? new WhipMarkTracker(holder) : oldData;
            int id = buf.readInt();
            if (holder instanceof Entity entity && entity.level().getEntity(id) instanceof LivingEntity living) {
                data.target = living;
            } else {
                data.target = null;
            }
            return data;
        }
    }
}
