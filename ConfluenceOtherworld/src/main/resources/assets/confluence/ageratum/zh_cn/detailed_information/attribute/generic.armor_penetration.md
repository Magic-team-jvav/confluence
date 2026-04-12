---
title: 护甲穿透
author: Westernat
version: 1.3.0
---

# 护甲穿透

如果攻击者的护甲穿透值不为0，那么被攻击者受到攻击者的伤害时，会先收集被攻击者的总护甲值，然后减去攻击者的护甲穿透值，得到最终护甲值。

```
最终护甲值 = 总护甲值 - 护甲穿透值
```

最后，被攻击者会通过最终护甲值计算伤害减免。

::: tip
该属性为**可替换**属性，即你可以通过`<gamedir>/config/confluence_magic_lib-startup.toml`文件修改。

且当同时安装**神化属性**时，该属性默认替换为`apothic_attributes:armor_pierce`。
:::

## 数据

|          |                                                                                     |
|:--------:|:-----------------------------------------------------------------------------------:|
|   内部键    | <confluence_hover>confluence_magic_lib:generic.armor_penetration</confluence_hover> |
|   翻译键    |    <confluence_hover>attribute.name.generic.armor_penetration</confluence_hover>    |
|   默认值    |                                         0.0                                         |
|   最小值    |                                         0.0                                         |
|   最大值    |                                       10000.0                                       |
| 推荐的修饰符操作 |                                      ADD_VALUE                                      |
|    类型    |      <hover type="SHOW_TEXT" data="增加正面属性值的修饰符为蓝色，减少正面属性值的修饰符为红色。">正面</hover>       |

## 历史

- 1.3.0: 命名空间从`terra_curio`更改为`confluence_magic_lib`
