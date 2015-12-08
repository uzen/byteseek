The ByteMatcher interface defines how matchers which match a single byte behave.

```
/**
 * An interface for classes which match a single provided byte.
 * This does not mean that a ByteMatcher can only match a single
 * byte value - for example, it could match a single byte against all the
 * odd bytes, or a set of bytes, or a range of bytes.
 * <p>
 * All implementations of this interface should be immutable.
 * This allows them to be safely shared amongst other classes and threads.
 * 
 * @author Matt Palmer
 */
public interface ByteMatcher extends SequenceMatcher {

    /*
     * Implementations of this method should strive to be as efficient as possible.
     *
     * @return Whether a given byte matches the byte matcher.
     */
    public boolean matches(byte theByte);

    
    /**
     * Implementations of this method can be calculated dynamically,
     * and may not be efficient if called repeatedly.
     *
     * @return An array of all the bytes that this byte matcher could match.
     */
    public byte[] getMatchingBytes();


    /**
     * Implementations of this method can be calculated dynamically,
     * and may not be efficient if called repeatedly.
     *
     * @return The number of bytes this byte matcher will match.
     */
    public int getNumberOfMatchingBytes();
    

    /**
     * Implementations of this method can be calculated dynamically,
     * and may not be efficient if called repeatedly.
     *
     * @return A string representation of a regular expression for this matcher.
     */
    public String toRegularExpression(boolean prettyPrint);

}


```