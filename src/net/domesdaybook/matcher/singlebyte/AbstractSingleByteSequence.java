/*
 * Copyright Matt Palmer 2011, All rights reserved.
 * 
 * This code is licensed under a standard 3-clause BSD license:
 *
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
 * 
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 * A simple abstract base class which implements most of the methods required
 * for a {@link SingleByteMatcher} to also behave as a {@link SequenceMatcher}.
 * 
 * @author Matt Palmer
 */
public abstract class AbstractSingleByteSequence implements SingleByteMatcher {
    
    /**
     * {@inheritDoc}
     * 
     * Returns this for position 0, or throws an IndexOutOfBoundsException.
     */
    @Override
    public final SingleByteMatcher getByteMatcherForPosition(final int position) {
        if (position != 0) {
            throw new IndexOutOfBoundsException("SingleByteMatchers only have a matcher at position 0.");
        }
        return this;
    }

    
    /**
     * {@inheritDoc}
     *
     * Always returns 1.
     */ 
    @Override
    public final int length() {
        return 1;
    }
    

    /**
     * {@inheritDoc}
     *
     * Always returns this.
     */ 
    @Override
    public final SequenceMatcher reverse() {
        return this;
    }    
    
    
}
