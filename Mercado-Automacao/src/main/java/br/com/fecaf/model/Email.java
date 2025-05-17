package br.com.fecaf.model;


public record Email(String to, String subject, String body){


    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }


}
