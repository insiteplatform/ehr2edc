package eu.ehr4cr.workbench.local.vocabulary.clinical;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

@JsonSerialize(using = StudyIdentifier.Serializer.class)
@JsonDeserialize(using = StudyIdentifier.Deserializer.class)
public class StudyIdentifier implements Serializable {
	private final long id;

	private StudyIdentifier(Builder builder) {
		id = builder.id;
	}

	public long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof StudyIdentifier)) {
			return false;
		}
		final StudyIdentifier that = (StudyIdentifier) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public static StudyIdentifier of(long id) {
		return newBuilder().withId(id)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private long id;

		private Builder() {
		}

		public Builder withId(long id) {
			this.id = id;
			return this;
		}

		public StudyIdentifier build() {
			return new StudyIdentifier(this);
		}
	}

	public static final class Serializer extends StdSerializer<StudyIdentifier> {
		public Serializer() {
			this(StudyIdentifier.class);
		}

		protected Serializer(Class<StudyIdentifier> t) {
			super(t);
		}

		@Override
		public void serialize(StudyIdentifier studyIdentifier, JsonGenerator jsonGenerator,
				SerializerProvider serializerProvider) throws IOException {
			jsonGenerator.writeNumber(studyIdentifier.getId());
		}
	}

	public static final class Deserializer extends StdDeserializer<StudyIdentifier> {
		public Deserializer() {
			this(StudyIdentifier.class);
		}

		public Deserializer(Class<StudyIdentifier> vc) {
			super(vc);
		}

		@Override
		public StudyIdentifier deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
				throws IOException {
			StudyIdentifier identifier = null;
			JsonNode node = jsonParser.getCodec()
					.readTree(jsonParser);
			if (!node.isNull()) {
				if (node instanceof TextNode) {
					identifier = StudyIdentifier.of(Long.valueOf(node.textValue()));
				} else if (node instanceof NumericNode) {
					identifier = StudyIdentifier.of(node.longValue());
				}
			}
			return identifier;
		}
	}
}