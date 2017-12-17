package org.company.retail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty3Plugin;

public class EmbeddedElasticsearchServer {

	private static final String DEFAULT_DATA_DIRECTORY = "target/elasticsearch-data";

	private final Node node;

	private static final Integer PORT = 9700;
	public static final String HOST = "http://127.0.0.1:" + PORT;

	public EmbeddedElasticsearchServer() throws NodeValidationException {

		this.deleteDataDirectory();

		Settings settings = Settings.builder()
									.put("http.enabled", "true")
									.put("http.port", PORT)
									.put("path.home", ".")
									.put("transport.type", "local")
									.put("http.type", "netty3")
									.put("path.data", DEFAULT_DATA_DIRECTORY)
									.build();

		Collection plugins = Arrays.asList(Netty3Plugin.class);
		node = new PluginConfigurableNode(settings, plugins).start();

	}

	public Node getNode() {
		return this.node;
	}

	public void shutdown() throws IOException {
		node.close();
		deleteDataDirectory();
	}

	private void deleteDataDirectory() {
		FileUtils.deleteQuietly(new File(DEFAULT_DATA_DIRECTORY));
	}

	private static class PluginConfigurableNode extends Node {
		public PluginConfigurableNode(Settings settings, Collection<Class<? extends Plugin>> classpathPlugins) {
			super(InternalSettingsPreparer.prepareEnvironment(settings, null), classpathPlugins);
		}
	}

}
