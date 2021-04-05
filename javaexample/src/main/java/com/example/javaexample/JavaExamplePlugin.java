package com.example.javaexample;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import java.util.HashMap;
import java.util.Map;

@Extension
@PluginDescriptor(
	name = "Java example",
	description = "Java example"
)
@Slf4j
public class JavaExamplePlugin extends Plugin
{

	// Injects our config
	@Inject
	private JavaExampleConfig config;

	// Provides our config
	@Provides
	JavaExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(JavaExampleConfig.class);
	}

	@Override
	protected void startUp()
	{
		// runs on plugin startup
		log.info("Plugin started");

		// example how to use config items
		if (config.example())
		{
			// do stuff
			log.info("The value of 'config.example()' is ${config.example()}");
		}
	}

	@Override
	protected void shutDown()
	{
		// runs on plugin shutdown
		log.info("Plugin stopped");
	}

	@Subscribe
	private void onGameTick(GameTick gameTick)
	{
		// runs every gametick
		log.info("Gametick");
	}
}