import java.time.Duration;

public class FakeClock implements Clock {
    private long nowMillis;

    FakeClock(long startTime) {
        nowMillis = startTime;
    }

    @Override
    public long now() {
        return nowMillis;
    }

    public void advance(Duration duration) {
        nowMillis += duration.toMillis();
    }
}
