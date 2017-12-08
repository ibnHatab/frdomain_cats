package frdomain
package algebra

import java.util.Date

import frdomain.common._

case class Balance(amount: Amount = 0)

case class Account(no: String, name: String, dateOfOpening: Date = today, dateOfClosing: Option[Date] = None,
  balance: Balance = Balance(0))
