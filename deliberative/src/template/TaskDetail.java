package ch.epfl.ia.deliberative;

/**
 * Additionnal information about a task in the TaskSet
 **/
public class TaskDetail {

    public final Task t;
    public final TaskStatus ts;

    enum TaskStatus {
        TO_DELIVER, TO_PICKUP
    }

    public TaskDetail(Task t, boolean delivery) {
        this.t = t;
        this.ts = (delivery) ? TO_DELIVER : TO_PICKUP;
    }

    public boolean isToDeliver() {
        return ts.equals(TO_DELIVER);
    }

    public boolean isToPickup() {
        return ts.equals(TO_PICKUP);
    }
}
