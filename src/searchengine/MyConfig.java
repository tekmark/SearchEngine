package searchengine;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.List;

/**
 * Created by chaohan on 6/9/16.
 */
public class MyConfig {
    private final static String DEFAULT_CONFIG_FILE_NAME = "defaultconfig.xml";
    private Configurations mConfigs;
    XMLConfiguration mXMLConfig;

    public MyConfig() {
        mConfigs = new Configurations();
    }

    public void loadDefault() {
        try {
            mXMLConfig = mConfigs.xml(DEFAULT_CONFIG_FILE_NAME);
//            String stage = config.getString("processing[@stage]");
//            List<String> paths = config.getList(String.class, "processing.paths.path");
//            for (String s : paths) {
//                System.out.println(s);
//            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void loadConfigFile(String filepath) {
        try {
            mXMLConfig = mConfigs.xml(filepath);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getDefaultConfigFilename () {
        return mXMLConfig.getString("default-config-filename");
    }

//    public String getAllConfig() {
//
//    }
}
