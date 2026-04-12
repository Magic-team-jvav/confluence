---
title: 闪避率
author: Westernat
version: 1.3.0
---

# 闪避率

当被攻击者的闪避率为 n% 时，其会在被攻击时有 n% 的概率无视该次伤害。

::: tip
该属性为**可替换**属性，即你可以通过`<gamedir>/config/confluence_magic_lib-startup.toml`文件修改。

且当同时安装**神化属性**时，该属性默认替换为`apothic_attributes:dodge_chance`。
:::

## 数据

|          |                                                                                |
|:--------:|:------------------------------------------------------------------------------:|
|   内部键    | <confluence_hover>confluence_magic_lib:generic.dodge_chance</confluence_hover> |
|   翻译键    |    <confluence_hover>attribute.name.generic.dodge_chance</confluence_hover>    |
|   默认值    |                                      0.0                                       |
|   最小值    |                                      0.0                                       |
|   最大值    |                                      1.0                                       |
| 推荐的修饰符操作 |                                   ADD_VALUE                                    |
|    类型    |    <hover type="SHOW_TEXT" data="增加正面属性值的修饰符为蓝色，减少正面属性值的修饰符为红色。">正面</hover>    |

::: tip
闪避率 10% 的实际值其实是 0.1 而不是 10.0
:::

## 历史

- 1.3.0: 命名空间从`terra_curio`更改为`confluence_magic_lib`
