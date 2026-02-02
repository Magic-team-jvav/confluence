package org.confluence.terraentity.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.npc.chat.NPCChat;
import org.confluence.terraentity.entity.npc.house.House;
import org.confluence.terraentity.entity.npc.mood.NPCMood;
import org.confluence.terraentity.entity.npc.trade.NPCTradeManager;
import org.confluence.terraentity.entity.npc.trade.TradeParams;
import org.confluence.terraentity.entity.util.KeyframeAnimationCounter;

import java.util.List;
import java.util.function.Supplier;


public final class TEEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, TerraEntity.MODID);

    public static final Supplier<EntityDataSerializer<NPCTradeManager>> NPC_TRADES_SERIALIZER = SERIALIZERS.register(NPCTradeManager.Loader.KEY, () -> EntityDataSerializer.forValueType(NPCTradeManager.STREAM_CODEC));
    public static final Supplier<EntityDataSerializer<House>> NPC_HOUSE_SERIALIZER = SERIALIZERS.register(House.KEY, () -> EntityDataSerializer.forValueType(House.STREAM_CODEC));
    public static final Supplier<EntityDataSerializer<NPCMood>> NPC_MOOD_SERIALIZER = SERIALIZERS.register(NPCMood.KEY, () -> EntityDataSerializer.forValueType(NPCMood.STREAM_CODEC));
    public static final Supplier<EntityDataSerializer<TradeParams>> NPC_TRADE_PARAMS_SERIALIZER = SERIALIZERS.register(TradeParams.KEY, () -> EntityDataSerializer.forValueType(TradeParams.STREAM_CODEC));

    public static final Supplier<EntityDataSerializer<KeyframeAnimationCounter>> KEYFRAME_ANIMATION_SERIALIZER = SERIALIZERS.register("keyframe_animation", () -> EntityDataSerializer.forValueType(KeyframeAnimationCounter.STREAM_CODEC));
    public static final Supplier<EntityDataSerializer<NPCChat>> NPC_CHAT_SERIALIZER = SERIALIZERS.register("npc_chat", () -> EntityDataSerializer.forValueType(NPCChat.STREAM_CODEC));
    public static final Supplier<EntityDataSerializer<List<Vec3>>> VEC3_LIST_SERIALIZER = SERIALIZERS.register("vec3_list", () -> EntityDataSerializer.forValueType(ByteBufCodecs.fromCodec(Vec3.CODEC.listOf())));

    public static final Supplier<EntityDataSerializer<List<Tuple<Integer, Vec3>>>> TUPLE_INT_VEC3_LIST_SERIALIZER = SERIALIZERS.register("tuple_int_vec3_list", () -> EntityDataSerializer.forValueType(ByteBufCodecs.fromCodec(
        RecordCodecBuilder.<Tuple<Integer, Vec3>>create(instance -> instance.group(
            Codec.INT.fieldOf("first").forGetter(Tuple::getA),
            Vec3.CODEC.fieldOf("second").forGetter(Tuple::getB)
        ).apply(instance, Tuple::new)).listOf()
    )));
    public static final Supplier<EntityDataSerializer<List<Tuple<Vec3, Integer>>>> TUPLET_VEC3_INT_LIST_SERIALIZER = SERIALIZERS.register("tuple_vec3_int_list", () -> EntityDataSerializer.forValueType(ByteBufCodecs.fromCodec(
            RecordCodecBuilder.<Tuple<Vec3, Integer>>create(instance -> instance.group(
                    Vec3.CODEC.fieldOf("first").forGetter(Tuple::getA),
                    Codec.INT.fieldOf("second").forGetter(Tuple::getB)
            ).apply(instance, Tuple::new)).listOf()
    )));
}
