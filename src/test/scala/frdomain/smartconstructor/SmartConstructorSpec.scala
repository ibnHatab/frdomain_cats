package frdomain
package smartconstructor

import java.util.Date

import scala.util.Success

import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.forAll


object  SmartConstructorSpecification extends Properties("SmartConstructor") {

  import Account._
  import frdomain.AllGen._

  val genBalance = genAmount map Balance

  implicit val arbitraryBalance: Arbitrary[Balance] = Arbitrary { genBalance }

  val validCheckingAccountGen = for {
    no <- genValidAccountNo
    nm <- genName
    od <- arbitrary[Date]
    cd <- genOptionalValidCloseDate(od)
    bl <- arbitrary[Balance]
  } yield checkingAccount(no, nm, Some(od), cd, bl)

  val validSavingsAccountGen = for {
    no <- genValidAccountNo
    nm <- genName
    rt <- Gen.choose(5, 10)
    od <- arbitrary[Date]
    cd <- genOptionalValidCloseDate(od)
    bl <- arbitrary[Balance]
  } yield savingsAccount(no, nm, rt, Some(od), cd, bl)

  val invalidCheckingAccountGen = for {
    no <- genInvalidAccountNo
    nm <- genName
    od <- arbitrary[Date]
    cd <- genInvalidOptionalCloseDate(od)
    bl <- arbitrary[Balance]
  } yield checkingAccount(no, nm, Some(od), cd, bl)

  val validClosedCheckingAccountGen = for {
    no <- genValidAccountNo
    nm <- genName
    od <- arbitrary[Date]
    cd <- genOptionalValidCloseDate(od) suchThat (_ isDefined)
    bl <- arbitrary[Balance]
  } yield checkingAccount(no, nm, Some(od), cd, bl)

  val validZeroBalanceCheckingAccountGen = for {
    no <- genValidAccountNo
    nm <- genName
    od <- arbitrary[Date]
  } yield checkingAccount(no, nm, Some(od), None, Balance(0))

  property("Checking Account creation successful") = forAll(validCheckingAccountGen)(_.isSuccess)

  property("Savings Account creation successful") = forAll(validSavingsAccountGen)(_.isSuccess)

}
