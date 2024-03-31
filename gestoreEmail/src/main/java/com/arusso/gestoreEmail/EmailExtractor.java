package com.arusso.gestoreEmail;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class EmailExtractor {

    public static void main(String[] args) {
        //String emlFilePath = "C:\\dev\\pectest\\mail_FAHGVXL_attachments\\postacert.eml";
        //String outputFilePath = "C:\\dev\\pectest\\outputdaecli.txt";
    	if(args.length < 2) {
            System.out.println("Usage: EmailExtractor <inputEmlPath> <outputFilePath>");
            return;
        }
        String emlFilePath = args[0];
        String outputFilePath = args[1];
        try {
            extractAndSaveEmailDetails(emlFilePath, outputFilePath);
            System.out.println("Dettagli email estratti con successo!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void extractAndSaveEmailDetails(String emlFilePath, String outputFilePath) throws Exception {
        Session session = Session.getDefaultInstance(new Properties());
        InputStream is = new FileInputStream(emlFilePath);
        Message message = new MimeMessage(session, is);

        String from = String.valueOf(message.getFrom()[0]);
        String subject = message.getSubject();
        String content = getTextFromMessage(message);

        String output = "Mittente: " + from + "\nOggetto: " + subject + "\nCorpo:\n" + content;

        Files.write(Paths.get(outputFilePath), output.getBytes());
    }

    private static String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return (String) message.getContent();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();

                if (disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {
                    continue; // attachment
                } else {
                    String result = getTextFromBodyPart(bodyPart);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return "";
    }

    private static String getTextFromBodyPart(BodyPart bodyPart) throws Exception {
        if (bodyPart.isMimeType("text/plain")) {
            return (String) bodyPart.getContent();
        } else if (bodyPart.isMimeType("text/html")) {
            String html = (String) bodyPart.getContent();
            return org.jsoup.Jsoup.parse(html).text();
        } else if (bodyPart.getContent() instanceof MimeMultipart){
            return getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
        }
        return null;
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }
}

