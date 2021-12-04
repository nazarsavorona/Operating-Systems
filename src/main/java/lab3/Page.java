package lab3;

public class Page {
    public int id;
    public int physical;
    public byte R;
    public byte M;
    public int inMemTime;
    public int lastTouchTime;
    public long high;
    public long low;

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", physical=" + physical +
                ", R=" + R +
                ", M=" + M +
                ", inMemTime=" + inMemTime +
                ", lastTouchTime=" + lastTouchTime +
                ", high=" + high +
                ", low=" + low +
                '}';
    }

    public Page(int id, int physical, byte R, byte M, int inMemTime, int lastTouchTime, long high, long low) {
        this.id = id;
        this.physical = physical;
        this.R = R;
        this.M = M;
        this.inMemTime = inMemTime;
        this.lastTouchTime = lastTouchTime;
        this.high = high;
        this.low = low;
    }

    public void reset() {
        inMemTime = 0;
        lastTouchTime = 0;
        R = 0;
        M = 0;
        physical = -1;
    }

}
