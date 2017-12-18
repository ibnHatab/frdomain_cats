package frdomain
package algebra

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
