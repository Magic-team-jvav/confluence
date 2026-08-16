package org.confluence.mod.common.init.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.BossEntities;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class SpawnEggItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    /// 这些物品必须是真正的 Forge 刷怪蛋。普通 Item 即使注册名以
    /// “spawn_egg”结尾，也不会获得对方块使用、刷怪笼改写或实体 NBT
    /// 应用能力。颜色暂与 1.21 一样使用白色，后续只需调整两个颜色值。
    public static final PortDeferredItem<ForgeSpawnEggItem>
            RETINAZER_SPAWN_EGG = ITEMS.register(
            "retinazer_spawn_egg",
            () -> egg(BossEntities.RETINAZER));
    public static final PortDeferredItem<ForgeSpawnEggItem>
            SPAZMATISM_SPAWN_EGG = ITEMS.register(
            "spazmatism_spawn_egg",
            () -> egg(BossEntities.SPAZMATISM));
    public static final PortDeferredItem<ForgeSpawnEggItem>
            THE_DESTROYER_SPAWN_EGG = ITEMS.register(
            "the_destroyer_spawn_egg",
            () -> egg(BossEntities.THE_DESTROYER));
    public static final PortDeferredItem<ForgeSpawnEggItem>
            THE_TWINS_SPAWN_EGG = ITEMS.register(
            "the_twins_spawn_egg",
            () -> egg(BossEntities.THE_TWINS));
    public static final PortDeferredItem<ForgeSpawnEggItem>
            SKELETRON_PRIME_SPAWN_EGG = ITEMS.register(
            "skeletron_prime_spawn_egg",
            () -> egg(BossEntities.SKELETRON_PRIME));
    public static final PortDeferredItem<ForgeSpawnEggItem>
            PLANTERA_SPAWN_EGG = ITEMS.register(
            "plantera_spawn_egg",
            () -> egg(BossEntities.PLANTERA));
    public static final PortDeferredItem<ForgeSpawnEggItem>
            PRIME_ENDER_DRAGON_SPAWN_EGG = ITEMS.register(
            "prime_ender_dragon_spawn_egg",
            () -> egg(BossEntities.PRIME_ENDER_DRAGON));

    private static ForgeSpawnEggItem egg(
            RegistryObject<? extends EntityType<? extends Mob>>
                    entityType) {
        return new ForgeSpawnEggItem(
                entityType,
                0xFFFFFF,
                0xFFFFFF,
                new Item.Properties());
    }
}
