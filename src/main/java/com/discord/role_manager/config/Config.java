package com.discord.role_manager.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error when loading config.properties", e);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Properties not found : " + key);
        }
        return value;
    }

    public static String getToken() {
        return getProperty("bot.token");
    }

    public static String getWelcomeMessageId() {
        return getProperty("welcome.message.id");
    }

    public static String getPresentationChannelId() {
        return getProperty("presentation.channel.id");
    }
}
