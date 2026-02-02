package org.confluence.terraentity.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.schedule.ScheduleBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.ai.brain.sensor.NPCHostilesSensor;
import org.confluence.terraentity.entity.ai.brain.sensor.NPCNearbyOthersSensor;
import org.confluence.terraentity.entity.ai.brain.sensor.NPCNearestVisibleAllianceSensor;
import org.confluence.terraentity.entity.ai.brain.sensor.NPCNurseTargetSensor;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class TEAi {
    public static final DeferredRegister<Schedule> SCHEDULES = DeferredRegister.create(BuiltInRegistries.SCHEDULE, TerraEntity.MODID);
    public static final DeferredRegister<SensorType<?>> SENSORS = DeferredRegister.create(BuiltInRegistries.SENSOR_TYPE, TerraEntity.MODID);
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULES = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, TerraEntity.MODID);
//    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(Registries.ACTIVITY, TerraEntity.MODID);


    public static class Activities{
        public static Activity STAY_HOME = registerActivity("stay_home");
        public static Activity RANGE_ATTACK = registerActivity("range_attack");

        private static Activity registerActivity(String key) {
//            return ACTIVITIES.register(key, () -> new Activity(key));
            return Registry.register(BuiltInRegistries.ACTIVITY, key, new Activity(key));
        }
        public static void register(IEventBus bus){
//            ACTIVITIES.register(bus);
        }
    }

    public static class MemoryModules {
        public static Supplier<MemoryModuleType<LivingEntity>> NEAREST_VISIBLE_ALLIANCE = MEMORY_MODULES.register("nearest_visible_alliance", () -> new MemoryModuleType<>(Optional.empty()));
        public static Supplier<MemoryModuleType<LivingEntity>> NEAREST_VISIBLE_ALLIANCE_NURSE_TARGET = MEMORY_MODULES.register("nearest_visible_alliance_nurse_target", () -> new MemoryModuleType<>(Optional.empty()));
        public static Supplier<MemoryModuleType<List<AbstractTerraNPC>>> NEARBY_NPC = MEMORY_MODULES.register("nearby_npc", () -> new MemoryModuleType<>(Optional.empty()));
        public static Supplier<MemoryModuleType<AbstractTerraNPC>> TALKING_NPC = MEMORY_MODULES.register("talking_npc", () -> new MemoryModuleType<>(Optional.empty()));

        public static void register(IEventBus bus){
            MEMORY_MODULES.register(bus);

        }
    }

    // 这里必须这样
    public static Supplier<Schedule> NPC_SCHEDULE = SCHEDULES.register("npc_schedule", ()->new ScheduleBuilder(new Schedule())
            .changeActivityAt(0, Activity.WORK)
            .changeActivityAt(12000, Activities.STAY_HOME)
            .build());




    public static class Sensors{
        public static Supplier<SensorType<NPCHostilesSensor<AbstractTerraNPC>>> NPC_HOSTILES_SENSOR = SENSORS.register("npc_hostiles_sensor", ()-> new SensorType<>(() -> new NPCHostilesSensor<>(10)));
        public static Supplier<SensorType<NPCNearestVisibleAllianceSensor<AbstractTerraNPC>>> NEAREST_VISIBLE_ALLIANCE_SENSOR = SENSORS.register("nearest_visible_alliance_sensor", ()-> new SensorType<>(() -> new NPCNearestVisibleAllianceSensor<>(10)));
        public static Supplier<SensorType<NPCNurseTargetSensor<AbstractTerraNPC>>> NEAREST_NURSE_TARGET_SENSOR = SENSORS.register("nearest_nurse_target_sensor", ()-> new SensorType<>(() -> new NPCNurseTargetSensor<>(10)));
        public static Supplier<SensorType<NPCNearbyOthersSensor>> NEARBY_NPC_SENSOR = SENSORS.register("nearby_npc_sensor", ()-> new SensorType<>(() -> new NPCNearbyOthersSensor(16)));


        public static void register(IEventBus bus){
            SENSORS.register(bus);
        }
    }



    public static void register(IEventBus bus){
        MemoryModules.register(bus);
        SCHEDULES.register(bus);
        Sensors.register(bus);

    }
}
