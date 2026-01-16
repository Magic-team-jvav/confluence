# Biomes

| Terraria    | Minecraft                    |
|-------------|------------------------------|
| forest      | #confluence:is_forest        |
| snow        | #c:is_snowy, #c:is_icy       |
| desert      | #c:is_desert, #c:is_badlands |
| jungle      | #c:is_jungle, #c:is_lush     |
| ocean       | #c:is_ocean                  |
| underworld  | #c:is_nether                 |
| space       | y>260                        |
| surface     | 260>y>40                     |
| underground | 40>y>0                       |
| cave        | 0>y                          |

# Events

| Terraria | Minecraft          |
|----------|--------------------|
| rain     | rain, thunder rain |
| blizzard | snow, thunder snow |

# Metrology

|                   | Terraria           | Minecraft |
|-------------------|--------------------|-----------|
| 1 meter           | 1.5 tile(12 pixie) | 1 block   |
| 1 second          | 60 tick            | 20 tick   |
| 1 day             | 24 minute          | 20 minute |
| 1 health (player) | 5 health           | 1 health  |

# Strength

|           | Terraria (expert) | Minecraft(Initial Value) | Minecraft(Classic) | Minecraft(Master)  |
|-----------|-------------------|--------------------------|--------------------|--------------------|
| Health    | 100               | 26                       | 17                 | 39                 |
| Damage    | 100               | 26                       | 17                 | 39                 |
| Defense   | 8                 | 8                        | 8                  | 8                  |
| Knockback | 50%               | 0.5(50%)                 | 0.5(50%)           | 0.5(50%)           |
| Coins     | Fixed Value       | Money Drop Formula       | Money Drop Formula | Money Drop Formula |

## Money Drop Formula

- Final Money Drop = min( round( ( (MaxHealth×0.15) + (AttackDmg×0.25) + (Armor×0.1) + (
  KnockbackRes×10+10) ) × (EffectiveDifficulty×0.5) ) × 7 , 100000 )

### Sword

- (terraria Damage / 2) + 2

### Mana Weapon

- (terraria Damage / 2) + 5

### Lance

- (terraria Damage / 2)
- (terraria KnckBack / 2)

### Gun

- (terraria Time / 3)
- (terraria Damage / 2) (then properly lower by a range of 0.5f to 1f)
- (terraria Speed / 8)
- (terraria KnockBack / 20)

### Boomerang

- (terraria Damage 40%)

# Distance

|       | Terraria | Minecraft |
|-------|----------|-----------|
| Block | 2        | 3         |
