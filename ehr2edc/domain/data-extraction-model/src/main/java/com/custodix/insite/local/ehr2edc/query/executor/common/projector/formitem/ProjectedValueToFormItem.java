package com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem;

import static com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem.UnitMapping.IGNORE;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.custodix.insite.local.ehr2edc.query.executor.common.domain.FormItem;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.MapToStringProjector;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValueField;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class ProjectedValueToFormItem implements Projector<FormItem, ProjectedValue> {
	private static final String NAME = "ProjectedValue to FormItem";
	private final Map<Object, String> indexMapping;
	private final ProjectedValueField outputField;
	private final UnitMapping unitMapping;
	private final boolean readOnly;
	private final boolean key;

	public ProjectedValueToFormItem(Map<Object, String> indexMapping, ProjectedValueField outputField,
			UnitMapping unitMapping, boolean readOnly, boolean key) {
		this.indexMapping = indexMapping == null ? emptyMap() : indexMapping;
		this.outputField = outputField == null ? ProjectedValueField.VALUE : outputField;
		this.unitMapping = unitMapping == null ? IGNORE : unitMapping;
		this.readOnly = readOnly;
		this.key = key;
	}

	@Override
	public Optional<FormItem> project(Optional<ProjectedValue> projectedValue, ProjectionContext context) {
		return projectedValue.map(m -> this.map(m, context));
	}

	@Override
	public String getName() {
		return NAME;
	}

	private FormItem map(ProjectedValue projectedValue, ProjectionContext context) {
		final String index = getIndex(projectedValue, context);
		if (index == null && isIndexMappingPresent()) {
			return null;
		}
		return FormItem.newBuilder()
				.withDataPoint(context.getDataPoint())
				.withValue(getValue(projectedValue).orElse(null))
				.withUnit(unitMapping.getUnit(projectedValue))
				.withIndex(index)
				.withReadOnly(readOnly)
				.withOutputUnit(unitMapping.getOutputUnit())
				.withKey(key)
				.build();
	}

	private String getIndex(ProjectedValue projectedValue, ProjectionContext context) {
		if (!isIndexMappingPresent()) {
			return null;
		}
		Optional<Object> code = Optional.ofNullable(projectedValue.getCode());
		Optional<String> optionalIndex = new MapToStringProjector(indexMapping).project(code, context);
		return optionalIndex.orElse(null);
	}

	private Optional<LabeledValue> getValue(ProjectedValue projectedValue) {
		return outputField.getFieldValue(projectedValue)
				.flatMap(this::getValue);
	}

	private Optional<LabeledValue> getValue(Object object) {
		if (object instanceof LabeledValue) {
			return Optional.of((LabeledValue) object);
		} else {
			return Optional.of(object.toString())
					.filter(StringUtils::isNotBlank)
					.map(LabeledValue::new);
		}
	}

	private boolean isIndexMappingPresent() {
		return indexMapping != null && !indexMapping.isEmpty();
	}

	public enum UnitMapping {
		IGNORE {
			@Override
			String getUnit(ProjectedValue projectedValue) {
				return null;
			}

			@Override
			boolean getOutputUnit() {
				return false;
			}
		},
		DISPLAY {
			@Override
			String getUnit(ProjectedValue projectedValue) {
				return defaultIfBlank(projectedValue.getUnit(), null);
			}

			@Override
			boolean getOutputUnit() {
				return false;
			}
		},
		EXPORT {
			@Override
			String getUnit(ProjectedValue projectedValue) {
				return defaultIfBlank(projectedValue.getUnit(), null);
			}

			@Override
			boolean getOutputUnit() {
				return true;
			}
		};

		abstract String getUnit(ProjectedValue projectedValue);

		abstract boolean getOutputUnit();
	}
}
