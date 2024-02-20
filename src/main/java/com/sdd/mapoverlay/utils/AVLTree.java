package com.sdd.mapoverlay.utils;

import java.util.Optional;

public class AVLTree<S> {

    private S data;

    private int height = 1;

    private AVLTree<S> parent;

    private AVLTree<S> leftChild;

    private AVLTree<S> rightChild;

    private AVLTree(AVLTree<S> node) {
        if(node != null) {
            this.data = node.getData();
            this.height = node.getHeight();
            this.setLeftChild(node.getLeftChild().orElse(null));
            this.setRightChild(node.getRightChild().orElse(null));
        }
    }

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

    public Optional<AVLTree<S>> getRightChild() { return Optional.ofNullable(rightChild); }

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

    private int getBalance() {
        return getRightChild().map(AVLTree::getHeight).orElse(0)
                - getLeftChild().map(AVLTree::getHeight).orElse(0);
    }

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

    public boolean hasLeftChild() {
        return this.leftChild != null;
    }

    public boolean hasRightChild() {
        return this.rightChild != null;
    }

    private int getDepth() {
        if (this.isLeaf()) {
            return 0;
        }
        int leftDepth = getLeftChild().map(AVLTree::getDepth).orElse(0);
        int rightDepth = getLeftChild().map(AVLTree::getDepth).orElse(0);

        return leftDepth > rightDepth ? leftDepth + 1 : rightDepth + 1;
    }

    public void setData(S data) {
        this.data = data;
    }

    protected void become(AVLTree<S> node) {
        this.data = node.getData();
        this.height = node.getHeight();
        this.setLeftChild(node.getLeftChild().orElse(null));
        this.setRightChild(node.getRightChild().orElse(null));
    }
    private void doLeftRotation() {
        AVLTree<S> rootCopy = new AVLTree<>(this);
        this.become(getRightChild().orElseThrow());

        AVLTree<S> leftChildCopy = new AVLTree<>(this.leftChild);
        this.setLeftChild(rootCopy);
        this.leftChild.setRightChild(leftChildCopy);

        this.leftChild.updateHeight();
        this.updateHeight();
    }

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
