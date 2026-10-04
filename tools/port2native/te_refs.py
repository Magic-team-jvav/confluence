#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""TE 退役施工依据：把主模组里每一条 org.confluence.terraentity.* 引用，
对齐到 1.20 对应文件里的原生写法。

背景：1.20 整个仓库对 terraentity 的引用数为 0（mod 全原生），
所以「TE 退役」= 把 1.21 的每条 TE 引用换成 1.20 同一位置的写法。
本工具用 difflib 把 1.21 文件与 1.20 同名文件逐行对齐，
为每条 TE 引用给出 1.20 的对侧行，作为改指依据。

用法:
  python tools/port2native/te_refs.py                    # 汇总
  python tools/port2native/te_refs.py --file LivingEntity # 只看匹配的文件
  python tools/port2native/te_refs.py --worksheet        # 另写 notes/TE-RETIREMENT-WORKSHEET.md
"""
import argparse
import difflib
import os
import re
import sys
from collections import OrderedDict, defaultdict

TE_PKG = "org.confluence.terraentity"

# B 类：TE 注册表 -> 主模组原生注册表（1.21 侧路径）
B_MAP = {
    "TEMonsterEntities": "common/init/entity/MonsterEntities.java",
    "TEBossEntities": "common/init/entity/BossEntities.java",
    "TEAnimals": "common/init/entity/CritterEntities.java",
    "TENpcEntities": "common/init/entity/NpcEntities.java",
    "TETags": "common/init/ModTags.java",
    "TEEffects": "common/init/ModEffects.java",
    "TESounds": "common/init/ModSoundEvents.java",
    "TEBoomerangItems": "common/init/item/BoomerangItems.java",
    "TEYoyosItems": "common/init/item/YoyoItems.java",
    "TEWhipItems": "common/init/item/WhipItems.java",
    "TESummonItems": "common/init/item/SummonItems.java",
    "TEItems": "common/init/item/ModItems.java",
    "TEArmors": "common/init/item/ArmorItems.java",
    "TEProjectileEntities": "common/init/ModEntities.java",
    "TEEntities": "common/init/ModEntities.java",
    "TESpawnEggItems": "common/init/item/SpawnEggItems.java",
    "TEEnchantments": "common/init/ModEnchantments.java",
    "TEFigureBlocks": "common/init/block/FigureBlocks.java",
    "TEPetItems": "common/init/item/PetItems.java",
}

IMPORT_RE = re.compile(r"^\s*import\s+(static\s+)?(" + re.escape(TE_PKG) + r"\.([\w\.]+))\s*;")


def read_lines(path):
    with open(path, "r", encoding="utf-8", errors="replace") as fh:
        return fh.read().splitlines()


def find_120(src120, rel):
    """1.21 相对路径 -> 1.20 文件；同名文件可能在别的包（如 init/ 与 init/entity/）。"""
    cand = os.path.join(src120, rel.replace("/", os.sep))
    if os.path.isfile(cand):
        return cand
    base = os.path.basename(rel)
    hits = []
    for root, _dirs, files in os.walk(src120):
        if base in files:
            hits.append(os.path.join(root, base))
    if not hits:
        return None
    # 同包层级优先，其次路径深度接近
    want = rel.count("/")
    hits.sort(key=lambda p: (abs(p.replace(src120, "").count(os.sep) - want), len(p)))
    return hits[0]


def align_map(lines121, lines120):
    """返回 1.21 行号(0基) -> 1.20 行号(0基) 的近似映射。"""
    sm = difflib.SequenceMatcher(None, lines121, lines120, autojunk=False)
    mapping = {}
    for tag, i1, i2, j1, j2 in sm.get_opcodes():
        if tag == "equal":
            for k in range(i2 - i1):
                mapping[i1 + k] = j1 + k
        elif tag in ("replace", "delete"):
            # 按顺序配对，多出的行映射到对侧区间起点（便于人工比对）
            for k in range(i2 - i1):
                mapping[i1 + k] = min(j1 + k, max(j1, j2 - 1)) if j2 > j1 else None
    return mapping


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--repo", default=".")
    ap.add_argument("--src120", default=r"D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java")
    ap.add_argument("--file", default=None, help="只处理路径包含该子串的文件")
    ap.add_argument("--worksheet", action="store_true", help="写 notes/TE-RETIREMENT-WORKSHEET.md")
    ap.add_argument("--resolve", action="store_true",
                    help="对「目标注册表缺同名字段」逐条给出 1.20 证据与 1.21 全仓定义位置")
    args = ap.parse_args()

    repo = os.path.abspath(args.repo)
    src121 = os.path.join(repo, "ConfluenceOtherworld", "src", "main", "java")
    src120 = args.src120
    mod121 = os.path.join(src121, "org", "confluence", "mod")
    mod120 = os.path.join(src120, "org", "confluence", "mod")
    if not os.path.isdir(mod121):
        sys.exit("找不到 1.21 主模组源码目录: " + mod121)
    if not os.path.isdir(mod120):
        sys.exit("找不到 1.20 主模组源码目录: " + mod120)

    files = []
    for root, _dirs, names in os.walk(mod121):
        for n in names:
            if not n.endswith(".java"):
                continue
            p = os.path.join(root, n)
            rel = os.path.relpath(p, mod121).replace(os.sep, "/")
            if args.file and args.file.lower() not in rel.lower():
                continue
            files.append((rel, p))

    by_symbol = defaultdict(list)     # TE 简单名 -> [(rel, lineno, code, target120)]
    wildcards = []                    # 通配符 import（无简单名，须单独报告，否则整体漏扫）
    per_file = OrderedDict()
    no_counterpart = []
    member_missing = []

    for rel, path in sorted(files):
        lines = read_lines(path)
        imports = OrderedDict()       # 简单名 -> 全限定名
        for i, ln in enumerate(lines, 1):
            m = IMPORT_RE.match(ln)
            if m:
                fqn = m.group(2)
                if fqn.endswith("*"):      # 通配符 import 没有简单名，单独记账（曾整体漏扫）
                    wildcards.append("%s:%d  %s" % (rel, i, fqn))
                    continue
                simple = fqn.split(".")[-1]
                imports[simple] = fqn
        if not imports:
            continue
        p120 = find_120(src120, rel)
        if p120 is None:
            no_counterpart.append(rel)
            l120 = []
            mapping = {}
        else:
            l120 = read_lines(p120)
            mapping = align_map(lines, l120)
        refs = []
        for i, ln in enumerate(lines, 1):
            if ln.lstrip().startswith("import "):
                continue
            for simple in imports:
                if re.search(r"\b" + re.escape(simple) + r"\b", ln):
                    tgt = mapping.get(i - 1)
                    tgt_txt = l120[tgt].strip() if (tgt is not None and 0 <= tgt < len(l120)) else ""
                    refs.append((i, simple, ln.strip(), tgt_txt))
                    by_symbol[simple].append((rel, i, ln.strip(), tgt_txt))
                    mm = re.search(r"\b" + re.escape(simple) + r"\.(\w+)", ln)
                    if mm and simple in B_MAP:
                        target_rel = B_MAP[simple]
                        tp = os.path.join(mod121, target_rel.replace("/", os.sep))
                        if os.path.isfile(tp):
                            body = "\n".join(read_lines(tp))
                            if not re.search(r"\b" + re.escape(mm.group(1)) + r"\b", body):
                                member_missing.append((rel, i, simple, mm.group(1), target_rel))
                        else:
                            member_missing.append((rel, i, simple, mm.group(1), target_rel + " (目标文件缺失)"))
        if refs:
            per_file[rel] = (imports, refs, os.path.relpath(p120, src120).replace(os.sep, "/") if p120 else "")

    print("=" * 78)
    print("TE 引用总览：%d 个文件 / %d 条引用 / %d 种 TE 类"
          % (len(per_file), sum(len(v[1]) for v in per_file.values()), len(by_symbol)))
    print("=" * 78)
    for simple, rows in sorted(by_symbol.items(), key=lambda kv: (-len(kv[1]), kv[0])):
        flag = "B->" + B_MAP[simple] if simple in B_MAP else "C/需判定"
        print("%4d  %-26s %s" % (len(rows), simple, flag))

    if wildcards:
        print("\n[通配符 TE import] %d 条（无简单名，逐符号扫描看不到它们，必须人工处理）：" % len(wildcards))
        for w in wildcards:
            print("   " + w)
    else:
        print("\n[通配符 TE import] 无")

    if no_counterpart:
        print("\n[1.20 无同名文件] %d 个：" % len(no_counterpart))
        for r in no_counterpart:
            print("   " + r)

    if member_missing:
        print("\n[目标注册表缺同名字段] %d 条：" % len(member_missing))
        for rel, i, sym, mem, tgt in member_missing:
            print("   %s:%d  %s.%s  -> %s" % (rel, i, sym, mem, tgt))
    else:
        print("\n[目标注册表缺同名字段] 无 —— B 类改指成员级可机械完成")

    if args.resolve and member_missing:
        all121 = []
        for root, _d, names in os.walk(mod121):
            for n in names:
                if n.endswith(".java"):
                    all121.append(os.path.join(root, n))
        print("\n" + "=" * 78)
        print("逐条解析（1.20 证据 / 1.21 定义位置）")
        print("=" * 78)
        for rel, i, sym, mem, tgt in member_missing:
            tgt_rel = tgt.replace(" (目标文件缺失)", "")
            print("\n%s:%d  %s.%s" % (rel, i, sym, mem))
            p120 = find_120(src120, "org/confluence/mod/" + tgt_rel)
            if p120 is None:
                print("   1.20: 目标文件不存在（推断目标本身有误）")
            else:
                hits = [(k, ln.strip()) for k, ln in enumerate(read_lines(p120), 1)
                        if re.search(r"\b" + re.escape(mem) + r"\b", ln)]
                if hits:
                    print("   1.20 %s 有该成员：" % os.path.basename(p120))
                    for k, txt in hits[:3]:
                        print("      L%-5d %s" % (k, txt[:150]))
                else:
                    print("   1.20 %s 也没有该成员 —— 名称已变，需按语义判定" % os.path.basename(p120))
            dec = re.compile(r"\b" + re.escape(mem) + r"\b")
            found = []
            for p in all121:
                for k, ln in enumerate(read_lines(p), 1):
                    if dec.search(ln) and re.search(r"(public|protected|private|static|\bnew\b|\bvar\b)", ln):
                        found.append("%s:%d  %s" % (os.path.relpath(p, mod121).replace(os.sep, "/"), k, ln.strip()[:120]))
                        if len(found) >= 3:
                            break
                if len(found) >= 3:
                    break
            if found:
                print("   1.21 同名字段出现于：")
                for f in found:
                    print("      " + f)
            else:
                print("   1.21 全仓无同名标识符 —— 该字段在主模组侧尚不存在（真缺口）")

    if args.file:
        for rel, (imports, refs, rel120) in per_file.items():
            print("\n" + "=" * 78)
            print(rel + "   <->   1.20:" + (rel120 or "无"))
            print("-" * 78)
            print("imports: " + ", ".join(imports.keys()))
            for i, simple, code, tgt in refs:
                print("  %5d | %s" % (i, code))
                print("        | 1.20> %s" % (tgt if tgt else "<无对侧行>"))

    if args.worksheet:
        out = os.path.join(repo, "notes", "TE-RETIREMENT-WORKSHEET.md")
        w = []
        w.append("# TE 退役逐条施工依据（te_refs.py 生成）\n")
        w.append("\n> 生成命令：`python tools/port2native/te_refs.py --worksheet`\n")
        w.append("\n> 1.20 侧 terraentity 引用数 = 0，故每条引用的目标是**同一位置的 1.20 原生写法**。\n")
        w.append("\n> difflib 逐行对齐给出「1.20 对侧行」，仅供比对；同名类在 1.20 里的写法即最终形态。\n\n")
        w.append("## 一、按 TE 类汇总\n\n| TE 类 | 引用数 | 处理 |\n|---|---:|---|\n")
        for simple, rows in sorted(by_symbol.items(), key=lambda kv: (-len(kv[1]), kv[0])):
            tag = ("改指 `%s`" % B_MAP[simple]) if simple in B_MAP else "逐条判定（见 §三）"
            w.append("| `%s` | %d | %s |\n" % (simple, len(rows), tag))
        w.append("\n## 二、逐文件清单（1.21 行 -> 1.20 对侧行）\n")
        for rel, (imports, refs, rel120) in per_file.items():
            w.append("\n### `%s`\n\n" % rel)
            w.append("- 1.20 对应：`%s`\n" % (rel120 or "<无同名文件>"))
            w.append("- TE import：%s\n\n" % ", ".join("`%s`" % k for k in imports))
            w.append("| 行 | TE 代码 | 1.20 对侧 |\n|---:|---|---|\n")
            for i, simple, code, tgt in refs:
                esc = lambda s: s.replace("|", "\\|")
                w.append("| %d | `%s` | `%s` |\n" % (i, esc(code), esc(tgt if tgt else "<无>")))
        with open(out, "w", encoding="utf-8", newline="\n") as fh:
            fh.write("".join(w))
        print("\n已写出 " + out)

    return 0


if __name__ == "__main__":
    sys.exit(main())
