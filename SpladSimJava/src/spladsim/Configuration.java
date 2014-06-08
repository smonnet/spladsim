/**
 * 
 */
package spladsim;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.simgrid.msg.Msg;

/**
 * @author smonnet
 *
 */
public class Configuration {

	int seed;

	public Configuration(String configFile) {
		Properties properties = new Properties();
		String buf = null;
		try {
			properties.load(new FileInputStream(configFile));
			buf = properties.getProperty("seed");
		} catch (IOException e) {
			Msg.info("Error reading configuration file : " + configFile);
			e.printStackTrace();
		}
		// convertion to long
		try {
			seed = Integer.parseInt(buf);
		} catch (NumberFormatException n) {
			Msg.info("Error while trying to convert to seeg to int : " + buf);
		}
	}
}
