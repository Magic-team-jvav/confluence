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
- OneTimeJump: Accept an array like [jumpTicks, jumpSpeed]
 - jumpTicks: An integer in range of [0, 2147483647]
 - jumpSpeed: A number in range of [0.0, 1.7976931348623157E308]
- LavaImmune: Accept any
- MagicQuiver: Accept any
- Scope: Accept any