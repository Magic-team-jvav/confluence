package org.confluence.mod.common.entity.boss.plantera;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.utils.TEUtils;

import java.util.List;

public abstract class AbstractPlanteraTentacleSrc extends AbstractTerraBossBase {
    // 默认null，进入阶段后初始化；死去的触手不要清理，以便追踪最大生命值信息等
    protected List<PlanteraTentacle> tentacles = null;

    public AbstractPlanteraTentacleSrc(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    // PlanteraTentacle实例会更新tentacles列表（直接生成与区块加载等方式载入逻辑都在同一处更新）
    protected PlanteraTentacle spawnTentacle() {
        Vec3 spawnLocOffset = TEUtils.rotToDir(random.nextFloat() * 360, random.nextFloat() * 180 - 90);
        return TEUtils.spawnEntity(
                () -> new PlanteraTentacle(TEBossEntities.PLANTERA_TENTACLE.get(), level(), this),
                (ServerLevel) level(), position().add(spawnLocOffset));
    }
}
