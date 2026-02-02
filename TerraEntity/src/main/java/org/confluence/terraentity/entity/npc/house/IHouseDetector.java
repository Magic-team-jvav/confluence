package org.confluence.terraentity.entity.npc.house;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.api.event.HouseDetectEvent;
import org.confluence.terraentity.entity.npc.brain.behavior.NPCHouseBehaviors;
import org.confluence.terraentity.item.HouseDetectItem;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <p>自定义房屋检测时的检测器
 * <p>替换检测器：{@link HouseDetectEvent}
 * <p>usage:
 * <p>{@link HouseDetectItem}
 * <p>{@link NPCHouseBehaviors#findHouse(MemoryModuleType)}
 */
public interface IHouseDetector {
    BlockPos min();

    BlockPos max();

    BlockPos center();

    List<BlockPos> list();

    boolean isError();

    String message();

    static IHouseDetector detect(BlockPos pos, Level level) {
        return AdapterUtils.postGameEvent(new HouseDetectEvent(pos, level)).getDetector();
    }

    default House getHouse(@NotNull UUID uuid) {
        return new House(Optional.of(uuid), min(), max(), center());
    }
}
