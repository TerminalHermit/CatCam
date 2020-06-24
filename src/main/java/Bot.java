import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class Bot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Config config;

    private FtpServer ftpServer;

    public Bot(Config config) throws IOException, FtpException {
        this.config = config;
        Path ftpRootDir = Files.createTempDirectory("cc");
        PropertiesUserManagerFactory propertiesUserManagerFactory = new PropertiesUserManagerFactory();
        UserManager userManager = propertiesUserManagerFactory.createUserManager();
        BaseUser baseUser = new BaseUser();
        baseUser.setName(config.getFtpUsername());
        baseUser.setPassword(config.getFtpPassword());
        baseUser.setHomeDirectory(ftpRootDir.toAbsolutePath().toString());
        baseUser.setAuthorities(Collections.singletonList(new WritePermission()));
        userManager.save(baseUser);
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(config.getFtpPort());
        FtpServerFactory ftpServerFactory = new FtpServerFactory();
        ftpServerFactory.setUserManager(userManager);
        ftpServerFactory.addListener("default", listenerFactory.createListener());
        ftpServerFactory.setFtplets(Collections.singletonMap("main", new DefaultFtplet() {
            @Override
            public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
                Path pathToFile = ftpRootDir.resolve(session.getFileSystemView().getWorkingDirectory().getAbsolutePath().substring(1)
                        + request.getArgument());
                logger.info("new file was successfully uploaded: " +
                        request.getArgument() + " located in " +
                        session.getFileSystemView().getWorkingDirectory().getAbsolutePath());
                System.out.println(pathToFile.toString());
                for (String chatId : config.getAllowedChatIds()) {
                    SendPhoto photo = new SendPhoto().setChatId(chatId);
                    logger.info("sending " + request.getArgument() + " to chatid: " + chatId);
                    try(FileInputStream fileInputStream = new FileInputStream(pathToFile.toFile())) {
                        photo.setPhoto(request.getArgument(), fileInputStream);
                        try {
                            execute(photo);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Files.delete(pathToFile);
                return super.onUploadEnd(session, request);
            }
        }));
        FtpServer server = ftpServerFactory.createServer();
        server.start();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().hasText()) {
            logger.info("received text message from " +
                    update.getMessage().getChatId() +
                    " with: " + update.getMessage().getText());
        }
    }

    @Override
    public String getBotUsername() {
        return "CC";
    }

    @Override
    public String getBotToken() {
        return config.getTelegramToken();
    }
}
