# -*- coding: utf-8 -*-
"""TE 退役 · 语言键批次。

1) 两个 datagen provider：删除 34×2 条 `terra_entity.subtitle.*`（全仓无人引用，TE 时代的死键）
2) 5 个已入库语区：`...terra_entity.<id>...` 键里，id 在主模组同名注册表存在 → 1:1 平移成 `confluence.`；
   否则删除（这些键在当前 1.21 里本就是死的，游戏查的是 confluence 命名空间）
3) 被删除的键全部写入 notes/TE-LANG-KEYS-REMOVED.md 留档
"""
import io
import json
import os
import re
import sys
from collections import Counter

REPO = os.path.abspath(".")
GEN = os.path.join(REPO, "ConfluenceOtherworld", "src", "main", "java", "org", "confluence", "mod", "common", "data", "gen")
LANG = os.path.join(REPO, "ConfluenceOtherworld", "src", "main", "resources", "assets", "confluence", "lang")
NOTES = os.path.join(REPO, "notes")

REG_FILES = {
    "entity": ["common/init/entity/MonsterEntities.java", "common/init/entity/CritterEntities.java",
               "common/init/entity/NpcEntities.java", "common/init/entity/BossEntities.java",
               "common/init/ModEntities.java"],
    "item": ["common/init/item/ModItems.java", "common/init/item/MaterialItems.java",
             "common/init/item/BlockItems.java", "common/init/item/FoodItems.java",
             "common/init/item/ArmorItems.java", "common/init/item/VanityArmorItems.java",
             "common/init/item/ToolItems.java", "common/init/item/SwordItems.java",
             "common/init/item/BowItems.java", "common/init/item/ArrowItems.java",
             "common/init/item/BoomerangItems.java", "common/init/item/YoyoItems.java",
             "common/init/item/WhipItems.java", "common/init/item/FlailItems.java",
             "common/init/item/SpearItems.java", "common/init/item/ManaWeaponItems.java",
             "common/init/item/GunItems.java", "common/init/item/SummonItems.java",
             "common/init/item/PetItems.java", "common/init/item/SpawnEggItems.java"],
    "block": ["common/init/block/ModBlocks.java", "common/init/block/NatureBlocks.java",
              "common/init/block/TorchBlocks.java", "common/init/block/StatueBlocks.java"],
    "effect": ["common/init/ModEffects.java"],
}

# TE 时代 id → 主模组 id 的改名映射（1.20 权威命名）。只收语义明确、且目标在注册表中真实存在的；
# 目标不存在时该条被忽略（自动退回"删除"），避免把译文挂到错误的实体上。
# 来源：`notes/TE-LANG-KEYS-REMOVED.md` 的相似度筛查 + 人工确认。
EXTRA_RENAME = {
    ("entity", "angry_bones"): "anger_bones",
    ("entity", "faeling"): "fealing",
    ("entity", "hellbat"): "hell_bat",
    ("entity", "possessed_armor"): "possess_armor",
    ("entity", "corrupted_slime"): "corrupt_slime",
    ("entity", "dark_caster_proj"): "dark_caster_projectile",
    ("entity", "demon_scythe_proj"): "demon_scythe",
    ("entity", "harpy_feature"): "harpy_feather",
    ("entity", "skeletron_prime_part"): "skeletron_prime_arm",
    ("entity", "the_destroyer_laser"): "destroyer_laser",
    ("item", "bei_dou_boomerang"): "beidou_boomerang",
    ("item", "hornet_staff"): "new_hornet_staff",
}

# 键前缀 → 候选注册表（按顺序任一命中即可平移成 confluence.）
# 漏映射的族会被整族当死键删除——实测踩过：dialogs./mood. 是按 NPC id 命名的，第一版漏了，
# 一次误删 811 条可复活键。新增族时必须先 dry-run 看清分布。
FAMILY = [
    ("bestiary.entity.", ["entity"]),
    ("entity.", ["entity"]),
    ("dialogs.", ["entity"]),          # NPC 对话：键用 NPC 注册 id
    ("mood.", ["entity"]),             # NPC 心情：同上
    ("task.", ["item"]),               # 渔夫任务：键用任务物品 id
    ("item.", ["item", "block"]),      # 方块物品也走 item 键
    ("block.", ["block", "item"]),
    ("tooltip.effect.", ["effect"]),
    ("tooltip.", ["item", "block", "entity"]),
    ("title.", ["item", "block", "entity"]),
    ("subtitles.entity.", ["entity"]),
]
# 明确为死键的族（TE 自有机制，主模组无对应）→ 直接删
DEAD_PREFIXES = ("terra_entity.", "subtitles.", "effect.", "enchantment.", "key.", "container.", "message.")


def read(p):
    with io.open(p, "r", encoding="utf-8", newline="") as fh:
        return fh.read()


def write(p, t):
    with io.open(p, "w", encoding="utf-8", newline="") as fh:
        fh.write(t)


def collect_ids():
    # 与 registered_ids.py 同一口径：register/ registerInsect/ registerEntity/ registerItems …
    # 注意别退回「行内含 .register( 就抽所有引号串」的写法——会漏掉 registerInsect 这类助手，集合偏小，
    # 结果把可平移的键误判成删除项（实测 2698 vs 正确 1580）。
    call = re.compile(r'\.?register\w*\s*\(\s*"([a-z0-9_]+)"')
    ids = {}
    for kind, rels in REG_FILES.items():
        s = set()
        for rel in rels:
            p = os.path.join(REPO, "ConfluenceOtherworld", "src", "main", "java", "org", "confluence", "mod",
                             rel.replace("/", os.sep))
            if not os.path.isfile(p):
                continue
            for ln in read(p).split("\n"):
                m = call.search(ln)
                if m:
                    s.add(m.group(1))
        ids[kind] = s
    return ids


def kind_of(key):
    """返回该键可用的候选注册表列表（空列表 = 死键族）。"""
    for pre, kinds in FAMILY:
        if key.startswith(pre):
            return kinds
    for pre in DEAD_PREFIXES:
        if key.startswith(pre):
            return []
    return []          # 未映射的族一律按死键处理（但要靠 dry-run 复核族分布）


def step1_providers():
    total = 0
    for name in ("ModChineseProvider.java", "ModEnglishProvider.java"):
        p = os.path.join(GEN, name)
        text = read(p)
        eol = "\r\n" if "\r\n" in text else "\n"
        out, n = [], 0
        for ln in text.split(eol):
            if '"terra_entity.subtitle.' in ln:
                n += 1
                continue
            out.append(ln)
        if n:
            write(p, eol.join(out))
            print("provider %-26s 删除 %d 条死字幕键" % (name, n))
        total += n
    return total


def step2_langs(ids, dry=False):
    removed_log = []
    stats = {}
    fam_stats = Counter()
    for name in sorted(os.listdir(LANG)):
        if not name.endswith(".json"):
            continue
        p = os.path.join(LANG, name)
        text = read(p)
        eol = "\r\n" if "\r\n" in text else "\n"
        lines = text.split(eol)
        keep, renamed, removed = [], 0, []
        for ln in lines:
            m = re.match(r'^(\s*)"([^"]+)"(\s*:\s*.*?)(,?)(\s*)$', ln)
            if not m:
                keep.append(ln)
                continue
            key = m.group(2)
            if "terra_entity." not in key:
                keep.append(ln)
                continue
            kinds = kind_of(key)
            i = key.find("terra_entity.")
            eid = key[i + len("terra_entity."):].split(".")[0]
            hit = any(eid in ids.get(k, ()) for k in kinds)
            target = eid
            if not hit:
                for k in kinds:
                    new = EXTRA_RENAME.get((k, eid))
                    if new and new in ids.get(k, ()):
                        hit, target = True, new
                        break
            fam_stats[("改指 " if hit else "删除 ") + key[:i] + "<ns>"] += 1
            if hit:
                newkey = key.replace("terra_entity." + eid, "confluence." + target, 1)
                nl = ln.replace('"%s"' % key, '"%s"' % newkey, 1)
                keep.append(nl)
                renamed += 1
            else:
                removed.append(key)
        if dry:
            stats[name] = (renamed, len(removed))
            if removed:
                removed_log.append((name, removed))
            continue
        # 逗号修复：对象内最后一个数据行不能带逗号
        data_idx = [i for i, l in enumerate(keep) if re.match(r'^\s*"', l)]
        if data_idx:
            last = data_idx[-1]
            keep[last] = re.sub(r',(\s*)$', r'\1', keep[last])
        write(p, eol.join(keep))
        with io.open(p, encoding="utf-8") as fh:
            json.load(fh)          # 解析校验
        stats[name] = (renamed, len(removed))
        if removed:
            removed_log.append((name, removed))
    return stats, removed_log, fam_stats


def main():
    dry = "--dry" in sys.argv
    print("== 1) provider 侧死字幕键 ==")
    if dry:
        print("   （dry-run，不落盘）")
    n = 0 if dry else step1_providers()
    print("   删除 %d 条" % n)

    ids = collect_ids()
    print("== 注册 id：%s ==" % " / ".join("%s %d" % (k, len(v)) for k, v in ids.items()))

    print("== 2) 5 个已入库语区%s ==" % ("（dry-run）" if dry else ""))
    stats, removed_log, fam_stats = step2_langs(ids, dry=dry)
    for name, (ren, rem) in stats.items():
        print("   %-14s 改指（复活） %5d   删除 %5d" % (name, ren, rem))
    print("   -- 按族分布 --")
    for k, v in sorted(fam_stats.items()):
        print("      %6d  %s" % (v, k))

    if dry:
        return 0

    out = ["# TE 退役：语言键批次删除清单\n",
           "\n> 由 `tools/port2native/te_lang_keys.py` 生成。删除的都是**当前已失效**的键：\n",
           "> 其实体/物品的注册 id 不在主模组注册表中（TE 独有内容，且 1.21 游戏查的是 `confluence.` 命名空间）。\n",
           "> 若要恢复某个键，按本节列出的原文补回对应语区文件即可。\n"]
    total = 0
    for name, keys in removed_log:
        out.append("\n## %s（%d 条）\n\n" % (name, len(keys)))
        for k in sorted(keys):
            out.append("- `%s`\n" % k)
        total += len(keys)
    if not os.path.isdir(NOTES):
        os.makedirs(NOTES)
    write(os.path.join(NOTES, "TE-LANG-KEYS-REMOVED.md"), "".join(out))
    print("删除清单 -> notes/TE-LANG-KEYS-REMOVED.md（共 %d 条）" % total)
    return 0


if __name__ == "__main__":
    sys.exit(main())
