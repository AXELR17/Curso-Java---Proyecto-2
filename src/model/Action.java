package model;

//Representa una accion realizada por el sistema


public class Action {
    private final String description;
    private final long timestamp;

    public Action(String description) {
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + description;
    }
}

