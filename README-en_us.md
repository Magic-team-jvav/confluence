- [中文](README.md)

| Project Name                                 | Description                                        | Runs Independently             |
|----------------------------------------------|----------------------------------------------------|--------------------------------|
| [ConfluenceOtherworld](ConfluenceOtherworld) | Main module                                        | Depends on all                 |
| [TerraEntity](TerraEntity)                   | Terra's Entities                                   | yes                            |
| [MineTeam](MineTeam)                         | Terra's Teams                                      | yes                            |
| [TerraCurio](TerraCurio)                     | Terra's Wearables                                  | yes                            |
| [TerraGuns](TerraGuns)                       | Terra's Guns                                       | yes                            |
| [ParticleStorm](ParticleStorm)               | Particles API                                      | yes                            |
| [HeavenDestinyMoment](HeavenDestinyMoment)   | Event API                                          | yes                            |
| [TerraMoment](TerraMoment)                   | Terra's Events                                     | depends on HeavenDestinyMoment |
| [IsekaiInvaded](IsekaiInvaded)               | Dimensional Invasion Event                         | depends on HeavenDestinyMoment |
| [TerraFurniture](TerraFurniture)             | Terra's Furniture                                  | yes                            |
| [TheTrackers](TheTrackers)                   | Provide real-time location hints for Boss entities | Yes                            |

| Official Integrations                                                               | Description                                                                            |
|-------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------|
| [ConfluenceDelight](https://github.com/cooobird/ConfluenceDelight)                  | Farmer's Delight integration                                                           |
| [ConfluenceMusic](https://github.com/westernat/Confluence-Music)                    | Extra music                                                                            |
| [ConfluenceDimensionPatch](https://github.com/westernat/Confluence-Dimension-Patch) | Transfer the Confluence Otherworld's modifications to the Overworld to a new dimension |
| [BetterExperience](https://github.com/EDGtheXu/BetterExperience)                    | The ported version authorized by the original creator of the Terra mod.                |

## Build the Project

- When you pull the project, confirm whether the sub-project files are pulled. If the folder is empty, you can run the
  following sequence of terminal commands:
  ~~~cmd
    git submodule init
    git submodule update
  ~~~

- If you would like to manually add a submodule, you can run:
  ~~~cmd
    git submodule add -b "<branch name>" "<url>"
  ~~~
  > You will also need to add the submodule name in [settings.gradle](settings.gradle)
  > and [ConfluenceOtherworld/build.gradle](ConfluenceOtherworld/build.gradle).

- After all the project is pulled successuly, you can use gradle's runClient command.
