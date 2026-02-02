package org.confluence.terraentity.registries.chat.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.api.npc.chat.IChatElement;
import org.confluence.terraentity.api.npc.chat.IChatRenderer;
import org.confluence.terraentity.client.gui.renderer.chat.element.ChatItemRenderer;
import org.confluence.terraentity.registries.chat.ChatElementProvider;
import org.confluence.terraentity.registries.chat.ChatProviderTypes;

import java.util.Optional;

public class ItemChatElement implements IChatElement<ItemStack> {

    public static final MapCodec<ItemChatElement> MAPCODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("content").forGetter(ItemChatElement::getContent),
            Codec.FLOAT.optionalFieldOf("scale").forGetter(i-> i.scale)
    ).apply(instance, (content, scale)-> new ItemChatElement(content, scale.orElse(1.0f))));

    ItemStack content;
    Optional<Float> scale;

    public float getScale() {
        return scale.orElse(1.0f);
    }
    public ItemChatElement(ItemStack content) {
        this.content = content;
        this.scale = Optional.empty();
    }
    public ItemChatElement(ItemStack content, float scale) {
        this.content = content;
        this.scale = Optional.of(scale);
    }

    @Override
    public ItemStack getContent() {
        return content;
    }

    @Override
    public ChatElementProvider getProvider() {
        return ChatProviderTypes.ITEM.get();
    }

    @Override
    public IChatRenderer<ItemChatElement> getRenderer() {
        return ChatItemRenderer.INSTANCE;
    }

    @Override
    public int wrapWidth(int input, Font font) {
        return input + 12;
    }

    @Override
    public int warpHeight(int input, Font font) {
        return input;
    }
}
