package gt.com.papiro.vista;

public class ContactUtil {

	public ContactUtil() {		
	}

	public static String joinContactInfo(String part1, String part2) {
		if (part1.trim().length() == 0) {
			return "(Contacto no ingresado)";
		}

		if (part2.trim().equalsIgnoreCase("telefono") || part2.trim().equalsIgnoreCase("otros") ) {
			return part1.trim();
		} else {
			return part1.replaceAll("@.*", "").trim() + part2.trim();
		}
	}
	
}
