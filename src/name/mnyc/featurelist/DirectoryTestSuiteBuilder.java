package name.mnyc.featurelist;

import java.io.File;
import java.io.IOException;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;

/**
 * @author Paul McKenzie (http://stackoverflow.com/users/135624/paul-mckenzie)
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class DirectoryTestSuiteBuilder
{
	static final ClassLoader classLoader = DirectoryTestSuiteBuilder.class.getClassLoader();
	private String rootPath;
	private TestSuite testSuite;
	
	public DirectoryTestSuiteBuilder(String rootPath) throws IOException, ClassNotFoundException
	{
		if (!rootPath.endsWith("/"))
			rootPath += "/";

		this.rootPath = rootPath;

		testSuite = new TestSuite();
		findTests(testSuite, new File(rootPath));
	}
	
	private void findTests(final TestSuite testSuite, final File folder) throws IOException,
			ClassNotFoundException
	{
		for (final String fileName : folder.list())
		{
			final File file = new File(folder.getPath() + "/" + fileName);
			if (file.isDirectory())
			{
				findTests(testSuite, file);
			}
			else if (isTest(file))
			{
				addTest(testSuite, file);
			}
		}
	}
	
	private boolean isTest(final File f)
	{
		return f.isFile() && f.getName().endsWith("Test.java");
	}
	
	private void addTest(final TestSuite testSuite, final File f)
			throws ClassNotFoundException
	{
		final String className = makeClassName(f);
		final Class<?> testClass = makeClass(className);
		testSuite.addTest(new JUnit4TestAdapter(testClass));
	}
	
	private Class<?> makeClass(final String className) throws ClassNotFoundException
	{
		return (classLoader.loadClass(className));
	}
	
	private String makeClassName(final File f)
	{
		return f.getPath().replace(rootPath, "").replace("/", ".").replace("\\", ".")
				.replace(".java", "");
	}

	public TestSuite getTestSuite()
	{
		return testSuite;
	}
}