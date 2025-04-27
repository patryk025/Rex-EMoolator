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
        if (!(currentNode instanceof DecisionTree.ConditionNode))
            throw new IllegalStateException("then() must follow when() or andWhen()");

        DecisionTree.ActionNode<T> actionNode = new DecisionTree.ActionNode<>(action);
        ((DecisionTree.ConditionNode<T>) currentNode).setTrueNode(actionNode);
        return this;
    }

    public DecisionTreeBuilder<T> andWhen(Predicate<Object[]> condition) {
        if (isFirstCondition)
            throw new IllegalStateException("Use when() for the first condition");
        if (!(currentNode instanceof DecisionTree.ConditionNode))
            throw new IllegalStateException("andWhen() must follow then()");

        DecisionTree.ConditionNode<T> newFalse =
                new DecisionTree.ConditionNode<>(condition, null, null);

        ((DecisionTree.ConditionNode<T>) currentNode).setFalseNode(newFalse);
        currentNode = newFalse;
        return this;
    }

    public DecisionTreeBuilder<T> otherwise(Function<Object[], T> action) {
        if (!(currentNode instanceof DecisionTree.ConditionNode))
            throw new IllegalStateException("otherwise() must follow then() or andWhen()");

        DecisionTree.ActionNode<T> actionNode = new DecisionTree.ActionNode<>(action);
        ((DecisionTree.ConditionNode<T>) currentNode).setFalseNode(actionNode);
        currentNode = actionNode;
        return this;
    }

    public DecisionTree<T> build() {
        if (isFirstCondition || currentNode instanceof DecisionTree.ConditionNode) {
            throw new IllegalStateException("Decision tree is incomplete; ensure all conditions have actions and an otherwise clause");
        }
        return new DecisionTree<>(root);
    }
}