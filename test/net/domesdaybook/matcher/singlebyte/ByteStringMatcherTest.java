/* This code tests the byte value sequence matcher class.
 * DO NOT MODIFY THE FIRST LINE OF THIS FILE without also modifying
 * the test byte sequence in testmatchess(), as it uses this source code
 * file to run its byte-matching tests against.
  */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.matcher.sequence.SequenceMatcherParser;
import net.domesdaybook.reader.ByteReader;
import net.domesdaybook.matcher.sequence.ByteSequenceMatcher;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Matt Palmer
 */
public class ByteStringMatcherTest {

    ByteReader bytes;
   

    public ByteStringMatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        // bytes = ???
    }

    @After
    public void tearDown() {
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullParse() {
        SequenceMatcherParser.byteSequenceFromExpression(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyParse() {
        SequenceMatcherParser.byteSequenceFromExpression("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNonHexParse() {
        SequenceMatcherParser.byteSequenceFromExpression("This should fail");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSomeHexParse() {
         SequenceMatcherParser.byteSequenceFromExpression("010203FFEED1This should fail.");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testMissingHexCharParse() {
        SequenceMatcherParser.byteSequenceFromExpression("A1B2C");
    }

    @Test
    public void testCorrectHexCharParse() {
        // The absence of an exception in the Parse means it has parsed the hex sequence.
        // Therefore we don't have an assertion in this test case.
        SequenceMatcherParser.byteSequenceFromExpression("Fd"); // test a single byte with mixed hex case.
        SequenceMatcherParser.byteSequenceFromExpression("000102A1b3Cde4Fe"); // test a long sequence with mixed hex case.
    }

    @Test
    public void testLength() {
        ByteSequenceMatcher instance;
        int expResult;

        instance = SequenceMatcherParser.byteSequenceFromExpression("01");
        assertEquals("Test length one byte.", 1, instance.length());

        instance = SequenceMatcherParser.byteSequenceFromExpression("01FF");
        assertEquals("Test length two bytes.", 2, instance.length());

        instance = SequenceMatcherParser.byteSequenceFromExpression("01FF3d4f728912");
        assertEquals("Test length seven bytes.", 7, instance.length());

        instance = SequenceMatcherParser.byteSequenceFromExpression("01FF3d01FF3d4f7289124f72891201FF3d4f728912");
        assertEquals("Test length twenty one bytes.", 21, instance.length());
    }

    /**
     * Test of matchess method, of class ByteValueSequenceMatcher.
     */
    @Test
    public void testmatchess() {
        ByteSequenceMatcher instance;
        long matchFrom;
        boolean result;
        boolean expResult;

        // Test that we can identify the beginning of this file:
        String fileStartHexBytes = "2f2a205468697320636f6465"; // "/* This code"
        instance = SequenceMatcherParser.byteSequenceFromExpression(fileStartHexBytes);
        matchFrom = 0L;
        expResult = true; // number of bytes in fileStartHexBytes.
        result = instance.matches(bytes, matchFrom);
        assertEquals("Test for a match at the start of the file.", expResult, result);

        // Test that we don't identify this file when starting from 1 byte in:
        matchFrom = 1L;
        expResult = false;
        result = instance.matches(bytes, matchFrom);
        assertEquals("Test for a failed match 1 byte from the start of the file.", expResult, result );

        fileStartHexBytes = "2f2a205468697320636f646500"; // "/* This code" plus a zero byte
        instance = SequenceMatcherParser.byteSequenceFromExpression(fileStartHexBytes);
        // Test that we don't identify this file when starting from
        matchFrom = 0L;
        expResult = false;
        result = instance.matches(bytes, matchFrom);
        assertEquals("Test for a failed match with a zero byte on the end.", expResult, result );

        //TODO: need to test for bytes in the 128-255 range, as bytes are actually
        //      signed 8-bit values (so they actually range from -128 to 127.

        // May need to store the byte values in 2s complement form.

    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testErrorOnmatchessOutsideFile() {
        final ByteSequenceMatcher instance = SequenceMatcherParser.byteSequenceFromExpression("010203");
        instance.matches(bytes, 100000000L);
    }

}
