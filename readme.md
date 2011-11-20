Feature list compiler
=====================

For projects using strict TDD will have a test for every feature. Not every test will reflect one feature (many JUnit tests won't) but some will, or will partly. Projects with this feature can use this utility to mark up what test case tests what feature, and then by using JUnit we can compile a feature list by seeing what features have successful tests.

An Example
==========

```java
// Car.java
public class Car {
	
	public void accelerate() {
		// ...
	}
}
// CarTest.java
public class CarTest {
	@Test
	@FeatureDescription("A car can accelerate", "Car")
	public final void testAccelerate() {
		// test the accelerate method
	}
}
```

Then, by running an execution of the FeatureListCompiler you can get an output like this: (assuming the test succeded)

```
Feature list 2011-nov-20
========================
Car
 + A car can accelerate.
```

Compiling a changelist for every release is then as simple as computing the diff between two feature lists.