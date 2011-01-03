/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author Matt Palmer
 */
public class SingleByteSequenceMatcher implements SequenceMatcher {

    private final List<SingleByteMatcher> matcherSequence = new ArrayList<SingleByteMatcher>();
    private final int length;

    
    public SingleByteSequenceMatcher(final List<SingleByteMatcher> sequence) {
        if (sequence == null || sequence.isEmpty()) {
            throw new IllegalArgumentException("Null or empty sequence passed in to SingleByteSequenceMatcher.");
        }
        this.matcherSequence.addAll(sequence);
        this.length = this.matcherSequence.size();
    }


    public SingleByteSequenceMatcher(final SingleByteMatcher matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("Null matcher passed in to SingleByteSequenceMatcher.");
        }
        this.matcherSequence.add(matcher);
        this.length = 1;
    }


    @Override
    public final boolean matches(final ByteReader reader, final long matchFrom) {
        boolean result = true;
        final List<SingleByteMatcher> matchList = this.matcherSequence;
        final int localStop = length;
        for ( int byteIndex = 0; result && byteIndex < localStop; byteIndex++) {
            final SingleByteMatcher byteMatcher = matchList.get(byteIndex);
            final byte byteRead = reader.readByte(matchFrom + byteIndex);
            result = byteMatcher.matches(byteRead);
        }
        return result;
    }


    @Override
    public final SingleByteMatcher getByteMatcherForPosition(final int position) {
        return matcherSequence.get(position);
    }


    @Override
    public final int length() {
        return length;
    }

    
    @Override
    public final String toRegularExpression(final boolean prettyPrint) {
        StringBuilder builder = new StringBuilder();
        for (SingleByteMatcher matcher : matcherSequence) {
            builder.append(matcher.toRegularExpression(prettyPrint));
        }
        return builder.toString();
    }

}
