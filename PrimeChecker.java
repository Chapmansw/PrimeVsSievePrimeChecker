public interface PrimeChecker {
    /**
     * Returns true if and only if val is prime
     */
    public boolean isPrime(long val);

    default public void checkValidity(long val) {
        if (val <= 1) {
            throw new IllegalArgumentException("Can only check primality for numbers greater than 1");
        }
    }
}