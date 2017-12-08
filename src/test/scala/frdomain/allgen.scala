package frdomain

import java.util.Date

import org.scalacheck._
import org.scalacheck.Gen._

object AllGen {

  val genAmount = for {
    value <- Gen.chooseNum(100, 10000000)
    valueDecimal = BigDecimal.valueOf(value.toLong)
  } yield valueDecimal / 100


  val genValidAccountNo = Gen.choose(100000, 999999).map(_.toString)
  val genInvalidAccountNo = Gen.choose(1000, 9999).map(_.toString)

  val genName = Gen.oneOf("john", "david", "mary")

  def genOptionalValidCloseDate(seed: Date) =
    Gen.frequency(
      (8, Some(aDateAfter(seed))),
      (1, None)
    )
  def aDateAfter(date: Date) = new Date(date.getTime() + 10000)
  def aDateBefore(date: Date) = new Date(date.getTime() - 10000)
  def genInvalidOptionalCloseDate(seed: Date) = Gen.oneOf(Some(aDateBefore(seed)), None)
}
