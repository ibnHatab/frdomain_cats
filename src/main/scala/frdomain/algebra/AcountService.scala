package frdomain
package algebra

import frdomain.repository.AccountRepository
import java.util.Date
import util.Try

import cats.data.Reader

trait AccountService[Account, Amount, Balance] {
  def open(no: String, name: String, openingDate: Option[Date]): Reader[AccountRepository, Try[Account]]
  def close(no: String, closeDate: Option[Date]): Reader[AccountRepository, Try[Account]]
  def debit(no: String, amount: Amount): Reader[AccountRepository, Try[Account]]
  def credit(no: String, amount: Amount): Reader[AccountRepository, Try[Account]]
  def balance(no: String): Reader[AccountRepository, Try[Balance]]
}

// trait AccountService[Account, Amount, Balance] {
//   def open(no: String, name: String, openingDate: Option[Date]): Try[Account]
//   def close(account: Account, closeDate: Option[Date]): Try[Account]
//   def debit(account: Account, amount: Amount): Try[Account]
//   def credit(account: Account, amount: Amount): Try[Account]
//   def balance(account: Account): Try[Balance]

//   def transfer(from: Account, to: Account, amount: Amount): Try[(Account, Account, Amount)] = for {
//     a <- debit(from, amount)
//     b <- credit(to, amount)
//   } yield (a, b, amount)
// }
