package org.confluence.mod.common.summoner.attachmentEntity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * 附件实体类型注册记录。
 * <p>
 * 用于注册不同类型的附件实体，包含标识与工厂方法以创建实体实例。
 * </p>
 *
 * @param <T> 实体类型
 */
public record AttachmentEntityType<T extends AttachmentEntity>(@NotNull ResourceLocation identifier, @NotNull Supplier<T> factory) {

    public Component getDisplayName() {
        String key = "summon." + identifier.getNamespace() + "." + identifier.getPath();
        return Component.translatable(key).withStyle(ChatFormatting.BLUE);
    }
}
