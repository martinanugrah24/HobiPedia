package id.hobipedia.hobipedia.model;

import java.util.ArrayList;

import static id.hobipedia.hobipedia.util.Constant.DEFAULT.DEFAULT_NOT_SET;
import static id.hobipedia.hobipedia.util.Constant.DEFAULT.DEFAULT_NOT_SET_DOUBLE;
import static id.hobipedia.hobipedia.util.Constant.DEFAULT.DEFAULT_NOT_SET_INT;

public class Event {

    private String address = DEFAULT_NOT_SET;
    private String category = DEFAULT_NOT_SET;
    private String description = DEFAULT_NOT_SET;
    private String eventId = DEFAULT_NOT_SET;
    private double latitude = DEFAULT_NOT_SET_DOUBLE;
    private double longitude = DEFAULT_NOT_SET_DOUBLE;
    private String name = DEFAULT_NOT_SET;
    private String ownerId = DEFAULT_NOT_SET;
    private String phone = DEFAULT_NOT_SET;
    private String photoUrl = DEFAULT_NOT_SET;
    private String date = DEFAULT_NOT_SET;
    private String time = DEFAULT_NOT_SET;
    private int maxMember = DEFAULT_NOT_SET_INT;
    private int minMember = DEFAULT_NOT_SET_INT;
    private ArrayList<String> members = new ArrayList<>();

    public Event() {
    }

    public Event(String address, String category, String description, String eventId, double latitude, double longitude, String name, String ownerId, String phone, String photoUrl, String date, String time, int maxMember, int minMember, ArrayList<String> members) {
        this.address = address;
        this.category = category;
        this.description = description;
        this.eventId = eventId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.ownerId = ownerId;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.date = date;
        this.time = time;
        this.maxMember = maxMember;
        this.minMember = minMember;
        this.members = members;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }

    public int getMinMember() {
        return minMember;
    }

    public void setMinMember(int minMember) {
        this.minMember = minMember;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }
}
