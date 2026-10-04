package org.confluence.mod.common.summoner.minion;

import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;

public interface ICarryMinion<T extends Minion> {

    boolean isOnCarry();

    void setOnCarry(boolean onCarry);

    PathNode getCarryPathNode(float partialTick);

    default void onCarry() {
        getMinion().setCurrentPathNode(getCarryPathNode(1));
    }

    default boolean canCarry() {
        return true;
    }

    @SuppressWarnings("unchecked")
    default T getMinion() {
        return (T) this;
    }

    default void tryCarry() {
        if (canCarry()) {
            Minion minion = getMinion();
            if (minion.getTickCount() > 20 && !minion.getOwner().getData(SummonerAttachmentTypes.ENTITY_DATA).isHasCarryMinion()) {
                if (minion.getOwner().position().distanceTo(minion.getPos()) < 2 && !minion.isRemove()) {
                    setOnCarry(true);
                }
            }
        }
    }
}
