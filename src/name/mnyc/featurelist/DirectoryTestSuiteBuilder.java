package name.mnyc.featurelist;

import java.io.File;
import java.io.IOException;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;

/**
 * A helper class which builds a test suite from a given directory.
 * 
 * @author Paul McKenzie (http://stackoverflow.com/users/135624/paul-mckenzie)
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class DirectoryTestSuiteBuilder
{
	static final ClassLoader classLoader = DirectoryTestSuiteBuilder.class.getClassLoader();
	private String rootPath;
	private TestSuite testSuite;
	
	/**
	 * @param rootPath
	 *            the root directory which contains all the tests which is included in the suite.
	 * @throws IOException
	 *             if a file could not be found. Probably indicates an incorrect rootPath.
	 */
	public DirectoryTestSuiteBuilder(String rootPath) throws IOException
	{
		if (!rootPath.endsWith("/"))
			rootPath += "/";

		this.rootPath = rootPath;

		testSuite = new TestSuite();
		findTests(testSuite, new File(rootPath));
	}
	
	/**
	 * Recursively search for tests.
	 * 
	 * @param testSuite
	 * @param folder
	 * @throws IOException
	 */
	private void findTests(final TestSuite testSuite, final File folder) throws IOException
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
	
	/**
	 * @return true if the file is a test file
	 */
	private boolean isTest(final File f)
	{
		return f.isFile() && f.getName().endsWith("Test.java");
	}
	
	/**
	 * Adds a test to the resulting test suite
	 * 
	 * @param testSuite
	 *            the test suite to add
	 * @param testFile
	 *            the file containing the test
	 * @throws ClassNotFoundException
	 *             is thrown if the test class could not be loaded.
	 */
	private void addTest(final TestSuite testSuite, final File testFile)
	{
		final String className = makeClassName(testFile);
		final Class<?> testClass = makeClass(className);
		testSuite.addTest(new JUnit4TestAdapter(testClass));
	}
	
	/**
	 * Attempts to load a class.
	 * 
	 * @param className
	 *            the name of the class
	 * @return a class object representing the class
	 */
	private Class<?> makeClass(final String className)
	{
		try
		{
			return (classLoader.loadClass(className));
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Constructs a class name from a file.
	 * 
	 * @param testFile
	 *            a file representing the test case
	 * @return a string with the class name
	 */
	private String makeClassName(final File testFile)
	{
		return testFile.getPath().replace(rootPath, "").replace("/", ".").replace("\\", ".")
				.replace(".java", "");
	}

	/**
	 * @return the built test suite
	 */
	public TestSuite getTestSuite()
	{
		return testSuite;
	}
}