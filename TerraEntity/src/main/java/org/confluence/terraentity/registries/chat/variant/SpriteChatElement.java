package org.confluence.terraentity.registries.chat.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.api.npc.chat.IChatElement;
import org.confluence.terraentity.api.npc.chat.IChatRenderer;
import org.confluence.terraentity.client.gui.renderer.chat.element.SpriteRenderer;
import org.confluence.terraentity.registries.chat.ChatElementProvider;
import org.confluence.terraentity.registries.chat.ChatProviderTypes;

import java.util.List;
import java.util.Optional;

/**
 * 渲染精灵(动)图
 */
public class SpriteChatElement implements IChatElement<List<ResourceLocation>> {

    public static final MapCodec<SpriteChatElement> MAPCODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("content").forGetter(SpriteChatElement::getContent),
            Codec.FLOAT.optionalFieldOf("scale").forGetter(i->i.scale)
    ).apply(instance, (content, scale)->new SpriteChatElement(content, scale.orElse(1.0f))));

    List<ResourceLocation> content;
    Optional<Float> scale;

    public float getScale() {
        return scale.orElse(1.0f);
    }

    public SpriteChatElement(List<ResourceLocation> content, float scale) {
        this.content = content;
        this.scale = Optional.of(scale);
    }

    public SpriteChatElement(List<ResourceLocation> content) {
        this.content = content;
        this.scale = Optional.empty();
    }

    @Override
    public List<ResourceLocation> getContent() {
        return content;
    }

    @Override
    public ChatElementProvider getProvider() {
        return ChatProviderTypes.SPIRIT.get();
    }

    @Override
    public IChatRenderer<SpriteChatElement> getRenderer() {
        return SpriteRenderer.INSTANCE;
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
