package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement;

import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.spi.ServiceProvider;

public class ConvertMeasurementUnit {
	public static void main(String[] args) {
		Unit unit = ServiceProvider.current().getUnitFormatService().getUnitFormat().parse("g/l");
		System.out.println(unit);
		Unit unit2 = ServiceProvider.current().getUnitFormatService().getUnitFormat().parse("mg/dl");
		System.out.println(unit2);
		UnitConverter converter = unit2.getConverterTo(unit);
		System.out.println(converter.convert(1));
	}
}
