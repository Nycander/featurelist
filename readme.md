Feature List Compiler
=====================

For projects using strict TDD will have a test for every feature. Not every test will reflect one feature (many JUnit tests won't) but some will or a collection of tests will. Projects with this feature can use this utility to mark up what test case tests what feature, and then by using JUnit we can compile a feature list by seeing what features have successful tests.

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

Then, by running a JUnit test run with the FeatureListCompiler added to the list of Listeners, example: `junitcore.addListener(new FeatureListCompiler());`. Well, assuming the test actually succeded.

```
Feature list 2011-nov-20
========================
Car
 + A car can accelerate.
```

Compiling a changelist for every release is then as simple as computing the diff between two feature lists.

Incorporating this into your project kind of forces you to create a test case for every new bug and feature, which is a good thing. A bug fix won't show up in the changelist unless its test passes.