#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""把「暂存目录 + 转换器输出」合并进目标源码树。

为什么单独写成工具：这一步踩过两个坑，写死了才不会再犯——

1. 转换器**只写出有改动的文件**，所以必须「暂存目录打底、转换器输出覆盖」两层一起拷，
   只拷 `out/` 会丢掉那些无需转换的文件。
2. 两个目录的相对路径必须**各自相对自己的根**算；混用会把转换结果写到
   `src/main/java/../out/...` 这种错位的目录里去（而且不报错）。
   同时逐文件打印 `add` / `OVERWRITE`，**1.21 侧已存在的同名文件必须是一眼可见的**，
   因为按约定它们不得被覆盖，得先 diff 再人工合并。

用法：
  python tools/port2native/apply_batch.py --stage <暂存根> --out <转换器输出根> --dest <目标源码根>
"""

from __future__ import annotations

import argparse
import os
import shutil
import sys


def collect(root: str) -> dict[str, str]:
    found: dict[str, str] = {}
    for dirpath, _dirs, files in os.walk(root):
        for f in files:
            if f.endswith(".java"):
                full = os.path.join(dirpath, f)
                found[os.path.relpath(full, root)] = full
    return found


def repo_root_of(path: str) -> str | None:
    """向上找到含 .git 的目录；找不到返回 None。"""
    cur = os.path.abspath(path)
    while True:
        if os.path.isdir(os.path.join(cur, '.git')):
            return cur
        parent = os.path.dirname(cur)
        if parent == cur:
            return None
        cur = parent


def main() -> int:
    try:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument("--stage", required=True, help="暂存目录（1.20 原始文件）")
    ap.add_argument("--out", default="", help="转换器输出目录（只含有改动的文件）")
    ap.add_argument("--dest", required=True, help="目标源码根（1.21）")
    ap.add_argument("--allow-outside-repo", action="store_true",
                    help="允许 --dest 不在 git 仓库里（默认拒绝）")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    # 安全闸：目标必须落在 git 仓库里，且形如 .../src/main/java。
    # 踩过（本次真实发生）：调用方 cwd 的 dirname 多写了一级，--dest 的相对路径
    # 被解析到隔壁另一个同名项目上；下面 makedirs(exist_ok=True) 不报错，
    # 于是静悄悄把 15 个文件写进了 D:\Minecraft\1.21neoforge\ConfluenceOtherworld。
    # 之所以没造成损失，纯粹是因为那个目录恰好是空的（事后靠目录创建时间才确认）。
    dest_abs = os.path.abspath(args.dest)
    if not args.allow_outside_repo:
        root = repo_root_of(dest_abs)
        if root is None:
            print("[!] --dest 不在任何 git 仓库内：%s\n"
                  "    这通常意味着相对路径解析错了（cwd 多写/少写一级 dirname）。\n"
                  "    确认无误可加 --allow-outside-repo。" % dest_abs, file=sys.stderr)
            return 2
        norm = dest_abs.replace('\\', '/')
        if not (norm.endswith('/src/main/java') or '/src/main/java/' in norm):
            print("[!] --dest 不像 java 源码根（应以 src/main/java 结尾）：%s" % dest_abs,
                  file=sys.stderr)
            return 2
        print("目标：%s（仓库根 %s）" % (dest_abs, root))

    merged = collect(os.path.abspath(args.stage))
    if args.out and os.path.isdir(args.out):
        merged.update(collect(os.path.abspath(args.out)))

    adds, over = [], []
    for rel in sorted(merged):
        dst = os.path.join(args.dest, rel)
        (over if os.path.exists(dst) else adds).append(rel)
        if not args.dry_run:
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            shutil.copy2(merged[rel], dst)

    try:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    except Exception:
        pass
    print(f"合计 {len(merged)} 个文件；新增 {len(adds)}，覆盖 {len(over)}"
          + ("（dry-run，未写盘）" if args.dry_run else ""))
    for rel in adds:
        print("  add        " + rel.replace("\\", "/"))
    for rel in over:
        print("  OVERWRITE  " + rel.replace("\\", "/"))
    if over:
        print("\n⚠️ 上面这些文件 1.21 侧已存在 —— 按约定不得盲覆盖，请逐个 diff 后再人工合并。")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
