package org.confluence.mod.common.init.entity;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 对注册表运行时 ID 与声明 ID 做无游戏生命周期依赖的双向比较。
 *
 * <p>这里故意只接收完整字符串 ID。这样普通单元测试可以注入伪造条目，验证遗漏检测和诊断内容，
 * 而不必提前启动 Minecraft 或 Forge 注册阶段。</p>
 */
final class RegistryCoverageComparison {
    private RegistryCoverageComparison() {}

    static Difference compare(Set<String> runtimeIds, Set<String> declarationIds) {
        Set<String> runtimeOnly = new TreeSet<>(runtimeIds);
        runtimeOnly.removeAll(declarationIds);
        Set<String> declarationOnly = new TreeSet<>(declarationIds);
        declarationOnly.removeAll(runtimeIds);
        return new Difference(runtimeOnly, declarationOnly);
    }

    /**
     * 分别比较同一批注册 ID 与多类资源 ID，保留每个资源轴的独立诊断结果。
     *
     * <p>{@code runtimeOnly} 表示已注册但缺少该类资源，{@code declarationOnly} 表示资源存在但
     * 没有对应注册。调用方因此可以区分缺失资源和孤儿资源，而不必把所有差异混成一个集合。</p>
     */
    static Matrix compareAxes(
            Set<String> runtimeIds,
            Map<String, Set<String>> declarationIdsByAxis) {
        Map<String, Difference> axes = new TreeMap<>();
        declarationIdsByAxis.forEach((axis, declarationIds) ->
                axes.put(axis, compare(runtimeIds, declarationIds)));
        return new Matrix(Map.copyOf(axes));
    }

    record Difference(Set<String> runtimeOnly, Set<String> declarationOnly) {}

    record Matrix(Map<String, Difference> axes) {}
}
