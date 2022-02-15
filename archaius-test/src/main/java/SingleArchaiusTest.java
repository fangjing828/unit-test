import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import model.NacosConfiguration;

import java.util.concurrent.TimeUnit;

/**
 * Created by fangjing 2022-01-07.
 */
public class SingleArchaiusTest {
//    private static final String namespace = "mcloud.middleware.sdk";
    private static final String namespace = "94621df7-a062-41a1-8a5c-de2bdb17a300";
    private static final String default_group = "DEFAULT_GROUP";

    public static void main(String[] args) throws Exception {
        String address = "10.85.247.135:8848";
        ConcurrentCompositeConfiguration compositeConfiguration = new ConcurrentCompositeConfiguration();
        compositeConfiguration.addConfiguration(new NacosConfiguration(address, namespace, "reserved.properties", default_group));
        compositeConfiguration.addConfiguration(new NacosConfiguration(address, namespace, "sdk.properties", "com.sankuai.finrisk.saas.approvemis"));
        compositeConfiguration.addConfiguration(new NacosConfiguration(address, namespace, "default.properties", default_group));


        ConfigurationManager.install(compositeConfiguration);

        DynamicStringProperty property = DynamicPropertyFactory.getInstance().getStringProperty("key", null);
        property.addCallback(() -> {
            System.out.println(property.get());
        });
        System.out.println(property.get());
        TimeUnit.MINUTES.sleep(30);
    }
}
