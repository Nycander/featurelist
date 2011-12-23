package name.mnyc.featurelist;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Feature
{
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

	private final String description;
	private boolean working;
	private String string;

	/**
	 * Creates a working feature.
	 * 
	 * @param description
	 *            the description of the feature.
	 */
	public Feature(String description)
	{
		this(description, true);
	}

	public Feature(String description, boolean working)
	{
		this.description = description;
		this.working = working;
	}
	
	public boolean isWorking()
	{
		return working;
	}

	@Override
	public String toString()
	{
		if (string != null)
			return string;

		StringBuilder sb = new StringBuilder();
		sb.append(isWorking() ? '+' : '-');
		sb.append(' ');
		sb.append(isWorking() ? getDescription() : getOppositeDescription());
		
		return string = sb.toString();
	}
	
	private String getOppositeDescription()
	{
		String desc = getDescription();
		for (Entry<String, String> entry : negationReplacement.entrySet())
		{
			desc = desc.replace(entry.getKey(), entry.getValue());
		}
		return desc;
	}

	public String getDescription()
	{
		return description;
	}
	
	public void setNotWorking()
	{
		working = false;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (working ? 1231 : 1237);
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature other = (Feature) obj;
		if (description == null)
		{
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (working != other.working)
			return false;
		return true;
	}
}
