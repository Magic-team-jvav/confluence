#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""提交覆盖率检查：该提交"新增的行"有多少已经存在于 1.21 侧的对应文件里。

为什么需要
----------
前 9 行逐行判定的实测结论：**大部分 `part N` 提交本质是「1.20 追赶 1.21 的 API」**
——把原生写法换成 Port 写法、把自己的方法签名对齐 1.21 的库、把 datagen 组织对齐 1.21 的形态。
这些改动对 1.21 而言**已经是既成事实**（1.21 侧本来就是正确的原生形态），因此是 `COVERED` / `SKIP`。

判据（逐提交）：
    coverage = (在 1.21 侧对应文件里能找到的新增行数) / (该提交的新增行数)
- 覆盖率 ≥ --cover-threshold（默认 0.8）→ 判 `COVERED`
- 覆盖率 ≤ --port-threshold（默认 0.3）→ 判 `NEEDS-PORT`
- 中间 → `UNCLEAR`（人工看）

路径映射：1.20 的相对路径优先精确命中 1.21 同名路径；否则按 **同名文件** 在 1.21 的同一顶层模块里找
（1.21 有过包移动，例如 LibEntityUtils→LibUtils），找不到就整文件计入"未覆盖"。

输入：`git log -p` 补丁文件（只含 java/json，避免二进制把统计冲淡）
    git -C <1.20仓库> log --reverse --no-merges --unified=0 --no-color \
        --format="C|%H|%ad|%s" --date=short 795ac9ccc..HEAD -- "*.java" "*.json" > patches.txt

用法：
    python commit_coverage.py --log patches.txt --ref <1.21仓库> --out <notes目录> \
        [--modules ConfluenceOtherworld,Confluence-Magic-Lib,TerraCurio,TerraFurniture]
"""

from __future__ import annotations

import argparse
import json
import pathlib
import re
import sys
from collections import defaultdict

HEADER_RE = re.compile(r"^C\|([0-9a-f]{7,40})\|(\d{4}-\d{2}-\d{2})\|(.*)$")
DIFF_RE = re.compile(r"^diff --git a/(.*?) b/(.*)$")
HUNK_RE = re.compile(r"^@@")
# 改动行里出现的 Port 引用（这类行对新版本无意义，不计入覆盖率分母）
PORT_REF_RE = re.compile(r"org\.mesdag\.portlib|\bPortLib\.extensions|\b(?:IPort|Port)[A-Z]\w*")
# 刻意忽略的路径（Q12 integration；以及纯资源）
IGNORE_HINTS = ("/integration/",)


def norm(line: str) -> str:
    return "".join(line.split())


def read_text_any(path: pathlib.Path) -> str:
    raw = path.read_bytes()
    for enc in ("utf-8-sig", "utf-16", "utf-8"):
        try:
            text = raw.decode(enc)
            if "\x00" not in text[:400]:
                return text
        except UnicodeDecodeError:
            continue
    return raw.decode("utf-8", errors="ignore")


class Ref:
    """1.21 侧参照：路径 -> 归一化行集合（**惰性读取**，只读补丁引用到的文件）；以及 basename -> 路径候选。"""

    def __init__(self, root: pathlib.Path, modules: list[str]) -> None:
        self.root = root
        self.modules = modules
        self._cache: dict[str, set[str] | None] = {}
        self.by_name: dict[str, list[str]] = defaultdict(list)
        self._index_built = False

    def build_index(self) -> None:
        """只建"文件名 -> 路径"的索引（不读内容），用于 1.21 侧有过包移动时的兜底匹配。"""
        if self._index_built:
            return
        exts = (".java", ".json")
        for mod in self.modules:
            base = self.root / mod / "src"
            if not base.is_dir():
                continue
            for p in base.rglob("*"):
                if not p.is_file() or p.suffix not in exts:
                    continue
                rel = p.relative_to(self.root).as_posix()
                if "/build/" in rel or "/run" in rel or "/generated/" in rel:
                    continue        # generated 是产物，不算参照
                self.by_name[p.name].append(rel)
        self._index_built = True

    def lines_of(self, rel: str) -> set[str] | None:
        if rel in self._cache:
            return self._cache[rel]
        p = self.root / rel
        if not p.is_file():
            self._cache[rel] = None
            return None
        try:
            text = read_text_any(p)
        except OSError:
            self._cache[rel] = None
            return None
        lines = {norm(l) for l in text.splitlines() if l.strip()}
        self._cache[rel] = lines
        return lines

    def resolve(self, rel: str, module: str) -> tuple[str | None, str]:
        """把 1.20 的相对路径映射到 1.21 侧的路径。返回 (路径, 方式)。

        顺序：精确同路径 → 同模块同名 → **任意模块同名**（1.21 的代码可能被放在别的模块里，
        例如 1.20 把实体内联进主模组、而 1.21 那份代码在 TerraEntity）。
        """
        if (self.root / rel).is_file():
            return rel, "exact"
        self.build_index()
        name = rel.rsplit("/", 1)[-1]
        same = [c for c in self.by_name.get(name, []) if c.startswith(module + "/")]
        if len(same) == 1:
            return same[0], "by-name"
        if len(same) > 1:
            return same[0], f"by-name-ambiguous({len(same)})"
        other = self.by_name.get(name, [])
        if len(other) == 1:
            return other[0], "by-name-cross-module"
        if len(other) > 1:
            return other[0], f"by-name-cross-module-ambiguous({len(other)})"
        return None, "missing"


def parse_patch(path: pathlib.Path) -> list[dict]:
    commits: list[dict] = []
    cur: dict | None = None
    cur_file: dict | None = None
    in_ignored = False
    for line in read_text_any(path).splitlines():
        m = HEADER_RE.match(line)
        if m:
            cur = {"hash": m.group(1), "date": m.group(2), "subject": m.group(3), "files": []}
            commits.append(cur)
            cur_file = None
            continue
        if cur is None:
            continue
        dm = DIFF_RE.match(line)
        if dm:
            rel = dm.group(2)
            in_ignored = any(h in rel for h in IGNORE_HINTS)
            if not in_ignored:
                cur_file = {"path": rel, "added": []}
                cur["files"].append(cur_file)
            else:
                cur_file = None
            continue
        if cur_file is None or HUNK_RE.match(line) or line.startswith("+++") or line.startswith("---"):
            continue
        if line.startswith("+"):
            content = line[1:]
            if content.strip() and not PORT_REF_RE.search(content):
                cur_file["added"].append(content)
    return [c for c in commits if c["files"]]


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description="提交覆盖率检查（新增行在 1.21 已存在的比例）")
    ap.add_argument("--log", required=True)
    ap.add_argument("--ref", required=True, help="1.21 仓库根")
    ap.add_argument("--out", required=True)
    ap.add_argument("--base", default="")
    ap.add_argument("--head", default="")
    ap.add_argument("--modules", default="ConfluenceOtherworld,Confluence-Magic-Lib,TerraCurio,TerraFurniture")
    ap.add_argument("--cover-threshold", type=float, default=0.8)
    ap.add_argument("--port-threshold", type=float, default=0.3)
    args = ap.parse_args(argv)

    modules = [m.strip() for m in args.modules.split(",") if m.strip()]
    ref = Ref(pathlib.Path(args.ref).resolve(), modules)
    commits = parse_patch(pathlib.Path(args.log).resolve())

    results = []
    for c in commits:
        total = matched = 0
        details = []
        for f in c["files"]:
            added = f["added"]
            if not added:
                continue
            module = f["path"].split("/", 1)[0]
            target, how = ref.resolve(f["path"], module)
            pool = ref.lines_of(target) if target else None
            if pool is None:
                total += len(added)
                details.append({"path": f["path"], "how": how, "added": len(added), "matched": 0})
                continue
            hit = sum(1 for l in added if norm(l) in pool)
            total += len(added)
            matched += hit
            details.append({"path": f["path"], "target": target, "how": how,
                            "added": len(added), "matched": hit})
        if total == 0:
            continue
        cov = matched / total
        verdict = ("COVERED" if cov >= args.cover_threshold
                   else "NEEDS-PORT" if cov <= args.port_threshold else "UNCLEAR")
        results.append({"hash": c["hash"], "date": c["date"], "subject": c["subject"],
                        "addedLines": total, "matchedOn121": matched,
                        "coverage": round(cov, 3), "verdict": verdict, "files": details})

    counts = defaultdict(int)
    for r in results:
        counts[r["verdict"]] += 1

    out_dir = pathlib.Path(args.out).resolve()
    out_dir.mkdir(parents=True, exist_ok=True)
    md = ["# 提交覆盖率检查（新增行是否已在 1.21 侧存在）", "",
          f"- 范围：`{args.base or '<base>'}..{args.head or '<head>'}`；有代码/数据改动的提交 **{len(results)}** 个",
          f"- 判据：`coverage = 新增行在 1.21 对应文件里已存在的比例`；≥{args.cover_threshold} → `COVERED`，"
          f"≤{args.port_threshold} → `NEEDS-PORT`，中间 `UNCLEAR`",
          f"- 结果：**COVERED {counts['COVERED']} / NEEDS-PORT {counts['NEEDS-PORT']} / UNCLEAR {counts['UNCLEAR']}**",
          "",
          "> 注意：这是**机器判定**，用于把台账从几百行收窄到可执行的短名单；`COVERED` 仍建议抽查，",
          "> `NEEDS-PORT` 才是真正要动手的部分。",
          ""]

    for verdict in ("NEEDS-PORT", "UNCLEAR", "COVERED"):
        rows = [r for r in results if r["verdict"] == verdict]
        rows.sort(key=lambda r: r["coverage"] if verdict == "NEEDS-PORT" else -r["coverage"])
        md += [f"## {verdict}（{len(rows)}）", "",
               "| 提交 | 日期 | 说明 | 新增行 | 已存在 | 覆盖率 |", "|---|---|---|---|---|---|"]
        for r in rows[:120]:
            md.append(f"| `{r['hash'][:9]}` | {r['date']} | {r['subject'][:44]} | {r['addedLines']} | "
                      f"{r['matchedOn121']} | {r['coverage']:.0%} |")
        if len(rows) > 120:
            md.append(f"| … | | 另有 {len(rows) - 120} 个 | | | |")
        md.append("")

    (out_dir / "COMMIT-COVERAGE.md").write_text("\n".join(md), encoding="utf-8")
    (out_dir / "commit-coverage.json").write_text(
        json.dumps({"range": {"base": args.base, "head": args.head}, "counts": dict(counts),
                    "results": results}, ensure_ascii=False, indent=2),
        encoding="utf-8")
    print(json.dumps(dict(counts), ensure_ascii=False, indent=2))
    print(f"报告: {out_dir / 'COMMIT-COVERAGE.md'}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
