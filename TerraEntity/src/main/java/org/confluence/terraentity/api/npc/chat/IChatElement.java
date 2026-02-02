package org.confluence.terraentity.api.npc.chat;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.Font;
import net.minecraft.util.RandomSource;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.chat.ChatElementProvider;

import java.util.List;

/**
 * NPC对话框元素绘制接口
 */
public interface IChatElement<T> {

    /**
     * 渲染器中获得对话内容
     */
    T getContent();

    ChatElementProvider getProvider();

    IChatRenderer<? extends IChatElement<T>> getRenderer();

    int wrapWidth(int input, Font font);

    int warpHeight(int input, Font font);

    /**
     * 由于某些复合结点，需要生成一个新的叶节点对象
     */
    default List<IChatElement> generate(RandomSource random){
        return List.of(this);
    }


    Codec<IChatElement> TYPED_CODEC = TERegistries.CHAT_ELEMENT_PROVIDERS
            .byNameCodec()
            .dispatch(IChatElement::getProvider, ChatElementProvider::codec);

}
