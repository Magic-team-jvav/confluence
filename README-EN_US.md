# Confluence: Otherworld

- [中文](README.md)

| Project Name                                                                  | Description                                        |
|-------------------------------------------------------------------------------|----------------------------------------------------|
| [ConfluenceMagicLib](Confluence-Magic-Lib)                                    | Confluence series development library              |
| [ConfluenceOtherworld](ConfluenceOtherworld)                                  | Main module                                        |
| [TerraCurio](TerraCurio)                                                      | Accessories part                                   |
| [ParticleStorm](https://github.com/westernat/ParticleStorm)                   | Complex particles API                              |
| [ThreeDimensionParticle](https://github.com/westernat/ThreeDimensionParticle) | 3D Particle core                                   |
| [TerraFurniture](TerraFurniture)                                              | Terra's Furniture                                  |
| [TheTrackers](TheTrackers)                                                    | Provide real-time location hints for Boss entities |
| [DataTip](https://github.com/cooobird/DataTip)                                | Assets-Driven item tooltip system                  |
| [OpalLight](https://github.com/westernat/OpalLight)                           | Colorful lighting system                           |
| [PortLib](PortLib)                                                            | backports library                                  |

| Official Integrations                                                               | Description                                                                            |
|-------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------|
| [ConfluenceDelight](https://github.com/cooobird/ConfluenceDelight)                  | Farmer's Delight integration                                                           |
| [ConfluenceMusic](https://github.com/westernat/Confluence-Music)                    | Official music mod                                                                     |
| [ConfluenceDimensionPatch](https://github.com/westernat/Confluence-Dimension-Patch) | Transfer the Confluence Otherworld's modifications to the Overworld to a new dimension |
| [BetterExperience](https://github.com/Magic-team-jvav/BetterExperience)             | The ported version authorized by the original creator of the Terra mod.                |

## Build the Project

- When you pull the project, confirm whether the sub-project files are pulled. If the folder is
  empty, you can run the
  following sequence of terminal commands:
  ~~~cmd
    git submodule init
    git submodule update
  ~~~

- After all the project is pulled successuly, boost `confluence [runData]` then `confluence [:ConfluenceOtherworld:runClient]` in gradle.
