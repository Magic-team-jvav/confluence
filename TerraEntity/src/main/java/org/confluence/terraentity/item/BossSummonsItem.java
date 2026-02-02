package org.confluence.terraentity.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.api.event.TEBossEvent;
import org.confluence.terraentity.network.s2c.SummonBossPacket;
import org.confluence.terraentity.utils.AdapterUtils;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.*;

public class BossSummonsItem<T extends Mob> extends Item {
    float maxSummonRange = 30;
    float offsetY = 0;
    float cameraDistance = 10;

    private final Supplier<EntityType<T>> entityType;
    private Predicate<Player> condition;
    private BiConsumer<Player, T> onSummon;
    private Function<Player, Vec3> summonPosFunc;
    private BiPredicate<Vec3, Player> summonPosPredict = ((pos, player)-> TEUtils.canSeePos(player, pos));

    public BossSummonsItem(Properties properties, Supplier<EntityType<T>> entityType) {
        super(properties);
        this.entityType = entityType;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if(usedHand == InteractionHand.OFF_HAND) return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        if (level instanceof ServerLevel serverLevel && (condition == null || condition.test(player))) {
            Vec3 pos = summonPosFunc == null? this.getSummonPos(player, 0.5f) : summonPosFunc.apply(player);
            if(!this.summonPosPredict.test(pos, player)){
                return InteractionResultHolder.fail(player.getItemInHand(usedHand));
            }
            EntityType<T> type = entityType.get();

            T entity = type.create(level);
            if (entity == null) {
                return InteractionResultHolder.fail(player.getItemInHand(usedHand));
            }

            TEBossEvent.Summon event = AdapterUtils.postEvent(new TEBossEvent.Summon(type, player, this.cameraDistance));
            if(event.isCanceled()){
                return InteractionResultHolder.fail(player.getItemInHand(usedHand));
            }

            entity.moveTo(pos);
            if(TEUtils.internalSpawnEntity(entity, serverLevel)){
                serverLevel.addFreshEntityWithPassengers(entity);
            }

            if (onSummon!= null) {
                onSummon.accept(player, entity);
            }

            if(event.shouldChangeCamera()) {
                SummonBossPacket.sendTo((ServerPlayer) player, entity, event.getDistance());
            }

            if(!player.isCreative()) {
                player.getItemInHand(usedHand).shrink(1);
            }
            player.invulnerableTime = 100;

            return InteractionResultHolder.consume(player.getItemInHand(usedHand));
        }
        return InteractionResultHolder.fail(player.getItemInHand(usedHand));
    }

    public EntityType<T> getEntityType() {
        return entityType.get();
    }

    public boolean canSummon(Vec3 pos, Player player){
        return summonPosPredict.test(pos, player);
    }

    public Vec3 getSummonPos(Player player, float partialTicks){
        Vec3 pos = TEUtils.getEyeVec3(player, this.maxSummonRange, partialTicks).add(0, offsetY, 0);
        int y = (int) pos.y();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(pos.x(),y,pos.z());
        while (!player.level().getBlockState(blockPos).isAir()){
            blockPos.move(0,1,0);
        }
        // todo 可以用轮滚控制
        return new Vec3(pos.x(), blockPos.getY(), pos.z);
    }

    public boolean hasSpecificSummonPos(){
        return summonPosFunc!= null;
    }

    public BossSummonsItem<T> setCondition(Predicate<Player> condition) {
        this.condition = condition;
        return this;
    }

    public BossSummonsItem<T> setOnSummon(BiConsumer<Player, T> onSummon) {
        this.onSummon = onSummon;
        return this;
    }

    public BossSummonsItem<T> setSummonPosFunc(Function<Player, Vec3> summonPosFunc) {
        this.summonPosFunc = summonPosFunc;
        return this;
    }

    public BossSummonsItem<T> setMaxSummonRange(float maxSummonRange,float cameraDistance ) {
        this.maxSummonRange = maxSummonRange;
        this.cameraDistance = cameraDistance;
        return this;
    }

    public BossSummonsItem<T> setOffsetY(float offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public BossSummonsItem<T> setSummonPosPredict(BiPredicate<Vec3, Player> predict){
        this.summonPosPredict = predict;
        return this;
    }

}
