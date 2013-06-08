package gt.com.papiro.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;

import com.google.gson.Gson;

public class MappingUtil {
	
	public static Object loadObjectFromFile(File file, Class clazz) {
		Gson gson = new Gson();
		try {
			return gson.fromJson(new FileReader(file), clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	public static boolean saveObjectToFile(Object obj, File file) {
		Gson gson = new Gson();

		try {			
			FileUtil.writeToFile(new ByteArrayInputStream(gson.toJson(obj).getBytes()), file);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
