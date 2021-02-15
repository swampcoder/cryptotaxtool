package taxtool.input;

import java.text.ParseException;

abstract public class CsvParser {

   abstract public Trade parseLine(String tradeCsv) throws ParseException;

}
