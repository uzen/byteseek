/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 * http://www.opensource.org/licenses/BSD-3-Clause
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
 *  * Neither the "byteseek" name nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission. 
 *  
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

package net.domesdaybook.reader;

/**
 *
 * @author matt
 */
public final class Window {
    
    private final byte[] bytes;
    private final long windowPosition;
    private final int limit;
    
    /**
     * Constructs a Window using the byte array provided.
=    * 
     * @param bytes  The byte array to wrap.
     * @param windowPosition The position at which the Window starts.
     * @param offset A starting position of a slice of the array.
     * @param limit  An ending position of a slice of the array.
     */
    public Window(final byte[] bytes, final long windowPosition, final int limit) {
        if (bytes == null) {
            throw new IllegalArgumentException("Null byte array passed in to Array.");
        }
        this.bytes = bytes;  
        this.windowPosition = windowPosition;
        this.limit = limit;
    }
    
    
    
    public byte getByte(final int position) {
        return bytes[position];
    }

    
    public byte[] getArray() {
        return bytes;
    }
    
    
    public long getWindowPosition() {
        return windowPosition;
    }
    
    
    public int getLimit() {
        return limit;
    }
}
