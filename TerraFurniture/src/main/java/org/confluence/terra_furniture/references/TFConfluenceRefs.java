package org.confluence.terra_furniture.references;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.util.TriConsumer;
import org.confluence.terra_furniture.common.entity.RideableEntityNull;

public class TFConfluenceRefs {
    public static TriConsumer<RideableEntityNull, ServerLevel, BlockPos> poop_task =
            (entityNull, serverLevel, pos) -> {};
}
