package eu.ehr4cr.workbench.local.conf.db;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "hibernate")
class ExternalHibernateSettings implements HibernateSettings {
	@NotBlank
	private String dialect = "org.hibernate.dialect.PostgreSQLDialect";
	@NotBlank
	private String hbm2Ddl = "validate";

	@Override
	public String getDialect() {
		return dialect;
	}

	@Override
	public String getHbm2Ddl() {
		return hbm2Ddl;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public void setHbm2Ddl(String hbm2Ddl) {
		this.hbm2Ddl = hbm2Ddl;
	}
}
