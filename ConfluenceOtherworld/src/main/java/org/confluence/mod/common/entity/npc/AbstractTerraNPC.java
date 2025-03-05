package org.confluence.mod.common.entity.npc;

import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.goal.NPCTradeGoal;
import org.confluence.mod.common.init.ModEntityDataSerializers;
import org.confluence.mod.common.menu.NPCTradesMenu;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class AbstractTerraNPC extends PathfinderMob implements GeoEntity {

    public NPCTrades trades;
    public Player tradingPlayer;
    boolean forge;

    public AbstractTerraNPC(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);

        if(!level.isClientSide()){
            trades = NPCTrades.getTrade(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()));
            entityData.set(DATA_DAVE_DATA, trades);
        }
    }

    private static final EntityDataAccessor<NPCTrades> DATA_DAVE_DATA = SynchedEntityData.defineId(AbstractTerraNPC.class, ModEntityDataSerializers.DAVE_TRADES_SERIALIZER.get());

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2,new NPCTradeGoal(this));

        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Monster.class, 20, 0.3f, 0.3f));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide() && DATA_DAVE_DATA.equals(key)) {
            this.trades = this.entityData.get(DATA_DAVE_DATA);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DAVE_DATA, new NPCTrades(List.of()));

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("dave_data", 10)) {
            DataResult<NPCTrades> data = NPCTrades.CODEC.parse(NbtOps.INSTANCE, compound.get("dave_data"));
            this.entityData.set(DATA_DAVE_DATA, data.result().get());
            this.trades = data.result().get();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        DataResult<Tag> data = NPCTrades.CODEC.encodeStart(NbtOps.INSTANCE, trades);
        compound.put("dave_data",data.result().get());
    }


    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if(level().isClientSide()){
            this.trades = this.entityData.get(DATA_DAVE_DATA);
        }

    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        player.openMenu(new SimpleMenuProvider((id, playerInventory, player1) -> new NPCTradesMenu(id,playerInventory, trades, forge), Component.translatable("confluence.menu.npc_shop")));
        tradingPlayer = player;
        return InteractionResult.PASS;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
