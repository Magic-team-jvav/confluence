package org.confluence.terraentity.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.entity.rideable.AbstractRideableEntity;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.integration.ModChecker;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RideableItem<T extends AbstractRideableEntity> extends Item {
    Supplier<EntityType<T>> entityType;
    Predicate<Player> canUse;
    public RideableItem(Properties properties,  Supplier<EntityType<T>> entityType) {
        this(properties.stacksTo(1), entityType, player -> true);
    }

    public RideableItem(Properties properties,  Supplier<EntityType<T>> entityType, Predicate<Player> canUse) {
        super(properties);
        this.entityType = entityType;
        this.canUse = canUse;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(summonRideableEntity(player)){
            player.swing(InteractionHand.MAIN_HAND, true);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(ModChecker.curios.isLoaded()){
            tooltipComponents.add(Component.translatable("tooltip.terra_entity.rideable_item.desc"));
        }
    }

    public boolean summonRideableEntity(Player player) {
        Level level = player.level();
        if(!level.isClientSide){
            if(player.getVehicle() == null){
                if(canUse.test(player)) {
                    AbstractRideableEntity rideable = entityType.get().create(level);
                    if (rideable != null) {
                        rideable.setOwnerUUID(player.getUUID());
                        rideable.setXRot(player.getXRot());
                        rideable.setYRot(player.getYRot());
                        rideable.setPos(player.getX(), player.getY(), player.getZ());
                        rideable.doPlayerRide(player);
                        level.addFreshEntity(rideable);
                        rideable.onInit(player);
                        level.playSound(null, player.blockPosition(), TESounds.USE_MOUNTS.get(), SoundSource.PLAYERS, 0.4F, 1.0F);
                        return true;
                    }
                }
            }
        }
        return false;
    }

}