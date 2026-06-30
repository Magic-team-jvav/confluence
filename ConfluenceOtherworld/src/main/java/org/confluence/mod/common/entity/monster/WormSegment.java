package org.confluence.mod.common.entity.monster;

import org.jetbrains.annotations.Nullable;

public interface WormSegment {
    int getSegmentIndex();
    @Nullable WormSegment getPrev();
    @Nullable WormSegment getNext();
    void updateSegmentPosition();
}
