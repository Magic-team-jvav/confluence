package org.confluence.terraentity.entity.ai.goal.behavior.condition;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;

/**
 * 行为树条件接口
 */
@FunctionalInterface
public interface Condition {

    boolean check();

    default @Nullable String getDesc(){
        return null;
    }

    default Condition setConDesc(String desc) {
        return this;
    }

    static NotCondition not(Condition condition) {
        return new NotCondition(condition);
    }

    static AndCondition and(Condition... conditions) {
        return new AndCondition(Lists.newArrayList(conditions));
    }

    static OrCondition or(Condition... conditions) {
        return new OrCondition(Lists.newArrayList(conditions));
    }

}

