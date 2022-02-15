package model;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigChangeEvent;
import com.alibaba.nacos.api.config.ConfigChangeItem;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.PropertyChangeType;
import com.alibaba.nacos.client.config.listener.impl.AbstractConfigChangeListener;
import com.alibaba.nacos.client.config.utils.SnapShotSwitch;
import com.alibaba.nacos.common.utils.StringUtils;
import com.netflix.config.ConcurrentMapConfiguration;
import com.netflix.config.DynamicPropertyUpdater;
import com.netflix.config.WatchedUpdateResult;

import java.io.Closeable;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fangjing 2022-01-07.
 */
public class NacosConfiguration extends ConcurrentMapConfiguration implements Closeable {
    private ConfigService configService;
    private final DynamicPropertyUpdater updater = new DynamicPropertyUpdater();

    public NacosConfiguration(String serverAddr, String nameSpace, String dataId, String group) throws Exception {
        setDelimiterParsingDisabled(true);
        Properties nacosServerProperties = new Properties();
        nacosServerProperties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        nacosServerProperties.put(PropertyKeyConst.NAMESPACE, nameSpace);
        //禁用本地文件缓存
        SnapShotSwitch.setIsSnapShot(false);
        //初始化nacos客户端
        configService = NacosFactory.createConfigService(nacosServerProperties);

        NacosConfigListener listener = new NacosConfigListener();
        //加载配置
        String defaultConfig = configService.getConfigAndSignListener(dataId, group, 3000L, listener);
        if (StringUtils.isNotBlank(defaultConfig)) {
            Properties config = new Properties();
            config.load(new StringReader(defaultConfig));
            updater.updateProperties(WatchedUpdateResult.createFull(Collections.unmodifiableMap(new HashMap<String, Object>((Map) config))), NacosConfiguration.this, false);
        }
    }

    @Override
    public void close() {
        if (this.configService != null) {
            try {
                this.configService.shutDown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class NacosConfigListener extends AbstractConfigChangeListener {
        @Override
        public void receiveConfigChange(ConfigChangeEvent event) {
            try {
                Collection<ConfigChangeItem> changeItems = event.getChangeItems();
                Map<String, Object> added = changeItems.stream().filter(configChangeItem -> configChangeItem.getType() == PropertyChangeType.ADDED)
                        .collect(Collectors.toMap(ConfigChangeItem::getKey, ConfigChangeItem::getNewValue, (key1, key2) -> key2));

                Map<String, Object> changed = changeItems.stream().filter(configChangeItem -> configChangeItem.getType() == PropertyChangeType.MODIFIED)
                        .collect(Collectors.toMap(ConfigChangeItem::getKey, ConfigChangeItem::getNewValue, (key1, key2) -> key2));

                Map<String, Object> deleted = changeItems.stream().filter(configChangeItem -> configChangeItem.getType() == PropertyChangeType.DELETED)
                        .collect(Collectors.toMap(ConfigChangeItem::getKey, ConfigChangeItem::getOldValue, (key1, key2) -> key2));

                WatchedUpdateResult updateResult = WatchedUpdateResult.createIncremental(added, changed, deleted);
                updater.updateProperties(updateResult, NacosConfiguration.this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
