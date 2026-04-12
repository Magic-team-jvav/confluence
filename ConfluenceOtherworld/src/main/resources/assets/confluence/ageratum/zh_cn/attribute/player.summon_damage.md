---
title: 召唤伤害
author: Westernat
version: 1.3.0
---

# 召唤伤害

该属性会影响攻击者的鞭子伤害，以及召唤物的最终伤害。

```
最终伤害 = 原始伤害 * 属性值
```

::: tip
可以认为该伤害为基础伤。
:::

## 数据

|          |                                                                                 |
|:--------:|:-------------------------------------------------------------------------------:|
|   内部键    | <confluence_hover>confluence_magic_lib:generic.summon_damage</confluence_hover> |
|   翻译键    |    <confluence_hover>attribute.name.generic.summon_damage</confluence_hover>    |
|   默认值    |                                       1.0                                       |
|   最小值    |                                       0.0                                       |
|   最大值    |                                     2048.0                                      |
| 推荐的修饰符操作 |                                 MULTIPLY_TOTAL                                  |
|    类型    |    <hover type="SHOW_TEXT" data="增加正面属性值的修饰符为蓝色，减少正面属性值的修饰符为红色。">正面</hover>     |

## 历史

- 1.3.0: 命名空间从`terra_entity`更改为`confluence_magic_lib`
- 1.0.0: 添加了召唤伤害属性
