For the Acceosseries part, We've created a Data-Driven Curio generator, which simply generate an interface-based item.

## Available Interfaces
>### General(both available for master project and curio only)
>- AutoAttack: Accpet any
>- FallResistance: Accept an integer in range of [-1, 2147483647], '-1' means canceling fall damage
>- FireAttack: Accept any
>- FireImmune: Accept any
>- Honeycomb: Accept any
>- InvulnerableTime: Accept an integer in range of [0, 2147483647]
>- LavaHurtReduce: Accept any
>- StarCloak: Accept any
>- JumpBoost: Accept a number in range of [0.0, 1.7976931348623157E308]
>- MayFly: Accept an array like [flyTicks, flySpeed]
>    - flyTicks: An integer in range of [0, 2147483647]
>    - flySpeed: A number in range of [0.0, 1.7976931348623157E308]
>- LavaImmune: Accept any
>- MagicQuiver: Accept any
>- Scope: Accept any
>- WallClimb: Accept any

>### Master Project
>- FishingPower: Accept a number in range of [0.0, 1.7976931348623157E308]
>- HighTestFishingLine: Accept any
>- LavaproofFishingHook: Accept any
>- TackleBox: Accept any
>- AutoGetMana: Accept any
>- ManaReduce: Accept a number as ratio in range of [0.0, 1.0], value `0.1` will reduce mana consume by 10%;

## Available Rarities
- GRAY
- WHITE
- BLUE
- GREEN
- ORANGE
- LIGHT_RED
- PINK
- LIGHT_PURPLE
- LIME
- YELLOW
- CYAN
- RED
- PURPLE
- EXPERT
- MASTER

It's optional, defaults to `WHITE`

## Tooltips
The `tooltips` is an array of string, which will show in hover text

Such as `Hi from Confluence Team` or `item.confluence.data_driven_test.tooltip`

It's optional, defaults to `[]`

## Slot
Accept a string, which is available for Curios API

It's optional, defaults to `curio`

## Attributes
Accept an object, which defining all attributes the curio requires

> The `CriticalChance` has been reproduced to attribute since 0.1.5, the id is `confluence:crit_chance`
>
> If you installed `Apothic Attributes`, please replace the id to `attributeslib:crit_chance`
>
> Removed `ProjectileAttack`, `AggroAttach`, `ArmorPass`, `HurtEvasion`, `MagicAttack` from Data-Driven because of replaceable attributes, since 0.1.7

>### Available Operations
>- ADDITION
>- MULTIPLY_BASE
>- MULTIPLY_TOTAL

It's optional, defaults to `{}`

## Example
```json
{
    "data_driven_test": {
        "rarity": "MASTER",
        "tooltips": [
            "It's a Data Driven Curio",
            "Hi from Confluence Team!"
        ],
        "slot": "accessory",
        "attributes": {
            "minecraft:generic.movement_speed": {
                "uuid": "DC2CE9B0-2637-329F-2E1F-998F1A8FA5A1",
                "name": "Data Driven",
                "value": 0.05,
                "operation": "MULTIPLY_TOTAL"
            }
        },
        "interfaces": {
            "AutoAttack": {},
            "FallResistance": -1,
            "MayFly": [32, 0.3]
        }
    }
}
```

## How can I create more than one curios?
```json
{
    "curio_1": {
        ......
    },
    "curio_2": {
        ......
    },
    "curio_3": {
        ......
    },
}
```

## Where should I put my own curio?
It's relative to your game folder on `./config/confluence/curio.json`

## Do I have to create a tag for it?
If you forget about it, the generator will automatically create one

But you should create all assets for it, such as texture, language key, item model