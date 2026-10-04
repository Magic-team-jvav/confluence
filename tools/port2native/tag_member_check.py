"""Check 1.21-side existence of every member referenced by the 1.20-only tag blocks."""
import re
import subprocess

R20 = r"D:\Minecraft\1.20forge\confluence"
R21 = r"D:\Minecraft\1.21neoforge\confluence"
P = "ConfluenceOtherworld/src/main/java/org/confluence/mod/common/data/gen/tag/ModEntityTypeTagsProvider.java"

WANT = ["ARTHROPOD", "AQUATIC", "ZOMBIES", "SKELETONS", "UNDEAD", "POWDER_SNOW_WALKABLE_MOBS",
        "AXOLOTL_ALWAYS_HOSTILES", "AXOLOTL_HUNT_TARGETS", "FREEZE_IMMUNE_ENTITY_TYPES",
        "CAN_BREATHE_UNDER_WATER", "FALL_DAMAGE_IMMUNE", "FLESH_ALLIANCE", "JELLY_FISH", "SLIME"]

s = subprocess.run(["git", "-C", R20, "show", f"HEAD:{P}"], capture_output=True).stdout.decode("utf-8", "replace")
lines = s.splitlines()

# collect member symbols per wanted block
blocks = {}
cur = None
for ln in lines:
    m = re.search(r"tag\(([^)]*)\)", ln)
    if m:
        cur = m.group(1)
    for w in WANT:
        if cur and cur.endswith("." + w):
            blocks.setdefault(w, [])
            for sym in re.findall(r"[A-Za-z_][A-Za-z0-9_]*\.[A-Z0-9_]+", ln):
                if sym not in blocks[w]:
                    blocks[w].append(sym)

# classes declared in 1.21 sources: map simple class name -> file path (search whole 1.21 repo)
listing = subprocess.run(["git", "-C", R21, "ls-tree", "-r", "HEAD", "--name-only"],
                         capture_output=True).stdout.decode("utf-8", "replace").splitlines()
classfile = {}
for f in listing:
    if f.endswith(".java"):
        cls = f.rsplit("/", 1)[-1][:-5]
        classfile.setdefault(cls, f)

for w in WANT:
    mems = blocks.get(w)
    print("==", w, "==")
    if not mems:
        print("   (no members parsed)")
        continue
    for sym in mems:
        cls, field = sym.split(".", 1)
        f = classfile.get(cls)
        if not f:
            print(f"   ? {sym}  (class {cls} not found in 1.21)")
            continue
        blob = subprocess.run(["git", "-C", R21, "show", f"HEAD:{f}"], capture_output=True).stdout.decode("utf-8", "replace")
        ok = re.search(rf"(?<![A-Za-z0-9_]){field}(?![A-Za-z0-9_])", blob)
        print(f"   {'OK ' if ok else 'MISSING'} {sym}")
