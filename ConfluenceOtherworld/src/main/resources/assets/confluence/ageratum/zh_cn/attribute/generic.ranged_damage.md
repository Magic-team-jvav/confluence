---
title: 远程伤害
author: Westernat
version: 1.3.0
---

# 远程伤害

该属性会影响攻击者的远程伤害。当攻击者造成的伤害源类型为`#minecraft:is_projectile`时，最终伤害如下：

```
最终伤害 = 原始伤害 * 属性值
```

::: tip
该属性为**可替换**属性，即你可以通过`<gamedir>/config/confluence_magic_lib-startup.toml`文件修改。

且当同时安装**神化属性**时，该属性默认替换为`apothic_attributes:arrow_damage`。
:::

## 数据

|          |                                                                                |
|:--------:|:------------------------------------------------------------------------------:|
|   内部键    | <confluence_hover>confluence_magic_lib:generic.dodge_chance</confluence_hover> |
|   翻译键    |    <confluence_hover>attribute.name.generic.dodge_chance</confluence_hover>    |
|   默认值    |                                      1.0                                       |
|   最小值    |                                      0.0                                       |
|   最大值    |                                      10.0                                      |
| 推荐的修饰符操作 |                                 MULTIPLY_TOTAL                                 |
|    类型    |    <hover type="SHOW_TEXT" data="增加正面属性值的修饰符为蓝色，减少正面属性值的修饰符为红色。">正面</hover>    |

## 历史

- 1.3.0: 命名空间从`terra_curio`更改为`confluence_magic_lib`
- 1.0.0: 添加了远程伤害属性
