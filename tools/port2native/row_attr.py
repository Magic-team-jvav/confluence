#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""按"落点"归属判定台账行：把该行 diff 的新增行拆成标识符落点，逐个 git log -S 定归属。

为什么要到落点粒度
------------------
`dfcc5c041`(毒提交) 与 `dc57ba5c2`(其在 1.20 侧的回退) 合计触及 1690 个路径，
但同文件里绝大多数内容来自**其它** 1.20 提交。按"文件命中排除集"整文件跳过会误杀合法内容。
本工具只把**归属到那两个提交的落点**判为 DO-NOT-PORT，同文件其它落点照常判。

用法:
    python tools/port2native/row_attr.py <commit> [--max-tokens 12] [--all]

输出: 逐文件列出
    - 1.20 HEAD 是否存在该文件（不存在 = SKIP-1.20-REVERTED 候选）
    - 该行新增行里的落点标识符，及其**最早**引入提交（git log -S，限 1.20 HEAD 谱系）
    - 归属到毒提交的落点单独标 DO-NOT-PORT
"""
from __future__ import annotations

import argparse
import collections
import re
import subprocess
import sys
from pathlib import Path

REPO_120 = Path(r"D:\Minecraft\1.20forge\confluence")
REPO_121 = Path(r"D:\Minecraft\1.21neoforge\confluence")
# 落点归属到毒提交 => DO-NOT-PORT（永不移植）。
# `dc57ba5c2`("注释 杀杀杀") 是毒提交在 1.20 侧的注释式回退：归到它的落点 = 只以注释形态存在的毒内容，
# 同样按 DO-NOT-PORT 处理（绝不要把被注释掉的毒内容当成 1.20 原文抄回来）。
POISON = {"dfcc5c041", "dc57ba5c2"}
# 其余同族回退/清理提交（台账行 97/98 等）：归到它们的落点要逐条人工判——
# `4af532ed1`/`1e0393178` 是"回退错误公共架构并恢复 1.20 实现"（可能带真内容），
# `a8e0b487f` 是清理残留（多为删除）。脚本只打标记，不给结论。
REVERT_FAMILY = {"4af532ed1", "a8e0b487f", "1e0393178"}

TOKEN = re.compile(r"[A-Za-z_$][A-Za-z0-9_$]*")
LINE_COMMENT = re.compile(r"//[^\n]*")
BLOCK_COMMENT = re.compile(r"/\*.*?\*/", re.S)
STRING_LIT = re.compile(r'"(?:\\.|[^"\\])*"')
PLATFORMISH = re.compile(r"(?:^Port|^IPort|PortLib|mesdag|Forge|NeoForge|EventBus|Deferred|RegistryObject|1211)")
SKIP_WORDS = {
    "import", "package", "public", "private", "protected", "static", "final", "class", "interface",
    "record", "return", "new", "this", "super", "null", "true", "false", "void", "int", "long",
    "float", "double", "boolean", "byte", "short", "char", "for", "while", "if", "else", "switch",
    "case", "default", "break", "continue", "throw", "throws", "try", "catch", "finally", "instanceof",
    "extends", "implements", "abstract", "synchronized", "volatile", "transient", "native", "var",
    "get", "set", "add", "of", "with", "and", "or", "not", "is", "has", "can", "should", "to", "from",
}


def git(repo: Path, *args: str) -> str:
    return subprocess.run(["git", "-C", str(repo), *args], capture_output=True).stdout.decode("utf-8", "replace")


def strip_noise(text: str) -> str:
    return STRING_LIT.sub(" ", BLOCK_COMMENT.sub(" ", LINE_COMMENT.sub(" ", text)))


def changed_files(commit: str):
    out = []
    for line in git(REPO_120, "show", "--name-status", "-M", "-C", "--format=", commit).splitlines():
        if not line.strip():
            continue
        p = line.split("\t")
        out.append((p[0][0], p[2] if len(p) > 2 else p[1]))
    return out


def added_lines(commit: str, path: str) -> list[str]:
    diff = git(REPO_120, "show", "-U0", "--format=", commit, "--", path)
    return [l[1:] for l in diff.splitlines() if l.startswith("+") and not l.startswith("+++")]


def introducer(token: str, path: str) -> str:
    """最早引入该标识符的提交（简称 + 日期 + 标题）。"""
    out = git(REPO_120, "log", "--format=%h|%ad|%s", "--date=short", "-S", token, "HEAD", "--", path)
    lines = [l for l in out.splitlines() if l.strip()]
    if not lines:
        return "(无 -S 命中)"
    return lines[-1]


def package_words() -> set[str]:
    """包名片段（com/mojang/blaze3d/... 之类）是纯噪音，全部剔除。"""
    words = set()
    for repo in (REPO_120, REPO_121):
        out = git(repo, "grep", "-h", "-E", "^package ", "HEAD", "--", "*.java")
        for line in out.splitlines():
            pkg = line.strip().removeprefix("package ").rstrip(";").strip()
            words.update(pkg.split("."))
    return {w for w in words if w}


def looks_like_content(token: str) -> bool:
    """只保留像"内容标识符"的词：全大写常量、camelCase、或长度>=8 的驼峰/副词。"""
    if token.isupper() and "_" in token:
        return True
    if re.search(r"[a-z][A-Z]", token):          # camelCase
        return True
    if re.search(r"^[A-Z][a-z]+[A-Z]", token):   # PascalCase>=3 段
        return True
    return len(token) >= 8 and token[0].isupper()


def main(argv: list[str]) -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("commit")
    ap.add_argument("--max-tokens", type=int, default=12)
    ap.add_argument("--all", action="store_true", help="不做每文件 token 上限")
    ap.add_argument("--out", default="", help="把报告写到该文件（UTF-8，避免控制台 GBK 乱码）")
    args = ap.parse_args(argv)

    buf: list[str] = []

    def emit(s: str = "") -> None:
        buf.append(s)

    stop = package_words()
    head20 = set(git(REPO_120, "ls-tree", "-r", "--name-only", "HEAD").splitlines())
    files = changed_files(args.commit)
    emit(f"# {git(REPO_120, 'log', '-1', '--format=%h|%ad|%s', '--date=short', args.commit).strip()}")
    emit(f"# 改动 {len(files)} 个路径  类型分布: {dict(collections.Counter(st for st, _ in files))}")
    emit("")

    reverted, verdicts = [], []
    for st, path in files:
        live = path in head20
        if not live:
            reverted.append(path)
            continue
        if not path.endswith(".java"):
            verdicts.append(("DEFER-ASSETS", path, []))
            continue
        lines = added_lines(args.commit, path)
        if not lines:
            verdicts.append(("NOTHING-TO-PORT(无新增行)", path, []))
            continue
        text = strip_noise("\n".join(lines))
        toks, seen = [], set()
        for t in TOKEN.findall(text):
            if len(t) < 5 or t in SKIP_WORDS or t in seen or t in stop or PLATFORMISH.search(t):
                continue
            if not looks_like_content(t):
                continue
            seen.add(t)
            toks.append(t)
        toks = toks if args.all else toks[: args.max_tokens]
        owners = [(t, introducer(t, path)) for t in toks]
        poison_hits = [t for t, o in owners if o.split("|")[0] in POISON]
        revert_hits = [t for t, o in owners if o.split("|")[0] in REVERT_FAMILY]
        if not owners:
            verdict = "NOTHING-TO-PORT(无内容标识符)"
        elif poison_hits and len(poison_hits) == len(owners):
            verdict = "DO-NOT-PORT(落点全归毒提交)"
        elif poison_hits:
            verdict = "PARTIAL-POISON(混有归毒提交的落点)"
        elif revert_hits:
            verdict = "待判(含回退族落点，逐条人工判：回退族≠毒，也≠可直接抄)"
        else:
            verdict = "待判"
        verdicts.append((verdict, path, owners))

    emit(f"== 1.20 HEAD 已无该文件（SKIP-1.20-REVERTED 候选）: {len(reverted)} ==")
    for p in reverted:
        emit("   " + p.replace("ConfluenceOtherworld/src/main/java/org/confluence/mod/", ""))
    emit("\n== 逐文件落点归属 ==")
    for verdict, path, owners in verdicts:
        rel = path.replace("ConfluenceOtherworld/src/main/java/org/confluence/mod/", "")
        emit(f"\n[{verdict}] {rel}")
        poison = []
        keeps = []
        for t, o in owners:
            head = o.split("|")[0]
            if head in POISON:
                mark = "DO-NOT-PORT"
                poison.append(t)
            elif head in REVERT_FAMILY:
                mark = "1.21-KEEPS"
                keeps.append(t)
            else:
                mark = "ok"
            emit(f"    {mark:12s} {t:34s} <- {o}")
        if poison:
            emit(f"    => 该文件内归毒提交的落点: {', '.join(poison)}")
        if keeps:
            emit(f"    => 该文件内归回退族的落点（1.21 保留，勿删）: {', '.join(keeps)}")

    report = "\n".join(buf)
    if args.out:
        Path(args.out).write_text(report, encoding="utf-8", newline="\n")
        print(f"written: {args.out}  ({len(report)} chars)")
    else:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
        print(report)
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
