#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""把工作树里的文本文件换行统一成 **CRLF**（本仓库的约定）。

**为什么需要它**：两个仓库都是 `core.autocrlf=true`，工作树里所有源码都是 CRLF
（1.21 侧 5813 个文件是 `i/lf + w/crlf`，索引仍是 LF，属正常）。
但用 Python / 本 harness 的写文件工具生成的**新文件默认是 LF**，
于是它们在 `git ls-files --eol` 里显示成 `w/lf`，与仓库其余文件不一致；
更麻烦的是「CRLF 文件被插入 LF 行」会变成 `w/mixed`（本轮 `.gitignore` 就是）。

**约定**：本仓库工作树一律 CRLF，索引 LF（autocrlf 负责转换）。
所以每批的**最后一步**都要跑一次本工具。

用法：
  python tools/port2native/fix_eol.py --repo .                    # 修已被 git 跟踪的 w/lf 与 w/mixed
  python tools/port2native/fix_eol.py --repo . --paths <文件或目录>...  # 也修尚未 add 的新文件
  python tools/port2native/fix_eol.py --repo . --check            # 只报告，不改

二进制按内容判定（含 NUL 字节即跳过），不靠扩展名。
"""

from __future__ import annotations

import argparse
import os
import subprocess
import sys

# 即使被当成文本也不该动的目录
SKIP_DIRS = {'.git', '.gradle', 'build', 'node_modules', '__pycache__', '.idea', '.vscode'}


def is_binary(raw: bytes) -> bool:
    return b'\x00' in raw[:8192]


def to_crlf(path: str) -> str:
    """返回 'fixed' / 'ok' / 'binary' / 'error'。"""
    try:
        with open(path, 'rb') as fh:
            raw = fh.read()
    except OSError:
        return 'error'
    if is_binary(raw):
        return 'binary'
    # 先把已有 CRLF 归一成 LF，再统一写成 CRLF —— 顺带修掉 mixed
    normalized = raw.replace(b'\r\n', b'\n').replace(b'\n', b'\r\n')
    if normalized == raw:
        return 'ok'
    with open(path, 'wb') as fh:
        fh.write(normalized)
    return 'fixed'


def tracked_with_lf(repo: str) -> list[str]:
    out = subprocess.run(['git', '-C', repo, 'ls-files', '--eol'],
                         capture_output=True, text=True, encoding='utf-8', errors='replace').stdout
    files = []
    for line in out.splitlines():
        parts = line.split('\t')
        if len(parts) != 2:
            continue
        head, path = parts[0], parts[1]
        if 'w/lf' in head or 'w/mixed' in head:
            files.append(os.path.join(repo, path))
    return files


def walk_paths(paths: list[str]) -> list[str]:
    out: list[str] = []
    for p in paths:
        if os.path.isfile(p):
            out.append(p)
            continue
        for dirpath, dirs, files in os.walk(p):
            dirs[:] = [d for d in dirs if d not in SKIP_DIRS]
            out.extend(os.path.join(dirpath, f) for f in files)
    return out


def main() -> int:
    try:
        sys.stdout.reconfigure(encoding='utf-8', errors='replace')
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument('--repo', default='.')
    ap.add_argument('--paths', nargs='*', default=[], help='额外处理未跟踪的新文件/目录')
    ap.add_argument('--check', action='store_true', help='只报告不改')
    args = ap.parse_args()
    repo = os.path.abspath(args.repo)

    targets = tracked_with_lf(repo)
    if args.paths:
        targets += walk_paths([os.path.abspath(p) for p in args.paths])
    seen, uniq = set(), []
    for p in targets:
        if p not in seen:
            seen.add(p)
            uniq.append(p)

    stats = {'fixed': 0, 'ok': 0, 'binary': 0, 'error': 0}
    for p in uniq:
        if args.check:
            try:
                with open(p, 'rb') as fh:
                    raw = fh.read()
            except OSError:
                r = 'error'
            else:
                if is_binary(raw):
                    r = 'binary'
                elif raw.replace(b'\r\n', b'\n').replace(b'\n', b'\r\n') == raw:
                    r = 'ok'
                else:
                    r = 'fixed'
        else:
            r = to_crlf(p)
        stats[r] = stats.get(r, 0) + 1
        if r == 'fixed':
            print(('would fix  ' if args.check else 'fixed  ') + os.path.relpath(p, repo).replace('\\', '/'))
    print(f"候选 {len(uniq)} 个 → 需修 {stats['fixed']}、已是 CRLF {stats['ok']}、"
          f"二进制跳过 {stats['binary']}、读取失败 {stats['error']}"
          + ('（--check，未写盘）' if args.check else ''))
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
