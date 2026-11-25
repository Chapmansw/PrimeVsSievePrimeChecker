import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Experiment1b {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"));

        PrintWriter resultsOut = new PrintWriter("results_" + timestamp + ".csv");
         int[] arrLength={10000,100000,1000000};
        long[] largeN={10000,100000,20000000};
        double[] percentPrime={.1,.0,.9};
        int experiment=0;
        for(int x=0;x<3;x++){
            for(int y=0;y<3;y++){
                for(int z=0;z<3;z++){
                    experiment++;
                    long[] results = runExperiment(arrLength[x],largeN[y],(int)Math.floor(((double)arrLength[x])*percentPrime[z]));
                    resultsOut.println("Experiment:"+experiment+", Array Size:"+arrLength[x]+ ", Largest Number Possible: "+largeN[y]+", Number of primes:"+(int)Math.floor(((double)arrLength[x])*percentPrime[z])+", \n Sieve: Total Running Time in s:" + results[0] + ", Time Per Query in ms:" + results[1] + ", Memory Used:" + results[2]+"mb \n Basic: Total Running Time in s:" + results[3] + ", Time Per Query in ms:" + results[4] + ", Memory Used:" + results[5]+"mb\n");
                    resultsOut.flush();
                    
                }
            }
        }
        resultsOut.close();
    }

    /**
     * @return array of three things: total running time (seconds), running time per query (seconds), and peak memory usage (MB)
     */
    public static long[] runExperiment(int arrLength, long largestN, int numPrimes) throws InterruptedException {
        // Setup for measuring running time and memory usage
        long startTime = System.nanoTime();

        MemoryMonitor memoryMonitor = new MemoryMonitor(10);
        Thread monitorThread = new Thread(memoryMonitor, "memory-monitor");
        monitorThread.setDaemon(true);
        monitorThread.start();

        int numQueries; // TODO: set this in the block below, according to your use case
        {
            // TODO: put your experiment code here
            // You'll want to add parameters to runExperiment to specify a particular use case,
            // unlike this hard-coded (way-too-small) use case I have here as a simple example

            SieveBasedPrimeChecker checker = new SieveBasedPrimeChecker(50_000_000);
            long[] values = {8, 4877, 3_767_544, 37_675_477, 101, 475651};
            numQueries = values.length;

            for (long val : values) {
                checker.isPrime(val);
            }
        }

        memoryMonitor.stop();
        monitorThread.join();

        long elapsedMillis = (System.nanoTime() - startTime) / 1_000_000;
        long maxMemory = (memoryMonitor.getMaxMemoryUsed() / 1024 / 1024);
        System.out.println("Max memory usage: " + maxMemory + " MB");
        System.out.println("Total elapsed time: " + elapsedMillis + " ms");
        System.out.println("Average time per query: " + (elapsedMillis / numQueries) + " ms");
        System.out.println("\n\n\n\n");

        long[] res = new long[3];
        res[0] = elapsedMillis / 1000;
        res[1] = res[0] / numQueries;
        res[2] = maxMemory;

        return res;
    }

    /**
     * Periodically samples the JVM's used heap memory and keeps track of the
     * maximum observed value, until stop() is called.
     */
    private static class MemoryMonitor implements Runnable {
        private final long intervalMillis;
        private volatile boolean running = true;
        private volatile long maxMemoryUsed = 0;

        MemoryMonitor(long intervalMillis) {
            this.intervalMillis = intervalMillis;
        }

        void stop() {
            running = false;
        }

        long getMaxMemoryUsed() {
            return maxMemoryUsed;
        }

        @Override
        public void run() {
            Runtime runtime = Runtime.getRuntime();
            while (running) {
                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                if (usedMemory > maxMemoryUsed) {
                    maxMemoryUsed = usedMemory;
                }
                //System.out.println("Current memory usage: " + (usedMemory / 1024 / 1024) + " MB");
                try {
                    Thread.sleep(intervalMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    
}


    
