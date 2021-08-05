package com.custodix.insite.local.ehr2edc.query.executor.common.query;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem;

final class ProjectionLogger {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectionLogger.class);

	private ProjectionLogger() {

	}

	static void logStart(Optional objectToProject, Projector projector) {
		LOGGER.trace("Current value for object to project: {}", mapToString(objectToProject));
		LOGGER.trace("Starting projection with projector {}", mapToString(projector));
	}

	private static String mapToString(Projector projector) {
		if(doesProjectorConvertToFormItem(projector)) {
			return ReflectionToStringBuilder.toString(projector);
		}
		return projector.getClass()
				.getSimpleName();
	}

	private static boolean doesProjectorConvertToFormItem(Projector projector) {
		return projector instanceof ProjectedValueToFormItem;
	}

	static void logAfterValue(Optional objectToProject) {
		LOGGER.trace("Value after projector: {}", mapToString(objectToProject));
	}

	private static String mapToString(Optional objectToProject) {
		return (String) objectToProject
				.map(ProjectionLogger::mapToString)
				.orElse("Empty");
	}

	private static String mapToString(Object o) {
		if (o instanceof String) {
			return (String) o;
		}
		return new CustomReflectionToStringBuilder(o).toString();
	}

	private static final class OwnRecursiveToString extends RecursiveToStringStyle {
		OwnRecursiveToString() {
			super();
			setUseShortClassName(true);
			setUseIdentityHashCode(false);
		}

		/**
		 * This overridden method should be identical to the superclass method,
		 * except that the CustomReflectionToStringBuilder is used
		 */
		@Override
		public void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
			if (!ClassUtils.isPrimitiveWrapper(value.getClass()) && !String.class.equals(value.getClass()) && accept(
					value.getClass())) {
				buffer.append(new CustomReflectionToStringBuilder(value).toString());
			} else {
				super.appendDetail(buffer, fieldName, value);
			}
		}
	}

	private static final class CustomReflectionToStringBuilder extends ReflectionToStringBuilder {
		CustomReflectionToStringBuilder(Object object) {
			super(object, new OwnRecursiveToString());
		}

		@Override
		protected void appendFieldsIn(Class<?> clazz) {
			if (clazz.equals(BigDecimal.class)) {
				getStringBuffer().append(getObject().toString());
			} else {
				super.appendFieldsIn(clazz);
			}
		}
	}
}
