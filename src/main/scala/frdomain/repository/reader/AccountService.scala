package frdomain
package repository
package reader

import cats._, cats.data._

import java.util.Date
import util.{ Success, Failure }
import frdomain.common._, frdomain.algebra._

import frdomain.algebra.Account

object AccountService extends AccountService[Account, Amount, Balance] {

  import frdomain.smartconstructor.Account

  def open(no: String, name: String, openingDate: Option[Date]) = Reader { (repo: AccountRepository) =>
    repo.query(no) match {
      case Success(Some(a)) => Failure(new Exception(s"Already existing account with no $no"))
      case Success(None) =>
        val a = Account.checkingAccount(no, name, openingDate.getOrElse(today))
        repo.store(a.get)
      case Failure(ex) => Failure(new Exception(s"Failed to open account $no: $name", ex))
    }
  }

  def close(no: String, closeDate: Option[Date]) = Reader { (repo: AccountRepository) =>
    repo.query(no) match {
      case Success(Some(a)) =>
        if (closeDate.getOrElse(today) before a.dateOfOpening)
          Failure(new Exception(s"Close date $closeDate cannot be before opening date ${a.dateOfOpening}"))
        else repo.store(Account.setDateOfClosing(a, closeDate))
      case Success(None) => Failure(new Exception(s"Account not found with $no"))
      case Failure(ex) => Failure(new Exception(s"Fail in closing account $no", ex))
    }
  }

  def debit(no: String, amount: Amount) = Reader { (repo: AccountRepository) =>
    repo.query(no) match {
      case Success(Some(a)) =>
        if (a.balance.amount < amount) Failure(new Exception("Insufficient balance"))
       else repo.store(Account.setBalance(a, Balance(a.balance.amount - amount)))
      case Success(None) => Failure(new Exception(s"Account not found with $no"))
      case Failure(ex) => Failure(new Exception(s"Fail in debit from $no amount $amount", ex))
    }
  }

  def credit(no: String, amount: Amount) = Reader { (repo: AccountRepository) =>
    repo.query(no) match {
      case Success(Some(a)) => repo.store(Account.setBalance(a, Balance(a.balance.amount + amount)))
      case Success(None) => Failure(new Exception(s"Account not found with $no"))
      case Failure(ex) => Failure(new Exception(s"Fail in credit to $no amount $amount", ex))
    }
  }

  def balance(no: String) = Reader((repo: AccountRepository) => repo.balance(no))
}

object App {
  import AccountService._
  import frdomain.common._
  import frdomain.smartconstructor.Account

  val a = Account.checkingAccount("1234", "vlad", today, None, Balance(0)).get
  val r = AccountRepositoryInMemory
  r.store(a)

  def op(no: String) = for {
    _ <- credit(no, BigDecimal(100))
    _ <- credit(no, BigDecimal(300))
    _ <- debit(no, BigDecimal(160))
    b <- balance(no)
  } yield b

  val b = op(a.no)(r)

}
