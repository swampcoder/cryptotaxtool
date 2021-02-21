package taxtool.input;

import java.text.ParseException;

abstract public class CsvParser {

   abstract public CryptoRecord parseLine(String tradeCsv) throws ParseException;

}
