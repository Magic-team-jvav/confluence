package org.confluence.mod.common.item.tooltipcomponent;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.confluence.mod.common.component.RepeaterContents;

public record RepeaterComponent(RepeaterContents contents) implements TooltipComponent {
}
