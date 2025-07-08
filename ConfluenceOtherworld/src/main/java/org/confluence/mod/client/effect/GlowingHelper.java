package org.confluence.mod.client.effect;

import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Slime;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class GlowingHelper {
    public static final GlowingHelper INSTANCE = Util.make(new GlowingHelper(), helper -> {
        //狩猎药水实体   表优先于中立生物和敌人
        helper.addHunter(Slime.class, Color.MAGENTA, true);//EXAMPLE:史莱姆持续显示紫色，覆盖enemy的orange
        helper.addHunter(Animal.class, Color.green, false);//动物

        //危险感知实体
        helper.addDanger(BoulderEntity.class, Color.red, true);//巨石
    });

    /**
     * 狩猎药水发光
     **/
    public boolean alwaysShow = false;

    public Color defaultColor = Color.white;//其他生物颜色
    public Color neutralColor = Color.blue;//中立生物颜色
    public boolean alwaysShowNeutral = false;//中立生物持续显示
    public Color angerColor = Color.red;//todo 中立生物愤怒颜色
    public Color enemyColor = Color.orange;//敌人颜色

    public Map<Class<? extends Entity>, Data> colorMap = new LinkedHashMap<>();//优先颜色类型表
    public List<Class<? extends Entity>> hunterCatalog = new ArrayList<>();
    public List<Class<? extends Entity>> dangerCatalog = new ArrayList<>();

    public void addHunter(Class<? extends Entity> clazz, Color color, boolean alwaysShow) {
        hunterCatalog.add(clazz);
        colorMap.put(clazz, new Data(color, alwaysShow));
    }

    public void addDanger(Class<? extends Entity> clazz, Color color, boolean alwaysShow) {
        dangerCatalog.add(clazz);
        colorMap.put(clazz, new Data(color, alwaysShow));
    }

    public record Data(Color color, boolean alwaysShow) {}
}
