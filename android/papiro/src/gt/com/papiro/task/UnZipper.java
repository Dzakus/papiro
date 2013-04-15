package gt.com.papiro.task;

import gt.com.papiro.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Observable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.os.AsyncTask;
import android.util.Log;

public class UnZipper extends Observable {

	private static final String TAG = "UnZip";
	private File zipFile;
	private File destination;

	public UnZipper(File zipFile, File destination) {
		this.zipFile = zipFile;
		this.destination = destination;
	}

	public File getZipFile() {
		return zipFile;
	}

	public void setZipFile(File zipFile) {
		this.zipFile = zipFile;
	}

	public File getDestination() {
		return destination;
	}

	public void setDestination(File destination) {
		this.destination = destination;
	}

	public void unzip() {
		Log.d(TAG, "unzipping " + zipFile.toString() + " to " + destination.toString());
		new UnZipTask().execute(zipFile, destination);
	}

	private class UnZipTask extends AsyncTask<File, Void, Boolean> {

		@SuppressWarnings("rawtypes")
		@Override
		protected Boolean doInBackground(File... params) {
			File source = params[0];
			File destination = params[1];
			
			try {
				ZipFile zipfile = new ZipFile(source);
				for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
					ZipEntry entry = (ZipEntry) e.nextElement();
					unzipEntry(zipfile, entry, destination);
				}
			} catch (Exception e) {
				Log.e(TAG, "Error while extracting file " + source, e);
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setChanged();
			notifyObservers();
		}

		private void unzipEntry(ZipFile zipfile, ZipEntry entry,
				File outputDir) throws IOException {

			if (entry.isDirectory()) {
				createDir(new File(outputDir, entry.getName()));
				return;
			}

			File outputFile = new File(outputDir, entry.getName());
			if (!outputFile.getParentFile().exists()) {
				createDir(outputFile.getParentFile());
			}

			Log.v(TAG, "Extracting: " + entry);
			BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

			try {
				FileUtil.copyStream(inputStream, outputStream);
			} finally {
				outputStream.close();
				inputStream.close();
			}
		}

		private void createDir(File dir) {
			if (dir.exists()) {
				return;
			}
			Log.v(TAG, "Creating dir " + dir.getName());
			if (!dir.mkdirs()) {
				throw new RuntimeException("Can not create dir " + dir);
			}
		}
	}
}
