# 更新日志（1.2.3）：

## 注意事项：

- 幽灵护目镜的临时配方已经删除，改为机械师在灵雾环境购买获取

## 修复：

- 修复血月无法自然触发的问题
- 修复成就完成时不广播的问题
- 修复祭坛左键会合成两次的问题
- 修复小动物保护指南导致所有生物都不能被攻击的问题
- 修复吊灯火焰光层与Iris的兼容性问题
- 修复多人模式下一人重生导致附近BOSS删除的问题
- 修复玩家召唤物困船问题
- 修复IdFixer加载过早导致的崩溃
- 修复灰烬枝杈渲染问题

## 优化：

- 策略性将未染色的通透玻璃和玻璃家具的模型渲染设置为cutout以扩展天气粒子可见性

## 调整：

- 同步泰拉瑞亚1.4.5版本：
  - 幽灵护目镜改为机械师在灵雾环境购买获取
  - 提高魔粉，毒粉，精华粉的效果距离
  - 臭味药水炼药桌配方改为获取俩瓶
  - 史莱姆之母、小雪怪 、 亡灵维京的罗盘掉落几率从 2% 降低至 1%
  - 蝙蝠敌人掉落深度计几率从 1% 降低至 0.5%
  - 蝙蝠敌人掉落蝙蝠棍几率从 4% 降低至 3%
  - 龙虾和巨型卷壳怪掉落对打球几率从 4% 增加到 6.67%
  - 血肉指虎和腐香囊可在微光互相转化
  - 更改武士刀，岩浆骷髅头，熔火骷髅玫瑰的价值
  - 挖掘鼹鼠矿车需要解锁85个图鉴条目获得
  - 臭味效果存在时减少0.25点运气，臭味效果可以通过接触水消除
  - 蛋酒耐药性持续时间从 60 秒减少到 45 秒
  - 云块系列可被铲子系列加速挖掘
  - 提炼机现在可提炼臭臭为泥土等物品，可提炼蜂巢为蜂蜜块，可提炼黑曜石为沙块
  - 特殊种子
    - No Traps的比特位从0b100000_00000000改成0b1000000_00000000
    - Don't Dig Up的比特位从0b1000000_00000000改成0b100000_00000000
    - Boulder World的比特位从0b100000000_00000000改成0b1000000000_00000000
    - 添加Skyblock，比特位为0b100000000_00000000
- 略微提高地下木箱，地表木箱的生成概率
- 略微降低墓穴爬虫的生成率
- 略微提高小雪怪，亡灵维京，冰雪蝙蝠，尖刺冰雪史莱姆，洞穴蝙蝠的生成率
- 取消阻尼摆类型家具受世界风影响的特性
- 地牢守卫者现在不会保存进卸载区块

## 更新内容：

- 全局成就及其界面（仅汇流来世成就）
- 世界创建的特殊种子选择界面（这些特殊全部没有做完）
- 血爬虫兼容Nyf's Spiders
- 家具床
- 丛林草方块，蘑菇草方块，灰烬草方块可以用铲子制造对应土径
- 阻止Lootr替换汇流来世的箱子
- 小雪怪皮毛床、大理石桌、气球桌
- 1.4.5新饰品：牧羊符文，可于地牢金箱，金锁盒中获取

# Changelog (1.2.3):

## Notes:

- The temporary recipe for the Spectre Goggles has been removed. They are now purchased from the Mechanic while in the Ecto Mist.

## Fixes:

- Fixed an issue where Blood Moons would not trigger naturally.
- Fixed an issue where achievements would not broadcast when completed.
- Fixed an issue where left-clicking an Altar would craft twice.
- Fixed an issue where the Guide to Critter Companions would prevent all creatures from being attacked.
- Fixed a compatibility issue with the Chandelier's flame light layer and Iris.
- Fixed an issue in multiplayer where one player respawning would cause nearby bosses to despawn.
- Fixed an issue where player summons could get stuck/lost.
- Fixed a crash caused by IdFixer loading too early.
- Fixed rendering issues with the Ashen Stalk.

## Improvements:

- Strategically set the render type for undyed Clear Glass and Glass furniture to Cutout to improve weather particle visibility.

## Adjustments:

- Synced with Terraria version 1.4.5:
  - Spectre Goggles are now purchased from the Mechanic while in the Spooky Fog.
  - Increased the effective range of Vile Powder, Vicious Powder, and Essence Powder.
  - The Alchemy Table recipe for Stink Potions now yields 2 potions.
  - Decreased the drop rate of the Compass from Mother Slimes, Snow Flinxes, and Viking Zombies from 2% to 1%.
  - Decreased the drop rate of the Depth Meter from Bats from 1% to 0.5%.
  - Decreased the drop rate of the Bat Bat from Bats from 4% to 3%.
  - Increased the drop rate of the Tunava from Lobsters and Giant Shellies from 4% to 6.67%.
  - The Flesh Knuckles and Putrid Scent can now be shimmered into each other.
  - Changed the sell values of the Katana, Magma Skull, and Molten Skull Rose.
  - The Digging Molecart now requires unlocking 85 Bestiary entries to obtain.
  - Being under the Stink effect now reduces luck by 0.25. The Stink effect can be removed by touching water.
  - Reduced the duration of the Eggnog cooldown (Potent Sickness) from 60 seconds to 45 seconds.
  - Cloud blocks can now be mined faster with Shovels.
  - The Extractinator can now process Stinkbugs into items like Dirt, process Hive into Honey Blocks, and process Obsidian into Sand Blocks.
  - Special Seeds:
    - The bit flag for the No Traps seed was changed from `0b100000_00000000` to `0b1000000_00000000`.
    - The bit flag for the Don't Dig Up seed was changed from `0b1000000_00000000` to `0b100000_00000000`.
    - The bit flag for the Boulder World seed was changed from `0b100000000_00000000` to `0b1000000000_00000000`.
    - Added the Skyblock seed, using the bit flag `0b100000000_00000000`.
- Slightly increased the generation chance of Underground and Surface Wooden Crates.
- Slightly decreased the spawn rate of Tomb Crawlers.
- Slightly increased the spawn rates of Snow Flinxes, Viking Zombies, Ice Bats, Spiked Ice Slimes, and Cave Bats.
- Removed the property where pendulum-type furniture (Damping Pot) would be affected by world wind.
- Dungeon Guardians will now be saved when their chunk is unloaded.

## Additions:

- Added global achievements and their interface (for Conflux Legacy achievements only).
- Added a special seed selection interface during world creation (these special seeds are not fully implemented yet).
- Added compatibility for Blood Crawlers with Nyf's Spiders.
- Added Furniture Beds.
- Added the ability to create Dirt Paths from Jungle Grass, Mushroom Grass, and Ash Grass blocks using Shovels.
- Added a fix to prevent Lootr from replacing Conflux Legacy chests.
- Added the Snow Flinx Fur Bed, Marble Table, and Balloon Table.
- Added the 1.4.5 new accessory: Shepherd's Crook, obtainable from Dungeon Gold Chests and Golden Lock Boxes.
