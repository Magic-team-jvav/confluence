"""掉落表覆盖比对：1.20 HEAD vs 1.21 HEAD 的实体掉落条目。

用途：找"实体在 1.21 没有掉落表"的缺口（例：`confluence:zombie`/`giant_bat`/`unicorn` 等 100+ 个）。

口径：
  1) 从两树的 `common/data/gen/loot/EntitySubProvider.java` 抽 `XxxEntities.CONST` 形式的实体引用；
  2) 把常量名解析成注册 id（在该侧的 `common/init/entity/*.java` 里找 `CONST = ... register*("id"`）；
  3) 1.21 侧还查 on-disk 构建产物 `ConfluenceOtherworld/src/generated/resources/data/confluence/loot_table/entities/<id>.json`
     是否存在（`src/generated` 被 gitignore，属构建产物；只作旁证）；
  4) 输出：1.20 有、1.21 无的实体（按实体类分组），并标注 1.21 是否连实体本身都不存在。

用法:
  python tools/port2native/loot_coverage.py [--out 文件] [--provider 相对路径]
"""
import argparse
import re
import subprocess
from collections import defaultdict
from pathlib import Path

REPO_120 = Path(r"D:\Minecraft\1.20forge\confluence")
REPO_121 = Path(r"D:\Minecraft\1.21neoforge\confluence")
DEFAULT_PROVIDER = "ConfluenceOtherworld/src/main/java/org/confluence/mod/common/data/gen/loot/EntitySubProvider.java"
ENTITY_FILES = "ConfluenceOtherworld/src/main/java/org/confluence/mod/common/init/entity"
GENERATED = Path("ConfluenceOtherworld/src/generated/resources/data/confluence/loot_table/entities")

REF = re.compile(r"(\w+Entities)\.([A-Z][A-Z0-9_]+)")
REG = re.compile(r"\b([A-Z][A-Z0-9_]+)\s*=\s*[^;]*?register\w*\s*\(\s*\"([a-z0-9_]+)\"")


def blob(repo: Path, rev: str, path: str) -> str | None:
    p = subprocess.run(["git", "show", f"{rev}:{path}"], cwd=repo, capture_output=True)
    return p.stdout.decode("utf-8", "replace") if p.returncode == 0 else None


def grep_files(repo: Path, rev: str, prefix: str) -> list[str]:
    p = subprocess.run(["git", "ls-tree", "-r", "--name-only", rev, "--", prefix], cwd=repo, capture_output=True)
    return [l for l in p.stdout.decode("utf-8", "replace").split("\n") if l.endswith(".java")]


def refs(repo: Path, rev: str, provider: str) -> set[tuple[str, str]]:
    text = blob(repo, rev, provider)
    if text is None:
        raise SystemExit(f"{repo}:{rev} 无 {provider}")
    return set(REF.findall(text))


def id_map(repo: Path, rev: str) -> dict[str, str]:
    """常量名 -> 注册 id（扫 common/init/entity/*.java）。"""
    out: dict[str, str] = {}
    for path in grep_files(repo, rev, ENTITY_FILES):
        text = blob(repo, rev, path)
        if not text:
            continue
        for const, rid in REG.findall(text):
            out.setdefault(const, rid)
    return out


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--out", default=None)
    ap.add_argument("--provider", default=DEFAULT_PROVIDER)
    args = ap.parse_args()

    a = refs(REPO_120, "HEAD", args.provider)
    b = refs(REPO_121, "HEAD", args.provider)
    id120 = id_map(REPO_120, "HEAD")
    id121 = id_map(REPO_121, "HEAD")

    only = sorted(a - b)
    groups: dict[str, list[tuple[str, str, str, str]]] = defaultdict(list)
    stat = {"has-loot-json": 0, "no-loot-json": 0, "entity-missing": 0, "id-unknown": 0}
    for cls, const in only:
        rid = id120.get(const, "")
        exists_121 = const in id121
        json_121 = (REPO_121 / GENERATED / f"{rid}.json").exists() if rid else False
        if not exists_121:
            state = "实体在 1.21 不存在"
            stat["entity-missing"] += 1
        elif not rid:
            state = "id 未知"
            stat["id-unknown"] += 1
        elif json_121:
            state = "1.21 有 on-disk 掉落表"
            stat["has-loot-json"] += 1
        else:
            state = "**1.21 无掉落表**"
            stat["no-loot-json"] += 1
        groups[cls].append((const, rid, state, "在 1.21 已注册" if exists_121 else "未注册"))

    lines = [
        "# 掉落表覆盖比对（1.20 HEAD vs 1.21 HEAD，provider = %s）" % args.provider,
        f"# 1.20 引用实体 {len(a)} 个 / 1.21 引用 {len(b)} 个 / 1.20-only {len(only)} 个",
        f"# 分类: 1.21 无掉落表 {stat['no-loot-json']}；1.21 有 on-disk 掉落表 {stat['has-loot-json']}；"
        f"实体在 1.21 不存在 {stat['entity-missing']}；id 未知 {stat['id-unknown']}",
        "# 注: on-disk 只作旁证（src/generated 是构建产物、被 gitignore）",
    ]
    for cls in sorted(groups):
        lines.append(f"\n## {cls}（{len(groups[cls])} 个）")
        for const, rid, state, note in sorted(groups[cls], key=lambda t: t[1]):
            lines.append(f"  {const:28s} id={rid or '?':32s} {state:22s} ({note})")
    report = "\n".join(lines) + "\n"
    if args.out:
        Path(args.out).write_text(report, encoding="utf-8", newline="\n")
        print(f"written: {args.out}  ({len(report)} chars)")
    else:
        import sys
        sys.stdout.reconfigure(encoding="utf-8")
        print(report)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
