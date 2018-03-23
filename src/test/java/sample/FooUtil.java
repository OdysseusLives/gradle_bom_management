package sample;

import org.apache.cassandra.utils.OutputHandler.SystemOutput;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

public class FooUtil extends AbstractTestNGSpringContextTests {
    protected void output(){
		new SystemOutput(true, true).output("foo");
    }
}
