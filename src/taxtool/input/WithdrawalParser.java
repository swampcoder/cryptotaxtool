package taxtool.input;

import java.text.ParseException;

abstract public class WithdrawalParser {

   abstract public Withdrawal parseCsv(String line) throws ParseException;
}
