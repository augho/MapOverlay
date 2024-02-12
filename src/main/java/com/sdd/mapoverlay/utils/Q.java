package com.sdd.mapoverlay.utils;

import java.util.ArrayList;

public class Q {
    private EventPoint eventPoint;
    private Q leftChild, rightChild;

    private Q(EventPoint eventPoint) {
        this.eventPoint = eventPoint;
    }


    private boolean isLeaf() {
        return this.leftChild == null && this.rightChild == null;
    }

    private boolean isEmpty() {
        return this.eventPoint == null;
    }

    private boolean hasLeftChild() {
        return this.leftChild != null;
    }
    private boolean hasRightChild() {
        return this.rightChild != null;
    }

    private void balanceTree() {}

    public EventPoint popNextEvent() {
        // Leaf node we return this node's event
        if (!this.hasLeftChild()) {
            EventPoint nextEvent = this.eventPoint;
            // Replacing this node by its right child
            if (!this.hasRightChild()) {
                this.eventPoint = null;
            } else {
                this.eventPoint = this.rightChild.eventPoint;
                this.leftChild = this.rightChild.leftChild;
                this.rightChild = this.rightChild.rightChild;
            }
            return nextEvent;
        }
        return this.leftChild.popNextEvent();
    }

    public void insert(EventPoint newEventPoint) {
        assert newEventPoint.getSegments().size() == 1;
        // Structure is empty we insert the event on the root node
        if (this.isEmpty()) {
            this.eventPoint = newEventPoint;
            return;
        }
        // New eventPoint in this node, we add the segment of the new event to this node's eventPoint
        if (this.eventPoint.contains(newEventPoint.getSegments().get(0))) {
            this.eventPoint.addSegment(newEventPoint.getSegments().get(0));
        } else if (newEventPoint.isLeftOf(this.eventPoint)) {
            // if no child then create new one with newEventPoint otherwise inserts into child
            if (this.hasLeftChild()) {
                this.leftChild.insert(newEventPoint);
            } else {
                this.leftChild = new Q(newEventPoint);
            }
        } else if (newEventPoint.isRightOf(this.eventPoint)) {
            // if no child then create new one with newEventPoint otherwise inserts into child
            if(this.hasRightChild()) {
                this.rightChild.insert(newEventPoint);
            } else {
                this.rightChild = new Q(newEventPoint);
            }
        }
    }
}
