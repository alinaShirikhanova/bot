package com.shelter.bot.controller;


import com.shelter.bot.entity.ShelterEntity;
import com.shelter.bot.repository.ShelterRepository;
import com.shelter.bot.service.ShelterService;
import com.shelter.bot.utils.ButtonsNames;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.shelter.bot.utils.ButtonsNames.*;

@Component
public class ShelterController {
    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    public void executeCommand(String callBackData, Long chatId) {
        if (callBackData.equals(INFO_ABOUT_SHELTER_BUTTON_DATA)) {
            shelterService.setSheltersMenuBot(chatId, "Приюты: ");
        } else if (callBackData.contains("SHELTERS")) {
            Long shelterId = Long.parseLong(callBackData.split("_")[0]);
            shelterService.setShelterInfoMenu(chatId, "Что Вы хотите узнать?", shelterId);
        }
        else if (callBackData.contains("INFO")) {
            shelterService.getInfoByShelterId(chatId, callBackData);
        }
    }


}
