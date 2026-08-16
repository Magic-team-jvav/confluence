package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/// 黏性爆炸物共享的当前版本存档格式。
///
/// <p>位置和方块状态必须成对出现；任一字段损坏都按“未黏附”处理。
/// 这样重新加载后既能继续检测原方块是否改变，也不会把缺省坐标误当作世界原点。</p>
final class StickyBlockPersistence {
    /// 黏附点只可能位于弹体附近；该上限同时阻止损坏 NBT 指向远端区块。
    private static final double MAX_ATTACHMENT_DISTANCE_SQUARED = 8.0 * 8.0;
    private static final String ROOT_KEY = "ConfluenceSticky";
    private static final String POSITION_KEY = "Position";
    private static final String BLOCK_STATE_KEY = "BlockState";

    private StickyBlockPersistence() {}

    static void save(CompoundTag entityTag, @Nullable BlockPos position, @Nullable BlockState blockState) {
        if (position == null || blockState == null) return;
        CompoundTag stickyTag = new CompoundTag();
        stickyTag.putLong(POSITION_KEY, position.asLong());
        stickyTag.put(BLOCK_STATE_KEY, NbtUtils.writeBlockState(blockState));
        entityTag.put(ROOT_KEY, stickyTag);
    }

    static Attachment load(CompoundTag entityTag, Entity entity) {
        if (!entityTag.contains(ROOT_KEY, Tag.TAG_COMPOUND)) return Attachment.EMPTY;
        CompoundTag stickyTag = entityTag.getCompound(ROOT_KEY);
        if (!stickyTag.contains(POSITION_KEY, Tag.TAG_LONG)
                || !stickyTag.contains(BLOCK_STATE_KEY, Tag.TAG_COMPOUND)) {
            return Attachment.EMPTY;
        }

        BlockPos position = BlockPos.of(stickyTag.getLong(POSITION_KEY));
        Level level = entity.level();
        if (!level.isInWorldBounds(position)
                || !level.getWorldBorder().isWithinBounds(position)
                || position.distToCenterSqr(entity.position()) > MAX_ATTACHMENT_DISTANCE_SQUARED) {
            return Attachment.EMPTY;
        }

        CompoundTag blockStateTag = stickyTag.getCompound(BLOCK_STATE_KEY);
        if (!blockStateTag.contains("Name", Tag.TAG_STRING)) return Attachment.EMPTY;
        ResourceLocation blockId = ResourceLocation.tryParse(blockStateTag.getString("Name"));
        Registry<Block> blocks = level.registryAccess().registryOrThrow(Registries.BLOCK);
        if (blockId == null || !blocks.containsKey(blockId)) return Attachment.EMPTY;

        BlockState blockState = NbtUtils.readBlockState(blocks.asLookup(), blockStateTag);
        return new Attachment(position, blockState);
    }

    record Attachment(@Nullable BlockPos position, @Nullable BlockState blockState) {
        private static final Attachment EMPTY = new Attachment(null, null);
    }
}
