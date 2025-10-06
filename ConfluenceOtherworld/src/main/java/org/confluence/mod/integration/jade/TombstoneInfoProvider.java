package org.confluence.mod.integration.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SignText;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.TombstoneBlock;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class TombstoneInfoProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    public static final TombstoneInfoProvider INSTANCE = new TombstoneInfoProvider();
    public static final ResourceLocation UID = Confluence.asResource("jade_tombstone_info");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        CompoundTag compoundTag = blockAccessor.getServerData();
        if (compoundTag.get("tombInfo") instanceof ListTag listTag) {
            for (Tag tag : listTag) {
                if (tag instanceof StringTag stringTag) {
                    String info = stringTag.getAsString();
                    if (!info.isEmpty()) {
                        iTooltip.add(Component.literal(info));
                    }
                }
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof TombstoneBlock.BEntity entity) {
            SignText signText = entity.getFrontText();
            ListTag listTag = new ListTag();
            for (Component message : signText.getMessages(false)) {
                listTag.add(StringTag.valueOf(message.getString()));
            }
            compoundTag.put("tombInfo", listTag);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
