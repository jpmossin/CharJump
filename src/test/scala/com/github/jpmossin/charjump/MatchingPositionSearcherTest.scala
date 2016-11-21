package com.github.jpmossin.charjump

import org.scalatest.FunSuite

class MatchingPositionSearcherTest extends FunSuite {

  test("All lower case letters are included as single char jumps") {
    val jumpKeys = MatchingPositionSearcher.mapPositionsToJumpKeys(0 to 25)
    assert(jumpKeys.values.toSet.flatten -- ('a' to 'z') == Set.empty)
  }

  test("All generated jump sequences are unique") {
    val positions = 0 to 1000
    val seqs = MatchingPositionSearcher.mapPositionsToJumpKeys(positions).values.toSet
    assert(positions.size == seqs.size)
  }

  test("The first character of jump sequences of different length should be unique") {
    val positions = 0 to 1000
    val seqs = MatchingPositionSearcher.mapPositionsToJumpKeys(positions).values
    val jumpSeqByLength = seqs.groupBy(_.size)
    assert(jumpSeqByLength.size == 3)  // just to make sure we are  actually testing something here.

    // Compute the first char for the sequences of length 1, 2, and 3
    // These sets should all be disjoint with each other.
    val firstChars = jumpSeqByLength.mapValues(seqs => seqs.map(_.head).toSet)
    assert((firstChars(0) intersect firstChars(1)).isEmpty)
    assert((firstChars(0) intersect firstChars(2)).isEmpty)
    assert((firstChars(1) intersect firstChars(2)).isEmpty)
  }

}
