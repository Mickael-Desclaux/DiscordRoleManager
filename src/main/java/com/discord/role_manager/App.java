package com.discord.role_manager;

import com.discord.role_manager.listeners.RoleManagementListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class App
{
    Dotenv dotenv = Dotenv.load();
    private final JDA jda;
    private String token = dotenv.get("BOT_TOKEN");
    private String welcomeMessageId = dotenv.get("WELCOME_MESSAGE_ID");
    private String presentationChannelId = dotenv.get("PRESENTATION_CHANNEL_ID");

    public App() {
        this.jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(new RoleManagementListener(token, welcomeMessageId, presentationChannelId))
                .build();
    }

    public static void main( String[] args )
    {
        new App();
    }
}