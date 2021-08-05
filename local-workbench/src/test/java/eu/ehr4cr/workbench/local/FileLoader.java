package eu.ehr4cr.workbench.local;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileLoader {
	private static final Path CURRENT_DIR = Paths.get(System.getProperty("user.dir"));

	private FileLoader() {
	}

	public static String loadFileContent(String classPathFilename) {
		final Path path = Paths.get(classPathFilename);
		return loadFileContent(path);
	}

	public static String loadFileContent(Path classPathResource) {
		final Path absolute = convertTAbsolutePath(classPathResource);
		return loadAbsoluteFileContent(CURRENT_DIR.resolve(absolute));
	}

	public static String loadRelativeFileContent(Path relativePath) {
		return loadAbsoluteFileContent(CURRENT_DIR.resolve(relativePath));
	}

	public static String loadAbsoluteFileContent(Path relativePath) {
		try {
			return new String(Files.readAllBytes(relativePath));
		} catch (IOException e) {
			throw new UnableToReadFileException(relativePath, e);
		}
	}

	public static Path convertToAbsolutePathFromFileName(String classPathFilename) {
		final Path path = Paths.get(classPathFilename);
		final URL resource = FileLoader.class.getClassLoader()
				.getResource(path.toString());
		return Paths.get(toUri(resource));
	}

	static Path convertTAbsolutePath(Path relativePath) {
		final URL resource = FileLoader.class.getClassLoader()
				.getResource(relativePath.toString());
		return Paths.get(toUri(resource));
	}

	private static URI toUri(URL resource) {
		try {
			return resource.toURI();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("The URL could not be converted to an URI", e);
		}
	}

	public static class UnableToReadFileException extends RuntimeException {
		public UnableToReadFileException(Path path, Throwable cause) {
			super("The test was unable to read the file from path " + path.toString(), cause);
		}
	}
}
