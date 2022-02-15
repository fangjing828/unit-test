import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import model.NacosConfiguration;
import org.apache.commons.configuration.AbstractConfiguration;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created by fangjing 2022-01-07.
 */
public class MultipleArchaiusTest {
    private static final String namespace = "45a92bab-9150-4459-a297-d16965d5a410";
    private static final String default_group = "DEFAULT_GROUP";

    public static void main(String[] args) throws Exception {
        String address = "10.85.247.135:8848";
        {
            ConcurrentCompositeConfiguration compositeConfiguration = new ConcurrentCompositeConfiguration();
            compositeConfiguration.addConfiguration(new NacosConfiguration(address, namespace, "reserved.properties", default_group));
            compositeConfiguration.addConfiguration(new NacosConfiguration(address, namespace, "sdk.properties", "com.sankuai.finrisk.saas.approvemis"));
            compositeConfiguration.addConfiguration(new NacosConfiguration(address, namespace, "default.properties", default_group));

            ConfigurationManager.install(compositeConfiguration);

            System.out.println(DynamicPropertyFactory.class.getClassLoader());

            DynamicStringProperty property = DynamicPropertyFactory.getInstance().getStringProperty("key", null);
            property.addCallback(() -> {
                System.out.println(property.get());
            });
            System.out.println(property.get());
        }

        {
            ArchaiusClassLoader classLoader = ArchaiusClassLoader.getInstance();
            Class<?> dynamicPropertyFactoryClazz = classLoader.loadClass(DynamicPropertyFactory.class.getName());
            Class<?> configurationManagerClazz = classLoader.loadClass(ConfigurationManager.class.getName());
            ConcurrentCompositeConfiguration compositeConfiguration = new ConcurrentCompositeConfiguration();
            compositeConfiguration.addConfiguration(new NacosConfiguration(address, namespace, "default.properties", default_group));

            Method installMethod = configurationManagerClazz.getDeclaredMethod("install", AbstractConfiguration.class);
            installMethod.invoke(configurationManagerClazz, compositeConfiguration);

            Method getInstanceMethod = dynamicPropertyFactoryClazz.getDeclaredMethod("getInstance");
            Object factory = getInstanceMethod.invoke(dynamicPropertyFactoryClazz);

            Method getStringPropertyMethod = dynamicPropertyFactoryClazz.getDeclaredMethod("getStringProperty", String.class, String.class);


            Object property = getStringPropertyMethod.invoke(factory, "key", null);

            System.out.println(property);
        }
        TimeUnit.MINUTES.sleep(30);

    }
}
