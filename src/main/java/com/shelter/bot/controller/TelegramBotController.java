package com.shelter.bot.controller;


import com.shelter.bot.config.BotConfiguration;
import com.shelter.bot.entity.PetEntity;
import com.shelter.bot.entity.ShelterEntity;
import com.shelter.bot.entity.UserEntity;
import com.shelter.bot.repository.PetRepository;
import com.shelter.bot.repository.RuleRepository;
import com.shelter.bot.repository.ShelterRepository;
import com.shelter.bot.repository.UserRepository;
import com.shelter.bot.utils.ButtonsNames;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TelegramBotController extends TelegramLongPollingBot {
    private final UserRepository userRepository;
    private final BotConfiguration configuration;
    private final ShelterRepository shelterRepository;
    private final PetRepository petRepository;
    private final RuleRepository ruleRepository;
    private final ShelterController shelterController;

    public TelegramBotController(UserRepository userRepository,
                                 BotConfiguration configuration,
                                 ShelterRepository shelterRepository,
                                 PetRepository petRepository,
                                 RuleRepository ruleRepository, ShelterController shelterController) {
        super(configuration.getToken());
        this.userRepository = userRepository;
        this.configuration = configuration;
        this.shelterRepository = shelterRepository;
        this.petRepository = petRepository;
        this.ruleRepository = ruleRepository;
        this.shelterController = shelterController;
    }

    /**
     * Основной метод взаимодействия бота с пользователем
     *
     * @param update
     */
    @Override
    public void onUpdateReceived(Update update) {
        Long chatId;
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            String name = update.getMessage().getChat().getFirstName();
            String text = update.getMessage().getText();
            UserEntity userEntity = new UserEntity().setChatId(chatId).setName(name);
            if (userRepository.findByChatId(chatId).isPresent()) {
                sendMessage(chatId, "И снова здравствуйте!" + userEntity.getName());
            } else {
                sendMessage(chatId, "Здравствуйте!" + userEntity.getName());
                userRepository.save(userEntity);
            }
            setStarMenuBot(chatId, "Главное меню");
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            String callBackData = update.getCallbackQuery().getData();
            if (callBackData.contains("SHELTERS_GROUP"))
                shelterController.executeCommand(callBackData, chatId);
//            } else if (callBackData.contains("SHELTERS")) {
//                Long shelterId = Long.parseLong(callBackData.split("_")[0]);
//                setShelterInfoMenu(chatId, "Что Вы хотите узнать?", shelterId);}
//             else if (callBackData.contains("INFO")) {
//                getInfoByShelterId(chatId, callBackData);
//            }
            if (callBackData.contains("PETS_GROUP"))
                shelterController.executeCommand(callBackData, chatId);
            if (callBackData.equals(ButtonsNames.GET_PET_FROM_SHELTER_BUTTON_DATA)) {
                getPetFromShelterMenu(chatId, "Выберите пункт: ");
//                getAllPetsMenu(chatId, "Наши питомцы");
            } else if (callBackData.contains("_PETS_BY_ID_")) {
                Long petId = Long.parseLong(callBackData.split("_")[0]);
                setPetInfoMenu(chatId, "Что хотите узнать?", petId);
            } else if (callBackData.contains("PET_INFO")) {
                getInfoByPetId(chatId, callBackData);
            } else if (callBackData.contains("GETPET")) {
                getPetsOrRecommendationsMenu(chatId, callBackData);

            }

        }
    }


    private void sendMessageWithKeyboard(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markup);
        executeMessage(chatId, text, sendMessage);
    }

    public void sendMessage(Long chatId, String text) {
        executeMessage(chatId, text, new SendMessage());
    }
    private void getPetsOrRecommendationsMenu(Long chatId, String callbackData) {
        if (callbackData.equals(ButtonsNames.OUR_PETS_BUTTON_DATA)){
            setAllPetsMenu(chatId, "Наши питомцы");
        }
        else {
            setRecommendationsMenu(chatId, "Выберите рекоммендацию: ");
        }
    }

    private void setRecommendationsMenu(Long chatId, String text) {
        List<List<String>> lists = new ArrayList<>();
        lists.add(List.of(ButtonsNames.RULES_DATING_PETS_BUTTON_NAME, ButtonsNames.RULES_DATING_PETS_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.DOCUMENTS_PETS_BUTTON_NAME, ButtonsNames.DOCUMENTS_PETS_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.REFUSAL_TO_ISSUE_ANIMAL_BUTTON_NAME, ButtonsNames.REFUSAL_TO_ISSUE_ANIMAL_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.TRANSPORTATION_RECOMMENDATIONS_BUTTON_NAME, ButtonsNames.TRANSPORTATION_RECOMMENDATIONS_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.RECOMMENDATION_HOUSE_BUTTON_NAME, ButtonsNames.RECOMMENDATION_HOUSE_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.RECOMMENDATIONS_DOG_HANDLER_BUTTON_NAME, ButtonsNames.RECOMMENDATIONS_DOG_HANDLER_BUTTON_DATA));
        setKeyboard(chatId, text, lists, 1);
    }


    private void getPetFromShelterMenu(Long chatId, String text) {
        List<List<String>> lists = new ArrayList<>();
        lists.add(List.of(ButtonsNames.RECOMMENDATIONS_BUTTON_NAME, ButtonsNames.RECOMMENDATIONS_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.OUR_PETS_BUTTON_NAME, ButtonsNames.OUR_PETS_BUTTON_DATA));
        setKeyboard(chatId, text, lists, 1);
    }

    /**
     * Меню отображения животных
     *
     * @param chatId ID чата
     * @param text   текст кнопок
     */
    private void getPetMenu(Long chatId, String text) {
        List<List<String>> lists = new ArrayList<>();
        lists.add(List.of(ButtonsNames.PET_NAME_BUTTON_NAME, ButtonsNames.PET_NAME_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.PET_AGE_BUTTON_NAME, ButtonsNames.PET_AGE_BUTTON_DATA));
        setKeyboard(chatId, text, lists, 1);
    }

    /**
     * Метод вывода всех животных приюта
     * <br/>
     * По циклу <b>информация из репозитория БД</b> {@link PetRepository} преобразуется в сущность {@link PetEntity}, где затем кладется в коллекцию {@link List}
     * <br/>
     * В цикле путем добавления из БД информации, создается меню <b>кнопок</b> {@code setKeyboard(chatId, text, lists, 2);}
     *
     * @param chatId ID чата
     * @param text   текст кнопок с информацией о питомце
     */
    private void setAllPetsMenu(Long chatId, String text) {
        List<List<String>> lists = new ArrayList<>();
        for (PetEntity petEntity : petRepository.findAll()) {
            lists.add(List.of(petEntity.getBreed() + " " + " возраст: " + petEntity.getAge() + " года ", petEntity.getId() + "_PETS_BY_ID_" + "BUTTON"));
        }
        setKeyboard(chatId, text, lists, 2);
    }

    /**
     * Метод в котором информация о питомце ищется путем резделения строки и поиску по информации ID {@code Long petId = Long.parseLong(callBackData.split("_")[0])}
     *
     * @param chatId       ID чата
     * @param callBackData аргумент (нажатая кнопка)
     */
    private void getInfoByPetId(Long chatId, String callBackData) {
        Long petId = Long.parseLong(callBackData.split("_")[0]);
//        System.out.println(petId.getClass().getName());
        Optional<PetEntity> pet = petRepository.findById(petId);
        if (pet.isPresent()) {
            if (callBackData.contains(ButtonsNames.PET_NAME_BUTTON_DATA)) {
                sendMessage(chatId, pet.get().getName());
            } else if (callBackData.contains(ButtonsNames.PET_AGE_BUTTON_DATA)) {
                sendMessage(chatId, String.valueOf(pet.get().getAge()));
            } else if (callBackData.contains(ButtonsNames.PET_BREED_BUTTON_DATA)) {
                sendMessage(chatId, pet.get().getBreed());
            } else if (callBackData.contains(ButtonsNames.PET_COMMENT_BUTTON_DATA)) {
                sendMessage(chatId, pet.get().getComment());
            }
        }
    }

    /**
     * Метод создания кнопок
     *
     * @param chatId ID чата
     * @param text   текст информации о питомце
     * @param petId  ID питомца
     */
    private void setPetInfoMenu(Long chatId, String text, Long petId) {
        List<List<String>> lists = new ArrayList<>();
        lists.add(List.of(ButtonsNames.PET_NAME_BUTTON_NAME, petId + "_" + ButtonsNames.PET_NAME_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.PET_AGE_BUTTON_NAME, petId + "_" + ButtonsNames.PET_AGE_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.PET_BREED_BUTTON_NAME, petId + "_" + ButtonsNames.PET_BREED_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.PET_COMMENT_BUTTON_NAME, petId + "_" + ButtonsNames.PET_COMMENT_BUTTON_DATA));
        setKeyboard(chatId, text, lists, 2);
    }

//    /**
//     * Метод в котором информация о приюте ищется путем резделения строки и поиску по информации ID {@code Long petId = Long.parseLong(callBackData.split("_")[0])}
//     *
//     * @param chatId       ID чата
//     * @param callBackData аргумент (нажатая кнопка)
//     */
//    private void getInfoByShelterId(Long chatId, String callBackData) {
//        Long shelterId = Long.parseLong(callBackData.split("_")[0]);
//        Optional<ShelterEntity> shelter = shelterRepository.findById(shelterId);
//        if (shelter.isPresent()) {
//            if (callBackData.contains(ButtonsNames.SCHEDULE_BUTTON_DATA)) {
//                sendMessage(chatId, shelter.get().getSchedule());
//            } else if (callBackData.contains(ButtonsNames.DRIVING_DIRECTIONS_BUTTON_DATA)) {
//                sendMessage(chatId, shelter.get().getDrivingDirections());
//            } else if (callBackData.contains(ButtonsNames.GUARD_DETAILS_BUTTON_DATA)) {
//                sendMessage(chatId, shelter.get().getGuardDetails());
//            } else if (callBackData.contains(ButtonsNames.SAFETY_PRECAUTIONS_BUTTON_DATA)) {
//                sendMessage(chatId, shelter.get().getSafetyPrecautions());
//            }
//        }
//    }

    /**
     * Метод создания клавиатуры бота
     *
     * @param chatId       ID чата
     * @param text         текс с информацией кнопки
     * @param buttonsInfo  список кнопок
     * @param amountOfRows количество строк кнопок
     */


//    /**
//     * Метод вывода всех животных приюта
//     * <br/>
//     * По циклу <b>информация из репозитория БД</b> {@link ShelterRepository} преобразуется в сущность {@link ShelterEntity}, где затем кладется в коллекцию {@link List}
//     * <br/>
//     * В цикле путем добавления из БД информации, создается меню <b>кнопок</b> {@code setKeyboard(chatId, text, lists, 1);}
//     *
//     * @param chatId
//     * @param text
//     */
//    private void setSheltersMenuBot(Long chatId, String text) {
//        List<List<String>> lists = new ArrayList<>();
//        for (ShelterEntity shelterEntity : shelterRepository.findAll()) {
//            lists.add(List.of(shelterEntity.getName(), shelterEntity.getId() + "_SHELTERS_" + "BUTTON"));
//        }
//        setKeyboard(chatId, text, lists, 1);
//    }

    /**
     * Метод создания кнопок
     *
     * @param chatId    ID чата
     * @param text      текст информации о приюте
     * @param shelterId ID приюта
     */
    private void setShelterInfoMenu(Long chatId, String text, Long shelterId) {
        List<List<String>> lists = new ArrayList<>();
        lists.add(List.of(ButtonsNames.SCHEDULE_BUTTON_NAME, shelterId + "_" + ButtonsNames.SCHEDULE_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.DRIVING_DIRECTIONS_BUTTON_NAME, shelterId + "_" + ButtonsNames.DRIVING_DIRECTIONS_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.GUARD_DETAILS_BUTTON_NAME, shelterId + "_" + ButtonsNames.GUARD_DETAILS_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.SAFETY_PRECAUTIONS_BUTTON_NAME, shelterId + "_" + ButtonsNames.SAFETY_PRECAUTIONS_BUTTON_DATA));
        setKeyboard(chatId, text, lists, 2);
    }
    private void executeMessage(Long chatId, String text, SendMessage sendMessage) {
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Метод создания стартового меню с кнопками
     *
     * @param chatId ID чата
     * @param text   текст информации кнопок
     */
    private void setStarMenuBot(Long chatId, String text) {
        List<List<String>> lists = new ArrayList<>();
        lists.add(List.of(ButtonsNames.INFO_ABOUT_SHELTER_BUTTON_NAME, ButtonsNames.INFO_ABOUT_SHELTER_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.GET_PET_FROM_SHELTER_BUTTON_NAME, ButtonsNames.GET_PET_FROM_SHELTER_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.SEND_REPORT_PETS_BUTTON_NAME, ButtonsNames.SEND_REPORT_PETS_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.CALL_VOLUNTEER_BUTTON_NAME, ButtonsNames.CALL_VOLUNTEER_BUTTON_DATA));
        setKeyboard(chatId, text, lists, 2);
    }



    @Override
    public String getBotUsername() {
        return configuration.getName();
    }

}