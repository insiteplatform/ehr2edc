package eu.ehr4cr.workbench.local.exception;

import java.nio.file.Path;

public class UnableToReadFileException extends RuntimeException {
	public UnableToReadFileException(Path path, Throwable cause) {
		super("The test was unable to read the file from path " + path.toString(), cause);
	}
}
