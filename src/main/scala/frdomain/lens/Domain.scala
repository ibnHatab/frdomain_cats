package frdomain
package lens

import monocle.Lens
import monocle.macros.{ GenLens, Lenses }
object EmployeeEx {

  case class Street(number: Int, name: String)
  case class Address(city: String, street: Street)
  case class Company(name: String, address: Address)
  case class Employee(name: String, company: Company)

  object EmployeeLens {
    val company   : Lens[Employee, Company] = GenLens[Employee](_.company)
    val address   : Lens[Company , Address] = GenLens[Company](_.address)
    val street    : Lens[Address , Street]  = GenLens[Address](_.street)
    val streetName: Lens[Street  , String]  = GenLens[Street](_.name)
  }

  import EmployeeLens._

  val employee = Employee("john", Company("awesome inc", Address("london", Street(23, "high street"))))

  (company composeLens address
    composeLens street
    composeLens streetName).modify(_.capitalize)(employee)

  import monocle.macros.syntax.lens._
  import monocle.function.Cons.headOption

  employee.lens(_.company.address.street.name).composeOptional(headOption).modify(_.toUpper)
}

trait Organism { def legs: Int }
// monocle @Lenses uses a macro to generate lenses
@Lenses case class Octopus(override val legs: Int, weight: Double)
  extends Organism
@Lenses case class Frog(val legs: Int, color: Int) extends Organism

object Organism {

  def clone[O <: Organism](o: O, legsLens: Lens[O, Int]): O =
    legsLens.set(-1)(o)

  val myOctopus = Octopus(8, 2.4)
  val myFrog = Frog(2, 4)

// use the generated Lenses
  val cloneOctopus: Octopus = clone(myOctopus, Octopus.legs)
  clone(myFrog, Frog.legs)
}


import java.util.Date

import frdomain.common._

case class Balance(amount: Amount = 0)

trait Account {
  def no: String
  def name: String
  def dateOfOpening: Date
  def dateOfClosing: Option[Date]
  def balance: Balance
}

final case class CheckingAccount private (no: String, name: String,
  dateOfOpening: Date, dateOfClosing: Option[Date] = None, balance: Balance = Balance()) extends Account

final case class SavingsAccount private (no: String, name: String, rateOfInterest: Amount,
  dateOfOpening: Date, dateOfClosing: Option[Date] = None, balance: Balance = Balance()) extends Account


object Account {

  implicit val noLensCheckingAccount: Lens[CheckingAccount, String]  = GenLens[CheckingAccount](_.no)
  implicit val noLensSavingsAccount: Lens[SavingsAccount, String]  = GenLens[SavingsAccount](_.no)

  implicit val balanceLensCheckingAccount: Lens[CheckingAccount, Balance]  = GenLens[CheckingAccount](_.balance)
  implicit val balanceLensSavingsAccount: Lens[SavingsAccount, Balance]  = GenLens[SavingsAccount](_.balance)

  implicit val dateOfClosingLensCheckingAccount: Lens[CheckingAccount, Option[Date]]  = GenLens[CheckingAccount](_.dateOfClosing)
  implicit val dateOfClosingLensSavingsAccount: Lens[SavingsAccount, Option[Date]]  = GenLens[SavingsAccount](_.dateOfClosing)

  def clone[O <: Account](o: O)(implicit noLens: Lens[O, String]): O =
    noLens.set("2222")(o)

  def setDateOfClosing[O <: Account](o: O, closeDate: Option[Date])
    (implicit dateOfClosingLens: Lens[O, Option[Date]]): O =  dateOfClosingLens.set(closeDate)(o)


  val myCheckingAccount = CheckingAccount("1111", "Checking Account", today, None, Balance(0))

  val mySavingsAccount = SavingsAccount("1111", "Saving Account", 1.2, today, None, Balance(0))

  val cloneCheckingAccount: CheckingAccount = clone(myCheckingAccount)
  val cloneSavingsAccount: SavingsAccount = clone(mySavingsAccount)

  val closedCheckingAccount = setDateOfClosing(cloneCheckingAccount, Some(today))
}
