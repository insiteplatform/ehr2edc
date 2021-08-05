package com.custodix.insite;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.data.annotation.PersistenceConstructor;

public class NoPrimitivesAllowedInPersitenceConstructorTest {

	@Test
	public void test_presistence_constuctors_dont_use_primitives() throws Exception {
		assertThat(getViolatingClasses()).isEmpty();
	}

	private List<String> getViolatingClasses() throws IOException, ClassNotFoundException {
		return Arrays.stream(getClasses("com.custodix.insite"))
				.map(this::validateNoPrimitives)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
	}

	private Optional<String> validateNoPrimitives(Class c) {
		return primitiveParameterPresentInParametersInClass(c) ?
				Optional.of(
						"The class " + c.getSimpleName() + " contains a persistence constructor with primitive type") :
				Optional.empty();
	}

	private boolean primitiveParameterPresentInParametersInClass(Class c) {
		return persistenceConstructors(c.getDeclaredConstructors()).map(Constructor::getParameterTypes)
				.flatMap(Arrays::stream)
				.anyMatch(Class::isPrimitive);
	}

	private Stream<Constructor> persistenceConstructors(Constructor[] constructors) {
		return Arrays.stream(constructors)
				.filter(this::isAPersistenceConstructor);
	}

	private boolean isAPersistenceConstructor(Constructor cons) {
		return cons.getAnnotation(PersistenceConstructor.class) != null;
	}

	private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName()
						.contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName()
					.endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName()
						.substring(0, file.getName()
								.length() - 6)));
			}
		}
		return classes;
	}
}
