import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.apache.ftpserver.ftplet.FtpException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws ParseException, IOException, TelegramApiRequestException, FtpException {
        Options commandLineOptions = new Options();
        commandLineOptions.addOption(
                Option.builder("c").longOpt("config").desc("path to config file").hasArg().required().build());

        CommandLine commandLine = new DefaultParser().parse(commandLineOptions, args);
        ObjectMapper objectMapper = new ObjectMapper();

        Config config = objectMapper.readValue(new File(commandLine.getOptionValue("c")), Config.class);

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        telegramBotsApi.registerBot(new Bot(config));

    }

}
