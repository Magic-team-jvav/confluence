"""注册 id 级差异扫描：1.20 HEAD vs 1.21 HEAD（按文件逐一对齐）。

用途：找"整类注册物漏搬"的缺口（例：1.20 的 `entity` 创造标签页，1.21 全缺）。
口径：只取注册调用里的 id 字面量（`register*("id"`），按**同一相对路径**比对；
      1.21 缺该文件 ⇒ 记 `MISSING-FILE`；文件在但 id 少 ⇒ 列 1.20-only 的 id。
输出默认写 UTF-8 文件（控制台是 GBK，中文会乱码）。

用法:
  python tools/port2native/registry_id_diff.py [--out 文件] [--roots 逗号分隔的相对目录]
                                              [--only-missing] [--min N]
"""
import argparse
import io
import re
import subprocess
from pathlib import Path

REPO_120 = Path(r"D:\Minecraft\1.20forge\confluence")
REPO_121 = Path(r"D:\Minecraft\1.21neoforge\confluence")

DEFAULT_ROOTS = [
    "ConfluenceOtherworld/src/main/java/org/confluence/mod/common/init",
    "ConfluenceOtherworld/src/main/java/org/confluence/mod/common/block",
    "ConfluenceOtherworld/src/main/java/org/confluence/mod/common/item",
    "ConfluenceOtherworld/src/main/java/org/confluence/mod/common/entity",
    "ConfluenceOtherworld/src/main/java/org/confluence/mod/common/effect",
    "ConfluenceOtherworld/src/main/java/org/confluence/mod/common/init/entity",
]

# register("id"、registerItem("id"、copyBlockRegister("id"、registerWithItem("id"、registerRelic("id" …
# 注意：1.21 大量使用驼峰助手名（copyBlockRegister / registerWithItem / registerRelic），
# 早期只写 `register[A-Za-z_]*` 会漏掉它们、把"已注册"误报成缺口——必须大小写都收。
REG_CALL = re.compile(r"[A-Za-z_]*[Rr]egister[A-Za-z_]*\s*\(\s*\"([a-z0-9_./]+)\"")
GREP_REG = r"[A-Za-z_]*[Rr]egister[A-Za-z_]*\(\"[a-z0-9_./]+\""
LINE_COMMENT = re.compile(r"//[^\n]*")


def git(repo: Path, *args: str) -> str:
    p = subprocess.run(["git", *args], cwd=repo, capture_output=True)
    return p.stdout.decode("utf-8", "replace")


def tree(repo: Path, root: str) -> list[str]:
    out = git(repo, "ls-tree", "-r", "--name-only", "HEAD", "--", root)
    return [l for l in out.split("\n") if l.strip()]


def blob(repo: Path, path: str) -> str | None:
    p = subprocess.run(["git", "show", f"HEAD:{path}"], cwd=repo, capture_output=True)
    if p.returncode != 0:
        return None
    return p.stdout.decode("utf-8", "replace")


def ids_of(text: str) -> set[str]:
    stripped = LINE_COMMENT.sub(" ", text)
    return set(REG_CALL.findall(stripped))


def where_in_121(id_literal: str, limit: int = 3) -> list[str]:
    """在 1.21 全仓（java/json）里找该 id 字面量出现的位置，用于排除"换到别的文件注册"的假缺口。"""
    p = subprocess.run(["git", "grep", "-l", "-F", id_literal, "HEAD", "--", "*.java", "*.json"],
                       cwd=REPO_121, capture_output=True)
    hits = [l for l in p.stdout.decode("utf-8", "replace").split("\n") if l.strip()]
    return hits[:limit]


JAVA_ROOT = "ConfluenceOtherworld/src/main/java"


def registered_ids_121() -> set[str]:
    """1.21 全仓所有 register*("id" 里出现的 id 并集（注册宇宙）。"""
    out = git(REPO_121, "grep", "-h", "-o", "-E", GREP_REG, "HEAD", "--", f"{JAVA_ROOT}/*.java")
    return set(re.findall(r'\"([a-z0-9_./]+)\"', out))


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--out", default=None)
    ap.add_argument("--roots", default=None)
    ap.add_argument("--only-missing", action="store_true")
    ap.add_argument("--min", type=int, default=1, help="只报 ≥N 个缺失 id 的文件")
    ap.add_argument("--verify-121", action="store_true",
                    help="对每个 1.20-only id 在 1.21 全仓 grep，区分真缺口与「换文件注册」的假缺口")
    args = ap.parse_args()

    roots = (args.roots.split(",") if args.roots else DEFAULT_ROOTS)
    universe = registered_ids_121() if args.verify_121 else set()
    if args.verify_121:
        print(f"1.21 注册宇宙 id 数: {len(universe)}")
    buf: list[str] = []
    emit = buf.append

    seen: set[str] = set()
    paths: list[str] = []
    for root in roots:
        for p in tree(REPO_120, root):
            if p.endswith(".java") and p not in seen:
                seen.add(p)
                paths.append(p)
    paths.sort()

    total_files = 0
    missing_files = 0
    total_missing_ids = 0
    for path in paths:
        src = blob(REPO_120, path)
        if src is None:
            continue
        total_files += 1
        src_ids = ids_of(src)
        if not src_ids:
            continue
        dst = blob(REPO_121, path)
        if dst is None:
            missing_files += 1
            emit(f"\n[MISSING-FILE] {path}  (1.20 侧 {len(src_ids)} 个注册 id)")
            emit(f"    1.20 ids: {', '.join(sorted(src_ids))}")
            total_missing_ids += len(src_ids)
            continue
        dst_ids = ids_of(dst)
        only = sorted(src_ids - dst_ids)
        if len(only) >= max(1, args.min) and only:
            total_missing_ids += len(only)
            emit(f"\n[ID-GAP] {path}  (1.20 {len(src_ids)} / 1.21 {len(dst_ids)})")
            emit(f"    1.20-only id ({len(only)}): {', '.join(only)}")
            if args.verify_121:
                real, aliased, moved = [], [], []
                for id_literal in only:
                    if id_literal in universe:
                        moved.append(id_literal)
                        continue
                    hits = where_in_121(id_literal)
                    (aliased if hits else real).append((id_literal, hits))
                emit(f"    -- 真缺口（1.21 既未注册、也无该字面量）: {len(real)} --")
                for id_literal, _ in real:
                    emit(f"       ABSENT     {id_literal}")
                emit(f"    -- 待人工看（1.21 未注册但字面量存在，多为改名/别名映射）: {len(aliased)} --")
                for id_literal, hits in aliased:
                    emit(f"       ALIAS?     {id_literal}  -> {', '.join(h.replace('HEAD:', '') for h in hits)}")
                emit(f"    -- 假缺口（1.21 已在别处注册同一 id）: {len(moved)} --")
                emit(f"       REGISTERED {', '.join(moved) if moved else '(无)'}")

    header = [
        f"# 注册 id 差异扫描（1.20 HEAD vs 1.21 HEAD）",
        f"# 扫描文件 {total_files} 个（roots: {', '.join(roots)}）",
        f"# 1.21 缺整个文件 {missing_files} 个；出现 1.20-only id 的文件见下；1.20-only id 合计 {total_missing_ids}",
    ]
    report = "\n".join(header + buf) + "\n"
    if args.out:
        Path(args.out).write_text(report, encoding="utf-8", newline="\n")
        print(f"written: {args.out}  ({len(report)} chars)")
    else:
        sys.stdout.reconfigure(encoding="utf-8")
        print(report)
    return 0


if __name__ == "__main__":
    import sys
    sys.exit(main())
