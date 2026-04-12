---
title: 暴击率
author: Westernat
version: 1.3.0
---

# 暴击率

当攻击者的暴击率为 n% 时，其攻击时会有 n% 的概率触发暴击，暴击伤害为原伤害的 1.5 倍。

如果启用了伤害指示器（即攻击时显示在被攻击者头上的数字），暴击伤害会用醒目的颜色来标记，如 **<
color=#AA0000>67</color>**。

该属性还会应用到任意继承自`AbstractArrow`类的射弹上（如各类箭矢，三叉戟），则这些射弹会被标记为暴击射弹。

::: tip
该属性为**可替换**属性，即你可以通过`<gamedir>/config/confluence_magic_lib-startup.toml`文件修改。

且当同时安装**神化属性**时，该属性默认替换为`apothic_attributes:crit_chance`。
:::

## 数据

|          |                                                                                   |
|:--------:|:---------------------------------------------------------------------------------:|
|   内部键    | <confluence_hover>confluence_magic_lib:generic.critical_chance</confluence_hover> |
|   翻译键    |    <confluence_hover>attribute.name.generic.critical_chance</confluence_hover>    |
|   默认值    |                                        0.0                                        |
|   最小值    |                                        0.0                                        |
|   最大值    |                                       10.0                                        |
| 推荐的修饰符操作 |                                     ADD_VALUE                                     |
|    类型    |     <hover type="SHOW_TEXT" data="增加正面属性值的修饰符为蓝色，减少正面属性值的修饰符为红色。">正面</hover>      |

::: tip
暴击率 10% 的实际值其实是 0.1 而不是 10.0
:::

## 历史

- 1.3.0: 命名空间从`terra_curio`更改为`confluence_magic_lib`
- 1.0.0: 添加了暴击率属性
