# TE 退役：datagen provider 侧键删除清单

> 由 `tools/port2native/te_lang_providers.py` 生成，收口 `f4d037ec4` 与
> `notes/TE-LANG-KEYS-REMOVED.md` 留下的 **provider 侧缺口**（provider 仍在吐
> `terra_entity.<id>`，不修源头 `runData` 会把那批成果回退）。
> 判定口径见 `tools/port2native/te_lang_providers.py` 文件头：`EXTRA_RENAME` 改名表与 `FAMILY` 族表
> 与 `te_lang_keys.py` 逐字一致；注册 id 抓取用「跨行 `register*(` 首个参数」口径，是 `registered_ids.py`
> 同行口径的**超集**——实测多出 `flying_fish`/`traveling_merchant`/`wandering_eye_fish`/`whip_attack`，
> 若按旧口径，前 3 个实体（都已注册）的 bestiary 键会被误删；`te_lang_keys.py` 那时就是这样把它们的键
> 从 de_de/es_es/lzh/pt_br 删掉的（见 `notes/TE-LANG-KEYS-REMOVED.md`，属该批缺陷，需另行恢复到语区）。

删除的都是**当前已失效**的键：其 id 不在主模组注册表中（TE 独有内容 / 未注册内容，
且 1.21 游戏查的是 `confluence.` 命名空间）。要恢复某条，按本节列出的键名从 git 历史里
取回原语句即可（本文件只记键名，与 `notes/TE-LANG-KEYS-REMOVED.md` 同格式）。

## common\data\gen\language\BestiaryLanguageSubProvider.java（10 个键 / 20 条语句）

- `bestiary.entity.terra_entity.crimson_slime.desc`
- `bestiary.entity.terra_entity.honey_slime.desc`
- `bestiary.entity.terra_entity.hornet_fatty.desc`
- `bestiary.entity.terra_entity.hornet_honey.desc`
- `bestiary.entity.terra_entity.hornet_leafy.desc`
- `bestiary.entity.terra_entity.hornet_spikey.desc`
- `bestiary.entity.terra_entity.hornet_stingy.desc`
- `bestiary.entity.terra_entity.the_hungry_attached.desc`
- `bestiary.entity.terra_entity.the_hungry_flying.desc`
- `bestiary.entity.terra_entity.wandering_eye.desc`

## 附：定点键修正 `KEY_FIX`（不涉删除）

> 批 11 把图鉴里的 `FEALING` 条目改回 1.20 的朴素形态（原生 `Fealing` 没有 `Variant` 枚举、
> 不是 `VariantHolder`），因此它的键要去掉 `.0` 序号。1.20 权威即如此：
> `BestiaryLanguageSubProvider:242/1031` → `bestiary.entity.confluence.fealing.desc`，
> `ModEnglishProvider:1639` → `add("entity.confluence.fealing", "Flying Spirit")`
> （1.20 的 `ModChineseProvider:5148` 走 `add(CritterEntities.FEALING.get(), …)` 重载，等价键同形）。

| 文件 | 行 | 改前 | 改后 |
|---|---|---|---|
| `common\data\gen\language\BestiaryLanguageSubProvider.java` | 241 | `bestiary.entity.confluence.fealing.0.desc` | `bestiary.entity.confluence.fealing.desc` |
| `common\data\gen\language\BestiaryLanguageSubProvider.java` | 1015 | `bestiary.entity.confluence.fealing.0.desc` | `bestiary.entity.confluence.fealing.desc` |
| `common\data\gen\ModChineseProvider.java` | 1551 | `entity.confluence.fealing.0` | `entity.confluence.fealing` |
| `common\data\gen\ModEnglishProvider.java` | 1516 | `entity.confluence.fealing.0` | `entity.confluence.fealing` |

> ⚠️ 语区侧遗留（不在本批次文件清单内）：`bestiary.entity.confluence.fealing.desc` 目前只有 `lzh.json` 有；
> `de_de.json`/`es_es.json`/`pt_br.json` 仍只有旧形 `…fealing.0.desc`（provider 已不再生成该键），
> 需要把这 3 个语区的键改名成 `…fealing.desc` 才与图鉴条目对上。`entity.confluence.fealing`
> 在 `es_es`/`lzh`/`pt_br` 已有，`de_de`/`ru_ru` 没有。

---

## 附：注释语句（`// add(...)`）的处理

> 注释语句**不生成任何键**，本批次的动机（防 `runData` 回退）对它们不成立，
> 因此只把命名空间段改成 `confluence.`、**一条都没删**。依据：本文件是 1.20 权威文件把
> `confluence.` 全局换成 `terra_entity.` 的产物，1.20 权威自己就把这些尚未实现的内容
> 以注释停车（`// add("bestiary.entity.confluence.<id>.desc", ...)`，连行号都对齐），
> 删掉它们等于删掉权威保留的停车内容。下表列出「注释里 id 未注册」的键——
> 若日后要按字面口径删除，`te_lang_providers.py --apply --drop-comments` 一条命令即可。

### common\data\gen\language\BestiaryLanguageSubProvider.java（233 个键 / 463 条语句，仅改命名空间）

- `bestiary.entity.terra_entity.alien_hornet.desc`
- `bestiary.entity.terra_entity.alien_larva.desc`
- `bestiary.entity.terra_entity.alien_queen.desc`
- `bestiary.entity.terra_entity.ancient_vision.desc`
- `bestiary.entity.terra_entity.angry_trapper.desc`
- `bestiary.entity.terra_entity.anomura_fungus.desc`
- `bestiary.entity.terra_entity.baby_mothron.desc`
- `bestiary.entity.terra_entity.bee_larger.desc`
- `bestiary.entity.terra_entity.betsy.desc`
- `bestiary.entity.terra_entity.blood_eel.desc`
- `bestiary.entity.terra_entity.blood_squid.desc`
- `bestiary.entity.terra_entity.blue_armored_bones.desc`
- `bestiary.entity.terra_entity.blue_armored_bones_mace.desc`
- `bestiary.entity.terra_entity.blue_armored_bones_no_pants.desc`
- `bestiary.entity.terra_entity.blue_armored_bones_sword.desc`
- `bestiary.entity.terra_entity.blue_cultist_archer.desc`
- `bestiary.entity.terra_entity.blue_macaw.desc`
- `bestiary.entity.terra_entity.bouncy_slime.desc`
- `bestiary.entity.terra_entity.brain_scrambler.desc`
- `bestiary.entity.terra_entity.brain_suckler.desc`
- `bestiary.entity.terra_entity.bunny_slime.desc`
- `bestiary.entity.terra_entity.bunny_with_a_hat.desc`
- `bestiary.entity.terra_entity.bunny_xmas.desc`
- `bestiary.entity.terra_entity.butcher.desc`
- `bestiary.entity.terra_entity.chattering_teeth_bomb.desc`
- `bestiary.entity.terra_entity.clown.desc`
- `bestiary.entity.terra_entity.cochineal_beetle.desc`
- `bestiary.entity.terra_entity.corite.desc`
- `bestiary.entity.terra_entity.corrupt_bunny.desc`
- `bestiary.entity.terra_entity.crawltipede.desc`
- `bestiary.entity.terra_entity.creature_from_the_deep.desc`
- `bestiary.entity.terra_entity.crimson_axe.desc`
- `bestiary.entity.terra_entity.crystal_slime.desc`
- `bestiary.entity.terra_entity.cursed_hammer.desc`
- `bestiary.entity.terra_entity.cyan_beetle.desc`
- `bestiary.entity.terra_entity.dark_mage.desc`
- `bestiary.entity.terra_entity.deadly_sphere.desc`
- `bestiary.entity.terra_entity.diabolist_red.desc`
- `bestiary.entity.terra_entity.diabolist_white.desc`
- `bestiary.entity.terra_entity.dr_man_fly.desc`
- `bestiary.entity.terra_entity.drakanian.desc`
- `bestiary.entity.terra_entity.drakin.desc`
- `bestiary.entity.terra_entity.drakomire.desc`
- `bestiary.entity.terra_entity.drakomire_rider.desc`
- `bestiary.entity.terra_entity.dreadnautilus.desc`
- `bestiary.entity.terra_entity.duke_fishron.desc`
- `bestiary.entity.terra_entity.dune_splicer.desc`
- `bestiary.entity.terra_entity.elf_archer.desc`
- `bestiary.entity.terra_entity.elf_copter.desc`
- `bestiary.entity.terra_entity.empress_of_light.desc`
- `bestiary.entity.terra_entity.etherian_goblin.desc`
- `bestiary.entity.terra_entity.etherian_goblin_bomber.desc`
- `bestiary.entity.terra_entity.etherian_javelin_thrower.desc`
- `bestiary.entity.terra_entity.etherian_lightning_bug.desc`
- `bestiary.entity.terra_entity.etherian_wyvern.desc`
- `bestiary.entity.terra_entity.everscream.desc`
- `bestiary.entity.terra_entity.evolution_beast.desc`
- `bestiary.entity.terra_entity.eyezor.desc`
- `bestiary.entity.terra_entity.floaty_gross.desc`
- `bestiary.entity.terra_entity.flocko.desc`
- `bestiary.entity.terra_entity.flow_invader.desc`
- `bestiary.entity.terra_entity.flying_dutchman.desc`
- `bestiary.entity.terra_entity.frankenstein.desc`
- `bestiary.entity.terra_entity.fritz.desc`
- `bestiary.entity.terra_entity.giant_antlion_charger.desc`
- `bestiary.entity.terra_entity.giant_cursed_skull.desc`
- `bestiary.entity.terra_entity.gigazapper.desc`
- `bestiary.entity.terra_entity.gingerbread_man.desc`
- `bestiary.entity.terra_entity.gold_bird.desc`
- `bestiary.entity.terra_entity.gold_frog.desc`
- `bestiary.entity.terra_entity.gold_goldfish.desc`
- `bestiary.entity.terra_entity.gold_mouse.desc`
- `bestiary.entity.terra_entity.gold_seahorse.desc`
- `bestiary.entity.terra_entity.gold_water_strider.desc`
- `bestiary.entity.terra_entity.golem.desc`
- `bestiary.entity.terra_entity.gray_cockatiel.desc`
- `bestiary.entity.terra_entity.gray_grunt.desc`
- `bestiary.entity.terra_entity.grebe.desc`
- `bestiary.entity.terra_entity.headless_horseman.desc`
- `bestiary.entity.terra_entity.heavenly_slime.desc`
- `bestiary.entity.terra_entity.hell_armored_bones.desc`
- `bestiary.entity.terra_entity.hell_armored_bones_mace.desc`
- `bestiary.entity.terra_entity.hell_armored_bones_spike_shield.desc`
- `bestiary.entity.terra_entity.hell_armored_bones_sword.desc`
- `bestiary.entity.terra_entity.hellhound.desc`
- `bestiary.entity.terra_entity.hemogoblin_shark.desc`
- `bestiary.entity.terra_entity.hoppin_jack.desc`
- `bestiary.entity.terra_entity.ice_queen.desc`
- `bestiary.entity.terra_entity.ichor_sticker.desc`
- `bestiary.entity.terra_entity.jungle_turtle.desc`
- `bestiary.entity.terra_entity.kobold.desc`
- `bestiary.entity.terra_entity.kobold_glider.desc`
- `bestiary.entity.terra_entity.krampus.desc`
- `bestiary.entity.terra_entity.lac_beetle.desc`
- `bestiary.entity.terra_entity.lavafly.desc`
- `bestiary.entity.terra_entity.lost_girl.desc`
- `bestiary.entity.terra_entity.lunatic_devotee.desc`
- `bestiary.entity.terra_entity.maggot_zombie.desc`
- `bestiary.entity.terra_entity.martian_drone.desc`
- `bestiary.entity.terra_entity.martian_engineer.desc`
- `bestiary.entity.terra_entity.martian_officer.desc`
- `bestiary.entity.terra_entity.martian_saucer.desc`
- `bestiary.entity.terra_entity.martian_walker.desc`
- `bestiary.entity.terra_entity.medusa.desc`
- `bestiary.entity.terra_entity.milkyway_weaver.desc`
- `bestiary.entity.terra_entity.mini_star_cell.desc`
- `bestiary.entity.terra_entity.mister_stabby.desc`
- `bestiary.entity.terra_entity.moon_lord.desc`
- `bestiary.entity.terra_entity.moth.desc`
- `bestiary.entity.terra_entity.mothron.desc`
- `bestiary.entity.terra_entity.mourning_wood.desc`
- `bestiary.entity.terra_entity.mouse.desc`
- `bestiary.entity.terra_entity.mushi_ladybug.desc`
- `bestiary.entity.terra_entity.nailhead.desc`
- `bestiary.entity.terra_entity.nebula_floater.desc`
- `bestiary.entity.terra_entity.nebula_pillar.desc`
- `bestiary.entity.terra_entity.necromancer_armored.desc`
- `bestiary.entity.terra_entity.nutcracker.desc`
- `bestiary.entity.terra_entity.ogre.desc`
- `bestiary.entity.terra_entity.old_ones_skeleton.desc`
- `bestiary.entity.terra_entity.owl.desc`
- `bestiary.entity.terra_entity.parrot.desc`
- `bestiary.entity.terra_entity.penguin_black.desc`
- `bestiary.entity.terra_entity.pigron.desc`
- `bestiary.entity.terra_entity.pigron_corrupt.desc`
- `bestiary.entity.terra_entity.pigron_crimson.desc`
- `bestiary.entity.terra_entity.pirate.desc`
- `bestiary.entity.terra_entity.poltergeist.desc`
- `bestiary.entity.terra_entity.predictor.desc`
- `bestiary.entity.terra_entity.present_mimic.desc`
- `bestiary.entity.terra_entity.princess.desc`
- `bestiary.entity.terra_entity.probe.desc`
- `bestiary.entity.terra_entity.psycho.desc`
- `bestiary.entity.terra_entity.pumpking.desc`
- `bestiary.entity.terra_entity.pupfish.desc`
- `bestiary.entity.terra_entity.queen_slime.desc`
- `bestiary.entity.terra_entity.ragged_caster_open_coat.desc`
- `bestiary.entity.terra_entity.rainbow_slime.desc`
- `bestiary.entity.terra_entity.rat.desc`
- `bestiary.entity.terra_entity.raven.desc`
- `bestiary.entity.terra_entity.ray_gunner.desc`
- `bestiary.entity.terra_entity.reaper.desc`
- `bestiary.entity.terra_entity.rusty_armored_bones_axe.desc`
- `bestiary.entity.terra_entity.rusty_armored_bones_flail.desc`
- `bestiary.entity.terra_entity.rusty_armored_bones_sword.desc`
- `bestiary.entity.terra_entity.rusty_armored_bones_sword_no_armor.desc`
- `bestiary.entity.terra_entity.salamander.desc`
- `bestiary.entity.terra_entity.sand_elemental.desc`
- `bestiary.entity.terra_entity.santa_claus.desc`
- `bestiary.entity.terra_entity.santa_nk1.desc`
- `bestiary.entity.terra_entity.scarecrow_cloth_face.desc`
- `bestiary.entity.terra_entity.scarecrow_cloth_face_stick.desc`
- `bestiary.entity.terra_entity.scarecrow_cloth_hat.desc`
- `bestiary.entity.terra_entity.scarecrow_cloth_hat_stick.desc`
- `bestiary.entity.terra_entity.scarecrow_guy_fawkes.desc`
- `bestiary.entity.terra_entity.scarecrow_guy_fawkes_stick.desc`
- `bestiary.entity.terra_entity.scarecrow_pumpkin_hat.desc`
- `bestiary.entity.terra_entity.scarecrow_pumpkin_hat_stick.desc`
- `bestiary.entity.terra_entity.scarecrow_pumpkin_head.desc`
- `bestiary.entity.terra_entity.scarecrow_pumpkin_head_stick.desc`
- `bestiary.entity.terra_entity.scarlet_macaw.desc`
- `bestiary.entity.terra_entity.scutlix.desc`
- `bestiary.entity.terra_entity.scutlix_gunner.desc`
- `bestiary.entity.terra_entity.sea_snail.desc`
- `bestiary.entity.terra_entity.sea_turtle.desc`
- `bestiary.entity.terra_entity.seagull.desc`
- `bestiary.entity.terra_entity.seahorse.desc`
- `bestiary.entity.terra_entity.selenian.desc`
- `bestiary.entity.terra_entity.sharkron.desc`
- `bestiary.entity.terra_entity.shimmer_slime.desc`
- `bestiary.entity.terra_entity.skeleton_alien.desc`
- `bestiary.entity.terra_entity.skeleton_archer.desc`
- `bestiary.entity.terra_entity.skeleton_astronaut.desc`
- `bestiary.entity.terra_entity.skeleton_commando.desc`
- `bestiary.entity.terra_entity.skeleton_headache.desc`
- `bestiary.entity.terra_entity.skeleton_misassembled.desc`
- `bestiary.entity.terra_entity.skeleton_pantless.desc`
- `bestiary.entity.terra_entity.skeleton_sniper.desc`
- `bestiary.entity.terra_entity.skeleton_top_hat.desc`
- `bestiary.entity.terra_entity.slime_bunny_mask.desc`
- `bestiary.entity.terra_entity.slime_green_present_slime.desc`
- `bestiary.entity.terra_entity.slime_red_present_slime.desc`
- `bestiary.entity.terra_entity.slime_white_present_slime.desc`
- `bestiary.entity.terra_entity.slime_yellow_present_slime.desc`
- `bestiary.entity.terra_entity.snow_balla.desc`
- `bestiary.entity.terra_entity.snowman_gangsta.desc`
- `bestiary.entity.terra_entity.solar_pillar.desc`
- `bestiary.entity.terra_entity.splinterling.desc`
- `bestiary.entity.terra_entity.squid.desc`
- `bestiary.entity.terra_entity.sroller.desc`
- `bestiary.entity.terra_entity.star_cell.desc`
- `bestiary.entity.terra_entity.stardust_pillar.desc`
- `bestiary.entity.terra_entity.stargazer.desc`
- `bestiary.entity.terra_entity.storm_diver.desc`
- `bestiary.entity.terra_entity.swamp_thing.desc`
- `bestiary.entity.terra_entity.tactical_skeleton.desc`
- `bestiary.entity.terra_entity.tavernkeep.desc`
- `bestiary.entity.terra_entity.tesla_turret.desc`
- `bestiary.entity.terra_entity.the_possessed.desc`
- `bestiary.entity.terra_entity.the_torch_god.desc`
- `bestiary.entity.terra_entity.tortured_soul.desc`
- `bestiary.entity.terra_entity.toucan.desc`
- `bestiary.entity.terra_entity.town_bunny.desc`
- `bestiary.entity.terra_entity.town_cat.desc`
- `bestiary.entity.terra_entity.town_dog.desc`
- `bestiary.entity.terra_entity.toxic_sludge.desc`
- `bestiary.entity.terra_entity.turtle.desc`
- `bestiary.entity.terra_entity.twinkle_popper.desc`
- `bestiary.entity.terra_entity.umbrella_slime.desc`
- `bestiary.entity.terra_entity.vampire.desc`
- `bestiary.entity.terra_entity.vicious_bunny.desc`
- `bestiary.entity.terra_entity.vortex_pillar.desc`
- `bestiary.entity.terra_entity.vortexian.desc`
- `bestiary.entity.terra_entity.vulture.desc`
- `bestiary.entity.terra_entity.water_strider.desc`
- `bestiary.entity.terra_entity.wither_beast.desc`
- `bestiary.entity.terra_entity.wolf.desc`
- `bestiary.entity.terra_entity.yellow_cockatiel.desc`
- `bestiary.entity.terra_entity.yeti.desc`
- `bestiary.entity.terra_entity.zombie_bald.desc`
- `bestiary.entity.terra_entity.zombie_doctor.desc`
- `bestiary.entity.terra_entity.zombie_elf.desc`
- `bestiary.entity.terra_entity.zombie_elf_beard.desc`
- `bestiary.entity.terra_entity.zombie_elf_girl.desc`
- `bestiary.entity.terra_entity.zombie_female.desc`
- `bestiary.entity.terra_entity.zombie_pincushion.desc`
- `bestiary.entity.terra_entity.zombie_pixie.desc`
- `bestiary.entity.terra_entity.zombie_superman.desc`
- `bestiary.entity.terra_entity.zombie_swamp.desc`
- `bestiary.entity.terra_entity.zombie_sweater.desc`
- `bestiary.entity.terra_entity.zombie_torch.desc`
- `bestiary.entity.terra_entity.zombie_twiggy.desc`
- `bestiary.entity.terra_entity.zombie_xmas.desc`
