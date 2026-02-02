package org.confluence.terraentity.registries.chat_condition.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.confluence.terraentity.api.npc.chat.IChatCondition;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.chat.ChatHolder;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProvider;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProviderTypes;

import java.util.Iterator;
import java.util.Map;

public record MemoryStateCondition(Map<MemoryModuleType<?> ,MemoryStatus> memoryStatus) implements IChatCondition {

//    public static Codec<MemoryStateCondition> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
//            BuiltInRegistries.MEMORY_MODULE_TYPE.byNameCodec().fieldOf("memory").forGetter(MemoryStateCondition::memory),
//            Codec.STRING.fieldOf("status").xmap(str->MemoryStatus.valueOf(str.toUpperCase()), state->state.name().toLowerCase()).forGetter(MemoryStateCondition::status)
//    ).apply(instance, MemoryStateCondition::new));

    public static MapCodec<MemoryStateCondition> CODEC = Codec.unboundedMap(
            BuiltInRegistries.MEMORY_MODULE_TYPE.byNameCodec(),
            Codec.STRING.xmap(str -> MemoryStatus.valueOf(str.toUpperCase()), state -> state.name().toLowerCase())
    ).xmap(MemoryStateCondition::new, MemoryStateCondition::memoryStatus).fieldOf("memory_status");

    @Override
    public ChatConditionProvider getProvider() {
        return ChatConditionProviderTypes.MEMORY_STATES.get();
    }

    @Override
    public boolean canChat(AbstractTerraNPC npc, ChatHolder chatHolder) {
        Iterator<Map.Entry<MemoryModuleType<?>, MemoryStatus>> var2 = this.memoryStatus.entrySet().iterator();
        MemoryModuleType<?> memorymoduletype;
        MemoryStatus memorystatus;
        do {
            if (!var2.hasNext()) {
                return true;
            }
            Map.Entry<MemoryModuleType<?>, MemoryStatus> entry = var2.next();
            memorymoduletype = entry.getKey();
            memorystatus = entry.getValue();
        } while(npc.getBrain().checkMemory(memorymoduletype, memorystatus));

        return false;
    }

}
