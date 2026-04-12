package org.confluence.mod.integration.ageratum;

import dev.anvilcraft.resource.ageratum.client.feat.markdown.component.MDInlineStyleParser;
import dev.anvilcraft.resource.ageratum.client.registries.AgeratumRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;

import java.util.regex.Pattern;

class AllAgeratum {
    static final HoverEvent HOVER_EVENT = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("点击复制到剪贴板"));

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
    }
}
