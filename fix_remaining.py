"""Fix remaining Port*Extension calls that the first script missed (edge cases)."""
import os
import re

ROOT = r"D:\Minecraft\1.20forge\confluence"
SRC = os.path.join(ROOT, "ConfluenceOtherworld", "src")

# Exact replacements: (old_pattern, new_pattern)
REPLACEMENTS = [
    # PortPlayerExtension
    ("PortPlayerExtension.hasInfiniteMaterials(player)", "player.hasInfiniteMaterials()"),
    ("PortPlayerExtension.blockInteractionRange(player)", "player.blockInteractionRange()"),
    ("PortPlayerExtension.entityInteractionRange(player)", "player.entityInteractionRange()"),
    ("PortPlayerExtension.hasCorrectToolForDrops(player, ", "player.hasCorrectToolForDrops("),

    # PortEntityExtension
    ("PortEntityExtension.getInBlockState(player)", "player.getInBlockState()"),
    ("PortEntityExtension.getInBlockState(entity)", "entity.getInBlockState()"),
    ("PortEntityExtension.getInBlockState(living)", "living.getInBlockState()"),
    ("PortEntityExtension.getRandom(entity)", "entity.getRandom()"),
    ("PortEntityExtension.getRandom(player)", "player.getRandom()"),
    ("PortEntityExtension.registryAccess(player)", "player.registryAccess()"),
    ("PortEntityExtension.registryAccess(entity)", "entity.registryAccess()"),
    ("PortEntityExtension.igniteForTicks(player, ", "player.igniteForTicks("),
    ("PortEntityExtension.igniteForTicks(entity, ", "entity.igniteForTicks("),
    ("PortEntityExtension.getKnownMovement(player)", "player.getKnownMovement()"),
    ("PortEntityExtension.getKnownMovement(entity)", "entity.getKnownMovement()"),
    ("PortEntityExtension.getWeaponItem(player)", "player.getWeaponItem()"),
    ("PortEntityExtension.getWeaponItem(entity)", "entity.getWeaponItem()"),
    ("PortEntityExtension.copyAttachmentsFrom(entity, ", "entity.copyAttachmentsFrom("),

    # PortItemStackExtension (remaining thiz methods)
    ("PortItemStackExtension.setData(stack, ", "stack.setData("),
    ("PortItemStackExtension.getData(stack, ", "stack.getData("),
    ("PortItemStackExtension.removeData(stack, ", "stack.removeData("),
    ("PortItemStackExtension.getDataOrDefault(stack, ", "stack.getDataOrDefault("),
    ("PortItemStackExtension.hasData(stack, ", "stack.hasData("),
    ("PortItemStackExtension.getPrototypeData(stack)", "stack.getPrototypeData()"),
    ("PortItemStackExtension.getCustomData(stack, ", "stack.getCustomData("),
    ("PortItemStackExtension.setCustomData(stack, ", "stack.setCustomData("),
    ("PortItemStackExtension.transmuteCopy(stack, ", "stack.transmuteCopy("),
    ("PortItemStackExtension.getFood(stack, ", "stack.getFood("),
    ("PortItemStackExtension.setFood(stack, ", "stack.setFood("),
    ("PortItemStackExtension.getTool(stack)", "stack.getTool()"),
    ("PortItemStackExtension.setTool(stack, ", "stack.setTool("),
    ("PortItemStackExtension.consume(stack, ", "stack.consume("),
    ("PortItemStackExtension.hurtAndBreak(stack, ", "stack.hurtAndBreak("),
    ("PortItemStackExtension.getEnchantmentLevel(stack, ", "stack.getEnchantmentLevel("),

    # PortItemStackExtension with other var names
    ("PortItemStackExtension.hasData(itemStack, ", "itemStack.hasData("),
    ("PortItemStackExtension.getDataOrDefault(itemStack, ", "itemStack.getDataOrDefault("),
    ("PortItemStackExtension.getData(itemStack, ", "itemStack.getData("),
    ("PortItemStackExtension.setData(itemStack, ", "itemStack.setData("),
    ("PortItemStackExtension.hasData(mainHandItem, ", "mainHandItem.hasData("),
    ("PortItemStackExtension.hasData(itemstack, ", "itemstack.hasData("),
    ("PortItemStackExtension.transmuteCopy(itemStack, ", "itemStack.transmuteCopy("),
    ("PortItemStackExtension.getFood(itemStack, ", "itemStack.getFood("),
    ("PortItemStackExtension.setFood(itemStack, ", "itemStack.setFood("),
    ("PortItemStackExtension.getTool(itemStack)", "itemStack.getTool()"),
    ("PortItemStackExtension.setTool(itemStack, ", "itemStack.setTool("),
    ("PortItemStackExtension.consume(itemStack, ", "itemStack.consume("),
    ("PortItemStackExtension.hurtAndBreak(itemStack, ", "itemStack.hurtAndBreak("),

    # PortDoubleBlockHalfExtension
    ("PortDoubleBlockHalfExtension.getDirectionToOther(originState.getValue(HALF))", "originState.getValue(HALF).getDirectionToOther()"),

    # PortBlockPosExtension
    ("PortBlockPosExtension.getBottomCenter(pos)", "pos.getBottomCenter()"),
    ("PortBlockPosExtension.getBottomCenter(blockPos)", "blockPos.getBottomCenter()"),

]

IMPORT_REMOVE = re.compile(r'^import\s+PortLib\.extensions\..*\.Port\w+Extension\s*;')

def get_used_static_exts(text):
    """Find which Port*Extension classes are still referenced as static calls."""
    return set(re.findall(r'\b(Port\w+Extension)\.', text))

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        text = f.read()

    original = text
    changed = False

    for old, new in REPLACEMENTS:
        if old in text:
            text = text.replace(old, new)
            changed = True

    if not changed:
        return False

    # Remove unused extension imports
    used = get_used_static_exts(text)
    lines = text.split('\n')
    new_lines = []
    for line in lines:
        m = IMPORT_REMOVE.match(line)
        if m:
            class_name = re.search(r'\.(Port\w+Extension)\s*;', line)
            if class_name and class_name.group(1) not in used:
                continue
        new_lines.append(line)

    text = '\n'.join(new_lines)

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(text)

    return True

count = 0
for root, dirs, files in os.walk(SRC):
    for f in files:
        if f.endswith('.java'):
            fp = os.path.join(root, f)
            if fix_file(fp):
                count += 1
                print(f"Fixed: {os.path.relpath(fp, ROOT)}")

print(f"\nTotal: {count}")
