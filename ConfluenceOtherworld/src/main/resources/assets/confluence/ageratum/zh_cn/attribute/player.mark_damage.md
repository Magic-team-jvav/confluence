---
title: 仆从标记伤害
author: Westernat
version: 1.3.0
---

# 仆从标记伤害

当攻击者造成的伤害源类型为`#terra_entity:summoner`，或者攻击者实现了`ISummonMob`
接口时（即各类召唤物实体），被攻击者受到的伤害为：

```
最终伤害 = 原始伤害 + 属性值
```

::: tip
可以认为该伤害为额外增伤。
:::

## 数据

|          |                                                                              |
|:--------:|:----------------------------------------------------------------------------:|
|   内部键    | <confluence_hover>confluence_magic_lib:player.mark_damage</confluence_hover> |
|   翻译键    |    <confluence_hover>attribute.name.player.mark_damage</confluence_hover>    |
|   默认值    |                                     0.0                                      |
|   最小值    |                                     0.0                                      |
| 推荐的修饰符操作 |                                    1024.0                                    |
|    类型    |   <hover type="SHOW_TEXT" data="增加正面属性值的修饰符为蓝色，减少正面属性值的修饰符为红色。">正面</hover>   |

## 历史

- 1.3.0: 命名空间从`terra_entity`更改为`confluence_magic_lib`
- 1.0.0: 添加了仆从标记伤害属性
