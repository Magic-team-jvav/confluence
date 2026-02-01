package org.confluence.mod.client.effect;

import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Slime;
import org.confluence.lib.color.IntegerRGB;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GlowingHelper {
    public static final GlowingHelper INSTANCE = Util.make(new GlowingHelper(), helper -> {
        //狩猎药水实体   表优先于中立生物和敌人
        helper.addHunter(Slime.class, IntegerRGB.of(255, 0, 255), true); // EXAMPLE:史莱姆持续显示紫色，覆盖enemy的orange
        helper.addHunter(Animal.class, IntegerRGB.GREEN, false); // 动物

        //危险感知实体
        helper.addDanger(BoulderEntity.class, IntegerRGB.RED, true); // 巨石
    });

    /// 狩猎药水发光
    public boolean alwaysShow = false;

    public IntegerRGB defaultColor = new IntegerRGB(255, 0, 255); // 其他生物颜色
    public IntegerRGB neutralColor = IntegerRGB.BLUE; // 中立生物颜色
    public boolean alwaysShowNeutral = false; // 中立生物持续显示
    public IntegerRGB angerColor = IntegerRGB.RED; // todo 中立生物愤怒颜色
    public IntegerRGB enemyColor = IntegerRGB.ORANGE; // 敌人颜色

    public Map<Class<? extends Entity>, Data> colorMap = new LinkedHashMap<>(); // 优先颜色类型表
    public List<Class<? extends Entity>> hunterCatalog = new ArrayList<>();
    public List<Class<? extends Entity>> dangerCatalog = new ArrayList<>();

    public void addHunter(Class<? extends Entity> clazz, IntegerRGB color, boolean alwaysShow) {
        hunterCatalog.add(clazz);
        colorMap.put(clazz, new Data(color, alwaysShow));
    }

    public void addDanger(Class<? extends Entity> clazz, IntegerRGB color, boolean alwaysShow) {
        dangerCatalog.add(clazz);
        colorMap.put(clazz, new Data(color, alwaysShow));
    }

    public record Data(IntegerRGB color, boolean alwaysShow) {}
}
