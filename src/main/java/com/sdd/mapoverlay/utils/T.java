package com.sdd.mapoverlay.utils;

import java.util.ArrayList;
import java.util.Optional;
// TODO Edge case when segment intersect on the lower endpoint of one what happens
// TODO doEquilibrate may break recursion it would be a pain (update should be ok but t keep in mind if weird stuff happens)
public class T extends AVLTree<Segment> {

    private final ArrayList<Point> intersectionCollection;

    public static T getEmpty(ArrayList<Point> intersectionCollection) { return new T(intersectionCollection); }

    private T(ArrayList<Point> intersectionCollection) {
        super(null, null);
        this.intersectionCollection = intersectionCollection;
    }
    private T(Segment data, T parent) {
        super(data, parent);
        this.intersectionCollection = parent.getIntersectionCollection();
    }

    public ArrayList<Point> getIntersectionCollection() {
        return intersectionCollection;
    }

    /**
     * @param data Segment to insert to the status
     *
     */
    public void insert(Segment data) {
        // A lot of error thrown here, those are cases that shouldn't happen according to our
        // algorithm. And we obviously trust our algorithm
        Position dataPositionOnSweepLine = this.getData().whereIs(data.getUpperEndpoint());
        if(this.isLeaf()) {
            switch (dataPositionOnSweepLine) {
                case LEFT -> {
                    Segment currDataCopy = this.getData();
                    this.setData(data);
                    this.setLeftChild(new T(data, this));
                    this.setRightChild(new T(currDataCopy, this));
                    this.doEquilibrate();
                }
                case RIGHT -> {
                    this.setLeftChild(new T(this.getData(), this));
                    this.setRightChild(new T(data, this));
                    this.doEquilibrate();
                }
                case INTERSECT -> {
                    // The upper endpoint of this segment is an intersection point
                    this.addIntersectionPoint(data.getUpperEndpoint());
                    switch (this.getData().whereIs(data.getLowerEndpoint())) {
                        case LEFT -> {
                            Segment currDataCopy = this.getData();
                            this.setData(data);
                            this.setLeftChild(new T(data, this));
                            this.setRightChild(new T(currDataCopy, this));
                            this.doEquilibrate();
                        }
                        case RIGHT -> {
                            this.setLeftChild(new T(this.getData(), this));
                            this.setRightChild(new T(data, this));
                            this.doEquilibrate();
                        }
                        case INTERSECT -> throw new RuntimeException("Segments are parallel");
                    }
                }
            }
        } else {
            switch (dataPositionOnSweepLine) {
                case LEFT -> ((T) this.getLeftChild().orElseThrow()).insert(data);
                case RIGHT -> ((T) this.getRightChild().orElseThrow()).insert(data);
                case INTERSECT -> {
                    // Will be intersected next to the intersecting segment which can be found
                    // In its left subtree
                    ((T) this.getLeftChild().orElseThrow()).insert(data);
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
            DeleteResult result = ((T) getLeftChildUnsafe()).delete(data);
            setData(result.newData());
            this.doEquilibrate();
            return new DeleteResult(result.newData(), this);
        }
        // For the search we look at the lower endpoint as it's the one on the sweep line
        switch (this.getData().whereIs(data.getLowerEndpoint())) {
            case LEFT -> {
                DeleteResult result = ((T) getLeftChildUnsafe()).delete(data);
                this.doEquilibrate();
                return result;
            }
            case RIGHT -> {
                DeleteResult result = ((T) getRightChildUnsafe()).delete(data);
                this.doEquilibrate();
                return result;
            }
            // TODO below
            case INTERSECT -> throw new RuntimeException("To implement");
        }
        throw new RuntimeException("Shouldn't be here");
    }

    private void addIntersectionPoint(Point point) {
        intersectionCollection.add(point);
    }
}
