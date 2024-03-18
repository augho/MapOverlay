package com.sdd.mapoverlay.utils;

import java.util.ArrayList;

/**
 * AVL storing the event points to be treated by the sweep line algorithm
 * It doesn't allow duplicate event points
 */
public class Q extends AVLTree<EventPoint> {

    public static Q getEmptyQueue() { return new Q(null, null); }

    private Q(EventPoint eventPoint, Q parent) {
        super(eventPoint, parent);
    }

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
        return ((Q) getLeftChild().orElseThrow()).popNextEvent();
    }

    /**
     * If the event point already exists, copies its data (segments) into the existing one
     * but doesn't create a new node
     * @param newEventPoint Event point to be inserted
     */
    public void insert(EventPoint newEventPoint) {
        assert newEventPoint.getSegments().size() == 1;
        // Structure is empty we insert the event on the root node
        if (this.isEmpty()) {
            this.setData(newEventPoint);
            return;
        }

        switch (newEventPoint.compare(this.getData())) {
            case LEFT -> {
                // if no child then create new one with newEventPoint otherwise inserts into child
                this.getLeftChild().ifPresentOrElse(
                        (left) -> {
                            ((Q) left).insert(newEventPoint);
                            this.doEquilibrate();
                        },
                        () -> this.setLeftChild(new Q(newEventPoint, this))
                );
            }
            case RIGHT -> {
                // if no child then create new one with newEventPoint otherwise inserts into child
                this.getRightChild().ifPresentOrElse(
                        (right) -> {
                            ((Q) right).insert(newEventPoint);
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
}
