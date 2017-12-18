package frdomain
package algebra.interpreter

import frdomain.repository.AccountRepositoryInMemory
import java.util.Date

import scala.util.Success

import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.forAll

object AccountSpecification extends Properties("Account") {

  import frdomain.algebra._
  import frdomain.repository.reader.AccountService._
  import frdomain.smartconstructor.Account._
  import frdomain.AllGen._

  val genBalance = genAmount map Balance

  implicit val arbitraryBalance: Arbitrary[Balance] = Arbitrary { genBalance }

  val validCheckingAccountGen = for {
    no <- genValidAccountNo
    nm <- genName
    od <- arbitrary[Date]
    cd <- genOptionalValidCloseDate(od)
    bl <- arbitrary[Balance]
  } yield checkingAccount(no, nm, od, cd, bl)

  val invalidCheckingAccountGen = for {
    no <- genInvalidAccountNo
    nm <- genName
    od <- arbitrary[Date]
    cd <- genInvalidOptionalCloseDate(od)
    bl <- arbitrary[Balance]
  } yield checkingAccount(no, nm, od, cd, bl)

  property("Checking Account creation successful") = forAll(validCheckingAccountGen)(_.isSuccess)


  property("Equal credit & debit in sequence retain the same balance") =
    forAll(validCheckingAccountGen, genAmount) { (creation, amount) => {
      val r = AccountRepositoryInMemory
      creation.map { account =>
          r.store(account)

          val ss = for {
            b <- balance(account.no)
            c <- credit(account.no, amount)
            d <- debit(c.get.no, amount)
          } yield (b, d.get.balance)

          val (before, after) =  ss(r)

          before == after
        }.getOrElse(false)
    } }
}
