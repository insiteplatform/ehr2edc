package com.custodix.insite.local.ehr2edc.mongo.app.configuration;

import static java.util.Arrays.stream;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.convert.TypeInformationMapper;
import org.springframework.data.mapping.Alias;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public class EntityScanningTypeInformationMapper implements TypeInformationMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(EntityScanningTypeInformationMapper.class);
	private final List<String> scanPackages;
	private Map<Alias, Class<? extends Object>> aliasToClass = new HashMap<>();

	EntityScanningTypeInformationMapper(List<String> scanPackages) {
		this.scanPackages = scanPackages;
		scanPackages();
	}

	@Override
	public TypeInformation<?> resolveTypeFrom(Alias alias) {
		Class<? extends Object> clazz = aliasToClass.get(alias);
		if (clazz != null) {
			return ClassTypeInformation.from(clazz);
		}
		return null;
	}

	@Override
	public Alias createAliasFor(TypeInformation<?> type) {
		LOGGER.debug("EntityScanningTypeInformationMapper asked to create alias for type: {}", type);
		return Alias.empty();
	}

	private void scanPackages() {
		scanPackages.forEach(this::scanPackage);
	}

	private void scanPackage(String pkg) {
		try {
			findMyTypes(pkg);
		} catch (IOException e) {
			LOGGER.error("Error scanning package {}", pkg, e);
			throwErrorDuringScanning(e);
		}
	}

	private void findMyTypes(String pkg) throws IOException {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
		LOGGER.debug("Scanning package {}", pkg);
		Resource[] resources = getClassesForPackage(pkg, resourcePatternResolver);
		stream(resources).filter(Resource::isReadable)
				.forEach(r -> processClass(metadataReaderFactory, r));
	}

	private void processClass(MetadataReaderFactory metadataReaderFactory, Resource resource) {
		try {
			MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
			Class<? extends Object> c = Class.forName(metadataReader.getClassMetadata()
					.getClassName());
			if (c.isAnnotationPresent(TypeAlias.class)) {
				registerTypeAlias(c);
			}
		} catch (IOException | ClassNotFoundException e) {
			throwErrorDuringScanning(e);
		}
	}

	private void throwErrorDuringScanning(Exception e) {
		throw new SystemException("Scanning for type aliases went wrong", e);
	}

	private void registerTypeAlias(Class<?> c) {
		TypeAlias typeAliasAnnot = c.getAnnotation(TypeAlias.class);
		String alias = typeAliasAnnot.value();
		LOGGER.debug("register type alias {} for class {}", alias, c);
		aliasToClass.put(Alias.of(alias), c);
	}

	private Resource[] getClassesForPackage(String basePackage, ResourcePatternResolver resourcePatternResolver)
			throws IOException {
		String packageSearchPath =
				ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + "/" + "**/*.class";
		return resourcePatternResolver.getResources(packageSearchPath);
	}

	private String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
	}
}