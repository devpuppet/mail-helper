package com.kkukielka;

import com.kkukielka.readers.GmailReader;
import com.kkukielka.readers.Reader;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Reader gmailReader = new GmailReader();
        List<String> emails = gmailReader.getEmailsContent();
        System.out.println(emails);
    }
}