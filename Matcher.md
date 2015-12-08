## Matching bytes ##

Byte matching is done through the Matcher interface:
```
public interface Matcher {
    /**
     * Returns whether there is a match or not at the given position.
     * 
     * @param reader The ByteReader to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     */
    public boolean matches(Reader reader, long matchPosition);

    
    /**
     * Returns whether there is a match or not at the given position in a byte array.
     * 
     * @param bytes An array of bytes to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     */
    public boolean matches(byte[] bytes, int matchPosition);
}
```