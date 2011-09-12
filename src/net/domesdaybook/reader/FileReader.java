/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A class which reads a random access file into cached byte arrays.
 * 
 * This class (like the underlying RandomAccessFile) is not thread-safe.
 * 
 * @author matt
 */
public final class FileReader implements Reader, Iterable<Window> {

    private final static String READ_ONLY = "r";
    private final static String NULL_ARGUMENTS = "Null file passed to FileReader";
    private final static boolean TEMP_FILE = true;
    private final static boolean NOT_TEMP = false;

    private final static int DEFAULT_ARRAY_SIZE = 4096;
    private final static int DEFAULT_CAPACITY = 8;
    
    private final int arraySize;
    
    private final File file;
    private final RandomAccessFile randomAccessFile;
    private final long length;
    private final WindowCache cache;
    private final boolean fileIsTemporary;

    /**
     * Constructs a FileReader which defaults to an array size of 4096,
     * caching the last 3 most recently used Windows.
     * 
     * @param file The file to read from.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file) throws FileNotFoundException {
        this(file, DEFAULT_ARRAY_SIZE, new WindowCacheMostRecentlyUsed(DEFAULT_CAPACITY), NOT_TEMP);
    }
    

    /**
     * Constructs a FileReader which defaults to an array size of 4096
     * using the WindowCache passed in to cache ArrayWindows.
     * 
     * @param file The file to read from.
     * @param cache the cache of Windows to use.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file, final WindowCache cache) throws FileNotFoundException {
        this(file, DEFAULT_ARRAY_SIZE, cache, NOT_TEMP);
    }     
    
    
    /**
     * Constructs a FileReader using the array size passed in, and caches the
     * last Window 
     * 
     * @param file The file to read from.
     * @param arraySize the size of the byte array to read from the file.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file, final int arraySize) throws FileNotFoundException {
        this(file, arraySize,
             new WindowCacheMostRecentlyUsed(DEFAULT_CAPACITY), NOT_TEMP);
    }    
    
    
    /**
     * Constructs a FileReader using the array size passed in, and caches the
     * last Window 
     * 
     * @param file The file to read from.
     * @param arraySize the size of the byte array to read from the file.
     * @param capacity the number of byte arrays to cache (using a most recently used strategy).
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file, final int arraySize, final int capacity) throws FileNotFoundException {
        this(file, arraySize, 
             new WindowCacheMostRecentlyUsed(capacity), NOT_TEMP);
    }    
    

    /**
     * Constructs a FileReader which defaults to an array size of 4096,
     * caching the last 3 most recently used Windows.
     * 
     * @param file The file to read from.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final InputStream in) throws FileNotFoundException, IOException {
        this(ReadUtils.createTempFile(in), DEFAULT_ARRAY_SIZE, 
             new WindowCacheMostRecentlyUsed(DEFAULT_CAPACITY), TEMP_FILE);
    }
    

    /**
     * Constructs a FileReader which defaults to an array size of 4096
     * using the WindowCache passed in to cache ArrayWindows.
     * 
     * @param file The file to read from.
     * @param cache the cache of Windows to use.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final InputStream in, final WindowCache cache) throws FileNotFoundException, IOException {
        this(ReadUtils.createTempFile(in), DEFAULT_ARRAY_SIZE, cache, TEMP_FILE);
    }     
    
    
    /**
     * Constructs a FileReader using the array size passed in, and caches the
     * last Window 
     * 
     * @param file The file to read from.
     * @param arraySize the size of the byte array to read from the file.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final InputStream in, final int arraySize) throws FileNotFoundException, IOException {
        this(ReadUtils.createTempFile(in), arraySize, 
             new WindowCacheMostRecentlyUsed(DEFAULT_CAPACITY), TEMP_FILE);
    }    
    
    
    /**
     * Constructs a FileReader using the array size passed in, and caches the
     * last Window 
     * 
     * @param file The file to read from.
     * @param arraySize the size of the byte array to read from the file.
     * @param capacity the number of byte arrays to cache (using a most recently used strategy).
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final InputStream in, final int arraySize, final int capacity) throws FileNotFoundException, IOException {
        this(ReadUtils.createTempFile(in), arraySize, 
             new WindowCacheMostRecentlyUsed(capacity), TEMP_FILE);
    }        
    
    
    /**
     * Constructs a FileReader which reads the file into arrays of
     * the specified size.
     *
     * @param file The file to read from.
     * @param arraySize the size of the byte array to read from the file.
     * @param cache the cache of Windows to use.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file, final int arraySize, 
                      final WindowCache cache, final boolean fileIsTemporary) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException(NULL_ARGUMENTS);
        }
        this.file = file;
        this.randomAccessFile = new RandomAccessFile(file, READ_ONLY);
        this.length = file.length();
        this.arraySize = arraySize;
        this.cache = cache;
        this.fileIsTemporary = fileIsTemporary;
    }    


   
    /**
     * 
     * @return The length of the file accessed by the reader.
     */
    @Override
    public long length(){
        return length;
    }

    
    
    /**
     * Reads a byte in the file at the given position.
     *
     * @param position The position in the file to read a byte from.
     * @return The byte at the given position.
     * @throws ReaderException if an IOException occurs reading the file.
     */
    @Override
    public byte readByte(final long position) throws ReaderException {
        final Window window = getWindow(position);
        if (window == null) {
            throw new ReaderException("No bytes can be read from this position:" + position);
        }
        return window.getByte((int) (position % arraySize));
    }
    
    
    /**
     * 
     * @return An Window containing a byte array and the offset into it for a given position.
     *         If an arrayWindow can't be provided for the given position, null is returned.
     */
    @Override
    public Window getWindow(final long position) throws ReaderException {
        if (position >= 0 && position < length) {
            final int blockSize = arraySize;
            final long readPos = (position / blockSize) * blockSize;
            Window window = cache.getWindow(readPos);
            if (window == null) {
                window = createWindow(readPos);
                cache.addWindow(window);
            }
            return window;
        }
        return null;
    }
    
    
    private Window createWindow(final long readPos) throws ReaderException {
        // If the remaining length is smaller than the block size,
        int blockSize = arraySize;
        if (readPos + blockSize > length) {
            blockSize = (int) (length - readPos); // cut down the blocksize.
        } 

        // Read the bytes for this position and create the window for it:
        final byte[] bytes = new byte[blockSize];
        try {
            randomAccessFile.seek(readPos);
            final int totalRead = ReadUtils.readBytes(randomAccessFile, bytes);
            return new Window(bytes, readPos, totalRead);
        } catch (IOException ex) {
            throw new ReaderException(ex);
        }
    }
    

    @Override
    public Iterator<Window> iterator() {
        return new FileWindowIterator();
    }

    
    @Override
    public void close() {
        cache.clear();        
        try {
            randomAccessFile.close();
            if (fileIsTemporary) {
                file.delete();
            }
        } catch (final IOException canDoNothing) {
        }
    }
    
    
    @Override
    public void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    
    @Override
    public void clearCache() {
        cache.clear();
    }
    
    
    private class FileWindowIterator implements Iterator<Window> {

        private int position = 0;
        
        @Override
        public boolean hasNext() {
            return position < length;
        }

        @Override
        public Window next() {
            final Window window = getWindow(position);
            if (window == null) {
                throw new NoSuchElementException();
            }
            position += arraySize;
            return window;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove Arrays from the FileArrayIterator.");
        }
    }
    
}
