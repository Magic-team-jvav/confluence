#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""把 `rw_rowscan.py` 报出的 STRONG token 归到"最早引入提交"（**路径级** `-S`），并映射台账行号。

**用途**：吃 `rw_rowscan.py` 的输出，对每个 `(该行的 1.20 路径, 缺失 token)` 做
`git log -S <token> -- <该路径>`，取**最早**那笔提交，再查台账行号；归到 `dfcc5c041`/`dc57ba5c2`
的落点判 `DO-NOT-PORT`，归到回退族（`4af532ed1`/`a8e0b487f`/`1e0393178`）的打 `REVERT-FAMILY` 待人工判。

**为什么必须路径级**：全仓级 `git log -S <tok>` 会被**共享史里别的文件的同名符号**带偏。
实例：`POWDER_SNOW_WALKABLE_MOBS` 全仓级最早命中 `4d02a62a6`（TerraEntity 搬家，非毒）、
路径级命中 `dfcc5c041`（毒），**结论相反**。落地归属一律以路径级为准；全仓级只当"该名字在别处出现过"
的粗筛，本工具在最前面找不到时会附带一行 `(全仓级粗筛: <sha>)`，**那行不构成裁定依据**。

**与 blame 的分工**：`git blame` 认的是"最后改这行的人"，**纯重排/折行的提交会抢走行归属**
（实例：`9bc04295b` 行 153 只是把多行 tag 语句压成一行，于是 blame 把 4 个毒引入的 tag 算成它的）。
所以**归属看 `-S`，blame 只用来定位缺口所在区间**。

**用法**：
    python tools/port2native/rw_attr.py <rw_rowscan 报告> <输出文件> [最多归属对数]

**与其它三个工具的分工**：
  - `rw_rowscan.py`：**提交 → 行**（本工具的输入生产者）。拿 1.21 全仓折叠行文本比对该提交的每条新增行，
    给出 `[STRONG]` 行与 `missing:` token 清单。
  - `rw_attr.py`（本工具）：**行 → 引入提交**。把上一步的 token 落成"毒/非毒"裁定。
  - `row_attr.py`：**提交 → 落点**，直接给逐文件裁定（DO-NOT-PORT / PARTIAL-POISON / 待判），
    用于"这一提交整体能不能搬"；本工具用于"这一提交里哪几行不能搬"。
  - `residual_file.py`：**文件 → 残差**，不依赖提交，看 1.20 HEAD 某文件对 1.21 全仓的覆盖度；
    它漏"token 在 1.21 别处存在"与"整块内容从未作为新增行出现"两类缺口，故不能替代上述三者。
"""
from __future__ import annotations

import io
import json
import os
import re
import subprocess
import sys

REPO_120 = r'D:\Minecraft\1.20forge\confluence'
REPO_121 = r'D:\Minecraft\1.21neoforge\confluence'
# 毒提交（落点归到它们 => 永不移植）
POISON = {'dfcc5c041', 'dc57ba5c2'}
# 同族回退/清理提交：归到它们要逐条人工判（回退族 ≠ 毒，也 ≠ 可直接抄）
REVERT_FAMILY = {'4af532ed1', 'a8e0b487f', '1e0393178'}
MISSING = '             missing: '


def git(repo: str, *args: str) -> str:
    return subprocess.run(['git', '-C', repo, *args], capture_output=True).stdout.decode('utf-8', 'replace')


def load_rows() -> dict:
    led = json.load(io.open(os.path.join(REPO_121, 'notes', 'port-ledger.json'), encoding='utf-8'))
    rows = {}
    for i, c in enumerate(led['commits']):
        rows[c['hash']] = i + 1
        rows[c['hash'][:9]] = i + 1
    return rows


def earliest(symbol: str, path: str):
    """该路径内最早一次 token 计数变化（= 引入提交）。返回 'sha|date|subject' 或 None。"""
    out = git(REPO_120, 'log', '--format=%H|%ad|%s', '--date=short', '-S', symbol, '--', path)
    lines = [l for l in out.split('\n') if l.strip()]
    return lines[-1] if lines else None


def repo_level(symbol: str):
    """全仓级粗筛：仅当路径级无命中时附带，**不作裁定依据**。"""
    out = git(REPO_120, 'log', '--format=%H|%ad|%s', '--date=short', '-S', symbol)
    lines = [l for l in out.split('\n') if l.strip()]
    return lines[-1] if lines else None


def parse_report(text: str):
    """[(1.20路径, token)] —— 按 `=== [状态] 路径` 分段，收集每段的 missing 行。"""
    pairs, cur = [], None
    for ln in text.split('\n'):
        if ln.startswith('=== '):
            m = re.search(r'\] (\S+)', ln)
            cur = m.group(1) if m else None
        elif ln.startswith(MISSING) and cur:
            for t in ln[len(MISSING):].split(','):
                t = t.strip()
                if t:
                    pairs.append((cur, t))
    seen, uniq = set(), []
    for p in pairs:
        if p not in seen:
            seen.add(p)
            uniq.append(p)
    return uniq


def main(argv: list[str]) -> int:
    if len(argv) < 2:
        print(__doc__)
        return 2
    rs, out = argv[0], argv[1]
    max_pairs = int(argv[2]) if len(argv) > 2 else 10 ** 9
    rows = load_rows()
    uniq = parse_report(io.open(rs, encoding='utf-8').read())[:max_pairs]

    f = io.open(out, 'w', encoding='utf-8')
    f.write('# 归属来源 %s：%d 个 (路径, token) 对\n' % (os.path.basename(rs), len(uniq)))
    f.write('# 口径：路径级 git log -S <token> -- <1.20 路径>；取最早提交\n\n')
    counts, poison_pairs = {}, []
    for path, tok in uniq:
        e = earliest(tok, path)
        if e is None:
            hint = repo_level(tok)
            verdict = 'NOT-IN-THIS-PATH(1.21/原版 API 或不在该文件)'
            if hint:
                verdict += ' (全仓级粗筛: %s)' % hint.split('|')[0][:9]
            sha = row = '-'
        else:
            sha, row = e.split('|')[0][:9], rows.get(e.split('|')[0], '?')
            if sha in POISON:
                verdict = 'DO-NOT-PORT(毒)'
                poison_pairs.append((path, tok))
            elif sha in REVERT_FAMILY:
                verdict = 'REVERT-FAMILY(人工判)'
            else:
                verdict = 'non-poison'
        counts[verdict.split('(')[0]] = counts.get(verdict.split('(')[0], 0) + 1
        f.write('%-56s %-34s <- %-9s row=%-5s %s\n' % (
            os.path.basename(path)[:56], tok[:34], sha, row, verdict))
        if e:
            f.write('%-56s %-34s    %s\n' % ('', '', e.split('|', 1)[1][:110]))
    f.write('\n# 汇总: %s\n' % counts)
    if poison_pairs:
        f.write('# 归毒落点（永不移植）%d 个:\n' % len(poison_pairs))
        for path, tok in poison_pairs:
            f.write('#   %s  <-  %s\n' % (tok, path))
    f.close()
    print('written %s  %s' % (out, counts))
    return 0


if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
