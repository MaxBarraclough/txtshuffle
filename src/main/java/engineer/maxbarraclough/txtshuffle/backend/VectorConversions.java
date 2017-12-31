package engineer.maxbarraclough.txtshuffle.backend;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import engineer.maxbarraclough.txtshuffle.backend.TxtShuffle.NumberTooGreatException;

// TODO no-allocate versions of the methods?


// TODO think about scaling the algorithm. Mult. is O(n^2). Perhaps break up the problem
// and deal with the order of sub-sequences, and also with their ordering, etc, in a 'tiered' way...
// or something similar.
// Or is the mult. not so bad, as the smaller of the two operands is bounded by n (the row count)?


// TODO eliminate pointless intermediate ArrayLists and boxing


public final class VectorConversions {

	/**
	 * Not to be instantiated
	 */
	private VectorConversions() {}


	/**
	 * Check that each element is in the (0..n) interval,
	 * and is unique within the array.
	 * @param vec
	 * @return
	 */
	public static boolean isValidSvOrIsv(final int[] vec)
	{
		boolean ret = true;

		final int[] copy = new int[vec.length];

		// Time complexity might be better if we used a hash-set,
		// (check interval each time, and each time check that
		// the value isn't already in the set before adding)
		// but this will do fine, especially for small vectors.
		// (Assuming O(n log(n)) sort.)
		System.arraycopy(vec, 0, copy, 0, vec.length);

		java.util.Arrays.sort(copy);

		for (int i = 0; i != copy.length; ++i)
		{
			if (copy[i] != i)
			{
				ret = false;
				break;
			}
		}

		return ret;
	}

	// And now the same again, line for line, but with Integer[]

	public static boolean isValidCompactVector(final List<Integer> vec)
	{
		// vec = java.util.Collections.unmodifiableList(vec); // makes no difference (other than giving us some assurances)

		boolean ret = true;

		final int outSz = vec.size();

		for (int i = 0; i != outSz; ++i) {
			final int got = vec.get(i);
			final int maxAllowed = outSz - (i + 1);

			// assert(got <= maxAllowed);
			if ((got > maxAllowed) || (got < 0)) {
				ret = false;
				break;
			}
		}

		return ret;
	}


	public static boolean isValidCompactVector(final int[] vec)
	{
		boolean ret = true;

		final int outSz = vec.length;

		for (int i = 0; i != outSz; ++i) {
			final int got = vec[i];
			final int maxAllowed = outSz - (i + 1);

			// assert(got <= maxAllowed);
			if ((got > maxAllowed) || (got < 0)) {
				ret = false;
				break;
			}
		}

		return ret;
	}



	/***
	 *
	 * @param extent
	 * Vector size
	 * @param secretNum
	 * @return
	 * @throws NumberTooGreatException
	 */
	public static int[] intToCompactVector_Slow(final int extent, final BigInteger secretNum)
			throws NumberTooGreatException
	{
		TxtShuffle.throwNtgeIfTooGreat(extent, secretNum); // Ensure we have enough bits to play with

		final BigInteger extent_BI = BigInteger.valueOf(extent);

		BigInteger acc = secretNum;

		BigInteger card = BigInteger.ONE; // int card = 1;
		// int would probably be fine but we'd end up converting to BigInteger anyway

		// build up this AL 'backwards' then reverse as we copy across to an array
		final ArrayList<Integer> al = new ArrayList<Integer>(extent);

		for (int i = 0; i != extent; ++i)
		{
			// assert(card <= extent);
			assert( card.compareTo(extent_BI) <= 0 );
			// assert(card >= 0);
			assert( card.compareTo(BigInteger.ZERO) >= 0 );
			assert(  acc.compareTo(BigInteger.ZERO) >= 0 ); // assert(acc >= 0);

			final BigInteger temp = acc.mod( card ); // final int temp = acc % card;
			// first time round we do x%1 (yielding zero, of course), which is fine



			al.add(temp.intValue()); // TODO proper check on this conversion

			acc = acc.subtract(temp); // acc -= temp;

			assert(acc.compareTo(BigInteger.ZERO) >= 0); // assert(acc >= 0);

			acc = acc.divide(card); // acc /= card; // first time round, divides by 1, which is fine
			card = card.add(BigInteger.ONE); // ++card;
		}

		// reverse the order as we return

		final int[] ret = new int[al.size()];
		final int lastIndex = ret.length - 1;

		for (int i = 0; i != ret.length; ++i)
		{
			int oppositeEnd = lastIndex - i;
			ret[i] = al.get(oppositeEnd);
		}

		return ret;
	}


// TODO compact vector will be fine with int[] not BigInteger[]
	public static int[] intToCompactVector(final int extent, final BigInteger secretNum)
			throws NumberTooGreatException
	{
		TxtShuffle.throwNtgeIfTooGreat(extent, secretNum); // Ensure we have enough bits to play with

		final BigInteger extent_BI = BigInteger.valueOf(extent);

		BigInteger acc = secretNum;

		BigInteger card = BigInteger.ONE; // int card = 1;
		// int would probably be fine but we'd end up converting to BigInteger anyway

		final int[] ret = new int[extent];

		for (int ii = extent - 1; ii >= 0; --ii)
		{
			assert( card.compareTo(extent_BI) <= 0 ); // assert(card <= extent);
			assert( card.compareTo(BigInteger.ZERO) >= 0 ); // assert(card >= 0);
			assert(  acc.compareTo(BigInteger.ZERO) >= 0 ); // assert(acc >= 0);

			final BigInteger temp = acc.mod( card ); // final int temp = acc % card;
			// first time round we do x%1 (yielding zero, of course), which is fine

			ret[ii] = temp.intValue(); // TODO proper check on this conversion
			// al.add(temp);

			acc = acc.subtract(temp); // acc -= temp;

			assert(acc.compareTo(BigInteger.ZERO) >= 0); // assert(acc >= 0);

			acc = acc.divide(card); // acc /= card; // first time round, divides by 1, which is fine
			card = card.add(BigInteger.ONE); // ++card;
		}

		assert(java.util.Arrays.equals(ret, intToCompactVector_Slow(extent, secretNum)));

		return ret;
	}






	/**
	 *
	 * @param compactVector
	 * Must *not* omit the final element, i.e. final element must be 0
	 *
	 * @return The corresponding 'permutation index', as in
	 * http://www.geekviewpoint.com/java/numbers/permutation_index
	 * TODO equivalent to http://mathworld.wolfram.com/PermutationIndex.html ???
	 */
	public static BigInteger compactVectorToInt(final int[] compactVector)
	{
		assert(0 == compactVector[compactVector.length - 1]); // TODO should conditionally throw?

		assert(isValidCompactVector(compactVector));

		final int stopBefore = compactVector.length - 1;
		// omit last element in the compact vector, by stopping *before* the last element's index

		// int acc = 0;
		BigInteger acc = BigInteger.ZERO;

		BigInteger card = BigInteger.valueOf(compactVector.length);
		// int card = compactVector.length;
		// cardinality of the set of 'options' is initially the length of the vector
                // Remember each interval starts at zero.

		for (int i = 0; i != stopBefore; ++i)
		{
			// acc *= card; // First iteration, just multiplies zero. That's fine.

			acc = acc.multiply(card);

			// acc += compactVector[i];
			final BigInteger toAdd = BigInteger.valueOf(compactVector[i]);
			acc = acc.add( toAdd );

			// --card;
			card = card.subtract(BigInteger.ONE);
		}
		return acc;
	}



	// TODO avoid linear-time horrors in this one, using AVL tree list

	public static int[] isvToCompact(final int[] isv)
	{
		assert(isValidSvOrIsv(isv)); // TODO should conditionally throw?

		final int sz = isv.length;

		// TODO this is a disastrous choice of data structure!
		// An 'AVL tree list' would make more sense.
		final ArrayList<Integer> workingVec = new ArrayList<Integer>(sz);

		for (int i = 0; i != sz; ++i)
		{
			workingVec.add(i);
		}

		final ArrayList<Integer> outputVec = new ArrayList<Integer>(sz);

		for (int i = 0; i != sz; ++i)
		{
			final int searchingFor = isv[i];
			final int indexIntoWorkingVec = workingVec.indexOf(searchingFor);

			assert(indexIntoWorkingVec >= 0);

			outputVec.add(indexIntoWorkingVec);
			workingVec.remove(indexIntoWorkingVec);
		}

		assert( workingVec.isEmpty() );
		assert( isValidCompactVector(outputVec) );

		// Clumsily convert from List<Integer> to int[]

		final int outSz = outputVec.size();
		final int[] outArr = new int[outSz];
		for(int i = 0; i != outSz; ++i)
		{
			outArr[i] = outputVec.get(i);
		}

		assert(isValidCompactVector(outArr));

		assert(java.util.Arrays.equals(
				outArr,
				isvToCompact_Fast(isv)
		       ));


		return outArr;
	}


	public static int[] isvToCompact_Fast(final int[] isv)
	{
		assert(isValidSvOrIsv(isv)); // TODO should conditionally throw?

		final int sz = isv.length;

		// TODO this is a disastrous choice of data structure!
		// An 'AVL tree list' would make more sense.
		final ArrayList<Integer> workingVec = new ArrayList<Integer>(sz);

		for (int i = 0; i != sz; ++i)
		{
			workingVec.add(i);
		}

		final int[] outArr = new int[sz];

		for (int i = 0; i != sz; ++i)
		{
			final int searchingFor = isv[i];
			final int indexIntoWorkingVec = workingVec.indexOf(searchingFor);

			assert(indexIntoWorkingVec >= 0);

			// outputVector.add(indexIntoWorkingVec);
			outArr[i] = indexIntoWorkingVec;

			workingVec.remove(indexIntoWorkingVec);
		}

		assert( workingVec.isEmpty() );
		assert( isValidCompactVector(outArr) );

		return outArr;
	}






	public static int[] compactToIsv(int[] compactVector)
	{
		assert(isValidCompactVector(compactVector));

		// First set up the 'vector augmentation' full of the indices
		// of the real data in their array
		final int sz = compactVector.length;

		// TODO this is a disastrous choice of data structure!
		// An 'AVL tree list' would make more sense.
		final ArrayList<Integer> workingVec = new ArrayList<Integer>(sz);

		for (int i = 0; i != sz; ++i)
		{
			workingVec.add(i);
		}

		final ArrayList<Integer> outputVector = new ArrayList<Integer>(sz);


		for (int indexIntoCompactVec = 0; indexIntoCompactVec != sz; ++indexIntoCompactVec)
		{
			final int indexIntoWorkingVec = compactVector[indexIntoCompactVec];
			final int val = workingVec.get(indexIntoWorkingVec);
			outputVector.add(val);
			workingVec.remove(indexIntoWorkingVec);
		}

		assert(workingVec.isEmpty());

		// Clumsily convert from List<Integer> to int[]

		final int outSz = outputVector.size();
		final int[] outArr = new int[outSz];
		for(int i = 0; i != outSz; ++i)
		{
			outArr[i] = outputVector.get(i);
		}

		assert(isValidSvOrIsv(outArr));

		return outArr;
	}


}
