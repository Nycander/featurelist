package name.mnyc.featurelist;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;

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
	private FeatureList features = new FeatureList();
	private final PrintWriter out;

	public FeatureListCompiler()
	{
		this(new PrintWriter(System.out));
	}
	
	public FeatureListCompiler(PrintWriter out)
	{
		super();
		this.out = out;
	}
	
	@Override
	public void testFinished(Description description) throws Exception
	{
		FeatureDescription feature = description.getAnnotation(FeatureDescription.class);
		if (feature != null)
		{
			features.addFeature(feature.category(), feature.value());
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
			Feature feat = features.getFeature(feature);
			
			if (feat == null)
				throw new RuntimeException("Could not find feature description " + feature
						+ " of broken test " + failure + ".");
			
			feat.setNotWorking();
		}
		super.testFailure(failure);
	}
	
	@Override
	public void testRunFinished(Result result) throws Exception
	{
		features.setTitle("Feature list "
				+ DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));
		
		out.print(features.toString());
		out.flush();

		super.testRunFinished(result);
	}
}
