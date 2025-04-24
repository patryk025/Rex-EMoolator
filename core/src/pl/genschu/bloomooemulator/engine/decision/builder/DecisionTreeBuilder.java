package pl.genschu.bloomooemulator.engine.decision.builder;

import pl.genschu.bloomooemulator.engine.decision.DecisionTree;

import java.util.function.Function;
import java.util.function.Predicate;

public class DecisionTreeBuilder<T> {
    private DecisionTree.DecisionNode<T> root;
    private DecisionTree.DecisionNode<T> currentNode;
    private boolean isFirstCondition;

    private DecisionTreeBuilder() {
        this.isFirstCondition = true;
    }

    public static <T> DecisionTreeBuilder<T> create() {
        return new DecisionTreeBuilder<>();
    }

    public DecisionTreeBuilder<T> when(Predicate<Object[]> condition) {
        if (isFirstCondition) {
            root = new DecisionTree.ConditionNode<>(condition, null, null);
            currentNode = root;
            isFirstCondition = false;
        } else {
            throw new IllegalStateException("Use andWhen() for subsequent conditions after the first when()");
        }
        return this;
    }

    public DecisionTreeBuilder<T> then(Function<Object[], T> action) {
        if (!(currentNode instanceof DecisionTree.ConditionNode)) {
            throw new IllegalStateException("then() must follow when() or andWhen()");
        }
        DecisionTree.ConditionNode<T> conditionNode = (DecisionTree.ConditionNode<T>) currentNode;
        conditionNode = new DecisionTree.ConditionNode<>(
                conditionNode.getCondition(),
                new DecisionTree.ActionNode<>(action),
                conditionNode.getFalseNode()
        );
        if (currentNode == root) {
            root = conditionNode;
        }
        currentNode = conditionNode;
        return this;
    }

    public DecisionTreeBuilder<T> andWhen(Predicate<Object[]> condition) {
        if (isFirstCondition) {
            throw new IllegalStateException("Use when() for the first condition");
        }
        if (!(currentNode instanceof DecisionTree.ConditionNode)) {
            throw new IllegalStateException("andWhen() must follow then()");
        }
        DecisionTree.ConditionNode<T> conditionNode = (DecisionTree.ConditionNode<T>) currentNode;
        DecisionTree.DecisionNode<T> newFalseNode = new DecisionTree.ConditionNode<>(condition, null, null);
        conditionNode = new DecisionTree.ConditionNode<>(
                conditionNode.getCondition(),
                conditionNode.getTrueNode(),
                newFalseNode
        );
        if (currentNode == root) {
            root = conditionNode;
        }
        currentNode = newFalseNode;
        return this;
    }

    public DecisionTreeBuilder<T> otherwise(Function<Object[], T> action) {
        if (!(currentNode instanceof DecisionTree.ConditionNode)) {
            throw new IllegalStateException("otherwise() must follow then() or andWhen()");
        }
        DecisionTree.ConditionNode<T> conditionNode = (DecisionTree.ConditionNode<T>) currentNode;
        conditionNode = new DecisionTree.ConditionNode<>(
                conditionNode.getCondition(),
                conditionNode.getTrueNode(),
                new DecisionTree.ActionNode<>(action)
        );
        if (currentNode == root) {
            root = conditionNode;
        }
        currentNode = conditionNode;
        return this;
    }

    public DecisionTree<T> build() {
        if (isFirstCondition || currentNode instanceof DecisionTree.ConditionNode) {
            throw new IllegalStateException("Decision tree is incomplete; ensure all conditions have actions and an otherwise clause");
        }
        return new DecisionTree<>(root);
    }
}