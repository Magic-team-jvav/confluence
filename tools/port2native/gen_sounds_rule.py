#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""从 1.21.1 的 SoundEvents 源码重新生成 `rules/soundevents-holder-1.21.json`。

**为什么需要它**：1.21.1 的 `SoundEvents` 里只有一部分字段是 Holder 形态
（`Holder<SoundEvent>` 22 个 + `Holder.Reference<SoundEvent>` 95 个 = 117 个），
其余 1486 个仍是裸 `SoundEvent`。所以**不能**写「所有 SoundEvents.X 都补 .value()」的统配规则。
手工维护这 117 个名字容易漏（第一版就只收了 22 个 `Holder<SoundEvent>`，
漏掉整批 `Holder.Reference<SoundEvent>`，由 `DesertSpiritCurse` 的编译错误才暴露），
因此从源码生成。

用法：
  python tools/port2native/gen_sounds_rule.py --jar <neoforge-21.1.219-sources.jar>
"""

from __future__ import annotations

import argparse
import json
import os
import re
import zipfile


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument('--jar', required=True, help='1.21.1 的 sources jar')
    ap.add_argument('--out', default=os.path.join(os.path.dirname(os.path.abspath(__file__)),
                                                  'rules', 'soundevents-holder-1.21.json'))
    args = ap.parse_args()

    z = zipfile.ZipFile(args.jar)
    src = z.read('net/minecraft/sounds/SoundEvents.java').decode('utf-8', 'replace')
    z.close()

    names = sorted({m.group(2) for m in
                    re.finditer(r'public static final (Holder(?:\.Reference)?<SoundEvent>) (\w+) =', src)})
    if not names:
        print('未从 SoundEvents.java 解析出任何 Holder 形态字段，请检查 jar 内容')
        return 2

    rule = {
        'generatedFrom': (
            '由 tools/port2native/gen_sounds_rule.py 从 1.21.1 的 SoundEvents.java 生成。'
            '1.21.1 里只有 117 个字段是 Holder 形态（Holder<SoundEvent> 22 + '
            'Holder.Reference<SoundEvent> 95），其余 1486 个仍是裸 SoundEvent，所以不能统配。'
            '1.20.1 侧同名字段全是裸 SoundEvent，而 Entity#playSound 收的仍是 SoundEvent'
            '（Entity.java:1138/:1144），所以 1.20 写法的调用点一律要补 .value()。'),
        'kinds': {'safe': '1.21.1 侧该字段确认是 Holder 形态，补 .value() 后类型正确'},
        'rules': [{
            'id': 'soundevents-holder-value',
            'pattern': '\\bSoundEvents\\.(' + '|'.join(names) + ')\\b(?!\\.value\\()',
            'replace': 'SoundEvents.\\1.value()',
            'kind': 'safe',
            'typeImports': [],
            'addImports': False,
            'notes': (
                '这 %d 个名字 = 1.21.1 SoundEvents 里**全部**的 Holder 形态字段'
                '（22 个 Holder<SoundEvent> + 95 个 Holder.Reference<SoundEvent>）。'
                '其余 1486 个字段是裸 SoundEvent，**不要**加 .value()。'
                '负面示例（踩过）：Hoplite 的 playSound(SoundEvents.TRIDENT_THROW, ...) 报类型错，'
                '而同批 Piranha 的 playSound(SoundEvents.DOLPHIN_ATTACK, ...) 不用改。'
                '第一版规则只收了 22 个 Holder<SoundEvent>，漏掉整批 Holder.Reference，'
                '由 DesertSpiritCurse（GENERIC_EXPLODE）的编译错误暴露。' % len(names)),
        }],
    }
    with open(args.out, 'w', encoding='utf-8', newline='\r\n') as fh:
        json.dump(rule, fh, ensure_ascii=False, indent=2)
        fh.write('\n')

    # 自检：模式必须能编译，且能命中/不误伤代表性样本
    rx = re.compile(rule['rules'][0]['pattern'])
    checks = [
        ('playSound(SoundEvents.GENERIC_EXPLODE, 0.4F, 1.6F);', True),
        ('playSound(SoundEvents.TRIDENT_THROW, 1F, 1F);', True),
        ('playSound(SoundEvents.DOLPHIN_ATTACK, 1F, 1F);', False),
        ('SoundEvents.TRIDENT_THROW.value()', False),
    ]
    bad = [(s, want, bool(rx.search(s))) for s, want in checks if bool(rx.search(s)) != want]
    print('生成 %s：Holder 字段 %d 个，pattern 长度 %d' % (args.out, len(names), len(rule['rules'][0]['pattern'])))
    print('自检:', '通过' if not bad else ('失败 %s' % bad))
    return 0 if not bad else 3


if __name__ == '__main__':
    raise SystemExit(main())
