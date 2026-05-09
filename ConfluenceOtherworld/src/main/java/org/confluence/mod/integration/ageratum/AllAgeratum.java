package org.confluence.mod.integration.ageratum;

import dev.anvilcraft.resource.ageratum.Ageratum;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.component.MDInlineStyleParser;
import dev.anvilcraft.resource.ageratum.client.registries.AgeratumRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ModItems;

import java.util.regex.Pattern;

class AllAgeratum {
    static final HoverEvent HOVER_EVENT = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("点击复制到剪贴板"));
    static DeferredItem<Item> INGAME_WIKI;

    static void register(IEventBus eventBus) {
        DeferredRegister<MDInlineStyleParser> inlineStyleParsers = DeferredRegister.create(AgeratumRegistries.INLINE_STYLE_PARSER_REGISTRY_KEY, Confluence.MODID);

        inlineStyleParsers.register("hover", () -> MDInlineStyleParser.create(
                100,
                Pattern.compile("<confluence_hover>"),
                "</confluence_hover>",
                (innerText, parentStyle, matcher) -> Component.literal(innerText)
                        .withStyle(parentStyle
                                .withHoverEvent(HOVER_EVENT)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, innerText))
                        )
        ));

        inlineStyleParsers.register(eventBus);

        INGAME_WIKI = ModItems.HIDDEN.register("ingame_wiki", () -> new Item(new Item.Properties().stacksTo(1)) {
            @Override
            public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
                if (player instanceof ServerPlayer serverPlayer) {
                    Ageratum.openGuide(serverPlayer, Confluence.asResource("index"));
                    level.playSound(null, player, SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F);
                    return InteractionResultHolder.success(player.getItemInHand(usedHand));
                }
                return super.use(level, player, usedHand);
            }
        });
    }

    public static void giveIngameWiki(ServerPlayer player) {
        player.addItem(INGAME_WIKI.toStack());
    }
}
