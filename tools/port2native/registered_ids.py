#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""抽出主模组注册表里的全部注册 id，供 TE 语言键/数据文件判定使用。

用法:
  python tools/port2native/registered_ids.py                      # 全部（entity/item/block 分组）
  python tools/port2native/registered_ids.py --kind entity        # 只要实体 id
  python tools/port2native/registered_ids.py --check-lang         # 逐键判定 5 个已入库语区的 TE 键

判定口径：语言键 `entity.terra_entity.<id>` 之类，若 `<id>` 在主模组同名注册表里存在 →
可 1:1 平移成 `entity.confluence.<id>`；否则是 TE 独有内容，退役时应删除该键。
输出重定向到文件再看（PowerShell 控制台 GBK 会打乱中文）。
"""
import argparse
import json
import os
import re
import sys
from collections import defaultdict

REG_FILES = {
    "entity": [
        "common/init/entity/MonsterEntities.java", "common/init/entity/CritterEntities.java",
        "common/init/entity/NpcEntities.java", "common/init/entity/BossEntities.java",
        "common/init/ModEntities.java",
    ],
    "item": [
        "common/init/item/ModItems.java", "common/init/item/MaterialItems.java",
        "common/init/item/BlockItems.java", "common/init/item/FoodItems.java",
        "common/init/item/ArmorItems.java", "common/init/item/VanityArmorItems.java",
        "common/init/item/ToolItems.java", "common/init/item/SwordItems.java",
        "common/init/item/BowItems.java", "common/init/item/ArrowItems.java",
        "common/init/item/BoomerangItems.java", "common/init/item/YoyoItems.java",
        "common/init/item/WhipItems.java", "common/init/item/FlailItems.java",
        "common/init/item/SpearItems.java", "common/init/item/ManaWeaponItems.java",
        "common/init/item/GunItems.java", "common/init/item/SummonItems.java",
        "common/init/item/PetItems.java", "common/init/item/SpawnEggItems.java",
    ],
    "block": ["common/init/block/ModBlocks.java", "common/init/block/NatureBlocks.java",
              "common/init/block/TorchBlocks.java", "common/init/block/StatueBlocks.java"],
}

# register("id"…)、registerItem("id"…)、registerEntity("id"…)、createItems(...).register("id"…)
REG_CALL = re.compile(r'\.?register\w*\s*\(\s*"([a-z0-9_]+)"')

LANG_DIR = os.path.join("ConfluenceOtherworld", "src", "main", "resources", "assets", "confluence", "lang")


def collect(repo, kinds):
    ids = defaultdict(set)
    for kind in kinds:
        for rel in REG_FILES.get(kind, []):
            p = os.path.join(repo, "ConfluenceOtherworld", "src", "main", "java", "org", "confluence", "mod",
                             rel.replace("/", os.sep))
            if not os.path.isfile(p):
                continue
            with open(p, encoding="utf-8", errors="replace") as fh:
                # ⚠️ 先把整文件压成一行再匹配：注册调用**经常跨行**
                # （如 `registerFlyingFish(\n        "flying_fish", 0.9F, …`），逐行匹配会漏 id。
                # 实测漏掉过 flying_fish / traveling_merchant / wandering_eye_fish / whip_attack /
                # carton_of_milk / dead_mans_seater，并因此误删过 3 个有效语言键。
                flat = re.sub(r"\s+", " ", fh.read())
            for m in REG_CALL.finditer(flat):
                ids[kind].add(m.group(1))
    return ids


def check_lang(repo, ids):
    """语言键形如 <family>.terra_entity.<id>（bestiary 会多一层 .desc 之类）。"""
    # 每个 family 用哪类 id
    family_kind = {
        "entity.": "entity", "bestiary.entity.": "entity", "item.": "item", "block.": "block",
        "subtitles.entity.": "entity", "subtitles.": "entity", "effect.": "effect",
    }
    lang_dir = os.path.join(repo, LANG_DIR)
    if not os.path.isdir(lang_dir):
        print("找不到语言目录: " + lang_dir)
        return
    total_ren = total_del = 0
    for name in sorted(os.listdir(lang_dir)):
        if not name.endswith(".json"):
            continue
        with open(os.path.join(lang_dir, name), encoding="utf-8") as fh:
            try:
                data = json.load(fh)
            except Exception as e:  # noqa: BLE001
                print("!! %s 解析失败: %s" % (name, e))
                continue
        ren, dele = [], []
        for k in data:
            if "terra_entity." not in k:
                continue
            head, _, rest = k.partition("terra_entity.")
            kind = None
            for pre, kd in sorted(family_kind.items(), key=lambda kv: -len(kv[0])):
                if k.startswith(pre):
                    kind = kd
                    break
            eid = rest.split(".")[0]
            if kind and eid in ids.get(kind, ()):
                ren.append(k)
            else:
                dele.append((k, kind or "?"))
        total_ren += len(ren)
        total_del += len(dele)
        print("%-14s 可平移 %5d   应删 %5d" % (name, len(ren), len(dele)))
        for k, kind in dele[:12]:
            print("      应删 [%s] %s" % (kind, k))
    print("---- 合计：可平移 %d，应删 %d" % (total_ren, total_del))


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--repo", default=".")
    ap.add_argument("--kind", choices=list(REG_FILES) + ["all"], default="all")
    ap.add_argument("--check-lang", action="store_true")
    args = ap.parse_args()
    repo = os.path.abspath(args.repo)
    kinds = list(REG_FILES) if args.kind == "all" else [args.kind]
    ids = collect(repo, list(REG_FILES))
    if args.check_lang:
        check_lang(repo, ids)
        return 0
    for kind in kinds:
        print("== %s ids: %d" % (kind, len(ids.get(kind, ()))))
        if args.kind != "all":
            for i in sorted(ids.get(kind, ())):
                print("   " + i)
    return 0


if __name__ == "__main__":
    sys.exit(main())
