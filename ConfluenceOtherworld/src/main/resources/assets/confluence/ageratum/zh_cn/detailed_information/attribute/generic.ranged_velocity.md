---
title: 远程速度
author: Westernat
version: 1.3.0
---

# 远程速度

当攻击者发射出来的射弹继承自`AbstractArrow`类时（如各类箭矢，三叉戟），该属性会这样影响射弹的速度：

```
最终速度 = 原始速度 * 属性值
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
