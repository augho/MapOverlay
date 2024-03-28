package com.sdd.mapoverlay.utils;

import com.sdd.mapoverlay.utils.Records.DeleteResult;
import com.sdd.mapoverlay.utils.Records.SegmentPair;
import com.sdd.mapoverlay.utils.Records.ULCSets;

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

    @Override
    public T getLeftChildUnsafe() {
        return ((T) super.getLeftChildUnsafe());
    }

    @Override
    public T getRightChildUnsafe() {
        return ((T) super.getRightChildUnsafe());
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

        return new SegmentPair(left, right);
    }


    // TODO intersection reporting should be out of this class I think
    private void addIntersectionPoint(Point point) {
        intersectionCollection.add(point);
    }
}
