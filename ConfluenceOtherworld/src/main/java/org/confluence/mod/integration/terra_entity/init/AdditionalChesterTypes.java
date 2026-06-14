package org.confluence.mod.integration.terra_entity.init;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerSafeContainer;
import org.confluence.mod.common.menu.PiggyBankMenu;

import java.util.function.Supplier;

public class AdditionalChesterTypes {
    public static final DeferredRegister<ChesterType> TYPES = DeferredRegister.create(TERegistries.CHESTER_TYPES, Confluence.MODID);

    public static final Supplier<ChesterType> PIGGY_BANK = TYPES.register("piggy_bank", () -> new ChesterType(Component.translatable("container.confluence.piggy_bank"),
            () -> new SimpleMenuProvider((id, inventory, player1) -> new PiggyBankMenu(id, inventory), Component.translatable("container.confluence.piggy_bank"))
    ));

    public static final Supplier<ChesterType> SAFE = TYPES.register("safe", () -> new ChesterType(Component.translatable("container.confluence.safe"),
            () -> new SimpleMenuProvider((id, inventory, player1) -> new ChestMenu(MenuType.GENERIC_9x6, id, inventory, PlayerSafeContainer.of(player1), 6), Component.translatable("container.confluence.safe"))
    ));

    public static void register(IEventBus bus) {
        TYPES.register(bus);
    }

}
