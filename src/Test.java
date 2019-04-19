import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

public class Test {
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
    }

    /**
     * Строит деревья в два прохода по списку узлов (при желании можно выполнять проходы параллельно)
     * @param nodes Список узлов
     * @return      Список корней построенных деревьев
     */
    private static List<TreeNode> buildTrees(Node[] nodes) {
        // Map<Id узла, Узел дерева>
        Map<Integer, TreeNode> treeMap = new HashMap<>();
        // Список корневых узлов
        List<TreeNode> trees = new ArrayList<>();
        // Заполняем узлы, пока не связывая их в деревья
        Stream<Node> nodesStream = Stream.of(nodes);
        nodesStream.forEach(node -> treeMap.put(node.id, new TreeNode(node.name)));
        // Связываем узлы в деревья
        nodesStream = Stream.of(nodes);
        nodesStream.forEach(node -> {
            if (node.parentId == -1) {
                trees.add(treeMap.get(node.id));
            } else {
                TreeNode parent = treeMap.get(node.parentId);
                TreeNode child = treeMap.get(node.id);
                if (parent == null) {
                    // skip incorrect node
                    System.out.println("Incorrect parentId, node = " + node);
                    return;
                }
                child.parent = parent;
                parent.children.add(child);
            }
        });

        return trees;
    }

    /**
     * Строит иерархическое строковое представление множества деревьев по их корням.
     * Построение ведется внутри одного строкового буфера типа StringBuilder.
     * Поскольку все элементы представления добавляются в один и тот же буфер,
     * то параллельная обработка деревьев будет невозможна.
     * @param roots Массив корней деревьев для обработки
     * @return      Строка, представляющая иерархию всех узлов заданных деревьев
     */
    private static String buildGraphicalTree(Node[] roots) {
        // Здесь будем формировать результат
        StringBuilder sb = new StringBuilder();
        buildTrees(roots).forEach(root -> root.buildGraphicalTree("", sb));
        // Параллельную обработку деревьев можно организовать следующим образом:
        //buildTrees(roots).parallelStream().forEach(root -> sb.append(root.buildGraphicalTreePar("")));
        // Недостаток этой реализации - необходимость многократного склеивания всех
        // построенных элементов в единый буфер, что в данном конкретном случае будет
        // перекрывать выгоду от распараллеливания
        return sb.toString();
    }

    /**
     * Исходное представление узлов
     */
    private static class Node {
        int id;
        String name;
        int parentId;

        public Node(int id, String name, int parentId) {
            this.id = id;
            this.name = name;
            this.parentId = parentId;
        }

        @Override
        public String toString() {
            return String.format("(id: %d, name: %s, parentId: %d)", id, name, parentId);
        }
    }

    /**
     * Предствление узлов дерева в виде связанной структуры данных
     */
    private static class TreeNode {
        /** Имя узла (для печати) */
        String nodeName;
        /** Родитель узла */
        TreeNode parent = null;
        /** Дети узла */
        List<TreeNode> children = new ArrayList<>();

        public TreeNode(String name) {
            nodeName = name;
        }

        /**
         * Рекурсивная функция построения графического представления поддерева с <i>этим</i> корнем.
         * @param prefix    Строковый префикс для представления узла на заданном уровне.
         * @param sb        Буфер для записи результата
         * @return          Строковое представление поддерева.
         */
        public void buildGraphicalTree(String prefix, StringBuilder sb) {
            sb.append(prefix + nodeName + '\n');
            children.forEach(child -> child.buildGraphicalTree(prefix + "- ", sb));
        }

        /**
         * Рекурсивная функция параллельного построения графического представления поддерева с <i>этим</i>
         * корнем. Каждый экземпляр фрейма функции содержит свой собственный строковый буфер
         * для сохранения результата работы, что позволяет распараллеливать работу.
         * @param prefix    Строковый префикс для представления узла на заданном уровне.
         * @return          Строковое представление поддерева.
         */
        public StringBuilder buildGraphicalTreePar(String prefix) {
            StringBuilder sb = new StringBuilder(prefix + nodeName + '\n');
            children.parallelStream().forEach(node -> sb.append(node.buildGraphicalTreePar(prefix + "- ")));
            return sb;
        }
    }
}
