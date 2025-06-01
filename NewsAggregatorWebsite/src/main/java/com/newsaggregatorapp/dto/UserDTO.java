package com.newsaggregatorapp.dto;

public class UserDTO {
    private Long id;
    private String username;
    private String preferences;

    public UserDTO() {
    }

    public UserDTO(Long id, String username, String preferences) {
        this.id = id;
        this.username = username;
        this.preferences = preferences;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
