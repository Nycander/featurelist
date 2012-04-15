package name.mnyc.featurelist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation which describes a feature, should be placed in front of JUnit integration test
 * cases.
 * 
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FeatureDescription
{
	String value();
	String category() default "General";
}
