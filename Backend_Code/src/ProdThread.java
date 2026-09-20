package src;

import java.util.Random;

public class ProdThread extends Thread {

    private SRGBuffer buffer;
    private boolean running = true;

    public ProdThread(SRGBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (running) {
            try {
                int randomNum = random.nextInt(Integer.MAX_VALUE);
                buffer.enqueue(randomNum);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    public void stopProducer() {
        running = false;
        this.interrupt();
    }
}