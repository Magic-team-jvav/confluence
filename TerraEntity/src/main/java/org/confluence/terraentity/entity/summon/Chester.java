package org.confluence.terraentity.entity.summon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.confluence.terraentity.api.entity.IPetMob;
import org.confluence.terraentity.attachment.SummonerAttachment;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.item.SummonItem;
import org.confluence.terraentity.mixed.IPlayer;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.chester.ChesterConditionalType;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Chester extends AbstractSummonMob implements IPetMob {

    Player opener;
    ChesterItemHandler itemHandler = new ChesterItemHandler(27);
    int openTime = 20;
    private final int _openTime = 20;


    static RawAnimation sleep = RawAnimation.begin().thenLoop("sleep");
    static RawAnimation open = RawAnimation.begin().thenPlay("open");
    static RawAnimation close = RawAnimation.begin().thenPlay("close");


    private static final EntityDataAccessor<Boolean> DATA_OPEN = SynchedEntityData.defineId(Chester.class, EntityDataSerializers.BOOLEAN);


    public Chester(EntityType<? extends Chester> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean shouldDoCollision(){
        return false;
    }

    public boolean isOpen() {
        return getEntityData().get(DATA_OPEN);
    }
    public void setOpen(boolean open) {
        getEntityData().set(DATA_OPEN, open);
    }
    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public float summon_getStartDistanceToOwner(){
        return 3 * 3;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(player.getMainHandItem().getItem() instanceof SummonItem<?>){
            return InteractionResult.PASS;
        }
        if(!level().isClientSide()){
            var data = player.getData(TEAttachments.SUMMONER_STORAGE);
            var globalEntry = TERegistries.CHESTER_TYPES.entrySet();
            int globalSize = globalEntry.size();
            if(data.chestType == globalSize - 1 && data.chestTypeAdditional != 0){
                int additionalSize = data.chestTypeAdditional;
                int maxAdditionalSize = data.boundBlocks.size();
                if(additionalSize <= maxAdditionalSize){
                    // 打开箱子
                    Map.Entry<SummonerAttachment.Key, ChesterConditionalType> entry = data.boundBlocks.entrySet().stream().toList().get(data.chestTypeAdditional - 1);
                    BlockPos pos = entry.getKey().pos();
                    ChesterConditionalType type = entry.getValue();
                    ((IPlayer) player).terra_entity$setInfiniteInteractBlock(true);
                    Level level = Objects.requireNonNull(level().getServer()).getLevel(entry.getKey().levelId());
                    if(type.tryOpen(pos, player, level)){
//                        player.sendSystemMessage(Component.literal("打开此箱子 :" + pos.toString()));
                        setOpen(true);
                        this.opener = player;
                        return InteractionResult.SUCCESS;
                    }
                    player.sendSystemMessage(Component.literal("无法打开此箱子 :" + pos.toString()));
                }
            }else{
                // 打开全局存储器
                player.openMenu(globalEntry.stream().toList()
                        .get(data.chestType).getValue()
                        .getMenuProviderSupplier().get());
                setOpen(true);
                this.opener = player;
                return InteractionResult.SUCCESS;
            }


        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "Chester", 5, state->{
            state.setControllerSpeed(1f);
            if(this.openTime > 0){
                if(this.isOpen()){
                    return state.setAndContinue(open);
                }
                return state.setAndContinue(close);
            }
            if(state.isMoving()){
                state.setControllerSpeed(3f);
                return state.setAndContinue(DefaultAnimations.WALK);
            }
            if(this.level().dayTime() % 24000 > 13000){
                return state.setAndContinue(sleep);
            }
            return state.setAndContinue(DefaultAnimations.IDLE);
        }));
    }

    public ItemStackHandler getInventory() {
        return itemHandler;
    }

    public static class ChesterItemHandler extends ItemStackHandler{
        public ChesterItemHandler () {
            super();
        }

        public ChesterItemHandler (int size) {
            super(size);
        }

        public ChesterItemHandler (NonNullList<ItemStack> stacks) {
            super(stacks);
        }

    }

    public void summon(Player player, ItemStack stack) {
        super.summon(player, stack);
        // 只能同时存在一个
        SummonerAttachment data = player.getData(TEAttachments.SUMMONER_STORAGE);
        List<Integer> list = data.getIds();
        for (Integer integer : list) {
            Entity e = level().getEntity(integer);
            if (e instanceof Chester) {
                e.discard();
            }
        }
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OPEN, false);
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key == DATA_OPEN){
            this.openTime = this._openTime;
        }

    }

    @Override
    public void tick() {
        super.tick();
        --this.openTime;
        if (opener != null && isOpen() && !this.level().isClientSide) {
            if(opener.containerMenu instanceof InventoryMenu){
                this.setOpen(false);
                this.opener = null;
            }
        }
    }
}
