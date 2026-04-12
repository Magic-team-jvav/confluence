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

# 修改/获取风速

## 格式：

- /confluence windSpeed set <x> <z>
- /confluence windSpeed get

## 用途：

- 可以修改/获取风速。
- <x> <z> 是一个二维向量，它影响风向和风速。

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

- 用于开始或结束某些事件，/confluence gameEvent start <事件id> 为开始事件，/confluence gameEvent end <事件id> 为结束事件。

- 事件id：

  - confluence:slime_rain 史莱姆雨
  - confluence:blood_moon 血月
  - confluence:goblin_army 哥布林军队来袭
  - confluence:lantern_night 灯笼夜
  - confluence:meteor_shower 流星雨