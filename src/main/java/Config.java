import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class Config {

    @JsonProperty("telegramToken")
    private String telegramToken;

    @JsonProperty("ftpPort")
    private Integer ftpPort;

    @JsonProperty("ftpUsername")
    private String ftpUsername;

    @JsonProperty("ftpPassword")
    private String ftpPassword;

    @JsonProperty("allowedChatIds")
    private String allowedChatIds;

    @JsonCreator
    public Config(@JsonProperty("telegramToken") String telegramToken,
                  @JsonProperty("ftpPort") Integer ftpPort,
                  @JsonProperty("ftpUsername") String ftpUsername,
                  @JsonProperty("ftpPassword") String ftpPassword,
                  @JsonProperty("allowedChatIds") String allowedChatIds) {
        this.telegramToken = Objects.requireNonNull(telegramToken);
        this.ftpPort = Objects.requireNonNull(ftpPort);
        this.ftpUsername = Objects.requireNonNull(ftpUsername);
        this.ftpPassword = Objects.requireNonNull(ftpPassword);
        this.allowedChatIds = Objects.requireNonNull(allowedChatIds);
    }

    @JsonGetter("telegramToken")
    public String getTelegramToken() {
        return telegramToken;
    }

    @JsonGetter("ftpPort")
    public Integer getFtpPort() {
        return ftpPort;
    }

    @JsonGetter("ftpUsername")
    public String getFtpUsername() {
        return ftpUsername;
    }

    @JsonGetter("ftpPassword")
    public String getFtpPassword() {
        return ftpPassword;
    }

    @JsonIgnore
    public Collection<String> getAllowedChatIds() {
        return Arrays.asList(allowedChatIds.split("\\|"));
    }
}
