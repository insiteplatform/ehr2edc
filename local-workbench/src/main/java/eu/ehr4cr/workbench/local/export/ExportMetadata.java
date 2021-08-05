package eu.ehr4cr.workbench.local.export;

import java.io.File;
import java.util.concurrent.Future;

/**
 * Created by aleksandar on 06/12/16.
 */
public class ExportMetadata {
	private long exportId;
	private Future<File> future;

	public ExportMetadata(long exportId, Future<File> future) {
		this.exportId = exportId;
		this.future = future;
	}

	public long getExportId() {
		return exportId;
	}

	public Future<File> getFuture() {
		return future;
	}
}
