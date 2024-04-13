package com.example;

import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

class Consumer {

    record DataRecord(int value, long timeStamp) {

    }
    ;
    
    final ConcurrentLinkedDeque<DataRecord> data = new ConcurrentLinkedDeque<>();

    // once a second
    private static final int CLEAR_OLD_DELAY = 1000;

    {
        // start a thread to clear old values.
        // virtual thread is always a deamon thread
        Thread.startVirtualThread(() -> {
            try {
                while (true) {
                    Thread.sleep(Duration.ofMillis(CLEAR_OLD_DELAY));
                    final long now = now();
                    final long fiveMinFiveSecAgo = now - 305000;
//                    int iCount = 0;
                    for (Iterator<DataRecord> it = data.descendingIterator(); it.hasNext();) {
                        if (it.next().timeStamp < fiveMinFiveSecAgo) {
                            it.remove();
//                            iCount++;
                        } else {
                            // no need to delete data younger than 5 min 5 sec
//                            System.out.println(String.format("deleted: %d, last.value: %d, was %d sec ago", iCount, it.next().value, (now - it.next().timeStamp) / 1000));
                            break;
                        }
                    }
                }
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        });
        
        // clean deque at the end
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                data.clear();
            }
        });
    }

    /**
     * Called periodically to consume an integer.
     */
    public void accept(int number) {
        data.addFirst(new DataRecord(number, now()));
    }

    /**
     * Returns the mean (aka average) of numbers consumed in the last 5 minute
     * period.
     */
    public float mean() {
        final long fiveMinAgo = nowMinus5Min();
        // TODO: need to clear. sum maybr long
        int sum = 0;
        int count = 0;
        for (DataRecord r : data) {
            if (r.timeStamp > fiveMinAgo) {
                // ADD is 1 CPU cycle.
                // MUL is 3-6 CPU cycle.
                sum = sum + r.value;
                count++;
            } else {
                // don't need old data
                break;
            }
        }
        if (count > 0) {
            // TODO: need to clear. double maybe
            return (float) sum / count;
        } else {
            return 0;
        }
    }

    long now() {
        return System.currentTimeMillis();
    }

    long nowMinus5Min() {
        // 60 * 1000 * 5
        return System.currentTimeMillis() - 300000;
    }

}
