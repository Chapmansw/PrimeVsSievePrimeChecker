import java.util.TreeSet;

public class SieveBasedPrimeChecker implements PrimeChecker {

    // All primes from 2 through primesScope are in primes
    TreeSet<Long> primes;
    long primesScope;

    public SieveBasedPrimeChecker(int sieveSize) {
        boolean[] isNotPrime = new boolean[sieveSize];
        int bound = (int) Math.ceil(Math.sqrt(sieveSize));

        for (int num = 2; num <= bound; num++) {
            // Mark off all multiples of num
            for (int multiple = 2 * num; multiple < sieveSize; multiple += num) {
                isNotPrime[multiple] = true;
            }
        }

        // isNotPrime is complete
        // Now collect the primes into the member variable to save memory
        primesScope = sieveSize - 1;
        primes = new TreeSet<>();
        for (int i = 2; i < sieveSize; i++) {
            if (! isNotPrime[i]) {
                primes.add((long) i);
            }
        }
    }

    public boolean isPrime(long val) {
        checkValidity(val);

        if (val <= primesScope) {
            return primes.contains(val);
        }

        long bound = (long) Math.ceil(Math.sqrt(val));
        // First, check prime divisors <= primesScope
        for (long i : primes) {
            if (i > bound) {
                // For-each loop uses iterator, which goes in ascending order
                return true;
            }
            if (val % i == 0) {
                return false;
            }
        }

        long i = primesScope + 1;
        if (i % 2 == 0) {
            i++;
        }
        
        while (i <= bound) {
            if (val % i == 0) {
                // Found a factor
                return false;
            }

            i += 2;
        }
        return true;
    }

}