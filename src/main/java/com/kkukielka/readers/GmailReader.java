package com.kkukielka.readers;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class GmailReader implements Reader {
    Dotenv dotenv = Dotenv.load();
    Properties props = new Properties();
    String host = "imap.gmail.com";
    String username = dotenv.get("GMAIL_USERNAME");
    String appPassword = dotenv.get("GMAIL_PASSWORD");

    public GmailReader() {
         this.props.put("mail.store.protocol", "imaps");
         this.props.put("mail.imaps.host", host);
         this.props.put("mail.imaps.port", "993");
         this.props.put("mail.imaps.ssl.enable", "true");
     }

    @Override
    public List<String> getEmailsContent() {
        Session session = Session.getInstance(props);
        Store store;
        List<String> messages;

        try {
            store = session.getStore("imaps");
            store.connect(host, username, appPassword);

            Folder inbox = store.getFolder("[Gmail]/Spam");
            inbox.open(Folder.READ_ONLY);

            messages = mapMessages(inbox);

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return messages;
    }

    private List<String> mapMessages(Folder folder) throws MessagingException {
        Message[] messages = folder.getMessages();
        int total = messages.length;
        int fromIndex = Math.max(0, total - 5);

        return Arrays.stream(folder.getMessages(), fromIndex, total)
                .map(message -> {
                    try {
                        return String.format("Subject: %s\nFrom: %s\nContent: %s\nDate: %s",
                                message.getSubject(),
                                message.getFrom()[0],
                                mapContent(message),
                                message.getSentDate());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private String mapContent(Message message) throws Exception {
        Object content = message.getContent();

        if (content instanceof String) {
            return content.toString();
        }

        if (content instanceof Multipart) {
            return getTextFromMultipart((Multipart) content);
        }

        throw new RuntimeException("Unknown message Content type: " + message.getContent().getClass());
    }

    private String getTextFromMultipart(Multipart multipart) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);

            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                continue;
            }

            if (part.isMimeType("text/plain")) {
                return part.getContent().toString();
            } else if (part.isMimeType("text/html")) {
                String html = (String) part.getContent();
                return html.replaceAll("<[^>]+>", "");
            } else if (part.getContent() instanceof Multipart) {
                return getTextFromMultipart((Multipart) part.getContent());
            }
        }

        return "[No readable content]";
    }

}
