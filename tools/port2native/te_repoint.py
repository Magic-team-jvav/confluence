#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""TE 退役执行工具：按映射表把主模组里的 TerraEntity 引用定点改成原生实现。

设计约束（本仓库的血泪教训）：
* 只动「显式列出、且映射表命中」的文件；不做目录级/仓库级替换。
* import 行**原位替换**，绝不重排 import 块。
* 目标类名与简单名相同的成员（如 TEMonsterEntities.SPIDER -> MonsterEntities.SPIDER）
  只改限定名；成员改名走 MEMBER_OVERRIDES。
* 目标注册表里找不到同名成员的引用会在报告里点名，默认**跳过该文件**（需人工判定）。
* 默认 dry-run：只打摘要 + 把完整 diff 写进报告文件。加 --apply 才落盘。

用法:
  python tools/port2native/te_repoint.py                      # dry-run（摘要 + 报告）
  python tools/port2native/te_repoint.py --file Loot         # 只看匹配文件
  python tools/port2native/te_repoint.py --apply              # 落盘
  python tools/port2native/te_repoint.py --apply --force      # 连「成员未解析」的文件一起改
"""
import argparse
import difflib
import os
import re
import sys
from collections import OrderedDict

TE_PKG = "org.confluence.terraentity"
MOD_PKG = "org.confluence.mod"

# TE 简单名 -> 主模组侧目标文件（相对 org/confluence/mod）
TARGETS = {
    "TEMonsterEntities": "common/init/entity/MonsterEntities.java",
    "TEAnimals": "common/init/entity/CritterEntities.java",
    "TENpcEntities": "common/init/entity/NpcEntities.java",
    "TEBossEntities": "common/init/entity/BossEntities.java",
    "TEEffects": "common/init/ModEffects.java",
    "TETags": "common/init/ModTags.java",
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

# 成员级改名：`TE简单名.成员` -> `目标简单名.新成员`
MEMBER_OVERRIDES = {
    "TEProjectileEntities.FIRE_IMP_PROJ": "ModEntities.FIRE_IMP_PROJECTILE",
}

# 已确认先不处理的 TE 类（C 类，等决策表）：出现这些类的文件整体跳过
DEFERRED = {
    "TerraEntity", "TEUtils", "TEEntities2", "IEffectStrategy", "TEEnchantmentHelper",
    "AbstractTerraNPC", "AbstractTerraBossBase", "AbstractMonster", "BaseWorm", "BaseWormPart",
    "BoneSerpent", "LittleHornet", "SurefaceWorm", "WoodenMimic", "DemonEye", "DemonEyeVariant",
    "FlyMonsterPrefab", "BaseSlime", "GoldenSlime", "SimpleVariantAnimal", "VariantsTextureMaps",
    "IMinion", "ISummonMob", "AbstractSummonMob", "ITrackType", "IVariant", "BasisTrack", "SimpleTrack",
    "ITradeHolder", "ITradeLock", "NPCTradeManager", "TradeParams", "UpdateNPCTradePacket",
    "IHouseDetector", "NPCMood", "AnglerNPC", "NPCEvent", "ILeftClickStateItem", "IAttackableProjectile",
    "ICollisionAttackEntity", "IPlayer", "IZombie", "CuriosHelper", "RecipeDrawerUtils", "AimUtils",
    "DeathAnimOptions", "WeaponStorage", "KeyframeAnimation", "AbstractBufferManager", "DebugBlocksHelper",
    "BaseEntityRenderer", "GeoNormalRenderer", "GeoNegativeVolumeRenderer", "GeoWormRenderer",
    "WallOfFleshRenderer", "TETradeScreen", "SpitParticle", "WallOfFlesh", "HillOfFlesh",
    "BrainOfCthulhu", "DungeonGuardian", "EaterOfWorlds", "QueenBee", "renderDebugBlock",
}

IMPORT_RE = re.compile(r"^(\s*)import\s+(static\s+)?" + re.escape(TE_PKG) + r"\.([\w\.]*\*?)\s*;(\s*)$")


def read_text(path):
    with open(path, "r", encoding="utf-8", errors="surrogateescape", newline="") as fh:
        return fh.read()


def split_eol(text):
    """保留原换行风格：本仓库源码是 CRLF，不能通用换行读 + LF 写，否则整个文件的行尾都被改。"""
    if "\r\n" in text:
        return text.split("\r\n"), "\r\n"
    return text.split("\n"), "\n"


def nested_region(text, cls):
    """返回目标文件里 `class <cls>` 的块体（花括号计数，粗略），找不到返回 None。"""
    m = re.search(r"\b(?:class|interface|enum|record)\s+" + re.escape(cls) + r"\b", text)
    if not m:
        return None
    i = text.find("{", m.end())
    if i < 0:
        return None
    depth = 0
    for j in range(i, len(text)):
        c = text[j]
        if c == "{":
            depth += 1
        elif c == "}":
            depth -= 1
            if depth == 0:
                return text[i:j + 1]
    return text[i:]


def write_text(path, text):
    with open(path, "w", encoding="utf-8", errors="surrogateescape", newline="") as fh:
        fh.write(text)


def target_fqn(mod_dir, rel):
    p = os.path.join(mod_dir, rel.replace("/", os.sep))
    if not os.path.isfile(p):
        return None, None
    pkg = None
    with open(p, encoding="utf-8", errors="replace") as fh:
        for ln in fh:
            m = re.match(r"\s*package\s+([\w\.]+)\s*;", ln)
            if m:
                pkg = m.group(1)
                break
    simple = os.path.basename(rel)[:-5] if rel.endswith(".java") else os.path.basename(rel)
    return (pkg + "." + simple) if pkg else None, p


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--repo", default=".")
    ap.add_argument("--file", default=None)
    ap.add_argument("--apply", action="store_true")
    ap.add_argument("--force", action="store_true", help="连成员未解析的文件一起改")
    ap.add_argument("--report", default=os.path.join(os.environ.get("TEMP", "."), "te_repoint_report.txt"))
    args = ap.parse_args()

    repo = os.path.abspath(args.repo)
    mod121 = os.path.join(repo, "ConfluenceOtherworld", "src", "main", "java", "org", "confluence", "mod")
    if not os.path.isdir(mod121):
        sys.exit("找不到 1.21 主模组源码目录: " + mod121)

    # 预解析目标 FQN
    fqns, bodies = {}, {}
    for simple, rel in TARGETS.items():
        fqn, path = target_fqn(mod121, rel)
        if fqn is None:
            print("[警告] 目标文件缺失：%s -> %s（该类的引用本次无法改指）" % (simple, rel))
            continue
        fqns[simple] = fqn
        if path:
            bodies[simple] = read_text(path)

    files = []
    for root, _d, names in os.walk(mod121):
        for n in names:
            if n.endswith(".java"):
                p = os.path.join(root, n)
                rel = os.path.relpath(p, mod121).replace(os.sep, "/")
                if args.file and args.file.lower() not in rel.lower():
                    continue
                files.append((rel, p))

    report = []
    changed, skipped, blocked, unresolved, stats = [], [], [], [], OrderedDict()
    for rel, path in sorted(files):
        text = read_text(path)
        if TE_PKG not in text:
            continue
        lines, eol = split_eol(text)
        imports = OrderedDict()          # 简单名 -> (行号, fqn, 是否静态)
        wildcard_lines = []
        for i, ln in enumerate(lines):
            m = IMPORT_RE.match(ln)
            if m:
                fqn = m.group(3)
                if fqn.endswith("*"):    # 通配符 import 无法机械改指，必须整文件交人工
                    wildcard_lines.append("%s:%d  %s" % (rel, i + 1, fqn))
                    continue
                imports[fqn.split(".")[-1]] = (i, fqn, bool(m.group(2)))
        if wildcard_lines:
            blocked.append((rel, "通配符 TE import（无简单名，不可机械改指）: " + ", ".join(wildcard_lines)))
            continue
        if not imports:
            continue
        unknown = [s for s in imports if s not in TARGETS and s not in DEFERRED]
        defer = [s for s in imports if s in DEFERRED]
        notarget = [s for s in imports if s in TARGETS and s not in fqns]
        if unknown:
            blocked.append((rel, "映射表未覆盖: " + ", ".join(unknown)))
            continue
        if notarget:
            blocked.append((rel, "目标文件尚不存在（等该批落地或需新判定）: " + ", ".join(notarget)))
            continue
        if defer:
            skipped.append((rel, ", ".join(defer)))
            continue

        # 成员解析检查
        pending = []
        for simple in imports:
            body = bodies.get(simple, "")
            for m in re.finditer(r"\b" + re.escape(simple) + r"\.((?:\w+\.)*\w+)", text):
                path = m.group(1).split(".")
                if (simple + "." + path[0]) in MEMBER_OVERRIDES:
                    continue
                if not body:
                    continue
                # 嵌套类形态 Class.Nested.MEMBER：必须在目标文件的 Nested 块内查到 MEMBER。
                # （只查 Nested 名会把 Items 之类当成员而空过校验——曾因此把
                #  TETags.Items.CURIOS_* 改成不存在的 ModTags.Items.CURIOS_*。）
                if len(path) >= 2:
                    region = nested_region(body, path[0])
                    if region is not None:
                        if not re.search(r"\b" + re.escape(path[-1]) + r"\b", region):
                            pending.append("%s.%s" % (simple, ".".join(path)))
                        continue
                    # region 找不到 → 是方法链（X.Y.get()），只校验第一段
                if not re.search(r"\b" + re.escape(path[0]) + r"\b", body):
                    pending.append("%s.%s" % (simple, path[0]))
        pending = sorted(set(pending))
        if pending and not args.force:
            unresolved.append((rel, ", ".join(pending)))
            continue

        new_lines = list(lines)
        n_imp = n_use = 0
        replaced_names = {}
        for simple, (idx, fqn, is_static) in imports.items():
            if is_static:
                continue
            tfqn = fqns[simple]
            tsimple = tfqn.split(".")[-1]
            replaced_names[simple] = tsimple
            if tfqn == fqn:
                continue
            already = any(l.strip() == "import %s;" % tfqn for l in lines)
            ind = re.match(r"\s*", new_lines[idx]).group(0)
            if already:
                new_lines[idx] = None           # 删掉重复 import
                n_imp += 1
            else:
                new_lines[idx] = "%simport %s;" % (ind, tfqn)
                n_imp += 1
        # 成员改名
        for key, val in MEMBER_OVERRIDES.items():
            old_simple, old_mem = key.split(".")
            if old_simple not in replaced_names:
                continue
            new_simple, new_mem = val.split(".")
            pat = re.compile(r"\b" + re.escape(old_simple) + r"\." + re.escape(old_mem) + r"\b")
            for i, ln in enumerate(new_lines):
                if ln is None or ln.lstrip().startswith("import "):
                    continue
                if pat.search(ln):
                    new_lines[i] = pat.sub(new_simple + "." + new_mem, ln)
                    n_use += 1
        # 其余限定名替换
        for i, ln in enumerate(new_lines):
            if ln is None or ln.lstrip().startswith("import "):
                continue
            orig = ln
            for simple, tsimple in replaced_names.items():
                if simple == tsimple:
                    continue
                ln = re.sub(r"\b" + re.escape(simple) + r"\b", tsimple, ln)
            if ln != orig:
                new_lines[i] = ln
                n_use += 1
        new_lines = [l for l in new_lines if l is not None]
        new_text = eol.join(new_lines)
        if new_text == text:
            continue
        if TE_PKG in new_text:
            blocked.append((rel, "替换后仍残留 terraentity 字样（可能有 FQN 直写）"))
            continue
        changed.append(rel)
        stats[rel] = (n_imp, n_use)
        diff = "".join(difflib.unified_diff(lines, new_lines, fromfile="a/" + rel, tofile="b/" + rel, lineterm="", n=2))
        report.append("=" * 78 + "\n" + rel + "  (import %d / 使用 %d)\n" % (n_imp, n_use) + diff)
        if args.apply:
            write_text(path, new_text)

    print("=" * 78)
    print("可改指文件 %d 个：import 替换 %d 行，使用替换 %d 行"
          % (len(changed), sum(v[0] for v in stats.values()), sum(v[1] for v in stats.values())))
    for rel, (a, b) in stats.items():
        print("   %-72s import %3d  使用 %3d" % (rel, a, b))
    if unresolved:
        print("\n[成员未解析，默认跳过] %d 个文件：" % len(unresolved))
        for rel, mem in unresolved:
            print("   %s  ::  %s" % (rel, mem))
    if skipped:
        print("\n[含待判定 C 类，本次跳过] %d 个文件：" % len(skipped))
        for rel, sym in skipped:
            print("   %s  ::  %s" % (rel, sym))
    if blocked:
        print("\n[无法自动处理] %d 个文件：" % len(blocked))
        for rel, why in blocked:
            print("   %s  ::  %s" % (rel, why))
    with open(args.report, "w", encoding="utf-8", newline="\n") as fh:
        fh.write("".join(report))
    print("\n完整 diff -> " + args.report + ("（已落盘）" if args.apply else "（dry-run，未改动任何文件）"))
    return 0


if __name__ == "__main__":
    sys.exit(main())
