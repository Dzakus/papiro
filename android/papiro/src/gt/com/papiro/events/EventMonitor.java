package gt.com.papiro.events;

import gt.com.papiro.util.DateUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import au.com.bytecode.opencsv.CSVWriter;

public class EventMonitor {

	File logFile;
	
	public EventMonitor(File logFile) {
		this.logFile = logFile;
	}
	
	public void logEvent(String companyId, String sourceType, String sourceId, String eventType, String eventName, String eventDescription, String eventInfo) {
		try {
			logFile.getParentFile().mkdirs();
			CSVWriter csvWriter = new CSVWriter(new FileWriter(logFile, true));
			String[] linea = new String[] {DateUtil.getDateAsIso8601(Calendar.getInstance().getTime()), companyId, sourceType, sourceId, eventType, eventName, eventDescription, eventInfo};
			csvWriter.writeNext(linea);
			csvWriter.close();
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}

}
