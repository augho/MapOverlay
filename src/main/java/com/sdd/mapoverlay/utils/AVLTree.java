package com.sdd.mapoverlay.utils;

import java.util.Optional;

/**
 * Implementation of an AVL tree as described in the slides and adds a parent attribute
 * that points to the parent of a node.
 * The insert and delete are not implemented as they differ between Q and T
 * @param <S> Type of the data to be stored in the node
 */
public class AVLTree<S> {

    private S data;

    private int height = 1;

    private AVLTree<S> parent;

    private AVLTree<S> leftChild;

    private AVLTree<S> rightChild;

    /**
     * Construct a new node as the clone of the one provided as arguments
     * It doesn't change the parent relationship only the data, height and children
     * @param node Node to be copied
     */
    private AVLTree(AVLTree<S> node) {
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
    protected AVLTree(S data, AVLTree<S> parent) {
        this.data = data;
        this.parent = parent;
    }

    public AVLTree<S> getParent() {
        return parent;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public void setParent(AVLTree<S> parent) {
        this.parent = parent;
    }

    public S getData() { return data; }

    public Optional<AVLTree<S>> getLeftChild() { return Optional.ofNullable(leftChild); }

    public AVLTree<S> getLeftChildUnsafe() { return leftChild; }

    public Optional<AVLTree<S>> getRightChild() { return Optional.ofNullable(rightChild); }

    public AVLTree<S> getRightChildUnsafe() { return rightChild; }


    public void setLeftChild(AVLTree<S> leftChild) {
        this.leftChild = leftChild;
        if (leftChild != null) {
            this.leftChild.setParent(this);
        }
    }

    public void setRightChild(AVLTree<S> rightChild) {
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
        return getRightChild().map(AVLTree::getHeight).orElse(0)
                - getLeftChild().map(AVLTree::getHeight).orElse(0);
    }

    /**
     * Update the height by looking at the height of its children
     * Complexity: O(1)
     */
    private void updateHeight() {
        if (this.isLeaf()) {
            height = 1;
        } else {
            int leftHeight = getLeftChild().map(AVLTree::getHeight).orElse(0);
            int rightHeight = getRightChild().map(AVLTree::getHeight).orElse(0);

            height = leftHeight > rightHeight ? leftHeight + 1 : rightHeight + 1;
        }
    }

    public boolean isLeaf() {
        return leftChild == null && rightChild == null;
    }

    public boolean isEmpty() {
        return this.data == null;
    }

    public void setData(S data) {
        this.data = data;
    }

    /**
     * Copies the data, height and children of the provided node into this one
     * It doesn't update the parent
     * @param node Node to copy
     */
    protected void become(AVLTree<S> node) {
        this.data = node.getData();
        this.height = node.getHeight();
        this.setLeftChild(node.getLeftChild().orElse(null));
        this.setRightChild(node.getRightChild().orElse(null));
    }

    /**
     * Implements the algorithm seen in class
     */
    private void doLeftRotation() {
        AVLTree<S> rootCopy = new AVLTree<>(this);
        this.become(getRightChild().orElseThrow());

        AVLTree<S> leftChildCopy = new AVLTree<>(this.leftChild);
        this.setLeftChild(rootCopy);
        this.leftChild.setRightChild(leftChildCopy);

        this.leftChild.updateHeight();
        this.updateHeight();
    }

    /**
     * Implements the algorithm seen in class
     */
    private void doRightRotation() {
        AVLTree<S> rootCopy = new AVLTree<>(this);
        this.become(getLeftChild().orElseThrow());

        AVLTree<S> rightChildCopy = new AVLTree<>(this.rightChild);
        this.setRightChild(rootCopy);
        this.rightChild.setLeftChild(rightChildCopy);

        this.rightChild.updateHeight();
        this.updateHeight();
    }

    private void doDoubleLeftRotation() {
        getLeftChild().orElseThrow().doRightRotation();
        this.doLeftRotation();
    }

    private void doDoubleRightRotation() {
        getRightChild().orElseThrow().doLeftRotation();
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

}
