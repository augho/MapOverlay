package com.sdd.mapoverlay.utils;

import java.util.ArrayList;

public class Q extends AVLTree<EventPoint> {

    private Q(EventPoint eventPoint, Q parent) {
        super(eventPoint, parent);
    }

    private void balanceTree() {}

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
        return ((Q) getLeftChild().orElseThrow()).popNextEvent();
    }

    public void insert(EventPoint newEventPoint) {
        assert newEventPoint.getSegments().size() == 1;
        // Structure is empty we insert the event on the root node
        if (this.isEmpty()) {
            this.setData(newEventPoint);
            return;
        }
        // New eventPoint in this node, we add the segment of the new event to this node's eventPoint
        if (this.getData().contains(newEventPoint.getSegments().get(0))) {
            this.getData().addSegment(newEventPoint.getSegments().get(0));
        } else if (newEventPoint.isLeftOf(this.getData())) {
            // if no child then create new one with newEventPoint otherwise inserts into child
            this.getLeftChild().ifPresentOrElse(
                    (left) -> ((Q) left).insert(newEventPoint),
                    () -> this.setLeftChild(new Q(newEventPoint, this))
            );
        } else if (newEventPoint.isRightOf(this.getData())) {
            // if no child then create new one with newEventPoint otherwise inserts into child
            this.getRightChild().ifPresentOrElse(
                    (right) -> ((Q) right).insert(newEventPoint),
                    () -> this.setRightChild(new Q(newEventPoint, this))
            );
        }
    }
}
