package com.shelter.bot.service;

import com.shelter.bot.entity.ShelterEntity;
import com.shelter.bot.repository.ShelterRepository;
import com.shelter.bot.utils.ButtonsNames;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ShelterService {
    private final UtilsService utilsService;
    private final ShelterRepository shelterRepository;

    public ShelterService(UtilsService utilsService, ShelterRepository shelterRepository) {
        this.utilsService = utilsService;
        this.shelterRepository = shelterRepository;
    }

    /**
     * Метод в котором информация о приюте ищется путем резделения строки и поиску по информации ID {@code Long petId = Long.parseLong(callBackData.split("_")[0])}
     *
     * @param chatId       ID чата
     * @param callBackData аргумент (нажатая кнопка)
     */
    public void getInfoByShelterId(Long chatId, String callBackData) {
        Long shelterId = Long.parseLong(callBackData.split("_")[0]);
        Optional<ShelterEntity> shelter = shelterRepository.findById(shelterId);
        if (shelter.isPresent()) {
            if (callBackData.contains(ButtonsNames.SCHEDULE_BUTTON_DATA)) {
                utilsService.sendMessage(chatId, shelter.get().getSchedule());
            } else if (callBackData.contains(ButtonsNames.DRIVING_DIRECTIONS_BUTTON_DATA)) {
                utilsService.sendMessage(chatId, shelter.get().getDrivingDirections());
            } else if (callBackData.contains(ButtonsNames.GUARD_DETAILS_BUTTON_DATA)) {
                utilsService.sendMessage(chatId, shelter.get().getGuardDetails());
            } else if (callBackData.contains(ButtonsNames.SAFETY_PRECAUTIONS_BUTTON_DATA)) {
                utilsService.sendMessage(chatId, shelter.get().getSafetyPrecautions());
            }
        }
    }
    public void setShelterInfoMenu(Long chatId, String text, Long shelterId) {
        List<List<String>> lists = new ArrayList<>();
        lists.add(List.of(ButtonsNames.SCHEDULE_BUTTON_NAME, shelterId + "_" + ButtonsNames.SCHEDULE_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.DRIVING_DIRECTIONS_BUTTON_NAME, shelterId + "_" + ButtonsNames.DRIVING_DIRECTIONS_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.GUARD_DETAILS_BUTTON_NAME, shelterId + "_" + ButtonsNames.GUARD_DETAILS_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.SAFETY_PRECAUTIONS_BUTTON_NAME, shelterId + "_" + ButtonsNames.SAFETY_PRECAUTIONS_BUTTON_DATA));
        utilsService.setKeyboard(chatId, text, lists, 2);
    }

    /**
     * Метод вывода всех животных приюта
     * <br/>
     * По циклу <b>информация из репозитория БД</b> {@link ShelterRepository} преобразуется в сущность {@link ShelterEntity}, где затем кладется в коллекцию {@link List}
     * <br/>
     * В цикле путем добавления из БД информации, создается меню <b>кнопок</b> {@code setKeyboard(chatId, text, lists, 1);}
     *
     * @param chatId
     * @param text
     */
    public void setSheltersMenuBot(Long chatId, String text) {
        List<List<String>> lists = new ArrayList<>();
        for (ShelterEntity shelterEntity : shelterRepository.findAll()) {
            lists.add(List.of(shelterEntity.getName(), shelterEntity.getId() + "_SHELTERS_" + "BUTTON"));
        }
        utilsService.setKeyboard(chatId, text, lists, 1);
    }
}
