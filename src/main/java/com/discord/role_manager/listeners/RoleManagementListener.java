package com.discord.role_manager.listeners;
import com.discord.role_manager.enums.UserRole;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleManagementListener extends ListenerAdapter {

    private final String WELCOME_MESSAGE_ID;
    private final String PRESENTATION_CHANNEL_ID;
    private final String WELCOME_EMOJI = "✅";

    public RoleManagementListener(String token, String welcomeMessageId, String presentationChannelId) {
        this.WELCOME_MESSAGE_ID = welcomeMessageId;
        this.PRESENTATION_CHANNEL_ID = presentationChannelId;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getMessageId().equals(WELCOME_MESSAGE_ID)) {
            if (event.getEmoji().getName().equals(WELCOME_EMOJI)) {
                net.dv8tion.jda.api.entities.Role newRole = event.getGuild().getRolesByName(UserRole.NOUVEAU.toString(), true).stream()
                        .findFirst()
                        .orElseGet(() -> event.getGuild().createRole()
                                .setName(UserRole.NOUVEAU.toString())
                                .complete());

                event.getGuild().addRoleToMember(event.getMember(), newRole).queue(
                        success -> {
                            event.getUser().openPrivateChannel().queue(channel ->
                                    channel.sendMessage("Bienvenue sur le serveur In Progress ! N'oublies pas " +
                                            "de te présenter dans le channel #présentation pour obtenir le rôle de " +
                                            "membre et accéder à l'intégralité des fonctionnalités du serveur !").queue());
                        },
                        error -> System.out.println("Erreur lors de l'ajout du rôle : " + error.getMessage())
                );
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getId().equals(PRESENTATION_CHANNEL_ID)) {
            if (event.getMessage().getContentRaw().length() >= 50) {
                net.dv8tion.jda.api.entities.Role memberRole = event.getGuild().getRolesByName(UserRole.MEMBRE.toString(), true).stream()
                        .findFirst()
                        .orElseGet(() -> event.getGuild().createRole()
                                .setName(UserRole.MEMBRE.toString())
                                .complete());

                Role newRole = event.getGuild().getRolesByName(UserRole.NOUVEAU.toString(), true).stream()
                        .findFirst()
                        .orElse(null);

                if (newRole != null) {
                    event.getGuild().removeRoleFromMember(event.getMember(), newRole).queue();
                } else {
                    System.out.println("Le rôle 'Nouveau' est introuvable.");
                }

                event.getGuild().addRoleToMember(event.getMember(), memberRole).queue(
                        success -> {
                            event.getAuthor().openPrivateChannel().queue(
                                    channel -> channel.sendMessage("Félicitation ! Tu as maintenant " +
                                            "accès à l'intégralité du serveur In Progress !").queue()
                            );
                        },
                        error -> System.out.println("Erreur lors de l'ajout du rôle : " + error.getMessage())
                );
            }
        }
    }
}
