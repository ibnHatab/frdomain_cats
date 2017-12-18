package frdomain
package smartconstructor

import java.util.{ Date }
import util.{ Try, Success, Failure }

import frdomain.common._
import frdomain.algebra._


final case class CheckingAccount private (no: String, name: String,
  dateOfOpening: Date, dateOfClosing: Option[Date] = None, balance: Balance = Balance()) extends Account

final case class SavingsAccount private (no: String, name: String, rateOfInterest: Amount,
  dateOfOpening: Date, dateOfClosing: Option[Date] = None, balance: Balance = Balance()) extends Account

object Account {
  def checkingAccount(no: String, name: String, openDate: Date = today, closeDate: Option[Date] = None,
    balance: Balance = Balance(0)): Try[Account] = {

    if (no.isEmpty || name.isEmpty) Failure(new Exception(s"Account no or name cannot be blank") )

    closeDateCheck(openDate, closeDate).map { d =>
      CheckingAccount(no, name, d._1, d._2, balance)
    }
  }

  def savingsAccount(no: String, name: String, rate: BigDecimal, openDate: Date, closeDate: Option[Date], balance: Balance): Try[Account] = {

    closeDateCheck(openDate, closeDate).map { d =>
      if (rate <= BigDecimal(0))
        throw new Exception(s"Interest rate $rate must be > 0")
      else
        SavingsAccount(no, name, rate, d._1, d._2, balance)
    }
  }

  private def closeDateCheck(openDate: Date, closeDate: Option[Date]): Try[(Date, Option[Date])] = {
    val od = openDate

    closeDate.map { cd =>
      if (cd before od) Failure(new Exception(s"Close date [$cd] cannot be earlier than open date [$od]"))
      else Success((od, Some(cd)))
    }.getOrElse {
      Success((od, closeDate))
    }
  }

  def setDateOfClosing[O <: Account](o: O, closeDate: Option[Date]) =
    o match {
      case ca: CheckingAccount => ca.copy(dateOfClosing = closeDate)
      case sa: SavingsAccount => sa.copy(dateOfClosing = closeDate)
    }

  def setBalance[O <: Account](o: O, balance: Balance) =
    o match {
      case ca: CheckingAccount => ca.copy(balance = balance)
      case sa: SavingsAccount => sa.copy(balance = balance)
    }
}
