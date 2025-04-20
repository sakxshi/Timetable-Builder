package model;

public class Classroom {
    private int id;
    private int capacity;
    private boolean avSupport;
    private int computers;
    private String roomType;
    
    public Classroom(int id, int capacity, boolean avSupport, int computers, String roomType) {
        this.id = id;
        this.capacity = capacity;
        this.avSupport = avSupport;
        this.computers = computers;
        this.roomType = roomType;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public boolean hasAV() { return avSupport; }
    public void setAvSupport(boolean avSupport) { this.avSupport = avSupport; }
    public int getComputers() { return computers; }
    public void setComputers(int computers) { this.computers = computers; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    
    @Override
    public String toString() { return "Room " + id; }
}