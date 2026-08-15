package org.confluence.mod.common.entity.npc.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

import java.util.List;

/**
 * 一个数据包文件对指定 NPC 商店作出的报价贡献。
 *
 * <p>文件名只作为贡献来源的稳定标识，真正的 NPC 类型写在内容中。因此附属模组可以新增自己的文件，
 * 向已有 NPC 追加商品，而不必覆盖本体或其他附属提供的整张商店表。</p>
 */
public record NPCShopDefinition(EntityType<?> npc, List<NPCTradeOffer> offers) {
    public static final Codec<NPCShopDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec()
                    .fieldOf("npc")
                    .forGetter(NPCShopDefinition::npc),
            NPCTradeOffer.CODEC.listOf()
                    .fieldOf("offers")
                    .forGetter(NPCShopDefinition::offers)
    ).apply(instance, NPCShopDefinition::new));

    public NPCShopDefinition {
        offers = List.copyOf(offers);
    }
}
