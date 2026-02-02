package org.confluence.terraentity.registries.chester;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.registries.TERegistries;

import java.util.function.Supplier;

/**
 * 注册切斯特可以远程打开的菜单
 */
public class ChesterTypes {
    public static DeferredRegister<ChesterType> TYPES = DeferredRegister.create(TERegistries.Keys.CHESTER_TYPE, TerraEntity.MODID);

    public static final Supplier<ChesterType> ENDER_CHEST = TYPES.register("ender_chest", ()->new ChesterType(
            "container.enderchest",
            ()->new SimpleMenuProvider((id, inv, player)->
                    ChestMenu.threeRows(id, inv, player.getEnderChestInventory()),
                    Component.literal("Chester Ender Chest"))));
    public static final Supplier<ChesterType> CHESTER = TYPES.register("chester", ()->new ChesterType(
            Component.translatable("container.terra_entity.chester"),
            ()->new SimpleMenuProvider((id, inventory, player1) ->
                    new ChestMenu(MenuType.GENERIC_9x6, id, inventory, player1.getData(TEAttachments.CHESTER), 6),
                    Component.translatable("container.terra_entity.chester"))));

}
