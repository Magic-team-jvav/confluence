# 音效层搬迁（`tools/port2native/port_sounds.py` 输出）

## 1. `ModSoundEvents`

- 1.20 解析出 **501** 条注册；字段名与注册名不一致的 **1** 条：[('BLOWPIPE_SHOT', 'blowgun_shot')]
- 1.21 现有 **32** 条；需要新增 **469** 条

## 2. `sounds.json`

- 1.20 **503** 条 / 1.21 **503** 条（1.21 是 1.20 的子集）
- 需要新增 **0** 条（路径已按 1.21 的**扁平**写法生成）
- 两侧都有的 503 条里，扁平化后仍与 1.21 现值不同的 **2** 条（保持 1.21 现值不动）：['regular_staff_shoot_2', 'repeater_item_aerial_shooting']

## 3. `.ogg` 资源

- 1.20 @ 磁盘 **813** 个 / 1.21 **813** 个
- 需要拷贝 **0** 个（1.21 的 `sounds/` 是扁平布局，按 basename 落盘）
- 已存在且**逐字节相同**：**813** 个（跳过）
- ⚠️ 已存在但内容不同（**不覆盖**）：**0** 个

## 4. 字幕 lang 键（`confluence.subtitle.*`）

| 文件 | 1.20 有 | 1.21 现有 | 可补 |
|---|---|---|---|
| `assets/confluence/lang/de_de.json` | 62 | 62 | **0** |
| `assets/confluence/lang/es_es.json` | 101 | 101 | **0** |
| `assets/confluence/lang/lzh.json` | 99 | 99 | **0** |
| `assets/confluence/lang/pt_br.json` | 100 | 100 | **0** |
| `assets/confluence/lang/ru_ru.json` | 0 | 0 | **0** |
| `i18n/en_us.json` | 30 | 30 | **0** |
| `i18n/zh_cn.json` | 30 | 30 | **0** |
