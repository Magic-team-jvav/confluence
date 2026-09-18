package org.confluence.mod.common.summoner;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.summoner.attachment.AttachmentEntityData;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.minion.Minion;
import org.confluence.mod.common.summoner.minion.MinionSlotType;

import java.util.Collection;
import java.util.List;

public final class SummonerHelper {

    private final Player player;
    private final AttachmentEntityData data;

    private SummonerHelper(Player player) {
        this.player = player;
        this.data = player.getData(SummonerAttachmentTypes.ENTITY_DATA);
    }

    public static SummonerHelper get(Player player) {
        return new SummonerHelper(player);
    }

    public AttachmentEntityData getEntityData() {
        return data;
    }

    public void add(AttachmentEntity entity) {
        data.add(entity);
    }

    public void remove(AttachmentEntityType<?> entityType) {
        data.getGroups()
                .getOrDefault(entityType, List.of())
                .stream()
                .filter(entity -> entity instanceof Minion)
                .forEach(AttachmentEntity::setRemove);
    }

    public boolean canSummon(MinionSlotType type, int slotCost) {
        return getMaxCount(type) - getUsedSlots(type) >= slotCost;
    }

    public int getMaxCount(MinionSlotType type) {
        AttributeInstance instance = switch (type) {
            case Minion -> player.getAttribute(ConfluenceMagicLib.MINION_CAPACITY);
            case Sentry -> player.getAttribute(ConfluenceMagicLib.SENTRY_CAPACITY);
            case None -> null;
        };
        return instance == null ? 0 : (int) instance.getValue();
    }

    public int getUsedSlots(MinionSlotType type) {
        return data.getGroups()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .filter(Minion.class::isInstance)
                .map(Minion.class::cast)
                .filter(minion -> minion.getSlotType() == type && !minion.isRemove())
                .mapToInt(Minion::getSlotCost)
                .sum();
    }
}
