# -*- coding: utf-8 -*-
r"""TE 退役 · datagen provider 侧的 `terra_entity.` 键字面量批次。

背景：`f4d037ec4` 只把**已入库译文**与两个 provider 的 `terra_entity.subtitle.*` 改掉了，
生成这些键的 provider 仍在吐 `terra_entity.<id>` —— 不改源头，`runData` 会把成果回退。

判定口径（与 `te_lang_keys.py` / `registered_ids.py` 完全一致，保证 provider 与 JSON 两边同意）：

  键形如 `<family>.terra_entity.<id>`，取 `terra_entity.` 后第一段为 `<id>`：
    · 现役语句（真正会被 `add(...)` 执行、也就是真正会生成键的那些）：
        - `<id>` 在主模组同名注册表存在            → 只替命名空间段：`terra_entity.` → `confluence.`
        - 不存在但 `EXTRA_RENAME` 有映射且目标存在 → 用映射后的 id（改名族，与已入库 JSON 一致）
        - 都不成立                                 → 整条语句删除（这些键当前本就是死的）
    · 注释语句（`// add(...)`）：**只替命名空间段，永不删除**。
      实测依据：本文件是 1.20 权威文件「把 `confluence.` 全局换成 `terra_entity.`」的产物，
      650 条注释行里的 457 组键与 1.20 权威**同样以注释停车**（如 `stylist`/`town_cat`，连行号都对齐），
      1.20 权威的停车注释用的就是 `confluence.<id>` 命名空间。删掉它们等于删掉权威文件保留的停车内容；
      而注释不生成任何键，本批次的动机（防 `runData` 回退）对它们不成立。
      需要按字面口径把这些注释也一并删除时用 `--drop-comments`。

  语句感知：单行 `add("k", "v");` 删整行；多行语句（`add("k",` … `);`）删整段。
  另有 `KEY_FIX` 一张定点修正表（与 `terra_entity` 无关、独立于命中判定）：批 11 把图鉴里的
  `FEALING` 改回朴素条目（原生 `Fealing` 不是变体持有者），其键要去掉 `.0` 序号 ——
  `bestiary.entity.confluence.fealing.0.desc` → `…fealing.desc`、`entity.confluence.fealing.0` →
  `entity.confluence.fealing`（1.20 权威 242/1031 与 ModEnglishProvider:1639 即如此）。
  删除的键全部留档到 `notes/TE-LANG-KEYS-REMOVED-PROVIDERS.md`（本次若已无删除项则不覆盖该文件）。

用法（默认 dry-run，只打印报告不落盘；输出重定向到文件再看，控制台 GBK 会打乱中文）：
  python tools/port2native/te_lang_providers.py --dry > %TEMP%\te_prov_dry.txt 2>&1
  python tools/port2native/te_lang_providers.py --apply
"""
import argparse
import io
import json
import os
import re
import sys
from collections import Counter, OrderedDict

REPO = os.path.abspath(".")
MOD = os.path.join(REPO, "ConfluenceOtherworld", "src", "main", "java", "org", "confluence", "mod")

# 显式文件清单（只碰这些文件）
LANG_FILES = [
    "common/data/gen/language/BestiaryLanguageSubProvider.java",
    "common/data/gen/language/DialogsLanguageSubProvider.java",
    "common/data/gen/ModChineseProvider.java",
    "common/data/gen/ModEnglishProvider.java",
]
BOOM_FILE = "common/recipe/special/BoomBunnyRecipe.java"
# BoomBunnyRecipe 的唯一命中是实体 id 字符串比较（ResourceLocation 形式，带冒号）
BOOM_FROM, BOOM_TO = '"terra_entity:bunny"', '"confluence:bunny"'

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
}

# 与 tools/port2native/te_lang_keys.py 的 EXTRA_RENAME 逐字一致（两边必须同意）
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

# 键前缀 → 候选注册表（与 te_lang_keys.py 的 FAMILY 一致）
FAMILY = [
    ("bestiary.entity.", ["entity"]),
    ("entity.", ["entity"]),
    ("dialogs.", ["entity"]),
    ("mood.", ["entity"]),
    ("task.", ["item"]),
    ("item.", ["item", "block"]),
    ("block.", ["block", "item"]),
    ("tooltip.effect.", ["effect"]),
    ("tooltip.", ["item", "block", "entity"]),
    ("title.", ["item", "block", "entity"]),
    ("subtitles.entity.", ["entity"]),
]

REG_CALL_LINE = re.compile(r'\.?register\w*\s*\(\s*"([a-z0-9_]+)"')     # 旧口径：只认同行
REG_CALL = re.compile(r'\.?register\w*\s*\(')                            # 跨行口径：先找调用，再取首个参数
STR = re.compile(r'"((?:[^"\\]|\\.)*)"')
KEY_LIT = re.compile(r'"([^"\\]*)"')
NS = "terra_entity."

# 批 11 之后的定点键修正：`FEALING` 已从「变体持有者」改回朴素条目（原生 `Fealing` 没有 `Variant`
# 枚举、不是 `VariantHolder`），因此它的键去掉 `.0` 序号。1.20 权威即如此：
#   BestiaryLanguageSubProvider:242/1031  add("bestiary.entity.confluence.fealing.desc", …)
#   ModEnglishProvider:1639               add("entity.confluence.fealing", "Flying Spirit")
#   （1.20 的 ModChineseProvider 走 `add(CritterEntities.FEALING.get(), …)` 重载，等价键同形）
#   es_es/lzh/pt_br 三个已入库语区也都已有 `entity.confluence.fealing`（无 `.0`）。
# 只按「带引号的完整字面量」替换，避免 `entity.confluence.fealing.0` 误命中
# `bestiary.entity.confluence.fealing.0.desc`。
KEY_FIX = [
    ("bestiary.entity.confluence.fealing.0.desc", "bestiary.entity.confluence.fealing.desc"),
    ("entity.confluence.fealing.0", "entity.confluence.fealing"),
]


def read(p):
    with io.open(p, "r", encoding="utf-8", newline="") as fh:
        return fh.read()


def write(p, t):
    with io.open(p, "w", encoding="utf-8", newline="") as fh:
        fh.write(t)


def mask_strings(t):
    """把字符串字面量内部字符抹平（长度不变），便于安全地做括号配平。"""
    out = list(t)
    for m in STR.finditer(t):
        for i in range(m.start() + 1, m.end() - 1):
            out[i] = "x"
    return "".join(out)


def first_arg(text, masked, start, end):
    """第一个顶层逗号之前的参数原文。"""
    depth = 0
    i = start
    while i < end:
        c = masked[i]
        if c in "([{":
            depth += 1
        elif c in ")]}":
            depth -= 1
        elif c == "," and depth == 0:
            return text[start:i]
        i += 1
    return text[start:end]


def collect_ids_file(text, ids):
    """跨行口径：`register*("id", …)` 的 id 允许出现在调用后的下一行。

    旧口径（同行正则，registered_ids.py 用的那个）会漏掉形如
        registerFlyingFish(
                "flying_fish", 0.9F, 0.9F,
    的注册，实测主模组实体少了 4 个 id（flying_fish / traveling_merchant /
    wandering_eye_fish / whip_attack）。这里按括号配平取「首个参数是纯字面量」的调用，
    是旧口径的超集（旧口径有而这里没有的 id 为 0），且不会误收 `prefix + "_torch"` 这种拼接。
    """
    masked = mask_strings(text)
    for m in REG_CALL.finditer(masked):
        j, depth = m.end(), 1
        while j < len(masked) and depth:
            if masked[j] == "(":
                depth += 1
            elif masked[j] == ")":
                depth -= 1
            j += 1
        sm = re.fullmatch(r'\s*"([a-z0-9_]+)"\s*', first_arg(text, masked, m.end(), j - 1))
        if sm:
            ids.add(sm.group(1))


def collect_ids():
    ids = {}
    old = {}
    for kind, rels in REG_FILES.items():
        s, o = set(), set()
        for rel in rels:
            p = os.path.join(MOD, rel.replace("/", os.sep))
            if not os.path.isfile(p):
                continue
            text = read(p)
            collect_ids_file(text, s)
            for ln in text.split("\n"):
                m = REG_CALL_LINE.search(ln)
                if m:
                    o.add(m.group(1))
        ids[kind] = s
        old[kind] = o
    return ids, old


def kind_of(key):
    for pre, kinds in FAMILY:
        if key.startswith(pre):
            return kinds
    return None


def decide(key, ids):
    """现役语句的判定：RENAME（含改名族）/ DELETE / UNKNOWN。"""
    kinds = kind_of(key)
    if kinds is None:
        return "UNKNOWN", None, None
    pos = key.find(NS)
    eid = key[pos + len(NS):].split(".")[0]
    for k in kinds:
        if eid in ids.get(k, ()):
            return "RENAME", key.replace(NS + eid, "confluence." + eid, 1), eid
    for k in kinds:
        new = EXTRA_RENAME.get((k, eid))
        if new and new in ids.get(k, ()):
            return "RENAME", key.replace(NS + eid, "confluence." + new, 1), eid + "->" + new
    return "DELETE", None, eid


def statement_extent(lines, i):
    """含第 i 行的完整语句 [start, end]；不是自含语句时返回 None。"""
    if lines[i].rstrip().endswith(");"):
        return i, i
    for j in range(i + 1, min(i + 10, len(lines))):
        if lines[j].rstrip().endswith(");"):
            return i, j
    return None


def process(rel, ids, drop_comments):
    p = os.path.join(MOD, rel.replace("/", os.sep))
    text = read(p)
    eol = "\r\n" if "\r\n" in text else "\n"
    lines = text.split(eol)
    hits = [i for i, ln in enumerate(lines) if NS in ln]
    stat = Counter()
    deleted = []          # (key, line_no)
    renamed = []          # (key, new_key, line_no, active?)
    comment_unreg = []    # 注释行里 id 未注册的键（仅改命名空间）
    undecided = []        # (line_no, text)
    drop_ranges = []
    line_edits = {}       # line index -> new line
    done = set()

    for i in hits:
        if i in done:
            continue
        s = lines[i].strip()
        commented = s.startswith("//")
        ext = statement_extent(lines, i)
        if ext is None:
            undecided.append((i + 1, s[:120]))
            continue
        start, end = ext
        for x in range(start, end + 1):
            done.add(x)
        body_lines = lines[start:end + 1]
        stmt = eol.join(body_lines)
        keys = [k for k in KEY_LIT.findall(stmt) if NS in k]
        if len(keys) != 1:
            undecided.append((i + 1, s[:120]))
            continue
        key = keys[0]
        if commented and not drop_comments:
            act, new, _ = decide(key, ids)
            new_key = key.replace(NS, "confluence.", 1)     # 注释：纯命名空间替换，保留 id 原样
            renamed.append((key, new_key, start + 1, False))
            if act == "DELETE":
                comment_unreg.append(key)
            line_edits[start] = lines[start].replace('"%s"' % key, '"%s"' % new_key, 1)
            stat["com_ren"] += 1
            if act != "DELETE":
                stat["com_ren_reg"] += 1
            else:
                stat["com_ren_unreg"] += 1
            continue
        # 现役（或 --drop-comments 下的注释行）
        act, new, eid = decide(key, ids)
        if act == "UNKNOWN":
            undecided.append((i + 1, s[:120]))
            continue
        if act == "RENAME":
            line_edits[start] = lines[start].replace('"%s"' % key, '"%s"' % new, 1)
            renamed.append((key, new, start + 1, not commented))
            stat["act_ren" if not commented else "com_ren"] += 1
            if eid and "->" in eid:
                stat["extra_rename"] += 1
        else:
            drop_ranges.append((start, end))
            deleted.append((key, start + 1))
            stat["act_del" if not commented else "com_del"] += 1
    # 落盘内容
    out = []
    for i, ln in enumerate(lines):
        if any(s <= i <= e for s, e in drop_ranges):
            continue
        out.append(line_edits.get(i, ln))
    # 定点键修正（与 terra_entity 无关，独立于上面的命中判定）
    fixed = []
    for i, ln in enumerate(out):
        nl = ln
        for old, new in KEY_FIX:
            if '"%s"' % old in nl:
                nl = nl.replace('"%s"' % old, '"%s"' % new)
                fixed.append((old, new, i + 1))
        out[i] = nl
    stat["key_fix"] = len(fixed)
    stat["hits"] = len(hits)
    stat["comment_hits"] = sum(1 for i in hits if lines[i].strip().startswith("//"))
    return eol.join(out), stat, deleted, renamed, comment_unreg, undecided, fixed


def process_boom(rel):
    p = os.path.join(MOD, rel.replace("/", os.sep))
    text = read(p)
    eol = "\r\n" if "\r\n" in text else "\n"
    stat = Counter()
    stat["hits"] = text.count(NS)
    n_from = text.count(BOOM_FROM)
    out = text.replace(BOOM_FROM, BOOM_TO)
    stat["act_ren"] = n_from
    renamed = [(BOOM_FROM.strip('"'), BOOM_TO.strip('"'), 0, True)] if n_from else []
    return out, stat, [], renamed, [], [], []


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry", action="store_true", help="只打印报告（默认行为）")
    ap.add_argument("--apply", action="store_true", help="落盘（默认 dry-run）")
    ap.add_argument("--drop-comments", action="store_true",
                    help="按字面口径把注释语句里 id 未注册的整条也删掉（默认只改命名空间并保留）")
    ap.add_argument("--full", action="store_true", help="逐条打印判定")
    ap.add_argument("--files", nargs="*", default=None, help="显式文件清单（相对 mod java 根）")
    args = ap.parse_args()
    dry = not args.apply
    files = args.files or (LANG_FILES + [BOOM_FILE])

    ids, old_ids = collect_ids()
    print("== 注册 id 口径（跨行 register*( 首个参数；registered_ids.py 的同行口径为子集）==")
    for k in sorted(ids):
        delta = sorted(ids[k] - old_ids[k])
        print("   %-8s %d（旧同行口径 %d；跨行补齐 %d：%s）" % (k, len(ids[k]), len(old_ids[k]),
                                                          len(delta), ",".join(delta)))
    print("== 模式：%s ==" % ("删除注释里未注册的整条（字面口径）" if args.drop_comments
                             else "注释行只改命名空间、永不删除（1.20 权威停车口径）"))

    results = OrderedDict()
    total = Counter()
    all_deleted = []
    for rel in files:
        p = os.path.join(MOD, rel.replace("/", os.sep))
        if not os.path.isfile(p):
            print("!! 缺文件 %s" % rel)
            continue
        if rel == BOOM_FILE:
            out, stat, deleted, renamed, cunreg, undec, fixed = process_boom(rel)
        else:
            out, stat, deleted, renamed, cunreg, undec, fixed = process(rel, ids, args.drop_comments)
        results[rel] = out
        total.update(stat)
        all_deleted.append((rel, deleted, cunreg))
        print("\n== %s ==" % rel)
        print("   命中行 %d（现役 %d / 注释 %d）" % (stat["hits"], stat["hits"] - stat["comment_hits"],
                                                stat["comment_hits"]))
        print("   现役：改命名空间 %d；删除整条 %d" % (stat["act_ren"], stat["act_del"]))
        print("   注释：改命名空间 %d（其中 id 已注册 %d / 未注册 %d）；删除整条 %d"
              % (stat["com_ren"], stat["com_ren_reg"], stat["com_ren_unreg"], stat["com_del"]))
        print("   EXTRA_RENAME 改名族生效 %d" % stat["extra_rename"])
        print("   定点键修正 %d" % stat["key_fix"])
        for old, new, ln in fixed:
            print("      FIX L%-5d %s -> %s" % (ln, old, new))
        print("   未判定 %d" % len(undec))
        for ln, s in undec:
            print("      !! L%d %s" % (ln, s))
        if args.full:
            print("   -- 逐条判定（现役）--")
            for k, new, ln, active in renamed:
                if active:
                    print("      RENAME L%-5d %-74s -> %s" % (ln, k, new))
            for k, ln in deleted:
                print("      DELETE L%-5d %s" % (ln, k))
            print("   -- 逐条判定（注释，仅改命名空间）--")
            for k, new, ln, active in renamed:
                if not active:
                    print("      COMMENT L%-5d %-74s -> %s" % (ln, k, new))

    print("\n== 汇总 ==")
    print("   现役改命名空间 %d；现役删除 %d" % (total["act_ren"], total["act_del"]))
    print("   注释改命名空间 %d（已注册 %d / 未注册 %d）；注释删除 %d"
          % (total["com_ren"], total["com_ren_reg"], total["com_ren_unreg"], total["com_del"]))
    print("   EXTRA_RENAME 生效 %d" % total["extra_rename"])
    print("   定点键修正（KEY_FIX）生效 %d" % total["key_fix"])

    if dry:
        print("\n(dry-run，未落盘)")
        return 0

    for rel, out in results.items():
        write(os.path.join(MOD, rel.replace("/", os.sep)), out)
        print("written %s" % rel)

    out = ["# TE 退役：datagen provider 侧键删除清单\n",
           "\n> 由 `tools/port2native/te_lang_providers.py` 生成，收口 `f4d037ec4` 与\n",
           "> `notes/TE-LANG-KEYS-REMOVED.md` 留下的 **provider 侧缺口**（provider 仍在吐\n",
           "> `terra_entity.<id>`，不修源头 `runData` 会把那批成果回退）。\n",
           "> 判定口径见 `tools/port2native/te_lang_providers.py` 文件头，与 `te_lang_keys.py` 逐字一致。\n",
           "\n删除的都是**当前已失效**的键：其 id 不在主模组注册表中（TE 独有内容 / 未注册内容，\n",
           "且 1.21 游戏查的是 `confluence.` 命名空间）。要恢复某条，按本节列出的键名从 git 历史里\n",
           "取回原语句即可（本文件只记键名，与 `notes/TE-LANG-KEYS-REMOVED.md` 同格式）。\n"]
    total_del = 0
    n_stmt = 0
    for rel, deleted, _cunreg in all_deleted:
        if not deleted:
            continue
        out.append("\n## %s（%d 个键 / %d 条语句）\n\n" % (rel.replace("/", "\\"), len(set(k for k, _ in deleted)),
                                                       len(deleted)))
        for k in sorted(set(k for k, _ in deleted)):
            out.append("- `%s`\n" % k)
        total_del += len(set(k for k, _ in deleted))
        n_stmt += len(deleted)
    out.append("\n---\n\n## 附：注释语句（`// add(...)`）的处理\n")
    out.append("\n> 注释语句**不生成任何键**，本批次的动机（防 `runData` 回退）对它们不成立，\n")
    out.append("> 因此只把命名空间段改成 `confluence.`、**一条都没删**。依据：本文件是 1.20 权威文件把\n")
    out.append("> `confluence.` 全局换成 `terra_entity.` 的产物，1.20 权威自己就把这些尚未实现的内容\n")
    out.append("> 以注释停车（`// add(\"bestiary.entity.confluence.<id>.desc\", ...)`，连行号都对齐），\n")
    out.append("> 删掉它们等于删掉权威保留的停车内容。下表列出「注释里 id 未注册」的键——\n")
    out.append("> 若日后要按字面口径删除，`te_lang_providers.py --apply --drop-comments` 一条命令即可。\n")
    for rel, _deleted, cunreg in all_deleted:
        if not cunreg:
            continue
        out.append("\n### %s（%d 个键 / %d 条语句，仅改命名空间）\n\n"
                   % (rel.replace("/", "\\"), len(set(cunreg)), len(cunreg)))
        for k in sorted(set(cunreg)):
            out.append("- `%s`\n" % k)
    np = os.path.join(REPO, "notes", "TE-LANG-KEYS-REMOVED-PROVIDERS.md")
    if total_del == 0 and os.path.isfile(np):
        print("!! 本次没有可记录的删除（目标文件里已无 terra_entity. 键）——"
              "保留既有 %s 不动，避免把上批的删除清单覆盖成空。" % os.path.relpath(np, REPO))
        return 0
    write(np, "".join(out).replace("\n", "\r\n"))       # 仓库约定 CRLF
    print("删除清单 -> notes/TE-LANG-KEYS-REMOVED-PROVIDERS.md（删除 %d 个键 / %d 条语句 + 注释附表）"
          % (total_del, n_stmt))
    return 0


if __name__ == "__main__":
    sys.exit(main())
