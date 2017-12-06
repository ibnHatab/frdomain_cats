package frdomain
package algebra.interpreter

import java.util.Date
import org.scalacheck._
import Prop.{ forAll, BooleanOperators }
import Gen._
import Arbitrary.arbitrary
import scala.util.Success

object AllGen {

  import common._

  val genAmount = for {
    value <- Gen.chooseNum(100, 10000000)
    valueDecimal = BigDecimal.valueOf(value.toLong)
  } yield valueDecimal / 100

  val genBalance = genAmount map Balance

  implicit val arbitraryBalance: Arbitrary[Balance] = Arbitrary { genBalance }

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

object AccountSpecification extends Properties("Account") {

  import Account._
  import AccountService._
  import AllGen._

  val validAccountGen = for {
    no <- genValidAccountNo
    nm <- genName
    od <- arbitrary[Date]
    cd <- genOptionalValidCloseDate(od)
    bl <- arbitrary[Balance]
  } yield Some(Account(no, nm, od, cd, bl))

  property("Checking Account creation successful") = forAll(validAccountGen)(! _.isEmpty)

  property("Equal credit & debit in sequence retain the same balance") =
    forAll(validAccountGen, genAmount) { (creation, amount) => {
        creation.map { account =>
          val Success((before, after)) = for {
            b <- balance(account)
            c <- credit(account, amount)
            d <- debit(c, amount)
          } yield (b, d.balance)

          before == after
        }.getOrElse(false)
    } }
}
