"""Convert Port*Extension static calls in TerraCurio to direct instance calls."""
import os
import re

ROOT = r"D:\Minecraft\1.20forge\confluence\TerraCurio"
SRC = os.path.join(ROOT, "src")

REPLACEMENTS = [
    # PortEntityExtension -> attachment methods are RENAMED
    ("PortEntityExtension.getAttach(entity, ", "entity.getAttachment("),
    ("PortEntityExtension.getAttach(living, ", "living.getAttachment("),
    ("PortEntityExtension.getAttach(player, ", "player.getAttachment("),
    ("PortEntityExtension.getExistingAttachOrNull(entity, ", "entity.getExistingAttachmentOrNull("),
    ("PortEntityExtension.getExistingAttachOrNull(living, ", "living.getExistingAttachmentOrNull("),
    ("PortEntityExtension.removeAttach(entity, ", "entity.removeAttachment("),
    ("PortEntityExtension.removeAttach(living, ", "living.removeAttachment("),
    ("PortEntityExtension.setAttach(entity, ", "entity.setAttachment("),
    ("PortEntityExtension.setAttach(living, ", "living.setAttachment("),
    ("PortEntityExtension.hasAttach(entity, ", "entity.hasAttachment("),
    ("PortEntityExtension.hasAttach(living, ", "living.hasAttachment("),
    ("PortEntityExtension.syncAttach(entity, ", "entity.syncAttachment("),
    ("PortEntityExtension.syncAttach(living, ", "living.syncAttachment("),

    # PortEntityExtension -> regular methods
    ("PortEntityExtension.getInBlockState(this)", "this.getInBlockState()"),
    ("PortEntityExtension.getInBlockState(entity)", "entity.getInBlockState()"),
    ("PortEntityExtension.getRandom(this)", "this.getRandom()"),
    ("PortEntityExtension.getRandom(entity)", "entity.getRandom()"),
    ("PortEntityExtension.registryAccess(entity)", "entity.registryAccess()"),
    ("PortEntityExtension.igniteForTicks(this, ", "this.igniteForTicks("),
    ("PortEntityExtension.igniteForTicks(entity, ", "entity.igniteForTicks("),
    ("PortEntityExtension.getKnownMovement(entity)", "entity.getKnownMovement()"),
    ("PortEntityExtension.getWeaponItem(this)", "this.getWeaponItem()"),
    ("PortEntityExtension.getWeaponItem(entity)", "entity.getWeaponItem()"),
    ("PortEntityExtension.copyAttachmentsFrom(entity, ", "entity.copyAttachmentsFrom("),

    # PortAABBExtension
    ("PortAABBExtension.getMinPosition(boundingBox)", "boundingBox.getMinPosition()"),
    ("PortAABBExtension.getMaxPosition(boundingBox)", "boundingBox.getMaxPosition()"),
    ("PortAABBExtension.getMinPosition(aabb)", "aabb.getMinPosition()"),
    ("PortAABBExtension.getMaxPosition(aabb)", "aabb.getMaxPosition()"),

    # PortHolderLookupExtension -> IPortHolderLookupProviderExtension
    ("PortHolderLookupExtension.Provider.createSerializationContext(provider, ", "provider.createSerializationContext("),

    # PortPlayerExtension (from earlier TCClientPacketHandler)
    ("PortPlayerExtension.hasInfiniteMaterials(player)", "player.hasInfiniteMaterials()"),
    ("PortPlayerExtension.blockInteractionRange(player)", "player.blockInteractionRange()"),
    ("PortPlayerExtension.entityInteractionRange(player)", "player.entityInteractionRange()"),
    ("PortPlayerExtension.hasCorrectToolForDrops(player, ", "player.hasCorrectToolForDrops("),

    # PortItemStackExtension
    ("PortItemStackExtension.getData(stack, ", "stack.getData("),
    ("PortItemStackExtension.getData(itemStack, ", "itemStack.getData("),
    ("PortItemStackExtension.getDataOrDefault(stack, ", "stack.getDataOrDefault("),
    ("PortItemStackExtension.getDataOrDefault(itemStack, ", "itemStack.getDataOrDefault("),
    ("PortItemStackExtension.setData(stack, ", "stack.setData("),
    ("PortItemStackExtension.setData(itemStack, ", "itemStack.setData("),
    ("PortItemStackExtension.hasData(stack, ", "stack.hasData("),
    ("PortItemStackExtension.hasData(itemStack, ", "itemStack.hasData("),
    ("PortItemStackExtension.removeData(stack, ", "stack.removeData("),
    ("PortItemStackExtension.removeData(itemStack, ", "itemStack.removeData("),
    ("PortItemStackExtension.getPrototypeData(stack)", "stack.getPrototypeData()"),
    ("PortItemStackExtension.getCustomData(stack, ", "stack.getCustomData("),
    ("PortItemStackExtension.setCustomData(stack, ", "stack.setCustomData("),
    ("PortItemStackExtension.getPortAttributeModifiers(stack)", "stack.getPortAttributeModifiers()"),
    ("PortItemStackExtension.is(stack, ", "stack.is("),
    ("PortItemStackExtension.consume(stack, ", "stack.consume("),
    ("PortItemStackExtension.hurtAndBreak(stack, ", "stack.hurtAndBreak("),
    ("PortItemStackExtension.getFood(stack, ", "stack.getFood("),
    ("PortItemStackExtension.setFood(stack, ", "stack.setFood("),
    ("PortItemStackExtension.getTool(stack)", "stack.getTool()"),
    ("PortItemStackExtension.setTool(stack, ", "stack.setTool("),
    ("PortItemStackExtension.getEnchantmentLevel(stack, ", "stack.getEnchantmentLevel("),

    # PortMobEffectInstanceExtension
    ("PortMobEffectInstanceExtension.getPortCures(instance)", "instance.getPortCures()"),
    ("PortMobEffectInstanceExtension.is(instance, ", "instance.is("),

    # PortHolderExtension
    ("PortHolderExtension.getData(stack.getItemHolder(), ", "stack.getItemHolder().getData("),
    ("PortHolderExtension.getData(item.getItemHolder(), ", "item.getItemHolder().getData("),
    ("PortHolderExtension.getData(holder, ", "holder.getData("),
    ("PortHolderExtension.is(holder, ", "holder.is("),
    ("PortHolderExtension.getRegisteredName(holder)", "holder.getRegisteredName()"),
    ("PortHolderExtension.getKey(holder)", "holder.getKey()"),

    # PortEntityExtension more
    ("PortEntityExtension.getKnownMovement(living)", "living.getKnownMovement()"),
    ("PortEntityExtension.getKnownMovement(this)", "this.getKnownMovement()"),
]

IMPORT_RE = re.compile(r'^import\s+PortLib\.extensions\..*\.(Port\w+Extension)\s*;')

def get_used_static_exts(text):
    return set(re.findall(r'\b(Port\w+Extension)\.', text))

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        text = f.read()

    changed = False
    for old, new in REPLACEMENTS:
        if old in text:
            text = text.replace(old, new)
            changed = True

    if not changed:
        return False

    used = get_used_static_exts(text)
    lines = text.split('\n')
    new_lines = []
    for line in lines:
        m = IMPORT_RE.match(line)
        if m and m.group(1) not in used:
            continue
        new_lines.append(line)

    text = '\n'.join(new_lines)

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(text)
    return True

if __name__ == '__main__':
    count = 0
    for root, dirs, files in os.walk(SRC):
        for f in files:
            if f.endswith('.java'):
                fp = os.path.join(root, f)
                if fix_file(fp):
                    count += 1
                    print(f"Fixed: {os.path.relpath(fp, ROOT)}")
    print(f"\nTotal: {count}")
