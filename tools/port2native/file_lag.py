#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""文件级滞后报告：1.21 相对 1.20 的「内容欠账」清单。

为什么需要
----------
`commit_coverage.py` 有两个盲区，实测下来一起把 124 个提交变成了「无覆盖率条目」：
  1. 只要一行提到 Port 类型（`IPort*` / `Port*` / `org.mesdag.portlib.*`）就不计入分母；
     1.20 后期几乎全部新增代码都引用 Port 类型 → 整个提交被跳过（例如 f4ffadaa7，+10081 行）。
  2. `total == 0` 的提交直接不产出条目，于是台账里最需要判断的那些行反而没有机器结论。

本工具换成**文件级**、**Port 免疫**的度量：对 1.20 侧每个 java 文件，取其「有效行」
（去掉 import/package、纯符号行、Port 引用行、平台 API 引用行），与 1.21 侧映射到的同名文件
求交，得到

    overlap = |有效行(1.20) ∩ 有效行(1.21)| / |有效行(1.20)|

- 1.21 找不到对应文件 → `MISSING`（新子系统/新内容）
- overlap < 0.50      → `LAGGING`（1.21 明显落后）
- 0.50 ~ 0.85         → `PARTIAL`
- ≥ 0.85              → `IN-SYNC`
- 有效行 < 3          → `TINY`（忽略）

路径映射与 commit_coverage.py 一致：精确同路径 → 同模块同名 → 跨模块同名（1.20 把 TerraEntity /
TerraGuns 的代码内联进了主模组，所以跨模块同名是**正常**映射，不是错误）。

再把结果按提交汇总（`--log`，`git log --numstat`）：
- 该提交改过的路径若在 1.20 HEAD 已经不存在 → `DEAD`（1.20 自己后来删掉了，无需移植）
- 其余按 有效行权重 汇总出 `LAGGING+MISSING` 占比 → 提交级 `NEEDS-PORT / PARTIAL / COVERED`

用法：
    python file_lag.py --src <1.20仓库> --ref <1.21仓库> --out notes \
        [--src-modules ConfluenceOtherworld,Confluence-Magic-Lib,TerraCurio,TerraFurniture] \
        [--ref-modules ...,TerraEntity,TerraGuns] \
        [--log <gitlog numstat 文件>] [--range 795ac9ccc..18221c338]
"""

from __future__ import annotations

import argparse
import json
import pathlib
import re
import subprocess
import sys
from collections import defaultdict

# 改动行里出现的 Port 引用（这类行对新版本无意义）
PORT_REF_RE = re.compile(r"org\.mesdag\.portlib|PortLib\.extensions|\b(?:IPort|Port)[A-Z]\w*")
# 平台 API 引用（Forge / NeoForge / Mojang 内部 / maven 库），逐行视为「胶水」，不计入内容比对
PLATFORM_REF_RE = re.compile(
    r"net\.minecraftforge|net\.neoforged|net\.minecraft\.|com\.mojang\.|"
    r"com\.tterrag\.|net\.blamejared\.|org\.apache\.|it\.unimi\.dsi\.|com\.google\."
)
SKIP_DIR_HINTS = ("/build/", "/run", "/generated/", "/.gradle/")

HEADER_RE = re.compile(r"^C\|([0-9a-f]{7,40})")
NUMSTAT_RE = re.compile(r"^(\d+|-)\t(\d+|-)\t(.*)$")


def read_text_any(path: pathlib.Path) -> str:
    raw = path.read_bytes()
    # utf-8-sig 必须在 utf-8 之前：Windows 侧（PowerShell Out-File）写的日志带 BOM，
    # 先用 utf-8 解会把 BOM 留在首字符处，于是**第一条** `C|<hash>` 头永远匹配不上
    # （踩过一次：18221c338 整条提交凭空消失）。
    for enc in ("utf-8-sig", "utf-16", "utf-8"):
        try:
            text = raw.decode(enc)
            if "\x00" not in text[:400]:
                return text
        except UnicodeDecodeError:
            continue
    return raw.decode("utf-8", errors="ignore")


def sig_lines(text: str) -> set[str]:
    """有效行集合：数字母数字、非 import/package、不含 Port 与平台 API 引用。"""
    out: set[str] = set()
    for line in text.splitlines():
        s = "".join(line.split())
        if not s or s.startswith("//") or s.startswith("*") or s.startswith("/*"):
            continue
        if s.startswith("import") or s.startswith("package"):
            continue
        if PORT_REF_RE.search(s) or PLATFORM_REF_RE.search(s):
            continue
        if not any(ch.isalnum() for ch in s):
            continue
        out.add(s)
    return out


def git_lines(repo: pathlib.Path, args: list[str]) -> list[str]:
    p = subprocess.run(["git", "-C", str(repo)] + args, capture_output=True)
    if p.returncode != 0:
        sys.stderr.write(p.stderr.decode("utf-8", "ignore") + "\n")
        raise SystemExit(f"git 失败: {' '.join(args)}")
    return p.stdout.decode("utf-8", "ignore").splitlines()


def tree_java(repo: pathlib.Path, modules: list[str]) -> list[str]:
    """遍历工作树取 java 文件。

    必须走工作树而不是 `git ls-files`：子模块（1.21 的 TerraEntity / TerraGuns、两侧的
    Confluence-Magic-Lib 等）在主仓库里只是 gitlink，`ls-files` 看不到它们的文件
    （踩过一次：114 个 common/entity/monster/*.java 被误判成 MISSING）。
    """
    out: list[str] = []
    for mod in modules:
        base = repo / mod / "src"
        if not base.is_dir():
            continue
        for p in base.rglob("*.java"):
            rel = p.relative_to(repo).as_posix()
            if any(h in rel for h in SKIP_DIR_HINTS):
                continue
            out.append(rel)
    return out


class RefIndex:
    def __init__(self, root: pathlib.Path, files: list[str]) -> None:
        self.root = root
        self.files = files
        self.by_name: dict[str, list[str]] = defaultdict(list)
        for rel in files:
            self.by_name[rel.rsplit("/", 1)[-1]].append(rel)
        self._cache: dict[str, set[str] | None] = {}

    def lines_of(self, rel: str) -> set[str] | None:
        if rel in self._cache:
            return self._cache[rel]
        p = self.root / rel
        if not p.is_file():
            self._cache[rel] = None
            return None
        try:
            s = sig_lines(read_text_any(p))
        except OSError:
            s = None
        self._cache[rel] = s
        return s

    def resolve(self, rel: str, module: str) -> tuple[str | None, str]:
        if (self.root / rel).is_file():
            return rel, "exact"
        name = rel.rsplit("/", 1)[-1]
        cands = self.by_name.get(name, [])
        same = [c for c in cands if c.startswith(module + "/")]
        if len(same) == 1:
            return same[0], "by-name"
        if len(same) > 1:
            return same[0], f"by-name-ambiguous({len(same)})"
        if len(cands) == 1:
            return cands[0], "cross-module"
        if len(cands) > 1:
            return cands[0], f"cross-module-ambiguous({len(cands)})"
        return None, "missing"


def verdict_of(overlap: float) -> str:
    if overlap < 0.50:
        return "LAGGING"
    if overlap < 0.85:
        return "PARTIAL"
    return "IN-SYNC"


def build_file_report(src: pathlib.Path, ref_root: pathlib.Path,
                      src_modules: list[str], ref_modules: list[str]) -> dict:
    src_files = tree_java(src, src_modules)
    ref = RefIndex(ref_root, tree_java(ref_root, ref_modules))
    src_present = set(src_files)
    rows = []
    for rel in sorted(src_files):
        module = rel.split("/", 1)[0]
        target, how = ref.resolve(rel, module)
        s20 = sig_lines(read_text_any(src / rel))
        if target is None:
            rows.append({"path": rel, "target": None, "how": "missing", "s20": len(s20),
                         "s21": 0, "inter": 0, "overlap": 0.0, "verdict": "MISSING",
                         "refExtra": 0})
            continue
        s21 = ref.lines_of(target) or set()
        if len(s20) < 3:
            rows.append({"path": rel, "target": target, "how": how, "s20": len(s20),
                         "s21": len(s21), "inter": len(s20 & s21), "overlap": 1.0,
                         "verdict": "TINY", "refExtra": len(s21 - s20)})
            continue
        inter = len(s20 & s21)
        ov = inter / len(s20)
        rows.append({"path": rel, "target": target, "how": how, "s20": len(s20), "s21": len(s21),
                     "inter": inter, "overlap": round(ov, 3), "verdict": verdict_of(ov),
                     "refExtra": len(s21 - s20)})
    return {"rows": rows, "srcPresent": sorted(src_present), "refFiles": ref.files}


def parse_numstat(path: pathlib.Path) -> dict[str, dict[str, int]]:
    """git log --numstat 补丁 -> {commit: {path: added}}（只保留计入的路径，added 为 '-' 记 0）。"""
    commits: dict[str, dict[str, int]] = {}
    cur = None
    for line in read_text_any(path).splitlines():
        line = line.rstrip("\n")
        m = HEADER_RE.match(line)
        if m:
            cur = m.group(1)
            commits.setdefault(cur, {})
            continue
        if cur is None:
            continue
        nm = NUMSTAT_RE.match(line)
        if nm:
            added = 0 if nm.group(1) == "-" else int(nm.group(1))
            commits[cur][nm.group(3)] = added
    return commits


def rollup_commits(file_rows: dict[str, dict], commits: dict[str, dict[str, int]],
                   src_present: set[str]) -> list[dict]:
    out = []
    for h, paths in commits.items():
        bucket: dict[str, int] = defaultdict(int)
        dead_portlib = dead_other = 0
        for path, added in paths.items():
            if not path.endswith(".java"):
                continue
            if path not in src_present:
                if path.startswith("PortLib/"):
                    dead_portlib += 1
                else:
                    dead_other += 1
                continue
            r = file_rows.get(path)
            if r is None:
                continue
            w = max(added, 1)
            bucket[r["verdict"]] += w
        total = sum(bucket.values())
        dead = dead_portlib + dead_other
        if total == 0:
            # PortLib 是 1.20 专有的模拟层（1.21 侧不需要），和"1.20 自己后来删掉的代码"要分开：
            # 前者永远不移植，后者无需移植。
            if dead and dead_other == 0:
                verdict = "PORTLIB-ONLY"
            elif dead:
                verdict = "DEAD-ONLY"
            else:
                verdict = "NO-JAVA"
        else:
            bad = bucket.get("LAGGING", 0) + bucket.get("MISSING", 0)
            if bad / total >= 0.5:
                verdict = "NEEDS-PORT"
            elif (bad + bucket.get("PARTIAL", 0)) / total >= 0.3:
                verdict = "PARTIAL"
            else:
                verdict = "COVERED"
        out.append({"hash": h, "verdict": verdict, "deadPaths": dead,
                    "deadPortLib": dead_portlib, "deadOther": dead_other,
                    "weights": dict(bucket), "total": total})
    return out


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description="1.21 相对 1.20 的文件级内容欠账报告")
    ap.add_argument("--src", required=True, help="1.20 仓库根")
    ap.add_argument("--ref", required=True, help="1.21 仓库根")
    ap.add_argument("--out", required=True)
    ap.add_argument("--src-modules", default="ConfluenceOtherworld,Confluence-Magic-Lib,TerraCurio,TerraFurniture")
    ap.add_argument("--ref-modules", default="ConfluenceOtherworld,Confluence-Magic-Lib,TerraCurio,TerraFurniture,TerraEntity,TerraGuns")
    ap.add_argument("--log", default="", help="git log --numstat 文件（用于提交级汇总）")
    ap.add_argument("--range", default="")
    args = ap.parse_args(argv)

    src = pathlib.Path(args.src).resolve()
    ref_root = pathlib.Path(args.ref).resolve()
    out_dir = pathlib.Path(args.out).resolve()
    out_dir.mkdir(parents=True, exist_ok=True)

    rep = build_file_report(src, ref_root, [m.strip() for m in args.src_modules.split(",") if m.strip()],
                            [m.strip() for m in args.ref_modules.split(",") if m.strip()])
    rows = rep["rows"]
    counts = defaultdict(int)
    for r in rows:
        counts[r["verdict"]] += 1
    print(json.dumps(dict(counts), ensure_ascii=False))

    md = ["# 1.21 相对 1.20 的文件级内容欠账（Port 免疫度量）", "",
          f"- 1.20 侧参与比对的 java 文件 **{len(rows)}**；1.21 侧参照 **{len(rep['refFiles'])}**",
          "- `overlap = |有效行(1.20) ∩ 有效行(1.21)| / |有效行(1.20)|`；有效行已剔除 import/package、"
          "纯符号行、Port 引用行与平台 API 引用行",
          "- 判定：无对应文件 `MISSING`；<50% `LAGGING`；50~85% `PARTIAL`；≥85% `IN-SYNC`；有效行<3 `TINY`",
          f"- 结果：**" + " / ".join(f"{k} {counts[k]}" for k in
                                     ("MISSING", "LAGGING", "PARTIAL", "IN-SYNC", "TINY")) + "**",
          ""]
    for verdict in ("MISSING", "LAGGING", "PARTIAL"):
        sub = [r for r in rows if r["verdict"] == verdict]
        sub.sort(key=lambda r: (r["overlap"], -r["s20"]))
        md += [f"## {verdict}（{len(sub)}）", "",
               "| 1.20 文件 | 1.21 对应 | 映射 | 有效行 | 交集 | overlap | 1.21 独有 |", "|---|---|---|---|---|---|---|"]
        for r in sub[:200]:
            md.append(f"| `{r['path'].replace('ConfluenceOtherworld/src/main/java/org/confluence/mod/', 'CO:')}` | "
                      f"`{(r['target'] or '').replace('ConfluenceOtherworld/src/main/java/org/confluence/mod/', 'CO:')}` | "
                      f"{r['how'].split('(')[0]} | {r['s20']} | {r['inter']} | {r['overlap']:.0%} | {r['refExtra']} |")
        if len(sub) > 200:
            md.append(f"| … | | | | | | 另有 {len(sub) - 200} 个 |")
        md.append("")
    (out_dir / "FILE-LAG.md").write_text("\n".join(md), encoding="utf-8")
    payload = {"srcModules": args.src_modules, "refModules": args.ref_modules,
               "counts": dict(counts), "rows": rows}
    (out_dir / "file-lag.json").write_text(json.dumps(payload, ensure_ascii=False, indent=2),
                                           encoding="utf-8")
    print(f"报告: {out_dir / 'FILE-LAG.md'}")

    if args.log:
        commits = parse_numstat(pathlib.Path(args.log))
        file_rows = {r["path"]: r for r in rows}
        roll = rollup_commits(file_rows, commits, set(rep["srcPresent"]))
        ccounts = defaultdict(int)
        for r in roll:
            ccounts[r["verdict"]] += 1
        print(json.dumps(dict(ccounts), ensure_ascii=False))
        (out_dir / "commit-lag.json").write_text(
            json.dumps({"range": args.range, "counts": dict(ccounts), "commits": roll},
                       ensure_ascii=False, indent=2), encoding="utf-8")
        cm = ["# 提交级欠账汇总（file-lag 加权）", "",
              f"- 范围 `{args.range}`；提交 **{len(roll)}**",
              f"- 结果：**" + " / ".join(f"{k} {ccounts[k]}" for k in
                                        ("NEEDS-PORT", "PARTIAL", "COVERED", "DEAD-ONLY", "NO-JAVA")) + "**",
              "- `DEAD-ONLY`：该提交改过的 java 文件在 1.20 HEAD 已全部不存在（1.20 自己后来删除了）",
              "", "| 提交 | 判定 | 有效权重 | LAGGING | MISSING | PARTIAL | IN-SYNC | DEAD |", "|---|---|---|---|---|---|---|---|"]
        for r in sorted(roll, key=lambda r: r["hash"]):
            w = r["weights"]
            cm.append(f"| `{r['hash'][:9]}` | {r['verdict']} | {r['total']} | {w.get('LAGGING',0)} | "
                      f"{w.get('MISSING',0)} | {w.get('PARTIAL',0)} | {w.get('IN-SYNC',0)} | {r['deadPaths']} |")
        (out_dir / "COMMIT-LAG.md").write_text("\n".join(cm), encoding="utf-8")
        print(f"报告: {out_dir / 'COMMIT-LAG.md'}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
