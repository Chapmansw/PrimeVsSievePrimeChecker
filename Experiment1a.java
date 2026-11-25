import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class Experiment1a {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"));

        PrintWriter resultsOut = new PrintWriter("results_" + timestamp + ".csv");
        // resultsOut.println("use_case,total_running_time_seconds,time_per_query_seconds,memory_MB");
        
        // TODO: add more use cases here, calling ".flush" after each one
        //  to keep the file current as experiments are running
        // How long is the array [empty array, small array, large array]
        // how big are the numbers [small, medium, large]
        // and how many primes are there [no primes, few primes, many/all primes, all primes]
        int[] arrLength={10000,100000,1000000};
        long[] largeN={10000,100000,2000000000000L};
        double[] percentPrime={.1,.0,.9};
        int experiment=0;
        for(int x=0;x<3;x++){
            for(int y=0;y<3;y++){
                for(int z=0;z<3;z++){
                    experiment++;
                    double[] results = runExperiment(arrLength[x],largeN[y],(int)Math.floor(((double)arrLength[x])*percentPrime[z]));
                    resultsOut.println("Experiment:"+experiment+", Array Size:"+arrLength[x]+ ", Largest Number Possible: "+largeN[y]+", Number of primes:"+(int)Math.floor(((double)arrLength[x])*percentPrime[z])+", \n Sieve: Total Running Time in ms:" + results[0] + ", Time Per Query in ms:" + results[1] + ", Memory Used:" + results[2]+"mb \n Basic: Total Running Time in ms:" + results[3] + ", Time Per Query in ms:" + results[4] + ", Memory Used:" + results[5]+"mb\n");
                    resultsOut.flush();
                    
                }
            }
        }
        resultsOut.close();
    }

    /**
     * @return array of three things: total running time (seconds), running time per query (seconds), and peak memory usage (MB)
     */
    public static double[] runExperiment(int arrLength, long largestN, int numPrimes) throws InterruptedException {
        // Setup for measuring running time and memory usage
        

        int numQueries; // TODO: set this in the block below, according to your use case
            // TODO: put your experiment code here
            // You'll want to add parameters to runExperiment to specify a particular use case,
            // unlike this hard-coded (way-too-small) use case I have here as a simple example

            SieveBasedPrimeChecker checker = new SieveBasedPrimeChecker(arrLength);
            long[] values=new long[arrLength];
            Random rand=new Random();
            int currentPrimes=0;
            System.out.println("Starting set");
            for(int x=0;x<arrLength;x++){
                long randLong=rand.nextLong(largestN-1)+2;
                if(checker.isPrime(randLong)&&currentPrimes<numPrimes){
                    currentPrimes++;
                }
                else if(currentPrimes>=numPrimes){
                    try{
                         while(checker.isPrime(randLong)){
                            randLong=rand.nextLong(largestN-1)+2;
                        }
                    }catch(Exception e){
                        System.out.println(randLong);
                        System.out.println(arrLength);
                        System.out.println(largestN);
                        System.out.println(numPrimes);
                    }
                   
                }
                values[x]=randLong;
            }
        System.out.println("Finished Creating Set");
        long startTime = System.nanoTime();
        MemoryMonitor memoryMonitor = new MemoryMonitor(10);
        Thread monitorThread = new Thread(memoryMonitor, "memory-monitor");
        monitorThread.setDaemon(true);
        monitorThread.start();
        numQueries = values.length;
        for (long val : values) {
            checker.isPrime(val);
        }


        memoryMonitor.stop();
        monitorThread.join();

        long elapsedMillis = (System.nanoTime() - startTime) / 1_000_000;
        long maxMemory = (memoryMonitor.getMaxMemoryUsed() / 1024 / 1024);
        System.out.println("Sieve Max memory usage: " + maxMemory + " MB");
        System.out.println("Sieve Total elapsed time: " + elapsedMillis*1.0 + " ms");
        System.out.println("Sieve Average time per query: " + (elapsedMillis / numQueries) + " ms");
        System.out.println("\n\n\n\n");

        BasicPrimeChecker checker2=new BasicPrimeChecker();
        long startTime2 = System.nanoTime();
        MemoryMonitor memoryMonitor2 = new MemoryMonitor(10);
        Thread monitorThread2 = new Thread(memoryMonitor2, "memory-monitor");
        monitorThread2.setDaemon(true);
        monitorThread2.start();
        numQueries = values.length;
        for (long val : values) {
            checker2.isPrime(val);
        }


        memoryMonitor2.stop();
        monitorThread2.join();

        long elapsedMillis2 = (System.nanoTime() - startTime2) / 1_000_000;
        long maxMemory2 = (memoryMonitor2.getMaxMemoryUsed() / 1024 / 1024);
        System.out.println("Basic Max memory usage: " + maxMemory2 + " MB");
        System.out.println("Basic Total elapsed time: " + elapsedMillis2*1.0 + " ms");
        System.out.println("Basic Average time per query: " + (elapsedMillis2 / numQueries) + " ms");
        System.out.println("\n\n\n\n");

        double[] res = new double[6];
        res[0] = elapsedMillis*1.0;
        res[1] = res[0] / numQueries;
        res[2] = maxMemory;
        res[3] = elapsedMillis2*1.0;
        res[4] = res[3]/numQueries;
        res[5] = maxMemory2;

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