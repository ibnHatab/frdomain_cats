package frdomain
package algebra.interpreter

import java.util.Date

import scala.util.Success

import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.forAll

object AccountSpecification extends Properties("Account") {

  import algebra.{Account, Balance}
  import repository.interpreter.AccountService._
  import frdomain.AllGen._

  val genBalance = genAmount map Balance

  implicit val arbitraryBalance: Arbitrary[Balance] = Arbitrary { genBalance }

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
