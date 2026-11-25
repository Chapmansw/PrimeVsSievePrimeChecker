public class BasicPrimeChecker implements PrimeChecker {
    public boolean isPrime(long val) {
        checkValidity(val);

        if (val == 2) {
            return true;
        }
        
        if (val % 2 == 0){
            return false;
        }
        
        // n is not even, so we don't need to check even factors
        long i = 3;
        long bound = (long) Math.ceil(Math.sqrt(val));
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