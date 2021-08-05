package eu.ehr4cr.workbench.local.export;

public enum ExporterFormatType {
	CSV() {
		@Override
		public Exporter getExporter(ExporterSettings settings) {
			return new CSVExporter(settings);
		}
	},
	EXCEL() {
		@Override
		public Exporter getExporter(ExporterSettings settings) {
			return new ExcelExporter(settings);
		}
	};

	public abstract Exporter getExporter(ExporterSettings settings);
}
