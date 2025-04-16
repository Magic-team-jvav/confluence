package org.confluence.mod.integration.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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

import java.util.Arrays;

public class TombstoneInfoProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    public static final TombstoneInfoProvider INSTANCE = new TombstoneInfoProvider();
    public static final ResourceLocation UID = Confluence.asResource("jade_tombstone_info");
    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        CompoundTag compoundTag = blockAccessor.getServerData();
        if (compoundTag.get("tombInfo") instanceof ListTag listTag) {
            listTag.forEach(tag -> {
                if (tag instanceof CompoundTag tag0) {
                    String info = tag0.getString("info");
                    if(!info.isEmpty()){
                        iTooltip.add(Component.nullToEmpty(info));
                    }
                }
            });
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof TombstoneBlock.Entity entity) {
            SignText signText = entity.getFrontText();
            ListTag listTag = new ListTag();
            Arrays.stream(signText.getMessages(false))
                    .forEach(component -> {
                        CompoundTag tag = new CompoundTag();
                        tag.putString("info",component.getString());
                        listTag.add(tag);
                    });
            compoundTag.put("tombInfo",listTag);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
