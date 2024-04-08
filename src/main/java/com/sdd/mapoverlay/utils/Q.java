package com.sdd.mapoverlay.utils;

import java.util.ArrayList;
import java.util.Optional;

/**
 * AVL storing the event points to be treated by the sweep line algorithm
 * It doesn't allow duplicate event points
 */
public class Q {

    public static Q getEmptyQueue() { return new Q(null, null); }


    /**
     * @return The next event point to be treated meaning the highest one below the sweep line
     */
    public EventPoint popNextEvent() {
        // Leaf node we return this node's event
        if (this.getLeftChild().isEmpty()) {
            EventPoint nextEvent = this.getData();
            // Replacing this node by its right child
            getRightChild().ifPresentOrElse(
                    this::become,
                    () -> {
                        if (this.isRoot()) {
                            this.setData(null);
                        } else {
                            this.getParent().setLeftChild(null);
                        }
                    }
            );
            return nextEvent;
        }
        return getLeftChildUnsafe().popNextEvent();
    }

    /**
     * If the event point already exists, copies its data (segments) into the existing one
     * but doesn't create a new node
     * @param newEventPoint Event point to be inserted
     */
    public void insert(EventPoint newEventPoint) {
//        assert newEventPoint.getSegments().size() == 1;
        // Structure is empty we insert the event on the root node
        if (this.isEmpty()) {
            this.setData(newEventPoint);
            return;
        }

        switch (newEventPoint.compare(this.getData())) {
            case LEFT -> {
                // if no child then create new one with newEventPoint otherwise inserts into child
                this.getLeftChild().ifPresentOrElse(
                        left -> {
                            left.insert(newEventPoint);
                            this.doEquilibrate();
                        },
                        () -> this.setLeftChild(new Q(newEventPoint, this))
                );
            }
            case RIGHT -> {
                // if no child then create new one with newEventPoint otherwise inserts into child
                this.getRightChild().ifPresentOrElse(
                        right -> {
                            right.insert(newEventPoint);
                            this.doEquilibrate();
                        },
                        () -> this.setRightChild(new Q(newEventPoint, this))
                );
            }
            case INTERSECT -> {
                // New eventPoint in this node, we add the segment of the new event to this node's eventPoint
                this.getData().addSegments(newEventPoint.getSegments());
            }
        }

    }


    //--------------------------------------------------------------------------------------------------------------
    //AVL TREE

    private EventPoint data;

    private int height = 1;

    private Q parent;

    private Q leftChild;

    private Q rightChild;

    /**
     * Construct a new node as the clone of the one provided as arguments
     * It doesn't change the parent relationship only the data, height and children
     * @param node Node to be copied
     */
    private Q(Q node) {
        if(node != null) {
            this.data = node.getData();
            this.height = node.getHeight();
            this.setLeftChild(node.getLeftChild().orElse(null));
            this.setRightChild(node.getRightChild().orElse(null));
        }
    }

    /**
     * @param data Data to be contained in this node
     * @param parent Node parent of this one
     */
    private Q(EventPoint data, Q parent) {
        this.data = data;
        this.parent = parent;
    }

    public Q getParent() {
        return parent;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public void setParent(Q parent) {
        this.parent = parent;
    }

    public EventPoint getData() { return data; }

    public Optional<Q> getLeftChild() { return Optional.ofNullable(leftChild); }

    public Q getLeftChildUnsafe() { return leftChild; }

    public Optional<Q> getRightChild() { return Optional.ofNullable(rightChild); }

    public Q getRightChildUnsafe() { return rightChild; }


    public void setLeftChild(Q leftChild) {
        this.leftChild = leftChild;
        if (leftChild != null) {
            this.leftChild.setParent(this);
        }
    }

    public void setRightChild(Q rightChild) {
        this.rightChild = rightChild;
        if (rightChild != null) {
            this.rightChild.setParent(this);
        }
    }


    private int getHeight() {
        return this.height;
    }

    /**
     * @return Balance of this node computed in O(1)
     */
    private int getBalance() {
        return getRightChild().map(Q::getHeight).orElse(0)
                - getLeftChild().map(Q::getHeight).orElse(0);
    }

    /**
     * Update the height by looking at the height of its children
     * Complexity: O(1)
     */
    private void updateHeight() {
        if (this.isLeaf()) {
            height = 1;
        } else {
            int leftHeight = getLeftChild().map(Q::getHeight).orElse(0);
            int rightHeight = getRightChild().map(Q::getHeight).orElse(0);

            height = leftHeight > rightHeight ? leftHeight + 1 : rightHeight + 1;
        }
    }

    public boolean isLeaf() {
        return leftChild == null && rightChild == null;
    }

    public boolean isEmpty() {
        return this.data == null;
    }

    public void setData(EventPoint data) {
        this.data = data;
    }

    /**
     * Copies the data, height and children of the provided node into this one
     * It doesn't update the parent
     * @param node Node to copy
     */
    protected void become(Q node) {
        this.data = node.getData();
        this.height = node.getHeight();
        this.setLeftChild(node.getLeftChild().orElse(null));
        this.setRightChild(node.getRightChild().orElse(null));
    }

    /**
     * Implements the algorithm seen in class
     */
    private void doLeftRotation() {
        Q rootCopy = new Q(this);
        this.become(getRightChildUnsafe());

        Q leftChildCopy = this.getLeftChild().isPresent() ? new Q(this.leftChild) : null;
        rootCopy.setRightChild(leftChildCopy);
        this.setLeftChild(rootCopy);

        this.leftChild.updateHeight();
        this.updateHeight();
    }

    /**
     * Implements the algorithm seen in class
     */
    private void doRightRotation() {
        Q rootCopy = new Q(this);
        this.become(getLeftChildUnsafe());

        Q rightChildCopy = getRightChild().isPresent() ? new Q(this.rightChild) : null;
        this.setRightChild(rootCopy);
        this.rightChild.setLeftChild(rightChildCopy);

        this.rightChild.updateHeight();
        this.updateHeight();
    }

    private void doDoubleLeftRotation() {
        getLeftChildUnsafe().doRightRotation();
        this.doLeftRotation();
    }

    private void doDoubleRightRotation() {
        getRightChildUnsafe().doLeftRotation();
        this.doRightRotation();
    }

    /**
     * Implements the algorithm seen in class
     */
    protected void doEquilibrate() {
        if (this.getBalance() == 2) {
            if(this.rightChild.getBalance() >= 0) {
                this.doLeftRotation();
            } else {
                this.doDoubleLeftRotation();
            }
        } else if (this.getBalance() == -2) {
            if (this.leftChild.getBalance() <= 0) {
                this.doRightRotation();
            } else {
                this.doDoubleRightRotation();
            }
        } else {
            this.updateHeight();
        }
    }

    public void inOrderTraversal(StringBuilder sb, int level, String side) {
        if (isEmpty()) {
            sb
                    .append(" ".repeat(level * 2))
                    .append("(").append(level).append(")")
                    .append(getData()).append("\n");
            return;
        }

        getRightChild().ifPresent(rightChild -> rightChild.inOrderTraversal(sb, level + 1, "r"));

        sb
                .append(" ".repeat(level * 2))
                .append("(").append(level).append(")")
                .append(getData()).append("\n");

        getLeftChild().ifPresent(leftChild -> leftChild.inOrderTraversal(sb, level + 1, "l"));
    }

    public void printTree() {
        StringBuilder sb = new StringBuilder();
        sb.append("[PRINT Q]").append("\n");
        this.inOrderTraversal(sb, 0, "R");
        System.out.println(sb);
    }
}
