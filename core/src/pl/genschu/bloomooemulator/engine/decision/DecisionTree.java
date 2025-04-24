package pl.genschu.bloomooemulator.engine.decision;

import java.util.function.Function;
import java.util.function.Predicate;

public class DecisionTree<T> {
    private final DecisionNode<T> root;

    public DecisionTree(DecisionNode<T> root) {
        this.root = root;
    }

    public T evaluate(Object... context) {
        return root.evaluate(context);
    }

    public interface DecisionNode<T> {
        T evaluate(Object... context);
    }

    public static class ConditionNode<T> implements DecisionNode<T> {
        private final Predicate<Object[]> condition;
        private final DecisionNode<T> trueNode;
        private final DecisionNode<T> falseNode;

        public ConditionNode(Predicate<Object[]> condition, DecisionNode<T> trueNode, DecisionNode<T> falseNode) {
            this.condition = condition;
            this.trueNode = trueNode;
            this.falseNode = falseNode;
        }

        @Override
        public T evaluate(Object... context) {
            if (condition.test(context)) {
                return trueNode.evaluate(context);
            } else {
                return falseNode.evaluate(context);
            }
        }

        public Predicate<Object[]> getCondition() {
            return condition;
        }

        public DecisionNode<T> getTrueNode() {
            return trueNode;
        }

        public DecisionNode<T> getFalseNode() {
            return falseNode;
        }
    }

    public static class ActionNode<T> implements DecisionNode<T> {
        private final Function<Object[], T> action;

        public ActionNode(Function<Object[], T> action) {
            this.action = action;
        }

        @Override
        public T evaluate(Object... context) {
            return action.apply(context);
        }
    }
}
