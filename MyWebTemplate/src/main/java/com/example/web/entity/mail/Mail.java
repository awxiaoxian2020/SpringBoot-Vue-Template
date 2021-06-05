package com.example.web.entity.mail;

import lombok.Data;

@Data
public abstract class Mail {
    final String destination;
    String title;
    String context;
}
