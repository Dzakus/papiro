package gt.com.papiro.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class FileUtil {
	
	public static boolean appendToFile(String text, File file) {
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		    out.println(text);
		    out.close();
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public static boolean writeToFile(InputStream is, File file) {
		FileOutputStream fos = null;
		
        try 
        {
        	file.getParentFile().mkdirs();
            fos = new FileOutputStream(file); 
            
            copyStream(is, fos);
            
            is.close();            
            fos.close();
            return true;
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
	}

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[1024]; 
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}
	
	public static void deleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            deleteRecursive(child);

	    fileOrDirectory.delete();
	}
	public static void renameDirectory(String fromDir, String toDir){
		File from = new File(fromDir);
		if (!from.exists() || !from.isDirectory()) {
		  System.out.println("El directorio no existe: " + fromDir);
		  return;
		}
		File to = new File(toDir);		
		//Rename
		if (from.renameTo(to))
		  System.out.println("Se ha renombrado el directorio");
		else
		  System.out.println("Error: Error al renombrar el directorio: " + from + " a " + to);
	}
	public static void copyDirectory(File sourceLocation , File targetLocation) throws IOException {
	    if (sourceLocation.isDirectory()) {
	    		    	
	        if (!targetLocation.exists()) {
	            targetLocation.mkdir();
	        }

	        String[] children = sourceLocation.list();
	        for (int i=0; i<children.length; i++) {
	            copyDirectory(new File(sourceLocation, children[i]),
	                    new File(targetLocation, children[i]));
	        }
	    } else {

	        InputStream in = new FileInputStream(sourceLocation);
	        OutputStream out = new FileOutputStream(targetLocation);

	        // Copy the bits from instream to outstream
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
	    }
	}
	
}
