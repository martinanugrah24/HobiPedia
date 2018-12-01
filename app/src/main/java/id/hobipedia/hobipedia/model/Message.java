package id.hobipedia.hobipedia.model;

import static id.hobipedia.hobipedia.util.Constant.DEFAULT.DEFAULT_NOT_SET;

public class Message {

    private String messageId = DEFAULT_NOT_SET;
    private String ownerId = DEFAULT_NOT_SET;
    private String message = DEFAULT_NOT_SET;

    public Message() {
    }

    public Message(String messageId, String ownerId, String message) {
        this.messageId = messageId;
        this.ownerId = ownerId;
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
