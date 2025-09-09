- [English](README-en_us.md)

| 项目名                                                         | 备注                | 是否独立运行                |
|-------------------------------------------------------------|-------------------|-----------------------|
| [ConfluenceOtherworld](ConfluenceOtherworld)                | 本体                | 依赖所有                  |
| [TerraEntity](TerraEntity)                                  | 泰拉实体生物            | 是                     |
| [MineTeam](MineTeam)                                        | 类泰拉团队机制           | 是                     |
| [EquipmentBenediction](EquipmentBenediction)                | 多种装备加成机制          | 是                     |
| [TerraCurio](TerraCurio)                                    | 泰拉饰品              | 是                     |
| [TerraGuns](TerraGuns)                                      | 泰拉枪支              | 是                     |
| [ParticleStorm](https://github.com/westernat/ParticleStorm) | 为复杂的粒子提供API       | 是                     |
| [HeavenDestinyMoment](HeavenDestinyMoment)                  | 提供各类事件机制API       | 是                     |
| [PhaseJourney](PhaseJourney)                                | 提供各类阶游戏段机制API     | 是                     |
| [TerraMoment](TerraMoment)                                  | 泰拉事件              | 依赖HeavenDestinyMoment |
| [IsekaiInvaded](IsekaiInvaded)                              | 多维度入侵事件           | 依赖HeavenDestinyMoment |
| [TerraFurniture](TerraFurniture)                            | 泰拉家具              | 是                     |
| [EntityTrackerHUD](EntityTrackerHUD)                        | 提供包括Boss实体的实时位置提示 | 是                     |

| 官方附属                                                                                | 备注                 |
|-------------------------------------------------------------------------------------|--------------------|
| [ConfluenceDelight](https://github.com/cooobird/ConfluenceDelight)                  | 汇流乐事               |
| [ConfluenceMusic](https://github.com/westernat/Confluence-Music)                    | 汇流音乐               |
| [ConfluenceDimensionPatch](https://github.com/westernat/Confluence-Dimension-Patch) | 将汇流来世对主世界的修改转移到新维度 |

## 构建项目

- 项目拉取完成后,确认子项目文件是否有拉取，如空文件夹请按下列指令在终端内顺序执行
  ~~~cmd
    git submodule init
    git submodule update
  ~~~

- 如有其他情况可以手动添加
  ~~~cmd
    git submodule add -b "分支名" "url"
  ~~~
  > 还需要在[settings.gradle](settings.gradle)
  > 以及[ConfluenceOtherworld/build.gradle](ConfluenceOtherworld/build.gradle)里加上子模块名

- 全部拉取完成后,在gradle插件中启动ConfluenceOtherworld本体项目中的runClient命令

## Maven 例子

```groovy
repositories {
    maven {
        name "org.confluenceReleases"
        url "https://maven.confluence.ink/releases"
    }
    maven {
        name "org.confluenceSnapshots"
        url "https://maven.confluence.ink/snapshots"
    }
}

dependencies {
    implementation "org.confluence.lib:Confluence-Magic-Lib:<version>"
}
```
