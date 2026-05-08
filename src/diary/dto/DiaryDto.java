package diary.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiaryDto {
    private static final DateTimeFormatter LIST_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private int id;
    private String title;
    private String content;
    private String weather;
    private LocalDateTime createdDate;

    public DiaryDto() {
    }

    public DiaryDto(String title, String content, String weather) {
        this.title = title;
        this.content = content;
        this.weather = weather;
    }

    public DiaryDto(int id, String title, String content, String weather, LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.weather = weather;
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        String dateText = "";
        if (createdDate != null) {
            dateText = createdDate.format(LIST_DATE_FORMATTER) + " ";
        }
        return dateText + title;
    }
}
