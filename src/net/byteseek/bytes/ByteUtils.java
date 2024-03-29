/*
 * Copyright Matt Palmer 2009-2014, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.byteseek.bytes;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.byteseek.object.ArgUtils;

/**
 * A utility class containing useful methods to work with bytes, including:
 * <ul>
 * <li>Translating between arrays and collections of bytes
 * <li>Counting bits in bytes.
 * <li>Counting permutations of bytes given a bit mask matching any or all bits.
 * <li>Returning the set of bytes matching a bit mask (on any or all of them).
 * </ul>
 * 
 * @author Matt Palmer
 */
public final class ByteUtils {
	
	private static final String CHAR_BYTE_FORMAT = "'%c'";
	private static final String HEX_BYTE_FORMAT = "%02x";

	private static final byte ASCII_CASE_DIFFERENCE = 32;
    
	private static final int QUOTE_CHARACTER_VALUE = 39;
    private static final int START_PRINTABLE_ASCII = 32;
    private static final int END_PRINTABLE_ASCII = 126;

    private static int[] VALID_ALL_BITMASK_SET_SIZES = {1, 2, 4, 8, 16, 32, 64, 128, 256};
    private static int[] VALID_ANY_BITMASK_SET_SIZES = {0, 128, 192, 224, 240, 248, 252, 254, 255};

    private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    /**
     * Private constructor for static utility class.
     */
    private ByteUtils() {
    }


    /**
     * Returns the number of bits set in a given byte.
     * 
     * Algorithm taken from:
     * http://www-graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
     * 
     * @param b The byte to count the set bits.
     * @return The number of bits set in the byte.
     */
    public static int countSetBits(final byte b) {
        final int bits = b & 0xFF;
        int result = bits - ((bits >>> 1) & 0x55);
        result = ((result >>> 2) & 0x33) + (result & 0x33);
        result = ((result >>> 4) + result) & 0x0F;
        return result;
    }


    /**
     * Returns the number of unset bits in a given byte.
     *
     * @param b The byte to count the unset bits.
     * @return The number of bits unset in the byte.
     */
    public static int countUnsetBits(final byte b) {
        return 8 - countSetBits(b);
    }


    /**
     * Returns the number of bytes which would match all the bits
     * in a given bitmask.
     * <p>
     * Note that if the bitmask is zero, then this will match all bytes, since
     * the matching algorithm is byte & bitmask == bitmask.
     * A bitmask of zero will always produce zero bits when ANDed with any byte.
     *
     * @param bitmask The bitmask.
     * @return The number of bytes matching all the bits in the bitmask.
     */
    public static int countBytesMatchingAllBits(final byte bitmask) {
    	return 1 << countUnsetBits(bitmask);
    }
    

    /**
     * Returns the number of bytes which would match any of the bits
     * in a given bitmask.
     * <p>
     * Note that if the bitmask is zero, then this will never match any byte, since
     * the matching algorithm is byte & bitmask != 0.
     * A bitmask of zero will always produce zero bits when ANDed with any byte, and
     * this can never be anything other than zero - which means no match.
     * 
     * @param bitmask The bitmask.
     * @return The number of bytes matching any of the bits in the bitmask.
     */
    public static int countBytesMatchingAnyBit(final byte bitmask) {
        return 256 - countBytesMatchingAllBits(bitmask);
    }

    /**
     * Returns a byte array containing the byte values which match an all bitmask.
     * 
     * @param bitMask The bitmask to match.
     * @return An array of bytes containing the bytes that matched the all bitmask.
     */
    public static byte[] getBytesMatchingAllBitMask(final byte bitMask) {
    	final int numberOfBytes = countBytesMatchingAllBits(bitMask);
    	final byte[] bytes = new byte[numberOfBytes];
    	int arrayCount = 0;
    	for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
    		if ((((byte) byteIndex) & bitMask) == bitMask) {
    			bytes[arrayCount++] = (byte) byteIndex;
    		}
    	}
    	return bytes;
    }
    
    
    /**
     * Returns a byte array containing the byte values which do not match an all bitmask.
     * 
     * @param bitMask The bitmask to match.
     * @return An array of bytes containing the bytes that did not match the all bitmask.
     */
    public static byte[] getBytesNotMatchingAllBitMask(final byte bitMask) {
    	final int numberOfBytes = 256 - countBytesMatchingAllBits(bitMask);
    	final byte[] bytes = new byte[numberOfBytes];
    	int arrayCount = 0;
    	for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
    		if ((((byte) byteIndex) & bitMask) != bitMask) {
    			bytes[arrayCount++] = (byte) byteIndex;
    		}
    	}
    	return bytes;
    }
    
    
    /**
     * Returns a byte array containing the byte values which match an any bitmask.
     * 
     * @param bitMask The bitmask to match.
     * @return An array of bytes containing the bytes that matched the any bitmask.
     */
    public static byte[] getBytesMatchingAnyBitMask(final byte bitMask) {
    	final int numberOfBytes = countBytesMatchingAnyBit(bitMask);
    	final byte[] bytes = new byte[numberOfBytes];
    	int arrayCount = 0;
    	for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
    		if ((((byte) byteIndex) & bitMask) != 0) {
    			bytes[arrayCount++] = (byte) byteIndex;
    		}
    	}
    	return bytes;
    }
    
    
    /**
     * Returns a byte array containing the byte values which do not match an any bitmask.
     * 
     * @param bitMask The bitmask to match.
     * @return An array of bytes containing the bytes that did not match the any bitmask.
     */
    public static byte[] getBytesNotMatchingAnyBitMask(final byte bitMask) {
    	final int numberOfBytes = 256 - countBytesMatchingAnyBit(bitMask);
    	final byte[] bytes = new byte[numberOfBytes];
    	int arrayCount = 0;
    	for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
    		if ((((byte) byteIndex) & bitMask) == 0) {
    			bytes[arrayCount++] = (byte) byteIndex;
    		}
    	}
    	return bytes;
    }
    
    
    /**
     * Adds the bytes which would match all the bits in a given bitmask to a 
     * Collection of Byte.
     *
     * @param bitMask The bitmask to match.
     * @param bytes The collection of bytes to add the Bytes to.
     * @throws IllegalArgumentException if the collection of bytes passed in is null.
     */
    public static void addBytesMatchingAllBitMask(final byte bitMask, 
    											  final Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((((byte) byteIndex) & bitMask) == bitMask) {
                bytes.add(Byte.valueOf(byteValue));
            }
        }
    }
    
	/**
	 * Adds all possible bytes to a collection of Byte.
	 * 
	 * @param bytes A collection of bytes to add all possible bytes to.
	 * @throws IllegalArgumentException if the collection of bytes passed in is null.
	 */
	public static void addAllBytes(final Collection<Byte> bytes) {
		ArgUtils.checkNullCollection(bytes);
    	for (int i = 0; i < 256; i++) {
			bytes.add(Byte.valueOf((byte) i));
		}
	}

    
    /**
     * Adds the bytes not matching an all-bit bitmask to a collection of Byte.
     *
     * @param bitMask The bitmask to not match.
     * @param bytes The collection of bytes to add the bytes to.
	 * @throws IllegalArgumentException if the collection of bytes passed in is null. 
     */
    public static void addBytesNotMatchingAllBitMask(final byte bitMask,
    												 final Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((((byte) byteIndex) & bitMask) != bitMask) {
                bytes.add(Byte.valueOf(byteValue));
            }
        }
    }


    /**
     * Returns a bitmask which would match the set of bytes in the array
     * and no others, if they must match all the bits in the bitmask.  If no
     * such bitmask exists, then null is returned.
     * 
     * @param bytes An array of bytes for which a matching bitmask is required.
     * @return A bitmask which matches all the bytes in the array (and no others)
     *         or null if no such bitmask exists.
     * @throws IllegalArgumentException if the byte array is null.
     */
    public static Byte getAllBitMaskForBytes(final byte[] bytes) {
        return getAllBitMaskForBytes(toSet(bytes));
    }
    

    /**
     * Returns a set of bytes from an array of bytes.
     * 
     * @param bytes The array of bytes.
     * @return A set of bytes.
     * @throws IllegalArgumentException if the byte array is null.
     */
    public static Set<Byte> toSet(final byte[] bytes) {
    	ArgUtils.checkNullByteArray(bytes);
        final Set<Byte> setOfBytes = new HashSet<Byte>((int) (bytes.length / 0.75));
        addAll(bytes, setOfBytes);
        return setOfBytes;
    }
    

    /**
     * Returns a list of bytes from an array of bytes.
     * 
     * @param bytes The array of bytes
     * @return A list of bytes
     * @throws IllegalArgumentException if the byte array is null. 
     */
    public static List<Byte> toList(final byte[] bytes) {
    	ArgUtils.checkNullByteArray(bytes);
        final List<Byte> listOfBytes = new ArrayList<Byte>(bytes.length);
        for (final byte b : bytes) {
            listOfBytes.add(Byte.valueOf(b));
        }
        return listOfBytes;
    }


    /**
     * Adds all the bytes in an array to a collection of Bytes.
     * 
     * @param bytes The array of bytes to add.
     * @param toCollection The collection of Bytes to add to.
     * @throws IllegalArgumentException if the byte array or collection is null.
     */
    public static void addAll(final byte[] bytes, final Collection<Byte> toCollection) {
        ArgUtils.checkNullByteArray(bytes);
        ArgUtils.checkNullCollection(toCollection);
    	final int size = bytes.length;
        for (int count = 0; count < size; count++) {
            toCollection.add(Byte.valueOf(bytes[count]));
        }
    }
    
    
    /**
     * Adds all the bytes specified as byte parameters to the collection.
     * @param toCollection The collection to add the bytes to.
     * @param values The byte values as parameters.
     * @throws IllegalArgumentException if the collection is null.
     */
    public static void addBytes(final Collection<Byte> toCollection, final byte...values) {
    	ArgUtils.checkNullCollection(toCollection);
    	for (final byte value : values) {
    		toCollection.add(Byte.valueOf(value));
    	}
    }
    
    
    /**
     * Adds the bytes in a string encoded as ISO-8859-1 bytes to a collection of bytes.
     * 
     * @param string The ISO-8859-1 string whose bytes should be added
     * @param toCollection The collection to add the bytes to.
     * @throws IllegalArgumentException if the string or collection is null.
     */
    public static void addStringBytes(final String string, final Collection<Byte> toCollection) {
    	addAll(getBytes(string), toCollection);
    }
    
    
    /**
     * Returns a byte array of the string passed in, encoded as ISO-8859-1.
     * <p>
     * If the string cannot be encoded as ISO-8859-1, then the behaviour of this
     * method is undefined.
     * 
     * @param string The string to convert to a byte array encoded as ISO-8859-1
     * @return The byte array representing the string encoded as ISO-8859-1
     * @throws IllegalArgumentException if the string passed in is null.
     */
    public static byte[] getBytes(final String string) {
    	ArgUtils.checkNullString(string);
   		return string.getBytes(ISO_8859_1);
    }
    
    
    /**
     * Adds the bytes in a string encoded as ISO-8859-1 to a collection of bytes.
     * Upper and lower case bytes are also added if their counterpart is encountered.
     * 
     * @param string The ISO-8859-1 string whose bytes should be added
     * @param toCollection The collection to add the bytes to.
     * @throws UnsupportedEncodingException If the string cannot be encoded as ISO-8859-1.
     * @throws IllegalArgumentException if the string or collection passed in is null. 
     */
    public static void addCaseInsensitiveStringBytes(final String string, final Collection<Byte> toCollection) {
    	ArgUtils.checkNullCollection(toCollection);
    	final byte[] byteValues = getBytes(string);
		for (int charIndex = 0; charIndex < byteValues.length; charIndex++) {
			final byte charAt = byteValues[charIndex];
			if (charAt >= 'a' && charAt <= 'z') {
				toCollection.add(Byte.valueOf((byte) (charAt - ASCII_CASE_DIFFERENCE)));
			} else if (charAt >= 'A' && charAt <= 'Z') {
				toCollection.add(Byte.valueOf((byte) (charAt + ASCII_CASE_DIFFERENCE)));
			}
			toCollection.add(Byte.valueOf(charAt));
		}
    }
    
    
    /**
     * Returns an array of bytes from a collection of Bytes.
     * 
     * @param collection The collection of bytes to convert to an array.
     * @return An array of bytes
     * @throws IllegalArgumentException if the collection passed in is null.
     */
    public static byte[] toArray(final Collection<Byte> collection) {
    	ArgUtils.checkNullCollection(collection);
    	final byte[] result = new byte[collection.size()];
        int position = 0;
        for (final Byte b : collection) {
            result[position++] = b;
        }
        return result;
    }
    
    	
    /**
     * Returns an array of bytes from a list of byte parameters.
     * 
     * @param values The byte parameters
     * @return A byte array of the parameters.
     */
    public static byte[] toArray(byte... values) {
    	return values;
    }
    
    
    /**
     * Reverses an array of bytes.
     * 
     * @param array The array of bytes to reverse.
     * @return byte[] The reversed array of bytes.
     * @throws IllegalArgumentException if the array passed in is null.
     */
    public static byte[] reverseArray(final byte[] array) {
    	ArgUtils.checkNullByteArray(array);
        final int lastpos = array.length - 1;
        final byte[] reversed = new byte[array.length];
        for (int i = 0; i <= lastpos; i++) {
            reversed[i] = array[lastpos - i];
        }
        return reversed;
    }
    
    
    /**
     * Reverses a subsequence of an array.
     * 
     * @param array The array to reverse a subsequence of.
     * @param startIndex The start position in the array, inclusive.
     * @param endIndex The end index in the array, exclusive.
     * @return A new array containing the bytes of the original array from the
     *         start index to the end index, in reverse order.
     * @throws IllegalArgumentException if the array is null or the indexes are outside of the array.
     */
    public static byte[] reverseArraySubsequence(final byte[] array, final int startIndex, final int endIndex) {
        checkBounds(array, startIndex, endIndex);
    	final int length = endIndex - startIndex;
        final int endPos = endIndex - 1;
        final byte[] reversed = new byte[length];
        for (int i = 0; i < length; i++) {
            reversed[i] = array[endPos - i];
        }
        return reversed;        
    }

    
    /**
     * Returns a byte array containing the original array passed in repeated a 
     * number of times.  It will always create a new array, even if the number of
     * times to repeat is only one.
     * @param numberOfRepeats The number of times to repeat the array.
     * @param array The array to repeat.
     * 
     * @return A new array containing the original array repeated a number of time.
     * @throws IllegalArgumentException if the array is null or the number of repeats is negative.
     */
    public static byte[] repeat(final int numberOfRepeats, final byte[] array) {
    	ArgUtils.checkNullByteArray(array);
    	checkNegativeRepeats(numberOfRepeats);
    	final int repeatLength = array.length;
        final int size = repeatLength * numberOfRepeats;
        final byte[] repeated = new byte[size];
        for (int repeat = 0; repeat < numberOfRepeats; repeat++) {
            System.arraycopy(array, 0, repeated, repeat * repeatLength, repeatLength);
        }    
        return repeated;
    }

    
    /**
     * Returns a byte array containing the original array passed in, 
     * with a subsequence of it repeated a number of times.  
     * It will always produce a new array, even if the number of repeats is only one.
     * @param numberOfRepeats The number of times to repeat the subsequence.
     * @param array The array to repeat a subsequence of.
     * @param startIndex The start index to begin repeating the array from, inclusive.
     * @param endIndex The end index to stop repeating the array from, exclusive.
     * 
     * @return A new byte array consisting of the portions of the original array
     *         from the startIndex to the endIndex repeated.
     * @throws IllegalArgumentException if the array passed in is null, the number of repeats
     *         is negative, or the indexes are out of bounds for the array.
     */
    public static byte[] repeat(final int numberOfRepeats, final byte[] array,
                                final int startIndex, final int endIndex) {
    	checkBounds(array, startIndex, endIndex);
    	checkNegativeRepeats(numberOfRepeats);
    	final int repeatLength = endIndex - startIndex;
        final int size = repeatLength * numberOfRepeats;
        final byte[] repeated = new byte[size];
        for (int repeat = 0; repeat < numberOfRepeats; repeat++) {
            System.arraycopy(array, startIndex, repeated, repeat * repeatLength, repeatLength);
        }    
        return repeated;
    }    
    
    
    /**
     * Returns a byte array filled with the value for the number of repeats.
     * 
     * @param value The value to repeat
     * @param numberOfRepeats The number of times to repeat the value.
     * @return A byte array sized to the number of repeats filled with the value.
     * @throws IllegalArgumentException if the number of repeats is negative.
     */
    public static byte[] repeat(final byte value, final int numberOfRepeats) {
        checkNegativeRepeats(numberOfRepeats);
        final byte[] repeats = new byte[numberOfRepeats];
        Arrays.fill(repeats, value);
        return repeats;
    }
    
    
    /**
     * Converts an array of bytes to an array of ints in the range 0 to 255.
     * 
     * @param bytes The byte array.
     * @return int[] The integer array.
     * @throws IllegalArgumentException if the byte array is null.
     */
    public static int[] toIntArray(final byte[] bytes) {
    	ArgUtils.checkNullByteArray(bytes);
        final int length = bytes.length;
    	final int[] integers = new int[length];
        for (int index = 0; index < length; index++) {
            integers[index] = bytes[index] & 0xFF;
        }
        return integers;
    }


    /**
     * Returns an array of bytes containing all possible byte values.
     * 
     * @return byte[] The array of bytes.
     */
    public static byte[] getAllByteValues() {
        return getBytesInRange(0, 255);
    }


    /**
     * Returns an array of bytes in the range of values inclusive.  The from and to
     * values can be specified in any order (greater or smaller than each other), but
     * the range returned will always be in order of smallest to highest values.
     * <p>
     * Note: byte values are specified as integer in the range 0 to 255 (unsigned).
     * 
     * @param from The lowest byte value to include.
     * @param to The highest byte value to include.
     * @return byte[] The array of bytes.
     * @throws IllegalArgumentException if the from or to values are not between 0 and 255 inclusive.
     */
    public static byte[] getBytesInRange(final int from, final int to) {
    	checkIntToByteRange(from, to);
    	final int start = from < to? from : to;
        final int end   = from < to? to : from;
    	final byte[] range = new byte[end-start+1];
        int position = 0;
        for (int value = start; value <= end; value++) {
            range[position++] = (byte) value;
        }
        return range;
    }


    /**
     * Adds all the bytes in a range to a collection of Byte.  The range can be specified
     * either forwards or backwards.
     * 
     * @param from A number in the range from 0 to 255;
     * @param to A number in the range from 0 to 255.
     * @param bytes A set of bytes to add the bytes in the range to.
     * @throws IllegalArgumentException if the from or to values are not between 0 and 255 inclusive
     * 									or the collection of bytes is null.
     */
    public static void addBytesInRange(final int from, final int to, final Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	checkIntToByteRange(from, to);
    	final int start = from < to? from : to;
    	final int end =   from < to? to : from;
    	for (int value = start; value <= end; value++) {
    		bytes.add(Byte.valueOf((byte) value));
    	}
    }
    
    
   /**
    * Adds all the bytes other than the byte provided to a collection of Byte.  
    * 
    * @param value The byte value which should not appear in the collection of bytes.
    * @param bytes A set of bytes to add all the other bytes to.
    * @throws IllegalArgumentException if collection of bytes passed in is null.
    */
    public static void addInvertedByteValues(final byte value, final Collection<Byte> bytes) {
    	final int intValue = value & 0xFF;
    	if (intValue > 0) {
    		addBytesInRange(0, intValue - 1, bytes);
    	}
    	if (intValue < 255) {
    		addBytesInRange(intValue + 1, 255, bytes);
    	}
    }
    
    
    /**
     * Adds all the bytes in an inverted range to a collection.  The inverted range can be specified
     * either forwards or backwards. An inverted range contains all bytes except for the ones
     * specified in the range (which is inclusive).
     * 
     * @param from A number in the range from 0 to 255;
     * @param to A number in the range from 0 to 255.
     * @param bytes A byte collection to add the bytes in the inverted range to.
     * @throws IllegalArgumentException if the from or to values are not between 0 and 255,
     *                                  or the collection of bytes passed in is null.
     */
    public static void addBytesNotInRange(final int from, final int to, final Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	checkIntToByteRange(from, to);
    	final int start = from < to? from : to;
    	final int end =   from < to? to : from;
    	for (int value = 0; value < start; value++) {
    		bytes.add(Byte.valueOf((byte) value));
    	}
    	for (int value = end + 1; value < 256; value++) {
    		bytes.add(Byte.valueOf((byte) value));
    	}
    }
    
    
    /**
     * Returns an inverted set of bytes.  This set of bytes contains all other
     * possible byte values than the ones in the set provided.
     * <p>
     * The set returned is a HashSet with a capacity of the size of the
     * set of bytes passed in, divided by the default load factor.  If you want
     * to specify a different set, call buildInvertedSet instead.
     *  
     * @param bytes A set of bytes.
     * @return Set<Byte> A set of all other bytes.
     * @throws IllegalArgumentException if the set of bytes passed in is null.
     */
    public static Set<Byte> invertedSet(final Set<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	final int capacity = (int) (bytes.size() / 0.75);
        final Set<Byte> inverted = new HashSet<Byte>(capacity);
        buildInvertedSet(bytes, inverted);
        return inverted;
    }
    
    
    /**
     * Returns true if one set of bytes is the inverse of the other.
     * Neither set is modified.
     * 
     * @param set The first set to test
     * @param inverseSet The other set to test
     * @return True if the sets are the inverse of each other.
     * @throws IllegalArgumentException if either of the sets passed in is null.
     */
    public static boolean inverseOf(final Set<Byte> set, final Set<Byte> inverseSet) {
    	ArgUtils.checkNullCollection(set, "parameter:set");
    	ArgUtils.checkNullCollection(inverseSet, "parameter:inverseSet");
    	// If the set sizes are compatible with being the inverse of each other:
    	if (set.size() == 256 - inverseSet.size()) {
    		// Go through  the bytes in the smaller set, to see if they appear in the
    		// bigger set.  If any do, the sets are not inverses of each other.
    		final boolean setIsSmaller = set.size() < inverseSet.size();
    		final Set<Byte> needles  = setIsSmaller? set : inverseSet;
    		final Set<Byte> haystack = setIsSmaller? inverseSet : set;
    		for (final Byte needle : needles) {
    			if (haystack.contains(needle)) {
    				return false;
    			}
    		}
    		return true;
    	}
    	return false;
    }
    
    
    /**
     * Returns an inverted set of bytes containing all bytes except 
     * for the value passed in.  
     * 
     * @param value The value which should not appear in the set of bytes.
     * @return Set<Byte> A set of all other bytes.
     */
    public static Set<Byte> invertedSet(final byte value) {
        final Set<Byte> inverted = new HashSet<Byte>(342);
        for (int i = 0; i < 256; i ++) {
        	inverted.add(Byte.valueOf((byte) i));
        }
        inverted.remove(Byte.valueOf(value));
        return inverted;
    }    
    

    /**
     * Builds an inverted set of bytes.  This set of bytes contains all other
     * possible byte values than the ones in the set provided.
     * 
     * @param bytes A set of bytes.
     * @param invertedSet The set of bytes to add the inverted bytes to.  
     * @throws IllegalArgumentException if either of the sets passed in is null.  
     */
    public static void buildInvertedSet(final Set<Byte> bytes, final Set<Byte> invertedSet) {
    	ArgUtils.checkNullCollection(bytes, "parameter:bytes");
    	ArgUtils.checkNullCollection(invertedSet, "parameter:invertedSet");
    	for (int value = 0; value < 256; value++) {
            if (!bytes.contains((byte) value)) {
                invertedSet.add(Byte.valueOf((byte) value));
            }
        }
    }    
    
    
    /**
     * Removes any bytes in common from the sets passed in (the intersection of the two sets)
     * and returns a list containing the intersection.
     * 
     * @param firstSet The first set of bytes.  Any bytes in common with the second set will be removed.
     * @param secondSet The second set of bytes.  Any bytes in common with the first set will be removed.
     * @return A list containing the intersection of the two sets
     * @throws IllegalArgumentException if either of the sets passed in is null. 
     */
    public static List<Byte> removeIntersection(final Set<Byte> firstSet, final Set<Byte> secondSet) {
    	final List<Byte> bytesRemoved = new ArrayList<Byte>();
        removeIntersection(firstSet, secondSet, bytesRemoved);
        return bytesRemoved;
    }   
    
    
    /**
     * Removes any bytes in common from the sets passed in (the intersection of the two sets)
     * and adds the intersection to a collection also passed in.
     * 
     * @param firstSet The first set of bytes.  Any bytes in common with the second set will be removed.
     * @param secondSet The second set of bytes.  Any bytes in common with the first set will be removed.
     * @param bytesRemoved Any bytes in the intersection of the two sets are added to this collection.
     * @throws IllegalArgumentException if any of the collections passed in are null.
     */
    public static void removeIntersection(final Set<Byte> firstSet, 
                                          final Set<Byte> secondSet,
                                          final Collection<Byte> bytesRemoved) {
    	ArgUtils.checkNullCollection(firstSet, "parameter:firstSet");
    	ArgUtils.checkNullCollection(secondSet, "parameter:secondSet");
    	ArgUtils.checkNullCollection(bytesRemoved, "parameter:bytesRemoved");
    	final Iterator<Byte> byteIterator = firstSet.iterator();
        while (byteIterator.hasNext()) {
            final Byte theByte = byteIterator.next();
            if (secondSet.remove(theByte)) {
                bytesRemoved.add(theByte);
                byteIterator.remove();
            }
        }
    }

    
    /**
     * Returns the log base 2 of an integer, rounded to the floor.
     * 
     * Note that the integer must be positive.
     * 
     * @param i The integer
     * @return int the log base 2 of an integer, rounded to the floor. 
     * @throws IllegalArgumentException if the integer passed in is zero or negative.
     */
    public static int floorLogBaseTwo(final int i) {
    	checkPositiveInteger(i);
        return 31 - Integer.numberOfLeadingZeros(i);
    }
    
    
    /**
     * Returns the log base 2 of an integer, rounded to the ceiling.
     * 
     * Note that the integer must be positive.
     * 
     * @param i The integer.
     * @return int the log base 2 of an integer, rounded to the ceiling.
     * @throws IllegalArgumentException if the integer passed in is zero or negative.
     */
    public static int ceilLogBaseTwo(final int i) {
    	checkPositiveInteger(i);
        return 32 - Integer.numberOfLeadingZeros(i - 1);
    }    
    
    
    /**
     * Returns true if an integer is a power of two.
     * 
     * @param i The integer
     * @return boolean True if the integer was a power of two.
     */
    public static boolean isPowerOfTwo(final int i) {
        return i > 0? (i & (i - 1)) == 0 : false;
    }
    
    
    /**
     * Returns the number which is the next highest power of two bigger than another integer.
     * 
     * @param i The integer
     * @return int the closest number which is a power of two and greater than the original integer.
     */
    public static int nextHighestPowerOfTwo(final int i) {
        return Integer.highestOneBit(i) << 1;
    }
    
    
    /**
     * Calculates a bitmask for which the set of bytes provided would match all of
     * the bits in the bitmask, and for which there are no other bytes it would match.
     * 
     * @param bytes A set of bytes to find an all bitmask to match.
     * @return A bitmask to match the set with, or null if no bitmask exists for that set of bytes.
     * @throws IllegalArgumentException if the set of bytes passed in is null.
     */
    public static Byte getAllBitMaskForBytes(final Set<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
        final int setSize = bytes.size();
        if (setSize == 256) { // if we have all byte values, then a bitmask of zero matches all of them.
        	return Byte.valueOf((byte) 0);
        } else if (Arrays.binarySearch(VALID_ALL_BITMASK_SET_SIZES, setSize) >= 0) {
            // Build a candidate bitmask from the bits all the bytes have in common.
            final int bitsInCommon = getBitsInCommon(bytes);
            if (bitsInCommon > 0) {
                // If the number of bytes in the set is the same as the number of bytes
                // which would match the bitmask, then the set of bytes can be matched
                // by that bitmask.
                final byte mask = (byte) bitsInCommon;
                if (setSize == countBytesMatchingAllBits(mask)) {
                	return Byte.valueOf(mask);
                }
            }
        }
        return null;
    }


    /**
     * Calculates a bitmask for which the set of bytes provided would match any of
     * the bits in the bitmask, and for which there are no other bytes it would match.
     *
     * @param bytes A set of bytes to find an any bitmask to match.
     * @return A bitmask to match the set with, or null if no bitmask exists for that
     *         set of bytes.
     * @throws IllegalArgumentException if the set of bytes passed in is null.
     */
    public static Byte getAnyBitMaskForBytes(final Set<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
        final int setSize = bytes.size();
        if (setSize == 0) {
            return Byte.valueOf((byte)0);
        } else if (Arrays.binarySearch(VALID_ANY_BITMASK_SET_SIZES, setSize) >= 0) {
            // Find which bits in the set are matched by 128 bytes in the set.
            // These bits might form a valid any bitmask.
            final int possibleAnyMask = getBitsSetForAllPossibleBytes(bytes);

            // Check that the any bitmask produced gives a set of bytes
            // the same size as the set provided.
            if (possibleAnyMask > 0) {
                final byte mask = (byte) possibleAnyMask;
                if (setSize == countBytesMatchingAnyBit(mask)) {
                    return Byte.valueOf(mask);
                }
            }
        }
        return null;
    }


    /**
     * Calculates a bitmask for which the set of bytes provided in the array
     * would match all of the bits in the bitmask, and for which there are no 
     * other bytes it would match.
     *
     * @param bytes An array of bytes to find an any bitmask to match.
     * @return A bitmask to match the byte values in the array with, or null, 
     *         if no bitmask exists for that set of  bytes.
     * @throws IllegalArgumentException if the bytes passed in is null.
     */
    public static Byte getAnyBitMaskForBytes(final byte[] bytes) {
        return getAnyBitMaskForBytes(toSet(bytes));
    }
    
    
    /**
     * Returns a bitmask which contains all the bits in common in the collection of bytes
     * provided - anding all the bytes together.  If the collection passed in is empty,
     * then zero will be returned.
     * 
     * @param bytes A collection of bytes to find the bits in common.
     * @return An integer mask containing only the bits in common, in the range 0 to 255.
     * @throws IllegalArgumentException if the collection of bytes passed in is null.
     */
    public static int getBitsInCommon(final Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
        if (bytes.isEmpty()) {
        	return 0;
        }
    	int bitsinCommon = 0xFF;
        for (final Byte b : bytes) {
            bitsinCommon &= b;
        }
        return bitsinCommon;
    }


    /**
     * Calculate a bitmask in which a bit is set if across all the bytes in the 
     * set provided, there were 128 matches for that bit.  This means that the
     * set of bytes contains all the bytes with that bit set.
     * <p>
     * Any given bit can only match a maximum of 128 byte values (the other 128 
     * being the ones where that bit is not set).  
     * 
     * @param bytes A set of bytes 
     * @return int a bitmask containing bits where all possible byte values are
     *             present in the set for that bit.
	 * @throws IllegalArgumentException if the set of bytes passed in is null.             
     */
    public static int getBitsSetForAllPossibleBytes(final Set<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	// Count how many bytes match each bit:
        int bit1 = 0, bit2 = 0, bit3 = 0, bit4 = 0, bit5 = 0, bit6 = 0, bit7 = 0, bit8 = 0;
        for (final Byte b : bytes) {
            final int value = b & 0xFF;
            bit1 += value & 1;
            bit2 += (value & 2) >> 1;
            bit3 += (value & 4) >> 2;
            bit4 += (value & 8) >> 3;
            bit5 += (value & 16) >> 4;
            bit6 += (value & 32) >> 5;
            bit7 += (value & 64) >> 6;
            bit8 += (value & 128) >> 7;
        }
        // produce a mask of the bits which each matched 128 bytes in the set:
        int anyBitMask = 0;
        if (bit1 == 128) anyBitMask = 1;
        if (bit2 == 128) anyBitMask = anyBitMask | 2;
        if (bit3 == 128) anyBitMask = anyBitMask | 4;
        if (bit4 == 128) anyBitMask = anyBitMask | 8;
        if (bit5 == 128) anyBitMask = anyBitMask | 16;
        if (bit6 == 128) anyBitMask = anyBitMask | 32;
        if (bit7 == 128) anyBitMask = anyBitMask | 64;
        if (bit8 == 128) anyBitMask = anyBitMask | 128;
        return anyBitMask;
    }


    /**
     * Adds the bytes matching an any bitmask (any of the bits can match) to 
     * a collection of bytes.
     * 
     * @param bitMask The bitmask to match.
     * @param bytes The collection of bytes to add the values to.
     * @throws IllegalArgumentException if the set of bytes passed in is null.
     */
    public static void addBytesMatchingAnyBitMask(final byte bitMask,
    											  final Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	// start loop at one - any bitmask matchers can never match the zero byte.
        for (int byteIndex = 1; byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((byteValue & bitMask) != 0) {
                bytes.add(Byte.valueOf((byte) byteIndex));
            }
        }
    }
    
    
    /**
     * Adds the bytes not matching an any bitmask (no bits must match) to
     * a collection of Byte.
     * 
     * @param bitMask The bitmask to not match any bits of.
     * @param bytes The collection of Bytes to add the bytes to.
     * @throws IllegalArgumentException if the set of bytes passed in is null.
     */
    public static void addBytesNotMatchingAnyBitMask(final byte bitMask,
    											     Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((byteValue & bitMask) == 0) {
                bytes.add(Byte.valueOf((byte) byteIndex));
            }
        }
    }

    
    /**
     * Returns the byte represented by a two-digit hex string.
     * 
     * @param hexByte The string containing the 2-digit hex representation of a byte.
     * @return The byte represented by the hexByte.
     * @throws IllegalArgumentException if the string does not contain a valid hex byte or is null.
     */
    public static byte byteFromHex(final String hexByte) {
        if (hexByte != null && hexByte.length() == 2) {
            try {
                return (byte) Integer.parseInt(hexByte, 16);
            } catch (NumberFormatException dropThroughToIllegalArgumentException) {
                // do nothing - illegal argument exception will be thrown below.
            }
        }
        throw new IllegalArgumentException("Not a valid hex byte [" + hexByte + ']');
    }

    
    /**
     * Returns a byte value as either a 2-char hex string, or if
     * pretty printing, and the byte value is a printable ASCII
     * character, as a quoted ASCII char, unless it is a single quote
     * character itself, in which case it will still be represented as
     * a hex byte.
     * 
     * @param prettyPrint Whether to pretty print the byte value.
     * @param byteValue The byte value to convert.
     * @return A string containing the byte value as a string.
     */
    public static String byteToString(final boolean prettyPrint, int byteValue) {
        String result;
        if (prettyPrint &&
            byteValue >= START_PRINTABLE_ASCII &&
            byteValue <= END_PRINTABLE_ASCII &&
            byteValue != QUOTE_CHARACTER_VALUE) {
            result = String.format(CHAR_BYTE_FORMAT, byteValue);
        } else {
            result = String.format(HEX_BYTE_FORMAT, byteValue);
        } 
        return result;
    }
    
    
    /**
     * Returns a String containing a 2-digit hex representation of each byte in the
     * array.  If pretty printing and the byte value is a printable ASCII character,
     * these values are returned as a quoted ASCII string delimited with single quotes.
     * Note that a single quote character will be represented as a hex byte
     * Pretty printing also spaces hex bytes. 
     * 
     * @param prettyPrint Whether to pretty print the byte string.
     * @param bytes the array of bytes to convert.
     * @return A string containing the byte values as a string.
     * @throws IllegalArgumentException if the array is null.
     */
    public static String bytesToString(final boolean prettyPrint, final byte[] bytes) {
    	ArgUtils.checkNullByteArray(bytes);
    	return bytesToString(prettyPrint, bytes, 0, bytes.length);
    }
 
    
    /**
     * Returns a String containing a 2-digit hex representation of each byte in the
     * array.  If pretty printing and the byte value is a printable ASCII character,
     * these values are returned as a quoted ASCII string delimited with single quotes.
     * Note that a single quote character will be represented as a hex byte
     * Pretty printing also spaces hex bytes. 
     * 
     * @param prettyPrint Whether to pretty print the byte string.
     * @param bytes the list of Bytes to convert.
     * @return A string containing the byte values as a string.
     * @throws IllegalArgumentException if the collection of bytes is null.
     */
    public static String bytesToString(final boolean prettyPrint, final List<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	return bytesToString(prettyPrint, toArray(bytes), 0, bytes.size());
    }

    
    /**
     * Returns a byte array as a String.  If not pretty printed, the bytes
     * are presented as 2 digit hex numbers.  If pretty printed, then bytes
     * which would be printable ASCII characters are represented as such
     * enclosed in single quotes and hex byte elements are spaced.  The single
     * quote character will not be enclosed in single quotes, but will be represented as
     * a hex byte outside of quotes.
     * 
     * @param prettyPrint Whether to pretty print the byte array.
     * @param bytes The bytes to render as a String.
     * @param startIndex the start index to start at, inclusive
     * @param endIndex the end index to stop at, exclusive.
     * @return A string containing a representation of the byte array.
     * @throws IllegalArgumentException if the array is null or the indexes are outside the array bounds.
     */
    public static String bytesToString(final boolean prettyPrint, final byte[] bytes,
                                       final int startIndex, final int endIndex) {
    	checkBounds(bytes, startIndex, endIndex);
    	final int estimatedSize = prettyPrint? (endIndex - startIndex) * 4 : (endIndex - startIndex) * 2; 
    	final StringBuilder string = new StringBuilder(estimatedSize);
        boolean inString = false;
        boolean firstByte = true;
        for (int byteIndex = startIndex; byteIndex < endIndex; byteIndex++) {
            final int byteValue = 0xFF & bytes[byteIndex];
            if (prettyPrint) {
            	if (!firstByte && !inString) {
            		string.append(' ');
            	}
            	if (byteValue >= START_PRINTABLE_ASCII &&
                    byteValue <= END_PRINTABLE_ASCII &&
                    byteValue != QUOTE_CHARACTER_VALUE) {
                    if (!inString) {
                    	string.append('\'');
                    }
                   	string.append((char) byteValue);
                    inString = true;
            	} else {
            		if (inString) {
            			string.append("' ");
            		}
            		string.append(String.format(HEX_BYTE_FORMAT, byteValue));
                    inString = false;
            	}
            } else {
            	string.append(String.format(HEX_BYTE_FORMAT, byteValue));
            }
            firstByte = false;
        }
        if (prettyPrint && inString) {
            string.append('\'');
        }
        return string.toString();
    }    

    
    /*
     * Private utility methods
     */
    
	private static void checkNegativeRepeats(final int numberOfRepeats) {
		if (numberOfRepeats < 0) {
        	throw new IllegalArgumentException("Number of repeats cannot be negative " + numberOfRepeats);
        }
	}
	
	private static void checkPositiveInteger(final int value) {
		if (value <= 0) {
			throw new IllegalArgumentException("The value must be positive " + value);
		}
	}

	private static void checkBounds(final byte[] array, final int startIndex, final int endIndex) {
		ArgUtils.checkNullByteArray(array);
		if (startIndex < 0 || startIndex >= endIndex || endIndex > array.length) {
        	throw new IllegalArgumentException("The start index must be between 0 inclusive and the array length exclusive" +
        									   ",end index must be greater than the start index and not greater than the length. " +
        									   "Array length is " + array.length + " start index is " + startIndex + " end index is " + endIndex);	
        }
	}

	private static void checkIntToByteRange(final int from, final int to) {
		if (from < 0 || from > 255 || to < 0 || to > 255) {
    		final String message = "The from and to values must be in the range 0 to 255.  Values provided were %d and %d";
    		throw new IllegalArgumentException(String.format(message, from, to));
    	}
	}
	
}
