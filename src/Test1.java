import java.util.Arrays;

/**
 * Задача: Вынести список узлов в виде превовидного графического
 * представления, например:
 * Parent1
 * - Child1-1
 * - Child1-2
 * - SubParent1-1
 * -  - Child1-1-1
 * -  - Child1-1-2
 * Parent2
 * - Child2-1
 * - Child2-2
 * - Child2-3
 * Parent3
 */

public class Test1 {
    public static void main(String[] args) {
        Node[] nodes = {
                new Node(1, "Parent1", -1),
                new Node(11, "Child1-1", 1),
                new Node(12, "Child1-2", 1),
                new Node(13, "SubParent1-1", 1),
                new Node(131, "Child1-1-1", 13),
                new Node(132, "Child1-1-2", 13),
                new Node(21, "Child2-1", 2),
                new Node(3, "Parent3", -1),
                new Node(2000, "Child2-2", 2),
                new Node(400, "Child2-3", 2),
                new Node(2, "Parent2", -1),
        };

        System.out.println(buildGraphicalTree(nodes));
        //System.out.println("[" + repeatDashes(0) + "]");
    }

    private static String buildGraphicalTree(Node[] nodes) {
        StringBuilder sb = new StringBuilder();
        for (Node node : nodes) {
            if (node.parentId == -1) {
                sb.append(buildGraphicalTree("", node, nodes));
            }
        }
        return sb.toString();
    }

    private static StringBuilder buildGraphicalTree(String prefix, Node node, Node[] nodes) {
        StringBuilder sb = new StringBuilder(prefix + node.name + '\n');
        for (Node n : nodes) {
            if (n.parentId == node.id) {
                sb.append(buildGraphicalTree(prefix + "- ", n, nodes));
            }
        }
        return sb;
    }

    private static class Node {
        int id;
        String name;
        int parentId;

        public Node(int id, String name, int parentId) {
            this.id = id;
            this.name = name;
            this.parentId = parentId;
        }
    }
}
