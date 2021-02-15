package taxtool.input;

import java.io.IOException;
import java.text.ParseException;

// record loader interface to separate sensitive data from application
public interface IRecordLoader extends IRankedService {

   public DataRecord loadRecords() throws IOException, ClassNotFoundException, ParseException;
}
