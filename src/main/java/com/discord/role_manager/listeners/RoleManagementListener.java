package com.discord.role_manager.listeners;
import com.discord.role_manager.enums.UserRole;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleManagementListener extends ListenerAdapter {

    private final String WELCOME_MESSAGE_ID;
    private final String WELCOME_CHANNEL_ID;
    private final String PRESENTATION_CHANNEL_ID;

    private final String ROLE_NEW_MESSAGE = "Bienvenue sur le serveur In Progress ! N'oublie pas de te présenter sur le " +
            "canal #présentation pour accéder à l'intégralité du serveur !";
    private final String ROLE_MEMBER_MESSAGE = "Félicitation, tu as maintenant accès à l'intégralité du serveur In Progress !";
    private final String WELCOME_EMOJI = "✅";

    public RoleManagementListener(String welcomeMessageId, String welcomeChannelId, String presentationChannelId) {
        this.WELCOME_MESSAGE_ID = welcomeMessageId;
        this.WELCOME_CHANNEL_ID = welcomeChannelId;
        this.PRESENTATION_CHANNEL_ID = presentationChannelId;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getMessageId().equals(WELCOME_MESSAGE_ID)) {
            if (event.getEmoji().getName().equals(WELCOME_EMOJI)) {
                net.dv8tion.jda.api.entities.Role newRole = event.getGuild().getRolesByName(UserRole.NOUVEAU.toString(),
                                true).stream()
                        .findFirst()
                        .orElseGet(() -> event.getGuild().createRole()
                                .setName(UserRole.NOUVEAU.toString())
                                .complete());

                event.getGuild().addRoleToMember(event.getMember(), newRole).queue(
                        success -> {
                            event.getUser().openPrivateChannel().queue(channel ->
                                    channel.sendMessage(ROLE_NEW_MESSAGE).queue());
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
                net.dv8tion.jda.api.entities.Role memberRole = event.getGuild().getRolesByName(UserRole.MEMBRE.toString(),
                                true).stream()
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
                                    channel -> channel.sendMessage(ROLE_MEMBER_MESSAGE).queue()
                            );
                        },
                        error -> System.out.println("Erreur lors de l'ajout du rôle : " + error.getMessage())
                );
            }
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member newMember = event.getMember();
        String username = newMember.getEffectiveName();
        String serverId = event.getGuild().getId();
        String welcomeChannelUrl = "https://discord.com/channels/" + serverId + "/" + WELCOME_CHANNEL_ID;

        String welcomeMessage = SendWelcomeMessage(username, welcomeChannelUrl);

        newMember.getUser().openPrivateChannel().queue(
                (PrivateChannel privateChannel) -> {
                    privateChannel.sendMessage(welcomeMessage).queue(
                            success -> System.out.println("Message de bienvenue envoyé à " + username),
                            error -> System.out.println("Erreur lors de l'envoi du message à " + username + ": " + error.getMessage())
                    );
                },
                error -> System.out.println("Impossible d'ouvrir un canal privé avec " + username + ": " + error.getMessage())
        );
    }

    private String SendWelcomeMessage(String username, String welcomeChannelUrl) {

        String message = String.format("\uD83C\uDF89 Bienvenue %s sur In Progress !\n\n" +
                        "\uD83D\uDD39 Pour accéder au serveur, il te suffit de réagir au message dans le salon **[#bienvenue](%s)**\n\n" +
                        "Clique sur %s pour obtenir le rôle Nouveau et accéder aux discussions.\n\n" +
                        "\uD83D\uDCAC Une fois que c'est fait, tu pourras te présenter dans #présentation afin d'accéder à l'ensemble du serveur !\n\n" +
                        "\uD83D\uDE80 Hâte de faire ta connaissance et d'échanger avec toi !",
                        username, welcomeChannelUrl, WELCOME_EMOJI);

        return message;
    }
}
