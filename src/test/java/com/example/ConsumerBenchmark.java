package com.example;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ConsumerBenchmark {
    
    final Random rn = new Random();
    private final Consumer consumer = new Consumer();
    
    @Param({ "100", "1000", "10000" })
    public int iterations;    
    
   @Setup(Level.Invocation)
    public void setUp() {
        final long now = consumer.now();
        final long fiveMinAgo = now - 300000;

        // fill data
        for (long l = fiveMinAgo; l < now; l = l + 10) {
            consumer.data.addFirst(new Consumer.DataRecord(rn.nextInt(100), l));
        }
    }    
    
    
    @Benchmark
//    @BenchmarkMode(Mode.SampleTime)
//    @BenchmarkMode(Mode.AverageTime)
//    @BenchmarkMode(Mode.SingleShotTime)
    @BenchmarkMode(Mode.Throughput)
    //@Fork(value = 1, warmups = 1)
    @Fork(value = 1)
    @Warmup(iterations = 1)
    public void consumerBTest(Blackhole blackhole) {
        
        for (int i = 1; i <= iterations; i++) {
            consumer.accept(i);
        }

        blackhole.consume(consumer.mean());
        consumer.data.clear();
    }

}
