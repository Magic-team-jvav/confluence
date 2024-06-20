For the Acceosseries part, We've created a Data-Driven Curio generator, which simply generate an interface-based item.

## Available Interfaces
>### General(both available for master project and curio only)
>- AggroAttach: Accept an integer in range of [-2147483648, 2147483647]
>- ArmorPass: Accept an integer in range of [0, 2147483647]
>- AutoAttack: Accpet any
>- CriticalHit: Accept a number as `chance` in range of [0.0, 1.0]
>- FireAttack: Accept any
>- FireImmune: Accept any
>- Honeycomb: Accept any
>- HurtEvasion: Accept any
>- InvulnerableTime: Accept an integer in range of [0, 2147483647]
>- LavaHurtReduce: Accept any
>- MagicAttack: Accept a number as `attackBonus` in range of [0.0, 1.7976931348623157E308]
>- ProjectileAttack: Accept a number as `attackBonus` in range of [0.0, 1.7976931348623157E308]
>- StarCloak: Accept any
>- BreakSpeedBonus: Accept a number in range of [0.0, 1.7976931348623157E308]
>- FallResistance: Accept an integer in range of [-1, 2147483647], '-1' means canceling fall damage
>- JumpBoost: Accept a number in range of [0.0, 1.7976931348623157E308]
>- MayFly: Accept an array like [flyTicks, flySpeed]
>    - flyTicks: An integer in range of [0, 2147483647]
>    - flySpeed: A number in range of [0.0, 1.7976931348623157E308]
>- MultiJump: Accept a number as `jumpSpeed` in range of [0.0, 1.7976931348623157E308], could not use with `OneTimeJump`
>- OneTimeJump: Accept an array like [jumpTicks, jumpSpeed], could not use with `MultiJump`
>    - jumpTicks: An integer in range of [0, 2147483647]
>    - jumpSpeed: A number in range of [0.0, 1.7976931348623157E308]
>- LavaImmune: Accept any
>- MagicQuiver: Accept any
>- Scope: Accept any

>### Master Project
>- FishingPower: Accept a number in range of [0.0, 1.7976931348623157E308]
>- HighTestFishingLine: Accept any
>- LavaproofFishingHook: Accept any
>- TackleBox: Accept any
>- AutoGetMana: Accept any
>- ManaReduce: Accept a number as ratio in range of [0.0, 1.0], value `0.1` will reduce mana consume by 10%;

## Available Rarities
It's optional, defaults to `WHITE`
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

## Tooltips
The `tooltips` is an array of string, which will show in hover text

Such as `Hi from Confluence Team` or `item.confluence.data_driven_test.tooltip`

It's optional, defaults to []

## Slot
Accept a string, which is available for Curios API

It's optional, defaults to `curio`

## Attributes
Accept an object, which defining all attributes the curio requires

>### Available Operations
>- ADDITION
>- MULTIPLY_BASE
>- MULTIPLY_TOTAL

It's optional, defaults to {}

## Example
```json
{
    "data_driven_test": {
        "rarity": "MASTER",
        "tooltips": [
            "It's a Data Driven curio",
            "Hi from Confluence Team!"
        ],
        "slot": "curio",
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

## Where should I put my own curio?
It's relative to your game folder on `./config/confluence/curio.json`

## Do I have to create a tag for it?
If you forget about it, the generator will automatically create one

But you should create all assets for it, such as texture, language key, item model