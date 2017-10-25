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

		final String[] encoded = TxtShuffle.encodeSmallNumberIntoData(
                        "src/test/java/engineer/maxbarraclough/txtshuffle/backend/example1.txt",
                        secretNum
                );

		final BigInteger retrievedNum = TxtShuffle.retrieveNumberFromData(encoded);

		org.junit.Assert.assertEquals(secretNum_BI, retrievedNum);
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

		// TODO ugly conversion business again ///////////////////////////////
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

		//////////////////////////////////////////////////////////////////////

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

}
