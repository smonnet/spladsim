/**
 * 
 */
package spladsim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.simgrid.msg.Msg;

/**
 * @author smonnet
 *
 */
public class Configuration {
	// spladsim params
	double maintenancePeriod;
	int selectionRange;
	int placementPolicie;
	// application specific
	long fileSize;
	long nbFiles;
	int replFactor;
	double mtbf;
	// simulation specific
	double endTime;
	int seed;
	double observationPeriod;
	FileWriter gnuplotFile;
	
	public Configuration(String configFile) {
		Properties properties = new Properties();
		String buf = null;
		try {
			properties.load(new FileInputStream(configFile));
			Msg.info("***** ***** CONFIGURATION ***** *****");
			
			buf = properties.getProperty("maintenancePeriod");
			maintenancePeriod = Double.parseDouble(buf);
			Msg.info("maintenancePeriod::"+maintenancePeriod);
			
			buf = properties.getProperty("selectionRange");
			selectionRange = Integer.parseInt(buf);
			if(selectionRange%2==0) { // Selection range should be odd (contains the root noe)
				selectionRange=selectionRange+1;
				Msg.info("WARNING -- the given selection range was even, it has to be odd");
			}
			Msg.info("selectionRange::"+selectionRange);

			buf = properties.getProperty("placementPolicie");
			placementPolicie = Integer.parseInt(buf);
			Msg.info("placementPolicie::"+placementPolicie);
			
			buf = properties.getProperty("fileSize");
			fileSize = Long.parseLong(buf);
			Msg.info("fileSize::"+fileSize);
			
			buf = properties.getProperty("nbFiles");
			nbFiles = Long.parseLong(buf);
			Msg.info("nbFiles::"+nbFiles);
			
			buf = properties.getProperty("replFactor");
			replFactor = Integer.parseInt(buf);
			if(replFactor > selectionRange) {
				replFactor = selectionRange;
				Msg.info("WARNING -- the given replication factor was greater then the selection range");
			}
			Msg.info("replFactor::"+replFactor);
			
			buf = properties.getProperty("mtbf");
			mtbf = Double.parseDouble(buf);
			Msg.info("mtbf::"+mtbf);
			
			buf = properties.getProperty("endTime");
			endTime = Double.parseDouble(buf);
			Msg.info("endTime::"+endTime);
			
			buf = properties.getProperty("seed");
			seed = Integer.parseInt(buf);
			Msg.info("seed::"+seed);
			
			buf = properties.getProperty("observationPeriod");
			observationPeriod = Double.parseDouble(buf);
			Msg.info("observationPeriod::"+observationPeriod);
			
			buf = properties.getProperty("gnuplotFile");
			gnuplotFile = new FileWriter(new File(buf));
			Msg.info("gnuplotFile::"+buf);

		} catch (IOException e) {
			Msg.info("Error reading configuration file : " + configFile);
			e.printStackTrace();
		} catch (NumberFormatException n) {
			Msg.info("Error while trying to convert to seeg to int : " + buf);
		}
	}
}
