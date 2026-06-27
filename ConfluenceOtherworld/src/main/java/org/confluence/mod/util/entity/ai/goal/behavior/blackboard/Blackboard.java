package org.confluence.mod.util.entity.ai.goal.behavior.blackboard;

import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;
import org.confluence.mod.util.entity.ai.goal.behavior.condition.Condition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/// 黑板变量，共享行为树数据
public class Blackboard {
    private final Map<KeyType<?>, Object> data;

    public Blackboard() {
        this.data = new HashMap<>();
    }

    public static Blackboard create() {
        return new Blackboard();
    }

    public <V> void put(KeyType<V> key, V value) {
        this.data.put(key, value);
    }

    public void remove(KeyType<?>key) {
        this.data.remove(key);
    }

    public <V> V get(KeyType<V> key) {
        return (V) this.data.get(key);
    }

    public boolean containsKey(KeyType<?> key) {
        return this.data.containsKey(key);
    }

    public <V> boolean containsValue(KeyType<V> key, Predicate<V> valuePredicate) {
        V value = this.get(key);
        return valuePredicate.test(value);
    }

    public static <V> Condition containsKey(IBlackboardHolder holder, KeyType<V> key) {
        return new ContainsKey(holder, key);
    }

    public static <V> Condition notContainsKey(IBlackboardHolder holder, KeyType<V> key) {
        return Condition.not(containsKey(holder, key));
    }

    public static <V> Condition containsValue(IBlackboardHolder holder, KeyType<V> key, Predicate<V> valuePredicate) {
        return new ContainsValue<>(holder, key, valuePredicate);
    }

    public static <V> Condition notContainsValue(IBlackboardHolder holder, KeyType<V> key, Predicate<V> valuePredicate) {
        return Condition.not(containsValue(holder, key, valuePredicate));
    }

    public static <V> BTNode setValue(IBlackboardHolder holder, KeyType<V> key, Supplier<V> valueSupplier) {
        return new SetValue<>(holder, key, valueSupplier);
    }

    public static <V> BTNode removeValue(IBlackboardHolder holder, KeyType<V> key) {
        return new RemoveValue<>(holder, key);
    }

    public record ContainsKey(IBlackboardHolder holder, KeyType<?> key) implements Condition {
        @Override
        public boolean check() {
            return holder.getBlackboard().containsKey(key);
        }
    }

    public record ContainsValue<V>(IBlackboardHolder holder, KeyType<V> key, Predicate<V> valuePredicate) implements Condition {
        @Override
        public boolean check() {
            return holder.getBlackboard().containsValue(key, valuePredicate);
        }
    }

    public static class SetValue<V> extends BTNode {
        final IBlackboardHolder holder;
        final KeyType<V> key;
        final Supplier<V> valueSupplier;

        public SetValue(IBlackboardHolder holder, KeyType<V> key, Supplier<V> valueSupplier) {
            this.holder = holder;
            this.key = key;
            this.valueSupplier = valueSupplier;
        }

        @Override
        public BTStatus execute() {
            holder.getBlackboard().put(key, valueSupplier.get());
            return BTStatus.SUCCESS;
        }
    }

    public static class RemoveValue<V> extends BTNode {
        final IBlackboardHolder holder;
        final KeyType<V> key;
        public RemoveValue(IBlackboardHolder holder, KeyType<V> key) {
            this.holder = holder;
            this.key = key;
        }
        @Override
        public BTStatus execute() {
            holder.getBlackboard().remove(key);
            return BTStatus.SUCCESS;
        }
    }
}
