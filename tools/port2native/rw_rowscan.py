#!/usr/bin/env python3
# -*- coding: utf-8 -*-
r"""按提交普查"1.21 侧从未出现过的 1.20 新增行"：新增行 → 折叠空白 → 对 1.21 文本行集合比对。

**用途**：给台账行走做**行级粗筛**。给定一个 1.20 提交，把它 diff 里的每条新增行折叠空白
（连续空白压成一个空格）后，去 1.21 全仓文本行集合里找：
  - 找不到 ⇒ `[STRONG]`：该行的**整行文本**在 1.21 全仓一次都没出现过，高度可疑（多为待移植内容）；
  - 找到了、且行内内容标识符在 1.21 也都在 ⇒ `[weak]`：只是上下文差异，基本可跳过；
  - 行内内容标识符里**混有 1.21 缺失的** ⇒ `[STRONG]`（`missing:` 列出缺的那些 token）。
非 `.java` 的新增行（json/lang/png 清单等）单独打成 `[asset]`，供资产侧人工过。

**用法**：
    python tools/port2native/rw_rowscan.py <1.20提交> <输出文件> [--all]
    # --all: 不限制每行列出的缺失 token 数（默认 14）

控制台是 GBK，中文会炸 ⇒ **必须走输出文件**（UTF-8）。产物再交给 `rw_attr.py` 归属。

**与其它三个工具的分工**（四者互不替代）：
  - `rw_rowscan.py`（本工具）：**提交 → 行**。只看"这一提交新增了什么行、这些行 1.21 有没有"，
    比对面是**1.21 全仓的折叠行文本**，因此能发现"整行没搬"的漏项，但**不判**内容归属、
    也不判毒。跨文件类型（java + 资源）。
  - `rw_attr.py`：**行 → 引入提交**。吃掉本工具的 `[STRONG]`/`missing:` token，对每个
    (token, 该行的 1.20 路径) 做**路径级** `git log -S`，归到最早引入提交并映射台账行号，
    标出毒提交/回退族 ⇒ 这是"能不能搬"的第一道闸。
  - `row_attr.py`：**提交 → 落点**。同是"按提交"，但它不比对 1.21，而是把该提交改动的每个
    java 文件的新增行拆成"落点标识符"，逐个路径级 `-S` 定归属，直接给**逐文件裁定**
    （DO-NOT-PORT / PARTIAL-POISON / 待判）。用于"这一提交能不能整体搬"。
  - `residual_file.py`：**文件 → 残差**。不依赖提交，拿 1.20 HEAD 某文件的标识符集合
    减去 **1.21 全仓**标识符集合，看覆盖度。它**会漏两类"被掩盖缺口"**：
    ① token 在 1.21 别处存在（假覆盖）；② **整块内容从未作为任何已走行的新增行出现**
    （例如整段 tag 块由某个从未被行走的提交引入）——这类要靠 `git blame -L` + 1.21 同位置核对。
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

TOKEN = re.compile(r'[A-Za-z_$][A-Za-z0-9_$]*')
STOP = set('''import package public private protected static final class interface record return new this super null true false
void int long float double boolean byte short char for while if else switch case default break continue throw throws try catch
finally instanceof extends implements abstract var get set add of with and or not is has can should to from out
String Override Deprecated Object Integer Boolean List Map Set Supplier Function Consumer Runnable Exception'''.split())
# 平台/移植层名字：它们的"缺失"只说明 1.21 换了 API 或退役了 portlib，不是内容缺口
PLATFORMISH = re.compile(r'(?:^Port|^IPort|PortLib|mesdag|Forge|NeoForge|Deferred|RegistryObject)')


def git(repo: str, *args: str) -> str:
    return subprocess.run(['git', '-C', repo, *args], capture_output=True).stdout.decode('utf-8', 'replace')


def fold(s: str) -> str:
    r"""折叠空白：`\s+` → 单个空格，便于跨缩进/换行风格比对。"""
    return re.sub(r'\s+', ' ', s).strip()


def iter_java(root: str):
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = [d for d in dirnames if d not in ('build', '.git', 'generated', 'run', 'runs')]
        if '_nfsrc' in dirpath:
            continue
        for fn in filenames:
            if fn.endswith('.java'):
                yield os.path.join(dirpath, fn)


def build_index():
    """1.21 全仓 java 的：折叠行集合 + 标识符/字符串 id 集合。"""
    lines, idents, n = set(), set(), 0
    for p in iter_java(REPO_121):
        n += 1
        try:
            txt = io.open(p, encoding='utf-8', errors='replace').read()
        except OSError:
            continue
        for ln in txt.split('\n'):
            fs = fold(ln)
            if fs:
                lines.add(fs)
        idents.update(TOKEN.findall(txt))
        idents.update(re.findall(r'"([a-z0-9_/.\-]+)"', txt))
    return lines, idents, n


def parse_diff(sha: str):
    """把一个提交的 diff 拆成 [{path,status,added[]}]（-M 跟随改名）。"""
    txt = git(REPO_120, 'show', '--format=', '-M', '--no-color', sha)
    files, cur = [], None
    for ln in txt.split('\n'):
        if ln.startswith('diff --git '):
            if cur:
                files.append(cur)
            cur = {'path': None, 'status': '?', 'added': []}
        elif cur is not None and ln.startswith('--- '):
            if ln[4:].strip() == '/dev/null':
                cur['status'] = 'A'
        elif cur is not None and ln.startswith('+++ '):
            p = ln[4:].strip()
            if p == '/dev/null':
                cur['status'] = 'D'
            else:
                cur['path'] = p[2:] if p.startswith('b/') else p
        elif cur is not None and ln.startswith('+') and not ln.startswith('+++'):
            cur['added'].append(ln[1:])
    if cur:
        files.append(cur)
    return files


def main(argv: list[str]) -> int:
    if len(argv) < 2:
        print(__doc__)
        return 2
    sha, out = argv[0], argv[1]
    max_tok = 10 ** 9 if '--all' in argv else 14

    led = json.load(io.open(os.path.join(REPO_121, 'notes', 'port-ledger.json'), encoding='utf-8'))
    rows = {c['hash']: i + 1 for i, c in enumerate(led['commits'])}
    residue = {l.strip() for l in io.open(os.path.join(REPO_121, 'notes', 'poison-residue-files.txt'),
                                          encoding='utf-8').read().split('\n') if l.strip()}

    lines121, idents121, njava = build_index()
    files = parse_diff(sha)

    f = io.open(out, 'w', encoding='utf-8')
    meta = git(REPO_120, 'log', '-1', '--format=%H%n%ad%n%s', '--date=short', sha).strip().split('\n')
    f.write('# commit %s  row=%s\n' % (meta[0][:9], rows.get(meta[0], '?')))
    f.write('# date %s   subject %s\n' % (meta[1], meta[2]))
    f.write('# 1.21 java idx: %d files, %d folded lines, %d identifiers+ids\n' % (njava, len(lines121), len(idents121)))
    f.write('# changed paths: %d\n\n' % len(files))

    tot_strong = 0
    for fl in files:
        path = fl['path']
        if path is None:
            continue
        live = os.path.exists(os.path.join(REPO_120, path.replace('/', os.sep)))
        rel121 = os.path.exists(os.path.join(REPO_121, path.replace('/', os.sep)))
        rmark = 'RESIDUE' if path in residue else ''
        if not fl['added']:
            f.write('=== [%s] %s   srcHead=%s tgtExists=%s %s  (no added lines)\n'
                    % (fl['status'], path, live, rel121, rmark))
            continue
        f.write('=== [%s] %s   srcHead=%s tgtExists=%s %s\n' % (fl['status'], path, live, rel121, rmark))
        for al in fl['added']:
            fs = fold(al)
            if not fs or fs in lines121:
                continue
            if not path.endswith('.java'):
                f.write('    [asset] %s\n' % fs[:150])
                continue
            toks, seen = [], set()
            for t in TOKEN.findall(re.sub(r'"(?:\\.|[^"\\])*"', ' ', fs)):
                if len(t) < 4 or t in STOP or t in seen or PLATFORMISH.search(t):
                    continue
                seen.add(t)
                toks.append(t)
            strong = [t for t in toks if t not in idents121]
            weak = [t for t in toks if t in idents121]
            if strong:
                tot_strong += 1
                f.write('    [STRONG] %s\n' % fs[:150])
                f.write('             missing: %s\n' % ', '.join(strong[:max_tok]))
            elif toks and len(weak) >= 2:
                f.write('    [weak  ] %s\n' % fs[:150])
                f.write('             present: %s\n' % ', '.join(weak[:8]))
    f.write('\n# total STRONG added lines: %d\n' % tot_strong)
    f.close()
    print('written %s' % out)
    return 0


if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
