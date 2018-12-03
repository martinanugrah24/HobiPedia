package id.hobipedia.hobipedia.model;

import static id.hobipedia.hobipedia.util.Constant.DEFAULT.DEFAULT_NOT_SET;
import static id.hobipedia.hobipedia.util.Constant.DEFAULT.DEFAULT_NOT_SET_INT;

public class GroupChat {

    private String groupChatId = DEFAULT_NOT_SET;
    private String eventId = DEFAULT_NOT_SET;
    //    private int members = DEFAULT_NOT_SET_INT;
    private Message messages;

    public GroupChat() {
    }

    public GroupChat(String groupChatId, String eventId, Message messages) {
        this.groupChatId = groupChatId;
        this.eventId = eventId;
        this.messages = messages;
    }

    public String getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(String groupChatId) {
        this.groupChatId = groupChatId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Message getMessages() {
        return messages;
    }

    public void setMessages(Message messages) {
        this.messages = messages;
    }
}
