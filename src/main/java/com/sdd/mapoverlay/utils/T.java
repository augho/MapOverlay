package com.sdd.mapoverlay.utils;

import java.util.Optional;

public class T extends AVLTree<Segment> {

    protected T(Segment data, AVLTree<Segment> parent) {
        super(data, parent);
    }

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
                    // TODO What happens ?? Treat right away ? Probably not but where to insert ?
                    throw new RuntimeException("Unimplemented: They intersect on the sweep line");
                }
            }
        } else {
            switch (dataPositionOnSweepLine) {
                case LEFT -> ((T) this.getLeftChild().orElseThrow()).insert(data);
                case RIGHT -> ((T) this.getRightChild().orElseThrow()).insert(data);
                case INTERSECT -> {
                    // TODO left or right ??? idk probably left but need to be sure
                    ((T) this.getLeftChild().orElseThrow()).insert(data);
                }
            }
        }
    }
}
