package eu.ehr4cr.workbench.local.vocabulary;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@JsonSerialize(using = CohortIdentifier.Serializer.class)
@JsonDeserialize(using = CohortIdentifier.Deserializer.class)
public class CohortIdentifier implements Serializable {
	private final long id;

	private CohortIdentifier(Builder builder) {
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
		if (!(o instanceof CohortIdentifier)) {
			return false;
		}
		final CohortIdentifier that = (CohortIdentifier) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public static CohortIdentifier of(long id) {
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

		public CohortIdentifier build() {
			return new CohortIdentifier(this);
		}
	}

	public static final class Serializer extends StdSerializer<CohortIdentifier> {
		public Serializer() {
			this(CohortIdentifier.class);
		}

		protected Serializer(Class<CohortIdentifier> t) {
			super(t);
		}

		@Override
		public void serialize(CohortIdentifier cohortIdentifier, JsonGenerator jsonGenerator,
				SerializerProvider serializerProvider) throws IOException {
			jsonGenerator.writeNumber(cohortIdentifier.getId());
		}
	}

	public static final class Deserializer extends StdDeserializer<CohortIdentifier> {
		public Deserializer() {
			this(CohortIdentifier.class);
		}

		public Deserializer(Class<CohortIdentifier> vc) {
			super(vc);
		}

		@Override
		public CohortIdentifier deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
				throws IOException, JsonProcessingException {
			CohortIdentifier identifier = null;
			JsonNode node = jsonParser.getCodec()
					.readTree(jsonParser);
			if (!node.isNull()) {
				if (node instanceof TextNode) {
					identifier = CohortIdentifier.of(Long.valueOf(node.textValue()));
				} else if (node instanceof NumericNode) {
					identifier = CohortIdentifier.of(node.longValue());
				}
			}
			return identifier;
		}
	}
}
