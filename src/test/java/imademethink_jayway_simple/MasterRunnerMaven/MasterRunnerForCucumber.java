package imademethink_jayway_simple.MasterRunnerMaven;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/test/resources",
        glue = "imademethink_jayway_simple.StepDefinitions",
		dryRun = false,
		strict = false,
		monochrome = true,
		//tags 			= {"@tag1"},
        plugin = {
                    "pretty",
                    "html:target/cucumber",
                } 
        )
public class MasterRunnerForCucumber {
	@BeforeClass
	public static void beforeClass() {
		System.out.println("*************************************************");		
	}
	
	@AfterClass
	public static void afterClass() {
		System.out.println("*************************************************");		
	}
	
}

