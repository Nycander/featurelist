/**
 * 
 */
package name.mnyc.featurelist;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.TestSuite;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 * 
 */
public class FeatureListCompiler extends RunListener
{
	private Map<String, List<String>> features = new TreeMap<String, List<String>>();
	private Set<String> fails = new HashSet<String>();
	
	// Will not utilize serialization
	@SuppressWarnings("serial")
	private static final Map<String, String> negationReplacement = new HashMap<String, String>() {
		{
			put("has", "has not");
			put("can", "can not");
			put("will", "will not");
			put("support", "does not support");
			put("is", "is not");
			put("are", "are not");
		}
	};

	public FeatureListCompiler()
	{
		super();
	}
	
	@Override
	public void testFinished(Description description) throws Exception
	{
		FeatureDescription feature = description.getAnnotation(FeatureDescription.class);
		if (feature != null)
		{
			List<String> list = features.get(feature.category());
			if (list == null)
				list = new ArrayList<String>();
			
			if (!list.contains(feature.value()))
				list.add(feature.value());

			features.put(feature.category(), list);
		}
		super.testFinished(description);
	}
	
	@Override
	public void testFailure(Failure failure) throws Exception
	{
		Description description = failure.getDescription();
		FeatureDescription feature = description.getAnnotation(FeatureDescription.class);
		if (feature != null)
		{
			fails.add(feature.category() + ": " + feature.value());
		}
		super.testFailure(failure);
	}
	
	@Override
	public void testRunFinished(Result result) throws Exception
	{
		String title = "Feature list "
				+ DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
		
		printFeatures(title, '+');
		
		if (fails.size() > 0)
		{
			printBrokenFeatures("Broken features ", '!');
		}

		super.testRunFinished(result);
	}

	private void printFeatures(String title, char bulletPoint)
	{
		System.out.println(title);
		for (int i = 0; i < title.length(); i++)
			System.out.print('=');
		System.out.println();

		for (String category : features.keySet())
		{
			if (features.get(category).size() == 0)
				continue;

			StringBuilder sb = new StringBuilder();
			for (String feature : features.get(category))
			{
				boolean testFailed = fails.contains(category + ": " + feature);
				
				if (testFailed)
					continue;
				
				sb.append(' ');
				sb.append(bulletPoint);
				sb.append(' ');
				sb.append(feature);
				sb.append('\n');
			}
			if (sb.length() > 0)
			{
				System.out.println(category);
				System.out.println(sb);
			}
		}
	}

	private void printBrokenFeatures(String title, char bulletPoint)
	{
		System.out.println(title);
		for (int i = 0; i < title.length(); i++)
			System.out.print('=');
		System.out.println();
		
		for (String category : features.keySet())
		{
			if (features.get(category).size() == 0)
				continue;
			
			StringBuilder sb = new StringBuilder();
			for (String feature : features.get(category))
			{
				boolean testFailed = fails.contains(category + ": " + feature);
				
				if (!testFailed)
					continue;
				
				sb.append(' ');
				sb.append(bulletPoint);
				sb.append(' ');
				
				String brokenFeature = feature;
				for (Entry<String, String> entry : negationReplacement.entrySet())
				{
					brokenFeature = brokenFeature.replace(entry.getKey(), entry.getValue());
				}
				sb.append(brokenFeature);
				sb.append('\n');
			}
			if (sb.length() > 0)
			{
				System.out.println(category);
				System.out.println(sb);
			}
		}
	}

	public static final void main(String... args) throws Exception
	{
		TestSuite suite;
		DirectoryTestSuiteBuilder builder = new DirectoryTestSuiteBuilder("test.unit/");
		suite = builder.getTestSuite();
		builder = new DirectoryTestSuiteBuilder("test.integration/");
		suite.addTest(builder.getTestSuite());

		JUnitCore core = new JUnitCore();
		core.addListener(new FeatureListCompiler());
		core.run(suite);
		System.exit(0);
	}
}
