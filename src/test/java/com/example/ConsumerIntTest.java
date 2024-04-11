package com.example;

import com.example.Consumer.DataRecord;
import java.time.Duration;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class ConsumerIntTest {

    final Random rn = new Random();
    private final Consumer consumer = new Consumer();
    
    // TODO: maybe it should runs depending on profile
    @Test
    void consumerTest() throws InterruptedException {

        final long now = consumer.now();
        final long fiveMinAgo = now - 300000;

        // fill data
        for (long l = fiveMinAgo; l < now; l = l + 10) {
            consumer.data.addFirst(new DataRecord(rn.nextInt(100), l));
        }

        // get averege every 100 millis
        Thread.startVirtualThread(() -> {
            try {
                while (true) {
                    Thread.sleep(Duration.ofMillis(100));
                    float mean = consumer.mean();
//                    System.out.println(String.format("size: %d, mean: %f", consumer.data.size(), mean));
                }
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        });

        // add a value in 10 millis
        // break the test in 5 min
        final long fiveMinPass = now + 300000;
        while (true) {
            Thread.sleep(Duration.ofMillis(10));
            consumer.accept(rn.nextInt(100));

            if (consumer.now() > fiveMinPass) {
                break;
            }

        }
        
        // TODO: add an assertion that there wasn't any exception
    }
    
    // for profiler.
    public static void main(String[] args) throws InterruptedException {
        new ConsumerIntTest().consumerTest();
    }

}
