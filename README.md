# Confluence: Otherworld

- [English](README-EN_US.md)

| 项目名                                                                           | 备注                |
|-------------------------------------------------------------------------------|-------------------|
| [ConfluenceMagicLib](Confluence-Magic-Lib)                                    | 汇流系列开发库           |
| [ConfluenceOtherworld](ConfluenceOtherworld)                                  | 本体                |
| [TerraCurio](TerraCurio)                                                      | 泰拉饰品              |
| [ParticleStorm](https://github.com/westernat/ParticleStorm)                   | 复杂粒子API           |
| [ThreeDimensionParticle](https://github.com/westernat/ThreeDimensionParticle) | 3D粒子核心            |
| [TerraFurniture](TerraFurniture)                                              | 泰拉家具              |
| [TheTrackers](https://github.com/MakerTechno/TheTrackers)                     | 提供包括Boss实体的实时位置提示 |
| [DataTip](https://github.com/cooobird/DataTip)                                | 资源驱动物品Tooltip系统   |
| [OpalLight](https://github.com/westernat/OpalLight)                           | 彩色光照系统            |
| [PortLib](PortLib)                                                            | 反向移植库             |

| 官方附属                                                                                | 备注                 |
|-------------------------------------------------------------------------------------|--------------------|
| [ConfluenceDelight](https://github.com/cooobird/ConfluenceDelight)                  | 汇流乐事               |
| [ConfluenceMusic](https://github.com/westernat/Confluence-Music)                    | 汇流音乐               |
| [ConfluenceDimensionPatch](https://github.com/westernat/Confluence-Dimension-Patch) | 将汇流来世对主世界的修改转移到新维度 |
| [BetterExperience](https://github.com/Magic-team-jvav/BetterExperience)             | 经过泰拉模组原作者许可的移植版    |

## 构建项目

- 项目拉取完成后,确认子项目文件是否有拉取，如空文件夹请按下列指令在终端内顺序执行
  ~~~cmd
    git submodule init
    git submodule update
  ~~~

- 全部拉取完成后，在gradle插件中先启动`confluence [runData]`，然后启动`confluence [:ConfluenceOtherworld:runClient]`
