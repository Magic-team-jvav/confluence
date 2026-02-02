package org.confluence.terraentity.api.npc.trade;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.terraentity.TerraEntity;

import java.util.Iterator;
import java.util.List;

import static org.confluence.terraentity.client.gui.container.TETradeScreen.MENU_LOCATION;

public interface ITradeLootTable extends ITrade{

    ResourceLocation DefaultSprite = TerraEntity.space("unknown");
    String DefaultTranslationKey = "LootTable";

    ResourceKey<LootTable> lootTable();

    ResourceLocation sprite();

    String translationKey();

    @Override
    default void onTrade(ServerPlayer player, ITradeHolder npc, int index) {
        List<ItemStack> loot = player.level().getServer().reloadableRegistries()
                .getLootTable(lootTable())
                .getRandomItems(new LootParams.Builder((ServerLevel) player.level())
                        .withParameter(LootContextParams.THIS_ENTITY, player)
                        .withParameter(LootContextParams.ORIGIN, player.position())
                        .create(LootContextParamSets.GIFT));
        for(ItemStack stack : loot){
            player.getInventory().placeItemBackInInventory(stack);
        }
    }

    @OnlyIn(Dist.CLIENT)
    default void renderResult(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, int index){

        ResourceLocation sprite = sprite();
        if(sprite == null){
            sprite = DefaultSprite;
        }
        guiGraphics.blitSprite(sprite,x,y,16,16);

    }

    @OnlyIn(Dist.CLIENT)
    default void renderResultHover(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY){
        ClientTooltipComponent clienttooltipcomponent2;


        List<ClientTooltipComponent> components = List.of(
                ClientTooltipComponent.create(Component.translatable(translationKey()==null? DefaultTranslationKey: translationKey()).withStyle(Style.EMPTY).getVisualOrderText()),
                new ClientTextTooltip(FormattedCharSequence.forward(this.lootTable().location().toString(), Style.EMPTY.withItalic(true).withColor(0x3253FF)))
        );

        int i = 0;
        int j = components.size() == 1 ? -2 : 0;

        ClientTooltipComponent clienttooltipcomponent;
        for(Iterator var9 = components.iterator(); var9.hasNext(); j += clienttooltipcomponent.getHeight()) {
            clienttooltipcomponent = (ClientTooltipComponent)var9.next();
            int k = clienttooltipcomponent.getWidth(font);
            if (k > i) {
                i = k;
            }
        }
        x = mouseX + 12;

        TooltipRenderUtil.renderTooltipBackground(guiGraphics, x, mouseY, i, j, 400,
                0xBA0053C8,0x00000000,
                0x530032C1,0x00000000
        );

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);
        int k1 = mouseY;

        int k2;

        for(k2 = 0; k2 < components.size(); ++k2) {
            clienttooltipcomponent2 = components.get(k2);
            clienttooltipcomponent2.renderText(font,x, k1, guiGraphics.pose().last().pose(), guiGraphics.bufferSource());
            k1 += clienttooltipcomponent2.getHeight() + (k2 == 0 ? 2 : 0);
        }


        for(k2 = 0; k2 < components.size(); ++k2) {
            clienttooltipcomponent2 = components.get(k2);
            clienttooltipcomponent2.renderImage(font, x, k1, guiGraphics);
            k1 += clienttooltipcomponent2.getHeight() + (k2 == 0 ? 2 : 0);
        }

        guiGraphics.pose().popPose();

    }


    @OnlyIn(Dist.CLIENT)
    default void renderResultSlot(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, boolean canBuy, Slot slot){
        if(canBuy){
            ResourceLocation sprite = sprite();
            if(sprite == null){
                sprite = DefaultSprite;
            }
            guiGraphics.blitSprite(sprite,x+35,y+2,16,16);
            guiGraphics.blit(MENU_LOCATION,x,y,276,0,35,17,512,256);
        }else{

            guiGraphics.blit(MENU_LOCATION,x,y,276,17,35,17,512,256);
        }
        slot.set(ItemStack.EMPTY);
    }

    // todo 加载jei配方的时候无法获取lootTable
    default List<ItemStack> normalizeResult(){

        return List.of();
    }

}
