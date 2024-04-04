package com.sdd.mapoverlay.utils;

import com.sdd.mapoverlay.utils.Records.DeleteResult;
import com.sdd.mapoverlay.utils.Records.SegmentPair;
import com.sdd.mapoverlay.utils.Records.ULCSets;

import java.util.ArrayList;
import java.util.Optional;

// TODO Edge case when segment intersect on the lower endpoint of one what happens
// TODO doEquilibrate may break recursion it would be a pain (update should be ok but t keep in mind if weird stuff happens)
public class T {


    public static T getEmpty() { return new T(null, null); }

    /**
     * @param data Segment to insert to the status
     *
     */
    public void insert(Segment data) {
        if (isEmpty()) {
            System.out.println("[INSERT IN T] " + data);
            this.setData(data);
            return;
        }
        // A lot of error thrown here, those are cases that shouldn't happen according to our
        // algorithm. And we obviously trust our algorithm
        Position dataPositionOnSweepLine = this.getData().whereIs(data.getUpperEndpoint());
        switch (dataPositionOnSweepLine) {
            case LEFT -> {
                if (!isLeaf()) {
                    this.getLeftChildUnsafe().insert(data);
                    return;
                }
                Segment currDataCopy = this.getData();
                this.setData(data);
                this.setLeftChild(new T(data, this));
                this.setRightChild(new T(currDataCopy, this));
                this.doEquilibrate();
                System.out.println("[INSERT IN T] " + data);

            }
            case RIGHT -> {
                if(!isLeaf()) {
                    this.getRightChildUnsafe().insert(data);
                    return;
                }
                this.setLeftChild(new T(this.getData(), this));
                this.setRightChild(new T(data, this));
                this.doEquilibrate();
                System.out.println("[INSERT IN T] " + data);

            }
            case INTERSECT -> {
                if(!isLeaf()) {
                    // TODO Correct to always go left ?
                    // Will be intersected next to the intersecting segment which can be found
                    // In its left subtree
                    this.getLeftChildUnsafe().insert(data);
                    return;
                }
                // The upper endpoint of this segment is an intersection point
                switch (this.getData().whereIs(data.getLowerEndpoint())) {
                    case LEFT -> {
                        Segment currDataCopy = this.getData();
                        this.setData(data);
                        this.setLeftChild(new T(data, this));
                        this.setRightChild(new T(currDataCopy, this));
                        this.doEquilibrate();
                        System.out.println("[INSERT IN T] " + data);

                    }
                    case RIGHT -> {
                        this.setLeftChild(new T(this.getData(), this));
                        this.setRightChild(new T(data, this));
                        this.doEquilibrate();
                        System.out.println("[INSERT IN T] " + data);

                    }
                    case INTERSECT -> throw new RuntimeException("Segments are parallel: " + getData() + " / " + data);
                }
            }
        }
    }

    public DeleteResult delete(Segment data) {
        if (isEmpty()) {
            return null;
        } else if (isLeaf() && !this.getData().sameAs(data)) {
            return new DeleteResult(null, this);
        }
        if (getLeftChildUnsafe().isLeaf() && getLeftChildUnsafe().getData().sameAs(data)) {
            this.become(getRightChildUnsafe());
            return new DeleteResult(getData(), this);

        } else if (getRightChildUnsafe().isLeaf() && getRightChildUnsafe().getData().sameAs(data)) {
            this.become(getLeftChildUnsafe());
            return new DeleteResult(getData(), this);
        }
        // inside node to be deleted
        if (getData().sameAs(data)) {
            DeleteResult result = getLeftChildUnsafe().delete(data);
            setData(result.newData());
            this.doEquilibrate();
            return new DeleteResult(result.newData(), this);
        }
        // For the search we look at the lower endpoint as it's the one on the sweep line
        switch (this.getData().whereIs(data.getLowerEndpoint())) {
            case LEFT -> {
                DeleteResult result = getLeftChildUnsafe().delete(data);
                this.doEquilibrate();
                return result;
            }
            case RIGHT -> {
                DeleteResult result = getRightChildUnsafe().delete(data);
                this.doEquilibrate();
                return result;
            }
            // TODO below
            case INTERSECT -> throw new RuntimeException("To implement");
        }
        throw new RuntimeException("Shouldn't be here");
    }

    public ULCSets findAllContaining(Point point) {
        ULCSets ulcSets = ULCSets.getEmpty();
        addAllContaining(point, ulcSets);
        return ulcSets;
    }

    private void addAllContaining(Point point, ULCSets sets) {
        switch (getData().whereIs(point)) {
            case LEFT -> {
                if (!isLeaf()) {
                    this.getLeftChildUnsafe().addAllContaining(point, sets);
                }
            }
            case RIGHT -> {
                if (!isLeaf()) {
                    this.getRightChildUnsafe().addAllContaining(point, sets);
                }
            }
            case INTERSECT -> {
                if (isLeaf()) {
                    this.getLeftChildUnsafe().addAllContaining(point, sets);
                    this.getRightChildUnsafe().addAllContaining(point, sets);
                } else {
                    Segment data = getData();
                    if (data.getLowerEndpoint().sameAs(point)) {
                        sets.L().add(data);
                    } else {
                        sets.C().add(data);
                    }
                }
            }
        }
    }

    public SegmentPair findLeftAndRightNeighbour(Point p) {
        return findLeftAndRightNeighbour(p, null, null);
    }
    private SegmentPair findLeftAndRightNeighbour(Point p, Segment left, Segment right) {
        // TODO not sure about this method
        switch (getData().whereIs(p)) {
            case LEFT -> {
                if (isLeaf()) {
                    return new SegmentPair(left, getData());
                }
                return getLeftChildUnsafe().findLeftAndRightNeighbour(p, left, getData());

            }
            case RIGHT -> {
                if (isLeaf()) {
                    return new SegmentPair(getData(), right);
                }
                return getRightChildUnsafe().findLeftAndRightNeighbour(p, getData(), right);
            }
            case INTERSECT -> throw new RuntimeException("Why here ?");
        }
        throw new RuntimeException("Why here ??");
    }

    public Optional<Segment> findLeftNeighbour(Segment segment, Double sweepLineY) {
        return Optional.ofNullable(
                findLeftNeighbour(new Point(segment.xAt(sweepLineY), sweepLineY), null)
        );
    }

    private Segment findLeftNeighbour(Point p, Segment leftNeighbour) {
        switch (getData().whereIs(p)) {
            case LEFT -> {
                if (isLeaf()) {
                    return leftNeighbour;
                }
                return getRightChildUnsafe().findLeftNeighbour(p, leftNeighbour);
            }
            case RIGHT -> {
                if (isLeaf()) {
                    return getData();
                }
                return getRightChildUnsafe().findLeftNeighbour(p, getData());
            }
            case INTERSECT -> {
                if (isLeaf()) {
                    return leftNeighbour;
                }
                return getLeftChildUnsafe().findLeftNeighbour(p, leftNeighbour);
            }
        }
        throw new RuntimeException("Why here ??");
    }

    public Optional<Segment> findRightNeighbour(Segment segment, Double sweepLineY) {
        return Optional.ofNullable(
                findRightNeighbour(new Point(segment.xAt(sweepLineY), sweepLineY), null)
        );
    }

    private Segment findRightNeighbour(Point p, Segment rightNeighbour) {
        switch (getData().whereIs(p)) {
            case LEFT -> {
                if (isLeaf()) {
                    return getData();
                }
                return getLeftChildUnsafe().findRightNeighbour(p, getData());
            }
            case RIGHT -> {
                if (isLeaf()) {
                    return rightNeighbour;
                }
                return getLeftChildUnsafe().findRightNeighbour(p, rightNeighbour);
            }
            case INTERSECT -> {
                if (isLeaf()) {
                    return rightNeighbour;
                }
                return getRightChildUnsafe().findRightNeighbour(p, rightNeighbour);
            }
        }
        throw new RuntimeException("Why here ??");
    }

    //--------------------------------------------------------------------------------------------------------------
    //AVL TREE

    private Segment data;

    private int height = 1;

    private T parent;

    private T leftChild;

    private T rightChild;

    /**
     * Construct a new node as the clone of the one provided as arguments
     * It doesn't change the parent relationship only the data, height and children
     * @param node Node to be copied
     */
    private T(T node) {
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
    private T(Segment data, T parent) {
        this.data = data;
        this.parent = parent;
    }

    public T getParent() {
        return parent;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    public Segment getData() { return data; }

    public Optional<T> getLeftChild() { return Optional.ofNullable(leftChild); }

    public T getLeftChildUnsafe() { return leftChild; }

    public Optional<T> getRightChild() { return Optional.ofNullable(rightChild); }

    public T getRightChildUnsafe() { return rightChild; }


    public void setLeftChild(T leftChild) {
        this.leftChild = leftChild;
        if (leftChild != null) {
            this.leftChild.setParent(this);
        }
    }

    public void setRightChild(T rightChild) {
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
        return getRightChild().map(T::getHeight).orElse(0)
                - getLeftChild().map(T::getHeight).orElse(0);
    }

    /**
     * Update the height by looking at the height of its children
     * Complexity: O(1)
     */
    private void updateHeight() {
        if (this.isLeaf()) {
            height = 1;
        } else {
            int leftHeight = getLeftChild().map(T::getHeight).orElse(0);
            int rightHeight = getRightChild().map(T::getHeight).orElse(0);

            height = leftHeight > rightHeight ? leftHeight + 1 : rightHeight + 1;
        }
    }

    public boolean isLeaf() {
        return leftChild == null && rightChild == null;
    }

    public boolean isEmpty() {
        return this.data == null;
    }

    public void setData(Segment data) {
        this.data = data;
    }

    /**
     * Copies the data, height and children of the provided node into this one
     * It doesn't update the parent
     * @param node Node to copy
     */
    protected void become(T node) {
        this.data = node.getData();
        this.height = node.getHeight();
        this.setLeftChild(node.getLeftChild().orElse(null));
        this.setRightChild(node.getRightChild().orElse(null));
    }

    /**
     * Implements the algorithm seen in class
     */
    private void doLeftRotation() {
        T rootCopy = new T(this);
        this.become(getRightChildUnsafe());

        T leftChildCopy = getLeftChild().isPresent() ? new T(this.leftChild) : null;
        this.setLeftChild(rootCopy);
        this.leftChild.setRightChild(leftChildCopy);

        this.leftChild.updateHeight();
        this.updateHeight();
    }

    /**
     * Implements the algorithm seen in class
     */
    private void doRightRotation() {
        T rootCopy = new T(this);
        this.become(getLeftChildUnsafe());

        T rightChildCopy = getRightChild().isPresent() ? new T(this.rightChild) : null;
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
    private void doEquilibrate() {
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

    private void inOrderTraversal(StringBuilder sb, int level, String side) {
        if (isEmpty()) {
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
        sb.append("[PRINT T]").append("\n");
        this.inOrderTraversal(sb, 0, "R");
        System.out.println(sb);
    }
}
