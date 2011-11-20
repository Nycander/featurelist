/**
 * 
 */
package name.mnyc.featurelist;

import java.io.PrintWriter;
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

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Example usage:
 * 
 * <pre>
 * public static final void main(String... args) throws Exception
 * {
 * 	TestSuite suite = new TestSuite();
 * 	
 * 	for (int i = 0; i &lt; args.length; i++)
 * 	{
 * 		DirectoryTestSuiteBuilder builder = new DirectoryTestSuiteBuilder(args[i]);
 * 		suite.addTest(builder.getTestSuite());
 * 	}
 * 	
 * 	JUnitCore core = new JUnitCore();
 * 	core.addListener(new FeatureListCompiler());
 * 	core.run(suite);
 * 	System.exit(0);
 * }
 * </pre>
 * 
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class FeatureListCompiler extends RunListener
{
	private Map<String, List<String>> features = new TreeMap<String, List<String>>();
	private Set<String> fails = new HashSet<String>();
	private final PrintWriter out;
	
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
		this(new PrintWriter(System.out));
	}
	
	public FeatureListCompiler(PrintWriter out)
	{
		super();
		this.out = out;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.notification.RunListener#testFinished(org.junit.runner.Description)
	 */
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.junit.runner.notification.RunListener#testFailure(org.junit.runner.notification.Failure)
	 */
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.notification.RunListener#testRunFinished(org.junit.runner.Result)
	 */
	@Override
	public void testRunFinished(Result result) throws Exception
	{
		String title = "Feature list "
				+ DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
		
		printFeatures(title, '+', FeatureType.WORKING);
		
		if (fails.size() > 0)
		{
			printFeatures("Broken features ", '!', FeatureType.BROKEN);
		}
		out.flush();
		super.testRunFinished(result);
	}

	/**
	 * Prints working features.
	 * 
	 * @param title
	 *            the headline for the feature set
	 * @param bulletPoint
	 *            the type of bullet point to use in the list
	 */
	private void printFeatures(final String title, final char bulletPoint, FeatureType featureType)
	{
		out.println(title);
		for (int i = 0; i < title.length(); i++)
			out.print('=');
		out.println();

		for (final String category : features.keySet())
		{
			if (features.get(category).size() == 0)
				continue;

			final String features;
			switch (featureType)
			{
				case BROKEN:
					features = brokenFeaturesInCategory(bulletPoint, category);
					break;
				case WORKING:
					features = featuresInCategory(bulletPoint, category);
					break;
				default:
					features = "";
					break;
			}
			
			if (features.length() > 0)
			{
				out.println(category);
				out.println(features);
			}
		}
	}

	/**
	 * Build a list of features.
	 * 
	 * @param bulletPoint
	 *            the type of bullet point to use in the list.
	 * @param category
	 *            the category to print features from.
	 * @return the compiled list.
	 */
	private String featuresInCategory(final char bulletPoint, final String category)
	{
		final StringBuilder sb = new StringBuilder();
		for (final String feature : features.get(category))
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
		return sb.toString();
	}

	/**
	 * Build a list of broken features. An attempt is made to negate any feature string, example:
	 * "supports JSON objects" -> "does not support JSON objects"
	 * 
	 * @param bulletPoint
	 *            the type of bullet point to use in the list.
	 * @param category
	 *            the category to print features from.
	 * @return the compiled list.
	 */
	private String brokenFeaturesInCategory(char bulletPoint, final String category)
	{
		StringBuilder sb = new StringBuilder();
		for (final String feature : features.get(category))
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
		return sb.toString();
	}
}
