package org.confluence.mod.common.init;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.confluence.mod.common.entity.npc.NPCTrades;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.MODID;


public final class ModEntityDataSerializer {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, MODID);

    public static Supplier<EntityDataSerializer<NPCTrades>> DAVE_TRADES_SERIALIZER = ENTITY_DATA_SERIALIZERS.register(NPCTrades.KEY,()->EntityDataSerializer.forValueType(NPCTrades.STREAM_CODEC));


}
