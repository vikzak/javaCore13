import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Car implements Runnable {
    Lock lock = new ReentrantLock();
    static Car winner;
    private static int CARS_COUNT;
    private static final CountDownLatch cdl = new CountDownLatch(4);
    private Race race;
    private int speed;
    private String name;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            System.out.println(this.name + " готов");
            cdl.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MainClass.cdlStart.countDown();
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        try {
            lock.lock();
            if (isWinner(this)) System.out.println(getName() + " - WIN");
        } finally {
            lock.unlock();
        }
    }

    public static boolean isWinner(Car c) {
        if (winner == null){
            winner = c;
            return true;
        }
        return false;
    }

}