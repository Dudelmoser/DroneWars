package dronewars.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileHelper {

	public static String readFile(String path) {
		String file = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));
			try {
				String line = br.readLine();
				while(line != null) {
					file += line + "\n";
					line = br.readLine();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				br.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return file;
	}
	
	public static void writeFile(String path, String content) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF8"));
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
