package frdomain
package lens

import monocle.Lens
import monocle.macros.GenLens

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
