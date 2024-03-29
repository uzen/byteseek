/*
 * Copyright Matt Palmer 2013, All rights reserved.
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

package net.byteseek.object;

import java.util.Collection;

public final class ArgUtils {

	private static final String OBJECT_CANNOT_BE_NULL               = "The object cannot be null";
	private static final String COLLECTION_CANNOT_BE_NULL           = "The collection cannot be null";
	private static final String COLLECTION_MUST_BE_RIGHT_SIZE       = "The collection of size %d must be exactly %d in size";
	private static final String COLLECTION_CANNOT_BE_EMPTY          = "The collection cannot be empty";
	private static final String COLLECTION_ELEMENT_CANNOT_BE_NULL   = "Elements cannot be null";
	private static final String ARRAY_CANNOT_BE_NULL                = "The array cannot be null";
	private static final String ARRAY_CANNOT_BE_EMPTY		        = "The array cannot be empty";
	private static final String STRING_CANNOT_BE_NULL               = "The string cannot be null";
	private static final String STRING_CANNOT_BE_EMPTY              = "The string cannot be empty";
    private static final String END_PAST_LENGTH_ERROR               = "The end %d is past the end, length = %d";
    private static final String START_LESS_THAN_ZERO_ERROR          = "The start %d is less than zero.";
    private static final String START_PAST_END_INDEX_ERROR          = "The start index %d is equal to or greater than the end index %d";
    private static final String START_PAST_LENGTH_ERROR             = "Start position %d is past the end, length = %d.";
    private static final String POSITIVE_INTEGER				    = "The number %d should be a positive integer.";

	public static void checkNullObject(final Object object) {
		if (object == null) {
			throw new IllegalArgumentException(OBJECT_CANNOT_BE_NULL);
		}
	}

	public static void checkNullObject(final Object object, final String description) {
		if (object == null) {
			throw new IllegalArgumentException(OBJECT_CANNOT_BE_NULL + ' ' + description);
		}
	}
	
	public static <T> void checkNullCollection(final Collection<T> collection) {
		if (collection == null) {
    		throw new IllegalArgumentException(COLLECTION_CANNOT_BE_NULL);
    	}
	}

	
	public static <T> void checkNullCollection(final Collection<T> collection, final String description) {
		if (collection == null) {
    		throw new IllegalArgumentException(COLLECTION_CANNOT_BE_NULL + ' ' + description);
    	}
	}
 
	
	public static <T> void checkNullOrEmptyCollection(final Collection<T> collection) {
		checkNullCollection(collection);
		if (collection.isEmpty()) {
			throw new IllegalArgumentException(COLLECTION_CANNOT_BE_EMPTY);
		}
	}
	
	
	public static <T> void checkCollectionSize(final Collection<T> collection, final int size) {
		checkNullCollection(collection);
		if (collection.size() != size) {
			throw new IllegalArgumentException(String.format(COLLECTION_MUST_BE_RIGHT_SIZE, collection.size(), size));
		}
	}
	
	
	public static <T> void checkCollectionSizeNoNullElements(final Collection<T> collection, final int size) {
		checkCollectionSize(collection, size);
		for (T element : collection) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL);
		}
	}
	

	public static <T> void checkNullOrEmptyCollection(final Collection<T> collection, final String description) {
		checkNullCollection(collection, description);
		if (collection.isEmpty()) {
			throw new IllegalArgumentException(COLLECTION_CANNOT_BE_EMPTY + ' ' + description);
		}
	}
	
	
	public static <T> void checkNullCollectionElements(final Collection<T> collection) {
		checkNullCollection(collection);
		for (T element : collection) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL);
		}
	}
	
	public static <T> void checkNullCollectionElements(final Collection<T> collection, final String description) {
		checkNullCollection(collection, description);
		for (T element : collection) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL + ' ' + description);
		}
	}
	
	public static <T> void checkNullOrEmptyCollectionNoNullElements(final Collection<T> collection) {
		checkNullOrEmptyCollection(collection);
		for (T element : collection) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL);
		}
	}
	
	public static <T> void checkNullOrEmptyCollectionNoNullElements(final Collection<T> collection, final String description) {
		checkNullOrEmptyCollection(collection, description);
		for (T element : collection) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL + ' ' + description);
		}
	}

	
	public static void checkNullByteArray(final byte[] bytes) {
		if (bytes == null) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_NULL);
    	}
	}    	
	

	public static void checkNullByteArray(final byte[] bytes, final String description) {
		if (bytes == null) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_NULL + ' ' + description);
    	}
	}  

	
	public static void checkNullOrEmptyByteArray(final byte[] bytes) {
		checkNullByteArray(bytes);
		if (bytes.length == 0) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_EMPTY);
    	}
	}    	

	
	public static void checkNullOrEmptyByteArray(final byte[] bytes, final String description) {
		checkNullByteArray(bytes);
		if (bytes.length == 0) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_EMPTY + ' ' + description);
    	}
	}  
	
	
	public static <T> void checkNullArray(final T[] array) {
		if (array == null) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_NULL);
    	}
	}    	
	

	public static <T> void checkNullArray(final T[] array, final String description) {
		if (array == null) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_NULL + ' ' + description);
    	}
	}  

	
	public static <T> void checkNullOrEmptyArray(final T[] array) {
		checkNullArray(array);
		if (array.length == 0) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_EMPTY);
    	}
	}    	
	
	public static <T> void checkNullOrEmptyArray(final T[] array, final String description) {
		checkNullArray(array, description);
		if (array.length == 0) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_EMPTY + ' ' + description);
    	}
	}  

	
	public static <T> void checkNullOrEmptyArrayNoNullElements(final T[] array, final String description) {
		checkNullOrEmptyArray(array, description);
		for (T element : array) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL + ' ' + description);
		}
	}  
	

	public static <T> void checkNullOrEmptyArrayNoNullElements(final T[] array) {
		checkNullOrEmptyArray(array);
		for (T element : array) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL);
		}
	}    	

	


	public static void checkNullString(final String string) {
		if (string == null) {
    		throw new IllegalArgumentException(STRING_CANNOT_BE_NULL);
    	}
	} 
	
	public static void checkNullString(final String string, final String description) {
		if (string == null) {
    		throw new IllegalArgumentException(STRING_CANNOT_BE_NULL + ' ' + description);
    	}
	} 

	public static void checkNullOrEmptyString(final String string) {
		if (string == null) {
    		throw new IllegalArgumentException(STRING_CANNOT_BE_NULL);
    	}
		if (string.isEmpty()) {
			throw new IllegalArgumentException(STRING_CANNOT_BE_EMPTY);
		}
	} 
	
	public static void checkNullOrEmptyString(final String string, final String description) {
		if (string == null) {
    		throw new IllegalArgumentException(STRING_CANNOT_BE_NULL + ' ' + description);
    	}
		if (string.isEmpty()) {
			throw new IllegalArgumentException(STRING_CANNOT_BE_EMPTY + ' ' + description);
		}
	} 
	
	
	public static void checkIndexOutOfBounds(final int length, final int position) {
        if (position < 0) {
        	throw new IndexOutOfBoundsException(String.format(START_LESS_THAN_ZERO_ERROR, position));
        }
        if (position >= length) {
            throw new IndexOutOfBoundsException(String.format(START_PAST_LENGTH_ERROR, position, length));
        }
	}
	
	
	public static void checkIndexOutOfBounds(final int length, final int startIndex, final int endIndex) {
        if (startIndex < 0) {
        	throw new IndexOutOfBoundsException(String.format(START_LESS_THAN_ZERO_ERROR, startIndex));
        }
		if (startIndex >= endIndex) {
            throw new IndexOutOfBoundsException(String.format(START_PAST_END_INDEX_ERROR, startIndex, endIndex));
        }
        if (startIndex >= length) {
            throw new IndexOutOfBoundsException(String.format(START_PAST_LENGTH_ERROR, startIndex, length));
        }
        if (endIndex > length) {
            throw new IndexOutOfBoundsException(String.format(END_PAST_LENGTH_ERROR, endIndex, length));
        }
	}
	
	public static void checkPositiveInteger(final int number) {
		if (number < 1) {
			throw new IllegalArgumentException("The number " + number + " should be a positive integer.");
		}
	}
	
	public static void checkPositiveInteger(final int number, final String description) {
		if (number < 1) {
			throw new IllegalArgumentException(String.format(POSITIVE_INTEGER + ' ' + description, number));		}
	}
	
}
