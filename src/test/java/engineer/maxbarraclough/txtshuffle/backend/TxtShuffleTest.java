package engineer.maxbarraclough.txtshuffle.backend;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import engineer.maxbarraclough.txtshuffle.backend.TxtShuffle.NumberTooGreatException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class TxtShuffleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testReversal() {

		final int[] isv = new int[] {3, 8, 5, 9, 4, 7, 6, 0, 2, 1}; // non involutory

		final int[] sv = TxtShuffle.invertIsvOrSv(isv);

		final boolean shouldBeFalse = java.util.Arrays.equals(isv, sv);

		org.junit.Assert.assertFalse(shouldBeFalse);

		final int[] backAgain = TxtShuffle.invertIsvOrSv(sv);

		org.junit.Assert.assertArrayEquals(isv, backAgain);

		// TxtShuffle.applyIsvToStringArr(input, isv)
	}

	@Test
	public final void testFindSortingIsv() throws IOException
	{
		final String[] strs =
                        TxtShuffle.readFileIntoStringArr("src/test/java/engineer/maxbarraclough/txtshuffle/backend/example1.txt");

		final int[] filesSortingIsv = TxtShuffle.findSortingIsv_AVOID(strs);

		org.junit.Assert.assertTrue(VectorConversions.isValidSvOrIsv(filesSortingIsv));

		// // // TODO avoid the needless invert

		final String[] strsSorted = strs.clone();
		java.util.Arrays.sort(strsSorted);

		final boolean shouldBeFalse = java.util.Arrays.equals(strs, strsSorted);
		org.junit.Assert.assertFalse(shouldBeFalse);

		// // // TODO try applySvToStringArr

		final String[] strsAfterSortingOrder =
		  TxtShuffle.applyIsvToStringArr(strs, filesSortingIsv);

		{
			final int[] filesSortingSv = TxtShuffle.findSortingSv(strs);

			org.junit.Assert.assertTrue(VectorConversions.isValidSvOrIsv(filesSortingSv));

			final String[] strsAfterSortingOrder_ =
					  TxtShuffle.applySvToStringArr(strs, filesSortingSv);

			org.junit.Assert.assertArrayEquals(strsAfterSortingOrder, strsAfterSortingOrder_);
		}

		org.junit.Assert.assertArrayEquals(strsSorted, strsAfterSortingOrder);

		{
			final int[] sv = TxtShuffle.invertIsvOrSv(filesSortingIsv);

			// we apply the sv treating it as an ISV, thus reversing the sorting
			final String[] scrambledBack = TxtShuffle.applyIsvToStringArr(strsAfterSortingOrder, sv);
			org.junit.Assert.assertArrayEquals(strs, scrambledBack);
		}
	}


	@Test
	public final void encodeIntoDataAndRetrieve() throws IOException, NumberTooGreatException
	{
		final int secretNum = 19409;
		final BigInteger secretNum_BI = BigInteger.valueOf(secretNum);
		// 40319; // 40320 is fact(8) and is the lowest too-high integer

		final String[] encoded = TxtShuffle.encodeSmallNumberIntoFileData(
                        "src/test/java/engineer/maxbarraclough/txtshuffle/backend/example1.txt",
                        secretNum
                );

		final BigInteger retrievedNum = TxtShuffle.retrieveNumberFromData(encoded);

		org.junit.Assert.assertEquals(secretNum_BI, retrievedNum);
	}




        @Test
        public final void testConversionAndSummation() // i.e. mapping a compact vector to a BigInteger
        {
            final int[] compactVector = new int[] {3,0,1,/*last element must be zero*/0};

            final BigInteger bd1 = VectorConversions.compactVectorToInt_Orig(compactVector);
            final String bd1str = bd1.toString();

            final BigInteger bd2 = VectorConversions.compactVectorToInt_Fast(compactVector);
            final String bs2str = bd2.toString();

            org.junit.Assert.assertEquals(bd1, bd2);
        }





	@Test
	public final void encodeIntoDataAndRetrieve_FineGrain() throws IOException, NumberTooGreatException
	{
		final BigInteger secretNum =
        new BigInteger(
         "123321100012345678998877663335544332231415926535897932384626433287911"
        +"998890055496064220900999744643839384732828372372347749000230696474742"
        +"993259855500111011011100424242000119911919191999447474744242442444222"
        +"998890055496064220900999744643839384732828372372347749000230696474742"
        +"228890055496064220900999744643839384732828372372347749000230696474742"
        +"111190055496064220900999744643839384732828372372347749000230696474111"
        +"118890055496064220900999744643839384732828372372347749000230696474742"
		);
				// BigInteger.valueOf(1233211230696474742L);

		final String[] strs =
                        TxtShuffle.readFileIntoStringArr("src/test/java/engineer/maxbarraclough/txtshuffle/backend/example1.txt");

		final int[] compact =
				VectorConversions.intToCompactVector(
				  strs.length,
				  secretNum
				);

//		final int[] compact = new int[compact_BIs.length];
//		for(int i = 0; i != compact.length; ++i)
//		{
//			compact[i] = compact_BIs[i].intValue();
//		}

		final int[] isvFromCompact = VectorConversions.compactToIsv(compact);

		// final int[] isvForFilesOrder = TxtShuffle.findSortingIsv(strs); // NO! not needed for the encode direction, only for decode!

		final String[] strsSorted = strs.clone();
		java.util.Arrays.sort(strsSorted);

		final String[] strsEncodingNum = TxtShuffle.applyIsvToStringArr(strsSorted, isvFromCompact);

		{
			final boolean shouldBeFalse = java.util.Arrays.equals(strs, strsEncodingNum);
			org.junit.Assert.assertFalse(shouldBeFalse);
		}

		{
			final boolean shouldBeFalse = java.util.Arrays.equals(strsSorted, strsEncodingNum);
			org.junit.Assert.assertFalse(shouldBeFalse);
		}

// Now let's go back and retrieve the number

//		final int[] retrievedSortingIsv = TxtShuffle.findSortingIsv_AVOID(strsEncodingNum);
//		final int[] retrievedUseful = TxtShuffle.invertIsv(retrievedSortingIsv);

		final int[] foundSortingSv = TxtShuffle.findSortingSv(strsEncodingNum);

		org.junit.Assert.assertArrayEquals(foundSortingSv, isvFromCompact);

		final int[] retrievedCompact = VectorConversions.isvToCompact(foundSortingSv);

		org.junit.Assert.assertArrayEquals(compact, retrievedCompact);

		final BigInteger retrievedNum = VectorConversions.compactVectorToInt(retrievedCompact);

		org.junit.Assert.assertEquals(secretNum, retrievedNum);
	}

// https://graphics.stanford.edu/~seander/bithacks.html#IntegerLogDeBruijn
    private static final int[] MAGIC = new int[]{
        0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8,
        31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
    };

    public static int log2of2power(int x) {
        final int index = (x * 0x077CB531) >>> 27;
        final int r = MAGIC[index];
        return r;
    }


        // // TODO relocate, perhaps even to its own test class

	@Test
	public final void encodeChunky() throws IOException, NumberTooGreatException
	{
		final BigInteger secretNum = new BigInteger( "3" );
				// BigInteger.valueOf(1233211230696474742L);

		final String[] strs =
                        TxtShuffle.readFileIntoStringArr("src/test/java/engineer/maxbarraclough/txtshuffle/backend/example2.txt");

		final int[] compact =
				VectorConversions.intToCompactVector(
				  strs.length,
				  secretNum
				);

		final int[] isvFromCompact = VectorConversions.compactToIsv(compact);

		// final int[] isvForFilesOrder = TxtShuffle.findSortingIsv(strs); // NO! not needed for the encode direction, only for decode!

		final String[] strsSorted = strs.clone();
		java.util.Arrays.sort(strsSorted);

		final String[] strsEncodingNum = TxtShuffle.applyIsvToStringArr(strsSorted, isvFromCompact);

		{
			final boolean shouldBeFalse = java.util.Arrays.equals(strs, strsEncodingNum);
			org.junit.Assert.assertFalse(shouldBeFalse);
		}

		{
			final boolean shouldBeFalse = java.util.Arrays.equals(strsSorted, strsEncodingNum);
			org.junit.Assert.assertFalse(shouldBeFalse);
		}

// Now let's go back and retrieve the number

//		final int[] retrievedSortingIsv = TxtShuffle.findSortingIsv_AVOID(strsEncodingNum);
//		final int[] retrievedUseful = TxtShuffle.invertIsv(retrievedSortingIsv);

		final int[] foundSortingSv = TxtShuffle.findSortingSv(strsEncodingNum);

		org.junit.Assert.assertArrayEquals(foundSortingSv, isvFromCompact);

		final int[] retrievedCompact = VectorConversions.isvToCompact(foundSortingSv);

		org.junit.Assert.assertArrayEquals(compact, retrievedCompact);

		final BigInteger retrievedNum = VectorConversions.compactVectorToInt(retrievedCompact);

		org.junit.Assert.assertEquals(secretNum, retrievedNum);

                final byte[] retrievedNumBytes = retrievedNum.toByteArray(); // big endian



                // // Yup, real code in a test. The horror!

//                final ArrayList<Byte> bytesAl = new ArrayList<>(); // big endian

BitSet bs = new BitSet();

                final VectorConversions.MultiplierVal[] mults = VectorConversions.genMultipliersList(isvFromCompact.length);

                int stintStartingIndex = 0;
                for (int i = 0; i != isvFromCompact.length; ++i)
                {
                    final boolean twoPower = mults[i].stepwiseMultIsTwoPower();
                    if (twoPower)
                    {
                        final BigInteger bigSum =
IntStream.range(stintStartingIndex, i+1/*it's exclusive*/).map((int j) -> isvFromCompact[j])
        .mapToObj((int k ) -> BigInteger.valueOf(k))
        .reduce(BigInteger.ZERO, (BigInteger bi, BigInteger bi2) -> bi.add(bi2));

                        final BigInteger dividedOff = bigSum.divide(mults[i].bi);

                        // if the multiplier is 8 that means we .... uh.... THAT TELLS US HOW MANY ARE COMING, NOT HOW MANY TO WRITE!
                        // how many to write DEPENDS, right?
                        final int numBitsToSet = mults[i].bi.getLowestSetBit(); // log2of2power();

//                       final byte[] bytes = dividedOff.toByteArray();

//                        final Byte[] bytesBoxed = new Byte[bytes.length];
//                        for (int d = 0; d != bytes.length; ++d)
//                        {
//                            bytesBoxed[d] = bytes[d];
//                        }
//
//                        bytesAl.addAll(Arrays.asList(bytesBoxed));

//final BigInteger totalSum = multiplied.reduce( BigInteger.ZERO, (BigInteger bi, BigInteger bi2) -> bi.add(bi2) );
                    }
                    else {
                        //
                    }
                }

               final Byte[] retrievedNumBytesBoxed = new Byte[retrievedNumBytes.length];
               for (int e = 0; e != retrievedNumBytes.length; ++e)
               {
                   retrievedNumBytesBoxed[e] = retrievedNumBytes[e];
               }

            final List<Byte> retrievedNumBytesBoxed_List = Arrays.asList(retrievedNumBytesBoxed);
            final boolean equal = bytesAl.equals(retrievedNumBytesBoxed_List);
            org.junit.Assert.assertTrue(equal);
	}



}
