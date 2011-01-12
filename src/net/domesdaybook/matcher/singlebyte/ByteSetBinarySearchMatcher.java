/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.Arrays;
import java.util.Set;
import net.domesdaybook.reader.ByteReader;

/**
 * A ByteSetBinarySearchMatcher is a {@link SingleByteMatcher which
 * uses a binary search to determine whether a given byte is in the
 * set of bytes.  This makes it more memory efficient than the {@link ByteSetMatcher} class, at the expense of slightly more
 * time to match.
 *
 * @author Matt Palmer
 */
public final class ByteSetBinarySearchMatcher extends InvertibleMatcher implements SingleByteMatcher {

    private static final String ILLEGAL_ARGUMENTS = "Null or empty set of bytes passed in to ByteSetBinarySearchMatcher.";

    private byte[] bytes;


    /**
     * Constructs an immutable ByteSetBinarySearchMatcher.
     * 
     * @param bytes The Set of bytes to match.
     * @param inverted Whether the set of bytes is inverted or not.
     * @throws {@link IllegalArgumentException} if the set is null or empty.
     */
    public ByteSetBinarySearchMatcher(final Set<Byte> bytes, final boolean inverted) {
        super(inverted);
        if (bytes == null || bytes.isEmpty()) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENTS);
        }
        this.bytes = new byte[bytes.size()];
        int byteIndex = 0;
        for (Byte b : bytes) {
            this.bytes[byteIndex++] = b;
        }
        Arrays.sort(this.bytes);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean matches(byte theByte) {
        return Arrays.binarySearch(bytes, theByte) >= 0 ^ inverted;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] getMatchingBytes() {
        if (inverted) {
            final byte[] invertedValues = new byte[getNumberOfMatchingBytes()];
            int byteIndex = 0;
            for (int value = 0; value < 256; value++) {
                if (matches((byte) value)) {
                    invertedValues[byteIndex++] = (byte) value;
                }
            }
            return invertedValues;
        } else {
            return bytes;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final int getNumberOfMatchingBytes() {
        return inverted ? 256 - bytes.length : bytes.length;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final String toRegularExpression(final boolean prettyPrint) {
        StringBuilder regularExpression = new StringBuilder();
        if ( prettyPrint ) {
            regularExpression.append(' ');
        }
        regularExpression.append("[");
        if (inverted) {
            regularExpression.append("^");
        }
        int byteIndex = 0;
        while (byteIndex < bytes.length) {
            int byteValue = (int) bytes[byteIndex];

            // Look for ranges of values from this position:
            int lastValue = byteValue;
            int searchIndex;
            for (searchIndex = byteIndex + 1; searchIndex < bytes.length; searchIndex++) {
                int nextValue = (int) bytes[byteIndex];
                if (nextValue == lastValue+1) {
                    lastValue = nextValue;
                } else {
                    break;
                }
            }

            // If we have a range of more than 2 contiguous positions,
            // represent this as a range of values:
            if (lastValue - byteValue > 2) {
                final String minValue = ByteUtilities.byteToString(prettyPrint, byteValue);
                final String maxValue = ByteUtilities.byteToString(prettyPrint, lastValue);
                regularExpression.append( String.format("%s-%s", minValue, maxValue));
                byteIndex = searchIndex + 1;
            } else { // just write out this byte.
                final String byteVal = ByteUtilities.byteToString(prettyPrint, byteValue);
                regularExpression.append(byteVal);
                byteIndex++;
            }
        }
        regularExpression.append("]");
        if (prettyPrint) {
            regularExpression.append(' ');
        }
        return regularExpression.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean matches(final ByteReader reader, final long matchFrom) {
        return matches(reader.readByte(matchFrom));
    }

}
