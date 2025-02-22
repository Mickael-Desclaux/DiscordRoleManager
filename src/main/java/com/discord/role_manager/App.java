package com.discord.role_manager;

import com.discord.role_manager.config.Config;
import com.discord.role_manager.listeners.RoleManagementListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class App
{
    private final JDA jda;
    private String token = Config.getToken();
    private String welcomeMessageId = Config.getWelcomeMessageId();
    private String presentationChannelId = Config.getPresentationChannelId();

    public App() {
        this.jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(new RoleManagementListener(token, welcomeMessageId, presentationChannelId))
                .setActivity(Activity.playing("C'est un test"))
                .build();
    }

    public static void main( String[] args )
    {
        new App();
    }
}