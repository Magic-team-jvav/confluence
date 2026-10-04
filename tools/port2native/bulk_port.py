"""按「整批」移植 1.20 → 1.21 的辅助工具（本次 WP2 全量收口与 BOSS 批用的就是这套口径）。

子命令：
  copy-classes   把 1.20 里 1.21 缺失的 .java 类按固定替换表拷过来（不自作主张改逻辑）
  registrations  抽取 1.20 注册类的成员声明块，机械替换后插入 1.21 对应文件
  renderers      抽取 1.20 ModClientEvents 的 registerEntityRenderer 行并插入
  groups         抽取 1.20 的 group(...) 放置登记（只保留 1.21 已注册的成员）
  predicates     抽取 1.20 的谓词方法体（含 /// 注释）并插入
  systemic       批量套用已知的系统性 API 差异（颜色尾参、applyRotations 6 参、
                 defineSynchedData Builder、VertexConsumer 改名、NeoForge 重命名）
  imports        为某个文件自动补齐缺失 import（保留 import static）

用法示例：
  python tools/port2native/bulk_port.py copy-classes --roots common/entity/boss client/entity/renderer
  python tools/port2native/bulk_port.py registrations --src120 <1.20 根> --from MonsterEntities --to MonsterEntities
  python tools/port2native/bulk_port.py systemic --roots common/entity/boss client/entity/renderer
  python tools/port2native/bulk_port.py imports --file <1.21 java 路径>

约定：所有操作都基于「1.20 原文 + 固定替换表」，不重写逻辑；替换表见 SUBS。
"""

import argparse
import os
import re
import sys

SUBS = [
    ("org.confluence.mod.common.init.entity.ModEntities", "org.confluence.mod.common.init.entity.ModEntities"),
    ("software.bernie.geckolib.core.animatable.model.CoreGeoBone", "software.bernie.geckolib.cache.object.GeoBone"),
    ("software.bernie.geckolib.core.animation.AnimationState", "software.bernie.geckolib.animation.AnimationState"),
    ("software.bernie.geckolib.core.animation.RawAnimation", "software.bernie.geckolib.animation.RawAnimation"),
    ("software.bernie.geckolib.core.animation.AnimationController", "software.bernie.geckolib.animation.AnimationController"),
    ("software.bernie.geckolib.core.animation.AnimatableManager", "software.bernie.geckolib.animation.AnimatableManager"),
    ("software.bernie.geckolib.core.object.", "software.bernie.geckolib.animation."),
    ("software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache",
     "software.bernie.geckolib.animatable.instance.AnimatableInstanceCache"),
    ("software.bernie.geckolib.util.RenderUtils", "software.bernie.geckolib.util.RenderUtil"),
    ("javax.annotation.Nullable", "org.jetbrains.annotations.Nullable"),
    ("net.minecraftforge.entity.PartEntity", "net.neoforged.neoforge.entity.PartEntity"),
    ("net.minecraftforge.network.PlayMessages", "net.neoforged.neoforge.network.PlayMessages"),
    ("net.minecraftforge.common.", "net.neoforged.neoforge.common."),
    ("net.minecraftforge.event.", "net.neoforged.neoforge.event."),
    ("net.minecraftforge.client.event.", "net.neoforged.neoforge.client.event."),
]

COLOR_PARAMS = re.compile(r"float\s+red\s*,\s*float\s+green\s*,\s*float\s+blue\s*,\s*float\s+alpha")
SUPER4 = re.compile(r",\s*red\s*,\s*green\s*,\s*blue\s*,\s*alpha(\s*[,)])")
DECL = re.compile(r"public static final \w+<[^;=]*?>\s+([A-Z0-9_]+)\s*=")


def apply_subs(text):
    for a, b in SUBS:
        text = text.replace(a, b)
    return text


def read_text(path):
    return open(path, encoding="utf-8", newline="").read()


def write_text(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    open(path, "w", encoding="utf-8", newline="").write(text)


def copy_classes(args):
    ref = os.path.abspath(args.root121)
    copied = 0
    for rel in args.roots:
        src_root = os.path.join(os.path.abspath(args.src120), rel.replace("/", os.sep))
        if not os.path.isdir(src_root):
            print("skip (no such dir):", rel)
            continue
        for dp, _dirs, files in os.walk(src_root):
            for fn in files:
                if not fn.endswith(".java"):
                    continue
                s = os.path.join(dp, fn)
                target = os.path.join(ref, os.path.relpath(s, os.path.abspath(args.src120)))
                if os.path.exists(target):
                    continue
                write_text(target, apply_subs(read_text(s)))
                copied += 1
    print("copied:", copied)


def _insert_before(text, lines, idx):
    return lines[:idx] + text.splitlines() + [""] + lines[idx:]


def registrations(args):
    src = os.path.abspath(args.src120)
    ref = os.path.abspath(args.root121)
    src_file = os.path.join(src, args.from_file)
    dst_file = os.path.join(ref, args.to_file)
    src_lines = read_text(src_file).splitlines()
    dst_text = read_text(dst_file)
    present = set(DECL.findall(dst_text))

    blocks, i = [], 0
    while i < len(src_lines):
        m = DECL.search(src_lines[i])
        if m and m.group(1) not in present:
            stmt = [src_lines[i]]
            j = i
            while not stmt[-1].rstrip().endswith(");") and j + 1 < len(src_lines):
                j += 1
                stmt.append(src_lines[j])
            blocks.append("\n".join(apply_subs("\n".join(stmt)).splitlines()))
            i = j
        i += 1

    out = []
    for b in blocks:
        b = b.replace("public static final RegistryObject<EntityType<",
                      "public static final DeferredHolder<EntityType<?>, EntityType<")
        b = b.replace("LibAttributes.getArmorPenetration().get()", "LibAttributes.getArmorPenetration()")
        out.append(b)
    text = "\n\n".join(out)
    lines = read_text(dst_file).splitlines()
    idx = next(i for i, l in enumerate(lines) if l.startswith("    private static ") or l.startswith("    public static void "))
    write_text(dst_file, "\n".join(_insert_before(text, lines, idx)) + "\n")
    print(f"inserted {len(blocks)} registrations into {args.to_file}")


def renderers(args):
    src = os.path.abspath(args.src120)
    ref = os.path.abspath(args.root121)
    dst_file = os.path.join(ref, args.to_file)
    dst_text = read_text(dst_file)
    present = set(re.findall(r"MonsterEntities\.([A-Z0-9_]+)\.get\(\)", dst_text))
    registered = set(DECL.findall(read_text(os.path.join(ref, args.entity_file))))
    need = sorted(registered - present)

    src_render = read_text(os.path.join(src, args.from_file)).splitlines()
    by_member = {}
    for i, l in enumerate(src_render):
        m = re.search(r"registerEntityRenderer\(MonsterEntities\.([A-Z0-9_]+)\.get\(\)", l)
        if m and m.group(1) in need:
            by_member[m.group(1)] = apply_subs(l.rstrip())

    missing = [n for n in need if n not in by_member]
    text = "\n".join(by_member[n] for n in need if n in by_member)
    lines = read_text(dst_file).splitlines()
    idx = next(i for i, l in enumerate(lines) if "registerBlockEntityRenderer" in l)
    write_text(dst_file, "\n".join(_insert_before(text, lines, idx)) + "\n")
    print(f"inserted {len(by_member)} renderer lines; no 1.20 line for: {missing}")


def groups(args):
    src = os.path.abspath(args.src120)
    ref = os.path.abspath(args.root121)
    sp_dst = os.path.join(ref, args.spawn_placements)
    registered = set(DECL.findall(read_text(os.path.join(ref, args.entity_file))))
    covered = set(re.findall(r"MonsterEntities\.([A-Z0-9_]+)\b", read_text(sp_dst)))
    need = registered - covered

    src_lines = read_text(os.path.join(src, args.spawn_placements_src)).splitlines()
    out, i = [], 0
    while i < len(src_lines):
        if "group(" in src_lines[i]:
            stmt = [src_lines[i]]
            j = i
            while not stmt[-1].rstrip().endswith(");") and j + 1 < len(src_lines):
                j += 1
                stmt.append(src_lines[j])
            text = "\n".join(stmt)
            keep = [m for m in re.findall(r"MonsterEntities\.([A-Z0-9_]+)", text) if m in need]
            if keep:
                preds = re.findall(r"SpawnPlacementChecks::(\w+)", text)
                placement = "NO_RESTRICTIONS" if "NO_RESTRICTIONS" in text else ("IN_WATER" if "IN_WATER" in text else "ON_GROUND")
                hard = "SpawnPlacementChecks.hardmode(" in text
                pred = preds[0] if preds else "checkMobSpawnRules"
                ref_expr = f"SpawnPlacementChecks.hardmode(SpawnPlacementChecks::{pred})" if hard else f"SpawnPlacementChecks::{pred}"
                body = ", ".join("MonsterEntities." + m for m in keep)
                out.append(f"        group(event, SpawnPlacementTypes.{placement}, {ref_expr},\n                {body});")
            i = j
        i += 1
    lines = read_text(sp_dst).splitlines()
    idx = max(i for i, l in enumerate(lines) if l.strip().startswith("group("))
    write_text(sp_dst, "\n".join(lines[:idx + 1] + [""] + out + lines[idx + 1:]) + "\n")
    print(f"inserted {len(out)} groups")


def predicates(args):
    src = os.path.abspath(args.src120)
    ref = os.path.abspath(args.root121)
    dst_file = os.path.join(ref, args.spawn_placements)
    have = set(re.findall(r"public static boolean (\w+)\(", read_text(dst_file)))
    used = set(re.findall(r"SpawnPlacementChecks::(\w+)", read_text(os.path.join(ref, args.spawn_placements_src))))
    want = used - have
    src_lines = read_text(os.path.join(src, args.spawn_placements_src)).splitlines()
    blocks, i = [], 0
    while i < len(src_lines):
        m = re.search(r"public static boolean (\w+)\(", src_lines[i])
        if m and m.group(1) in want:
            start = i
            k = i - 1
            while k >= 0 and src_lines[k].strip().startswith("///"):
                start = k
                k -= 1
            stmt = src_lines[start:i]
            j = i
            while j < len(src_lines):
                stmt.append(src_lines[j])
                if src_lines[j].rstrip() == "    }":
                    break
                j += 1
            blocks.append(apply_subs("\n".join(stmt)).replace("PortTags.Biomes.", "Tags.Biomes."))
            i = j
        i += 1
    text = "\n\n".join(blocks)
    lines = read_text(dst_file).splitlines()
    idx = max(i for i, l in enumerate(lines) if l.startswith("    private ") or l.startswith("    public static"))
    write_text(dst_file, "\n".join(_insert_before(text, lines, idx)) + "\n")
    print(f"inserted {len(blocks)} predicates; still referenced but absent: {sorted(want - {re.search(chr(98)+'oolean (..w+)', b).group(1) for b in blocks})}")


def systemic(args):
    ref = os.path.abspath(args.root121)
    patched = 0
    for rel in args.roots:
        root = os.path.join(ref, rel.replace("/", os.sep))
        for dp, _dirs, files in os.walk(root):
            for fn in files:
                if not fn.endswith(".java"):
                    continue
                p = os.path.join(dp, fn)
                t = o = read_text(p)
                t = apply_subs(t)
                t = COLOR_PARAMS.sub("int colour", t)
                t = SUPER4.sub(r", colour\1", t)
                t = re.sub(r"\.vertex\(", ".addVertex(", t)
                t = re.sub(r"\.color\(", ".setColor(", t)
                t = re.sub(r"\.overlayCoords\(([^)]*)\)", r".setOverlay(\1)", t)
                t = re.sub(r"\.uv2\(([^)]*)\)", r".setLight(\1)", t)
                t = t.replace(".endVertex();", ";")
                t = t.replace("onAddedToWorld()", "onAddedToLevel()")
                t = re.sub(r"\bgetDimensions\(Pose (\w+)\)", r"getDefaultDimensions(Pose \1)", t)
                if "protected void defineSynchedData()" in t:
                    t = t.replace("protected void defineSynchedData() {",
                                  "protected void defineSynchedData(SynchedEntityData.Builder builder) {")
                    t = t.replace("        super.defineSynchedData();\n", "")
                    t = re.sub(r"this\.entityData\.define\(", "builder.define(", t)
                    if "import net.minecraft.network.syncher.SynchedEntityData;" not in t:
                        t = t.replace("import net.minecraft.network.syncher.EntityDataAccessor;",
                                      "import net.minecraft.network.syncher.EntityDataAccessor;\nimport net.minecraft.network.syncher.SynchedEntityData;")
                if t != o:
                    write_text(p, t)
                    patched += 1
    print("patched:", patched)


def imports(args):
    root = os.path.abspath(args.root121)
    p = os.path.join(root, args.file) if not os.path.isabs(args.file) else args.file
    text = read_text(p)
    lines = text.splitlines()
    plain = {l[len("import "):-1] for l in lines if l.startswith("import ") and not l.startswith("import static")}
    statics = [l for l in lines if l.startswith("import static")]
    body = re.sub(r"^import .*$", "", text, flags=re.M)
    refs = set(re.findall(r"\bnew ([A-Z]\w+)\s*[<(]", body)) | set(re.findall(r"\b([A-Z]\w+)::new\b", body))
    index = {}
    for dp, dirs, files in os.walk(root):
        dirs[:] = [d for d in dirs if d != "build"]
        for fn in files:
            if fn.endswith(".java"):
                index.setdefault(fn[:-5], os.path.join(dp, fn).replace(root + os.sep, "").replace(os.sep, ".")[:-5])
    add = [index[r] for r in sorted(refs) if r not in {x.rsplit('.', 1)[-1] for x in plain} and index.get(r, "").startswith("org.confluence.")]
    first = next(i for i, l in enumerate(lines) if l.startswith("import "))
    last = max(i for i, l in enumerate(lines) if l.startswith("import "))
    new_imports = [f"import {x};" for x in sorted(plain | set(add), key=lambda s: (s.rsplit('.', 1)[-1]))] + statics
    write_text(p, "\n".join(lines[:first] + new_imports + lines[last + 1:]) + "\n")
    print("imports added:", len(add))


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = ap.add_subparsers(dest="cmd", required=True)

    c = sub.add_parser("copy-classes")
    c.add_argument("--src120", required=True)
    c.add_argument("--root121", required=True)
    c.add_argument("--roots", nargs="+", required=True)
    c.set_defaults(func=copy_classes)

    c = sub.add_parser("registrations")
    c.add_argument("--src120", required=True)
    c.add_argument("--root121", required=True)
    c.add_argument("--from-file", dest="from_file", required=True)
    c.add_argument("--to-file", dest="to_file", required=True)
    c.set_defaults(func=registrations)

    c = sub.add_parser("renderers")
    c.add_argument("--src120", required=True)
    c.add_argument("--root121", required=True)
    c.add_argument("--from-file", dest="from_file", required=True)
    c.add_argument("--to-file", dest="to_file", required=True)
    c.add_argument("--entity-file", dest="entity_file", required=True)
    c.set_defaults(func=renderers)

    c = sub.add_parser("groups")
    c.add_argument("--src120", required=True)
    c.add_argument("--root121", required=True)
    c.add_argument("--spawn-placements-src", dest="spawn_placements_src", required=True)
    c.add_argument("--spawn-placements", dest="spawn_placements", required=True)
    c.add_argument("--entity-file", dest="entity_file", required=True)
    c.set_defaults(func=groups)

    c = sub.add_parser("predicates")
    c.add_argument("--src120", required=True)
    c.add_argument("--root121", required=True)
    c.add_argument("--spawn-placements-src", dest="spawn_placements_src", required=True)
    c.add_argument("--spawn-placements", dest="spawn_placements", required=True)
    c.set_defaults(func=predicates)

    c = sub.add_parser("systemic")
    c.add_argument("--root121", required=True)
    c.add_argument("--roots", nargs="+", required=True)
    c.set_defaults(func=systemic)

    c = sub.add_parser("imports")
    c.add_argument("--root121", required=True)
    c.add_argument("--file", required=True)
    c.set_defaults(func=imports)

    args = ap.parse_args()
    args.func(args)


if __name__ == "__main__":
    sys.exit(main())
