package taxtool.input;

import java.text.ParseException;

abstract public class WithdrawalParser {

   abstract public CryptoRecord parseCsv(String line) throws ParseException;
}
