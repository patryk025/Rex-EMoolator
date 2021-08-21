package pl.cba.genszu.amcodetranslator.lexer.tree;

public class BSTPrinter
{
	//adapted from https://www.baeldung.com/java-print-binary-tree-diagram
	
	public static void print(BinaryTree tree) {
		System.out.println(traversePreOrder(tree.root));
	}
	
	private static String traversePreOrder(Node root) {

		if (root == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append(root);

		String pointerRight = "└──";
		String pointerLeft = (root.right != null) ? "├──" : "└──";

		traverseNodes(sb, "", pointerLeft, root.left, root.right != null);
		traverseNodes(sb, "", pointerRight, root.right, false);

		return sb.toString();
	}
	
	private static void traverseNodes(StringBuilder sb, String padding, String pointer, Node node, 
							  boolean hasRightSibling) {
		if (node != null) {
			sb.append("\n");
			sb.append(padding);
			sb.append(pointer);
			sb.append(node);

			StringBuilder paddingBuilder = new StringBuilder(padding);
			if (hasRightSibling) {
				paddingBuilder.append("│  ");
			} else {
				paddingBuilder.append("   ");
			}

			String paddingForBoth = paddingBuilder.toString();
			String pointerRight = "└──";
			String pointerLeft = (node.right != null) ? "├──" : "└──";

			traverseNodes(sb, paddingForBoth, pointerLeft, node.left, node.right != null);
			traverseNodes(sb, paddingForBoth, pointerRight, node.right, false);
		}
	}
}
