package com.sdd.mapoverlay.utils;

import com.sdd.mapoverlay.utils.Records.DeleteResult;
import com.sdd.mapoverlay.utils.Records.SegmentPair;
import com.sdd.mapoverlay.utils.Records.ULCSets;

import java.util.Optional;


public class T {


    public static T getEmpty() { return new T(null, null); }

    /**
     * @param data Segment to insert to the status
     *
     */
    public void insert(Segment data, Point eventPoint) {
        if (isEmpty()) {
//            System.out.println("[INSERT IN T] " + data);
            this.setData(data);
            return;
        }

        switch (getData().whereIs(eventPoint)) {
            case LEFT -> {
                if (!isLeaf()) {
                    this.getLeftChildUnsafe().insert(data, eventPoint);
                    doEquilibrate();
                    return;
                }
                Segment currDataCopy = this.getData();
                this.setData(data);
                this.setLeftChild(new T(data, this));
                this.setRightChild(new T(currDataCopy, this));
                this.doEquilibrate();
//                System.out.println("[INSERT IN T] " + data);

            }
            case RIGHT -> {
                if(!isLeaf()) {
                    this.getRightChildUnsafe().insert(data, eventPoint);
                    doEquilibrate();
                    return;
                }
                this.setLeftChild(new T(this.getData(), this));
                this.setRightChild(new T(data, this));
                this.doEquilibrate();
//                System.out.println("[INSERT IN T] " + data);

            }
            case INTERSECT -> {
                if(!isLeaf()) {
                    // Will be intersected next to the intersecting segment which can be found
                    // In its left subtree
                    this.getLeftChildUnsafe().insert(data, eventPoint);
                    doEquilibrate();
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
//                        System.out.println("[INSERT IN T] " + data);

                    }
                    case RIGHT -> {
                        this.setLeftChild(new T(getData(), this));
                        this.setRightChild(new T(data, this));
                        this.doEquilibrate();
//                        System.out.println("[INSERT IN T] " + data);

                    }
                    case INTERSECT -> throw new RuntimeException(eventPoint +" Segments are parallel: " + getData() + " / " + data);
                }
            }
        }
    }

    public DeleteResult delete(Segment data, Point eventPoint) {
        if (isEmpty()) {
            return null;
        } else if (isLeaf() && !this.getData().sameAs(data)) {
            return new DeleteResult(null, this);
        } else if (isRoot() && isLeaf() && this.getData().sameAs(data)) {
            this.setData(null);
            return new DeleteResult(null, this);
        }
        if (leftChild.isLeaf() && leftChild.getData().sameAs(data)) {
            this.become(getRightChildUnsafe());
            return new DeleteResult(getData(), this);

        } else if (rightChild.isLeaf() && rightChild.getData().sameAs(data)) {
            Segment oldData = getData();
            this.become(getLeftChildUnsafe());
            return new DeleteResult(oldData, this);
        }
        // inside node to be deleted
        if (getData().sameAs(data)) {
            DeleteResult result = getLeftChildUnsafe().delete(data, eventPoint);
            setData(result.newData());
            this.doEquilibrate();
            return new DeleteResult(result.newData(), this);
        }
        // event point corresponds to lower endpoint
        switch (this.getData().whereIs(eventPoint)) {
            case LEFT -> {
                DeleteResult result = getLeftChildUnsafe().delete(data, eventPoint);
                this.doEquilibrate();
                return result;
            }
            case RIGHT -> {
                DeleteResult result = getRightChildUnsafe().delete(data, eventPoint);
                this.doEquilibrate();
                return result;
            }
            case INTERSECT -> {
                switch (getData().whereIs(data.getUpperEndpoint())) {
                    case LEFT -> {
                        DeleteResult result = getLeftChildUnsafe().delete(data, eventPoint);
                        this.doEquilibrate();
                        return result;
                    }
                    case RIGHT -> {
                        DeleteResult result = getRightChildUnsafe().delete(data, eventPoint);
                        this.doEquilibrate();
                        return result;
                    }
                    case INTERSECT -> throw new RuntimeException("Segments are parallel: " + getData() + " / " + data);
                }
            }
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
                if (!isLeaf()) {
                    this.getLeftChildUnsafe().addAllContaining(point, sets);
                    this.getRightChildUnsafe().addAllContaining(point, sets);
                } else {
                    Segment data = getData();
                    if (data.getLowerEndpoint().sameAs(point)) {
                        sets.L().add(data);
                    } else if (data.getUpperEndpoint().sameAs(point)) {
                        System.out.println("Found upper endpoint what should we do sir ?");
                    } else {
                        sets.C().add(data);
                    }
                }
            }
        }
    }

    public SegmentPair findLeftAndRightNeighbour(Point p) {
        if (isEmpty()) {
            return new SegmentPair(null, null);
        }
        return findLeftAndRightNeighbour(p, null, null);
    }
    private SegmentPair findLeftAndRightNeighbour(Point p, Segment left, Segment right) {
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
            case INTERSECT -> {
                return new SegmentPair(null, null);
//                throw new RuntimeException("Why here ? p: " + p + " / s: " + getData());
            }
        }
        throw new RuntimeException("Why here ??");
    }

    public Optional<Segment> findLeftNeighbour(Segment segment, Double sweepLineY) {
        return Optional.ofNullable(
                findLeftNeighbour(segment, new Point(segment.xAt(sweepLineY), sweepLineY), null)
        );
    }

    private Segment findLeftNeighbour(Segment s, Point sOnLine, Segment leftNeighbour) {
        switch (getData().whereIs(sOnLine)) {
            case LEFT -> {
                if (isLeaf()) {
                    return leftNeighbour;
                }
                return getLeftChildUnsafe().findLeftNeighbour(s, sOnLine, leftNeighbour);
            }
            case RIGHT -> {
                if (isLeaf()) {
                    return getData();
                }
                return getRightChildUnsafe().findLeftNeighbour(s, sOnLine, getData());
            }
            case INTERSECT -> {
                switch (s.whereIs(getData().getLowerEndpoint())) {
                    case RIGHT, INTERSECT -> {
                        if (isLeaf()) {
                            return leftNeighbour;
                        }
                        return getLeftChildUnsafe().findLeftNeighbour(s, sOnLine, leftNeighbour);
                    }
                    case LEFT -> {
                        if (isLeaf()) {
                            return getData();
                        }
                        return getRightChildUnsafe().findLeftNeighbour(s, sOnLine, getData());
                    }
                }
            }

        }
        throw new RuntimeException("Why here ??");
    }

    public Optional<Segment> findRightNeighbour(Segment segment, Double sweepLineY) {
        return Optional.ofNullable(
                findRightNeighbour(segment, new Point(segment.xAt(sweepLineY), sweepLineY), null)
        );
    }

    private Segment findRightNeighbour(Segment s, Point sOnLine, Segment rightNeighbour) {
        switch (getData().whereIs(sOnLine)) {
            case LEFT -> {
                if (isLeaf()) {
                    return getData();
                }
                return getLeftChildUnsafe().findRightNeighbour(s, sOnLine, getData());
            }
            case RIGHT -> {
                if (isLeaf()) {
                    return rightNeighbour;
                }
                return getRightChildUnsafe().findRightNeighbour(s, sOnLine, rightNeighbour);
            }
            case INTERSECT -> {
                switch (s.whereIs(getData().getLowerEndpoint())) {
                    case RIGHT -> {
                        if (isLeaf()) {
                            return getData();
                        }
                        return getLeftChildUnsafe().findRightNeighbour(s, sOnLine, getData());
                    }
                    case LEFT, INTERSECT -> {
                        if (isLeaf()) {
                            return rightNeighbour;
                        }
                        return getRightChildUnsafe().findRightNeighbour(s, sOnLine, rightNeighbour);
                    }
                }
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
        getRightChildUnsafe().doRightRotation();
        doLeftRotation();
    }

    private void doDoubleRightRotation() {
        getLeftChildUnsafe().doLeftRotation();
        doRightRotation();
    }

    /**
     * Implements the algorithm seen in class
     */
    private void doEquilibrate() {
        if (getBalance() == 2) {
            if (rightChild.getBalance() >= 0) {
                doLeftRotation();
            } else {
                doDoubleLeftRotation();
            }
        } else {
            if (getBalance() == -2) {
                if (leftChild.getBalance() <= 0) {
                    doRightRotation();
                } else {
                    doDoubleRightRotation();
                }
            } else {
                updateHeight();
            }
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

    public boolean scanForNull() {
        if (isLeaf() && isRoot()) return false;
        if(isLeaf()) return isEmpty();
        else return rightChild.scanForNull() || leftChild.scanForNull();
    }

    public String getStatus(boolean readable) {
        if(isEmpty()) return "null";
        if (isLeaf()) return readable ? getData().readableToString() : getData().toString();
        return getLeftChild().map(t -> t.getStatus(readable)).orElse("") + " | "
                + getRightChild().map(t -> t.getStatus(readable)).orElse("");
    }
    public String getRootAndLeafMirror(boolean readable) {
        if (isEmpty()) return "null";
        if (isLeaf()) return readable ? getData().readableToString() : getData().toString();
        if (isRoot()) return readable ?
                getData().readableToString() + " / " +
                        getLeftChild().map(t -> t.getRootAndLeafMirror(true)).orElse("") :
                getData().toString() + " / " +
                        getLeftChild().map(t -> t.getRootAndLeafMirror(false)).orElse("");
        return getRightChild().map(t -> t.getRootAndLeafMirror(readable)).orElse("");
    }

    public boolean valid() {
        if (isEmpty() || isLeaf()) return true;
        if (leftChild.getRightmost().sameAs(getData())) {
            return getLeftChildUnsafe().valid() && getRightChildUnsafe().valid();
        } else {
            System.out.println("[ERR INTEGRITY]" + getData().readableToString());
            return false;
        }
    }

    private Segment getRightmost() {
        if (isLeaf()) return getData();
        return rightChild.getRightmost();
    }
}
