package org.confluence.terra_curio.common.item.curio;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.fml.ModList;

import java.util.Arrays;
import java.util.List;

public class RequiresModLoadedCurioItem extends BaseCurioItem {
    private final String[] required;
    private final boolean noneLoaded;

    public RequiresModLoadedCurioItem(Builder builder, String... required) {
        super(builder);
        this.required = required;
        this.noneLoaded = Arrays.stream(required).noneMatch(ModList.get()::isLoaded);
    }

    public RequiresModLoadedCurioItem(Properties properties, String... required) {
        super(properties);
        this.required = required;
        this.noneLoaded = Arrays.stream(required).noneMatch(ModList.get()::isLoaded);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (noneLoaded) tooltipComponents.add(Component.translatable("tooltip.terra_curio.requires_mod_loaded", Arrays.toString(required)));
    }
}
