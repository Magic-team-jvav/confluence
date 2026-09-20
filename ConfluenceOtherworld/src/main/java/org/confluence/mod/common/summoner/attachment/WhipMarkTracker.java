package org.confluence.mod.common.summoner.attachment;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityDamageSource;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.attachment.IPortAttachmentHolder;
import org.mesdag.portlib.attachment.PortAttachmentSyncHandler;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;

import java.util.Random;

public class WhipMarkTracker implements PortAttachmentSyncHandler<WhipMarkTracker> {

    private final Random random = new Random();
    private Player owner = null;
    private LivingEntity target = null;
    private SummonMarkType type = null;
    private int duration = 0;
    private boolean used = false;
    private boolean changed = false;

    public void tick(Player player) {
        if (!player.level().isClientSide()) {
            owner = player;
            if (duration > 0) {
                if (target != null && target.isAlive()) {
                    type.tick(target, player);
                } else {
                    setDuration(0);
                }
                setDuration(duration - 1);
            }
            if (duration <= 0) {
                setMarkTarget(null);
                setUsed(false);
                setMarkType(null);
            }
            if (changed) {
                changed = false;
                player.syncData(SummonerAttachmentTypes.SUMMON_MARK_DATA.get());
            }
        }
    }

    public void tracker(LivingEntity target, SummonMarkType type, int duration) {
        if (owner != target && type != null) {
            setMarkTarget(target);
            setMarkType(type);
            setDuration(duration);
        }
    }

    public float getDamageModifier(AttachmentEntityDamageSource source, float damage) {
        if (type != null) {
            damage += type.additionalDamage();
            source.setArmorPenetration(source.getArmorPenetration() + type.additionalArmorPierce());
            if (type.criticalHitRate() > random.nextFloat()) {
                damage *= 2;
            }
        }
        return damage;
    }

    public SummonMarkType getType() {
        return type;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public @Nullable LivingEntity getMarkTarget() {
        return target;
    }

    public void setMarkType(SummonMarkType type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setMarkTarget(@Nullable LivingEntity target) {
        if (this.target != target) {
            changed = true;
        }
        this.target = target;
    }

    public boolean isSummonMarkTarget(LivingEntity living) {
        return target == living;
    }

    @Override
    public void write(PortRegistryFriendlyByteBuf buf, WhipMarkTracker data, boolean initialSync) {
        buf.writeInt(data.target == null ? -1 : data.target.getId());
    }

    @Override
    public WhipMarkTracker read(@NotNull IPortAttachmentHolder holder, @NotNull PortRegistryFriendlyByteBuf buf, @Nullable WhipMarkTracker oldData) {
        WhipMarkTracker data = oldData == null ? new WhipMarkTracker() : oldData;
        int id = buf.readInt();
        if (holder instanceof Entity entity && entity.level().getEntity(id) instanceof LivingEntity living) {
            data.target = living;
        } else {
            data.target = null;
        }
        return data;
    }
}
