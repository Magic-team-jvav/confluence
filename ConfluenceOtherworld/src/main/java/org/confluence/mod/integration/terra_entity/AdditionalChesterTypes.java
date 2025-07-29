package org.confluence.mod.integration.terra_entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerPiggyBankContainer;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.chester.ChesterType;

import java.util.function.Supplier;

public class AdditionalChesterTypes {

    public static final DeferredRegister<ChesterType> TYPES = DeferredRegister.create(TERegistries.ChesterTypesProviders.REGISTRY, Confluence.MODID);

    public static final Supplier<ChesterType> PIGGY_BANK = TYPES.register("piggy_bank", ()->new ChesterType(
            Component.translatable("container.confluence.piggy_bank"),
            ()->new SimpleMenuProvider((id, inventory, player1) ->
                    new ChestMenu(MenuType.GENERIC_9x6, id, inventory, PlayerPiggyBankContainer.of(player1), 6),
                    Component.translatable("container.confluence.piggy_bank"))
    ));

    public static final Supplier<ChesterType> SAFE = TYPES.register("safe", ()->new ChesterType(
            Component.translatable("container.confluence.safe"),
            ()->new SimpleMenuProvider((id, inventory, player1) ->
                    new ChestMenu(MenuType.GENERIC_9x6, id, inventory, player1.getData(ModAttachmentTypes.SAFE), 6),
                    Component.translatable("container.confluence.safe"))
    ));

    public static final Supplier<ChesterType> CHESTER = TYPES.register("chester", ()->new ChesterType(
            Component.translatable("container.confluence.chester"),
            ()->new SimpleMenuProvider((id, inventory, player1) ->
                    new ChestMenu(MenuType.GENERIC_9x6, id, inventory, player1.getData(ModAttachmentTypes.CHESTER), 6),
                    Component.translatable("container.confluence.chester"))
    ));

    public static void register(IEventBus bus) {
        TYPES.register(bus);
    }

}
