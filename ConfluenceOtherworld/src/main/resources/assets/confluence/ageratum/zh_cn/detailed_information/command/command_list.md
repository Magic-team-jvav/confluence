# 游戏阶段

## 格式：

- /confluence gamePhase set <gamePhase>
- /confluence gamePhase get

## 用途：

- 获取/修改游戏阶段。
- 如果是修改游戏阶段，则最后的 <gamePhase> 处需填入想要修改的游戏阶段。
- 举例：
    - /confluence gamePhase set AFTER_SKELETRON：将游戏阶段设置为“骷髅王后”。

# 指定陨石落地位置

## 格式：

- /confluence meteorite <pos> <tick>

## 用途：

- 使陨石在指定位置落地。
- <pos> 处需填入 x、y、z 坐标，这是陨石落地的位置的坐标。<tick> 处则为陨石落地的延时。该指令的 y 坐标实际上不会对实际效果有影响。

- 举例：
    - /confluence meteorite ~ ~ ~ 20：在 20 tick（一秒）后，陨石会在玩家输入指令时的位置落地。

# 为指定方块刷上/移除油漆

## 格式：

- /confluence paint <start> <end> brush <face> <color>
- /confluence paint <start> <end> brush <color>
- /confluence paint <start> <end> scraper <face>
- /confluence paint <start> <end> scraper

## 用途：

# 给指定范围内的方块刷上或移除油漆。

- <start> 和 <end> 处需填入两组 x、y、z 值，定义填充区域的两组对角方块坐标（类似于/fill指令）；
- brush 代表刷上油漆，scraper 则代表移除油漆；
- <face> 处需填入方块的方向，例如 UP、DOWN 等，代表方块哪个方向上的面会被刷上/移除油漆。此项可以不填，若不填则会直接为方块的所有面刷上/移除油漆；
- <rbga> 值代表油漆的颜色，需传入的 <color> 值为24位ARBG颜色编码，并且 alpha（不透明度） 固定为 255 。

# 给予油漆

## 格式：

- /confluence paint item <color>

## 用途：

- 给予玩家一个指定颜色的油漆。
- <color> 处同样需要传入 24 位颜色编码。

# 重载资源包

## 格式：

/confluence reload SPELUNKER

## 用途：

- 重载矿透资源包。

# 获取星象

## 格式：

- /confluence starPhase set <index> <timeOffset> <radius> <angle>
- /confluence starPhase get <index>

## 用途：

- 可以修改/获取星象的信息。
- <index> 处是星象的索引，范围为 0-9 ；
- <timeOffset> 是星象的运动时间偏移；
- <radius> 表示轨道半径；
- <angle> 表示轨道角度。

# 修改/获取风向风速

## 格式：

- /confluence wind get
- /confluence wind set <x> <z> [持续时间]
- /confluence wind stop
- /confluence wind debug

## 用途：

- 风速使用国际单位 **m/s**，用**分量**表示：`<x>` / `<z>` 是风**吹向**的分量，`+X` 是东、`+Z` 是南（与原版坐标轴一致），负值表示相反方向。
  例如 `wind set 8 0` 是 8 m/s 的西风（吹向东），`wind set 0 -6` 是 6 m/s 的北风（吹向北），
  `wind set -5 5` 是东南向的斜风（风速 √50 ≈ 7.07 m/s）。
- `wind set` 强制恒定风速与风向，常用于测试大风天/沙尘暴；`set 0 0` 就是强制静风。
- **持续时间**（可选）以 **tick** 为单位，与 `/confluence meteorite` 的延时参数同一套单位：
  **20 tick = 1 秒**，因此 1200 tick = 1 分钟。
  省略则本局一直有效，直到用 `wind stop` 解除。例：`/confluence wind set 9 0 600` = 大风天级的风持续 30 秒。
- `wind get` 显示当前风的 x/z 分量、风速（m/s 与 mph）、**风来的方位**、档位与阵风系数。
  注意两个方向的区别：命令输入的 `<x> <z>` 是"吹向"，而气象台/`wind get` 显示的方位是"**来向**"
  （气象学惯例：东风 = 从东边吹来、吹向西）。两者正好相差 180 度。
  真实风向标的箭头也指向风来的方向，所以风标与气象台读数是一致的。
- `wind stop` 解除强制，让风恢复自然演化。
- `wind debug` 显示该位置的风向偏差、所属风晶格的坐标与曝露度，用于核对温度噪声造成的局地风向。

# 调整蔓延

## 格式：

- /gamerule confluenceSpreadableChanse <数值>

## 用途：

- 用于调整邪恶地形（如猩红之地）的蔓延速度。
- 在<数值>处填写具体数值即可自由调整蔓延速度，默认的蔓延数值为 10 ，调整至 0 即可关闭蔓延。

# 开始/结束事件

## 格式：

- /confluence gameEvent start/end <事件id>

## 用途：

- 用于开始或结束某些事件，/confluence gameEvent start <事件id> 为开始事件，/confluence gameEvent end <
  事件id> 为结束事件。

- 事件id：
    - confluence:slime_rain 史莱姆雨
    - confluence:blood_moon 血月
    - confluence:goblin_army 哥布林军队来袭
    - confluence:lantern_night 灯笼夜
    - confluence:meteor_shower 流星雨

# 重置灵魂技能装备

## 格式：

- /confluence soulSkill reset

## 用途：

- 把执行者的灵魂技能装备恢复到初始状态：清空全部装备槽位、当前选中索引归零、清空所有技能冷却。
- 该指令不会删除已经永久学会的技能、临时可用状态或技能书记录，只重置"装了什么"。
- 举例：
    - /confluence soulSkill reset：把三个槽位全部清空，并重新计算当前可用技能。

# 调整灵魂技能装备数量

## 格式：

- /confluence soulSkill slots <count>

## 用途：

- 修改执行者可装备的灵魂技能槽位数量，<count> 只能填 3 到 6 之间的整数。
- 初始状态为 3 个槽位；第 1 / 3 / 5 次仪式会各自动解锁一个栏位（3 → 4 → 5 → 6），本条指令用于手动覆盖。
- 数量增加时多出的槽位为空，数量减少时会裁掉超出部分的槽位内容。
- 修改后会立即同步到客户端，并按其重新计算一次可用技能。
- 举例：
    - /confluence soulSkill slots 6：把槽位数量扩展到 6。
    - /confluence soulSkill slots 3：把槽位数量收回初始的 3。
# 设置仪式次数

## 格式：

- /confluence soulSkill ritual <count/confluence soulSkill ritual <count

## 用途：

- 设置执行者已完成的献祭仪式次数，<count> 只能填 0 到 6 之间的整数。
- 每完成一次仪式提升 30 点灵魂上限，全流程最多 6 次（合计 +180）。
- 次数提高时会自动解锁对应的门槛技能；次数降低时灵魂上限同步降低，超出上限的当前灵魂会被压低。
- 第 1 / 3 / 5 次仪式会各解锁一个技能栏位（3 → 4 → 5 → 6），减少次数时栏位回退。
- 举例：
    - /confluence soulSkill ritual 1：完成第一次仪式，解锁强力引魂、附魂印记、灵魂透支、魂术轮转与激荡系列。
    - /confluence soulSkill ritual 0：把仪式次数退回 0，灵魂上限随之减少 30。
