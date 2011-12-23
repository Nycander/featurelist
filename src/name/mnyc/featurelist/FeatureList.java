package name.mnyc.featurelist;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class FeatureList extends TreeMap<String, List<Feature>>
{
	private static final long serialVersionUID = 1L;

	private String title;

	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void addFeature(String category, String description)
	{
		List<Feature> list = get(category);
		if (list == null)
			list = new ArrayList<Feature>();
		
		if (findInList(list, description) != null)
			return;
		
		list.add(new Feature(description));
		put(category, list);
	}
	
	public Feature getFeature(FeatureDescription featdesc)
	{
		return getFeature(featdesc.category(), featdesc.value());
	}
	
	public Feature getFeature(String category, String description)
	{
		return findInList(get(category), description);
	}
	
	private Feature findInList(List<Feature> list, String description)
	{
		if (list == null)
			return null;

		for (Feature feat : list)
		{
			if (feat.getDescription().equals(description))
			{
				return feat;
			}
		}
		return null;
	}

	/**
	 * Build a list of features in a category.
	 * 
	 * @param bulletPoint
	 *            the type of bullet point to use in the list.
	 * @param category
	 *            the category to print features from.
	 * @return the compiled list.
	 */
	public String toString(String category)
	{
		final StringBuilder sb = new StringBuilder();
		for (final Feature feature : get(category))
		{
			if (!feature.isWorking())
				continue;
			
			sb.append(feature);
			sb.append('\n');
		}
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		out.append(title);
		out.append('\n');
		for (int i = 0; i < title.length(); i++)
			out.append('=');
		out.append('\n');
		
		for (final String category : keySet())
		{
			if (get(category).size() == 0)
				continue;
			
			final String features = toString(category);

			if (features.length() > 0)
			{
				out.append(category);
				out.append('\n');
				out.append(features);
				out.append('\n');
			}
		}
		return out.toString();
	}
}
