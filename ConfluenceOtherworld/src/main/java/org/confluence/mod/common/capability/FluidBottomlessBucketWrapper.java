package org.confluence.mod.common.capability;

import PortLib.extensions.net.minecraftforge.fluids.FluidStack.PortFluidStackExtension;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.confluence.mod.common.item.common.BottomlessBucketItem;
import org.jetbrains.annotations.Nullable;

public class FluidBottomlessBucketWrapper implements IFluidHandlerItem, ICapabilityProvider {
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

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
            return new FluidStack(bucketItem.getFluid(), FluidType.BUCKET_VOLUME);
        }
        return FluidStack.EMPTY;
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
        if (!fluidStack.isEmpty() && PortFluidStackExtension.isSameFluidSameComponents(fluidStack, resource)) {
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

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(cap, holder);
    }
}
