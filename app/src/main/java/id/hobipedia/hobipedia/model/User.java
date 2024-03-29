package id.hobipedia.hobipedia.model;

public class User {

    private String userId;
    private String nama;
    private String email;
    private String alamat;
    private String photoUrl;

    public User() {
    }

    public User(String userId, String nama, String email, String alamat, String photoUrl) {
        this.userId = userId;
        this.nama = nama;
        this.email = email;
        this.alamat = alamat;
        this.photoUrl = photoUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
