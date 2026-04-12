---
title: 仇恨值
author: Westernat
version: 1.3.0
---

# 仇恨值

当实现自`Enemy`接口的生物（如各类怪物）尝试索敌玩家时，会寻找自己索敌范围内的所有玩家，

并对这些玩家的仇恨值进行比较，仇恨值最大的玩家会被优先索敌。

如果两位玩家的仇恨值一样，那么该生物不会改变其目标。

::: info
单人模式下无效
:::

## 数据

|          |                                                                          |
|:--------:|:------------------------------------------------------------------------:|
|   内部键    |  <confluence_hover>confluence_magic_lib:player.aggro</confluence_hover>  |
|   翻译键    |     <confluence_hover>attribute.name.player.aggro</confluence_hover>     |
|   默认值    |                                   0.0                                    |
|   最小值    |                                 -10000.0                                 |
|   最大值    |                                 10000.0                                  |
| 推荐的修饰符操作 |                                ADD_VALUE                                 |
|    类型    | <hover type="SHOW_TEXT" data="增加负面属性值的修饰符为红色，减少负面属性值的修饰符为蓝色。">负面</hover> |

## 历史

- 1.3.0: 命名空间从`terra_curio`更改为`confluence_magic_lib`
- 1.0.0: 添加了仇恨值属性
