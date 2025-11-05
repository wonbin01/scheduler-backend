package wonbin.scheduler.Entity;

public class ScheduleEntry {
    private String name;
    private String date; // 예: 11/06(목)
    private String checkIn; // 예: 9:30
    private String checkOut; // 예: 15:30
    private String workTime; // 예: 6:00

    public ScheduleEntry(String name, String date, String checkIn, String checkOut, String workTime) {
        this.name = name;
        this.date = date;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.workTime = workTime;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public String getWorkTime() {
        return workTime;
    }

    @Override
    public String toString() {
        return "ScheduleEntry{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", checkIn='" + checkIn + '\'' +
                ", checkOut='" + checkOut + '\'' +
                ", workTime='" + workTime + '\'' +
                '}';
    }
}
