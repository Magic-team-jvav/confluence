#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""音效层搬迁：`ModSoundEvents` + `sounds.json` + `.ogg` + 字幕 lang。

**为什么需要单独一批**：1.21 侧 `ModSoundEvents` 只有 31 条，1.20 侧有 501 条；
`sounds.json` 30 / 503；`.ogg` 46 / 813。WP2 里 **49 / 99** 个待移植怪物类会引用
缺失的 `ModSoundEvents.X`（如 `BaseMimic` 要 `SOUL_DEATH`/`METAL_HURT`），
所以这是当前扇出最大的单点解锁项。

**两侧的资源布局不同，必须按 1.21 侧的写法生成**：
  * 1.20：`sounds/{item,mob,summon}/xxx.ogg`，`sounds.json` 里写 `confluence:item/xxx`
  * 1.21：`sounds/` **扁平**，`sounds.json` 里写 `confluence:xxx`
已核对：1.21 现有 46 个 ogg **逐字节等于** 1.20 对应文件，且 1.20 侧同名 basename
**不存在跨目录冲突**（0 个），所以扁平化无歧义。
本工具因此把 1.20 的路径**扁平化**后再写进 1.21，并且**不覆盖** 1.21 已有的
`sounds.json` 条目与 ogg 文件（沿用 1.21 既有写法）。

用法：
  python tools/port2native/port_sounds.py \
      --src120 D:\\Minecraft\\1.20forge\\confluence\\ConfluenceOtherworld \
      --dest   ConfluenceOtherworld --out notes/SOUND-PORT.md        # 只报告
  加 --apply 才真正写盘。
"""

from __future__ import annotations

import argparse
import collections
import json
import os
import re
import shutil
import sys

REG_RE = re.compile(
    r'public static final RegistryObject<SoundEvent>\s+(\w+)\s*=\s*register\("([^"]+)"\);\s*(//[^\n]*)?')
NK_REG = '    public static final DeferredHolder<SoundEvent, SoundEvent> %s = register("%s");%s\n'

LANG_TARGETS = [
    ('assets/confluence/lang/de_de.json', 'assets/confluence/lang/de_de.json'),
    ('assets/confluence/lang/es_es.json', 'assets/confluence/lang/es_es.json'),
    ('assets/confluence/lang/lzh.json', 'assets/confluence/lang/lzh.json'),
    ('assets/confluence/lang/pt_br.json', 'assets/confluence/lang/pt_br.json'),
    ('assets/confluence/lang/ru_ru.json', 'assets/confluence/lang/ru_ru.json'),
    ('i18n/en_us.json', 'i18n/en_us.json'),
    ('i18n/zh_cn.json', 'i18n/zh_cn.json'),
]


def load_json(path: str):
    """Windows 上的 lang 文件常带 BOM —— `utf-8-sig` 必须排在 `utf-8` 前。"""
    with open(path, 'r', encoding='utf-8-sig') as fh:
        return json.load(fh, object_pairs_hook=collections.OrderedDict)


def dump_json(path: str, obj, bom: bool) -> None:
    enc = 'utf-8-sig' if bom else 'utf-8'
    with open(path, 'w', encoding=enc, newline='\r\n') as fh:
        json.dump(obj, fh, ensure_ascii=False, indent=2)
        fh.write('\n')


def has_bom(path: str) -> bool:
    with open(path, 'rb') as fh:
        return fh.read(3) == b'\xef\xbb\xbf'


def main() -> int:
    try:
        sys.stdout.reconfigure(encoding='utf-8', errors='replace')
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument('--src120', required=True, help='1.20 侧 ConfluenceOtherworld 模块根')
    ap.add_argument('--dest', required=True, help='1.21 侧 ConfluenceOtherworld 模块根')
    ap.add_argument('--src120-root', required=True, help='1.20 侧仓库根（i18n/ 在这一层）')
    ap.add_argument('--dest-root', required=True, help='1.21 侧仓库根（i18n/ 在这一层）')
    ap.add_argument('--apply', action='store_true', help='真正写盘（默认只报告）')
    ap.add_argument('--out', default='', help='报告输出路径')
    args = ap.parse_args()

    src, dst = args.src120, args.dest
    L: list[str] = []
    L.append('# 音效层搬迁（`tools/port2native/port_sounds.py` 输出）')
    L.append('')

    # ---------- 1. ModSoundEvents ----------
    p120 = os.path.join(src, 'src/main/java/org/confluence/mod/common/init/ModSoundEvents.java')
    p121 = os.path.join(dst, 'src/main/java/org/confluence/mod/common/init/ModSoundEvents.java')
    t120 = open(p120, encoding='utf-8').read()
    t121 = open(p121, encoding='utf-8').read()
    rows = [m for m in REG_RE.finditer(t120)]
    parsed = [(m.group(1), m.group(2), (m.group(3) or '').rstrip()) for m in rows]
    mismatch = [(f, n) for f, n, _c in parsed if n != f.lower()]
    have121 = set(re.findall(
        r'public static final DeferredHolder<SoundEvent, SoundEvent>\s+(\w+)\s*=', t121))
    only121 = sorted(have121 - {f for f, _n, _c in parsed})
    missing = [(f, n, c) for f, n, c in parsed if f not in have121]

    L.append('## 1. `ModSoundEvents`')
    L.append('')
    L.append(f'- 1.20 解析出 **{len(parsed)}** 条注册；字段名与注册名不一致的 **{len(mismatch)}** 条'
             + (f'：{mismatch[:5]}' if mismatch else ''))
    L.append(f'- 1.21 现有 **{len(have121)}** 条；需要新增 **{len(missing)}** 条')
    if only121:
        L.append(f'- ⚠️ 1.21 有而 1.20 没有（保留原样，不动）：{only121}')
    L.append('')

    if args.apply and missing:
        block = ''.join(NK_REG % (f, n, (' ' + c) if c else '') for f, n, c in missing)
        anchor = '    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {'
        assert anchor in t121, 'ModSoundEvents 里找不到 register 私有方法的锚点，请手工确认'
        note = ('    // 以下条目随 1.20.1 的 ModSoundEvents.java 一起搬迁'
                f'（工具 tools/port2native/port_sounds.py 生成，共 {len(missing)} 条）；\n'
                f'    // 上面的 {len(have121)} 条是 1.21 分支原有的，保持原样未动。\n')
        t121 = t121.replace(anchor, note + block + '\n' + anchor, 1)
        with open(p121, 'w', encoding='utf-8', newline='\r\n') as fh:
            fh.write(t121)

    # ---------- 2. sounds.json ----------
    s120 = os.path.join(src, 'src/main/resources/assets/confluence')
    s121 = os.path.join(dst, 'src/main/resources/assets/confluence')
    j120 = load_json(os.path.join(s120, 'sounds.json'))
    j121 = load_json(os.path.join(s121, 'sounds.json'))
    add_entries = collections.OrderedDict()
    changed_paths = []
    for k, v in j120.items():
        flat = collections.OrderedDict()
        for key in ('category', 'subtitle', 'replace', 'sounds'):
            if key not in v:
                continue
            if key == 'sounds':
                flat['sounds'] = [f"{s.split(':')[0]}:{s.split(':', 1)[1].rsplit('/', 1)[-1]}"
                                  if ':' in s else s.rsplit('/', 1)[-1] for s in v['sounds']]
            else:
                flat[key] = v[key]
        if k not in j121:
            add_entries[k] = flat
        elif json.dumps(j121[k], sort_keys=True) != json.dumps(flat, sort_keys=True):
            changed_paths.append(k)
    L.append('## 2. `sounds.json`')
    L.append('')
    L.append(f'- 1.20 **{len(j120)}** 条 / 1.21 **{len(j121)}** 条（1.21 是 1.20 的子集）')
    L.append(f'- 需要新增 **{len(add_entries)}** 条（路径已按 1.21 的**扁平**写法生成）')
    L.append(f'- 两侧都有的 {len(j121)} 条里，扁平化后仍与 1.21 现值不同的 **{len(changed_paths)}** 条'
             f'（保持 1.21 现值不动）'
             + (f'：{changed_paths[:5]}' if changed_paths else ''))
    L.append('')
    if args.apply and add_entries:
        merged = collections.OrderedDict()
        for k, v in list(j121.items()) + list(add_entries.items()):
            merged[k] = v
        dump_json(os.path.join(s121, 'sounds.json'), merged, has_bom(os.path.join(s121, 'sounds.json')))

    # ---------- 3. ogg ----------
    def oggs(root):
        out = {}
        base = os.path.join(root, 'sounds')
        for dp, _d, fs in os.walk(base):
            for f in fs:
                if f.lower().endswith('.ogg'):
                    out[f] = os.path.join(dp, f)
        return out
    o120, o121 = oggs(s120), oggs(s121)
    copy_list, identical, conflict = [], [], []
    for name, path in sorted(o120.items()):
        if name not in o121:
            copy_list.append(name)
        elif open(path, 'rb').read() == open(o121[name], 'rb').read():
            identical.append(name)
        else:
            conflict.append(name)
    L.append('## 3. `.ogg` 资源')
    L.append('')
    L.append(f'- 1.20 @ 磁盘 **{len(o120)}** 个 / 1.21 **{len(o121)}** 个')
    L.append(f'- 需要拷贝 **{len(copy_list)}** 个（1.21 的 `sounds/` 是扁平布局，按 basename 落盘）')
    L.append(f'- 已存在且**逐字节相同**：**{len(identical)}** 个（跳过）')
    L.append(f'- ⚠️ 已存在但内容不同（**不覆盖**）：**{len(conflict)}** 个'
             + (f'：{conflict[:10]}' if conflict else ''))
    L.append('')
    if args.apply and copy_list:
        target = os.path.join(s121, 'sounds')
        os.makedirs(target, exist_ok=True)
        for name in copy_list:
            shutil.copy2(o120[name], os.path.join(target, name))

    # ---------- 4. 字幕 lang ----------
    L.append('## 4. 字幕 lang 键（`confluence.subtitle.*`）')
    L.append('')
    L.append('| 文件 | 1.20 有 | 1.21 现有 | 可补 |')
    L.append('|---|---|---|---|')
    for rel_src, rel_dst in LANG_TARGETS:
        # `assets/**` 在模块内；`i18n/**` 在**仓库根**（不在模块里）。
        if rel_src.startswith('assets'):
            ps = os.path.join(src, 'src/main/resources', rel_src)
            pd = os.path.join(dst, 'src/main/resources', rel_dst)
        else:
            ps = os.path.join(args.src120_root, rel_src)
            pd = os.path.join(args.dest_root, rel_dst)
        if not os.path.isfile(ps) or not os.path.isfile(pd):
            L.append(f'| `{rel_dst}` | （缺一侧：{ps if not os.path.isfile(ps) else pd}） | | |')
            continue
        a, b = load_json(ps), load_json(pd)
        sub_a = {k: v for k, v in a.items() if k.startswith('confluence.subtitle.')}
        sub_b = {k: v for k, v in b.items() if k.startswith('confluence.subtitle.')}
        to_add = {k: v for k, v in sub_a.items() if k not in b}
        L.append(f'| `{rel_dst}` | {len(sub_a)} | {len(sub_b)} | **{len(to_add)}** |')
        if args.apply and to_add:
            merged = collections.OrderedDict(b)
            for k, v in to_add.items():
                merged[k] = v
            if list(b.keys()) == sorted(b.keys()):
                merged = collections.OrderedDict(sorted(merged.items()))
            dump_json(pd, merged, has_bom(pd))
    L.append('')

    md = '\n'.join(L)
    if args.out:
        os.makedirs(os.path.dirname(os.path.abspath(args.out)), exist_ok=True)
        with open(args.out, 'w', encoding='utf-8', newline='\r\n') as fh:
            fh.write(md)
    else:
        sys.stdout.write(md)
    print(json.dumps({'registrations_to_add': len(missing), 'sounds_json_to_add': len(add_entries),
                      'ogg_to_copy': len(copy_list), 'ogg_conflict': len(conflict),
                      'applied': args.apply}, ensure_ascii=False))
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
