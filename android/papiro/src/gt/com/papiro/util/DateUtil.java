package gt.com.papiro.util;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public DateUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getDateAsIso8601(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		String dateAsString = df.format(date);
		return dateAsString;
	}

}
