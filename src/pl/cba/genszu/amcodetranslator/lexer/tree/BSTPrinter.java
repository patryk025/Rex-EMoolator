package pl.cba.genszu.amcodetranslator.lexer.tree;

public class BSTPrinter
{
	//adapted from https://www.baeldung.com/java-print-binary-tree-diagram
	
	public static void print(BinaryTree tree) {
		System.out.println(traversePreOrder(tree.root));
		try {
			if(((Branch) tree.root).condition != null) {
				System.out.println("CONDITION:");
				System.out.println(traversePreOrder(((Branch) tree.root).condition));
			}
			if(((Branch) tree.root).loopIncrement != null) {
				System.out.println("LOOP INCREMENT:");
				System.out.println(traversePreOrder(((Branch) tree.root).loopIncrement));
			}
		}
		catch(ClassCastException ignored) {}
		System.out.println();
	}
	
	private static String traversePreOrder(Node root) {

		if (root == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append(root);

		String pointerRight = "└──";
		String pointerLeft = (root.right != null) ? "├──" : "└──";

		String padding = new String();
		
		boolean hasRightSibling = true;

		if (root.instr != null) {
			int i = 0;
			for(BinaryTree tree : root.instr.instr) {
				sb.append("\n");
				if(i < root.instr.instr.size() - 1)
					sb.append("├──");
				else {
					sb.append("└──");
					hasRightSibling = false;
				}
				sb.append(tree.root);
				
				if (hasRightSibling) {
					padding = "│  ";
				} else {
					padding = "   ";
				}

				pointerLeft = (tree.root.right != null) ? "├──" : "└──";
				traverseNodes(sb, padding, pointerLeft, tree.root.left, tree.root.right != null);
				traverseNodes(sb, padding, pointerRight, tree.root.right, false);
				i++;
			}
		}
		else {
			traverseNodes(sb, "", pointerLeft, root.left, root.right != null);
			traverseNodes(sb, "", pointerRight, root.right, false);
		}
		
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

			if(node.value.valueAsFunc != null) {
				int i = 0;
				for(BinaryTree tree : node.value.valueAsFunc.instr) {
					sb.append("\n").append(paddingForBoth);
					if(i < node.value.valueAsFunc.instr.size() - 1)
						sb.append("├──");
					else {
						sb.append("└──");
						hasRightSibling = false;
					}
					sb.append(tree.root);
					
					pointerLeft = (tree.root.right != null) ? "├──" : "└──";
					
					if (hasRightSibling) {
						paddingBuilder.append("│  ");
					} else {
						paddingBuilder.append("   ");
					}
					
					paddingForBoth = paddingBuilder.toString();
					
					traverseNodes(sb, paddingForBoth, pointerLeft, tree.root.left, tree.root.right != null);
					traverseNodes(sb, paddingForBoth, pointerRight, tree.root.right, false);
					i++;
				}
			}
			else {
				traverseNodes(sb, paddingForBoth, pointerLeft, node.left, node.right != null);
				traverseNodes(sb, paddingForBoth, pointerRight, node.right, false);
			}
		}
	}
}
