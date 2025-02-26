package com.discord.role_manager;

import com.discord.role_manager.listeners.RoleManagementListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class App
{
    private final JDA jda;
    private String token = System.getenv("BOT_TOKEN");
    private String welcomeMessageId = System.getenv("WELCOME_MESSAGE_ID");
    private String presentationChannelId = System.getenv("PRESENTATION_CHANNEL_ID");
    private String welcomeChannelId = System.getenv("WELCOME_CHANNEL_ID");

    public App() {
        this.jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS
                )
                .addEventListeners(new RoleManagementListener(welcomeMessageId, welcomeChannelId, presentationChannelId))
                .build();
    }

    public static void main( String[] args )
    {
        new App();
    }
}