For the Acceosseries part, We've created a Data-Driven Curio generator, which simply generate an interface-based item.

## Available Interfaces
- AggroAttach: Accept an integer in range of [-2147483648, 2147483647]
- ArmorPass: Accept an integer in range of [0, 2147483647]
- AutoAttack: Accpet any
- CriticalHit: Accept a number as `chance` in range of [0.0, 1.0]
- FireAttack: Accept any
- FireImmune: Accept any
- Honeycomb: Accept any
- HurtEvasion: Accept any
- InvulnerableTime: Accept an integer in range of [0, 2147483647]
- LavaHurtReduce: Accept any
- MagicAttack: Accept a number as `attackBonus` in range of [0.0, 1.7976931348623157E308]
- ProjectileAttack: Accept a number as `attackBonus` in range of [0.0, 1.7976931348623157E308]
- StarCloak: Accept any
- BreakSpeedBonus: Accept a number in range of [0.0, 1.7976931348623157E308]
- FallResistance: Accept an integer in range of [-1, 2147483647], '-1' means canceling fall damage
- JumpBoost: Accept a number in range of [0.0, 1.7976931348623157E308]
- MayFly: Accept an array like [flyTicks, flySpeed]
 - flyTicks: An integer in range of [0, 2147483647]
 - flySpeed: A number in range of [0.0, 1.7976931348623157E308]
- MultiJump: Accept a number as `jumpSpeed` in range of [0.0, 1.7976931348623157E308], could not use with `OneTimeJump`
- OneTimeJump: Accept an array like [jumpTicks, jumpSpeed], could not use with `MultiJump`
 - jumpTicks: An integer in range of [0, 2147483647]
 - jumpSpeed: A number in range of [0.0, 1.7976931348623157E308]
- LavaImmune: Accept any
- MagicQuiver: Accept any
- Scope: Accept any

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

## Tooltips
The `tooltips` is an array of string, which will show in hover text

Such as `Hi from Confluence Team` or `item.confluence.data_driven_test.tooltip`

## Example
```json
{
    "data_driven_test": {
        "rarity": "MASTER",
        "tooltips": ["It's a Data Driven item", "Hi from Confluence Team!"],
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