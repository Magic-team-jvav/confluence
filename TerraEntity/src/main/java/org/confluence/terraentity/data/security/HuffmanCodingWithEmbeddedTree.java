package org.confluence.terraentity.data.security;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class HuffmanCodingWithEmbeddedTree {
    private Map<Character, String> huffmanCodes;
    private HuffmanNode root;

    private void buildTree(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();
            HuffmanNode parent = new HuffmanNode(
                    left.frequency + right.frequency, left, right);
            priorityQueue.add(parent);
        }

        root = priorityQueue.poll();
        huffmanCodes = new HashMap<>();
        buildCodeTable(root, "");
    }

    private void buildCodeTable(HuffmanNode node, String code) {
        if (node == null) return;
        if (node.isLeaf()) {
            huffmanCodes.put(node.data, code);
            return;
        }
        buildCodeTable(node.left, code + "0");
        buildCodeTable(node.right, code + "1");
    }

    String encodeWithEmbeddedTree(String text) {
        if (huffmanCodes == null || huffmanCodes.isEmpty()) {
            buildTree(text);
        }

        String treeString = serializeTreeToString(root);
        String encodedText = encode(text);

        return treeString + "|" + encodedText;
    }

    private String serializeTreeToString(HuffmanNode node) {
        StringBuilder sb = new StringBuilder();
        serializeNode(node, sb);
        return sb.toString();
    }

    private void serializeNode(HuffmanNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("N");
            return;
        }

        if (node.isLeaf()) {
            sb.append("L");
            // 确保字符数据不为null
            if (node.data != null) {
                sb.append(escapeCharacter(node.data));
            }
            sb.append(",");
            sb.append(node.frequency);
            sb.append(",");
        } else {
            sb.append("B");
            sb.append(node.frequency);
            sb.append(",");
            serializeNode(node.left, sb);
            serializeNode(node.right, sb);
        }
    }

    private String escapeCharacter(char c) {
        // 对特殊字符进行转义处理
        if (c == '\\' || c == ',') {
            return "\\" + c;
        }
        return String.valueOf(c);
    }

    private HuffmanNode deserializeTreeFromString(StringReader reader) throws IOException {
        int c = reader.read();
        if (c == -1) return null;

        char type = (char) c;
        if (type == 'N') {
            return null;
        }

        // 对于叶子节点和分支节点，顺序不同
        if (type == 'L') {
            // 1. 先读取字符数据（处理转义）
            StringBuilder charBuilder = new StringBuilder();
            boolean escape = false;
            while ((c = reader.read()) != -1) {
                char ch = (char) c;
                if (escape) {
                    charBuilder.append(ch);
                    escape = false;
                } else if (ch == '\\') {
                    escape = true;
                } else if (ch == ',') {
                    break;
                } else {
                    charBuilder.append(ch);
                }
            }
            char data = charBuilder.length() > 0 ? charBuilder.charAt(0) : 0;

            // 2. 然后读取频率
            StringBuilder numBuilder = new StringBuilder();
            while ((c = reader.read()) != -1 && c != ',') {
                numBuilder.append((char) c);
            }
            int frequency = Integer.parseInt(numBuilder.toString());

            // 3. 跳过最后的逗号
            if (c == ',') {
                // 已经消费了逗号
            }

            return new HuffmanNode(data, frequency);
        } else if (type == 'B') {
            // 分支节点：先读取频率
            StringBuilder numBuilder = new StringBuilder();
            while ((c = reader.read()) != -1 && c != ',') {
                numBuilder.append((char) c);
            }
            int frequency = Integer.parseInt(numBuilder.toString());

            // 递归读取左右子树
            HuffmanNode left = deserializeTreeFromString(reader);
            HuffmanNode right = deserializeTreeFromString(reader);
            return new HuffmanNode(frequency, left, right);
        } else {
            throw new IOException(String.valueOf(type));
        }
    }

    String decodeWithEmbeddedTree(String encodedTextWithTree) throws IOException {
        String[] parts = encodedTextWithTree.split("\\|", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException();
        }

        String treeString = parts[0];
        String encodedText = parts[1];

        StringReader reader = new StringReader(treeString);
        root = deserializeTreeFromString(reader);

        huffmanCodes = new HashMap<>();
        buildCodeTable(root, "");

        return decode(encodedText);
    }

    private String encode(String text) {
        if (huffmanCodes == null || huffmanCodes.isEmpty()) {
            throw new IllegalStateException();
        }

        StringBuilder encodedText = new StringBuilder();
        for (char c : text.toCharArray()) {
            encodedText.append(huffmanCodes.get(c));
        }
        return encodedText.toString();
    }

    private String decode(String encodedText) {
        if (root == null) {
            throw new IllegalStateException();
        }

        StringBuilder decodedText = new StringBuilder();
        HuffmanNode current = root;

        for (char bit : encodedText.toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }

            if (current.isLeaf()) {
                decodedText.append(current.data);
                current = root;
            }
        }

        if (current != root) {
            throw new IllegalArgumentException();
        }

        return decodedText.toString();
    }

    /**
     * 哈夫曼树节点
     */
    static class HuffmanNode implements Comparable<HuffmanNode>, Serializable {
        Character data;
        int frequency;
        HuffmanNode left;
        HuffmanNode right;

        public HuffmanNode(Character data, int frequency) {
            this.data = data;
            this.frequency = frequency;
            this.left = null;
            this.right = null;
        }

        public HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
            this.data = null;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public int compareTo(HuffmanNode other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }
}