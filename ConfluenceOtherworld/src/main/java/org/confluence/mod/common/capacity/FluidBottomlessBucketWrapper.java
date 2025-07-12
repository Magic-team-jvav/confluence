package org.confluence.mod.common.capacity;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.confluence.mod.common.item.common.BottomlessBucketItem;

public class FluidBottomlessBucketWrapper implements IFluidHandlerItem {
    protected final ItemStack container;

    public FluidBottomlessBucketWrapper(ItemStack container) {
        this.container = container;
    }

    @Override
    public ItemStack getContainer() {
        return container;
    }

    public FluidStack getFluid() {
        if (container.getItem() instanceof BottomlessBucketItem bucketItem) {
            return new FluidStack(bucketItem.content, FluidType.BUCKET_VOLUME);
        } else {
            return FluidStack.EMPTY;
        }
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return FluidType.BUCKET_VOLUME;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack fluidStack = getFluid();
        if (!fluidStack.isEmpty() && FluidStack.isSameFluidSameComponents(fluidStack, resource)) {
            return fluidStack;
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack fluidStack = getFluid();
        if (!fluidStack.isEmpty()) {
            return fluidStack;
        }
        return FluidStack.EMPTY;
    }
}
