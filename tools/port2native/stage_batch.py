#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""一个批次的「暂存 -> 转换 -> 合并」三步（标准循环第 2 步）。

把这一步单独做成工具，是为了少打一堆重复命令，也避免手写路径时出错
（踩过：两个目录的相对路径混用同一个根，会把结果写到 src/main/java/../out/... 而不报错）。

用法：
  python tools/port2native/stage_batch.py --name wp2e \
      --src120 <1.20 侧 src/main/java 根> --dest <1.21 侧 src/main/java 根> \
      --file org/confluence/mod/common/entity/monster/RockGolem.java [...]
  加 --convert 会接着跑 port2native.py，加 --apply 会再跑 apply_batch.py 把结果落进目标树。
"""

from __future__ import annotations

import argparse
import os
import shutil
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))


def main() -> int:
    try:
        sys.stdout.reconfigure(encoding='utf-8', errors='replace')
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument('--name', required=True, help='批次名，用作临时目录名')
    ap.add_argument('--src120', required=True, help='1.20 侧 src/main/java 根')
    ap.add_argument('--dest', required=True, help='1.21 侧 src/main/java 根')
    ap.add_argument('--file', action='append', required=True,
                    help='相对源码根的路径（正斜杠），可重复')
    ap.add_argument('--convert', action='store_true')
    ap.add_argument('--apply', action='store_true')
    ap.add_argument('--root', default='', help='工具链根（默认本脚本所在目录的上一级）')
    args = ap.parse_args()

    base = os.path.join(os.environ.get('TEMP', '.'), 'port2native', args.name)
    stage = os.path.join(base, 'src')
    out = os.path.join(base, 'out')
    if os.path.isdir(stage):
        shutil.rmtree(stage)
    if os.path.isdir(out):
        shutil.rmtree(out)

    total = 0
    for rel in args.file:
        rel = rel.replace('\\', '/')
        s = os.path.join(args.src120, *rel.split('/'))
        if not os.path.isfile(s):
            print('[!] 1.20 侧找不到: %s' % rel, file=sys.stderr)
            return 2
        d = os.path.join(stage, *rel.split('/'))
        os.makedirs(os.path.dirname(d), exist_ok=True)
        shutil.copy2(s, d)
        n = len([x for x in open(s, encoding='utf-8', errors='replace').read().split('\n') if x.strip()])
        total += n
    print('暂存 %d 个文件 / %d 非空行 -> %s' % (len(args.file), total, stage))

    # 工具链根 = 仓库根。`HERE` 是 <repo>/tools/port2native，所以上去**两级**。
    # 踩过：写成三级 -> cwd 变成仓库的**上一级**（D:\Minecraft\1.21neoforge），
    # 于是 --dest 的相对路径被解析到隔壁另一个同名项目里，
    # apply_batch.py 那边 makedirs(exist_ok=True) 不报错，静悄悄把 15 个文件写到别人目录下。
    tools = args.root or os.path.dirname(os.path.dirname(HERE))
    if not os.path.isdir(os.path.join(tools, '.git')):
        print('[!] 工具链根不在 git 仓库根：%s（用 --root 显式指定）' % tools, file=sys.stderr)
        return 2
    rules = os.path.join(HERE, 'rules')
    if args.convert:
        r = subprocess.run([sys.executable, os.path.join(HERE, 'port2native.py'),
                            '--source', stage, '--out', out, '--rules', rules], cwd=tools)
        if r.returncode != 0:
            return r.returncode
    if args.apply:
        r = subprocess.run([sys.executable, os.path.join(HERE, 'apply_batch.py'),
                            '--stage', stage, '--out', out, '--dest', args.dest], cwd=tools)
        if r.returncode != 0:
            return r.returncode
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
