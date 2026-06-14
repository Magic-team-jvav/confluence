function initializeCoreMod() {
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
    var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
    var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
    var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
    var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
    var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
    var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
    var FieldNode = Java.type('org.objectweb.asm.tree.FieldNode');

    return {
        'extend_boat_type_enum': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.entity.vehicle.Boat$Type'
            },
            'transformer': function(classNode) {
                // Guard: already transformed?
                for (var i = 0; i < classNode.fields.size(); i++) {
                    if (classNode.fields.get(i).name === 'confluence$boatTypesExtended') {
                        return classNode;
                    }
                }
                classNode.fields.add(new FieldNode(
                    Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
                    'confluence$boatTypesExtended', 'Z', null, null
                ));

                var clinit = null;
                for (var i = 0; i < classNode.methods.size(); i++) {
                    if (classNode.methods.get(i).name === '<clinit>') {
                        clinit = classNode.methods.get(i);
                        break;
                    }
                }
                if (clinit === null) return classNode;

                var instructions = clinit.instructions;
                var nodes = instructions.toArray();
                if (nodes === null || nodes.length === 0) return classNode;

                var retIdx = -1;
                for (var j = nodes.length - 1; j >= 0; j--) {
                    if (nodes[j].getOpcode() === Opcodes.RETURN) {
                        retIdx = j;
                        break;
                    }
                }
                if (retIdx === -1) return classNode;

                var boatType = 'net/minecraft/world/entity/vehicle/Boat$Type';
                var block = 'net/minecraft/world/level/block/Block';
                var modBoatTypes = 'org/confluence/mod/common/init/ModBoatTypes';
                var enumProxy = 'org/confluence/mod/common/init/ModBoatTypes$EnumProxy';
                var supplier = 'java/util/function/Supplier';

                var enumProxyDesc = 'L' + enumProxy + ';';
                var planksFieldDesc = 'L' + supplier + ';';
                var stringDesc = 'Ljava/lang/String;';
                var constructorDesc = '(Ljava/lang/String;IL' + block + ';Ljava/lang/String;)V';

                var baseLocals = clinit.maxLocals;
                var oldArrVar = baseLocals;
                var newArrVar = baseLocals + 1;
                var proxyVar = baseLocals + 2;
                var planksVar = baseLocals + 3;
                var nameVar = baseLocals + 4;
                var newInstVar = baseLocals + 5;
                clinit.maxLocals = baseLocals + 6;

                var boatTypes = [
                    { proxy: 'ASH',              enumName: 'CONFLUENCE_ASH' },
                    { proxy: 'BAOBAB',           enumName: 'CONFLUENCE_BAOBAB' },
                    { proxy: 'EBONY',            enumName: 'CONFLUENCE_EBONY' },
                    { proxy: 'GLOWING_MUSHROOM', enumName: 'CONFLUENCE_GLOWING_MUSHROOM' },
                    { proxy: 'LIVING',           enumName: 'CONFLUENCE_LIVING' },
                    { proxy: 'LIVING_MAHOGANY',  enumName: 'CONFLUENCE_LIVING_MAHOGANY' },
                    { proxy: 'PALM',             enumName: 'CONFLUENCE_PALM' },
                    { proxy: 'PEARL',            enumName: 'CONFLUENCE_PEARL' },
                    { proxy: 'SHADOW',           enumName: 'CONFLUENCE_SHADOW' },
                    { proxy: 'SPOOKY',           enumName: 'CONFLUENCE_SPOOKY' },
                    { proxy: 'YELLOW_WILLOW',    enumName: 'CONFLUENCE_YELLOW_WILLOW' },
                    { proxy: 'DYNASTY',          enumName: 'CONFLUENCE_DYNASTY' },
                    { proxy: 'PINE',             enumName: 'CONFLUENCE_PINE' },
                    { proxy: 'FEY',              enumName: 'CONFLUENCE_FEY' }
                ];

                var list = new InsnList();

                // oldArr = $VALUES
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, boatType, '$VALUES', '[L' + boatType + ';'));
                list.add(new VarInsnNode(Opcodes.ASTORE, oldArrVar));

                // newArr = new Boat$Type[oldArr.length + boatTypes.length]
                list.add(new VarInsnNode(Opcodes.ALOAD, oldArrVar));
                list.add(new InsnNode(Opcodes.ARRAYLENGTH));
                list.add(new LdcInsnNode(boatTypes.length));
                list.add(new InsnNode(Opcodes.IADD));
                list.add(new TypeInsnNode(Opcodes.ANEWARRAY, boatType));
                list.add(new VarInsnNode(Opcodes.ASTORE, newArrVar));

                // System.arraycopy(oldArr, 0, newArr, 0, oldArr.length)
                list.add(new VarInsnNode(Opcodes.ALOAD, oldArrVar));
                list.add(new InsnNode(Opcodes.ICONST_0));
                list.add(new VarInsnNode(Opcodes.ALOAD, newArrVar));
                list.add(new InsnNode(Opcodes.ICONST_0));
                list.add(new VarInsnNode(Opcodes.ALOAD, oldArrVar));
                list.add(new InsnNode(Opcodes.ARRAYLENGTH));
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, 'java/lang/System', 'arraycopy',
                    '(Ljava/lang/Object;ILjava/lang/Object;II)V', false));

                // For each boat type: create enum instance, set on proxy, add to newArr
                for (var k = 0; k < boatTypes.length; k++) {
                    var bt = boatTypes[k];

                    // proxyVar = ModBoatTypes.{proxy}
                    list.add(new FieldInsnNode(Opcodes.GETSTATIC, modBoatTypes, bt.proxy, enumProxyDesc));
                    list.add(new VarInsnNode(Opcodes.ASTORE, proxyVar));

                    // planksVar = (Block) ((Supplier) proxyVar.planks.get()).get()
                    list.add(new VarInsnNode(Opcodes.ALOAD, proxyVar));
                    list.add(new FieldInsnNode(Opcodes.GETFIELD, enumProxy, 'planks', planksFieldDesc));
                    list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, supplier, 'get', '()Ljava/lang/Object;', true));
                    list.add(new TypeInsnNode(Opcodes.CHECKCAST, supplier));
                    list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, supplier, 'get', '()Ljava/lang/Object;', true));
                    list.add(new TypeInsnNode(Opcodes.CHECKCAST, block));
                    list.add(new VarInsnNode(Opcodes.ASTORE, planksVar));

                    // nameVar = proxyVar.name
                    list.add(new VarInsnNode(Opcodes.ALOAD, proxyVar));
                    list.add(new FieldInsnNode(Opcodes.GETFIELD, enumProxy, 'name', stringDesc));
                    list.add(new VarInsnNode(Opcodes.ASTORE, nameVar));

                    // ordinal = oldArr.length + k
                    var ordinal = new InsnList();
                    ordinal.add(new VarInsnNode(Opcodes.ALOAD, oldArrVar));
                    ordinal.add(new InsnNode(Opcodes.ARRAYLENGTH));
                    if (k <= 5) {
                        ordinal.add(new InsnNode(Opcodes.ICONST_0 + k));
                    } else {
                        ordinal.add(new LdcInsnNode(k));
                    }
                    ordinal.add(new InsnNode(Opcodes.IADD));
                    list.add(ordinal);

                    // newInstVar = new Boat$Type(enumName, ordinal, planksVar, nameVar)
                    list.add(new TypeInsnNode(Opcodes.NEW, boatType));
                    list.add(new InsnNode(Opcodes.DUP));
                    list.add(new LdcInsnNode(bt.enumName));
                    list.add(new VarInsnNode(Opcodes.ALOAD, planksVar));
                    list.add(new VarInsnNode(Opcodes.ALOAD, nameVar));
                    list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, boatType, '<init>', constructorDesc, false));
                    list.add(new VarInsnNode(Opcodes.ASTORE, newInstVar));

                    // proxyVar.setValue(newInstVar)
                    list.add(new VarInsnNode(Opcodes.ALOAD, proxyVar));
                    list.add(new VarInsnNode(Opcodes.ALOAD, newInstVar));
                    list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, enumProxy, 'setValue', '(Ljava/lang/Enum;)V', false));

                    // newArr[oldArr.length + k] = newInstVar
                    list.add(new VarInsnNode(Opcodes.ALOAD, newArrVar));
                    list.add(new VarInsnNode(Opcodes.ALOAD, oldArrVar));
                    list.add(new InsnNode(Opcodes.ARRAYLENGTH));
                    if (k <= 5) {
                        list.add(new InsnNode(Opcodes.ICONST_0 + k));
                    } else {
                        list.add(new LdcInsnNode(k));
                    }
                    list.add(new InsnNode(Opcodes.IADD));
                    list.add(new VarInsnNode(Opcodes.ALOAD, newInstVar));
                    list.add(new InsnNode(Opcodes.AASTORE));
                }

                // $VALUES = newArr
                list.add(new VarInsnNode(Opcodes.ALOAD, newArrVar));
                list.add(new FieldInsnNode(Opcodes.PUTSTATIC, boatType, '$VALUES', '[L' + boatType + ';'));

                instructions.insertBefore(nodes[retIdx], list);

                return classNode;
            }
        }
    };
}
