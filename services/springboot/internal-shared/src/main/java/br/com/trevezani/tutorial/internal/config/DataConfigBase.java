package br.com.trevezani.tutorial.internal.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataConfigBase {

	public Connection getConnection(final InputStream inputStream) throws ClassNotFoundException, IOException, SQLException {
		Class.forName("org.relique.jdbc.csv.CsvDriver");

		final String url = "jdbc:relique:csv:zip:" + dataFile(inputStream);

		return DriverManager.getConnection(url);
	}

	private String dataFile(final InputStream inputStream) throws IOException {
		File fstream = File.createTempFile("data", ".zip");
		fstream.deleteOnExit();

		try (OutputStream outputStream = new FileOutputStream(fstream)) {
			inputStream.transferTo(outputStream);
		}

		Path data = fstream.toPath();

		return data.toString();
	}	
	
}
