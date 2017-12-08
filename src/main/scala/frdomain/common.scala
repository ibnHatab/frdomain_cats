package frdomain

import java.util.Calendar

object common {
  type Amount = BigDecimal

  def today = Calendar.getInstance.getTime
}
